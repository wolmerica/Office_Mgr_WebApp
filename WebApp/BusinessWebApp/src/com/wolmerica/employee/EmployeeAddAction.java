/*
 * EmployeeAddAction.java
 *
 * Created on August 15, 2005, 8:45 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.employee;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
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

public class EmployeeAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer insertEmployee(HttpServletRequest request, ActionForm form)
    throws Exception, SQLException {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer eKey = 1;

    try {
      EmployeeDO formDO = (EmployeeDO) request.getSession().getAttribute("employee");

      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// Check the employee table for any duplicate user name values.
//--------------------------------------------------------------------------------
      String query = "SELECT user_name "
                   + "FROM employee "
                   + "WHERE UPPER(user_name) = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, formDO.getUserName().toUpperCase());
      rs = ps.executeQuery();

      if (!(rs.next())) {
//--------------------------------------------------------------------------------
// Get the maximum key from the customer.
//--------------------------------------------------------------------------------
        query = "SELECT COUNT(*) AS e_cnt, MAX(thekey)+1 AS e_key "
              + "FROM employee";
        ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the customer table.
//--------------------------------------------------------------------------------
        rs = ps.executeQuery();
        if ( rs.next() ) {
          if ( rs.getInt("e_cnt") > 0 ) {
            eKey = rs.getInt("e_key");
          }
        }
        else {
          throw new Exception("Employee MAX() not found!");
        }

//--------------------------------------------------------------------------------
// Prepare an insert statement for the employee table.
//--------------------------------------------------------------------------------
        query = "INSERT INTO employee (thekey,"
              + "user_name,"
              + "password,"
              + "admin_id,"
              + "first_name,"
              + "last_name,"
              + "address,"
              + "address2,"
              + "city,"
              + "state,"
              + "zip,"
              + "phone,"
              + "phone_num,"
              + "phone_ext,"
              + "mobile_num,"              
              + "email,"
              + "email2,"
              + "yim_id,"
              + "permission_slip,"
              + "create_user,"
              + "create_stamp,"
              + "update_user,"
              + "update_stamp )"
              + "VALUES (?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,?,?,?,?,?,?,?," 
              + "CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
        ps = conn.prepareStatement(query);
        ps.setInt(1, eKey);
        ps.setString(2, formDO.getUserName());
        ps.setString(3, formDO.getPassword());
        ps.setBoolean(4, formDO.getAdminId());
        ps.setString(5, formDO.getFirstName());
        ps.setString(6, formDO.getLastName());
        ps.setString(7, formDO.getAddress());
        ps.setString(8, formDO.getAddress2());
        ps.setString(9, formDO.getCity());
        ps.setString(10, formDO.getState());
        ps.setString(11, formDO.getZip());
        ps.setString(12, formDO.getPhone());
        ps.setString(13, formDO.getPhoneNum());
        ps.setString(14, formDO.getPhoneExt());
        ps.setString(15, formDO.getMobileNum());        
        ps.setString(16, formDO.getEmail());
        ps.setString(17, formDO.getEmail2());        
        ps.setString(18, formDO.getYimId());
        ps.setString(19, formDO.getPermissionSlip());
        ps.setString(20, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(21, request.getSession().getAttribute("USERNAME").toString());

        ps.executeUpdate();
      }
      else {
//--------------------------------------------------------------------------------
// The employee user name you entered already exists.
//--------------------------------------------------------------------------------
        eKey = null;
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
    return eKey;
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
      theKey = insertEmployee(request, form);
//--------------------------------------------------------------------------------
// A employee key of null indicates a duplicate in the user name field.
//--------------------------------------------------------------------------------
      if (theKey == null) {
        target = "error";
        ActionMessages errors = new ActionMessages();
        errors.add("userName", new ActionMessage("errors.duplicate"));
        saveErrors(request, errors);
      }
      else
      {
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