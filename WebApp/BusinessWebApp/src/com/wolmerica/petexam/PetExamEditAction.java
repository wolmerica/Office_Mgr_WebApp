/*
 * PetExamEditAction.java
 *
 * Created on August 23, 2007  09:11 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petexam;

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
import java.util.Calendar;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

public class PetExamEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void updatePetExam(HttpServletRequest request,
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
        
      PetExamDO formDO = (PetExamDO) request.getSession().getAttribute("petexam");

//--------------------------------------------------------------------------------
// Prepare a SQL statement to update pet exam.
//--------------------------------------------------------------------------------
      String query = "UPDATE petexam SET "
                   + "dvm_resource_key=?,"
                   + "tech_resource_key=?,"
                   + "heart_rate=?,"
                   + "respt_rate=?,"
                   + "cap_refill_time=?,"
                   + "temperature=?,"
                   + "body_weight=?,"
                   + "general_condition=?,"
                   + "medical_data=?,"
                   + "start_stamp=?,"
                   + "surgery_stamp=?,"
                   + "end_stamp=?,"
                   + "reflex_stamp=?,"
                   + "recovery_stamp=?,"
                   + "note_line1=?,"
                   + "release_id=?,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Construct the start stamp value(s) using the treatment date with start hour/minute.
//--------------------------------------------------------------------------------
      Calendar calValue = Calendar.getInstance();
      calValue.setTime(formDO.getTreatmentDate());

      Timestamp startStamp = null;
      calValue.set(calValue.get(Calendar.YEAR),
                   calValue.get(Calendar.MONTH),
                   calValue.get(Calendar.DATE),
                   formDO.getStartHour().intValue(),
                   formDO.getStartMinute().intValue(),
                   0);
      startStamp = new Timestamp(calValue.getTime().getTime());
      startStamp.setNanos(0);

//--------------------------------------------------------------------------------
// Construct the surgery stamp value(s) using the treatment date with start hour/minute.
//--------------------------------------------------------------------------------
      Timestamp surgeryStamp = null;
      calValue.set(calValue.get(Calendar.YEAR),
                   calValue.get(Calendar.MONTH),
                   calValue.get(Calendar.DATE),
                   formDO.getSurgeryHour().intValue(),
                   formDO.getSurgeryMinute().intValue(),
                   0);
      surgeryStamp = new Timestamp(calValue.getTime().getTime());
      surgeryStamp.setNanos(0);

//--------------------------------------------------------------------------------
// Construct the end stamp value(s) using the treatment date with start hour/minute.
//--------------------------------------------------------------------------------
      Timestamp endStamp = null;
      calValue.set(calValue.get(Calendar.YEAR),
                   calValue.get(Calendar.MONTH),
                   calValue.get(Calendar.DATE),
                   formDO.getEndHour().intValue(),
                   formDO.getEndMinute().intValue(),
                   0);
      endStamp = new Timestamp(calValue.getTime().getTime());
      endStamp.setNanos(0);

//--------------------------------------------------------------------------------
// Construct the reflex stamp value(s) using the treatment date with start hour/minute.
//--------------------------------------------------------------------------------
      Timestamp reflexStamp = null;
      calValue.set(calValue.get(Calendar.YEAR),
                   calValue.get(Calendar.MONTH),
                   calValue.get(Calendar.DATE),
                   formDO.getReflexHour().intValue(),
                   formDO.getReflexMinute().intValue(),
                   0);
      reflexStamp = new Timestamp(calValue.getTime().getTime());
      reflexStamp.setNanos(0);

//--------------------------------------------------------------------------------
// Construct the recovery stamp value(s) using the treatment date with start hour/minute.
//--------------------------------------------------------------------------------
      Timestamp recoveryStamp = null;

      calValue.set(calValue.get(Calendar.YEAR),
                   calValue.get(Calendar.MONTH),
                   calValue.get(Calendar.DATE),
                   formDO.getRecoveryHour().intValue(),
                   formDO.getRecoveryMinute().intValue(),
                   0);
      recoveryStamp = new Timestamp(calValue.getTime().getTime());
      recoveryStamp.setNanos(0);
      
      cat.debug(this.getClass().getName() + ": medical data : " + formDO.getMedicalData());
//--------------------------------------------------------------------------------
// Edit a row in the pet exam table.
//--------------------------------------------------------------------------------
      ps.setInt(1, formDO.getDvmResourceKey());
      ps.setInt(2, formDO.getTechResourceKey());
      ps.setString(3, formDO.getHeartRate());
      ps.setString(4, formDO.getResptRate());
      ps.setString(5, formDO.getCapRefillTime());
      ps.setBigDecimal(6, formDO.getTemperature());
      ps.setBigDecimal(7, formDO.getBodyWeight());
      ps.setString(8, formDO.getGeneralCondition());
      ps.setString(9, formDO.getMedicalData());
      ps.setTimestamp(10, startStamp);
      ps.setTimestamp(11, surgeryStamp);
      ps.setTimestamp(12, endStamp);
      ps.setTimestamp(13, reflexStamp);
      ps.setTimestamp(14, recoveryStamp);
      ps.setString(15, formDO.getNoteLine1());
      ps.setBoolean(16, formDO.getReleaseId());
      ps.setString(17, request.getSession().getAttribute("USERNAME").toString());
      ps.setInt(18, formDO.getKey());
      ps.executeUpdate();
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

      updatePetExam(request, form);
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
