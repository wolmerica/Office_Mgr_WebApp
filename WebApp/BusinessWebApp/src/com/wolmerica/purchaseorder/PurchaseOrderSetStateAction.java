/*
 * PurchaseOrderSetStateAction.java
 *
 * Created on September 10, 2005, 2:32 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 04/03/2006 - SUM() the handling charges for a P.O. with multiple vendor invoices.
 *              This came to me while drive to Thomson Gale in the morning to work.
 */

package com.wolmerica.purchaseorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.util.common.EnumPurchaseOrderStatus;
import com.zoasis.service.zoasislab.ZoasisLabService;
import com.zoasis.service.zoasislab.DefaultZoasisLabService;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class PurchaseOrderSetStateAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

 private UserStateService userStateService = new DefaultUserStateService();
 private ZoasisLabService zoasisLabService = new DefaultZoasisLabService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public ZoasisLabService getZoasisLabService() {
      return zoasisLabService;
  }

  public void setZoasisLabService(ZoasisLabService zoasisLabService) {
      this.zoasisLabService = zoasisLabService;
  }

  
  private String setPurchaseOrderState(HttpServletRequest request,
                                       Integer poKey,
                                       Integer poState)
    throws Exception, SQLException {

    String orderStatus = "success";
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "UPDATE purchaseorder "
                   + "SET order_status=?,"
                   + "update_user=?,"
                   + "update_stamp = CURRENT_TIMESTAMP" + " "
                   + "WHERE thekey=?";

      String nextState = null;
      switch(poState)
      {
         // Change status to "Ordered".
         case 1: nextState = EnumPurchaseOrderStatus.O.getValue();           
                 if (getZoasisLabService().isAntechLabOrder(request, conn, poKey)) {
                   nextState = EnumPurchaseOrderStatus.N.getValue();
                   orderStatus = getZoasisLabService().createAntechLabOrder(request, conn, poKey);
                   cat.debug(this.getClass().getName() + ": createAntechLabOrder(): " + orderStatus);
                 }
                 if (orderStatus.contains("success")) {
                   if (getZoasisLabService().isTrackResultOrder(conn, poKey)) {
                     int trackCount = insertVendorResult(request, conn, poKey);
                     cat.debug(this.getClass().getName() + ": insertVendorResult(): " + trackCount);
                   }
                   nextState = EnumPurchaseOrderStatus.O.getValue();
                   query = "UPDATE purchaseorder "
                         + "SET order_status=?,"
                         + "submit_order_stamp=CURRENT_TIMESTAMP,"
                         + "update_user=?,"
                         + "update_stamp = CURRENT_TIMESTAMP" + " "
                         + "WHERE thekey=?";
                 }
                 break;
         // Change status to "Check-In".
         case 2: nextState = EnumPurchaseOrderStatus.I.getValue();
                 break;
         // Change status to "Back-Ordered".
         case 3: nextState = EnumPurchaseOrderStatus.B.getValue();
                 break;
         // Change status to "Complete".
         case 4: nextState = EnumPurchaseOrderStatus.C.getValue();
                 break;
      }
      ps = conn.prepareStatement(query);
      ps.setString(1, nextState);
      ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
      ps.setInt(3, poKey);
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
    return orderStatus;
  }


  private Integer insertVendorResult(HttpServletRequest request,
                                     Connection conn,
                                     Integer poKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer resultCount = 0;

    try {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call SetVendorResultAdd(?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, poKey);
      cStmt.setString(2, request.getSession().getAttribute("USERNAME").toString());
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": resultCount : " + cStmt.getInt("resultCount"));
      resultCount = cStmt.getInt("resultCount");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("insertVendorResult() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("insertVendorResult() " + e.getMessage());
      }
    }
    return resultCount;
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
      // Vendor Invoice and Vendor Invoice Item add requires
      // the poKey to perform their operations.
      request.setAttribute("poKey", theKey.toString());

      Integer theState = null;
      if (!(request.getParameter("poState") == null)) {
        theState = new Integer(request.getParameter("poState"));
      }
      else {
        if (!(request.getAttribute("poState") == null)) {
          theState = new Integer(request.getAttribute("poState").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [poState] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[poState] = " + theState.toString());
      request.setAttribute("poState", theState.toString());

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

      String orderStatus = setPurchaseOrderState(request,
                                                 theKey,
                                                 theState);
      if (!(orderStatus.equalsIgnoreCase("success"))) {
        request.setAttribute("popupMessage", orderStatus);
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
