/*
 * CustomerInvoiceAddAction.java
 *
 * Created on October 23, 2005, 6:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.customerinvoice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.tax.TaxService;
import com.wolmerica.service.tax.DefaultTaxService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.util.common.EnumCustomerInvoiceScenario;

import java.io.IOException;
import java.text.DecimalFormat;
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
import java.util.Calendar;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class CustomerInvoiceAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private TaxService taxService = new DefaultTaxService();
  private UserStateService userStateService = new DefaultUserStateService();

  public TaxService getTaxService() {
      return taxService;
  }

  public void setTaxService(TaxService taxService) {
      this.taxService = taxService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private Integer insertCustomerInvoice(HttpServletRequest request,
                                        ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer ciKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      CustomerInvoiceDO formDO = (CustomerInvoiceDO) request.getSession().getAttribute("customerinvoice");

//--------------------------------------------------------------------------------
// Compute the customer invoice number and return the value in the customerinvoice object.
//--------------------------------------------------------------------------------
      formDO = getCustomerInvoiceNumber(request,
                                        conn,
                                        formDO);
      cat.debug(this.getClass().getName() + ": CustomerInvoice#= " + formDO.getCustomerInvoiceNumber());

//--------------------------------------------------------------------------------
// Get the ITEM SALES tax rate related to the sales tax key.  The item and
// service sales tax rates are paired in the TaxAndMarkUp table by a factor
// of 50.  Item sales tax key = 0 and service sales tax = 50.
//--------------------------------------------------------------------------------
      Integer serviceTaxKey = formDO.getSalesTaxKey() + 50;
      formDO.setSalesTaxRate(getTaxService().getTaxRate(conn, formDO.getSalesTaxKey()));

//----------------------------------------------------------------------
// Get the SERVICE tax rate related to the service tax key.
//----------------------------------------------------------------------
      formDO.setServiceTaxKey(serviceTaxKey.byteValue());
      formDO.setServiceTaxRate(getTaxService().getTaxRate(conn, formDO.getServiceTaxKey()));

//--------------------------------------------------------------------------------
// Get the maximum key from the customer invoice item.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS ci_cnt, MAX(thekey)+1 AS ci_key "
                   + "FROM customerinvoice ";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the customer invoice
// item table.  We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("ci_cnt") > 0 ) {
          ciKey = rs.getInt("ci_key");
        }
      }
      else {
        throw new Exception("CustomerInvoice  " + ciKey.toString() + " not found!");
      }
      formDO.setKey(ciKey);

      query = "INSERT INTO customerinvoice "
            + "(thekey,"
            + "customer_key,"
            + "customertype_key,"
            + "vendorinvoice_key,"
            + "invoice_num,"
            + "note_line1,"
            + "note_line2,"
            + "note_line3,"
            + "item_total,"
            + "sub_total,"
            + "sales_tax_key,"
            + "sales_tax_rate,"
            + "sales_tax,"
            + "service_tax_key,"
            + "service_tax_rate,"
            + "service_tax,"
            + "debit_adjustment,"
            + "packaging,"
            + "freight,"
            + "miscellaneous,"
            + "credit_adjustment,"
            + "invoice_total," 
            + "active_id,"
            + "genesis_key,"
            + "scenario_key,"
            + "sourcetype_key,"
            + "source_key,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, formDO.getKey());
      ps.setInt(2, formDO.getCustomerKey());
      ps.setByte(3, formDO.getCustomerTypeKey());
      ps.setInt(4, formDO.getVendorInvoiceKey());
      ps.setString(5, formDO.getCustomerInvoiceNumber());
      ps.setString(6, formDO.getNoteLine1());
      ps.setString(7, formDO.getNoteLine2());
      ps.setString(8, formDO.getNoteLine3());
      ps.setBigDecimal(9, formDO.getItemNetAmount());
      ps.setBigDecimal(10, formDO.getSubTotal());
      ps.setByte(11, formDO.getSalesTaxKey());
      ps.setBigDecimal(12, formDO.getSalesTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(13, formDO.getSalesTaxCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setByte(14, formDO.getSalesTaxKey());
      ps.setBigDecimal(15, formDO.getServiceTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(16, formDO.getServiceTaxCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(17, formDO.getDebitAdjustment().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(18, formDO.getPackagingCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(19, formDO.getFreightCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(20, formDO.getMiscellaneousCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(21, formDO.getCreditAdjustment().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(22, formDO.getInvoiceTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBoolean(23, formDO.getActiveId());
      ps.setInt(24, formDO.getGenesisKey());
      ps.setByte(25, formDO.getScenarioKey());
      ps.setByte(26, formDO.getSourceTypeKey());
      ps.setInt(27, formDO.getSourceKey());
      ps.setString(28, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(29, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();
//--------------------------------------------------------------------------------
// A null master invoice key implies this is the original invoice creation.
// For auditing the subsequent credits will be linked to the original invoice.
//--------------------------------------------------------------------------------
      if (formDO.getGenesisKey() < 0) {
        formDO.setGenesisKey(formDO.getKey());
        query = "UPDATE customerinvoice "
              + "SET genesis_key=? "
              + "WHERE thekey =?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, formDO.getKey());
        ps.setInt(2, formDO.getKey());
        ps.executeUpdate();
      }
//--------------------------------------------------------------------------------
// Preserve the invoice key with the appointment it is associated with.
// May consider setting the schedule status_key to complete at this point.
//--------------------------------------------------------------------------------
      if (formDO.getScheduleKey() > 0) {
        query = "UPDATE schedule "
              + "SET customerinvoice_key = ? "
              + "WHERE thekey = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, formDO.getKey().toString());
        ps.setInt(2, formDO.getScheduleKey());
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
    return ciKey;
  }

  private CustomerInvoiceDO getCustomerInvoiceNumber(HttpServletRequest request,
                                                     Connection conn,
                                                     CustomerInvoiceDO formDO)
    throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;
    Integer returnKey = null;
    Integer poCount = null;
    Boolean cClinicId = true;
    String custInvNum = "";

    try {
//--------------------------------------------------------------------------------
// Look-up the customer type and clinic indicator for the customer.
//--------------------------------------------------------------------------------
      String query = "SELECT customertype_key, clinic_id "
                   + "FROM customer "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, formDO.getCustomerKey());
      rs = ps.executeQuery();
      if ( rs.next() ) {
        formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        cClinicId = rs.getBoolean("clinic_id");
      }
      else {
        throw new Exception("Customer key " + formDO.getCustomerKey().toString() + " not found!");
      }

//--------------------------------------------------------------------------------
// After confirming with my wife it was decided that we will preserve the original
// invoice number from the order and append "CR" and a two digit sequential number.
//--------------------------------------------------------------------------------
      if (formDO.getGenesisKey() >= 0) {
        query = "SELECT COUNT(*) AS ci_count "
              + "FROM customerinvoice "
              + "WHERE genesis_key=?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, formDO.getGenesisKey());
        rs = ps.executeQuery();
        if ( rs.next() ) {
          poCount = rs.getInt("ci_count");
        }
        else {
          throw new Exception("CustomerInvoice COUNT() not found!");
        }
      }
//--------------------------------------------------------------------------------
// Database interactions to come up with a standard invoice number.
//--------------------------------------------------------------------------------
      else {
        query = "SELECT order_count "
              + "FROM customerinvoicenumber "
              + "WHERE order_date = CURRENT_DATE "
              + "AND clinic_id=?";
        ps = conn.prepareStatement(query);
        ps.setBoolean(1, cClinicId);
        rs = ps.executeQuery();
        if ( rs.next() ) {
          poCount = rs.getInt("order_count");
//--------------------------------------------------------------------------------
// Increment the order_count value by one in customerinvoicebumber
//--------------------------------------------------------------------------------
          query = "UPDATE customerinvoicenumber "
                + "SET order_count = order_count + 1 "
                + "WHERE clinic_id=?";
          ps = conn.prepareStatement(query);
          ps.setBoolean(1, cClinicId);
          ps.executeUpdate();
        }
        else {
//--------------------------------------------------------------------------------
// Updating the 'ordernumber' table for a new day.
//--------------------------------------------------------------------------------
          poCount = 1;
          query = "UPDATE customerinvoicenumber "
                + "SET order_count = ?, "
                + "order_date = CURRENT_DATE, "
                + "update_user = ?, "
                + "update_stamp = CURRENT_TIMESTAMP "
                + "WHERE order_date != CURRENT_DATE "
                + "AND clinic_id=?";
          ps = conn.prepareStatement(query);
          ps.setInt(1, new Integer("2"));
          ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
          ps.setBoolean(3, cClinicId);
          ps.executeUpdate();
        }
      }

//--------------------------------------------------------------------------------
// Set the scenario key for "direct ship", "drop ship", or "sell inventory".
//--------------------------------------------------------------------------------
      if (!(cClinicId)) {
        if (formDO.getVendorInvoiceKey() >= 0) {
          if (formDO.getScenarioKey() != new Byte(EnumCustomerInvoiceScenario.DirectShip.getValue()))
            formDO.setScenarioKey(new Byte(EnumCustomerInvoiceScenario.DropShip.getValue()));
        }
        else
          formDO.setScenarioKey(new Byte(EnumCustomerInvoiceScenario.SellInventory.getValue()));
      }

//--------------------------------------------------------------------------------
// A credit preserves the original customer invoice number and appends "-CR<count>"
//--------------------------------------------------------------------------------
      if (formDO.getGenesisKey() >= 0) {
        formDO.setScenarioKey(new Byte(EnumCustomerInvoiceScenario.ReturnCredit.getValue()));
        custInvNum = formDO.getCustomerInvoiceNumber()+"-CR"+poCount.toString();
      }
      else {
        Calendar now = Calendar.getInstance();

        Integer pomth = now.get(Calendar.MONTH) + 1;
        Integer poday = now.get(Calendar.DATE);
        Integer poyear = now.get(Calendar.YEAR);
        String posYear = poyear.toString();

        DecimalFormat myFormat = new java.text.DecimalFormat("00");
        custInvNum = myFormat.format(pomth);
        custInvNum = custInvNum + myFormat.format(poday);
        custInvNum = custInvNum + posYear.substring(2);
//--------------------------------------------------------------------------------
// Append the poCount value to the to the beginning of the customer invoice number.
//--------------------------------------------------------------------------------
        myFormat = new java.text.DecimalFormat("000");
        custInvNum = custInvNum + myFormat.format(poCount);
        if (poCount > 999)
          throw new Exception("Purchase order count for the day exceeds the 999 maximum.");

//--------------------------------------------------------------------------------
// The legacy HB accounting system can only handle 7 digit invoice numbers.
// We drop year out of the customer invoice number for now due to old bookkeeping.
// Regular - MMDDnnn
// 0 - Direct ship to customer.
// 1 - Drop ship to customer.
// 2 - Sell inventory to customer.
// Credit -  MMDDnnn-CRxxx
// 3 - Credit for a customer return.
// Add Inventory - MMDDnnn-STOCK
// 4 - Business adds to inventory.
// Office Use - MMDDnnn-OFFICE
// 5 - Business use of inventory.
// Office Loss - MMDDnnn-LOSS
// 6 - Business inventory loss.
//--------------------------------------------------------------------------------
        if (cClinicId) {
          if (formDO.getVendorInvoiceKey() >= 0) {
            formDO.setScenarioKey(new Byte(EnumCustomerInvoiceScenario.AddInventory.getValue()));
            custInvNum=custInvNum+"-STOCK";
          }
          else {
            formDO.setScenarioKey(new Byte(EnumCustomerInvoiceScenario.OfficeUse.getValue()));
            custInvNum=custInvNum+"-OFFICE";
          }
        }
      }
      formDO.setCustomerInvoiceNumber(custInvNum);
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
    }
    return formDO;
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

      cat.debug(this.getClass().getName() + ": call insertCustomerInvoice");
      Integer theKey = insertCustomerInvoice(request, form);
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());
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