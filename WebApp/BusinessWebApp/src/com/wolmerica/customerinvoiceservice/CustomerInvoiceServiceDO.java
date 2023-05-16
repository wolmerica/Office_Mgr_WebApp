/*
 * CustomerInvoiceServiceDO.java
 *
 * Created on August 16, 2006, 12:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoiceservice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CustomerInvoiceServiceDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer customerInvoiceKey = null;
  private Integer serviceDictionaryKey = null;
  private Byte priceTypeKey = new Byte("0");
  private String priceTypeName = "";
  private String serviceNum = "";
  private String serviceName = "";
  private Short availableQty = new Short("0");
  private Short orderQty = new Short("0");
  private Short remainingQty = new Short("0");
  private BigDecimal thePrice = new BigDecimal("0");
  private BigDecimal extendPrice = new BigDecimal("0");
  private BigDecimal discountRate = new BigDecimal("0");
  private BigDecimal discountAmount = new BigDecimal("0");
  private BigDecimal costBasis = new BigDecimal("0");
  private Boolean enableServiceTaxId = false;
  private Boolean serviceTaxId = false;
  private Integer genesisKey = null;
  private Integer masterKey = null;  
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

  public void setCustomerInvoiceKey(Integer customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public Integer getCustomerInvoiceKey() {
    return customerInvoiceKey;
  }

  public void setServiceDictionaryKey(Integer serviceDictionaryKey) {
    this.serviceDictionaryKey = serviceDictionaryKey;
  }

  public Integer getServiceDictionaryKey() {
    return serviceDictionaryKey;
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

  public void setServiceNum(String serviceNum) {
    this.serviceNum = serviceNum;
  }

  public String getServiceNum() {
    return serviceNum;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setAvailableQty(Short availableQty) {
    this.availableQty = availableQty;
  }

  public Short getAvailableQty() {
    return availableQty;
  }

  public void setOrderQty(Short orderQty) {
    this.orderQty = orderQty;
  }

  public Short getOrderQty() {
    return orderQty;
  }

  public void setRemainingQty(Short remainingQty) {
    this.remainingQty = remainingQty;
  }

  public Short getRemainingQty() {
    return remainingQty;
  }

  public void setThePrice(BigDecimal thePrice) {
    this.thePrice = thePrice;
  }

  public BigDecimal getThePrice() {
    return thePrice;
  }

  public void setExtendPrice(BigDecimal extendPrice) {
    this.extendPrice = extendPrice;
  }

  public BigDecimal getExtendPrice() {
    return extendPrice;
  }

  public void setDiscountRate(BigDecimal discountRate) {
    this.discountRate = discountRate;
  }

  public BigDecimal getDiscountRate() {
    return discountRate;
  }

  public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
  }

  public BigDecimal getDiscountAmount() {
    return discountAmount;
  }

  public void setCostBasis(BigDecimal costBasis) {
    this.costBasis = costBasis;
  }

  public BigDecimal getCostBasis() {
    return costBasis;
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

  public void setGenesisKey(Integer genesisKey) {
    this.genesisKey = genesisKey;
  }

  public Integer getGenesisKey() {
    return genesisKey;
  }

  public void setMasterKey(Integer masterKey) {
    this.masterKey = masterKey;
  }

  public Integer getMasterKey() {
    return masterKey;
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