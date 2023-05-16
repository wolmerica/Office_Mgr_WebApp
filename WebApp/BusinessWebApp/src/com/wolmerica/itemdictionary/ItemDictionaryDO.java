/*
 * ItemDictionaryDO.java
 *
 * Created on August 29, 2005, 09:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.itemdictionary;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ItemDictionaryDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String brandName = "";
  private String genericName = "";
  private String itemNum = "";
  private String profileNum = "";
  private String manufacturer = "";
  private Integer vendorKey = null;
  private String vendorName = "";
  private Byte vendorSpecificId = new Byte("0");
  private ArrayList customerTypeForm;
  private Byte customerTypeKey = new Byte("1");
  private Byte licenseKeyId = new Byte("0");
  private Byte reportId = new Byte("0");
  private String itemName = "";
  private BigDecimal size = new BigDecimal("0");
  private String sizeUnit = "";
  private BigDecimal dose = new BigDecimal("0");
  private String doseUnit = "";
  private String other = "";
  private String itemMemo = "";
  private BigDecimal carryFactor = new BigDecimal("0");
  private BigDecimal percentUse = new BigDecimal("0");
  private BigDecimal firstCost = new BigDecimal("0");
  private BigDecimal prevFirstCost = new BigDecimal("0");
  private BigDecimal unitCost = new BigDecimal("0");
  private BigDecimal prevUnitCost = new BigDecimal("0");
  private BigDecimal muAdditional = new BigDecimal("0");
  private BigDecimal muVendor = new BigDecimal("0");
  private BigDecimal labelCost = new BigDecimal("0");
  private BigDecimal qtyOnHand = new BigDecimal("0");
  private BigDecimal orderThreshold = new BigDecimal("0");
  private BigDecimal orderedQty = new BigDecimal("0");
  private BigDecimal forecastQty = new BigDecimal("0");
  private Short rebateCount = null;
  private Short rebateInstanceCount = null;
  private String permissionStatus = "";
  private Integer attachmentCount = null;
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;
  private String brandNameFilter = "";
  private String itemNumFilter = "";
  private String genericNameFilter = "";
  private Integer currentPage = new Integer("1");

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
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

  public void setItemNum(String itemNum) {
    this.itemNum = itemNum;
  }

  public String getItemNum() {
    return itemNum;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setVendorKey(Integer vendorKey) {
    this.vendorKey = vendorKey;
  }

  public Integer getVendorKey() {
    return vendorKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorSpecificId(Byte vendorSpecificId) {
    this.vendorSpecificId = vendorSpecificId;
  }

  public Byte getVendorSpecificId() {
    return vendorSpecificId;
  }

  public void setCustomerTypeForm(ArrayList customerTypeList){
  this.customerTypeForm=customerTypeList;
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

  public void setLicenseKeyId(Byte licenseKeyId) {
    this.licenseKeyId = licenseKeyId;
  }

  public Byte getLicenseKeyId() {
    return licenseKeyId;
  }

  public void setReportId(Byte reportId) {
    this.reportId = reportId;
  }

  public Byte getReportId() {
    return reportId;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public String getItemName() {
    return itemName;
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

  public void setDoseUnit(String doseUnit) {
    this.doseUnit = doseUnit;
  }

  public String getDoseUnit() {
    return doseUnit;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public String getOther() {
    return other;
  }

  public void setItemMemo(String itemMemo) {
    this.itemMemo = itemMemo;
  }

  public String getItemMemo() {
    return itemMemo;
  }

  public void setCarryFactor(BigDecimal carryFactor) {
    this.carryFactor = carryFactor;
  }

  public BigDecimal getCarryFactor(){
    return carryFactor;
  }

  public void setPercentUse(BigDecimal percentUse) {
    this.percentUse = percentUse;
  }

  public BigDecimal getPercentUse(){
    return percentUse;
  }

  public void setFirstCost(BigDecimal firstCost) {
    this.firstCost = firstCost;
  }

  public BigDecimal getFirstCost() {
    return firstCost;
  }

  public void setPrevFirstCost(BigDecimal prevFirstCost) {
    this.prevFirstCost = prevFirstCost;
  }

  public BigDecimal getPrevFirstCost() {
    return prevFirstCost;
  }

  public void setUnitCost(BigDecimal unitCost) {
    this.unitCost = unitCost;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public void setPrevUnitCost(BigDecimal prevUnitCost) {
    this.prevUnitCost = prevUnitCost;
  }

  public BigDecimal getPrevUnitCost() {
    return prevUnitCost;
  }

  public void setMuAdditional(BigDecimal muAdditional) {
    this.muAdditional = muAdditional;
  }

  public BigDecimal getMuAdditional() {
    return muAdditional;
  }

  public void setMuVendor(BigDecimal muVendor) {
    this.muVendor = muVendor;
  }

  public BigDecimal getMuVendor() {
    return muVendor;
  }

  public void setLabelCost(BigDecimal labelCost) {
    this.labelCost = labelCost;
  }

  public BigDecimal getLabelCost() {
    return labelCost;
  }

  public void setQtyOnHand(BigDecimal qtyOnHand) {
    this.qtyOnHand = qtyOnHand;
  }

  public BigDecimal getQtyOnHand() {
    return qtyOnHand;
  }

  public void setOrderThreshold(BigDecimal orderthreshold) {
    this.orderThreshold = orderthreshold;
  }

  public BigDecimal getOrderThreshold() {
    return orderThreshold;
  }

  public void setOrderedQty(BigDecimal orderedQty) {
    this.orderedQty = orderedQty;
  }

  public BigDecimal getOrderedQty() {
    return orderedQty;
  }

  public void setForecastQty(BigDecimal forecastQty) {
    this.forecastQty = forecastQty;
  }

  public BigDecimal getForecastQty() {
    return forecastQty;
  }

  public void setRebateCount(Short rebateCount) {
    this.rebateCount = rebateCount;
  }

  public Short getRebateCount() {
    return rebateCount;
  }

  public void setRebateInstanceCount(Short rebateInstanceCount) {
    this.rebateInstanceCount = rebateInstanceCount;
  }

  public Short getRebateInstanceCount() {
    return rebateInstanceCount;
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

  public void setBrandNameFilter(String brandNameFilter) {
    this.brandNameFilter = brandNameFilter;
  }

  public String getBrandNameFilter() {
    return brandNameFilter;
  }

  public void setItemNumFilter(String itemNumFilter) {
    this.itemNumFilter = itemNumFilter;
  }

  public String getItemNumFilter() {
    return itemNumFilter;
  }

  public void setGenericNameFilter(String genericNameFilter) {
    this.genericNameFilter = genericNameFilter;
  }

  public String getGenericNameFilter() {
    return genericNameFilter;
  }

  public void setCurrentPage(Integer currentPage ) {
    this.currentPage = currentPage;
  }

  public Integer getCurrentPage() {
    return currentPage;
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

