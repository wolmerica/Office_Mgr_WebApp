/*
 * PurchaseOrderListAction.java
 *
 * Created on September 5, 2005, 6:28 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.purchaseorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.rebate.RebateService;
import com.wolmerica.service.rebate.DefaultRebateService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;

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
import java.util.HashMap;

import org.apache.log4j.Logger;

public class PurchaseOrderListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private PropertyService propertyService = new DefaultPropertyService();
  private RebateService rebateService = new DefaultRebateService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public RebateService getRebateService() {
      return rebateService;
  }

  public void setRebateService(RebateService rebateService) {
      this.rebateService = rebateService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private PurchaseOrderListHeadDO getPurchaseOrder(HttpServletRequest request,
                                                   String vendorNameFilter,
                                                   String orderNumberFilter,
                                                   Integer itemKeyFilter,
                                                   Integer scheduleKeyFilter,
                                                   Integer pageNo)
    throws Exception, SQLException {

    PurchaseOrderListHeadDO formHDO = new PurchaseOrderListHeadDO();
    PurchaseOrderListDO purchaseOrderRow = null;
    ArrayList<PurchaseOrderListDO> purchaseOrderRows = new ArrayList<PurchaseOrderListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT purchaseorder.thekey, "
                   + "purchase_order_num, "
                   + "vendor_key,"
                   + "vendor.name,"
                   + "vendor.order_form_key,"
                   + "customer_key,"
                   + "customer.client_name,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "schedule_key,"
                   + "purchaseorder.create_stamp, "
                   + "order_status "
                   + "FROM purchaseorder, customer, vendor "
                   + "WHERE UPPER(vendor.name) LIKE ? "
                   + "AND UPPER(purchase_order_num) LIKE ? "
                   + "AND purchaseorder.vendor_key = vendor.thekey "
                   + "AND purchaseorder.customer_key = customer.thekey";
//--------------------------------------------------------------------------------
// 07-30-2007  Limit the purchase order to a particular item.
//--------------------------------------------------------------------------------
      if (itemKeyFilter > 0) {
        query = query + " AND purchaseorder.thekey IN (SELECT purchaseorder_key "
                                                   + " FROM purchaseorderitem "
                                                   + " WHERE itemdictionary_key = " + itemKeyFilter + ")";
      }
//--------------------------------------------------------------------------------
// 10-12-2008  Limit the purchase order results to a particular scheduled event.
//--------------------------------------------------------------------------------
      if (scheduleKeyFilter > 0) {
        query = query + " AND purchaseorder.schedule_key = " + scheduleKeyFilter;
      }      
      query = query + " ORDER by create_stamp DESC ";

      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + vendorNameFilter.toUpperCase().trim() + "%");
      ps.setString(2, "%" + orderNumberFilter.toUpperCase().trim() + "%");
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"purchaseorder.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          purchaseOrderRow = new PurchaseOrderListDO();

          purchaseOrderRow.setKey(rs.getInt("thekey"));
          purchaseOrderRow.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
          purchaseOrderRow.setVendorKey(rs.getInt("vendor_key"));
          purchaseOrderRow.setVendorName(rs.getString("name"));
          purchaseOrderRow.setOrderFormKey(rs.getByte("order_form_key"));
          purchaseOrderRow.setCustomerKey(rs.getInt("customer_key"));
          purchaseOrderRow.setClientName(rs.getString("client_name"));
          purchaseOrderRow.setSourceTypeKey(rs.getByte("sourcetype_key"));
          purchaseOrderRow.setSourceKey(rs.getInt("source_key"));
          purchaseOrderRow.setScheduleKey(rs.getInt("schedule_key"));
          purchaseOrderRow.setCreateStamp(rs.getTimestamp("create_stamp"));
          purchaseOrderRow.setOrderStatus(rs.getString("order_status"));

//--------------------------------------------------------------------------------
// Find out if the item is eligible for a rebate.
//--------------------------------------------------------------------------------
          purchaseOrderRow.setRebateCount(getRebateService().getRebateCountByPO(conn,
                                                            purchaseOrderRow.getKey(),
                                                            purchaseOrderRow.getCreateStamp()));

//--------------------------------------------------------------------------------
// Find out if the purchase order item has any rebates assigned to it.
//--------------------------------------------------------------------------------
          purchaseOrderRow.setRebateInstanceCount(getRebateService().getRebateInstanceCountByPO(conn,
                                                                    purchaseOrderRow.getKey()));

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRows.add(getUserStateService().getUserListToken(request,conn,
                                  this.getClass().getName(),purchaseOrderRow.getKey()));
       
//--------------------------------------------------------------------------------
// Get quantity and cost totals for items and services respectively.
//--------------------------------------------------------------------------------
          purchaseOrderRow = getItemTotalsByPO(conn, purchaseOrderRow);
          purchaseOrderRow = getServiceTotalsByPO(conn, purchaseOrderRow);
        
          purchaseOrderRow.setOrderTotal(purchaseOrderRow.getItemTotal().add(purchaseOrderRow.getServiceTotal()));

//--------------------------------------------------------------------------------
// The source type key value is based on the FeatureResources.properties file.
//--------------------------------------------------------------------------------
          if (purchaseOrderRow.getSourceTypeKey().compareTo(new Byte("-1")) > 0) {
//--------------------------------------------------------------------------------
// Retrieve the pet, system, or vehicle attributed to this invoice.
//--------------------------------------------------------------------------------
            HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                    purchaseOrderRow.getSourceTypeKey(),
                                                    purchaseOrderRow.getSourceKey());
            purchaseOrderRow.setAttributeToEntity(nameMap.get("sourceName").toString());
            purchaseOrderRow.setAttributeToName(nameMap.get("attributeToName").toString());
          }
          
          purchaseOrderRows.add(purchaseOrderRow);
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
      formHDO.setVendorNameFilter(vendorNameFilter);
      formHDO.setOrderNumberFilter(orderNumberFilter);
      formHDO.setItemKeyFilter(itemKeyFilter);
      formHDO.setScheduleKeyFilter(scheduleKeyFilter);
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
      if (purchaseOrderRows.isEmpty()) {
        purchaseOrderRows.add(new PurchaseOrderListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setPurchaseOrderListForm(purchaseOrderRows);
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
  
  public PurchaseOrderListDO getItemTotalsByPO(Connection conn,
                                               PurchaseOrderListDO formDO)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
      cStmt = conn.prepareCall("{call GetItemTotalsByPO(?,?,?)}");
      cStmt.setInt(1, formDO.getKey());
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": itemQuantity  : " + cStmt.getInt("itemQuantity"));
      cat.debug(this.getClass().getName() + ": itemCostTotal : " + cStmt.getBigDecimal("itemCostTotal"));

      formDO.setItemQty(cStmt.getShort("itemQuantity"));
      formDO.setItemTotal(cStmt.getBigDecimal("itemCostTotal"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemTotalsByPO() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemTotalsByPO() " + e.getMessage());
      }
    }
    return formDO;
  }


  public PurchaseOrderListDO getServiceTotalsByPO(Connection conn,
                                                  PurchaseOrderListDO formDO)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
      cStmt = conn.prepareCall("{call GetServiceTotalsByPO(?,?,?)}");
      cStmt.setInt(1, formDO.getKey());
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": serviceQuantity  : " + cStmt.getInt("serviceQuantity"));
      cat.debug(this.getClass().getName() + ": serviceCostTotal : " + cStmt.getBigDecimal("serviceCostTotal"));

      formDO.setServiceQty(cStmt.getShort("serviceQuantity"));
      formDO.setServiceTotal(cStmt.getBigDecimal("serviceCostTotal"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getServiceTotalsByPO() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getServiceTotalsByPO() " + e.getMessage());
      }
    }
    return formDO;
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
      String vendorNameFilter = "";
      if (!(request.getParameter("vendorNameFilter") == null)) {
        vendorNameFilter = request.getParameter("vendorNameFilter");
      }


      String orderNumberFilter = "";
      if (!(request.getParameter("orderNumberFilter") == null)) {
        orderNumberFilter = request.getParameter("orderNumberFilter");
      }

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }

//--------------------------------------------------------------------------------
// Check for limiting the purchase order list to a particular item.
//--------------------------------------------------------------------------------
      Integer itemKeyFilter = 0;
      if (request.getParameter("itemKeyFilter") != null) {
        itemKeyFilter = new Integer(request.getParameter("itemKeyFilter"));
      }

//--------------------------------------------------------------------------------
// Check for limiting the purchase order list to a particular scheduled event.
//--------------------------------------------------------------------------------
      Integer scheduleKeyFilter = 0;
      if (request.getParameter("scheduleKeyFilter") != null) {
        scheduleKeyFilter = new Integer(request.getParameter("scheduleKeyFilter"));
      }
      
//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        PurchaseOrderListHeadDO formHDO = getPurchaseOrder(request,
                                                           vendorNameFilter,
                                                           orderNumberFilter,
                                                           itemKeyFilter,
                                                           scheduleKeyFilter,
                                                           pageNo);
        request.getSession().setAttribute("purchaseOrderListHDO", formHDO);

        PurchaseOrderListHeadForm formHStr = new PurchaseOrderListHeadForm();
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
