/*
 * PetExamAddAction.java
 *
 * Created on August 23, 2007  08:53 PM
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

public class PetExamAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer insertPetExam(HttpServletRequest request,
                                ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer peKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      PetExamDO formDO = (PetExamDO) request.getSession().getAttribute("petexam");

//--------------------------------------------------------------------------------
// Get the maximum key from the pet exam key.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS petexam_cnt, MAX(thekey)+1 AS petexam_key "
                   + "FROM petexam";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("petexam_cnt") > 0 ) {
          peKey = rs.getInt("petexam_key");
        }
      }
      else {
        throw new Exception("Pet Exam MAX() not found!");
      }
      cat.warn(this.getClass().getName() + ": pet exam max() : " + peKey);
//===========================================================================
// Prepare a SQL query to insert a row into the pet exam table.
//===========================================================================
      query = "INSERT INTO petexam "
            + "(thekey,"
            + "schedule_key,"
            + "dvm_resource_key,"
            + "tech_resource_key,"
            + "heart_rate,"
            + "respt_rate,"
            + "cap_refill_time,"
            + "temperature,"
            + "body_weight,"
            + "general_condition,"
            + "medical_data,"
            + "start_stamp,"
            + "surgery_stamp,"
            + "end_stamp,"
            + "reflex_stamp,"
            + "recovery_stamp,"
            + "note_line1,"
            + "release_id,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,?,?,?,?,?,"
            + "CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      cat.warn(this.getClass().getName() + ": prepare statement"); 
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
      cat.warn(this.getClass().getName() + ": prepare statement"); 
//--------------------------------------------------------------------------------
// Add a new row to the pet exam table.
//--------------------------------------------------------------------------------
      ps.setInt(1, peKey);
      ps.setInt(2, formDO.getScheduleKey());
      ps.setInt(3, formDO.getDvmResourceKey());
      ps.setInt(4, formDO.getTechResourceKey());
      ps.setString(5, formDO.getHeartRate());
      ps.setString(6, formDO.getResptRate());
      ps.setString(7, formDO.getCapRefillTime());
      ps.setBigDecimal(8, formDO.getTemperature());
      ps.setBigDecimal(9, formDO.getBodyWeight());
      ps.setString(10, formDO.getGeneralCondition());
      ps.setString(11, formDO.getMedicalData());
      ps.setTimestamp(12, startStamp);
      ps.setTimestamp(13, surgeryStamp);
      ps.setTimestamp(14, endStamp);
      ps.setTimestamp(15, reflexStamp);
      ps.setTimestamp(16, recoveryStamp);
      ps.setString(17, formDO.getNoteLine1());
      ps.setBoolean(18, formDO.getReleaseId());
      ps.setString(19, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(20, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();
      cat.warn(this.getClass().getName() + ": executeUpdate()");
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
    return peKey;
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
    cat.warn(this.getClass().getName() + ": PetExamAddAction execute()");
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
      theKey = insertPetExam(request, form);
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