/*
 * PetExamGetAction.java
 *
 * Created on August 23, 2007  09:20 PM
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
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
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
import java.util.HashMap;

import org.apache.log4j.Logger;

public class PetExamGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  
  private AttributeToService attributeToService = new DefaultAttributeToService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private PetExamDO buildPetExamForm(HttpServletRequest request,
                                     Integer peKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    PetExamDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT petexam.thekey,"
                   + "customer.client_name,"
                   + "schedule.source_key AS pet_key,"
                   + "pet.name AS pet_name,"
                   + "pet.species_key,"
                   + "pet.breed_key,"
                   + "petexam.schedule_key,"
                   + "DATE(schedule.start_stamp) AS treatment_date,"
                   + "schedule.subject,"
                   + "petexam.dvm_resource_key,"
                   + "r1.name AS dvm_resource_name,"
                   + "petexam.tech_resource_key,"
                   + "r2.name AS tech_resource_name,"
                   + "petexam.heart_rate,"
                   + "petexam.respt_rate,"
                   + "petexam.cap_refill_time,"
                   + "petexam.temperature,"
                   + "petexam.body_weight,"
                   + "petexam.general_condition,"
                   + "petexam.medical_data,"
                   + "HOUR(petexam.start_stamp) AS start_hour,"
                   + "MINUTE(petexam.start_stamp) AS start_minute,"
                   + "HOUR(petexam.surgery_stamp) AS surgery_hour,"
                   + "MINUTE(petexam.surgery_stamp) AS surgery_minute,"
                   + "HOUR(petexam.end_stamp) AS end_hour,"
                   + "MINUTE(petexam.end_stamp) AS end_minute,"
                   + "HOUR(petexam.reflex_stamp) AS reflex_hour,"
                   + "MINUTE(petexam.reflex_stamp) AS reflex_minute,"
                   + "HOUR(petexam.recovery_stamp) AS recovery_hour,"
                   + "MINUTE(petexam.recovery_stamp) AS recovery_minute,"
                   + "petexam.note_line1,"
                   + "petexam.release_id,"
                   + "petexam.create_user,"
                   + "petexam.create_stamp,"
                   + "petexam.update_user,"
                   + "petexam.update_stamp "
                   + "FROM customer, pet, petexam, resource r1, resource r2, schedule "
                   + "WHERE petexam.thekey = ? "
                   + "AND petexam.schedule_key = schedule.thekey "
                   + "AND schedule.source_key = pet.thekey "
                   + "AND schedule.customer_key = customer.thekey "
                   + "AND petexam.dvm_resource_key = r1.thekey "
                   + "AND petexam.tech_resource_key = r2.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, peKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {

        formDO = new PetExamDO();

        formDO.setKey(rs.getInt("thekey"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setPetKey(rs.getInt("pet_key"));
        formDO.setScheduleKey(rs.getInt("schedule_key"));
        formDO.setTreatmentDate(rs.getDate("treatment_date"));
        formDO.setSubject(rs.getString("subject"));
        formDO.setDvmResourceKey(rs.getInt("dvm_resource_key"));
        formDO.setDvmResourceName(rs.getString("dvm_resource_name"));
        formDO.setTechResourceKey(rs.getInt("tech_resource_key"));
        formDO.setTechResourceName(rs.getString("tech_resource_name"));
        formDO.setHeartRate(rs.getString("heart_rate"));
        formDO.setResptRate(rs.getString("respt_rate"));
        formDO.setCapRefillTime(rs.getString("cap_refill_time"));
        formDO.setTemperature(rs.getBigDecimal("temperature"));
        formDO.setBodyWeight(rs.getBigDecimal("body_weight"));
        formDO.setGeneralCondition(rs.getString("general_condition"));
        formDO.setMedicalData(rs.getString("medical_data"));
        formDO.setStartHour(rs.getByte("start_hour"));
        formDO.setStartMinute(rs.getByte("start_minute"));
        formDO.setSurgeryHour(rs.getByte("surgery_hour"));
        formDO.setSurgeryMinute(rs.getByte("surgery_minute"));
        formDO.setEndHour(rs.getByte("end_hour"));
        formDO.setEndMinute(rs.getByte("end_minute"));
        formDO.setReflexHour(rs.getByte("reflex_hour"));
        formDO.setReflexMinute(rs.getByte("reflex_minute"));
        formDO.setRecoveryHour(rs.getByte("recovery_hour"));
        formDO.setRecoveryMinute(rs.getByte("recovery_minute"));
        formDO.setNoteLine1(rs.getString("note_line1"));
        formDO.setReleaseId(rs.getBoolean("release_id"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

        HashMap nameMap = getAttributeToService().getAttributeToName(conn, new Byte("4"), formDO.getPetKey());
        formDO.setPetName(nameMap.get("attributeToName").toString());
      }
      else {
        throw new Exception("Pet Exam " + peKey.toString() + " not found!");
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        PetExamDO formDO = buildPetExamForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("petexam", formDO);
        PetExamForm formStr = new PetExamForm();
        formStr.populate(formDO);

        form = formStr;
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