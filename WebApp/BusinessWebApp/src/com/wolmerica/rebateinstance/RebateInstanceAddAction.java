/*
 * RebateInstanceAddAction.java
 *
 * Created on May 29, 2006, 8:28 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.rebateinstance;

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


public class RebateInstanceAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private Integer insertRebateInstance(HttpServletRequest request,
                                       Integer rebateKey,
                                       Integer poiKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer riKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
//===========================================================================
// Get the maximum key from the rebate table.
//===========================================================================
      String query = "SELECT COUNT(*) AS ri_cnt, MAX(thekey)+1 AS ri_key "
                   + "FROM rebateinstance";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("ri_cnt") > 0 ) {
           riKey = rs.getInt("ri_key");
        }
      }
      else {
        throw new Exception("Rebate Instance Max() not found!");
      }

      query = "INSERT INTO rebateinstance "
            + "(thekey,rebate_key,purchaseorderitem_key,"
            + "tracking_url,note_line1,"
            + "eligible_id,submit_id,complete_id,"
            + "create_user,create_stamp,"
            + "update_user,update_stamp) "
            + "VALUES(?,?,?,?,?,?,?,?,"
            + "?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      cat.debug(this.getClass().getName() + ": query = " + query);
      ps = conn.prepareStatement(query);

      ps.setInt(1, riKey);
      ps.setInt(2, rebateKey);
      ps.setInt(3, poiKey);
      ps.setString(4, "");
      ps.setString(5, "");
      ps.setBoolean(6, false);
      ps.setBoolean(7, false);
      ps.setBoolean(8, false);
      ps.setString(9, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(10, request.getSession().getAttribute("USERNAME").toString());

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
    return riKey;
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
// Check for a rebate key being passed in.
//--------------------------------------------------------------------------------
      Integer rebateKey = null;
      if (!(request.getParameter("rebateKey") == null)) {
        rebateKey = new Integer(request.getParameter("rebateKey"));
      }
      else {
        throw new Exception("Request getParameter [rebateKey] not found!");
      }
//--------------------------------------------------------------------------------
// Check for a purchase order item key being passed in.
//--------------------------------------------------------------------------------
      Integer poiKey = null;
      if (!(request.getParameter("poiKey") == null)) {
        poiKey = new Integer(request.getParameter("poiKey"));
      }
      else {
        throw new Exception("Request getParameter [poiKey] not found!");
      }
//--------------------------------------------------------------------------------
// Check for an item dictionary key being passed in for future reference.
//--------------------------------------------------------------------------------
      if (request.getParameter("idKey") == null) {
        throw new Exception("Request getParameter [idKey] not found!");
      }
//--------------------------------------------------------------------------------
// Check for a purchase order key being passed in for future reference.
//--------------------------------------------------------------------------------
      if (request.getParameter("poKey") == null) {
        throw new Exception("Request getParameter [poKey] not found!");
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
      theKey = insertRebateInstance(request,
                                    rebateKey,
                                    poiKey);
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