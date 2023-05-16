/*
 * CustomerInvoiceDO.java
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
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CustomerInvoiceDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer vendorInvoiceKey = new Integer("-1");
  private String vendorName = "";
  private Integer customerKey = null;
  private String clientName = "";
  private Byte scenarioKey = new Byte("-1");
  private ArrayList customerTypeForm;
  private Byte customerTypeKey = new Byte("1");
  private String customerInvoiceNumber = "";
  private String noteLine1 = "";
  private String noteLine2 = "";
  private String noteLine3 = "";
  private Integer itemCount = new Integer("0");
  private BigDecimal itemGrossAmount = new BigDecimal("0");
  private BigDecimal itemDiscountAmount = new BigDecimal("0");
  private BigDecimal itemNetAmount = new BigDecimal("0");
  private BigDecimal serviceGrossAmount = new BigDecimal("0");
  private BigDecimal serviceDiscountAmount = new BigDecimal("0");
  private BigDecimal serviceNetAmount = new BigDecimal("0");
  private BigDecimal subTotal = new BigDecimal("0");
  private BigDecimal grossProfitAmount = new BigDecimal("0");
  private BigDecimal netProfitAmount = new BigDecimal("0");
  private ArrayList taxMarkUpForm;
  private Byte salesTaxKey = new Byte("0");
  private BigDecimal taxableTotal = new BigDecimal("0");
  private BigDecimal salesTaxRate = new BigDecimal("0");
  private BigDecimal salesTaxCost = new BigDecimal("0");
  private Byte serviceTaxKey = new Byte("0");
  private BigDecimal serviceTaxableTotal = new BigDecimal("0");
  private BigDecimal serviceTaxRate = new BigDecimal("0");
  private BigDecimal serviceTaxCost = new BigDecimal("0");
  private BigDecimal debitAdjustment = new BigDecimal("0");
  private BigDecimal packagingCost = new BigDecimal("0");
  private BigDecimal freightCost = new BigDecimal("0");
  private BigDecimal miscellaneousCost = new BigDecimal("0");
  private BigDecimal creditAdjustment = new BigDecimal("0");
  private BigDecimal invoiceTotal = new BigDecimal("0");
  private Boolean activeId = true;
  private Boolean creditId = false;
  private Boolean allowCreditId = false;
  private Boolean adjustmentId = false;
  private Boolean allowAdjustmentId = false;
  private Integer genesisKey = new Integer("-1");
  private String attributeToEntity = "";
  private Byte sourceTypeKey = new Byte("-1");
  private Integer sourceKey = new Integer("-1");
  private String attributeToName = "";
  private Integer scheduleKey = new Integer("-1");  
  private Integer logisticsCount = null;      
  private String permissionStatus = "";
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

  public void setVendorInvoiceKey(Integer vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public Integer getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setCustomerKey(Integer customerKey) {
    this.customerKey = customerKey;
  }

  public Integer getCustomerKey() {
    return customerKey;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setScenarioKey(Byte scenarioKey) {
    this.scenarioKey = scenarioKey;
  }

  public Byte getScenarioKey() {
    return scenarioKey;
  }

  public void setCustomerTypeForm(ArrayList customerTypeList){
  this.customerTypeForm = customerTypeList;
  }

  public ArrayList getCustomerTypeForm(){
  return customerTypeForm;
  }

  public void setCustomerTypeKey(Byte customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public Byte getCustomerTypeKey() {
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

  public void setItemCount(Integer itemCount) {
    this.itemCount = itemCount;
  }

  public Integer getItemCount(){
    return itemCount;
  }

  public void setItemGrossAmount(BigDecimal itemGrossAmount) {
    this.itemGrossAmount = itemGrossAmount;
  }

  public BigDecimal getItemGrossAmount(){
    return itemGrossAmount;
  }

  public void setItemDiscountAmount(BigDecimal itemDiscountAmount) {
    this.itemDiscountAmount = itemDiscountAmount;
  }

  public BigDecimal getItemDiscountAmount(){
    return itemDiscountAmount;
  }

  public void setItemNetAmount(BigDecimal itemNetAmount) {
    this.itemNetAmount = itemNetAmount;
  }

  public BigDecimal getItemNetAmount(){
    return itemNetAmount;
  }

  public void setServiceGrossAmount(BigDecimal serviceGrossAmount) {
    this.serviceGrossAmount = serviceGrossAmount;
  }

  public BigDecimal getServiceGrossAmount(){
    return serviceGrossAmount;
  }

  public void setServiceDiscountAmount(BigDecimal serviceDiscountAmount) {
    this.serviceDiscountAmount = serviceDiscountAmount;
  }

  public BigDecimal getServiceDiscountAmount(){
    return serviceDiscountAmount;
  }

  public void setServiceNetAmount(BigDecimal serviceNetAmount) {
    this.serviceNetAmount = serviceNetAmount;
  }

  public BigDecimal getServiceNetAmount(){
    return serviceNetAmount;
  }

  public void setSubTotal(BigDecimal subTotal) {
    this.subTotal = subTotal;
  }

  public BigDecimal getSubTotal(){
    return subTotal;
  }

  public void setGrossProfitAmount(BigDecimal grossProfitAmount) {
    this.grossProfitAmount = grossProfitAmount;
  }

  public BigDecimal getGrossProfitAmount(){
    return grossProfitAmount;
  }

  public void setNetProfitAmount(BigDecimal netProfitAmount) {
    this.netProfitAmount = netProfitAmount;
  }

  public BigDecimal getNetProfitAmount(){
    return netProfitAmount;
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

  public void setTaxableTotal(BigDecimal taxableTotal) {
    this.taxableTotal = taxableTotal;
  }

  public BigDecimal getTaxableTotal(){
    return taxableTotal;
  }

  public void setSalesTaxRate(BigDecimal salesTaxRate) {
    this.salesTaxRate = salesTaxRate;
  }

  public BigDecimal getSalesTaxRate(){
    return salesTaxRate;
  }

  public void setSalesTaxCost(BigDecimal salesTaxCost) {
    this.salesTaxCost = salesTaxCost;
  }

  public BigDecimal getSalesTaxCost(){
    return salesTaxCost;
  }

  public void setServiceTaxKey(Byte serviceTaxKey) {
    this.serviceTaxKey = serviceTaxKey;
  }

  public Byte getServiceTaxKey() {
    return serviceTaxKey;
  }

  public void setServiceTaxableTotal(BigDecimal serviceTaxableTotal) {
    this.serviceTaxableTotal = serviceTaxableTotal;
  }

  public BigDecimal getServiceTaxableTotal(){
    return serviceTaxableTotal;
  }

  public void setServiceTaxRate(BigDecimal serviceTaxRate) {
    this.serviceTaxRate = serviceTaxRate;
  }

  public BigDecimal getServiceTaxRate(){
    return serviceTaxRate;
  }

  public void setServiceTaxCost(BigDecimal serviceTaxCost) {
    this.serviceTaxCost = serviceTaxCost;
  }

  public BigDecimal getServiceTaxCost(){
    return serviceTaxCost;
  }

  public void setDebitAdjustment(BigDecimal debitAdjustment) {
    this.debitAdjustment = debitAdjustment;
  }

  public BigDecimal getDebitAdjustment(){
    return debitAdjustment;
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

  public void setCreditAdjustment(BigDecimal creditAdjustment) {
    this.creditAdjustment = creditAdjustment;
  }

  public BigDecimal getCreditAdjustment() {
    return creditAdjustment;
  }

  public void setInvoiceTotal(BigDecimal invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public BigDecimal getInvoiceTotal(){
    return invoiceTotal;
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

  public void setAdjustmentId(Boolean adjustmentId) {
    this.adjustmentId = adjustmentId;
  }

  public Boolean getAdjustmentId() {
    return adjustmentId;
  }

  public void setAllowAdjustmentId(Boolean allowAdjustmentId) {
    this.allowAdjustmentId = allowAdjustmentId;
  }

  public Boolean getAllowAdjustmentId() {
    return allowAdjustmentId;
  }

  public void setGenesisKey(Integer genesisKey) {
    this.genesisKey = genesisKey;
  }

  public Integer getGenesisKey() {
    return genesisKey;
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

  public void setScheduleKey(Integer scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public Integer getScheduleKey() {
    return scheduleKey;
  }
  
  public void setLogisticsCount(Integer logisticsCount) {
    this.logisticsCount = logisticsCount;
  }

  public Integer getLogisticsCount() {
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

