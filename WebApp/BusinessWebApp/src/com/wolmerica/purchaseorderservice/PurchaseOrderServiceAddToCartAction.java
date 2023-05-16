/*
 * PurchaseOrderServiceAddToCart.java
 *
 * Created on August 13, 2008, 09:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.purchaseorderservice;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class PurchaseOrderServiceAddToCartAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private Integer insertPurchaseOrderService(HttpServletRequest request,
                                             Integer poKey,
                                             Integer pbsKey,
                                             Short orderQty)
    throws Exception, SQLException {
      
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    Byte sourceTypeKey = 6;
    Byte ptKey = null;
    Integer sdKey = null;
    Integer posKey = 1;    
    
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
      
//--------------------------------------------------------------------------------
// Get service key and price type key from the price by service value.
//--------------------------------------------------------------------------------      
      CallableStatement cStmt = null;
      cStmt = conn.prepareCall("{call GetItemOrServiceKey(?,?,?,?,?,?,?)}");
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, pbsKey);
      cStmt.execute();
      sdKey = cStmt.getInt("sourceKey");      
      ptKey = cStmt.getByte("priceTypeKey");
      
//--------------------------------------------------------------------------------
// Call a stored procedure to add a new purchase order service record.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call SetPurchaseOrderServiceAdd(?,?,?,?,?,?)}");
      cStmt.setInt(1, poKey);
      cStmt.setInt(2, sdKey);
      cStmt.setByte(3, ptKey);
      cStmt.setShort(4, orderQty);
      cStmt.setString(5, request.getSession().getAttribute("USERNAME").toString());
      cStmt.execute();
      posKey = cStmt.getInt("posKey");     
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("insertPurchaseOrderService() " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("insertPurchaseOrderService() " + e.getMessage());
        }
        rs = null;
      }        
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("insertPurchaseOrderService() " + e.getMessage());
        }
        ps = null;
      }
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("insertPurchaseOrderService() " + e.getMessage());
        }
        conn = null;
      }
    }
    return posKey;
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
// Get the purchase order key for the order.
//--------------------------------------------------------------------------------        
      Integer theKey = null;
      if (request.getAttribute("key") != null) {
        theKey = new Integer(request.getAttribute("key").toString());
      }
      else {
        if (request.getParameter("key") != null) {
          theKey = new Integer(request.getParameter("key"));
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

//--------------------------------------------------------------------------------
// Get the price by service key for the service.
//--------------------------------------------------------------------------------      
      Integer pbsKey = null;
      if (request.getParameter("pbsKey") != null) {
        pbsKey = new Integer(request.getParameter("pbsKey"));
      }
      else {
        if (request.getAttribute("pbsKey") != null) {
          pbsKey = new Integer(request.getAttribute("pbsKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [pbsKey] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[pbsKey] = " + pbsKey.toString());
      request.setAttribute("pbsKey", pbsKey.toString());

//--------------------------------------------------------------------------------
// Get the quantity to be ordered.
//--------------------------------------------------------------------------------      
      Short orderQty = 1;
      if (request.getParameter("orderQty") != null) {
        orderQty = new Integer(request.getParameter("orderQty")).shortValue();
      }
      else {
        if (request.getAttribute("orderQty") != null) {
          orderQty = new Integer(request.getAttribute("orderQty").toString()).shortValue();
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
    
      Integer posKey = insertPurchaseOrderService(request,
                                                  theKey,
                                                  pbsKey,
                                                  orderQty);
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