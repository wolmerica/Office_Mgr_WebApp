/*
 * VendorInvoiceListForm.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoice;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;

public class VendorInvoiceListForm extends MasterForm {

  private String key;
  private String purchaseOrderKey;
  private String purchaseOrderNumber;
  private String vendorName;
  private String invoiceNumber;
  private String invoiceDate;
  private String invoiceDueDate;
  private String invoiceTotal;
  private String salesTaxId;
  private String activeId;
  private String masterInvoiceId;
  private String directShipId;
  private String customerInvoiceCount;
  private String balanceQty;
  private String balanceAmount;

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setPurchaseOrderKey(String purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public String getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDueDate(String invoiceDueDate) {
    this.invoiceDueDate = invoiceDueDate;
  }

  public String getInvoiceDueDate() {
    return invoiceDueDate;
  }

  public void setInvoiceTotal(String invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }


  public String getInvoiceTotal(){
    return invoiceTotal;
  }

  public void setSalesTaxId(String salesTaxId) {
    this.salesTaxId = salesTaxId;
  }

  public String getSalesTaxId() {
    return salesTaxId;
  }

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
  }

  public void setMasterInvoiceId(String masterInvoiceId) {
    this.masterInvoiceId = masterInvoiceId;
  }

  public String getMasterInvoiceId() {
    return masterInvoiceId;
  }

  public void setDirectShipId(String directShipId) {
    this.directShipId = directShipId;
  }

  public String getDirectShipId() {
    return directShipId;
  }

  public void setCustomerInvoiceCount(String customerInvoiceCount) {
    this.customerInvoiceCount = customerInvoiceCount;
  }

  public String getCustomerInvoiceCount() {
    return customerInvoiceCount;
  }

  public void setBalanceQty(String balanceQty) {
    this.balanceQty = balanceQty;
  }

  public String getBalanceQty() {
    return balanceQty;
  }

  public void setBalanceAmount(String balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public String getBalanceAmount() {
    return balanceAmount;
  }

  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();

    EmployeesActionMapping employeesMapping = (EmployeesActionMapping)mapping;

    // Does this action require the user to login
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {

        // return null to force action to handle login
        // error
        return null;
      }
    }

    errors = super.validate(mapping, request);

    // Post a global message instructing user to clean up
    // validation errors and resubmit
    if (errors.size() > 0) {
      ActionMessage message =
          new ActionMessage("message.validation");
      ActionMessages messages = new ActionMessages();
          messages.add(ActionMessages.GLOBAL_MESSAGE, message);
      request.setAttribute(Globals.MESSAGE_KEY, messages);
    }

    // Set the disableEdit attribute to false when errors encountered.
    request.setAttribute("disableEdit", false);

    return errors;
  }
}

