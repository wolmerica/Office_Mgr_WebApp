/*
 * VendorInvoiceGetAction.java
 *
 * Created on October 23, 2005, 9:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.logistic.LogisticService;
import com.wolmerica.service.logistic.DefaultLogisticService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.taxmarkup.TaxMarkUpDO;
import com.wolmerica.taxmarkup.TaxMarkUpDropList;
import com.wolmerica.tools.formatter.FormattingException;

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
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class VendorInvoiceGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private AttributeToService attributeToService = new DefaultAttributeToService();
  private LogisticService LogisticService = new DefaultLogisticService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public LogisticService getLogisticService() {
      return LogisticService;
  }

  public void setLogisticService(LogisticService LogisticService) {
      this.LogisticService = LogisticService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private VendorInvoiceDO buildVendorInvoiceForm(HttpServletRequest request,
                                                   Integer viKey)
   throws Exception, SQLException {

    VendorInvoiceDO formDO = null;
    TaxMarkUpDO taxMarkUpRow = null;
    ArrayList taxMarkUpRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    MathContext mc = new MathContext(4);
    BigDecimal bdPercentRatio = new BigDecimal("0.01");

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT vendorinvoice.thekey,"
                   + "vendorinvoice.master_key,"
                   + "vendorinvoice.genesis_key,"
                   + "vendorinvoice.purchaseorder_key,"
                   + "purchaseorder.purchase_order_num,"
                   + "vendor.name,"
                   + "customertype.attribute_to_entity,"                    
                   + "purchaseorder.sourcetype_key,"
                   + "purchaseorder.source_key, "                   
                   + "purchaseorder.purchase_order_num,"
                   + "vendorinvoice.invoice_num,"
                   + "vendorinvoice.invoice_date,"
                   + "vendorinvoice.invoice_due_date,"
                   + "vendorinvoice.sales_tax_cost,"
                   + "vendorinvoice.service_tax_cost,"                   
                   + "vendorinvoice.packaging,"
                   + "vendorinvoice.freight,"
                   + "vendorinvoice.miscellaneous,"
                   + "vendorinvoice.grand_total,"
                   + "vendorinvoice.sales_tax_key,"
                   + "vendorinvoice.sales_tax_rate,"
                   + "vendorinvoice.service_tax_key,"
                   + "vendorinvoice.service_tax_rate,"                   
                   + "vendorinvoice.carry_factor_id,"
                   + "vendorinvoice.active_id,"
                   + "vendorinvoice.rma_number,"
                   + "vendorinvoice.note_line1,"
                   + "vendorinvoice.create_user,"
                   + "vendorinvoice.create_stamp,"
                   + "vendorinvoice.update_user,"
                   + "vendorinvoice.update_stamp "
                   + "FROM vendorinvoice, purchaseorder, vendor, customer, customertype "
                   + "WHERE purchaseorder_key = purchaseorder.thekey "
                   + "AND purchaseorder.vendor_key = vendor.thekey "
                   + "AND purchaseorder.customer_key = customer.thekey "
                   + "AND customer.customertype_key = customertype.thekey "                   
                   + "AND vendorinvoice.thekey=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        formDO = new VendorInvoiceDO();

        formDO.setKey(rs.getInt("thekey"));
        formDO.setMasterKey(rs.getInt("master_key"));
        formDO.setGenesisKey(rs.getInt("genesis_key"));
        formDO.setPurchaseOrderKey(rs.getInt("purchaseorder_key"));
        formDO.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
        formDO.setVendorName(rs.getString("name"));
        formDO.setAttributeToEntity(rs.getString("attribute_to_entity"));
        formDO.setSourceTypeKey(rs.getByte("sourcetype_key"));
        formDO.setSourceKey(rs.getInt("source_key"));        
        formDO.setInvoiceNumber(rs.getString("invoice_num"));
        formDO.setInvoiceDate(rs.getDate("invoice_date"));
        formDO.setInvoiceDueDate(rs.getDate("invoice_due_date"));
        formDO.setSalesTaxCost(rs.getBigDecimal("sales_tax_cost"));
        formDO.setServiceTaxCost(rs.getBigDecimal("service_tax_cost"));        
        formDO.setPackagingCost(rs.getBigDecimal("packaging"));
        formDO.setFreightCost(rs.getBigDecimal("freight"));
        formDO.setMiscellaneousCost(rs.getBigDecimal("miscellaneous"));
        formDO.setInvoiceTotal(rs.getBigDecimal("grand_total"));
        formDO.setSalesTaxKey(rs.getByte("sales_tax_key"));
        formDO.setSalesTaxRate(rs.getBigDecimal("sales_tax_rate"));
        formDO.setServiceTaxKey(rs.getByte("service_tax_key"));
        formDO.setServiceTaxRate(rs.getBigDecimal("service_tax_rate"));        
        formDO.setCarryFactorId(rs.getBoolean("carry_factor_id"));
        formDO.setActiveId(rs.getBoolean("active_id"));
        formDO.setCreditId(!(formDO.getKey().equals(formDO.getGenesisKey())));
        formDO.setRmaNumber(rs.getString("rma_number"));
        formDO.setNoteLine1(rs.getString("note_line1"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));
        
//--------------------------------------------------------------------------------
// The source type key value is based on the FeatureResources.properties file.
//--------------------------------------------------------------------------------
        if (formDO.getSourceTypeKey().compareTo(new Byte("-1")) > 0) {
//--------------------------------------------------------------------------------
// Retrieve the pet, system, or vehicle attributed to this invoice.
//--------------------------------------------------------------------------------
          HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                    formDO.getSourceTypeKey(),
                                                    formDO.getSourceKey());
          formDO.setAttributeToName(nameMap.get("attributeToName").toString());
        }
                
//--------------------------------------------------------------------------------
// Get the count of attachments associated with this vendor invoice.
//--------------------------------------------------------------------------------
        formDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                        getUserStateService().getFeatureKey().byteValue(),
                                                        formDO.getKey()));
        
//--------------------------------------------------------------------------------
// Get the logistics record count for the vendor invoice.
//--------------------------------------------------------------------------------
        formDO.setLogisticsCount(getLogisticService().getLogisticsCount(conn,
                                                      getUserStateService().getFeatureKey().byteValue(),
                                                      formDO.getKey()));        
      }
      else {
        throw new Exception("VendorInvoice  " + viKey.toString() + " not found!");
      }
//-----------------------------------------------------------------------------
// Retrieve the tax mark-up values available.
//-----------------------------------------------------------------------------
      TaxMarkUpDropList tmudl = new TaxMarkUpDropList();
      taxMarkUpRows = tmudl.getSalesTaxMarkUpList(conn);
      formDO.setTaxMarkUpForm(taxMarkUpRows);

//--------------------------------------------------------------------------------
// 02/21/2007 Call the GetVendorInvoiceTotalsByVI stored procedure to compute values.
//--------------------------------------------------------------------------------
//  1) LineItemTotal()       summation of line items price before discounts.
//  2) TaxableTotal()        summation of the taxable items.
//  3) NonTaxableTotal()     summation of the non-taxable items.
//  4) SalesTaxCost()        computed sales tax cost based on taxable items.
//  5) ServiceTotal()        summation of service price.
//  6) ServiceTaxableAmt()   summation of the taxable services.
//  7) ServiceNonTaxableAmt()summation of the non-taxable services.
//  8) ServiceTaxAmt()       computed services tax cost bases on taxable services.     
//--------------------------------------------------------------------------------
        formDO = getVendorInvoiceTotalsByViKey(conn, formDO);

//--------------------------------------------------------------------------------
// Evaluate the state of non-active and non-credit orders to find
// out if a credit invoice is permissable for the order.
//--------------------------------------------------------------------------------
      if (!(formDO.getActiveId()) && !(formDO.getCreditId())) {
        // Make sure there is not an active invoice related to the original.
        query = "SELECT thekey "
              + "FROM vendorinvoice "
              + "WHERE genesis_key = ? "
              + "AND active_id";
        ps = conn.prepareStatement(query);
        ps.setInt(1, formDO.getGenesisKey());
        rs = ps.executeQuery();
        if (!(rs.next())) {
//--------------------------------------------------------------------------------
// Get the stockitem count for all the items checked in from this vendor invoice
// that have at least one unit left that can be returned to the vendor.
//--------------------------------------------------------------------------------
          query = "SELECT COUNT(*) AS record_cnt "
                + "FROM stockitem, vendorinvoiceitem "
                + "WHERE stockitem.vendorinvoiceitem_key = vendorinvoiceitem.thekey "
                + "AND vendorinvoiceitem.vendorinvoice_key = ? "
                + "AND stockitem.quantity >= 1 "
                + "AND stockitem.active_id";
          ps = conn.prepareStatement(query);
          ps.setInt(1, formDO.getKey());
          rs = ps.executeQuery();
          if ( rs.next() ) {
            if (rs.getShort("record_cnt") > 0) {
              cat.debug(this.getClass().getName() + ": setAllowCreditId(true)");
              formDO.setAllowCreditId(true);
            }
          }
          else {
            throw new Exception("StockItem COUNT() " + viKey.toString() + " not found!");
          }
        }
      }
//--------------------------------------------------------------------------------
// Compare the order quantity from the purchase order with what is received.
// 04/20/2006 - No need to check for quantity amounts when dealing with credit.
//--------------------------------------------------------------------------------
      if (formDO.getActiveId() && (!(formDO.getCreditId()))) {
        formDO.setBalanceQty(getCheckInQuantityDifference(conn, formDO.getKey()));
      }
//--------------------------------------------------------------------------------
// Compare the amounts in the vendor invoice with computed item amounts.
// 04/20/2006 - Only check the dollar amount for active vendor invoices.
//--------------------------------------------------------------------------------
      if (formDO.getActiveId()) {
        formDO.setBalanceAmount(getCheckInAmountDifference(conn, formDO.getKey()));
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
    return formDO;
  }


  public VendorInvoiceDO getVendorInvoiceTotalsByViKey(Connection conn,
                                                       VendorInvoiceDO formDO)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with three IN parameters and four OUT parameters.
// lineItemTotal, taxableAmt, and salesTaxAmt values are updated when active.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetVendorInvoiceTotalsByVI(?,?,?,?,?,?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, formDO.getKey());
      cStmt.setBoolean(2, formDO.getActiveId());
      cStmt.setBigDecimal(3, formDO.getSalesTaxRate());
      cStmt.setBigDecimal(4, formDO.getServiceTaxRate());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": lineItemTotal.......: " + cStmt.getBigDecimal("lineItemTotal"));
      cat.debug(this.getClass().getName() + ": taxableAmt..........: " + cStmt.getBigDecimal("taxableAmt"));
      cat.debug(this.getClass().getName() + ": nonTaxableAmt.......: " + cStmt.getBigDecimal("nonTaxableAmt"));
      cat.debug(this.getClass().getName() + ": salesTaxAmt.........: " + cStmt.getBigDecimal("salesTaxAmt"));
      cat.debug(this.getClass().getName() + ": serviceTotal........: " + cStmt.getBigDecimal("serviceTotal"));
      cat.debug(this.getClass().getName() + ": serviceTaxableAmt...: " + cStmt.getBigDecimal("serviceTaxableAmt"));
      cat.debug(this.getClass().getName() + ": serviceNonTaxableAmt: " + cStmt.getBigDecimal("serviceNonTaxableAmt"));
      cat.debug(this.getClass().getName() + ": serviceTaxAmt.......: " + cStmt.getBigDecimal("serviceTaxAmt"));
      
//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setLineItemTotal(cStmt.getBigDecimal("lineItemTotal"));
      formDO.setTaxableTotal(cStmt.getBigDecimal("taxableAmt"));
      formDO.setNonTaxableTotal(cStmt.getBigDecimal("nonTaxableAmt"));
      formDO.setSalesTaxCost(cStmt.getBigDecimal("salesTaxAmt"));
      formDO.setServiceTotal(cStmt.getBigDecimal("serviceTotal"));
      formDO.setServiceTaxableTotal(cStmt.getBigDecimal("serviceTaxableAmt"));
      formDO.setServiceNonTaxableTotal(cStmt.getBigDecimal("serviceNonTaxableAmt"));      
      formDO.setServiceTaxCost(cStmt.getBigDecimal("serviceTaxAmt"));      
      
      formDO.setSubTotal(formDO.getLineItemTotal().add(formDO.getServiceTotal()));
    } 
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorInvoiceTotalsByVI() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorInvoiceTotalsByVI() " + e.getMessage());
      }
    }
    return formDO;
  }


  private VendorInvoiceDO creditVendorInvoiceForm(HttpServletRequest request,
                                                  VendorInvoiceDO formDO)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer viCount = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Find out how many vendor invoice records are associated with the genesis_key.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS vi_count "
                   + "FROM vendorinvoice "
                   + "WHERE genesis_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, formDO.getGenesisKey());
      rs = ps.executeQuery();
      if ( rs.next() ) {
        viCount = rs.getInt("vi_count");
      }
      else {
        throw new Exception("VendorInvoice COUNT() not found!");
      }

      formDO.setKey(null);
//    formDO.setMasterKey(null);
//    formDO.setGenesisKey(null);
//    formDO.setPurchaseOrderKey(null);
//    formDO.setPurchaseOrderNumber("");
//    formDO.setVendorName("");
      formDO.setInvoiceNumber(formDO.getInvoiceNumber()+"-CR"+viCount.toString());
//    formDO.setInvoiceDate(null);
//    formDO.setInvoiceDueDate(null);
      formDO.setSalesTaxCost(new BigDecimal("0"));
      formDO.setPackagingCost(new BigDecimal("0"));
      formDO.setFreightCost(new BigDecimal("0"));
      formDO.setMiscellaneousCost(new BigDecimal("0"));
      formDO.setInvoiceTotal(new BigDecimal("0"));
//    formDO.setSalesTaxKey(new Byte("0"));
//    formDO.setSalesTaxRate(new BigDecimal("0"));
      formDO.setLineItemTotal(new BigDecimal("0"));
      formDO.setTaxableTotal(new BigDecimal("0"));
//    formDO.setCarryFactorId(false);
      formDO.setActiveId(true);
      formDO.setCreditId(true);
      formDO.setAllowCreditId(false);
      formDO.setCreateUser("");
      formDO.setCreateStamp(null);
      formDO.setUpdateUser("");
      formDO.setUpdateStamp(null);
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
    return formDO;
  }

  public Integer getCheckInQuantityDifference(Connection conn,
                                              Integer viKey)
   throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer pendingQty = new Integer("0");
    Integer receiveQty = new Integer("0");
    Integer balanceQty = new Integer("0");

    try {
//--------------------------------------------------------------------------------
// Check the balance of the pending and receive quantity in the vendor invoice.
//--------------------------------------------------------------------------------
      String query = "SELECT SUM(pending_qty) AS pending_qty, "
                   + "SUM(receive_qty) AS receive_qty "
                   + "FROM vendorinvoiceitem "
                   + "WHERE vendorinvoice_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        pendingQty = rs.getInt("pending_qty");
        receiveQty = rs.getInt("receive_qty");
      }
      else {
        throw new Exception("VendorInvoiceItem SUM(qty) " + viKey.toString() + " not found!");
      }
      balanceQty = ( pendingQty - receiveQty );
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
    return balanceQty;
  }

  public BigDecimal getCheckInAmountDifference(Connection conn,
                                               Integer viKey)
   throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    BigDecimal lineItemAmount = new BigDecimal("0");
    BigDecimal otherAmount = new BigDecimal("0");
    BigDecimal totalAmount = new BigDecimal("0");
    BigDecimal balanceAmount = new BigDecimal("0");

    try {
//--------------------------------------------------------------------------------
// Compute the total number of items that were ordered for this P.O.
//--------------------------------------------------------------------------------
      String query = "SELECT sales_tax_cost+packaging+freight+miscellaneous AS other_cost, "
                   + "grand_total "
                   + "FROM vendorinvoice "
                   + "WHERE vendorinvoice.thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        otherAmount = rs.getBigDecimal("other_cost");
        totalAmount = rs.getBigDecimal("grand_total");
      }
      else {
        throw new Exception("VendorInvoice " + viKey.toString() + " not found!");
      }
//--------------------------------------------------------------------------------
// Compute the total number of items that were received for this vendor invoice.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS row_count, "
            + "SUM(receive_qty * (first_cost - variant_amount)) AS item_total "
            + "FROM vendorinvoiceitem "
            + "WHERE vendorinvoice_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        if (rs.getInt("row_count") > 0) {
          lineItemAmount = rs.getBigDecimal("item_total");
        }
        else {
//--------------------------------------------------------------------------------
// A vendor invoice check-in cannot be complete without a vendor invoice.
//--------------------------------------------------------------------------------
          lineItemAmount = new BigDecimal("9999.99");
        }
      }
      else {
        throw new Exception("VendorInvoiceItem SUM(cost) " + viKey.toString() + " not found!");
      }
      cat.debug(this.getClass().getName() + ": Total = " + totalAmount.toString());
      cat.debug(this.getClass().getName() + ": LineItem = " + lineItemAmount.toString());
      cat.debug(this.getClass().getName() + ": Other = " + otherAmount.toString());
      balanceAmount = lineItemAmount.add(otherAmount);
      cat.debug(this.getClass().getName() + ": minus = " + balanceAmount.toString());
      balanceAmount = totalAmount.subtract(balanceAmount);
      cat.debug(this.getClass().getName() + ": Balance = " + balanceAmount.toString());
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
    return balanceAmount;
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
      // Vendor Invoice Key
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        VendorInvoiceDO formDO = buildVendorInvoiceForm(request, theKey);
        formDO.setPermissionStatus(usToken);
//--------------------------------------------------------------------------------
// Make sure that only the active vendor invoice is editable.
//--------------------------------------------------------------------------------
        if (formDO.getActiveId() && (usToken.compareToIgnoreCase(getUserStateService().getLocked()) == 0))
          request.setAttribute(getUserStateService().getDisableEdit(), false);
        else
          request.setAttribute(getUserStateService().getDisableEdit(), true);
        cat.debug(this.getClass().getName() + ": key = " + theKey.toString() + " creditId = " + formDO.getCreditId());
        if ((!(request.getParameter("creditKey") == null)) && (!formDO.getCreditId())) {
          formDO = creditVendorInvoiceForm(request, formDO);
        }
        request.getSession().setAttribute("vendorinvoice", formDO);
        VendorInvoiceForm formStr = new VendorInvoiceForm();
        formStr.populate(formDO);

        form = formStr;
      }
      catch (FormattingException fe) {
            fe.getMessage();
      }

      if ( form == null ) {
        cat.debug(this.getClass().getName() + ":---->form is null<----");
      }
      if ("request".equals(mapping.getScope())) {
        cat.debug(this.getClass().getName() + ":---->request.setAttribute<----");
        request.setAttribute(mapping.getAttribute(), form);
      }
      else {
        cat.debug(this.getClass().getName() + ":---->session.setAttribute<----");
        request.getSession().setAttribute(mapping.getAttribute(), form);
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