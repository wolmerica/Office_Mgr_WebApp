/*
 * PetVacAddAction.java
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

import org.apache.log4j.Logger;


public class PetVacAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private Integer insertPetVac(HttpServletRequest request,
                            ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer pvKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      PetVacDO formDO = (PetVacDO) request.getSession().getAttribute("petvac");

//--------------------------------------------------------------------------------
// Get the maximum key from the pet vaccination key.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS petvac_cnt, MAX(thekey)+1 AS petvac_key "
                   + "FROM petvaccination";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("petvac_cnt") > 0 ) {
          pvKey = rs.getInt("petvac_key");
        }
      }
      else {
        throw new Exception("Pet Vac MAX() not found!");
      }

//===========================================================================
// Prepare a SQL query to insert a row into the pet table.
//===========================================================================
      query = "INSERT INTO petvaccination "
            + "(thekey,"
            + "schedule_key,"
            + "resource_key,"
            + "rabies_id,"
            + "rabies_tag_number,"
            + "vac_route_number,"
            + "vac_name,"
            + "vac_serial_number,"
            + "vac_expiration_date,"
            + "canine_distemper_id,"
            + "corunna_id,"
            + "feline_distemper_id,"
            + "feline_leukemia_id,"
            + "other_id,"
            + "bundle_key,"
            + "note_line1,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,NULL,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, pvKey);
      ps.setInt(2, formDO.getScheduleKey());
      ps.setInt(3, formDO.getResourceKey());
      ps.setByte(4, formDO.getRabiesId());
      ps.setString(5, formDO.getRabiesTagNumber());
      ps.setString(6, formDO.getVacRouteNumber());
      ps.setString(7, formDO.getVacName());
      ps.setString(8, formDO.getVacSerialNumber());
      ps.setDate(9, new java.sql.Date(formDO.getVacExpirationDate().getTime()));
      ps.setByte(10, formDO.getCanineDistemperId());
      ps.setByte(11, formDO.getCorunnaId());
      ps.setByte(12, formDO.getFelineDistemperId());
      ps.setByte(13, formDO.getFelineLeukemiaId());
      ps.setByte(14, formDO.getOtherId());
      ps.setString(15, formDO.getNoteLine1());
      ps.setString(16, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(17, request.getSession().getAttribute("USERNAME").toString());
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
    return pvKey;
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
      theKey = insertPetVac(request, form);
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