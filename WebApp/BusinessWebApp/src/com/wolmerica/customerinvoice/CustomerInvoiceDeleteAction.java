/*
 * CustomerInvoiceDeleteAction.java
 *
 * Created on October 23, 2005, 6:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoice;

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

public class CustomerInvoiceDeleteAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void deleteCustomerInvoice(HttpServletRequest request,
                                       Integer cKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// Release the stockitem records that were being held by this order.
// 04/20/2006 - Add sourcetype_key to stockitem to handle vendor and cust inv.
//--------------------------------------------------------------------------------
      String query = "UPDATE stockitem SET "
                   + "active_id=?,"
                   + "source_key=null, "
                   + "update_user=?, "
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE sourcetype_key IN (SELECT thekey "
                                            + "FROM accountingtype "
                                            + "WHERE name = 'SOURCE' "
                                            + "AND description = 'Customer Invoice') "
                   + "AND source_key=?";
      ps = conn.prepareStatement(query);
      ps.setBoolean(1, true);
      ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
      ps.setInt(3, cKey);
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// Update the license allocation if necessary.
//--------------------------------------------------------------------------------
      query = "UPDATE license SET "
            + "invoicetype_key = NULL,"
            + "invoice_key = NULL "
            + "WHERE invoicetype_key = 14 "
            + "AND invoice_key IN (SELECT thekey "
                                + "FROM customerinvoiceitem "
                                + "WHERE customerinvoice_key=?)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, cKey);
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// We first must delete any Customer Invoice Item records if they exist
//--------------------------------------------------------------------------------
      query = "DELETE FROM customerinvoiceitem "
            + "WHERE customerinvoice_key=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, cKey);
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// We first must delete any Customer Invoice Service records if they exist
//--------------------------------------------------------------------------------
      query = "DELETE FROM customerinvoiceservice "
            + "WHERE customerinvoice_key=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, cKey);
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// Preserve the invoice key with the appointment it is associated with.
//--------------------------------------------------------------------------------
      query = "UPDATE schedule "
            + "SET customerinvoice_key = null "
            + "WHERE customerinvoice_key = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, cKey.toString());
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// Delete the logistics for the customer invoice.
//--------------------------------------------------------------------------------
      query = "DELETE FROM logistics "
            + "WHERE sourcetype_key = 14 "
            + "AND source_key=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, cKey);
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// Now we can delete the actual Customer Invoice
//--------------------------------------------------------------------------------
      query = "DELETE FROM customerinvoice "
               + "where thekey = ?";

      ps = conn.prepareStatement(query);
      ps.setInt(1, cKey);
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
      Integer ciKey = null;
      if (!(request.getParameter("ciKey") == null)) {
        ciKey = new Integer(request.getParameter("ciKey"));
      }
      else {
        if (!(request.getAttribute("ciKey") == null)) {
          ciKey = new Integer(request.getAttribute("ciKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [ciKey] not found!");
        }
      }
      request.setAttribute("ciKey", ciKey.toString());

      // Vendor Invoice
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
          request.setAttribute("key", theKey.toString());
        }
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              ciKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        deleteCustomerInvoice(request, ciKey);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());
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
