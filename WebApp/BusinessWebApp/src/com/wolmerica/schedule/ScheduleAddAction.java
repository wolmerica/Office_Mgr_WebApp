/*
 * ScheduleAddAction.java
 *
 * Created on September 7, 2006, 10:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.schedule;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

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
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class ScheduleAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private Integer insertSchedule(HttpServletRequest request,
                                 ActionForm form,
                                 Integer pvKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer schKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      ScheduleDO formDO = (ScheduleDO) request.getSession().getAttribute("schedule");

//--------------------------------------------------------------------------------
// Convert the startDate, startHour, startMinute to a start Timestamp.
//--------------------------------------------------------------------------------
      Calendar calValue = Calendar.getInstance();
      calValue.setTime(formDO.getStartDate());
      calValue.set(calValue.get(Calendar.YEAR),
                   calValue.get(Calendar.MONTH),
                   calValue.get(Calendar.DATE),
                   formDO.getStartHour().intValue(),
                   formDO.getStartMinute().intValue(),
                   0);
      formDO.setStartStamp(new Timestamp(calValue.getTime().getTime()));
      formDO.getStartStamp().setNanos(0);

//--------------------------------------------------------------------------------
// Get the maximum key from the schedule table.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS sch_cnt, MAX(thekey)+1 AS sch_key "
                   + "FROM schedule";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("sch_cnt") > 0 )
          schKey = rs.getInt("sch_key");
      }
      else
        throw new Exception("Schedule MAX() not found!");

//===========================================================================
// Prepare a SQL query to insert a row into the schedule table.
//===========================================================================
      query = "INSERT INTO schedule "
            + "(thekey,"
            + "eventtype_key,"
            + "location_id,"
            + "subject,"
            + "customer_key,"
            + "sourcetype_key,"
            + "source_key,"
            + "start_stamp,"
            + "address_id,"
            + "address,"
            + "city,"
            + "state,"
            + "note_line1,"
            + "status_key,"
            + "create_user,create_stamp,"
            + "update_user,update_stamp )"
            + "VALUES(?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, schKey);
      ps.setByte(2, formDO.getEventTypeKey());
      ps.setBoolean(3, formDO.getLocationId());
      ps.setString(4, formDO.getSubject());
      ps.setInt(5, formDO.getCustomerKey());
      ps.setByte(6, formDO.getSourceTypeKey());
      ps.setInt(7, formDO.getSourceKey());
      ps.setTimestamp(8, formDO.getStartStamp());
      ps.setBoolean(9, formDO.getAddressId());
      ps.setString(10, formDO.getAddress());
      ps.setString(11, formDO.getCity());
      ps.setString(12, formDO.getState());
      ps.setString(13, formDO.getNoteLine1());
      ps.setByte(14, formDO.getStatusKey());
      ps.setString(15, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(16, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// Update the pet vaccination table with the event key associated with it.
//--------------------------------------------------------------------------------
      if (pvKey != null) {
        query = "UPDATE petvaccination "
              + "SET reminder_key = ? "
              + "WHERE thekey = ?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, schKey);
        ps.setInt(2, pvKey);
        ps.executeUpdate();
      }
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
    return schKey;
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
// The key will most likely be the pet vaccination key that initiated a reminder.
//--------------------------------------------------------------------------------
      Integer pvKey = null;
      if (request.getParameter("pvKey") != null) {
        pvKey = new Integer(request.getParameter("pvKey"));
      }

//--------------------------------------------------------------------------------
// Replicate event will set the orginal schedule key in ScheduleEntry
//--------------------------------------------------------------------------------
      String orgsKey = "";  
      if (request.getParameter("copyWO") != null) {
        orgsKey = request.getParameter("copyWO").toString();
        cat.debug(this.getClass().getName() + ": copyWO Parameter..: " + orgsKey);        
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

      Integer theKey = null;
      theKey = insertSchedule(request, form, pvKey);
      request.setAttribute("key", theKey.toString());
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