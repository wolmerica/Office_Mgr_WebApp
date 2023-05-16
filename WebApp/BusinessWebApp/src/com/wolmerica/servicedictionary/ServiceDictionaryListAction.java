/*
 * ServiceDictionaryListAction.java
 *
 * Created on June 20, 2006, 07:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.servicedictionary;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.purchaseorder.PurchaseOrderService;
import com.wolmerica.service.purchaseorder.DefaultPurchaseOrderService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.permission.PermissionListDO;

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

public class ServiceDictionaryListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
  private PurchaseOrderService purchaseOrderService = new DefaultPurchaseOrderService();
  private UserStateService userStateService = new DefaultUserStateService();

  public DateRangeService getDateRangeService() {
      return dateRangeService;
  }

  public void setDateRangeService(DateRangeService dateRangeService) {
      this.dateRangeService = dateRangeService;
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

  public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
      this.purchaseOrderService = purchaseOrderService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private ServiceDictionaryListHeadDO getServiceDictionaryList(HttpServletRequest request,
                                                               String serviceNumFilter,
                                                               String serviceNameFilter,
                                                               String categoryNameFilter,
                                                               Integer pageNo,
                                                               Integer poKey,
                                                               Integer sKey,
                                                               Byte ctKey,
                                                               Byte sourceTypeKey)
    throws Exception, SQLException {

    ServiceDictionaryListHeadDO formHDO = new ServiceDictionaryListHeadDO();
    ServiceDictionaryListDO serviceRow = null;
    ArrayList<ServiceDictionaryListDO> serviceRows = new ArrayList<ServiceDictionaryListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

//--------------------------------------------------------------------------------
// Get the Year To Date from and to date values to get the hours sold value.
//--------------------------------------------------------------------------------
    DateFormatter dateFormatter = new DateFormatter();
    String theDate = getDateRangeService().getDateToString(getDateRangeService().getBACKFromDate(new Integer(getPropertyService().getCustomerProperties(request,"service.days.back")).intValue()));
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
        query = "SELECT thekey AS sd_key,"
              + "name AS sd_name,"
              + "service_num AS sd_number,"
              + "profile_num,"
              + "category AS sd_category,"
              + "release_id,"
              + "duration_hours,"
              + "duration_minutes,"
              + "service_cost "
              + "FROM servicedictionary "
              + "WHERE name LIKE ? "
              + "AND service_num LIKE ? "
              + "AND category LIKE ? "
              + "ORDER BY sd_name";
      }
      else {
        query = "SELECT pricebyservice.thekey AS pbs_key,"
              + "servicedictionary.name AS sd_name,"
              + "servicedictionary.service_num AS sd_number,"
              + "servicedictionary.profile_num,"
              + "servicedictionary.category AS sd_category,"
              + "servicedictionary.release_id,"
              + "servicedictionary.duration_hours,"
              + "servicedictionary.duration_minutes,"
              + "pricetype.thekey AS pt_key,"
              + "pricetype.name AS pt_name,"
              + getPropertyService().getCustomerProperties(request,"customerinvoiceservice.default.price") + " AS service_cost "
              + "FROM servicedictionary, pricebyservice, pricetype,"
              + "customerattributebyservice "
              + "WHERE UPPER(servicedictionary.name) LIKE ? "
              + "AND UPPER(servicedictionary.service_num) LIKE ? "
              + "AND UPPER(servicedictionary.category) LIKE ? "
              + "AND servicedictionary.thekey = customerattributebyservice.servicedictionary_key "
              + "AND customerattributebyservice.thekey = pricebyservice.customerattributebyservice_key "
              + "AND customerattributebyservice.customertype_key = ? "
              + "AND pricebyservice.pricetype_key = pricetype.thekey "
              + "AND pricetype.active_id ";
        if ((poKey != null) || (sKey != null)) {
          query += " AND servicedictionary.pricetype_key = pricetype.thekey ";
        }
        if (vendorKey != null) {
          query += " AND (servicedictionary.vendor_key = " + vendorKey
                + " OR NOT(servicedictionary.vendor_specific_id))";
        }
        query += " ORDER BY sd_name";
      }
      cat.debug(" Query #1 " + query);
      cat.debug(" brandNameFilter...: [" + serviceNameFilter + "]");
      cat.debug(" itemNumFilter.....: [" + serviceNumFilter + "]");
      cat.debug(" genericNameFilter.: [" + categoryNameFilter + "]");
      cat.debug(" vendorKey......  .: [" + vendorKey + "]");
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + serviceNameFilter.toUpperCase().trim() + "%");
      ps.setString(2, "%" + serviceNumFilter.toUpperCase().trim() + "%");
      ps.setString(3, "%" + categoryNameFilter.toUpperCase().trim() + "%");
      if (ctKey != null)
        ps.setByte(4, ctKey);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"service.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          serviceRow = new ServiceDictionaryListDO();

          if (ctKey == null) {
            serviceRow.setKey(rs.getInt("sd_key"));
          }
          else {
            serviceRow.setKey(rs.getInt("pbs_key"));
            serviceRow.setPriceTypeName(rs.getString("pt_name"));
          }
          serviceRow.setServiceName(rs.getString("sd_name"));
          serviceRow.setServiceNum(rs.getString("sd_number"));
          serviceRow.setProfileNum(rs.getString("profile_num"));
          serviceRow.setServiceCategory(rs.getString("sd_category"));
          serviceRow.setReleaseId(rs.getBoolean("release_id"));
          serviceRow.setDurationHours(rs.getByte("duration_hours"));
          serviceRow.setDurationMinutes(rs.getByte("duration_minutes"));
          serviceRow.setServiceCost(rs.getBigDecimal("service_cost"));
//--------------------------------------------------------------------------------
// Only retrieve the service hours sold value for the regular list display.
//--------------------------------------------------------------------------------
          if (ctKey == null) {
            serviceRow.setServiceHoursSold(getServiceHoursSold(conn,
                                                               serviceRow.getKey(),
                                                               fromDate,
                                                               toDate));
          }

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRow = getUserStateService().getUserListToken(request,conn,
                             this.getClass().getName(),serviceRow.getKey());
          permissionRows.add(permissionRow);

          if (permissionRow.getDeleteId() && (ctKey == null)) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the service dictionary has any dependency on linked to tables.
//--------------------------------------------------------------------------------
            serviceRow.setAllowDeleteId(getServiceDependency(conn,
                                                             sourceTypeKey,
                                                             serviceRow.getKey()));
          }
          serviceRows.add(serviceRow);
        }
      }

//--------------------------------------------------------------------------------
// Pagination logic to figure out what the previous and next page
// values will be for the next screen to be displayed.
//--------------------------------------------------------------------------------
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
      formHDO.setServiceNameFilter(serviceNameFilter);
      formHDO.setServiceNumFilter(serviceNumFilter);
      formHDO.setCategoryNameFilter(categoryNameFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (serviceRows.isEmpty()) {
        serviceRows.add(new ServiceDictionaryListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setServiceDictionaryListForm(serviceRows);
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

  public Boolean getServiceDependency(Connection conn,
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
      cStmt = conn.prepareCall("{call GetServiceDependency(?,?,?,?)}");
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
      cat.debug(this.getClass().getName() + ": dependencyCnt : " + cStmt.getInt("dependencyCnt"));
      cat.debug(this.getClass().getName() + ": tableName : " + cStmt.getString("tableName"));
//--------------------------------------------------------------------------------
// Retrieve the dependency count return value
//--------------------------------------------------------------------------------
      if (cStmt.getInt("dependencyCnt") == 0)
        allowDeleteId = true;
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getServiceDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getServiceDependency() " + e.getMessage());
      }
    }
    return allowDeleteId;
  }


  public BigDecimal getServiceHoursSold(Connection conn,
                                        Integer serviceKey,
                                        Date fromDate,
                                        Date toDate)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    BigDecimal hoursSoldQty = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with three IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetServiceHoursSold(?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, serviceKey);
      cStmt.setDate(2, new java.sql.Date(fromDate.getTime()));
      cStmt.setDate(3, new java.sql.Date(toDate.getTime()));
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      hoursSoldQty = cStmt.getBigDecimal("hoursSoldQty");
      cat.debug(this.getClass().getName() + ": hoursSoldQty : " + hoursSoldQty.toString());
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getServiceHoursSold() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getServiceHoursSold() " + e.getMessage());
      }
    }
    return hoursSoldQty;
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
      String serviceNumFilter = "";
      String serviceNameFilter = "";
      String categoryNameFilter = "";
      Integer poKey = null;
      Integer sKey = null;
      Integer theKey = null;
      Byte ctKey = null;
      Integer sdPageNo = new Integer("1");
      cat.debug(this.getClass().getName() + ": ServiceDictionaryList params ");
      Enumeration paramNames = request.getParameterNames();
      String paramName = "";
      while (paramNames.hasMoreElements()) {
        paramName = (String)paramNames.nextElement();
        cat.debug(this.getClass().getName() + ": paramName = " + paramName);

        if (paramName.equals(new String("serviceNumFilter"))) {
          serviceNumFilter = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[serviceNumFilter] = " + serviceNumFilter);
        }
        if (paramName.equals(new String("serviceNameFilter"))) {
          serviceNameFilter = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[serviceNameFilter] = " + serviceNameFilter);
        }
        if (paramName.equals(new String("categoryNameFilter"))) {
          categoryNameFilter = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[categoryNameFilter] = " + categoryNameFilter);
        }
        if (paramName.equals(new String("poKey"))) {
          poKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[poKey] = " + poKey.toString());
        }
        if (paramName.equals(new String("sKey"))) {
          sKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[sKey] = " + sKey.toString());
        }
        if (paramName.equals(new String("key"))) {
          theKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
          request.setAttribute("key", theKey.toString());
        } else if (request.getAttribute("key") != null) {
            theKey = new Integer(request.getAttribute("key").toString());
            request.setAttribute("key", theKey.toString());
        }
        if (paramName.equals(new String("ctKey"))) {
          ctKey = new Byte(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[ctKey] = " + ctKey.toString());
        }
        if (paramName.equals(new String("sdPageNo"))) {
          sdPageNo = new Integer(request.getParameter(paramName).toString());
          if (sdPageNo < 0)
            sdPageNo = new Integer("1");
          cat.debug(this.getClass().getName() + ": get[sdPageNo] = " + sdPageNo);
        }
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
// A non-null idKey will leave the existing P.O. or Customer Invoice lock in place.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if ((poKey == null) || (ctKey == null)) {
        
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
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        ServiceDictionaryListHeadDO formHDO = getServiceDictionaryList(request,                                                                       
                                                                       serviceNumFilter,
                                                                       serviceNameFilter,
                                                                       categoryNameFilter,
                                                                       sdPageNo,
                                                                       poKey,
                                                                       sKey,
                                                                       ctKey,
                                                                       sourceTypeKey);
        request.getSession().setAttribute("servicedictionarylistHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for Service list.
//--------------------------------------------------------------------------------
        ServiceDictionaryListHeadForm formHStr = new ServiceDictionaryListHeadForm();
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
