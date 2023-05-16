/*
 * VendorInvoiceServiceEditAction.java
 *
 * Created on November 08, 2008, 12:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoiceservice;

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

import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class VendorInvoiceServiceEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private void updateVendorInvoiceService(HttpServletRequest request,
                                          ActionForm form)
   throws Exception, IOException, SQLException, ServletException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;

    Integer receiveQty = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      VendorInvoiceServiceHeadDO formHDO = (VendorInvoiceServiceHeadDO) request.getSession().getAttribute("vendorinvoiceserviceHDO");

//--------------------------------------------------------------------------------
// Prepare a query to update the vendor invoice service table according to thekey.
//--------------------------------------------------------------------------------
      String query = "UPDATE vendorinvoiceservice SET "
                   + "receive_qty=?,"
                   + "first_cost=?,"
                   + "variant_amount=?,"                   
                   + "service_tax_id=?,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// We need only to update note_line1 of the the master record for each service.
//--------------------------------------------------------------------------------
      query = "UPDATE vendorinvoiceservice SET "
            + "note_line1=? "
            + "WHERE thekey=?";
      ps2 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Get the vendorinvoiceservice list.
//--------------------------------------------------------------------------------
      ArrayList visList = formHDO.getVendorInvoiceServiceForm();
      VendorInvoiceServiceDO formDO = null;
//--------------------------------------------------------------------------------
// Iterate through the rows in the list.
//--------------------------------------------------------------------------------
      for (int j=0; j < visList.size(); j++) {
        formDO = (VendorInvoiceServiceDO) visList.get(j);

//--------------------------------------------------------------------------------
// Update vendor invoice service if the order is active.  The value for a credit
// must be converted to a negative since the UI only accepts positive values.
//--------------------------------------------------------------------------------
        if (formHDO.getActiveId()) {
          receiveQty = formDO.getReceiveQty().intValue();
          if (formHDO.getCreditId())
            receiveQty = receiveQty * -1;
          ps.setShort(1, receiveQty.shortValue());
          ps.setBigDecimal(2, formDO.getFirstCost().setScale(2, BigDecimal.ROUND_HALF_UP));
          cat.debug(this.getClass().getName() + ": enableServiceTaxId=" + formDO.getEnableServiceTaxId());
          cat.debug(this.getClass().getName() + ": serviceTaxId=" + formDO.getServiceTaxId());
          ps.setBigDecimal(3, formDO.getVariantAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
          ps.setBoolean(4, formDO.getServiceTaxId());          
          ps.setString(5, request.getSession().getAttribute("USERNAME").toString());
          ps.setInt(6, formDO.getKey());
          ps.executeUpdate();
        }
        ps2.setString(1, formDO.getNoteLine1());
        ps2.setInt(2, formDO.getKey());
        ps2.executeUpdate();
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
   throws Exception, IOException, SQLException, ServletException {

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
// The Vendor Invoice Key is being passed via the key variable.
//--------------------------------------------------------------------------------        
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (request.getAttribute("key") != null) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()) ||
              usToken.equalsIgnoreCase(getUserStateService().getReadOnly()))
        throw new Exception(getUserStateService().getAccessDenied());

      updateVendorInvoiceService(request,form);
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