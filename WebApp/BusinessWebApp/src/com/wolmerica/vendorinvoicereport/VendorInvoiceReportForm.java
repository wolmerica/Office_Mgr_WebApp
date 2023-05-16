/*
 * VendorInvoiceReportForm.java
 *
 * Created on February 21, 2007, 10:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoicereport;

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

public class VendorInvoiceReportForm extends MasterForm {

  private String purchaseOrderKey = null;
  private String vendorInvoiceKey = null;
  private String vendorKey = null;
  private String vendorName = "";
  private String invoiceDate = null;
  private String invoiceNumber = "";
  private String noteLine1 = "";
  private String noteLine2 = "";
  private String noteLine3 = "";
  private String subTotal = new String("0");
  private String salesTaxCost = new String("0");
  private String serviceTaxCost = new String("0");
  private String handlingCost = new String("0");
  private String invoiceTotal = new String("0");

  public void setPurchaseOrderKey(String purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public String getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setVendorInvoiceKey(String vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public String getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setVendorKey(String vendorKey) {
    this.vendorKey = vendorKey;
  }

  public String getVendorKey() {
    return vendorKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setNoteLine2(String noteLine2) {
    this.noteLine2 = noteLine2;
  }

  public String getNoteLine2() {
    return noteLine2;
  }

  public void setNoteLine3(String noteLine3) {
    this.noteLine3 = noteLine3;
  }

  public String getNoteLine3() {
    return noteLine3;
  }

  public void setSubTotal(String subTotal) {
    this.subTotal = subTotal;
  }

  public String getSubTotal(){
    return subTotal;
  }

  public void setSalesTaxCost(String salesTaxCost) {
    this.salesTaxCost = salesTaxCost;
  }

  public String getSalesTaxCost(){
    return salesTaxCost;
  }

  public void setServiceTaxCost(String serviceTaxCost) {
    this.serviceTaxCost = serviceTaxCost;
  }

  public String getServiceTaxCost(){
    return serviceTaxCost;
  }

  public void setHandlingCost(String handlingCost) {
    this.handlingCost = handlingCost;
  }

  public String getHandlingCost(){
    return handlingCost;
  }

  public void setInvoiceTotal(String invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public String getInvoiceTotal(){
    return invoiceTotal;
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

