/*
 * AttributeToEventListAction.java
 *
 * Created on October 08, 2006, 8:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 11/01/2007 Leverage additional detail from work order.
 */

package com.wolmerica.resourceinstance;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.schedule.ScheduleService;
import com.wolmerica.service.schedule.DefaultScheduleService;
import com.wolmerica.schedule.ScheduleForm;
import com.wolmerica.workorder.WorkOrderForm;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
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
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class AttributeToEventListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
  private ScheduleService scheduleService = new DefaultScheduleService();
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

  
  private ResourceInstanceListHeadForm getAttributeToEventList(HttpServletRequest request,
                                                               Byte sourceTypeKey,
                                                               Integer sourceKey,
                                                               String fromDate,
                                                               String toDate,
                                                               Integer pageNo)
    throws Exception, SQLException {

    
    ResourceInstanceListHeadForm formHStr = new ResourceInstanceListHeadForm();
    ResourceInstanceForm resourceInstanceRow = null;
    ArrayList<ResourceInstanceForm> resourceInstanceRows = new ArrayList<ResourceInstanceForm>();
    ArrayList<ScheduleForm> scheduleRows = new ArrayList<ScheduleForm>();
    ArrayList<WorkOrderForm> workOrderRows = new ArrayList<WorkOrderForm>();
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    Calendar startDay = Calendar.getInstance();
    Calendar lastDay = Calendar.getInstance();
    Integer scheduleKey = -1;

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date dtFromDate = (Date) dateFormatter.unformat(fromDate);
      Date dtToDate = (Date) dateFormatter.unformat(toDate);
      cat.debug(this.getClass().getName() + ": sourceTypeKey=" + sourceTypeKey.toString());
      cat.debug(this.getClass().getName() + ": sourceKey=" + sourceKey.toString());
      cat.debug(this.getClass().getName() + ": fromDate=" + fromDate);
      cat.debug(this.getClass().getName() + ": toDate=" + toDate);
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the attribute to appointment records.
//--------------------------------------------------------------------------------
      String query = "SELECT DISTINCT DATE (workorder.start_stamp) AS start_date "
                   + "FROM schedule, workorder "
                   + "WHERE schedule.sourcetype_key = ? "
                   + "AND schedule.source_key = ? "
                   + "AND schedule.eventtype_key = 0 "
                   + "AND schedule.thekey = workorder.schedule_key "
                   + "AND ? BETWEEN DATE(workorder.start_stamp) AND DATE(workorder.end_stamp) "
                   + "AND workorder.sourcetype_key = 6 "
                   + "ORDER BY start_date";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Loop thru all the days now that we are supporting start and end dates.
//--------------------------------------------------------------------------------
      startDay.setTime(dtFromDate);
      startDay.set(startDay.get(Calendar.YEAR),
                   startDay.get(Calendar.MONTH),
                   startDay.get(Calendar.DATE),
                   0,0,0);
      lastDay.setTime(dtToDate);
      lastDay.set(lastDay.get(Calendar.YEAR),
                  lastDay.get(Calendar.MONTH),
                  lastDay.get(Calendar.DATE),
                   23,0,0);

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"resourceinstance.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      Integer recordCount = 0;
//--------------------------------------------------------------------------------
// Loop through all the dates between the from and to dates.
//--------------------------------------------------------------------------------
      while (lastDay.compareTo(startDay) > 0) {
        dtToDate = new Date(lastDay.getTime().getTime());
        cat.debug(this.getClass().getName() + ": dtToDate = " + dtToDate);

        ps.setByte(1, sourceTypeKey);
        ps.setInt(2, sourceKey);
        ps.setDate(3, new java.sql.Date(dtToDate.getTime()));
        rs = ps.executeQuery();

        while ( rs.next() ) {
          ++recordCount;
          resourceInstanceRow = new ResourceInstanceForm();

          resourceInstanceRow.setStartDate(dateFormatter.formatToWords(dtToDate));

//--------------------------------------------------------------------------------
// 09-13-2006 Add workOrderRows to the resourceInstance object.
//--------------------------------------------------------------------------------
          if (scheduleRows.isEmpty())
            scheduleRows.add(new ScheduleForm());
          resourceInstanceRow.setScheduleForm(scheduleRows);

//--------------------------------------------------------------------------------
// 10-29-2007 Get all work order entries for the particular resource that occur
// between the start and end date.  Pass zero as the work order key since we need
// not exclude any work orders.  Return an ArrayList() of work order objects.
//--------------------------------------------------------------------------------
          workOrderRows = getScheduleService().getAttributeToSchedule(request,
                                                                      conn,
                                                                      sourceTypeKey,
                                                                      sourceKey,
                                                                      dtToDate);
                  
          resourceInstanceRow.setEventCount(new Integer(workOrderRows.size()).toString());

//--------------------------------------------------------------------------------
// 10-28-2007 Add workOrderRows to the resourceInstance object.
//--------------------------------------------------------------------------------
          if (workOrderRows.isEmpty())
            workOrderRows.add(new WorkOrderForm());
          resourceInstanceRow.setWorkOrderForm(workOrderRows);

          resourceInstanceRows.add(resourceInstanceRow);
        }
        lastDay.add(Calendar.DATE, -1);
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
      formHStr.setRecordCount(recordCount.toString());
      formHStr.setFirstRecord(firstRecord.toString());
      formHStr.setLastRecord(lastRecord.toString());
      formHStr.setPreviousPage(prevPage.toString());
      formHStr.setNextPage(nextPage.toString());
//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (resourceInstanceRows.isEmpty())
        resourceInstanceRows.add(new ResourceInstanceForm());
      if (permissionRows.isEmpty())
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));

      formHStr.setResourceInstanceForm(resourceInstanceRows);
      formHStr.setPermissionListForm(permissionRows);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
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
    return formHStr;
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
// The source type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey").toString());
      }
      else {
        if (request.getAttribute("sourceTypeKey") != null) {
          sourceTypeKey = new Byte(request.getAttribute("sourceTypeKey").toString());
        }
        else {
          throw new Exception("Request getParameter [sourceTypeKey] not found!");
        }
      }

//--------------------------------------------------------------------------------
// The source key value will either reference a schedule or resource record.
//--------------------------------------------------------------------------------
      Integer sourceKey = null;
      if (request.getParameter("key") != null) {
        sourceKey = new Integer(request.getParameter("key"));
      }
      else {
        if (request.getAttribute("key") != null) {
          sourceKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }

//--------------------------------------------------------------------------------
// Page number will default to one unless otherwise passed in parameter.
//--------------------------------------------------------------------------------
      Integer pageNo = new Integer("1");
      if (request.getParameter("pageNo") != null) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }

//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getBACKFromDate(new Integer(getPropertyService().getCustomerProperties(request,"schedule.days.back")).intValue()));
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getFWDToDate(new Integer(getPropertyService().getCustomerProperties(request,"schedule.days.ahead")).intValue()));

      if (!(request.getParameter("fromDate") == null)) {
        if (request.getParameter("fromDate").length() > 0 ) {
          fromDate = request.getParameter("fromDate");
        }
      }

      if (!(request.getParameter("toDate") == null)) {
        if (request.getParameter("toDate").length() > 0 ) {
          toDate = request.getParameter("toDate");
        }
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
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        ResourceInstanceListHeadForm formHStr = getAttributeToEventList(request,
                                                                        sourceTypeKey,
                                                                        sourceKey,
                                                                        fromDate,
                                                                        toDate,
                                                                        pageNo);
//--------------------------------------------------------------------------------
// Create the wrapper object for resource list.
//--------------------------------------------------------------------------------
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