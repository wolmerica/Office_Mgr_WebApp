/*
 * EventResourceListAction.java
 *
 * Created on September 10, 2006, 9:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.resourceinstance;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.schedule.ScheduleForm;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.workorder.WorkOrderForm;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

public class EventResourceListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
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

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private ResourceInstanceListHeadForm getEventResourceList(HttpServletRequest request,
                                                            Integer scheduleKey,
                                                            Integer pageNo)
    throws Exception, SQLException {

    
    ResourceInstanceListHeadForm formHStr = new ResourceInstanceListHeadForm();
    ResourceInstanceForm resourceInstanceRow = null;
    ArrayList<ResourceInstanceForm> resourceInstanceRows = new ArrayList<ResourceInstanceForm>();
    ArrayList<ScheduleForm> scheduleRows = new ArrayList<ScheduleForm>();
    ArrayList<WorkOrderForm> workOrderRows = new ArrayList<WorkOrderForm>();
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Define and initialize date value to get the minimum and maximum.
//--------------------------------------------------------------------------------
      Date startDate = getDateRangeService().getYTDFromDate();
      Date endDate = getDateRangeService().getYTDToDate();

//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the resource for an event.
//--------------------------------------------------------------------------------
      String query = "SELECT resource_key,"
                   + "resource.name AS resource_name, "
                   + "DATE (start_stamp) AS start_date, "
                   + "DATE (end_stamp) AS end_date "
                   + "FROM workorder, resourceinstance, resource "
                   + "WHERE workorder.schedule_key = ? "
                   + "AND workorder.thekey = resourceinstance.workorder_key "
                   + "AND resourceinstance.resource_key = resource.thekey "
                   + "ORDER BY resource_name";
      ps = conn.prepareStatement(query);
      ps.setInt(1, scheduleKey);
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"resourceinstance.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          resourceInstanceRow = new ResourceInstanceForm();

          resourceInstanceRow.setScheduleKey(scheduleKey.toString());
          resourceInstanceRow.setResourceKey(rs.getString("resource_key"));
          resourceInstanceRow.setResourceName(rs.getString("resource_name"));
          resourceInstanceRow.setStartDate(rs.getString("start_date"));
          resourceInstanceRow.setEndDate(rs.getString("end_date"));

//--------------------------------------------------------------------------------
// Get the minimum start date and the maximum end date from the list.
//--------------------------------------------------------------------------------
          if (startDate.compareTo(rs.getDate("start_date")) < 0)
            startDate = rs.getDate("start_date");
          if (endDate.compareTo(rs.getDate("end_date")) > 0)
            endDate = rs.getDate("end_date");

//--------------------------------------------------------------------------------
// 09-13-2006 Add workOrderRows to the resourceInstance object.
//--------------------------------------------------------------------------------
          if (scheduleRows.isEmpty())
            scheduleRows.add(new ScheduleForm());
          resourceInstanceRow.setScheduleForm(scheduleRows);

//--------------------------------------------------------------------------------
// 10-28-2007 Add workOrderRows to the resourceInstance object.
//--------------------------------------------------------------------------------
          if (workOrderRows.isEmpty())
            workOrderRows.add(new WorkOrderForm());
          resourceInstanceRow.setWorkOrderForm(workOrderRows);


          resourceInstanceRows.add(resourceInstanceRow);
        }
      }
//--------------------------------------------------------------------------------
// Now we need to list all the unallocated resources and show their availability.
//--------------------------------------------------------------------------------
      query = "SELECT thekey,"
            + "name "
            + "FROM resource "
            + "WHERE thekey NOT IN (SELECT resource_key "
                                 + "FROM workorder, resourceinstance "
                                 + "WHERE workorder.schedule_key = ? "
                                 + "AND workorder.thekey = resourceinstance.workorder_key) "
            + "ORDER BY name";
      ps = conn.prepareStatement(query);
      ps.setInt(1, scheduleKey);
      rs = ps.executeQuery();
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          resourceInstanceRow = new ResourceInstanceForm();

          resourceInstanceRow.setScheduleKey(new String("0"));
          resourceInstanceRow.setResourceKey(rs.getString("thekey"));
          resourceInstanceRow.setResourceName(rs.getString("name"));
          resourceInstanceRow.setStartDate(startDate.toString());
          resourceInstanceRow.setEndDate(endDate.toString());

//--------------------------------------------------------------------------------
// 09-13-2006 Add workOrderRows to the resourceInstance object.
//--------------------------------------------------------------------------------
          if (scheduleRows.isEmpty())
            scheduleRows.add(new ScheduleForm());
          resourceInstanceRow.setScheduleForm(scheduleRows);

//--------------------------------------------------------------------------------
// 10-28-2007 Add workOrderRows to the resourceInstance object.
//--------------------------------------------------------------------------------
          if (workOrderRows.isEmpty())
            workOrderRows.add(new WorkOrderForm());
          resourceInstanceRow.setWorkOrderForm(workOrderRows);

          resourceInstanceRows.add(resourceInstanceRow);
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
// Page number will default to one unless otherwise passed in parameter.
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
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        ResourceInstanceListHeadForm formHStr = getEventResourceList(request,
                                                                     theKey,
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
