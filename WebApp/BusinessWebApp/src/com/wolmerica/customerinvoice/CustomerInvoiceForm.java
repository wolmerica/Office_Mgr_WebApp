/*
 * CustomerInvoiceForm.java
 *
 * Created on November 01, 2005, 6:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.customerinvoice;

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
import java.util.ArrayList;
import java.math.BigDecimal;

public class CustomerInvoiceForm extends MasterForm {

  private String key;
  private String vendorInvoiceKey;
  private String vendorName;
  private String customerKey;
  private String clientName;
  private String scenarioKey;
  private ArrayList customerTypeForm;
  private String customerTypeKey;
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
  private ArrayList taxMarkUpForm;
  private String salesTaxKey;
  private String taxableTotal;
  private String salesTaxRate;
  private String salesTaxCost;
  private String serviceTaxKey;
  private String serviceTaxableTotal;
  private String serviceTaxRate;
  private String serviceTaxCost;
  private String debitAdjustment;
  private String packagingCost;
  private String freightCost;
  private String miscellaneousCost;
  private String creditAdjustment;
  private String invoiceTotal;
  private String activeId;
  private String creditId;
  private String allowCreditId;
  private String adjustmentId;
  private String allowAdjustmentId;
  private String genesisKey;
  private String attributeToEntity;
  private String sourceTypeKey;
  private String sourceKey;
  private String attributeToName;
  private String scheduleKey;  
  private String logisticsCount;  
  private String permissionStatus;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public CustomerInvoiceForm() {
    addRequiredFields(new String[] { "customerKey",
                    "customerTypeKey","salesTaxRate",
                    "salesTaxCost","debitAdjustment",
                    "packagingCost","freightCost", "miscellaneousCost",
                    "creditAdjustment", "invoiceTotal", "salesTaxKey", "ActiveId" } );
    addRange("salesTaxCost", new BigDecimal("-9999.99"), new BigDecimal("9999.99"));
    addRange("debitAdjustment", new BigDecimal("0.00"), new BigDecimal("9999.99"));
    addRange("packagingCost", new BigDecimal("0.00"), new BigDecimal("9999.99"));
    addRange("freightCost", new BigDecimal("0.00"), new BigDecimal("9999.99"));
    addRange("miscellaneousCost", new BigDecimal("0.00"), new BigDecimal("9999.99"));
    addRange("creditAdjustment", new BigDecimal("-9999.99"), new BigDecimal("0.00"));
    addRange("invoiceTotal", new BigDecimal("-99999.99"), new BigDecimal("99999.99"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
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

  public void setCustomerTypeForm(ArrayList customerTypeList){
  this.customerTypeForm=customerTypeList;
  }

  public ArrayList getCustomerTypeForm(){
  return customerTypeForm;
  }

  public void setCustomerTypeKey(String customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public String getCustomerTypeKey() {
    return customerTypeKey;
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

  public void setTaxMarkUpForm(ArrayList taxMarkUpList){
  this.taxMarkUpForm = taxMarkUpList;
  }

  public ArrayList getTaxMarkUpForm(){
  return taxMarkUpForm;
  }

   public void setSalesTaxKey(String salesTaxKey) {
    this.salesTaxKey = salesTaxKey;
  }

  public String getSalesTaxKey() {
    return salesTaxKey;
  }

  public void setTaxableTotal(String taxableTotal) {
    this.taxableTotal = taxableTotal;
  }

  public String getTaxableTotal(){
    return taxableTotal;
  }

  public void setSalesTaxRate(String salesTaxRate) {
    this.salesTaxRate = salesTaxRate;
  }

  public String getSalesTaxRate(){
    return salesTaxRate;
  }

  public void setSalesTaxCost(String salesTaxCost) {
    this.salesTaxCost = salesTaxCost;
  }

  public String getSalesTaxCost(){
    return salesTaxCost;
  }

  public void setServiceTaxKey(String serviceTaxKey) {
    this.serviceTaxKey = serviceTaxKey;
  }

  public String getServiceTaxKey() {
    return serviceTaxKey;
  }

  public void setServiceTaxableTotal(String serviceTaxableTotal) {
    this.serviceTaxableTotal = serviceTaxableTotal;
  }

  public String getServiceTaxableTotal(){
    return serviceTaxableTotal;
  }

  public void setServiceTaxRate(String serviceTaxRate) {
    this.serviceTaxRate = serviceTaxRate;
  }

  public String getServiceTaxRate(){
    return serviceTaxRate;
  }

  public void setServiceTaxCost(String serviceTaxCost) {
    this.serviceTaxCost = serviceTaxCost;
  }

  public String getServiceTaxCost(){
    return serviceTaxCost;
  }

  public void setDebitAdjustment(String debitAdjustment) {
    this.debitAdjustment = debitAdjustment;
  }

  public String getDebitAdjustment(){
    return debitAdjustment;
  }

  public void setPackagingCost(String packagingCost) {
    this.packagingCost = packagingCost;
  }

  public String getPackagingCost(){
    return packagingCost;
  }

  public void setFreightCost(String freightCost) {
    this.freightCost = freightCost;
  }

  public String getFreightCost(){
    return freightCost;
  }

  public void setMiscellaneousCost(String miscellaneousCost) {
    this.miscellaneousCost = miscellaneousCost;
  }

  public String getMiscellaneousCost(){
    return miscellaneousCost;
  }

  public void setCreditAdjustment(String creditAdjustment) {
    this.creditAdjustment = creditAdjustment;
  }

  public String getCreditAdjustment() {
    return creditAdjustment;
  }

  public void setInvoiceTotal(String invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public String getInvoiceTotal(){
    return invoiceTotal;
  }

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
  }

  public void setCreditId(String creditId) {
    this.creditId = creditId;
  }

  public String getCreditId() {
    return creditId;
  }

  public void setAllowCreditId(String allowCreditId) {
    this.allowCreditId = allowCreditId;
  }

  public String getAllowCreditId() {
    return allowCreditId;
  }

  public void setAdjustmentId(String adjustmentId) {
    this.adjustmentId = adjustmentId;
  }

  public String getAdjustmentId() {
    return adjustmentId;
  }

  public void setAllowAdjustmentId(String allowAdjustmentId) {
    this.allowAdjustmentId = allowAdjustmentId;
  }

  public String getAllowAdjustmentId() {
    return allowAdjustmentId;
  }

  public void setGenesisKey(String genesisKey) {
    this.genesisKey = genesisKey;
  }

  public String getGenesisKey() {
    return genesisKey;
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

  public void setScheduleKey(String scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public String getScheduleKey() {
    return scheduleKey;
  }
    
  public void setLogisticsCount(String logisticsCount) {
    this.logisticsCount = logisticsCount;
  }

  public String getLogisticsCount() {
    return logisticsCount;
  }  
  
  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateStamp(String createStamp) {
    this.createStamp = createStamp;
  }

  public String getCreateStamp() {
    return createStamp;
  }

  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getUpdateUser() {
    return updateUser;
  }

  public void setUpdateStamp(String updateStamp) {
    this.updateStamp = updateStamp;
  }

  public String getUpdateStamp() {
    return updateStamp;
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

