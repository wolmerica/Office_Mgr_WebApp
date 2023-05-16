/*
 * PetVacGetAction.java
 *
 * Created on May 07, 2007  08:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petvac;

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

public class PetVacGetAction extends Action {

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

  private PetVacDO buildPetVacForm(HttpServletRequest request,
                                   Integer pvKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    PetVacDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT petvaccination.thekey,"
                   + "petvaccination.schedule_key,"
                   + "customer.client_name,"
                   + "schedule.source_key AS pet_key,"              
                   + "petvaccination.resource_key,"
                   + "resource.name AS resource_name,"
                   + "DATE(schedule.start_stamp) AS vac_date,"
                   + "petvaccination.rabies_id,"
                   + "petvaccination.rabies_tag_number,"
                   + "petvaccination.vac_route_number,"
                   + "petvaccination.vac_name,"
                   + "petvaccination.vac_serial_number,"
                   + "petvaccination.vac_expiration_date,"
                   + "petvaccination.canine_distemper_id,"
                   + "petvaccination.corunna_id,"
                   + "petvaccination.feline_distemper_id,"
                   + "petvaccination.feline_leukemia_id,"
                   + "petvaccination.other_id,"
                   + "petvaccination.bundle_key,"
                   + "petvaccination.reminder_key,"
                   + "petvaccination.note_line1,"
                   + "petvaccination.create_user,"
                   + "petvaccination.create_stamp,"
                   + "petvaccination.update_user,"
                   + "petvaccination.update_stamp "
                   + "FROM customer, pet, petvaccination, resource, schedule "
                   + "WHERE petvaccination.thekey = ? "
                   + "AND petvaccination.schedule_key = schedule.thekey "
                   + "AND schedule.source_key = pet.thekey "
                   + "AND schedule.customer_key = customer.thekey "
                   + "AND petvaccination.resource_key = resource.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, pvKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {

        formDO = new PetVacDO();

        formDO.setKey(rs.getInt("thekey"));
        formDO.setScheduleKey(rs.getInt("schedule_key"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setPetKey(rs.getInt("pet_key"));
        formDO.setResourceKey(rs.getInt("resource_key"));
        formDO.setResourceName(rs.getString("resource_name"));
        formDO.setVacDate(rs.getDate("vac_date"));
        formDO.setRabiesId(rs.getByte("rabies_id"));
        formDO.setRabiesTagNumber(rs.getString("rabies_tag_number"));
        formDO.setVacRouteNumber(rs.getString("vac_route_number"));
        formDO.setVacName(rs.getString("vac_name"));
        formDO.setVacSerialNumber(rs.getString("vac_serial_number"));
        formDO.setVacExpirationDate(rs.getDate("vac_expiration_date"));
        formDO.setCanineDistemperId(rs.getByte("canine_distemper_id"));
        formDO.setCorunnaId(rs.getByte("corunna_id"));
        formDO.setFelineDistemperId(rs.getByte("feline_distemper_id"));
        formDO.setFelineLeukemiaId(rs.getByte("feline_leukemia_id"));
        formDO.setOtherId(rs.getByte("other_id"));
        formDO.setBundleKey(rs.getInt("bundle_key"));
        formDO.setReminderKey(rs.getInt("reminder_key"));
        formDO.setNoteLine1(rs.getString("note_line1"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

        HashMap nameMap = getAttributeToService().getAttributeToName(conn, new Byte("4"), formDO.getPetKey());
        formDO.setPetName(nameMap.get("attributeToName").toString());

//--------------------------------------------------------------------------------
// Look-up the bundle name if the other vaccination indicator is set.
//--------------------------------------------------------------------------------
        if ((formDO.getOtherId() > 0) && (formDO.getBundleKey() > 0)) {
          query = "SELECT name "
                + "FROM bundle "
                + "WHERE thekey = ?";
          ps = conn.prepareStatement(query);
          ps.setInt(1, formDO.getBundleKey());
          rs = ps.executeQuery();
          if ( rs.next() )
            formDO.setBundleName(rs.getString("name"));
          else
            throw new Exception("Bundle " + formDO.getBundleKey().toString() + " not found!");
        }
      }
      else {
        throw new Exception("Pet Vaccination " + pvKey.toString() + " not found!");
      }

//--------------------------------------------------------------------------------
// Retrieve the reminder date from schedule using the populated schedule_key.
//--------------------------------------------------------------------------------
      if (formDO.getReminderKey() > 0) {
        query = "SELECT DATE(start_stamp) AS reminder_date "
              + "FROM schedule "
              + "WHERE thekey = ? ";
        ps = conn.prepareStatement(query);
        ps.setInt(1, formDO.getReminderKey());
        rs = ps.executeQuery();
        if (rs.next())
          formDO.setReminderDate(rs.getDate("reminder_date"));
        else
          throw new Exception("Scheduled vaccination reminder not found!");
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
        PetVacDO formDO = buildPetVacForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("petvac", formDO);
        PetVacForm formStr = new PetVacForm();
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