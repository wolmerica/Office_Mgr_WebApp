/*
 * PetBoardGetAction.java
 *
 * Created on June 20, 2007  08:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petboard;

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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class PetBoardGetAction extends Action {

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


  private PetBoardDO buildPetBoardForm(HttpServletRequest request,
                                       Integer pbKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    CallableStatement cStmt = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    PetBoardDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT pb.thekey,"
                   + "customer.client_name,"
                   + "pet.thekey AS pet_key,"
                   + "pb.schedule_key,"
                   + "DATE(schedule.start_stamp) AS check_in_date,"
                   + "HOUR(schedule.start_stamp) AS check_in_hour,"
                   + "MINUTE(schedule.start_stamp) AS check_in_minute,"
                   + "pb.check_out_to,"
                   + "pb.board_reason,"
                   + "pb.board_instruction,"
                   + "pb.emergency_name,"
                   + "pb.emergency_phone,"
                   + "pb.vaccination_id,"
                   + "pb.special_diet_id,"
                   + "pb.medication_id,"
                   + "pb.service_id,"
                   + "pb.create_user,"
                   + "pb.create_stamp,"
                   + "pb.update_user,"
                   + "pb.update_stamp "
                   + "FROM customer, pet, petboarding pb, schedule "
                   + "WHERE pb.thekey = ? "
                   + "AND pb.schedule_key = schedule.thekey "
                   + "AND schedule.source_key = pet.thekey "
                   + "AND schedule.customer_key = customer.thekey";
      ps = conn.prepareStatement(query);

      ps.setInt(1, pbKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        formDO = new PetBoardDO();
        formDO.setKey(rs.getInt("thekey"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setPetKey(rs.getInt("pet_key"));
        formDO.setScheduleKey(rs.getInt("schedule_key"));
        formDO.setCheckInDate(rs.getDate("check_in_date"));
        formDO.setCheckInHour(rs.getByte("check_in_hour"));
        formDO.setCheckInMinute(rs.getByte("check_in_minute"));
        formDO.setCheckOutTo(rs.getString("check_out_to"));
        formDO.setBoardReason(rs.getString("board_reason"));
        formDO.setBoardInstruction(rs.getString("board_instruction"));
        formDO.setEmergencyName(rs.getString("emergency_name"));
        formDO.setEmergencyPhone(rs.getString("emergency_phone"));
        formDO.setVaccinationId(rs.getBoolean("vaccination_id"));
        formDO.setSpecialDietId(rs.getBoolean("special_diet_id"));
        formDO.setMedicationId(rs.getBoolean("medication_id"));
        formDO.setServiceId(rs.getBoolean("service_id"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

        HashMap nameMap = getAttributeToService().getAttributeToName(conn, new Byte("4"), formDO.getPetKey());
        formDO.setPetName(nameMap.get("attributeToName").toString());
      }
      else {
        throw new Exception("Pet Boarding " + pbKey.toString() + " not found!");
      }
//--------------------------------------------------------------------------------
// Get the check-out date, hour, and minute values from work order record.
//--------------------------------------------------------------------------------
      query = "SELECT DATE(MAX(end_stamp)) AS check_out_date,"
            + "HOUR(MAX(end_stamp)) AS check_out_hour,"
            + "MINUTE(MAX(end_stamp)) AS check_out_minute "
            + "FROM workorder "
            + "WHERE schedule_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, formDO.getScheduleKey());
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Populate the boarding check out values if they exist.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        formDO.setCheckOutDate(rs.getDate("check_out_date"));
        formDO.setCheckOutHour(rs.getByte("check_out_hour"));
        formDO.setCheckOutMinute(rs.getByte("check_out_minute"));
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
      if (cStmt != null) {
        try {
          cStmt.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        cStmt = null;
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
        PetBoardDO formDO = buildPetBoardForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("petboard", formDO);
        PetBoardForm formStr = new PetBoardForm();
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