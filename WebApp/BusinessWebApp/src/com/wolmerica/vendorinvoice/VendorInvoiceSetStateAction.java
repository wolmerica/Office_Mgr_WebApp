/*
 * VendorInvoiceSetStateAction.java
 *
 * Created on April 17, 2006, 9:42 PM
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
import java.sql.CallableStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class VendorInvoiceSetStateAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void completeVendorInvoiceCheckIn(HttpServletRequest request,
                                            Integer poKey,
                                            String userName)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      completeVendorInvoiceCheckInSP(conn, poKey, userName);

    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("completeVendorInvoiceCheckIn() " + e.getMessage());
    }
    finally {
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("completeVendorInvoiceCheckIn() " + e.getMessage());
        }
        conn = null;
      }
    }
  }

  private void completeVendorInvoiceCheckInSP(Connection conn,
                                              Integer poKey,
                                              String userName)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String spName = "";

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameters and zero OUT parameters.
//--------------------------------------------------------------------------------
      spName = "SetVendorAccountingRecord()";
      cStmt = conn.prepareCall("{call SetVendorAccountingRecord(?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      spName = "SetVendorAccountingRecord(cStmt)";
      cStmt.setInt(1, poKey);
      cStmt.setString(2,userName);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      spName = "SetVendorAccountingRecord(execute)";
      cStmt.execute();

//--------------------------------------------------------------------------------
// Call a procedure with one IN parameters and zero OUT parameters.
//--------------------------------------------------------------------------------
      spName = "SetVendorInvoiceCostBasis()";
      cStmt = conn.prepareCall("{call SetVendorInvoiceCostBasis(?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      spName = "SetVendorInvoiceCostBasis(cStmt)";
      cStmt.setInt(1, poKey);
      cStmt.setString(2,userName);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      spName = "SetVendorInvoiceCostBasis(execute)";
      cStmt.execute();

    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("completeVendorInvoiceCheckInSP(" + spName + ") " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("completeVendorInvoiceCheckInSP() " + e.getMessage());
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
      if (!(request.getParameter("poKey") == null)) {
        theKey = new Integer(request.getParameter("poKey"));
      }
      else {
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
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

//--------------------------------------------------------------------------------
// For complete and back orders we need to:
// 1) Post the vendor invoice total to the vendoraccounting table.
// 2) Set the first_cost value and calculate a new unit_cost value based
//    on first_cost, a ratio of the shipping charges, and vendor mark-up.
// 3) Change the active_id value in the vendor invoice from true to false.
//    This will put the vendor invoice into a view only state.
//--------------------------------------------------------------------------------
      completeVendorInvoiceCheckIn(request,
                                   theKey,
                                   request.getSession().getAttribute("USERNAME").toString());
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
