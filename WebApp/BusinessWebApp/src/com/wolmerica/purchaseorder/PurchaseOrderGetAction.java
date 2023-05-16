/*
 * PurchaseOrderGetAction.java
 *
 * Created on September 6, 2005, 8:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/23/2005 Implement tools.formatter library.
 * 12/28/2005 Add query to retrieve customer invoice counts for master vendor invoices.
 */

package com.wolmerica.purchaseorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.logistic.LogisticService;
import com.wolmerica.service.logistic.DefaultLogisticService;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.CurrencyFormatter;
import com.wolmerica.util.common.EnumPurchaseOrderStatus;
import com.wolmerica.vendorinvoice.VendorInvoice;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class PurchaseOrderGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private AttributeToService attributeToService = new DefaultAttributeToService();
  private LogisticService LogisticService = new DefaultLogisticService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();
  
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

  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }

  private PurchaseOrderDO buildPurchaseOrderForm(HttpServletRequest request,
                                                 Integer poKey)
   throws Exception, SQLException {

    PurchaseOrderDO formDO = null;
    CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT purchaseorder.purchase_order_num,"
                    + "purchaseorder.order_status,"
                    + "purchaseorder.priority_key,"
                    + "purchaseorder.vendor_key,"
                    + "vendor.name,"
                    + "purchaseorder.customer_key,"
                    + "customer.client_name,"
                    + "customertype.attribute_to_entity,"
                    + "purchaseorder.sourcetype_key,"
                    + "purchaseorder.source_key,"
                    + "purchaseorder.schedule_key,"
                    + "purchaseorder.submit_order_stamp,"
                    + "purchaseorder.sales_order_number,"
                    + "purchaseorder.note_line1,"
                    + "purchaseorder.create_user, purchaseorder.create_stamp,"
                    + "purchaseorder.update_user, purchaseorder.update_stamp "
                    + "FROM purchaseorder, customer, vendor, customertype "
                    + "WHERE purchaseorder.thekey = ? "
                    + "AND purchaseorder.vendor_key = vendor.thekey "
                    + "AND purchaseorder.customer_key = customer.thekey "
                    + "AND customer.customertype_key = customertype.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, poKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {

        formDO = new PurchaseOrderDO();

        formDO.setKey(poKey);
        formDO.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
        formDO.setOrderStatus(rs.getString("order_status"));
        formDO.setPriorityKey(rs.getByte("priority_key"));
        formDO.setVendorKey(rs.getInt("vendor_key"));
        formDO.setVendorName(rs.getString("name"));
        formDO.setCustomerKey(rs.getInt("customer_key"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setAttributeToEntity(rs.getString("attribute_to_entity"));
        formDO.setSourceTypeKey(rs.getByte("sourcetype_key"));
        formDO.setSourceKey(rs.getInt("source_key"));
        formDO.setScheduleKey(rs.getInt("schedule_key"));
        if (!(formDO.getOrderStatus().equalsIgnoreCase(EnumPurchaseOrderStatus.N.getValue())))
          formDO.setSubmitOrderStamp(rs.getTimestamp("submit_order_stamp"));
        formDO.setSalesOrderNumber(rs.getString("sales_order_number"));
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
          formDO.setAttributeToEntity(nameMap.get("sourceName").toString());
          formDO.setAttributeToName(nameMap.get("attributeToName").toString());
        }

//--------------------------------------------------------------------------------
// Get quantity and cost totals for items and services respectively.
//--------------------------------------------------------------------------------
        formDO = getItemTotalsByPO(conn, formDO);
        formDO = getServiceTotalsByPO(conn, formDO);

        formDO.setOrderTotal(formDO.getItemTotal().add(formDO.getServiceTotal()));

//--------------------------------------------------------------------------------
// Get the count of attachments associated with this purchase order.
//--------------------------------------------------------------------------------
        formDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                        getUserStateService().getFeatureKey().byteValue(),
                                                        formDO.getKey()));

//--------------------------------------------------------------------------------
// Get the logistics record count for the purchase order.
//--------------------------------------------------------------------------------
        formDO.setLogisticsCount(getLogisticService().getLogisticsCount(conn,
                                                      getUserStateService().getFeatureKey().byteValue(),
                                                      formDO.getKey()));

//===========================================================================
// While in the "check-in" state we need to check quantity and amount balance
// from vendor invoice amount and vendor invoice item quantity.
//===========================================================================
        if (formDO.getOrderStatus().equalsIgnoreCase(EnumPurchaseOrderStatus.I.getValue())) {
//===========================================================================
// Compare the order quantity from the purchase order with what is received.
//===========================================================================
          Integer differenceQty = getCheckInQuantityDifference(conn, true, poKey);
          cat.debug(this.getClass().getName() + ": Vendor Invoice Qty = " + differenceQty.toString());
          if (differenceQty > 0) {
            formDO.setBalanceQty("Unbalanced");
          }
          else {
            formDO.setBalanceQty("Balanced");
          }
//===========================================================================
// Compare the amounts in the vendor invoice with computed amounts.
//===========================================================================
          cat.debug(this.getClass().getName() + " before call CheckInAmountDiff" );
          BigDecimal differenceAmount = getCheckInAmountDifference(conn, true, poKey);
          cat.debug(this.getClass().getName() + " after call CheckInAmountDiff" );
          cat.debug(this.getClass().getName() + ": Vendor Invoice $ = " + currencyFormatter.format(differenceAmount));
          if ((currencyFormatter.format(differenceAmount).equalsIgnoreCase("0.00"))
            || (currencyFormatter.format(differenceAmount).equalsIgnoreCase("-0.00"))) {
            formDO.setBalanceAmount("Balanced");
          }
          else {
            formDO.setBalanceAmount("Unbalanced");
          }
        }
//--------------------------------------------------------------------------------
// Set the vendor item and service count values.
//--------------------------------------------------------------------------------
        formDO.setVendorItemCount(getVendorService().getItemCountByVendor(conn, formDO.getVendorKey()));
        formDO.setVendorServiceCount(getVendorService().getServiceCountByVendor(conn, formDO.getVendorKey()));
      }
      else {
        cat.error(this.getClass().getName() + ": PurchaseOrder " + poKey.toString() + " not found!");
        throw new Exception("PurchaseOrder " + poKey.toString() + " not found!");
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

  public PurchaseOrderDO getItemTotalsByPO(Connection conn,
                                           PurchaseOrderDO formDO)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
      cStmt = conn.prepareCall("{call GetItemTotalsByPO(?,?,?)}");
      cStmt.setInt(1, formDO.getKey());
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": itemQuantity  : " + cStmt.getInt("itemQuantity"));
      cat.debug(this.getClass().getName() + ": itemCostTotal : " + cStmt.getBigDecimal("itemCostTotal"));

      formDO.setItemQty(cStmt.getShort("itemQuantity"));
      formDO.setItemTotal(cStmt.getBigDecimal("itemCostTotal"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemTotalsByPO() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemTotalsByPO() " + e.getMessage());
      }
    }
    return formDO;
  }


  public PurchaseOrderDO getServiceTotalsByPO(Connection conn,
                                              PurchaseOrderDO formDO)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
      cStmt = conn.prepareCall("{call GetServiceTotalsByPO(?,?,?)}");
      cStmt.setInt(1, formDO.getKey());
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": serviceQuantity  : " + cStmt.getInt("serviceQuantity"));
      cat.debug(this.getClass().getName() + ": serviceCostTotal : " + cStmt.getBigDecimal("serviceCostTotal"));

      formDO.setServiceQty(cStmt.getShort("serviceQuantity"));
      formDO.setServiceTotal(cStmt.getBigDecimal("serviceCostTotal"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getServiceTotalsByPO() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getServiceTotalsByPO() " + e.getMessage());
      }
    }
    return formDO;
  }

  private ArrayList getVendorInvoice(HttpServletRequest request,
                                     Integer poKey)
   throws Exception, SQLException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    VendorInvoice vendorInvoice = null;
    ArrayList<VendorInvoice> vendorInvoices = new ArrayList<VendorInvoice>();
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    PreparedStatement ps2 = null;
    ResultSet rs2 = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT vendorinvoice.thekey,"
                   + "purchaseorder_key,"
                   + "master_key,"
                   + "genesis_key,"
                   + "customer_key,"
                   + "customer.clinic_id,"
                   + "purchase_order_num,"
                   + "vendor.name,"
                   + "invoice_num,"
                   + "vendorinvoice.active_id,"
                   + "invoice_date,"
                   + "invoice_due_date,"
                   + "grand_total,"
                   + "vendorinvoice.create_stamp "
                   + "FROM vendorinvoice, purchaseorder, vendor, customer "
                   + "WHERE purchaseorder.thekey = ? "
                   + "AND purchaseorder_key = purchaseorder.thekey "
                   + "AND purchaseorder.vendor_key = vendor.thekey "
                   + "AND purchaseorder.customer_key = customer.thekey "
                   + "ORDER BY invoice_date, vendorinvoice.thekey ";

      ps = conn.prepareStatement(query);

      query = "SELECT count(*) AS custinv_count "
            + "FROM customerinvoice "
            + "WHERE vendorinvoice_key = ? ";

      ps2 = conn.prepareStatement(query);

      ps.setInt(1, poKey);
      rs = ps.executeQuery();

      while (rs.next()) {

        cat.debug(this.getClass().getName() + ": Vendor Invoice Row="+rs.getString("purchase_order_num"));
        vendorInvoice = new VendorInvoice();

        vendorInvoice.setKey(rs.getInt("thekey"));
        vendorInvoice.setPurchaseOrderKey(rs.getInt("purchaseorder_key"));
        vendorInvoice.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
        vendorInvoice.setVendorName(rs.getString("name"));
        vendorInvoice.setInvoiceNumber(rs.getString("invoice_num"));
        vendorInvoice.setInvoiceDate(rs.getDate("invoice_date"));
        vendorInvoice.setActiveId(rs.getBoolean("active_id"));
        vendorInvoice.setCreditId(!(vendorInvoice.getKey().equals(rs.getInt("genesis_key"))));
        vendorInvoice.setInvoiceDueDate(rs.getDate("invoice_due_date"));
        if (vendorInvoice.getKey() == rs.getInt("master_key")) {
	      vendorInvoice.setMasterInvoiceId(true);
//--------------------------------------------------------------------------------
// Items with the purchaseorder customer_key greater than or equal
// to zero are purchase orders that shipped directly to the customer.
//--------------------------------------------------------------------------------
          if (rs.getBoolean("clinic_id")) {
            vendorInvoice.setDirectShipId(false);
          }
          else {
            vendorInvoice.setDirectShipId(true);
          }
//--------------------------------------------------------------------------------
// Find out how many customer invoices are related to this master invoice.
// When there are none we call the "CustomerInvoiceEntry.do" to add it.
// When there are one or more we call the "CustomerInvoiceList.do".
//--------------------------------------------------------------------------------
          ps2.setInt(1, vendorInvoice.getKey());
          rs2 = ps2.executeQuery();

          if (rs2.next()) {
            vendorInvoice.setCustomerInvoiceCount(rs2.getShort("custinv_count"));
          }
        }
	else {
	  vendorInvoice.setMasterInvoiceId(false);
        }
//--------------------------------------------------------------------------------
// Compare the order quantity from the purchase order with what is received.
// 04/20/2006 - No need to check for quantity amounts when dealing with credit.
//--------------------------------------------------------------------------------
        vendorInvoice.setBalanceQty("Balanced");
        if (vendorInvoice.getActiveId() && (!(vendorInvoice.getCreditId()))) {
          if (getCheckInQuantityDifference(conn, false, vendorInvoice.getKey()) > 0) {
            vendorInvoice.setBalanceQty("Unbalanced");
          }
        }
//--------------------------------------------------------------------------------
// Compare the amounts in the vendor invoice with computed amounts.
// 04/20/2006 - Only check the dollar amount for active vendor invoices.
//--------------------------------------------------------------------------------
        vendorInvoice.setBalanceAmount("Balanced");
        if (vendorInvoice.getActiveId()) {
          BigDecimal differenceAmount = getCheckInAmountDifference(conn,
                                                                   false,
                                                                   vendorInvoice.getKey());
          cat.debug(this.getClass().getName() + " after call CheckInAmountDiff" );
          cat.debug(this.getClass().getName() + ": Vendor Invoice Diff $ = " + currencyFormatter.format(differenceAmount));
          if (!((currencyFormatter.format(differenceAmount).equalsIgnoreCase("0.00"))
            || (currencyFormatter.format(differenceAmount).equalsIgnoreCase("-0.00")))) {
            vendorInvoice.setBalanceAmount("Unbalanced");
          }
        }
        vendorInvoice.setCreateStamp(rs.getTimestamp("create_stamp"));
	vendorInvoice.setGrandTotal(currencyFormatter.format(rs.getBigDecimal("grand_total")));

        vendorInvoices.add(vendorInvoice);
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
    return vendorInvoices;
  }


  public Integer getCheckInQuantityDifference(Connection conn,
                                              Boolean isPOId,
                                              Integer theKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    Integer pendingQty = new Integer("0");
    Integer receiveQty = new Integer("0");
    Integer balanceQty = new Integer("0");

    try
    {
//--------------------------------------------------------------------------------
// Item pending and receive quantity values for the order.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemQuantityByOrder(?,?,?,?)}");
      cStmt.setBoolean(1, isPOId);
      cStmt.setInt(2, theKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": itemPendingQuantity : " + cStmt.getInt("itemPendingQuantity"));
      cat.debug(this.getClass().getName() + ": itemReceiveQuantity : " + cStmt.getInt("itemReceiveQuantity"));

      pendingQty = cStmt.getInt("itemPendingQuantity");
      receiveQty = cStmt.getInt("itemReceiveQuantity");

      cat.debug(this.getClass().getName() + " item balance Qty = " + pendingQty + " minus " + receiveQty );
      balanceQty = ( pendingQty - receiveQty );

//--------------------------------------------------------------------------------
// Service pending and receive quantity values for the order.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetServiceQuantityByOrder(?,?,?,?)}");
      cStmt.setBoolean(1, isPOId);
      cStmt.setInt(2, theKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": servicePendingQuantity : " + cStmt.getInt("servicePendingQuantity"));
      cat.debug(this.getClass().getName() + ": serviceReceiveQuantity : " + cStmt.getInt("serviceReceiveQuantity"));

      pendingQty = cStmt.getInt("servicePendingQuantity");
      receiveQty = cStmt.getInt("serviceReceiveQuantity");

      cat.debug(this.getClass().getName() + " service balance Qty = " + pendingQty + " minus " + receiveQty );
      balanceQty = balanceQty + ( pendingQty - receiveQty );
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCheckInQuantityDifference() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCheckInQuantityDifference() " + e.getMessage());
      }
    }
    return balanceQty;
  }


  public BigDecimal getCheckInAmountDifference(Connection conn,
                                               Boolean isPOId,
                                               Integer theKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    BigDecimal lineItemAmount = new BigDecimal("0");
    BigDecimal lineServiceAmount = new BigDecimal("0");
    BigDecimal otherAmount = new BigDecimal("0");
    BigDecimal totalAmount = new BigDecimal("0");
    BigDecimal balanceAmount = new BigDecimal("0");

    try {
//--------------------------------------------------------------------------------
// Item pending and receive quantity values for the order.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemAmountByOrder(?,?,?,?,?)}");
      cStmt.setBoolean(1, isPOId);
      cStmt.setInt(2, theKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": lineItemAmount : " + cStmt.getBigDecimal("lineItemAmount"));
      cat.debug(this.getClass().getName() + ": otherAmount : " + cStmt.getBigDecimal("otherAmount"));
      cat.debug(this.getClass().getName() + ": totalAmount : " + cStmt.getBigDecimal("totalAmount"));

      lineItemAmount = cStmt.getBigDecimal("lineItemAmount");
      otherAmount = cStmt.getBigDecimal("otherAmount");
      totalAmount = cStmt.getBigDecimal("totalAmount");

//--------------------------------------------------------------------------------
// Get the checked in service amounts the order.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetServiceAmountByOrder(?,?,?)}");
      cStmt.setBoolean(1, isPOId);
      cStmt.setInt(2, theKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": lineServiceAmount : " + cStmt.getBigDecimal("lineServiceAmount"));

      lineServiceAmount = cStmt.getBigDecimal("lineServiceAmount");

      cat.debug(this.getClass().getName() + ": ServiceAmount = " + lineServiceAmount.toString());

      balanceAmount = lineItemAmount.add(otherAmount);
      balanceAmount = balanceAmount.add(lineServiceAmount);
      cat.debug(this.getClass().getName() + ": minus = " + balanceAmount.toString());
      balanceAmount = totalAmount.subtract(balanceAmount);

    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCheckInAmountDifference() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCheckInAmountDifference() " + e.getMessage());
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
// Pass along the state and message values from the PurchaseOrderSetState
//--------------------------------------------------------------------------------
      String popupMessage = "";
      if (request.getAttribute("popupMessage") != null) {
        popupMessage = request.getAttribute("popupMessage").toString();
        request.setAttribute("popupMessage", popupMessage);
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        PurchaseOrderDO formDO = buildPurchaseOrderForm(request, theKey);
        formDO.setPermissionStatus(usToken);
//--------------------------------------------------------------------------------
// Special scenario to make sure that only the new P.O. is editable.
// Set the disableEdit attribute default to true for P.O.
//--------------------------------------------------------------------------------
        if (formDO.getOrderStatus().equalsIgnoreCase(EnumPurchaseOrderStatus.N.getValue()) && (usToken.compareToIgnoreCase(getUserStateService().getLocked()) == 0))
          request.setAttribute("disableEdit", false);
        else
          request.setAttribute(getUserStateService().getDisableEdit(), true);
        request.getSession().setAttribute("purchaseorder", formDO);
        PurchaseOrderForm formStr = new PurchaseOrderForm();
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

      // Get all the vendor invoices associated with this P.O.
      ArrayList vendorInvoices = null;
      vendorInvoices = getVendorInvoice(request, theKey);

      // Set the target to failure
      if ( vendorInvoices == null ) {
        target = "login";
      }
      else {
        if (!(vendorInvoices.isEmpty())) {
          request.setAttribute("vendorinvoices",vendorInvoices);
        }
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