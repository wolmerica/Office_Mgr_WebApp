/*
 * VendorInvoiceDeleteAction.java
 *
 * Created on October 23, 2005, 6:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoice;

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

public class VendorInvoiceDeleteAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void deleteVendorInvoice(HttpServletRequest request,
                                     Integer viKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Release the stockitem records that were being held by this vendor invoice.
// 04/20/2006 - Add sourcetype_key to stockitem to handle vendor and cust inv.
//--------------------------------------------------------------------------------
      String query = "UPDATE stockitem SET "
                   + "active_id=?,"
                   + "source_key = null,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE sourcetype_key IN (SELECT thekey "
                                   + "FROM accountingtype "
                                   + "WHERE name = 'SOURCE' "
                                   + "AND description = 'Vendor Invoice') "
                   + "AND source_key=?";
      ps = conn.prepareStatement(query);
      ps.setBoolean(1, true);
      ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
      ps.setInt(3, viKey);
      ps.executeUpdate();
      
//--------------------------------------------------------------------------------
// We first must delete any vendor invoice items if they exist
//--------------------------------------------------------------------------------
      query = "DELETE FROM vendorinvoiceitem "
            + "WHERE vendorinvoice_key=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// We first must delete any vendor invoice services if they exist
//--------------------------------------------------------------------------------
      query = "DELETE FROM vendorinvoiceservice "
            + "WHERE vendorinvoice_key=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      ps.executeUpdate();
      
//--------------------------------------------------------------------------------
// Delete the logistics for the vendor invoice.
//--------------------------------------------------------------------------------  
      query = "DELETE FROM logistics "
            + "WHERE sourcetype_key = 13 "
            + "AND source_key=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      ps.executeUpdate();      

//--------------------------------------------------------------------------------
// Now we can delete the actual order
//--------------------------------------------------------------------------------      *
      query = "DELETE FROM vendorinvoice "
            + "where thekey=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
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
//--------------------------------------------------------------------------------
// We do not preserve the vendor invoice key for the next Action call.
// Instead we store the poKey since the purchase order get is called.
//--------------------------------------------------------------------------------
      Integer poKey = null;
      if (!(request.getParameter("poKey") == null)) {
        poKey = new Integer(request.getParameter("poKey"));
      }
      else {
        if (!(request.getAttribute("poKey") == null)) {
          poKey = new Integer(request.getAttribute("poKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [poKey] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[poKey] = " + poKey.toString());
      request.setAttribute("key", poKey.toString());      

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        deleteVendorInvoice(request, theKey);
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
