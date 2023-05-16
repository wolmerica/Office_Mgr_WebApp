/*
 * LicenseEditAction.java
 *
 * Created on April 9, 2010  1:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.license;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class LicenseEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private void updateLicense(HttpServletRequest request,
                               ActionForm form)
   throws Exception, IOException, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      LicenseListHeadDO formHDO = (LicenseListHeadDO) request.getSession().getAttribute("licenseHDO");

//--------------------------------------------------------------------------------
// Prepare a query to update the goods and services in work order table.
//--------------------------------------------------------------------------------
      String query = "UPDATE license SET "
                   + "license_user = ?,"
                   + "license_key = ?,"
                   + "note_line1 = ?,"
                   + "update_user = ?,"
                   + "update_stamp = CURRENT_TIMESTAMP "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Traverse the work order object.
//--------------------------------------------------------------------------------
      ArrayList logList = formHDO.getLicenseForm();
      LicenseDO formDO = null;

//--------------------------------------------------------------------------------
// Iterate through the rows in the license list.
//--------------------------------------------------------------------------------
      for (int j=0; j < logList.size(); j++) {
        formDO = (LicenseDO) logList.get(j);

        if (formDO.getKey() != null) {
//--------------------------------------------------------------------------------
// Perform the normal edit operations for license.
//--------------------------------------------------------------------------------
          cat.debug(this.getClass().getName() + ": update license: " + formDO.getLicenseUser() + " " + formDO.getLicenseKey());
          ps.setString(1, formDO.getLicenseUser());
          ps.setString(2, formDO.getLicenseKey());
          ps.setString(3, formDO.getNoteLine1());
          ps.setString(4, request.getSession().getAttribute("USERNAME").toString());
          ps.setInt(5, formDO.getKey());
          ps.executeUpdate();
        }
        else {
          if (!(formDO.getLicenseKey().equalsIgnoreCase(getPropertyService().getCustomerProperties(request,"license.default.key")))) {
            cat.debug(this.getClass().getName() + ": insert license: " + formDO.getLicenseUser() + " " + formDO.getLicenseKey());
            Integer newKey = insertLicense(conn,
                                           formDO.getSourceTypeKey(),
                                           formDO.getSourceKey(),
                                           formDO.getLicenseUser(),
                                           formDO.getLicenseKey(),
                                           formDO.getNoteLine1(),
                                           request.getSession().getAttribute("USERNAME").toString());
            cat.debug(this.getClass().getName() + ": insertLicense() returned " + newKey);
          }
        }
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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
  }

  private Integer insertLicense(Connection conn,
                                Byte sourceTypeKey,
                                Integer sourceKey,
                                String licenseUser,
                                String licenseKey,
                                String noteLine1,
                                String userName)
   throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer newKey = 1;

    try {
//--------------------------------------------------------------------------------
// Get the maximum key from the license table.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS license_cnt, MAX(thekey)+1 AS license_key "
                   + "FROM license";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("license_cnt") > 0 ) {
          newKey = rs.getInt("license_key");
        }
      }
      else {
        throw new Exception("License MAX() not found!");
      }

//--------------------------------------------------------------------------------
// Prepare an insert statement for the license table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO license "
            + "(thekey,"
            + "sourcetype_key,"
            + "source_key,"
            + "license_user,"
            + "license_key,"
            + "note_line1,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,?,?,"
            + "?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);

      ps.setInt(1, newKey);
      ps.setByte(2, sourceTypeKey);
      ps.setInt(3, sourceKey);
      ps.setString(4, licenseUser);
      ps.setString(5, licenseKey);
      ps.setString(6, noteLine1);
      ps.setString(7, userName);
      ps.setString(8, userName);
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
    }
    return newKey;
  }


    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws Exception, SQLException {

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
// The source type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey").toString());
      }
      else {
        throw new Exception("Request getParameter [sourceTypeKey] not found!");
      }

//--------------------------------------------------------------------------------
// The source key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        sourceKey = new Integer(request.getParameter("sourceKey"));
      }
      else {
        throw new Exception("Request getParameter [sourceKey] not found!");
      }

//--------------------------------------------------------------------------------
// The source name for which the license belong too.
//--------------------------------------------------------------------------------
      String sourceName = null;
      if (request.getParameter("sourceName") != null) {
        sourceName = new String(request.getParameter("sourceName"));
      }
      else {
        throw new Exception("Request getParameter [sourceName] not found!");
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              sourceKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      updateLicense(request,form);

      request.setAttribute("sourceTypeKey", sourceTypeKey.toString());
      request.setAttribute("sourceKey", sourceKey.toString());
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