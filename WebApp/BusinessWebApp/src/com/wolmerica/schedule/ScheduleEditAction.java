/*
 * ScheduleEditAction.java
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class ScheduleEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void updateSchedule(HttpServletRequest request,
                              ActionForm form)
    throws Exception, SQLException {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      ScheduleDO formDO = (ScheduleDO) request.getSession().getAttribute("schedule");

//--------------------------------------------------------------------------------
// Clear the address, city, and state value for on-site and default event locations.
//--------------------------------------------------------------------------------
      if (formDO.getLocationId() || !(formDO.getAddressId())) {
        formDO.setAddress("");
        formDO.setCity("");
        formDO.setState("");
      }

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
// Make a call to the SetScheduleVariance() stored procedure.
//--------------------------------------------------------------------------------
      setScheduleVariance(conn,
                          formDO.getKey(),
                          formDO.getStartStamp(),
                          request.getSession().getAttribute("USERNAME").toString());

//--------------------------------------------------------------------------------
// Prepare a SQL query to update a row into the schedule table.
//--------------------------------------------------------------------------------
      String query = "UPDATE schedule SET "
                   + "eventtype_key=?,"
                   + "location_id=?,"
                   + "subject=?,"
                   + "customer_key=?,"
                   + "sourcetype_key=?,"
                   + "source_key=?,"
                   + "start_stamp=?,"
                   + "address_id=?,"
                   + "address=?,"
                   + "city=?,"
                   + "state=?,"
                   + "note_line1=?,"
                   + "status_key=?,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);
      ps.setByte(1, formDO.getEventTypeKey());
      ps.setBoolean(2, formDO.getLocationId());
      ps.setString(3, formDO.getSubject());
      ps.setInt(4, formDO.getCustomerKey());
      ps.setByte(5, formDO.getSourceTypeKey());
      ps.setInt(6, formDO.getSourceKey());
      ps.setTimestamp(7, formDO.getStartStamp());
      ps.setBoolean(8, formDO.getAddressId());
      ps.setString(9, formDO.getAddress());
      ps.setString(10, formDO.getCity());
      ps.setString(11, formDO.getState());
      ps.setString(12, formDO.getNoteLine1());
      ps.setByte(13, formDO.getStatusKey());
      ps.setString(14, request.getSession().getAttribute("USERNAME").toString());
      ps.setInt(15, formDO.getKey());
      ps.executeUpdate();

  //--------------------------------------------------------------------------------
  // An appointment that is cancelled or rescheduled will release any resources.
  // 4-Customer Cancel 5-Customer Reschedule 6-Office Cancel 7-Office Reschedule
  //--------------------------------------------------------------------------------
      if ((formDO.getStatusKey() >= 4) && (formDO.getStatusKey() <= 7)) {
        query = "DELETE FROM resourceinstance "
              + "WHERE workorder_key IN (SELECT thekey "
                                      + "FROM workorder "
                                      + "WHERE schedule_key = ?)";
        ps = conn.prepareStatement(query);
        ps.setInt(1, formDO.getKey());
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
  }

  public Integer setScheduleVariance(Connection conn,
                                     Integer scheduleKey,
                                     Timestamp startStamp,
                                     String updateUser)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer woCnt = 0;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with three IN parameters and zero OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call SetScheduleVariance(?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, scheduleKey);
      cStmt.setTimestamp(2, startStamp);
      cStmt.setString(3, updateUser);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("setScheduleVariance() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("setScheduleVariance() " + e.getMessage());
      }
    }
    return woCnt;
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
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

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

      updateSchedule(request, form);
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
