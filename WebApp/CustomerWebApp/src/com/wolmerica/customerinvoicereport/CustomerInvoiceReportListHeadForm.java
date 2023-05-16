/*
 * CustomerInvoiceListReportHeadForm.java
 *
 * Created on February 16, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerInvoiceReportListHeadForm extends MasterForm {

  private String customerKey;
  private String clientName;
  private String attributeToEntity;
  private String sourceTypeKey;
  private String sourceKey;
  private String attributeToName;
  private String fromDate;
  private String toDate;
  private String recordCount;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String nextPage;
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
  private ArrayList customerInvoiceReportForm;

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

  public void setAttributeToEntity(String attributeToEntity) {
    this.attributeToEntity = attributeToEntity;
  }

  public String getAttributeToEntity() {
    return attributeToEntity;
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

  public void setFromDate(String fromDate) {
    this.fromDate = fromDate;
  }

  public String getFromDate() {
    return fromDate;
  }

  public void setToDate(String toDate) {
    this.toDate = toDate;
  }

  public String getToDate() {
    return toDate;
  }

  public void setRecordCount(String recordCount) {
    this.recordCount = recordCount;
  }

  public String getRecordCount() {
    return recordCount;
  }

  public void setFirstRecord(String firstRecord) {
    this.firstRecord = firstRecord;
  }

  public String getFirstRecord() {
    return firstRecord;
  }

  public void setLastRecord(String lastRecord) {
    this.lastRecord = lastRecord;
  }

  public String getLastRecord() {
    return lastRecord;
  }

  public void setPreviousPage(String previousPage ) {
    this.previousPage = previousPage;
  }

  public String getPreviousPage() {
    return previousPage;
  }

  public void setNextPage(String nextPage ) {
    this.nextPage = nextPage;
  }

  public String getNextPage() {
    return nextPage;
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

  public void setCustomerInvoiceReportForm(ArrayList customerInvoiceReportForm){
    this.customerInvoiceReportForm = customerInvoiceReportForm;
  }

  public ArrayList getCustomerInvoiceReportForm(){
    return customerInvoiceReportForm;
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

