/*
 * WorkOrderListAction.java
 *
 * Created on January 8, 2008, 7:45 PM
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
import com.wolmerica.schedule.ScheduleDO;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.resource.ResourceService;
import com.wolmerica.service.resource.DefaultResourceService;
import com.wolmerica.service.schedule.ScheduleService;
import com.wolmerica.service.schedule.DefaultScheduleService;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

public class WorkOrderListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private CustomerService CustomerService = new DefaultCustomerService();
  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private ResourceService resourceService = new DefaultResourceService();
  private ScheduleService scheduleService = new DefaultScheduleService();
  private UserStateService userStateService = new DefaultUserStateService();

  public CustomerService getCustomerService() {
      return CustomerService;
  }

  public void setCustomerService(CustomerService CustomerService) {
      this.CustomerService = CustomerService;
  }

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

  public ResourceService getResourceService() {
      return resourceService;
  }

  public void setResourceService(ResourceService resourceService) {
      this.resourceService = resourceService;
  }

  public ScheduleService getScheduleService() {
      return scheduleService;
  }

  public void setScheduleService(ScheduleService scheduleService) {
      this.scheduleService = scheduleService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private WorkOrderListHeadDO getWorkOrderList(HttpServletRequest request,
                                               Byte sourceTypeKey,
                                               Integer resourceKey,
                                               Integer customerKey,
                                               String fromDate,
                                               String toDate,
                                               Integer pageNo)
   throws Exception, SQLException {

    WorkOrderListHeadDO formHDO = new WorkOrderListHeadDO();
    WorkOrderDO workOrder = null;
    BundleDetailDO bundleDetail = null;
    ArrayList<WorkOrderDO> workOrders = new ArrayList<WorkOrderDO>();
    ScheduleDO scheduleRow = new ScheduleDO();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      formHDO.setSourceTypeKey(sourceTypeKey);
      formHDO.setResourceKey(resourceKey);
      formHDO.setCustomerKey(customerKey);
//--------------------------------------------------------------------------------
// Look-up the resource name with the resource key.
//--------------------------------------------------------------------------------
      if (resourceKey != null) {
        formHDO.setResourceName(getResourceService().getResourceName(conn, resourceKey));
      }

//--------------------------------------------------------------------------------
// Look-up the client name with the customer key.
//--------------------------------------------------------------------------------
      if (customerKey != null) {
        formHDO.setClientName(getCustomerService().getClientName(conn, customerKey));
      }

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

//--------------------------------------------------------------------------------
// Get the work order records for the defined resource, customer, and time frame.
//--------------------------------------------------------------------------------
      String query = "SELECT workorder.thekey,"
                   + "workorder.schedule_key,"
                   + "resourceinstance.resource_key,"
                   + "workorder.sourcetype_key,"
                   + "workorder.source_key,"
                   + "workorder.release_id,"
                   + "workorder.thirdparty_id,"
                   + "workorder.vendor_key,"
                   + "workorder.order_qty,"
                   + "workorder.the_price,"
                   + "DATE(workorder.start_stamp) AS start_date,"
                   + "HOUR(workorder.start_stamp) AS start_hour,"
                   + "MINUTE(workorder.start_stamp) AS start_minute,"
                   + "DATE(workorder.end_stamp) AS end_date,"
                   + "HOUR(workorder.end_stamp) AS end_hour,"
                   + "MINUTE(workorder.end_stamp) AS end_minute "
                   + "FROM schedule, workorder "
                   + "LEFT JOIN resourceinstance "
                   + "ON resourceinstance.workorder_key = workorder.thekey "
                   + "WHERE schedule.thekey = workorder.schedule_key "
                   + "AND DATE(workorder.start_stamp) BETWEEN ? AND ?";
      if (sourceTypeKey > 0)
        query = query + " AND workorder.sourcetype_key = " + sourceTypeKey;
      if (resourceKey != null)
        query = query + " AND resourceinstance.resource_key = " + resourceKey;
      if (customerKey != null)
        query = query + " AND schedule.customer_key = " + customerKey;
      query = query + " ORDER BY workorder.start_stamp, workorder.sourcetype_key DESC";
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"workorder.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

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
          workOrder.setStartDate(rs.getDate("start_date"));
          workOrder.setStartHour(rs.getByte("start_hour"));
          workOrder.setStartMinute(rs.getByte("start_minute"));
          workOrder.setEndDate(rs.getDate("end_date"));
          workOrder.setEndHour(rs.getByte("end_hour"));
          workOrder.setEndMinute(rs.getByte("end_minute"));
          workOrder.setSourceTypeKey(bundleDetail.getSourceTypeKey());
          workOrder.setSourceName(bundleDetail.getSourceName());
          workOrder.setSourceNum(bundleDetail.getSourceNum());
          workOrder.setCategoryName(bundleDetail.getCategoryName());
          workOrder.setPriceTypeKey(bundleDetail.getPriceTypeKey());
          workOrder.setPriceTypeName(bundleDetail.getPriceTypeName());
          workOrder.setOrderQty(bundleDetail.getOrderQty());
          workOrder.setSize(bundleDetail.getSize());
          workOrder.setSizeUnit(bundleDetail.getSizeUnit());
          workOrder.setEstimatedPrice(rs.getBigDecimal("the_price"));
          workOrder.setExtendedPrice(workOrder.getEstimatedPrice().multiply(rs.getBigDecimal("order_qty")));

          scheduleRow = new ScheduleDO();
          scheduleRow = getScheduleService().buildScheduleForm(conn, workOrder.getScheduleKey());
          workOrder.setClientName(scheduleRow.getClientName());
          workOrder.setAttributeToEntity(scheduleRow.getAttributeToEntity());
          workOrder.setAttributeToName(scheduleRow.getAttributeToName());
          workOrder.setActiveId(scheduleRow.getActiveId());

          if (rs.getString("resource_key") != null)
            workOrder.setResourceName(getResourceService().getResourceName(conn, rs.getInt("resource_key")));

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
      if (workOrders.isEmpty()) {
        workOrders.add(new WorkOrderDO());
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
// A sourceTypeKey of zero indicates the user wants to see all.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = 0;
      if (request.getParameter("sourceTypeKey") != null) {
        if (!(request.getParameter("sourceTypeKey").equalsIgnoreCase("")))
          sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }
      cat.debug(this.getClass().getName() + ": sourceTypeKey..: " + sourceTypeKey);      
      
//--------------------------------------------------------------------------------
// No resourceKey indicates the user wants to see all the resources.
//--------------------------------------------------------------------------------
      Integer resourceKey = null;
      if (request.getParameter("resourceKey") != null) {
        if (!(request.getParameter("resourceKey").equalsIgnoreCase("")))
          resourceKey = new Integer(request.getParameter("resourceKey"));
      }
      cat.debug(this.getClass().getName() + ": resourceKey....: " + resourceKey);
      
//--------------------------------------------------------------------------------
// No customerKey indicates the user wants all the customers.
//--------------------------------------------------------------------------------
      Integer customerKey = null;
      if (request.getParameter("customerKey") != null) {
        if (!(request.getParameter("customerKey").equalsIgnoreCase("")))
          customerKey = new Integer(request.getParameter("customerKey"));
      }
      cat.debug(this.getClass().getName() + ": customerKey....: " + customerKey);      

//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getYTDFromDate());
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getYTDToDate());

      if (request.getParameter("fromDate") != null) {
        if (request.getParameter("fromDate").length() > 0 ) {
          fromDate = request.getParameter("fromDate");
        }
      }

      if (request.getParameter("toDate") != null) {
        if (request.getParameter("toDate").length() > 0 ) {
          toDate = request.getParameter("toDate");
        }
      }

//--------------------------------------------------------------------------------
// Get the page number value.
//--------------------------------------------------------------------------------
      Integer pageNo = new Integer("1");
      if (request.getParameter("pageNo") != null) {
        if (!(request.getParameter("pageNo").equalsIgnoreCase("")))
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
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        WorkOrderListHeadDO formHDO = getWorkOrderList(request,
                                                       sourceTypeKey,
                                                       resourceKey,
                                                       customerKey,
                                                       fromDate,
                                                       toDate,
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
