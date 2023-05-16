/*
 * WorkOrderGetAction.java
 *
 * Created on October 13, 2007, 11:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.workorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.bundledetail.BundleDetailDO;
import com.wolmerica.itemdictionary.ItemDictionaryListDO;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
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
import java.util.Calendar;

import org.apache.log4j.Logger;

public class WorkOrderGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

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

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private WorkOrderListHeadDO getWorkOrder(HttpServletRequest request,
                                           Integer sKey,
                                           Integer pageNo)
   throws Exception, SQLException {

    WorkOrderListHeadDO formHDO = new WorkOrderListHeadDO();
    WorkOrderDO workOrder = null;
    BundleDetailDO bundleDetail = null;
    ItemDictionaryListDO idRow = new ItemDictionaryListDO();
    ArrayList<WorkOrderDO> workOrders = new ArrayList<WorkOrderDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Calendar rightNow = Calendar.getInstance();

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      Integer forecastDays = new Integer(getPropertyService().getCustomerProperties(request,"itemdictionary.forecast.days"));

//--------------------------------------------------------------------------------
// Get the work order records related to the schedule.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "schedule_key,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "release_id,"
                   + "thirdparty_id,"
                   + "vendor_key,"
                   + "order_qty,"
                   + "the_price,"
                   + "DATE(start_stamp) AS start_date,"
                   + "HOUR(start_stamp) AS start_hour,"
                   + "MINUTE(start_stamp) AS start_minute,"
                   + "DATE(end_stamp) AS end_date,"
                   + "HOUR(end_stamp) AS end_hour,"
                   + "MINUTE(end_stamp) AS end_minute,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM workorder "
                   + "WHERE schedule_key = ? "
                   + "ORDER by thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, sKey);
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"workorder.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      Short minQty = 0;
      Short maxQty = 0;
      cat.debug(this.getClass().getName() + ": GetWorkOrder() " + sKey);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          bundleDetail = new BundleDetailDO();
          bundleDetail.setKey(rs.getInt("thekey"));
          bundleDetail.setSourceTypeKey(rs.getByte("sourcetype_key"));
          bundleDetail.setSourceKey(rs.getInt("source_key"));
          bundleDetail.setOrderQty(rs.getShort("order_qty"));

          bundleDetail = getItemAndSrvService().getItemOrServiceName(conn, bundleDetail);

          workOrder = new WorkOrderDO();
          workOrder.setKey(rs.getInt("thekey"));
          workOrder.setScheduleKey(rs.getInt("schedule_key"));
          workOrder.setSourceTypeKey(rs.getByte("sourcetype_key"));
          workOrder.setSourceKey(rs.getInt("source_key"));
          workOrder.setReleaseId(rs.getBoolean("release_id"));
          workOrder.setThirdPartyId(rs.getBoolean("thirdparty_id"));
          workOrder.setVendorKey(rs.getInt("vendor_key"));
          workOrder.setOrderQty(rs.getShort("order_qty"));
          workOrder.setStartDate(rs.getDate("start_date"));
          workOrder.setStartHour(rs.getByte("start_hour"));
          workOrder.setStartMinute(rs.getByte("start_minute"));
          workOrder.setEndDate(rs.getDate("end_date"));
          workOrder.setEndHour(rs.getByte("end_hour"));
          workOrder.setEndMinute(rs.getByte("end_minute"));
          workOrder.setCreateUser(rs.getString("create_user"));
          workOrder.setCreateStamp(rs.getTimestamp("create_stamp"));
          workOrder.setUpdateUser(rs.getString("update_user"));
          workOrder.setUpdateStamp(rs.getTimestamp("update_stamp"));
          workOrder.setSourceName(bundleDetail.getSourceName());
          workOrder.setSourceNum(bundleDetail.getSourceNum());
          workOrder.setCategoryName(bundleDetail.getCategoryName());
          workOrder.setPriceTypeKey(bundleDetail.getPriceTypeKey());
          workOrder.setPriceTypeName(bundleDetail.getPriceTypeName());
          workOrder.setSize(bundleDetail.getSize());
          workOrder.setSizeUnit(bundleDetail.getSizeUnit());
          workOrder.setEstimatedPrice(rs.getBigDecimal("the_price"));
          workOrder.setExtendedPrice(workOrder.getEstimatedPrice().multiply(rs.getBigDecimal("order_qty")));

//--------------------------------------------------------------------------------
// To retrieve the item availability we first need to use the sourceTypeKey and sourceKey
// to return the item key.  The item key and size are passed to get the available quantity.
//--------------------------------------------------------------------------------
          if (workOrder.getSourceTypeKey() == 3) {
            idRow.setKey(getItemAndSrvService().getItemOrServiceKey(conn,
                                                   workOrder.getSourceTypeKey(),
                                                   workOrder.getSourceKey()));

            idRow.setBrandName(workOrder.getSourceName());
            idRow.setSize(workOrder.getSize());
            idRow.setSizeUnit(workOrder.getSizeUnit());

//--------------------------------------------------------------------------------
// Get the quantity available for the items.
//--------------------------------------------------------------------------------
            idRow = getItemAndSrvService().getItemAvailability(conn,
                                              idRow.getKey(),
                                              idRow);

            workOrder.setAvailableQty(idRow.getQtyOnHand().shortValue());
          }

//--------------------------------------------------------------------------------
// 10-30-06  Check if the vendor has any dependency on linked to tables.
//--------------------------------------------------------------------------------
          workOrder.setAllowDeleteId(getWorkOrderDependency(conn,
                                                            workOrder.getKey()));

          workOrders.add(workOrder);
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
//--------------------------------------------------------------------------------
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setScheduleKey(sKey);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (workOrders.isEmpty())
        workOrders.add(new WorkOrderDO());
      else {
        formHDO.setFromDate(workOrder.getStartDate());
        formHDO.setToDate(workOrder.getEndDate());
      }
      formHDO.setWorkOrderForm(workOrders);
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


  public Boolean getWorkOrderDependency(Connection conn,
                                        Integer sKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean allowDeleteId = false;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetWorkOrderDependency(?,?,?)}");
      cStmt.setInt(1, sKey);
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
      throw new Exception("getWorkOrderDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getWorkOrderDependency() " + e.getMessage());
      }
    }
    return allowDeleteId;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws Exception, IOException, SQLException, ServletException {

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
// Get the schedule key value.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (request.getAttribute("key") != null) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

//--------------------------------------------------------------------------------
// Get the page number value.
//--------------------------------------------------------------------------------
      Integer pageNo = new Integer("1");
      if (request.getParameter("pageNo") != null) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        WorkOrderListHeadDO formHDO = getWorkOrder(request,
                                                   theKey,
                                                   pageNo);
        formHDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("workorderHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for customerinvoiceitem.
//--------------------------------------------------------------------------------
        WorkOrderListHeadForm formHStr = new WorkOrderListHeadForm();
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
