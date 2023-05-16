/*
 * LicenseAssignAction.java
 *
 * Created on April 12, 2010  2:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.license;

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

public class LicenseAssignAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private void assignLicense(HttpServletRequest request,
                             Boolean assignId,
                             Byte invoiceTypeKey,
                             Integer invoiceKey,
                             Integer licenseKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "";
      if (assignId) {
        query = "UPDATE license SET "
              + "invoicetype_key=?,"
              + "invoice_key=? "
              + "WHERE thekey=? ";
        ps = conn.prepareStatement(query);
        ps.setByte(1, invoiceTypeKey);
        ps.setInt(2, invoiceKey);
        ps.setInt(3, licenseKey);
        ps.executeUpdate();
      } else {
        query = "UPDATE license SET "
              + "invoicetype_key=NULL,"
              + "invoice_key=NULL "
              + "WHERE thekey=? ";
        ps = conn.prepareStatement(query);
        ps.setInt(1, licenseKey);
        ps.executeUpdate();
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
        throw new Exception("Request getParameter/getAttribute [sourceKey] not found!");
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
// The invoice type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Byte invoiceTypeKey = null;
      if (request.getParameter("invoiceTypeKey") != null) {
        invoiceTypeKey = new Byte(request.getParameter("invoiceTypeKey").toString());
      }
      else {
        throw new Exception("Request getParameter [invoiceTypeKey] not found!");
      }

//--------------------------------------------------------------------------------
// The invoice key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Integer invoiceKey = null;
      if (request.getParameter("invoiceKey") != null) {
        invoiceKey = new Integer(request.getParameter("invoiceKey"));
      }
      else {
        throw new Exception("Request getParameter/getAttribute [invoiceKey] not found!");
      }

//--------------------------------------------------------------------------------
// The license primary key.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [key] not found!");
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());

      Boolean assignId = false;
      if (request.getParameter("assignId") != null) {
        assignId = new Boolean(request.getParameter("assignId").toString());
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        assignLicense(request,
                      assignId,
                      invoiceTypeKey,
                      invoiceKey,
                      theKey);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

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
