/*
 * SpeciesAddAction.java
 *
 * Created on March 11, 2010, 4:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.species;

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

public class SpeciesAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer insertSpecies(HttpServletRequest request,
                                ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer spcKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      SpeciesDO formDO = (SpeciesDO) request.getSession().getAttribute("species");

//--------------------------------------------------------------------------------
// Check for existing species name or ext_id values.
//--------------------------------------------------------------------------------
      String query = "SELECT species_name,"
                   + "species_ext_id "
                   + "FROM species "
                   + "WHERE UPPER(species_name) = ? "
                   + "OR UPPER(species_ext_id) = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, formDO.getSpeciesName().toUpperCase());
	  ps.setString(2, formDO.getSpeciesExtId().toUpperCase());
      rs = ps.executeQuery();
      if (!(rs.next())) {
//--------------------------------------------------------------------------------
// Get the maximum key from the Species key.
//--------------------------------------------------------------------------------
        query = "SELECT COUNT(*) AS spc_cnt, MAX(thekey)+1 AS spc_key "
              + "FROM species";
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        if ( rs.next() ) {
          if ( rs.getInt("spc_cnt") > 0 ) {
            spcKey = rs.getInt("spc_key");
          }
        }
        else {
          throw new Exception("Species MAX() not found!");
        }

//===========================================================================
// Prepare a SQL query to insert a row into the Species table.
//===========================================================================
        query = "INSERT INTO species "
              + "(thekey,"
              + "species_name,"
              + "species_ext_id,"
              + "create_user,"
              + "create_stamp,"
              + "update_user,"
              + "update_stamp) "
              + "VALUES(?,?,?,"
              + "?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
        ps = conn.prepareStatement(query);
        ps.setInt(1, spcKey);
        ps.setString(2, formDO.getSpeciesName());
        ps.setString(3, formDO.getSpeciesExtId().toUpperCase());
        ps.setString(4, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(5, request.getSession().getAttribute("USERNAME").toString());
        ps.executeUpdate();
      } else {
        if (rs.getString("species_name").equalsIgnoreCase(formDO.getSpeciesName())) {
          spcKey = new Integer("-1");
        } else {
          if (rs.getString("species_ext_id").equalsIgnoreCase(formDO.getSpeciesExtId())) {
            spcKey = new Integer("-2");
          }
        }
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
    return spcKey;
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
      theKey = insertSpecies(request, form);

      if (theKey < 1) {
        target = "error";
        ActionMessages errors = new ActionMessages();
        if (theKey == -1) {
          errors.add("speciesName", new ActionMessage("errors.duplicate"));
        }
        if (theKey == -2) {
          errors.add("speciesExtId", new ActionMessage("errors.duplicate"));
        }
        saveErrors(request, errors);
      }
      else {
        request.setAttribute("key", theKey.toString());
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