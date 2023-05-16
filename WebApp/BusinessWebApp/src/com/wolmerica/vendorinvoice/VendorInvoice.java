/*
 * VendorInvoice.java
 *
 * Created on October 23, 2005, 12:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/28/2005 Add customerInvoiceCount to hold customer invoice counts for master vendor invoices.
 */

package com.wolmerica.vendorinvoice;

/**
 *
 * @author Richard
 */

import java.sql.Timestamp;
import java.math.BigDecimal;

public class VendorInvoice {

  private Integer key;
  private Integer purchaseOrderKey;
  private String purchaseOrderNumber;
  private String vendorName;
  private String invoiceNumber;
  private java.sql.Date invoiceDate;
  private java.sql.Date invoiceDueDate;
  private BigDecimal salesTaxCost;
  private BigDecimal packagingCost;
  private BigDecimal freightCost;
  private BigDecimal miscellaneousCost;
  private BigDecimal invoiceTotal;
  private String grandTotal;
  private boolean salesTaxId;
  private BigDecimal usageTaxApplicableAmount;
  private BigDecimal usageTaxAmount;
  private BigDecimal nonUsageTaxAmount;
  private boolean carryFactorId;
  private boolean activeId;
  private boolean creditId;  
  private boolean masterInvoiceId;
  private boolean directShipId;
  private Short customerInvoiceCount;
  private String balanceQty;
  private String balanceAmount;
  private String createUser;
  private Timestamp createStamp;
  private String updateUser;
  private Timestamp updateStamp;

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

  public void setInvoiceDate(java.sql.Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public java.sql.Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDueDate(java.sql.Date invoiceDueDate) {
    this.invoiceDueDate = invoiceDueDate;
  }

  public java.sql.Date getInvoiceDueDate() {
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

  public void setGrandTotal(String grandTotal) {
    this.grandTotal = grandTotal;
  }

  public String getGrandTotal() {
    return grandTotal;
  }

  public void setSalesTaxId(boolean salesTaxId) {
    this.salesTaxId = salesTaxId;
  }

  public boolean getSalesTaxId() {
    return salesTaxId;
  }

  public void setUsageTaxApplicableAmount(BigDecimal usageTaxApplicableAmount) {
    this.usageTaxApplicableAmount = usageTaxApplicableAmount;
  }

  public BigDecimal getUsageTaxApplicableAmount(){
    return usageTaxApplicableAmount;
  }

  public void setUsageTaxAmount(BigDecimal usageTaxAmount) {
    this.usageTaxAmount = usageTaxAmount;
  }

  public BigDecimal getUsageTaxAmount(){

    return usageTaxAmount;
  }

  public void setNonUsageTaxAmount(BigDecimal nonUsageTaxAmount) {

    this.nonUsageTaxAmount = nonUsageTaxAmount;
  }

  public BigDecimal getNonUsageTaxAmount(){

    return nonUsageTaxAmount;
  }

  public void setCarryFactorId(boolean carryFactorId) {
    this.carryFactorId = carryFactorId;
  }

  public boolean getCarryFactorId() {
    return carryFactorId;
  }

  public void setActiveId(boolean activeId) {
    this.activeId = activeId;
  }

  public boolean getActiveId() {
    return activeId;
  }

  public void setCreditId(boolean creditId) {
    this.creditId = creditId;
  }

  public boolean getCreditId() {
    return creditId;
  }
    
  public void setMasterInvoiceId(boolean masterInvoiceId) {
    this.masterInvoiceId = masterInvoiceId;
  }

  public boolean getMasterInvoiceId() {
    return masterInvoiceId;
  }

  public void setDirectShipId(boolean directShipId) {
    this.directShipId = directShipId;
  }

  public boolean getDirectShipId() {
    return directShipId;
  }

  public void setCustomerInvoiceCount(Short customerInvoiceCount) {
    this.customerInvoiceCount = customerInvoiceCount;
  }

  public Short getCustomerInvoiceCount() {
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
