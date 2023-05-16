/*
 * CustomerInvoiceReportForm.java
 *
 * Created on February 16, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.customerinvoicereport;

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
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.math.BigDecimal;

public class CustomerInvoiceReportForm extends MasterForm {

  private String customerInvoiceKey;
  private String vendorInvoiceKey;
  private String vendorName;
  private String customerKey;
  private String clientName;
  private String scenarioKey;
  private String sourceTypeKey;
  private String sourceKey;
  private String attributeToName;
  private String dateOfService;
  private String customerInvoiceNumber;
  private String noteLine1;
  private String noteLine2;
  private String noteLine3;
  private String itemCount;
  private String itemGrossAmount;
  private String itemDiscountAmount;
  private String itemNetAmount;
  private String serviceGrossAmount;
  private String serviceDiscountAmount;
  private String serviceNetAmount;
  private String subTotal;
  private String grossProfitAmount;
  private String netProfitAmount;
  private String salesTaxCost;
  private String serviceTaxCost;
  private String handlingCost;
  private String invoiceTotal;

  public void setCustomerInvoiceKey(String customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public String getCustomerInvoiceKey() {
    return customerInvoiceKey;
  }

  public void setVendorInvoiceKey(String vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public String getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setCustomerKey(String customerKey) {
    this.customerKey = customerKey;
  }

  public String getCustomerKey() {
    return customerKey;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setScenarioKey(String scenarioKey) {
    this.scenarioKey = scenarioKey;
  }

  public String getScenarioKey() {
    return scenarioKey;
  }

  public void setSourceTypeKey(String sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public String getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceKey(String sourceKey) {
    this.sourceKey = sourceKey;
  }

  public String getSourceKey() {
    return sourceKey;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
  }

  public void setDateOfService(String dateOfService) {
    this.dateOfService = dateOfService;
  }

  public String getDateOfService() {
    return dateOfService;
  }

  public void setCustomerInvoiceNumber(String customerInvoiceNumber) {
    this.customerInvoiceNumber = customerInvoiceNumber;
  }

  public String getCustomerInvoiceNumber() {
    return customerInvoiceNumber;
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

  public void setItemCount(String itemCount) {
    this.itemCount = itemCount;
  }

  public String getItemCount(){
    return itemCount;
  }

  public void setItemGrossAmount(String itemGrossAmount) {
    this.itemGrossAmount = itemGrossAmount;
  }

  public String getItemGrossAmount(){
    return itemGrossAmount;
  }

  public void setItemDiscountAmount(String itemDiscountAmount) {
    this.itemDiscountAmount = itemDiscountAmount;
  }

  public String getItemDiscountAmount(){
    return itemDiscountAmount;
  }

  public void setItemNetAmount(String itemNetAmount) {
    this.itemNetAmount = itemNetAmount;
  }

  public String getItemNetAmount(){
    return itemNetAmount;
  }

  public void setServiceGrossAmount(String serviceGrossAmount) {
    this.serviceGrossAmount = serviceGrossAmount;
  }

  public String getServiceGrossAmount(){
    return serviceGrossAmount;
  }

  public void setServiceDiscountAmount(String serviceDiscountAmount) {
    this.serviceDiscountAmount = serviceDiscountAmount;
  }

  public String getServiceDiscountAmount(){
    return serviceDiscountAmount;
  }

  public void setServiceNetAmount(String serviceNetAmount) {
    this.serviceNetAmount = serviceNetAmount;
  }

  public String getServiceNetAmount(){
    return serviceNetAmount;
  }

  public void setSubTotal(String subTotal) {
    this.subTotal = subTotal;
  }

  public String getSubTotal(){
    return subTotal;
  }

  public void setGrossProfitAmount(String grossProfitAmount) {
    this.grossProfitAmount = grossProfitAmount;
  }

  public String getGrossProfitAmount(){
    return grossProfitAmount;
  }

  public void setNetProfitAmount(String netProfitAmount) {
    this.netProfitAmount = netProfitAmount;
  }

  public String getNetProfitAmount(){
    return netProfitAmount;
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

    CustomerActionMapping customerMapping = (CustomerActionMapping)mapping;

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

