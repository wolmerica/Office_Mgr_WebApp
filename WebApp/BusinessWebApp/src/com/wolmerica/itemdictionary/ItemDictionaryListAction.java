/*
 * ItemDictionaryListAction.java
 *
 * Created on August 29, 2005, 10:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/06/2006 Add ItemDictionaryHeadListDO & ItemDictionaryListDO object and pagination.
 */

package com.wolmerica.itemdictionary;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.purchaseorder.PurchaseOrderService;
import com.wolmerica.service.purchaseorder.DefaultPurchaseOrderService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.DateFormatter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ItemDictionaryListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private PurchaseOrderService purchaseOrderService = new DefaultPurchaseOrderService();
  private UserStateService userStateService = new DefaultUserStateService();

  public DateRangeService getDateRangeService() {
      return dateRangeService;
  }

  public void setDateRangeService(DateRangeService dateRangeService) {
      this.dateRangeService = dateRangeService;
  }

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
  }

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public PurchaseOrderService getPurchaseOrderService() {
      return purchaseOrderService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
      this.purchaseOrderService = purchaseOrderService;
  }


  private ItemDictionaryListHeadDO getItemDictionaryList(HttpServletRequest request,
                                                         String itemNumFilter,
                                                         String brandNameFilter,
                                                         String genericNameFilter,
                                                         Integer pageNo,
                                                         Integer poKey,
                                                         Integer sKey,
                                                         Byte ctKey,
                                                         Byte sourceTypeKey)
    throws Exception, SQLException {

    ItemDictionaryListHeadDO formHDO = new ItemDictionaryListHeadDO();
    ItemDictionaryListDO itemDictionaryRow = null;
    ArrayList<ItemDictionaryListDO> itemDictionaryRows = new ArrayList<ItemDictionaryListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

//--------------------------------------------------------------------------------
// Get the Year To Date from and to date values to get the hours sold value.
//--------------------------------------------------------------------------------
    DateFormatter dateFormatter = new DateFormatter();
    String theDate = getDateRangeService().getDateToString(getDateRangeService().getBACKFromDate(new Integer(getPropertyService().getCustomerProperties(request,"itemdictionary.days.back")).intValue()));
    cat.debug(this.getClass().getName() + ": fromDate : " + theDate);
    Date myDate = (Date) dateFormatter.unformat(theDate);
    Date fromDate = myDate;

    theDate = getDateRangeService().getDateToString(getDateRangeService().getYTDToDate());
    cat.debug(this.getClass().getName() + ": toDate : " + theDate);
    myDate = (Date) dateFormatter.unformat(theDate);
    Date toDate = myDate;

//--------------------------------------------------------------------------------
// Build the SQL query for the service dictionary list.
//--------------------------------------------------------------------------------
    String query = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      Integer vendorKey = null;
      if (poKey != null) {
        HashMap pokMap = getPurchaseOrderService().getPurchaseOrderKeys(conn, poKey);
        vendorKey = new Integer(pokMap.get("vendorKey").toString());
      }

      if (ctKey == null) {
        query = "SELECT thekey AS id_key, brand_name,"
              + "item_num, profile_num, generic_name,"
              + "manufacturer, size, size_unit,"
              + "first_cost, order_threshold "
              + "FROM itemdictionary "
              + "WHERE UPPER(brand_name) LIKE ? "
              + "AND UPPER(item_num) LIKE ? "
              + "AND UPPER(generic_name) LIKE ? ";
        if (vendorKey != null) {
          query += " AND (itemdictionary.vendor_key = " + vendorKey
                + " OR NOT(itemdictionary.vendor_specific_id))";
        }
        query += " ORDER BY brand_name, size ";
      }
      else {
        query = "SELECT itemdictionary.thekey AS id_key,"
              + "pricebyitem.thekey AS pbi_key,"
              + "brand_name, item_num, profile_num,"
              + "generic_name, manufacturer,"
              + "priceattributebyitem.size, size_unit,"
              + "order_threshold,"
              + getPropertyService().getCustomerProperties(request,"customerinvoiceitem.default.price") + " AS first_cost "
              + "FROM itemdictionary, priceattributebyitem, pricetype, pricebyitem "
              + "WHERE UPPER(brand_name) LIKE ? "
              + "AND UPPER(item_num) LIKE ? "
              + "AND UPPER(generic_name) LIKE ? "
              + "AND itemdictionary.thekey = priceattributebyitem.itemdictionary_key "
              + "AND priceattributebyitem.pricetype_key = pricetype.thekey "
              + "AND pricetype.unit_cost_base_id "
              + "AND pricetype.active_id "
              + "AND priceattributebyitem.thekey = priceattributebyitem_key "
              + "AND pricebyitem.customertype_key = ? "
              + "ORDER by brand_name, size";
      }
      cat.debug(" Query #1 " + query);
      cat.debug(" brandNameFilter...: [" + brandNameFilter + "]");
      cat.debug(" itemNumFilter.....: [" + itemNumFilter + "]");
      cat.debug(" genericNameFilter.: [" + genericNameFilter + "]");
      cat.debug(" vendorKey......  .: [" + vendorKey + "]");
      ps = conn.prepareStatement(query);

      ps.setString(1, "%" + brandNameFilter.toUpperCase().trim() + "%");
      ps.setString(2, "%" + itemNumFilter.toUpperCase().trim() + "%");
      ps.setString(3, "%" + genericNameFilter.toUpperCase().trim() + "%");
      if (ctKey != null)
        ps.setByte(4, ctKey);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"itemdictionary.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          itemDictionaryRow = new ItemDictionaryListDO();

          if (ctKey == null)
            itemDictionaryRow.setKey(rs.getInt("id_key"));
          else
            itemDictionaryRow.setKey(rs.getInt("pbi_key"));
          itemDictionaryRow.setBrandName(rs.getString("brand_name"));
          itemDictionaryRow.setSize(new BigDecimal(rs.getString("size")));
          itemDictionaryRow.setSizeUnit(rs.getString("size_unit"));
          itemDictionaryRow.setManufacturer(rs.getString("manufacturer"));
          itemDictionaryRow.setItemNum(rs.getString("item_num"));
          itemDictionaryRow.setProfileNum(rs.getString("profile_num"));
          itemDictionaryRow.setGenericName(rs.getString("generic_name"));
          itemDictionaryRow.setFirstCost(rs.getBigDecimal("first_cost"));
//------------------------------------------------------------------------------
// Perform an item availability check in stockitem.
//------------------------------------------------------------------------------
          itemDictionaryRow = getItemAndSrvService().getItemAvailability(conn,
                                                        rs.getInt("id_key"),
                                                        itemDictionaryRow);
//------------------------------------------------------------------------------
// Compare the quantity on hand against the order threshold and set accordingly.
//------------------------------------------------------------------------------
          if (itemDictionaryRow.getQtyOnHand() <= rs.getInt("order_threshold")){
            itemDictionaryRow.setBelowThresholdId(true);
          }
//------------------------------------------------------------------------------
// When dealing with an event we need to make sure that one and only one occurrence
// of the item exists in the work order.  This is due to an existing business rule
// in the customer invoice and since the work order can initiate a customer invoice
// the same rule must be enforced here.
//------------------------------------------------------------------------------
          if (sKey != null) {
            itemDictionaryRow.setSellableItemId(getItemAndSrvService().getItemBookedForEvent(conn,
                                                sKey,
                                                rs.getInt("id_key")));
          }

//--------------------------------------------------------------------------------
// Only retrieve the service hours sold value for the regular list display.
//--------------------------------------------------------------------------------
          if (ctKey == null) {
            itemDictionaryRow.setItemUnitsSold(getItemUnitsSold(conn,
                                                                rs.getInt("id_key"),
                                                                fromDate,
                                                                toDate));
          }

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRow = getUserStateService().getUserListToken(request,conn,
                             this.getClass().getName(),itemDictionaryRow.getKey());
          permissionRows.add(permissionRow);

          if (permissionRow.getDeleteId() && (sourceTypeKey != null)) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the item dictionary has any dependency on linked to tables.
//--------------------------------------------------------------------------------
            itemDictionaryRow.setAllowDeleteId(getItemDependency(conn,
                                                                 sourceTypeKey,
                                                                 itemDictionaryRow.getKey()));
          }

          itemDictionaryRows.add(itemDictionaryRow);
        }
      }

      //===========================================================================
      // Pagination logic to figure out what the previous and next page
      // values will be for the next screen to be displayed.
      //===========================================================================
      Integer prevPage=0;
      Integer nextPage=0;
      if (recordCount > lastRecord)
        nextPage = pageNo + 1;
      else
        lastRecord = recordCount;
      if (firstRecord > 1)
        prevPage = pageNo - 1;
      if (recordCount == 0)
        firstRecord=0;
      //===========================================================================
      // Store the filter, row count, previous and next page number values.
      //===========================================================================
      formHDO.setBrandNameFilter(brandNameFilter);
      formHDO.setItemNumFilter(itemNumFilter);
      formHDO.setGenericNameFilter(genericNameFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setCurrentPage(prevPage + 1);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (itemDictionaryRows.isEmpty()) {
        itemDictionaryRows.add(new ItemDictionaryListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setItemDictionaryListForm(itemDictionaryRows);
      formHDO.setPermissionListForm(permissionRows);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }
    return formHDO;
  }


  public Boolean getItemDependency(Connection conn,
                                   Byte stKey,
                                   Integer sKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean allowDeleteId = false;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemDependency(?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, stKey);
      cStmt.setInt(2, sKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug("ItemDictionaryListAction dependencyCnt : " + cStmt.getInt("dependencyCnt"));
      cat.debug("ItemDictionaryListAction tableName : " + cStmt.getString("tableName"));
//--------------------------------------------------------------------------------
// Retrieve the dependency count return value
//--------------------------------------------------------------------------------
      if (cStmt.getInt("dependencyCnt") == 0)
        allowDeleteId = true;
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemDependency() " + e.getMessage());
      }
    }
    return allowDeleteId;
  }


  public BigDecimal getItemUnitsSold(Connection conn,
                                     Integer itemKey,
                                     Date fromDate,
                                     Date toDate)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    BigDecimal unitsSoldQty = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with three IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemUnitsSold(?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, itemKey);
      cStmt.setDate(2, new java.sql.Date(fromDate.getTime()));
      cStmt.setDate(3, new java.sql.Date(toDate.getTime()));
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      unitsSoldQty = cStmt.getBigDecimal("unitsSoldQty");
      cat.debug(this.getClass().getName() + ": itemKey......: " + itemKey);
      cat.debug(this.getClass().getName() + ": unitsSoldQty.: " + unitsSoldQty.toString());
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemUnitsSold() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemUnitsSold() " + e.getMessage());
      }
    }
    return unitsSoldQty;
  }


    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

    // Default target to success
    String target = "success";

    EmployeesActionMapping employeesMapping =
      (EmployeesActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {
//--------------------------------------------------------------------------------
// The user is not logged in.
//--------------------------------------------------------------------------------
        target = "login";
        ActionMessages actionMessages = new ActionMessages();

        actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
          new ActionMessage("errors.login.required"));
//--------------------------------------------------------------------------------
// Report any ActionMessages we have discovered back to the original form.
//--------------------------------------------------------------------------------
        if (!actionMessages.isEmpty()) {
          saveMessages(request, actionMessages);
        }
//--------------------------------------------------------------------------------
// Forward to the request to the login screen.
//--------------------------------------------------------------------------------
        return (mapping.findForward(target));
      }
    }

    try {
//--------------------------------------------------------------------------------
// Traverse the request parameters and populate the appropriate values.
//--------------------------------------------------------------------------------
      String itemNumFilter = "";
      String brandNameFilter = "";
      String genericNameFilter = "";
      Integer idKey = null;
      Integer poKey = null;
      Integer sKey = null;
      Integer theKey = null;
      Byte ctKey = null;
      Integer idPageNo = new Integer("1");
      cat.debug(this.getClass().getName() + ": ItemDictionaryList params ");
      Enumeration paramNames = request.getParameterNames();
      String paramName = "";
      while (paramNames.hasMoreElements()) {
        paramName = (String)paramNames.nextElement();
        cat.debug(this.getClass().getName() + ": paramName = " + paramName);

        if (paramName.equals("itemNumFilter")) {
          itemNumFilter = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[itemNumFilter] = " + itemNumFilter);
        }
        if (paramName.equals("brandNameFilter")) {
          brandNameFilter = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[brandNameFilter] = " + brandNameFilter);
        }
        if (paramName.equals("genericNameFilter")) {
          genericNameFilter = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[genericNameFilter] = " + genericNameFilter);
        }
        if (paramName.equals("idKey")) {
          idKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[idKey] = " + idKey.toString());
          request.setAttribute("idKey", idKey.toString());
        }
        if (paramName.equals("poKey")) {
          poKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[poKey] = " + poKey.toString());
        }
        if (paramName.equals("sKey")) {
          sKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[sKey] = " + sKey.toString());
        }
        if (paramName.equals("key")) {
          theKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
          request.setAttribute("key", theKey.toString());
        }
        if (paramName.equals("ctKey")) {
          ctKey = new Byte(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[ctKey] = " + ctKey.toString());
        }
        if (paramName.equals("idPageNo")) {
          idPageNo = new Integer(request.getParameter(paramName).toString());
          if (idPageNo < 0)
            idPageNo = new Integer("1");
          cat.debug(this.getClass().getName() + ": get[idPageNo] = " + idPageNo);
        }
      }

//--------------------------------------------------------------------------------
// If no parameter values for idKey and key then check the attribute.
//--------------------------------------------------------------------------------
      if (idKey == null) {
        if (request.getAttribute("idKey") != null) {
          idKey = new Integer(request.getAttribute("idKey").toString());
          request.setAttribute("idKey", theKey.toString());
        }
      }
      if (theKey == null) {
        if (request.getAttribute("key") != null) {
          theKey = new Integer(request.getAttribute("key").toString());
          request.setAttribute("key", theKey.toString());
        }
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
// A non-null idKey will leave the existing P.O. or Customer Invoice lock in place.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (idKey == null){
        
        String usToken = getUserStateService().getUserToken(request,
                                                this.getDataSource(request).getConnection(),
                                                this.getClass().getName(),
                                                getUserStateService().getNoKey());
        if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
          request.setAttribute(getUserStateService().getDisableEdit(), false);
        else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
          throw new Exception(getUserStateService().getAccessDenied());
        sourceTypeKey = getUserStateService().getFeatureKey().byteValue();
      }

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        ItemDictionaryListHeadDO formHDO = getItemDictionaryList(request,
                                                                 itemNumFilter,
                                                                 brandNameFilter,
                                                                 genericNameFilter,
                                                                 idPageNo,
                                                                 poKey,
                                                                 sKey,
                                                                 ctKey,
                                                                 sourceTypeKey);
        request.getSession().setAttribute("itemDictionaryListHDO", formHDO);

//--------------------------------------------------------------------------------
// Create the wrapper object for itemdictionarylist.
//--------------------------------------------------------------------------------
        ItemDictionaryListHeadForm formHStr = new ItemDictionaryListHeadForm();
        formHStr.populate(formHDO);
        form = formHStr;
        }
      catch (FormattingException fe) {
            fe.getMessage();
      }

      if ( form == null ) {
        cat.debug(this.getClass().getName() + ":---->form is null<----");
      }
      if ("request".equals(mapping.getScope())) {
        cat.debug(this.getClass().getName() + ":---->request.setAttribute<----");
        request.setAttribute(mapping.getAttribute(), form);
      }
      else {
        cat.debug(this.getClass().getName() + ":---->session.setAttribute<----");
        request.getSession().setAttribute(mapping.getAttribute(), form);
      }
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
      target = "error";
      ActionMessages actionMessages = new ActionMessages();
      actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
        new ActionMessage("errors.database.error", e.getMessage()));
//--------------------------------------------------------------------------------
// Report any ActionMessages
//--------------------------------------------------------------------------------
      if (!actionMessages.isEmpty()) {
        saveMessages(request, actionMessages);
      }
    }
//--------------------------------------------------------------------------------
// Forward to the appropriate View
//--------------------------------------------------------------------------------
    return (mapping.findForward(target));
  }
}
