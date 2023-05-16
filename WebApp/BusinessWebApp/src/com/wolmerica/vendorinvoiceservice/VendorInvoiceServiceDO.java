/*
 * VendorInvoiceServiceDO.java
 *
 * Created on November 08, 2008, 12:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/29/2006 Implement tools.formatter library.
 */
package com.wolmerica.vendorinvoiceservice;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;

public class VendorInvoiceServiceDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer vendorInvoiceKey = null;
  private Integer purchaseOrderServiceKey = null;
  private Integer serviceDictionaryKey = null;
  private String serviceName = "";
  private Byte priceTypeKey = new Byte("0");
  private String priceTypeName = "";
  private Short orderQty = new Short("0");
  private Short receiveQty = new Short("0");
  private Short backOrderQty = new Short("0");
  private BigDecimal firstCost = new BigDecimal("0");
  private BigDecimal variantAmount = new BigDecimal("0");    
  private BigDecimal serviceTaxCost = new BigDecimal("0");
  private BigDecimal handlingCost = new BigDecimal("0");
  private BigDecimal unitCost = new BigDecimal("0");
  private BigDecimal extendCost = new BigDecimal("0");
  private Boolean enableServiceTaxId = false;
  private Boolean serviceTaxId = false;
  private String serviceAction = "";
  private String noteLine1 = "";

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

  public void setPurchaseOrderServiceKey(Integer purchaseOrderServiceKey) {
    this.purchaseOrderServiceKey = purchaseOrderServiceKey;
  }

  public Integer getPurchaseOrderServiceKey() {
    return purchaseOrderServiceKey;
  }

  public void setServiceDictionaryKey(Integer serviceDictionaryKey) {
    this.serviceDictionaryKey = serviceDictionaryKey;
  }

  public Integer getServiceDictionaryKey() {
    return serviceDictionaryKey;
  }

 public void setServiceName(String serviceName) {
   this.serviceName = serviceName;
 }
   
 public String getServiceName() {
   return serviceName; 
 }   
  
 public void setPriceTypeKey(Byte priceTypeKey) {
   this.priceTypeKey = priceTypeKey;
 }

 public Byte getPriceTypeKey() {
   return priceTypeKey;
 }

 public void setPriceTypeName(String priceTypeName) {
   this.priceTypeName = priceTypeName;
 }
   
 public String getPriceTypeName() {
   return priceTypeName; 
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
  
  public void setServiceTaxCost(BigDecimal serviceTaxCost) {
    this.serviceTaxCost = serviceTaxCost;
  }

  public BigDecimal getServiceTaxCost() {
    return serviceTaxCost;
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

  public void setEnableServiceTaxId(Boolean enableServiceTaxId) {
    this.enableServiceTaxId = enableServiceTaxId;
  }

  public Boolean getEnableServiceTaxId() {
    return enableServiceTaxId;
  }

  public void setServiceTaxId(Boolean serviceTaxId) {
    this.serviceTaxId = serviceTaxId;
  }

  public Boolean getServiceTaxId() {
    return serviceTaxId;
  }

  public void setServiceAction(String serviceAction) {
    this.serviceAction = serviceAction;
  }

  public String getServiceAction() {
    return serviceAction;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }
}