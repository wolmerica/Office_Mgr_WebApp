/*
 * SystemAddAction.java
 *
 * Created on August 26, 2005, 11:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.system;

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


public class SystemAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer insertSystem(HttpServletRequest request,
                              ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer sysKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      SystemDO formDO = (SystemDO) request.getSession().getAttribute("system");

//--------------------------------------------------------------------------------
// Get the maximum key from the system key.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS sys_cnt, MAX(thekey)+1 AS sys_key "
                   + "FROM system";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("sys_cnt") > 0 ) {
          sysKey = rs.getInt("sys_key");
        }
      }
      else {
        throw new Exception("System MAX() not found!");
      }

//===========================================================================
// Prepare a SQL query to insert a row into the system table.
//===========================================================================
      query = "INSERT INTO system "
                   + "(thekey,"
                   + "customer_key,"
                   + "name,"
                   + "processor,"
                   + "operating_system,"
                   + "memory,"
                   + "primary_drive,"
                   + "auxilary_drive,"
                   + "software_package,"
                   + "system_date,"
                   + "mac_address,"
                   + "mac_address2,"                   
                   + "serial_number,"
                   + "note_line1,"
                   + "note_line2,"
                   + "note_line3,"                   
                   + "active_id,"
                   + "create_user,create_stamp,"
                   + "update_user,update_stamp )"
                   + "values (?,?,?,?,?,?,?,?,?,?,"
                   + "?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, sysKey);
      ps.setInt(2, formDO.getCustomerKey());
      ps.setString(3, formDO.getMakeModel());
      ps.setString(4, formDO.getProcessor());
      ps.setString(5, formDO.getOperatingSystem());
      ps.setString(6, formDO.getMemory());
      ps.setString(7, formDO.getPrimaryDrive());
      ps.setString(8, formDO.getAuxilaryDrive());
      ps.setString(9, formDO.getSoftwarePackage());
      ps.setDate(10, new java.sql.Date(formDO.getSystemDate().getTime()));
      ps.setString(11, formDO.getMacAddress());
      ps.setString(12, formDO.getMacAddress2());      
      ps.setString(13, formDO.getSerialNumber());
      ps.setString(14, formDO.getNoteLine1());
      ps.setString(15, formDO.getNoteLine2());
      ps.setString(16, formDO.getNoteLine3());
      ps.setBoolean(17, formDO.getActiveId());
      ps.setString(18, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(19, request.getSession().getAttribute("USERNAME").toString());
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
    return sysKey;
  }

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
      theKey = insertSystem(request, form);
      request.setAttribute("key", theKey.toString());
      request.setAttribute("sourceTypeKey", getUserStateService().getFeatureKey().toString());
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