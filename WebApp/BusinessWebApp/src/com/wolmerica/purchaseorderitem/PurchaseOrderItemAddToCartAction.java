/*
 * PurchaseOrderItemAddToCart.java
 *
 * Created on September 11, 2005, 1:09 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.purchaseorderitem;

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

public class PurchaseOrderItemAddToCartAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private Integer insertPurchaseOrderItem(HttpServletRequest request,
                                          Integer poKey,
                                          Integer idKey,
                                          short orderQty)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    Integer poiKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Get the maximum key from the purchase order item table.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS poi_cnt, MAX(thekey)+1 AS poi_key "
                   + "FROM purchaseorderitem ";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the purchase order item.
//--------------------------------------------------------------------------------
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("poi_cnt") > 0 ) {
          poiKey = rs.getInt("poi_key");
        }
      }
      else {
        throw new Exception("PurchaseOrderItem  " + poiKey.toString() + " not found!");
      }      
      
//--------------------------------------------------------------------------------
// Prepare a query to insert a new row into the purchaseorderservice table.
//--------------------------------------------------------------------------------      
      query = "INSERT INTO purchaseorderitem "
            + "(thekey,"
            + "purchaseorder_key,"
            + "itemdictionary_key,"
            + "order_qty,"
            + "create_user, create_stamp,"
            + "update_user, update_stamp) "
            + "VALUES (?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);      
      ps.setInt(1, poiKey);
      ps.setInt(2, poKey);
      ps.setInt(3, idKey);
      ps.setShort(4, orderQty);
      ps.setString(5, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(6, request.getSession().getAttribute("USERNAME").toString());
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
    return poiKey;
  }
  
  private Integer getItemKey(HttpServletRequest request,
                             Integer pbiKey)
    throws Exception, SQLException {
      
    DataSource ds = null;
    Connection conn = null;
    
    Byte sourceTypeKey = 3;
    Byte ptKey = null;
    Integer idKey = null;
    
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
      
//--------------------------------------------------------------------------------
// Get service key and price type key from the price by service value.
//--------------------------------------------------------------------------------      
      CallableStatement cStmt = null;
      cStmt = conn.prepareCall("{call GetItemOrServiceKey(?,?,?,?,?,?,?)}");
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, pbiKey);
      cStmt.execute();
      idKey = cStmt.getInt("sourceKey");      
      ptKey = cStmt.getByte("priceTypeKey");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemKey() " + e.getMessage());
    }
    finally {
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException e) {
            cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
            throw new Exception("getItemKey() " + e.getMessage());
        }
        conn = null;
      }
    }
    return idKey;
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
// Get item dictionary key for the item.
//--------------------------------------------------------------------------------      
      Integer idKey = null;
      Integer pbiKey = null;
      if (request.getParameter("idKey") != null) {
        idKey = new Integer(request.getParameter("idKey"));
      }
      else {
        if (request.getAttribute("idKey") != null) {
          idKey = new Integer(request.getAttribute("idKey").toString());
        }
        else {
          if (request.getParameter("pbiKey") != null) {
            pbiKey = new Integer(request.getParameter("pbiKey"));
          }
          else {
            if (request.getAttribute("pbiKey") != null) {
              pbiKey = new Integer(request.getAttribute("pbiKey").toString());
            }
            else {
              throw new Exception("Request getParameter/getAttribute [idKey or pbiKey] not found!");
            }
          }
        }
      }
      if (pbiKey != null) {
        cat.debug(this.getClass().getName() + ": get[pbiKey] = " + pbiKey.toString());
        idKey = getItemKey(request, pbiKey);
      }
      cat.debug(this.getClass().getName() + ": get[idKey] = " + idKey.toString());
//      request.setAttribute("idKey", idKey.toString());
      
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
    
      Integer poiKey = insertPurchaseOrderItem(request,
                                               theKey,
                                               idKey,
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