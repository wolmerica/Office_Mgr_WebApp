/*
 * VendorInvoiceItemDO.java
 *
 * Created on January 29, 2006, 07:16 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/29/2006 Implement tools.formatter library.
 */
package com.wolmerica.vendorinvoiceitem;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VendorInvoiceItemDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer vendorInvoiceKey = null;
  private Integer purchaseOrderItemKey = null;
  private Integer itemDictionaryKey = null;
  private String brandName = "";
  private BigDecimal size = null;
  private String sizeUnit = "";
  private Short orderQty = new Short("0");
  private Short receiveQty = new Short("0");
  private Short backOrderQty = new Short("0");
  private BigDecimal firstCost = new BigDecimal("0");  
  private BigDecimal variantAmount = new BigDecimal("0");    
  private BigDecimal useTaxCost = new BigDecimal("0");
  private BigDecimal handlingCost = new BigDecimal("0");
  private BigDecimal unitCost = new BigDecimal("0");
  private BigDecimal extendCost = new BigDecimal("0");
  private Boolean enableSalesTaxId = false;
  private Boolean salesTaxId = false;
  private Boolean enableCarryFactorId = false;
  private BigDecimal carryFactor = new BigDecimal("0");
  private Date expirationDate = null;
  private String itemAction = "";
  private String noteLine1 = "";
  private Boolean licenseKeyId = null;
  private Integer licenseKeyCount = null;

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

  public void setPurchaseOrderItemKey(Integer purchaseOrderItemKey) {
    this.purchaseOrderItemKey = purchaseOrderItemKey;
  }

  public Integer getPurchaseOrderItemKey() {
    return purchaseOrderItemKey;
  }

  public void setItemDictionaryKey(Integer itemDictionaryKey) {
    this.itemDictionaryKey = itemDictionaryKey;
  }

  public Integer getItemDictionaryKey() {
    return itemDictionaryKey;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setSize(BigDecimal size) {
    this.size = size;
  }

  public BigDecimal getSize() {
    return size;
  }

  public void setSizeUnit(String sizeUnit) {
    this.sizeUnit = sizeUnit;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

 public void setOrderQty(Short orderQty) {
    this.orderQty = orderQty;
  }

  public Short getOrderQty() {
    return orderQty;
  }

  public void setReceiveQty(Short receiveQty) {
    this.receiveQty = receiveQty;
  }

  public Short getReceiveQty() {
    return receiveQty;
  }

  public void setBackOrderQty(Short backOrderQty) {
    this.backOrderQty = backOrderQty;
  }

  public Short getBackOrderQty() {
    return backOrderQty;
  }
  
  public void setFirstCost(BigDecimal firstCost) {
    this.firstCost = firstCost;
  }

  public BigDecimal getFirstCost() {
    return firstCost;
  }

  public void setVariantAmount(BigDecimal variantAmount) {
    this.variantAmount = variantAmount;
  }

  public BigDecimal getVariantAmount() {
    return variantAmount;
  }
  
  public void setUseTaxCost(BigDecimal useTaxCost) {
    this.useTaxCost = useTaxCost;
  }

  public BigDecimal getUseTaxCost() {
    return useTaxCost;
  }

  public void setHandlingCost(BigDecimal handlingCost) {
    this.handlingCost = handlingCost;
  }

  public BigDecimal getHandlingCost() {
    return handlingCost;
  }

  public void setUnitCost(BigDecimal unitCost) {
    this.unitCost = unitCost;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public void setExtendCost(BigDecimal extendCost) {
    this.extendCost = extendCost;
  }

  public BigDecimal getExtendCost() {
    return extendCost;
  }

  public void setEnableSalesTaxId(Boolean enableSalesTaxId) {
    this.enableSalesTaxId = enableSalesTaxId;
  }

  public Boolean getEnableSalesTaxId() {
    return enableSalesTaxId;
  }

  public void setSalesTaxId(Boolean salesTaxId) {
    this.salesTaxId = salesTaxId;
  }

  public Boolean getSalesTaxId() {
    return salesTaxId;
  }

  public void setEnableCarryFactorId(Boolean enableCarryFactorId) {
    this.enableCarryFactorId = enableCarryFactorId;
  }

  public Boolean getEnableCarryFactorId() {
    return enableCarryFactorId;
  }

  public void setCarryFactor(BigDecimal carryFactor) {
    this.carryFactor = carryFactor;
  }

  public BigDecimal getCarryFactor() {
    return carryFactor;
  }

  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  public void setItemAction(String itemAction) {
    this.itemAction = itemAction;
  }

  public String getItemAction() {
    return itemAction;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setLicenseKeyId(Boolean licenseKeyId) {
    this.licenseKeyId = licenseKeyId;
  }

  public Boolean getLicenseKeyId() {
    return licenseKeyId;
  }

  public void setLicenseKeyCount(Integer licenseKeyCount) {
    this.licenseKeyCount = licenseKeyCount;
  }

  public Integer getLicenseKeyCount() {
    return licenseKeyCount;
  }
}