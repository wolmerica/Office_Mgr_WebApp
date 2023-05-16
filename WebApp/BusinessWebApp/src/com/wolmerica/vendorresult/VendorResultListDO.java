/*
 * VendorResultListDO.java
 *
 * Created on April 29, 2010, 9:03 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorresult;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;

public class VendorResultListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer purchaseOrderKey = null;
  private String purchaseOrderNum = null;
  private Integer vendorKey = null;
  private String vendorName = "";
  private Integer customerKey = null;
  private String customerName = "";
  private String customerFirstName = "";
  private String customerLastName = "";
  private Byte attributeToEntityKey = null;
  private String attributeToEntityName = "";
  private Integer attributeToKey = null;
  private String attributeToName = "";
  private Integer speciesKey = null;
  private String speciesName = "";
  private Integer breedKey = null;
  private String breedName = "";
  private String petAge = "";
  private Byte petSexId = new Byte("0");
  private Byte neuteredId = new Byte("0");
  private Integer sourceKey = null;
  private String status = "";
  private String importFilename = "";
  private String orderStatus = "";
  private String siteName = "";
  private Date receiveDate = new Date();
  private String receiveAssessionId = "";
  private Date resultDate = new Date();
  private String resultAssessionId = "";
  private String profileNum = "";
  private String unitStatus = "";
  private String unitCode = "";
  private String unitName = "";
  private String abnormalStatus = "";
  private String testStatus = "";
  private String testCode = "";
  private String testName = "";
  private String testValue = "";
  private String testUnits = "";
  private String testRange = "";
  private String testComment = "";
  private String errorMessage = "";
  private String permissionStatus = "";
  private Integer attachmentCount = null;
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

  public void setPurchaseOrderNum(String purchaseOrderNum) {
    this.purchaseOrderNum = purchaseOrderNum;
  }

  public String getPurchaseOrderNum() {
    return purchaseOrderNum;
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

  public void setCustomerKey(Integer customerKey) {
    this.customerKey = customerKey;
  }

  public Integer getCustomerKey() {
    return customerKey;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerFirstName(String customerFirstName) {
    this.customerFirstName = customerFirstName;
  }

  public String getCustomerFirstName() {
    return customerFirstName;
  }

  public void setCustomerLastName(String customerLastName) {
    this.customerLastName = customerLastName;
  }

  public String getCustomerLastName() {
    return customerLastName;
  }

  public void setAttributeToEntityKey(Byte attributeToEntityKey) {
    this.attributeToEntityKey = attributeToEntityKey;
  }

  public Byte getAttributeToEntityKey() {
    return attributeToEntityKey;
  }

  public void setAttributeToKey(Integer attributeToKey) {
    this.attributeToKey = attributeToKey;
  }

  public Integer getAttributeToKey() {
    return attributeToKey;
  }

  public void setAttributeToEntityName(String attributeToEntityName) {
    this.attributeToEntityName = attributeToEntityName;
  }

  public String getAttributeToEntityName() {
    return attributeToEntityName;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
  }

  public void setSpeciesKey(Integer speciesKey) {
    this.speciesKey = speciesKey;
  }

  public Integer getSpeciesKey() {
    return speciesKey;
  }

  public void setSpeciesName(String speciesName) {
    this.speciesName = speciesName;
  }

  public String getSpeciesName() {
    return speciesName;
  }

  public void setBreedKey(Integer breedKey) {
    this.breedKey = breedKey;
  }

  public Integer getBreedKey() {
    return breedKey;
  }

  public void setBreedName(String breedName) {
    this.breedName = breedName;
  }

  public String getBreedName() {
    return breedName;
  }

  public void setPetAge(String petAge) {
    this.petAge = petAge;
  }

  public String getPetAge() {
    return petAge;
  }

  public void setPetSexId(Byte petSexId) {
    this.petSexId = petSexId;
  }

  public Byte getPetSexId() {
    return petSexId;
  }

  public void setNeuteredId(Byte neuteredId) {
    this.neuteredId = neuteredId;
  }

  public Byte getNeuteredId() {
    return neuteredId;
  }

  public void setSourceKey(Integer sourceKey) {
    this.sourceKey = sourceKey;
  }

  public Integer getSourceKey() {
    return sourceKey;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setImportFilename(String importFilename) {
    this.importFilename = importFilename;
  }

  public String getImportFilename() {
    return importFilename;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  public String getSiteName() {
    return siteName;
  }

  public void setReceiveDate(Date receiveDate) {
    this.receiveDate = receiveDate;
  }

  public Date getReceiveDate() {
    return receiveDate;
  }

  public void setReceiveAssessionId(String receiveAssessionId) {
    this.receiveAssessionId = receiveAssessionId;
  }

  public String getReceiveAssessionId() {
    return receiveAssessionId;
  }

  public void setResultDate(Date resultDate) {
    this.resultDate = resultDate;
  }

  public Date getResultDate() {
    return resultDate;
  }

  public void setResultAssessionId(String resultAssessionId) {
    this.resultAssessionId = resultAssessionId;
  }

  public String getResultAssessionId() {
    return resultAssessionId;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }

  public void setUnitStatus(String unitStatus) {
    this.unitStatus = unitStatus;
  }

  public String getUnitStatus() {
    return unitStatus;
  }

  public void setUnitCode(String unitCode) {
    this.unitCode = unitCode;
  }

  public String getUnitCode() {
    return unitCode;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public String getUnitName() {
    return unitName;
  }

  public void setAbnormalStatus(String abnormalStatus) {
    this.abnormalStatus = abnormalStatus;
  }

  public String getAbnormalStatus() {
    return abnormalStatus;
  }

  public void setTestStatus(String testStatus) {
    this.testStatus = testStatus;
  }

  public String getTestStatus() {
    return testStatus;
  }

  public void setTestCode(String testCode) {
    this.testCode = testCode;
  }

  public String getTestCode() {
    return testCode;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestValue(String testValue) {
    this.testValue = testValue;
  }

  public String getTestValue() {
    return testValue;
  }

  public void setTestUnits(String testUnits) {
    this.testUnits = testUnits;
  }

  public String getTestUnits() {
    return testUnits;
  }

  public void setTestRange(String testRange) {
    this.testRange = testRange;
  }

  public String getTestRange() {
    return testRange;
  }

  public void setTestComment(String testComment) {
    this.testComment = testComment;
  }

  public String getTestComment() {
    return testComment;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
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

