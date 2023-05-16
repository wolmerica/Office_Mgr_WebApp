/*
 * CustomerInvoiceServiceEditAction.java
 *
 * Created on August 16, 2006, 2:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.customerinvoiceservice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class CustomerInvoiceServiceEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void updateCustomerInvoiceService(HttpServletRequest request,
                                            ActionForm form)
   throws Exception, IOException, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      CustomerInvoiceServiceHeadDO formHDO = (CustomerInvoiceServiceHeadDO) request.getSession().getAttribute("customerinvoiceserviceHDO");

//--------------------------------------------------------------------------------
// Prepare a query to update the customer invoice service table according to thekey.
//--------------------------------------------------------------------------------
      String query = "UPDATE customerinvoiceservice SET "
                   + "order_qty=?,"
                   + "service_price=?,"
                   + "service_tax_id=?,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// We need only to update note_line1 of the the master record for each service.
//--------------------------------------------------------------------------------
      query = "UPDATE customerinvoiceservice SET "
            + "note_line1=? "
            + "WHERE thekey=?";
      ps2 = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Traverse the customerinvoiceservice object.
//--------------------------------------------------------------------------------
      ArrayList cisList = formHDO.getCustomerInvoiceServiceForm();
      CustomerInvoiceServiceDO formDO = null;

//--------------------------------------------------------------------------------
// Iterate through the rows in the service list.
//--------------------------------------------------------------------------------
      Integer cisOrderQty = 0;
      for (int j=0; j < cisList.size(); j++) {
        formDO = (CustomerInvoiceServiceDO) cisList.get(j);                

        if (formHDO.getActiveId()) {
          cisOrderQty = formDO.getOrderQty().intValue();
          if (formHDO.getCreditId()) {
//--------------------------------------------------------------------------------
// Perform the credit operations for these customer invoice items.
//--------------------------------------------------------------------------------
            cat.debug(this.getClass().getName() + " Credit CustomerInvoiceService");
            cisOrderQty = formDO.getOrderQty().intValue() * -1; 
          }
//--------------------------------------------------------------------------------
// Perform the normal edit operations for these customer invoice items.
//--------------------------------------------------------------------------------
          cat.debug(this.getClass().getName() + " Normal CustomerInvoiceService");
          cat.debug(this.getClass().getName() + " Qty = " + formDO.getOrderQty());
          cat.debug(this.getClass().getName() + " Price = " + formDO.getThePrice());            

          ps.setShort(1, cisOrderQty.shortValue());
          ps.setBigDecimal(2, formDO.getThePrice());
          ps.setBoolean(3, formDO.getServiceTaxId());
          ps.setString(4, request.getSession().getAttribute("USERNAME").toString());
          ps.setInt(5, formDO.getKey());
          ps.executeUpdate();
        }
//--------------------------------------------------------------------------------
// Update note_line1 of the the master record for each item.
//--------------------------------------------------------------------------------
        ps2.setString(1, formDO.getNoteLine1());
        ps2.setInt(2,formDO.getKey());
        ps2.executeUpdate();
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
      if (ps2 != null) {
        try {
          ps2.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps2 = null;
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
   throws Exception, SQLException {

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

      updateCustomerInvoiceService(request,form);
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