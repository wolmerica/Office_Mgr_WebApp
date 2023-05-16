/*
 * SystemDO.java
 *
 * Created on August 26, 2005, 11:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.system;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class SystemDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer customerKey = null;
  private String clientName = "";
  private String makeModel = "";
  private String processor = "";
  private String operatingSystem = "";
  private String memory = "";
  private String primaryDrive = "";
  private String auxilaryDrive = "";
  private String softwarePackage = "";
  private Date systemDate = null;
  private String macAddress = "";
  private String macAddress2 = "";  
  private String serialNumber = "";
  private String noteLine1 = "";
  private String noteLine2 = "";
  private String noteLine3 = "";  
  private String documentServerURL = "";
  private String photoFileName = "";
  private String permissionStatus = "";
  private Integer attachmentCount = null;
  private Boolean activeId = true;
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;
  private String clientNameFilter = null;
  private String makeModelFilter = "";  
  private Integer currentPage = new Integer("1");  

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
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

  public void setMakeModel(String makeModel) {
    this.makeModel = makeModel;
  }

  public String getMakeModel() {
    return makeModel;
  }

  public void setProcessor(String processor) {
    this.processor = processor;
  }

  public String getProcessor() {
    return processor;
  }

  public void setOperatingSystem(String operatingSystem) {
    this.operatingSystem = operatingSystem;
  }

  public String getOperatingSystem() {
    return operatingSystem;
  }

  public void setMemory(String memory) {
    this.memory = memory;
  }

  public String getMemory() {
    return memory;
  }

  public void setPrimaryDrive(String primaryDrive) {
    this.primaryDrive = primaryDrive;
  }

  public String getPrimaryDrive() {
    return primaryDrive;
  }

  public void setAuxilaryDrive(String auxilaryDrive) {
    this.auxilaryDrive = auxilaryDrive;
  }

  public String getAuxilaryDrive() {
    return auxilaryDrive;
  }

  public void setSoftwarePackage(String softwarePackage) {
    this.softwarePackage = softwarePackage;
  }

  public String getSoftwarePackage() {
    return softwarePackage;
  }

  public void setSystemDate(Date systemDate) {
    this.systemDate = systemDate;
  }

  public Date getSystemDate() {
    return systemDate;
  }
  
  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }

  public String getMacAddress() {
    return macAddress;
  }
  
  public void setMacAddress2(String macAddress2) {
    this.macAddress2 = macAddress2;
  }

  public String getMacAddress2() {
    return macAddress2;
  }  
  
  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public String getSerialNumber() {
    return serialNumber;
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
  
  public void setDocumentServerURL(String documentServerURL) {
    this.documentServerURL = documentServerURL;
  }

  public String getDocumentServerURL() {
    return documentServerURL;
  }

  public void setPhotoFileName(String photoFileName) {
    this.photoFileName = photoFileName;
  }

  public String getPhotoFileName() {
    return photoFileName;
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

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
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
  
  public void setClientNameFilter(String clientNameFilter) {
    this.clientNameFilter = clientNameFilter;
  }

  public String getClientNameFilter() {
    return clientNameFilter;
  }

  public void setMakeModelFilter(String makeModelFilter) {
    this.makeModelFilter = makeModelFilter;
  }

  public String getMakeModelFilter() {
    return makeModelFilter;
  }
  
  public void setCurrentPage(Integer currentPage ) {
    this.currentPage = currentPage;
  }

  public Integer getCurrentPage() {
    return currentPage;
  }  
}
