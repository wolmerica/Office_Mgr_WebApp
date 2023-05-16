/*
 * WorkOrderInitiatePurchaseOrderAction.java
 *
 * Created on October 5, 2008 2:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.workorder;

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

public class WorkOrderInitiatePurchaseOrderAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer getThirdPartyVendor(HttpServletRequest request,
                                      Integer sKey,
                                      Integer vKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Retreive a work order row associated with the appointment.
//--------------------------------------------------------------------------------
      String query = "SELECT DISTINCT vendor_key "
                   + "FROM workorder "
                   + "WHERE schedule_key = ? "
                   + "AND vendor_key > ? "
                   + "AND thirdparty_id "
                   + "ORDER BY vendor_key";
      cat.debug(this.getClass().getName() + ": query  : " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, sKey);
      ps.setInt(2, vKey);
      rs = ps.executeQuery();
      if (rs.next())
          vKey = rs.getInt("vendor_key");
      else
          vKey = null;
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
    return vKey;
  }
  
    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {
//--------------------------------------------------------------------------------
// Default target to success
//--------------------------------------------------------------------------------
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
// Schedule key from which the purchase order was intiated.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key").toString());
        request.getSession().setAttribute("SKEY", theKey.toString());
      }
      else {
        if (request.getSession().getAttribute("SKEY") != null) {
          theKey = new Integer(request.getSession().getAttribute("SKEY").toString());
        }
        else {
          throw new Exception("Request getParameter [key] not found!");
        }
      }
      request.setAttribute("key", theKey);

//--------------------------------------------------------------------------------
// Vendor key value to allow the creation of multiple purchase orders.
//--------------------------------------------------------------------------------
      Integer vendorKey = 0;
      if (request.getSession().getAttribute("VKEY") != null) {
        vendorKey = new Integer(request.getSession().getAttribute("VKEY").toString());
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

      cat.debug(this.getClass().getName() + ": sKey : " + theKey.toString());
      cat.debug(this.getClass().getName() + ": vKey : " + vendorKey.toString());      

//--------------------------------------------------------------------------------
// Retrieve the next third party vendor that requires a purchase order.
//--------------------------------------------------------------------------------
      vendorKey = getThirdPartyVendor(request,
                                      theKey,
                                      vendorKey);

      if (vendorKey != null) {
        target = new String("add");
        request.getSession().setAttribute("VKEY", vendorKey.toString());
        request.getSession().setAttribute("WOCNT", "0");        
      } 
      else {
        request.getSession().setAttribute("VKEY", null);
        request.getSession().setAttribute("SKEY", null);        
        request.getSession().setAttribute("WOCNT", null);        
      }
      cat.debug(this.getClass().getName() + ": target " + target + " " + vendorKey);
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