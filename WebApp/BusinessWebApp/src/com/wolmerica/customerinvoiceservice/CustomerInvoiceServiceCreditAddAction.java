/*
 * CustomerInvoiceServiceCreditAddAction.java
 *
 * Created on August 16, 2006, 04:42 PM
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
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class CustomerInvoiceServiceCreditAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  
  
  public void creditCustomerInvoiceService(HttpServletRequest request,
                                           Integer ciKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    PreparedStatement ps3 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    Integer cisKey = new Integer("0");

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// The new customer invoice contains a master invoice key which will tell us
// what the original customer invoice is that we want to provide credit for.
//--------------------------------------------------------------------------------
      String query = "SELECT genesis_key "
                   + "FROM customerinvoice "
                   + "WHERE thekey=?";

      cat.debug(this.getClass().getName() + ": Query #1 = " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();
      Integer genesisKey = 0;
      if ( rs.next() ) {
        genesisKey = rs.getInt("genesis_key");
      }
      else {
        throw new Exception("CustomerInvoice  " + ciKey.toString() + " not found!");
      }

//--------------------------------------------------------------------------------
// Select all the customer invoice services sold in the orginal invoice.
//--------------------------------------------------------------------------------
      query = "SELECT thekey,"
            + "servicedictionary_key,"
            + "order_qty,service_price,"
            + "service_tax_id,discount_rate,"
            + "pricetype_key,cost_basis,"
            + "genesis_key "
            + "FROM customerinvoiceservice "
            + "WHERE customerinvoice_key = ? "
            + "AND thekey = genesis_key";

      cat.debug(this.getClass().getName() + ": Query #2 = " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, genesisKey);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Select all the customer invoice services sold ore returned in invoices.
//--------------------------------------------------------------------------------
      query = "SELECT sum(order_qty) AS invoice_qty "
            + "FROM customerinvoiceservice "
            + "WHERE customerinvoiceservice.genesis_key = ?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Get the maximum key from the customer invoice service.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS cis_cnt, MAX(thekey)+1 AS cis_key "
            + "FROM customerinvoiceservice";
      ps2 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Preparation of a insert query for the customer invoice service.
//--------------------------------------------------------------------------------
      query = "INSERT INTO customerinvoiceservice "
            + "(thekey,customerinvoice_key,"
            + "servicedictionary_key,available_qty,order_qty,"
            + "service_price,discount_rate,service_tax_id,"
            + "pricetype_key,cost_basis,genesis_key,"
            + "create_user,create_stamp,update_user,update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      cat.debug(this.getClass().getName() + ": Insert #1 = " + query);
      ps3 = conn.prepareStatement(query);

      Integer invoiceQty = 0;
      while ( rs.next() ) {
//--------------------------------------------------------------------------------
// Services that still have a quantity to be checked in are added to customer invoice.
//--------------------------------------------------------------------------------
        ps.setInt(1, rs.getInt("thekey"));
        rs2 = ps.executeQuery();
        if ( rs2.next() ) {
          invoiceQty = rs2.getInt("invoice_qty");
          if (invoiceQty > 0) {
            cat.debug(this.getClass().getName()
                       + ": Add Service=" + rs.getString("servicedictionary_key")
                       + " Qty=" + invoiceQty.toString() );
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the customer invoice
// service table.  We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
            rs2 = ps2.executeQuery();
            if ( rs2.next() ) {
              if ( rs2.getInt("cis_cnt") > 0 ) {
                cisKey = rs2.getInt("cis_key");
              }
            }
            else {
              throw new Exception("CustomerInvoiceService MAX() not found!");
            }

//--------------------------------------------------------------------------------
// Insert new row into customer invoice service with credit amounts.
//--------------------------------------------------------------------------------
            ps3.setInt(1, cisKey);
            ps3.setInt(2, ciKey);
            ps3.setInt(3, rs.getInt("servicedictionary_key"));
            ps3.setInt(4, invoiceQty);
            ps3.setInt(5, 0);
            ps3.setBigDecimal(6, rs.getBigDecimal("service_price").setScale(2, BigDecimal.ROUND_HALF_UP));
            ps3.setBigDecimal(7, rs.getBigDecimal("discount_rate").setScale(2, BigDecimal.ROUND_HALF_UP));
            ps3.setBoolean(8, rs.getBoolean("service_tax_id"));
            ps3.setInt(9, rs.getByte("pricetype_key"));
            ps3.setBigDecimal(10, rs.getBigDecimal("cost_basis").setScale(3, BigDecimal.ROUND_HALF_UP));
            ps3.setInt(11, rs.getInt("genesis_key"));
            ps3.setString(12, request.getSession().getAttribute("USERNAME").toString());
            ps3.setString(13, request.getSession().getAttribute("USERNAME").toString());

            ps3.executeUpdate();
          }
        }
        else {
          throw new Exception("CustomerInvoiceService " + ciKey.toString() + " not found!");
        }
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
      if (rs2 != null) {
        try {
          rs2.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs2 = null;
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
      // Customer Invoice Key
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

      creditCustomerInvoiceService(request,
                                   theKey);
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