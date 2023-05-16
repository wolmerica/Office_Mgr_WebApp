/*
 * PurchaseOrderEditAction.java
 *
 * Created on September 7, 2005, 1:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/23/2005 Implement tools.formatter library.
 */

package com.wolmerica.purchaseorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.util.common.EnumPurchaseOrderStatus;

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

public class PurchaseOrderEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private void updatePurchaseOrder(HttpServletRequest request,
                                     ActionForm form)
    throws Exception, SQLException {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
       
      PurchaseOrderDO formDO = (PurchaseOrderDO) request.getSession().getAttribute("purchaseorder");

      String query = "";
      if (formDO.getOrderStatus().equalsIgnoreCase(EnumPurchaseOrderStatus.N.getValue())) {
        query = "UPDATE purchaseorder SET "
              + "purchase_order_num=?,"
              + "vendor_key=?,"
              + "customer_key=?,"
              + "sourcetype_key=?,"
              + "source_key=?,"
              + "priority_key=?,"
              + "note_line1=?,"
              + "update_user=?,"
              + "update_stamp=CURRENT_TIMESTAMP "
              + "WHERE thekey=?";
        ps = conn.prepareStatement(query);
        ps.setString(1, formDO.getPurchaseOrderNumber());
        ps.setInt(2, formDO.getVendorKey());
        ps.setInt(3, formDO.getCustomerKey());
        ps.setByte(4, formDO.getSourceTypeKey());
        ps.setInt(5, formDO.getSourceKey());
        ps.setByte(6, formDO.getPriorityKey());
        ps.setString(7, formDO.getNoteLine1());
        ps.setString(8, request.getSession().getAttribute("USERNAME").toString());
        ps.setInt(9, formDO.getKey());
        ps.executeUpdate();
      } 
      if (formDO.getOrderStatus().equalsIgnoreCase(EnumPurchaseOrderStatus.O.getValue())) {
        query = "UPDATE purchaseorder SET "
              + "sales_order_number=?,"
              + "note_line1=?,"
              + "update_user=?,"
              + "update_stamp=CURRENT_TIMESTAMP "
              + "WHERE thekey=?";
        ps = conn.prepareStatement(query);
        ps.setString(1, formDO.getSalesOrderNumber());
        ps.setString(2, formDO.getNoteLine1());
        ps.setString(3, request.getSession().getAttribute("USERNAME").toString());
        ps.setInt(4, formDO.getKey());
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

    cat.debug(this.getClass().getName() + ": execute() command" );

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
                
      updatePurchaseOrder(request, form);
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
