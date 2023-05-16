/*
 * VendorInvoiceDO.java
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
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class VendorInvoiceDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer purchaseOrderKey = null;
  private Integer masterKey = new Integer("-1");
  private Integer genesisKey = new Integer("-1");
  private String purchaseOrderNumber = "";
  private String vendorName = "";
  private String attributeToEntity = "";
  private Byte sourceTypeKey = new Byte("-1");
  private Integer sourceKey = new Integer("-1");
  private String attributeToName = "";    
  private String invoiceNumber = "";
  private Date invoiceDate = null;
  private Date invoiceDueDate = null;
  private BigDecimal salesTaxCost = new BigDecimal("0");  
  private BigDecimal packagingCost = new BigDecimal("0");
  private BigDecimal freightCost = new BigDecimal("0");
  private BigDecimal miscellaneousCost = new BigDecimal("0");
  private BigDecimal invoiceTotal = new BigDecimal("0");
  private ArrayList taxMarkUpForm;
  private Byte salesTaxKey = new Byte("0");
  private BigDecimal salesTaxRate = new BigDecimal("0");  
  private BigDecimal taxableTotal = new BigDecimal("0");
  private BigDecimal nonTaxableTotal = new BigDecimal("0");
  private BigDecimal lineItemTotal = new BigDecimal("0");
  private BigDecimal serviceTotal = new BigDecimal("0");  
  private BigDecimal subTotal = new BigDecimal("0");    
  private Byte serviceTaxKey = new Byte("0");
  private BigDecimal serviceTaxRate = new BigDecimal("0");  
  private BigDecimal serviceTaxableTotal = new BigDecimal("0");
  private BigDecimal serviceNonTaxableTotal = new BigDecimal("0");  
  private BigDecimal serviceTaxCost = new BigDecimal("0");  
  private Boolean carryFactorId = false;
  private Boolean activeId = true;
  private Boolean creditId = false;
  private Boolean allowCreditId = false;  
  private Integer balanceQty = new Integer("0");    
  private BigDecimal balanceAmount = new BigDecimal("0");
  private String permissionStatus = "";
  private Integer attachmentCount = null;
  private String rmaNumber;
  private Integer logisticsCount = null;  
  private String noteLine1 = "";  
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setPurchaseOrderKey(Integer purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public Integer getPurchaseOrderKey() {
    return purchaseOrderKey;
  }
 
  public void setMasterKey(Integer masterKey) {
    this.masterKey = masterKey;
  }

  public Integer getMasterKey() {
    return masterKey;
  }  

  public void setGenesisKey(Integer genesisKey) {
    this.genesisKey = genesisKey;
  }

  public Integer getGenesisKey() {
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

  public void setSourceTypeKey(Byte sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public Byte getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceKey(Integer sourceKey) {
    this.sourceKey = sourceKey;
  }

  public Integer getSourceKey() {
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

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDueDate(Date invoiceDueDate) {
    this.invoiceDueDate = invoiceDueDate;
  }

  public Date getInvoiceDueDate() {
    return invoiceDueDate;
  }
  
  public void setSalesTaxCost(BigDecimal salesTaxCost) {
    this.salesTaxCost = salesTaxCost;
  }

  public BigDecimal getSalesTaxCost(){
    return salesTaxCost;
  }  

  public void setPackagingCost(BigDecimal packagingCost) {
    this.packagingCost = packagingCost;
  }

  public BigDecimal getPackagingCost(){
    return packagingCost;
  }

  public void setFreightCost(BigDecimal freightCost) {
    this.freightCost = freightCost;
  }

  public BigDecimal getFreightCost(){
    return freightCost;
  }

  public void setMiscellaneousCost(BigDecimal miscellaneousCost) {
    this.miscellaneousCost = miscellaneousCost;
  }

  public BigDecimal getMiscellaneousCost(){
    return miscellaneousCost;
  }

  public void setInvoiceTotal(BigDecimal invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public BigDecimal getInvoiceTotal(){
    return invoiceTotal;
  }

  public void setTaxMarkUpForm(ArrayList taxMarkUpList){
  this.taxMarkUpForm = taxMarkUpList;
  }

  public ArrayList getTaxMarkUpForm(){
  return taxMarkUpForm;
  }

   public void setSalesTaxKey(Byte salesTaxKey) {
    this.salesTaxKey = salesTaxKey;
  }

  public Byte getSalesTaxKey() {
    return salesTaxKey;
  }
  
  public void setSalesTaxRate(BigDecimal salesTaxRate) {
    this.salesTaxRate = salesTaxRate;
  }

  public BigDecimal getSalesTaxRate(){
    return salesTaxRate;
  }  
  
  public void setTaxableTotal(BigDecimal taxableTotal) {
    this.taxableTotal = taxableTotal;
  }

  public BigDecimal getTaxableTotal(){
    return taxableTotal;
  }
  
  public void setNonTaxableTotal(BigDecimal nonTaxableTotal) {
    this.nonTaxableTotal = nonTaxableTotal;
  }

  public BigDecimal getNonTaxableTotal(){
    return nonTaxableTotal;
  }    
    
  public void setLineItemTotal(BigDecimal lineItemTotal) {
    this.lineItemTotal = lineItemTotal;
  }

  public BigDecimal getLineItemTotal(){
    return lineItemTotal;
  }

  public void setServiceTotal(BigDecimal serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public BigDecimal getServiceTotal(){
    return serviceTotal;
  }
  
  public void setSubTotal(BigDecimal subTotal) {
    this.subTotal = subTotal;
  }

  public BigDecimal getSubTotal(){
    return subTotal;
  }
  
  public void setServiceTaxKey(Byte serviceTaxKey) {
    this.serviceTaxKey = serviceTaxKey;
  }

  public Byte getServiceTaxKey() {
    return serviceTaxKey;
  }
  
  public void setServiceTaxRate(BigDecimal serviceTaxRate) {
    this.serviceTaxRate = serviceTaxRate;
  }

  public BigDecimal getServiceTaxRate(){
    return serviceTaxRate;
  }  

  public void setServiceTaxableTotal(BigDecimal serviceTaxableTotal) {
    this.serviceTaxableTotal = serviceTaxableTotal;
  }

  public BigDecimal getServiceTaxableTotal(){
    return serviceTaxableTotal;
  }

  public void setServiceNonTaxableTotal(BigDecimal serviceNonTaxableTotal) {
    this.serviceNonTaxableTotal = serviceNonTaxableTotal;
  }

  public BigDecimal getServiceNonTaxableTotal(){
    return serviceNonTaxableTotal;
  }
  
  public void setServiceTaxCost(BigDecimal serviceTaxCost) {
    this.serviceTaxCost = serviceTaxCost;
  }

  public BigDecimal getServiceTaxCost(){
    return serviceTaxCost;
  }  
  
  public void setCarryFactorId(Boolean carryFactorId) {
    this.carryFactorId = carryFactorId;
  }

  public Boolean getCarryFactorId() {
    return carryFactorId;
  }

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }
  
  public void setCreditId(Boolean creditId) {
    this.creditId = creditId;
  }

  public Boolean getCreditId() {
    return creditId;
  }

  public void setAllowCreditId(Boolean allowCreditId) {
    this.allowCreditId = allowCreditId;
  }

  public Boolean getAllowCreditId() {
    return allowCreditId;
  }  

  public void setBalanceQty(Integer balanceQty) {
    this.balanceQty = balanceQty;
  }

  public Integer getBalanceQty() {
    return balanceQty;
  }    
  
  public void setBalanceAmount(BigDecimal balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public BigDecimal getBalanceAmount() {
    return balanceAmount;
  }  
  
  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }    
  
  public void setAttachmentCount(Integer attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public Integer getAttachmentCount() {
    return attachmentCount;
  }
  
  public void setRmaNumber(String rmaNumber) {
    this.rmaNumber = rmaNumber;
  }

  public String getRmaNumber() {
    return rmaNumber;
  }  

  public void setLogisticsCount(Integer logisticsCount) {
    this.logisticsCount = logisticsCount;
  }

  public Integer getLogisticsCount() {
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

  public void setCreateStamp(Timestamp createStamp) {
    this.createStamp = createStamp;
  }

  public Timestamp getCreateStamp() {
    return createStamp;
  }

  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getUpdateUser() {
    return updateUser;
  }

  public void setUpdateStamp(Timestamp updateStamp) {
    this.updateStamp = updateStamp;
  }

  public Timestamp getUpdateStamp() {
    return updateStamp;
  }
}

