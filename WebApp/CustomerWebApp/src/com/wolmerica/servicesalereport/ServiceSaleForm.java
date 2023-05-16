/*
 * ServiceSaleForm.java
 *
 * Created on August 21, 2006, 3:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/27/2005 Implement tools.formatter library.
 */

package com.wolmerica.servicesalereport;

/**
 *
 * @author Richard
 */
import com.wolmerica.customer.CustomerActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.Date;

public class ServiceSaleForm extends MasterForm {

  private String invoiceKey;
  private String invoiceDate;
  private String invoiceYear;
  private String invoiceMonthName;
  private String invoiceNumber;
  private String serviceName;
  private String priceTypeName;
  private String orderQty;
  private String thePrice;
  private String discountAmount;
  private String salesTax;
  private String handlingCost;
  private String serviceTotal;

  public void setInvoiceKey(String invoiceKey) {
    this.invoiceKey = invoiceKey;
  }

  public String getInvoiceKey() {
    return invoiceKey;
  }

  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceYear(String invoiceYear) {
    this.invoiceYear = invoiceYear;
  }

  public String getInvoiceYear() {
    return invoiceYear;
  }

  public void setInvoiceMonthName(String invoiceMonthName) {
    this.invoiceMonthName = invoiceMonthName;
  }

  public String getInvoiceMonthName() {
    return invoiceMonthName;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setPriceTypeName(String priceTypeName) {
    this.priceTypeName = priceTypeName;
  }

  public String getPriceTypeName() {
    return priceTypeName;
  }

 public void setOrderQty(String orderQty) {
    this.orderQty = orderQty;
  }

  public String getOrderQty() {
    return orderQty;
  }

  public void setThePrice(String thePrice) {
    this.thePrice = thePrice;
  }

  public String getThePrice() {
    return thePrice;
  }

  public void setDiscountAmount(String discountAmount) {
    this.discountAmount = discountAmount;
  }

  public String getDiscountAmount() {
    return discountAmount;
  }

  public void setSalesTax(String salesTax) {
    this.salesTax = salesTax;
  }

  public String getSalesTax() {
    return salesTax;
  }

  public void setHandlingCost(String handlingCost) {
    this.handlingCost = handlingCost;
  }

  public String getHandlingCost() {
    return handlingCost;
  }

  public void setServiceTotal(String serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public String getServiceTotal() {
    return serviceTotal;
  }

  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();

    CustomerActionMapping customerMapping =
      (CustomerActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( customerMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("ACCTKEY") == null ) {

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
