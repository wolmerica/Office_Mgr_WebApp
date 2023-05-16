/*
 * BreedAddAction.java
 *
 * Created on March 11, 2010, 4:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.breed;

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

public class BreedAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");


  private UserStateService userStateService = new DefaultUserStateService();


  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer insertBreed(HttpServletRequest request,
                              ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer brdKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      BreedDO formDO = (BreedDO) request.getSession().getAttribute("breed");

//--------------------------------------------------------------------------------
// Check for existing breed name or ext_id values.
//--------------------------------------------------------------------------------
      String query = "SELECT breed_name,"
                   + "breed_ext_id "
                   + "FROM breed "
                   + "WHERE UPPER(breed_name) = ? "
                   + "OR UPPER(breed_ext_id) = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, formDO.getBreedName().toUpperCase());
	  ps.setString(2, formDO.getBreedExtId().toUpperCase());
      rs = ps.executeQuery();
      if (!(rs.next())) {
//--------------------------------------------------------------------------------
// Get the maximum key from the breed key.
//--------------------------------------------------------------------------------
        query = "SELECT COUNT(*) AS brd_cnt, MAX(thekey)+1 AS brd_key "
              + "FROM breed";
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        if ( rs.next() ) {
          if ( rs.getInt("brd_cnt") > 0 ) {
            brdKey = rs.getInt("brd_key");
          }
        }
        else {
          throw new Exception("Breed MAX() not found!");
        }

//===========================================================================
// Prepare a SQL query to insert a row into the breed table.
//===========================================================================
        query = "INSERT INTO breed "
              + "(thekey,"
              + "species_key,"
              + "breed_name,"
              + "breed_ext_id,"
              + "create_user,"
              + "create_stamp,"
              + "update_user,"
              + "update_stamp) "
              + "VALUES(?,?,?,?,"
              + "?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
        ps = conn.prepareStatement(query);
        ps.setInt(1, brdKey);
        ps.setInt(2, formDO.getSpeciesKey());
        ps.setString(3, formDO.getBreedName());
        ps.setString(4, formDO.getBreedExtId().toUpperCase());
        ps.setString(5, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(6, request.getSession().getAttribute("USERNAME").toString());
        ps.executeUpdate();
      } else {
        if (rs.getString("breed_name").equalsIgnoreCase(formDO.getBreedName())) {
          brdKey = new Integer("-1");
        } else {
          if (rs.getString("breed_ext_id").equalsIgnoreCase(formDO.getBreedExtId())) {
            brdKey = new Integer("-2");
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
    return brdKey;
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
      theKey = insertBreed(request, form);

      if (theKey < 1) {
        target = "error";
        ActionMessages errors = new ActionMessages();
        if (theKey == -1) {
          errors.add("breedName", new ActionMessage("errors.duplicate"));
        }
        if (theKey == -2) {
          errors.add("breedExtId", new ActionMessage("errors.duplicate"));
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