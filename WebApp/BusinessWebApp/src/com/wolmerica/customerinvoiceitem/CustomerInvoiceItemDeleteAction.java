/*
 * CustomerInvoiceItemDeleteAction.java
 *
 * Created on December 06, 2005, 12:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoiceitem;

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

public class CustomerInvoiceItemDeleteAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void deleteCustomerInvoiceItem(HttpServletRequest request,
                                         Integer ciiKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer idKey = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//===========================================================================
// Retrieve the item dictionary key from the customer invoice item.
//===========================================================================
      String query = "SELECT itemdictionary_key "
                   + "FROM customerinvoiceitem "
                   + "WHERE thekey = ?";
      cat.debug(this.getClass().getName() + ": Query #1 = " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciiKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        idKey = rs.getInt("itemdictionary_key");
      }
      else {
        throw new Exception("CustomerInvoiceItem  " + ciiKey.toString() + " not found!");
      }
//===========================================================================
// Lock stockitem records for this item until this order is complete.
// 04/20/2006 - Add sourcetype_key to stockitem to handle vendor and cust inv.
//===========================================================================
      query = "UPDATE stockitem SET "
            + "active_id = ?, "
            + "source_key = null, "
            + "update_user = ?, "
            + "update_stamp=CURRENT_TIMESTAMP "
            + "WHERE itemdictionary_key = ? "
            + "AND sourcetype_key IN (SELECT thekey "
                                   + "FROM accountingtype "
                                   + "WHERE name = 'SOURCE' "
                                   + "AND description = 'Customer Invoice')";
      ps = conn.prepareStatement(query);
      ps.setBoolean(1, true);
      ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
      ps.setInt(3, idKey);
      ps.executeUpdate();
//===========================================================================
// Update the license allocation if necessary.
//===========================================================================
      query = "UPDATE license SET "
            + "invoicetype_key = NULL,"
            + "invoice_key = NULL "
            + "WHERE invoicetype_key = 14 "
            + "AND invoice_key = ? ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciiKey);
      ps.executeUpdate();
//===========================================================================
// Remove the specified customer invoice item from the order.
//===========================================================================
      query = "DELETE FROM customerinvoiceitem "
            + "WHERE thekey = ?";

      ps = conn.prepareStatement(query);
      ps.setInt(1, ciiKey);
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

      Integer ciiKey = null;
      if (!(request.getParameter("ciiKey") == null)) {
        ciiKey = new Integer(request.getParameter("ciiKey"));
      }
      else {
        if (!(request.getAttribute("ciiKey") == null)) {
          ciiKey = new Integer(request.getAttribute("ciiKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [ciiKey] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[ciiKey] = " + ciiKey.toString());

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        deleteCustomerInvoiceItem(request,
                                  ciiKey);
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