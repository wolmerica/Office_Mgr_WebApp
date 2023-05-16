package com.wolmerica.customerinvoiceitem;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CustomerInvoiceItemDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer customerInvoiceKey = null;
  private Integer vendorInvoiceItemKey = null;
  private Integer itemDictionaryKey = null;
  private Byte priceTypeKey = new Byte("0");
  private String itemNum = "";
  private String brandName = "";
  private String genericName = "";
  private BigDecimal size = null;
  private String sizeUnit = "";
  private BigDecimal dose = null;  
  private Short availableQty = new Short("0");
  private Short orderQty = new Short("0");
  private Short remainingQty = new Short("0");
  private BigDecimal thePrice = new BigDecimal("0");
  private BigDecimal extendPrice = new BigDecimal("0");
  private BigDecimal discountRate = new BigDecimal("0");
  private BigDecimal discountAmount = new BigDecimal("0");
  private BigDecimal costBasis = new BigDecimal("0");
  private Boolean enableSalesTaxId = false;
  private Boolean salesTaxId = false;
  private Integer genesisKey = null;
  private Integer masterKey = null;
  private String noteLine1 = "";
  private Boolean licenseKeyId = null;
  private Integer licenseKeyCount = null;
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

  public void setVendorInvoiceItemKey(Integer vendorInvoiceItemKey) {
    this.vendorInvoiceItemKey = vendorInvoiceItemKey;
  }

  public Integer getVendorInvoiceItemKey() {
    return vendorInvoiceItemKey;
  }

  public void setItemDictionaryKey(Integer itemDictionaryKey) {
    this.itemDictionaryKey = itemDictionaryKey;
  }

  public Integer getItemDictionaryKey() {
    return itemDictionaryKey;
  }

  public void setPriceTypeKey(Byte priceTypeKey) {
    this.priceTypeKey = priceTypeKey;
  }

  public Byte getPriceTypeKey() {
    return priceTypeKey;
  }

  public void setItemNum(String itemNum) {
    this.itemNum = itemNum;
  }

  public String getItemNum() {
    return itemNum;
  }
  
  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setGenericName(String genericName) {
    this.genericName = genericName;
  }

  public String getGenericName() {
    return genericName;
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

  public void setDose(BigDecimal dose) {
    this.dose = dose;
  }

  public BigDecimal getDose() {
    return dose;
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