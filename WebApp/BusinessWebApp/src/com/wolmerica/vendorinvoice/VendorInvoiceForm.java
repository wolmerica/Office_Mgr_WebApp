/*
 * VendorInvoiceForm.java
 *
 * Created on October 23, 2005, 11:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoice;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;

public class VendorInvoiceForm extends MasterForm {

  private String key;
  private String purchaseOrderKey;
  private String masterKey;
  private String genesisKey;
  private String purchaseOrderNumber;
  private String vendorName;
  private String attributeToEntity;
  private String sourceTypeKey;
  private String sourceKey;
  private String attributeToName;    
  private String invoiceNumber;
  private String invoiceDate;
  private String invoiceDueDate;
  private String salesTaxCost;
  private String packagingCost;
  private String freightCost;
  private String miscellaneousCost;
  private String invoiceTotal;
  private ArrayList taxMarkUpForm;
  private String salesTaxKey;
  private String salesTaxRate;  
  private String taxableTotal;
  private String nonTaxableTotal;
  private String serviceTaxKey;
  private String serviceTaxRate;
  private String serviceTaxableTotal;
  private String serviceNonTaxableTotal;
  private String serviceTaxCost;
  private String lineItemTotal;
  private String serviceTotal;
  private String subTotal;
  private String carryFactorId;
  private String activeId;
  private String creditId;
  private String allowCreditId;
  private String balanceQty;
  private String balanceAmount;
  private String permissionStatus;
  private String attachmentCount;
  private String rmaNumber;
  private String logisticsCount;  
  private String noteLine1;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public VendorInvoiceForm() {
    addRequiredFields(new String[] { "invoiceNumber", "invoiceDate", "invoiceDueDate",
                                     "salesTaxCost", "packagingCost", "freightCost",
                                     "miscellaneousCost", "invoiceTotal"});
    addRange("invoiceDate", null, new Date());
    addRange("salesTaxCost", new BigDecimal("-9999.99"), new BigDecimal("9999.99"));
    addRange("packagingCost", new BigDecimal("0.00"), new BigDecimal("99999.99"));
    addRange("freightCost", new BigDecimal("0.00"), new BigDecimal("99999.99"));
    addRange("miscellaneousCost", new BigDecimal("0.00"), new BigDecimal("99999.99"));
    addRange("invoiceTotal", new BigDecimal("-99999.99"), new BigDecimal("99999.99"));
    addRange("salesTaxId", new Byte("0"), new Byte("1"));
  }

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

  public void setMasterKey(String masterKey) {
    this.masterKey = masterKey;
  }

  public String getMasterKey() {
    return masterKey;
  }

  public void setGenesisKey(String genesisKey) {
    this.genesisKey = genesisKey;
  }

  public String getGenesisKey() {
    return genesisKey;
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

  public void setSalesTaxCost(String salesTaxCost) {
    this.salesTaxCost = salesTaxCost;
  }

  public String getSalesTaxCost(){
    return salesTaxCost;
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

  public void setInvoiceTotal(String invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public String getInvoiceTotal(){
    return invoiceTotal;
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

  public void setSalesTaxRate(String salesTaxRate) {
    this.salesTaxRate = salesTaxRate;
  }

  public String getSalesTaxRate(){
    return salesTaxRate;
  }
  
  public void setTaxableTotal(String taxableTotal) {
    this.taxableTotal = taxableTotal;
  }

  public String getTaxableTotal(){
    return taxableTotal;
  }

  public void setNonTaxableTotal(String nonTaxableTotal) {
    this.nonTaxableTotal = nonTaxableTotal;
  }

  public String getNonTaxableTotal(){
    return nonTaxableTotal;
  }

  public void setLineItemTotal(String lineItemTotal) {
    this.lineItemTotal = lineItemTotal;
  }

  public String getLineItemTotal(){
    return lineItemTotal;
  }
  
  public void setServiceTotal(String serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public String getServiceTotal(){
    return serviceTotal;
  }  
  
  public void setSubTotal(String subTotal) {
    this.subTotal = subTotal;
  }

  public String getSubTotal(){
    return subTotal;
  }  
  
  public void setServiceTaxKey(String serviceTaxKey) {
    this.serviceTaxKey = serviceTaxKey;
  }

  public String getServiceTaxKey() {
    return serviceTaxKey;
  }
  
  public void setServiceTaxRate(String serviceTaxRate) {
    this.serviceTaxRate = serviceTaxRate;
  }

  public String getServiceTaxRate(){
    return serviceTaxRate;
  }  

  public void setServiceTaxableTotal(String serviceTaxableTotal) {
    this.serviceTaxableTotal = serviceTaxableTotal;
  }

  public String getServiceTaxableTotal(){
    return serviceTaxableTotal;
  }
  
  public void setServiceNonTaxableTotal(String serviceNonTaxableTotal) {
    this.serviceNonTaxableTotal = serviceNonTaxableTotal;
  }

  public String getServiceNonTaxableTotal(){
    return serviceNonTaxableTotal;
  }  

  public void setServiceTaxCost(String serviceTaxCost) {
    this.serviceTaxCost = serviceTaxCost;
  }

  public String getServiceTaxCost(){
    return serviceTaxCost;
  }  

  public void setCarryFactorId(String carryFactorId) {
    this.carryFactorId = carryFactorId;
  }

  public String getCarryFactorId() {
    return carryFactorId;
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

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }

  public void setAttachmentCount(String attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public String getAttachmentCount() {
    return attachmentCount;
  }

  public void setRmaNumber(String rmaNumber) {
    this.rmaNumber = rmaNumber;
  }

  public String getRmaNumber() {
    return rmaNumber;
  }
  
  public void setLogisticsCount(String logisticsCount) {
    this.logisticsCount = logisticsCount;
  }

  public String getLogisticsCount() {
    return logisticsCount;
  }    

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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

    @Override
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

