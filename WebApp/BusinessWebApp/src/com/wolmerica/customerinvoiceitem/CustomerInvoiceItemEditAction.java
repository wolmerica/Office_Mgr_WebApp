/*
 * CustomerInvoiceItemEditAction.java
 *
 * Created on December 05, 2005, 1:10 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 04/13/2006 - Add logic for credit invoice editing of fragmented item qty.
 */

package com.wolmerica.customerinvoiceitem;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class CustomerInvoiceItemEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void updateCustomerInvoiceItem(HttpServletRequest request,
                                         ActionForm form)
   throws Exception, IOException, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    PreparedStatement ps3 = null;
    ResultSet rs = null;


    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      CustomerInvoiceItemHeadDO formHDO = (CustomerInvoiceItemHeadDO) request.getSession().getAttribute("customerinvoiceitemHDO");
//--------------------------------------------------------------------------------
// A customer invoice item may be supplied by more than one vendor invoice item.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "vendorinvoiceitem_key,"
                   + "itemdictionary_key,"
                   + "available_qty,"
                   + "order_qty "
                   + "FROM customerinvoiceitem "
                   + "WHERE master_key=?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Prepare a query to update the customer invoice item table according to thekey.
//--------------------------------------------------------------------------------
      query = "UPDATE customerinvoiceitem SET "
            + "order_qty=?,"
            + "item_price=?,"
            + "sales_tax_id=?,"
            + "update_user=?,"
            + "update_stamp=CURRENT_TIMESTAMP "
            + "WHERE thekey=?";
      ps2 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// We need only to update note_line1 of the the master record for each item.
//--------------------------------------------------------------------------------
      query = "UPDATE customerinvoiceitem SET "
            + "note_line1=? "
            + "WHERE thekey=?";
      ps3 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Traverse the customerinvoiceitem object.
//--------------------------------------------------------------------------------
      ArrayList ciiList = formHDO.getCustomerInvoiceItemForm();
      CustomerInvoiceItemDO formDO = null;

      Integer remainCredit = null;
      Integer reqCredit = null;
      Integer availQty = null;
//--------------------------------------------------------------------------------
// Iterate through the rows in the list.
//--------------------------------------------------------------------------------
      for (int j=0; j < ciiList.size(); j++) {
        formDO = (CustomerInvoiceItemDO) ciiList.get(j);

        if (formHDO.getActiveId()) {
          if (formHDO.getCreditId()) {
//--------------------------------------------------------------------------------
// Perform the credit operations for these customer invoice items.
//--------------------------------------------------------------------------------
            remainCredit = formDO.getOrderQty().intValue() * -1;
            cat.debug(this.getClass().getName() + " CREDIT remainCredit = " + remainCredit.toString());
            ps.setInt(1,formDO.getKey());
            rs = ps.executeQuery();
            while ((rs.next()) && (remainCredit < 0)) {
              reqCredit = remainCredit;
              availQty = rs.getInt("available_qty");
              remainCredit = availQty + reqCredit;
              if (remainCredit < 0 )
                availQty = availQty * -1;
              else
                availQty = reqCredit;
              cat.debug(this.getClass().getName() + " update CustomerInvoiceItem. ");
              ps2.setShort(1, availQty.shortValue());
              ps2.setBigDecimal(2, formDO.getThePrice().setScale(2, BigDecimal.ROUND_HALF_UP));
              ps2.setBoolean(3, formDO.getSalesTaxId());
              ps2.setString(4, request.getSession().getAttribute("USERNAME").toString());
              ps2.setInt(5, rs.getInt("thekey"));
              ps2.executeUpdate();
            }
          }
          else {
//--------------------------------------------------------------------------------
// Perform the normal edit operations for these customer invoice items.
//--------------------------------------------------------------------------------
            cat.debug(this.getClass().getName() + " Normal edit thePrice = " + formDO.getThePrice().setScale(2, BigDecimal.ROUND_HALF_UP));
            ps2.setShort(1, formDO.getOrderQty());
            ps2.setBigDecimal(2, formDO.getThePrice().setScale(2, BigDecimal.ROUND_HALF_UP));
            ps2.setBoolean(3, formDO.getSalesTaxId());
            ps2.setString(4, request.getSession().getAttribute("USERNAME").toString());
            ps2.setInt(5, formDO.getKey());
            ps2.executeUpdate();
          }
        }
//--------------------------------------------------------------------------------
// Update note_line1 of the the master record for each item.
//--------------------------------------------------------------------------------
        ps3.setString(1, formDO.getNoteLine1());
        ps3.setInt(2,formDO.getKey());
        ps3.executeUpdate();
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
      if (ps3 != null) {
        try {
          ps3.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps3 = null;
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

      updateCustomerInvoiceItem(request,form);
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