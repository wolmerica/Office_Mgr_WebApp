/*
 * ServiceDictionaryDO.java
 *
 * Created on June 20, 2006, 07:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.servicedictionary;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ServiceDictionaryDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String serviceName = "";
  private String serviceCategory = "";
  private String serviceNum = "";
  private String serviceDescription = "";
  private String profileNum = "";
  private String other = "";
  private ArrayList priceTypeForm;
  private Byte priceTypeKey = new Byte("4");
  private Boolean billableId = true;
  private Boolean releaseId = false;
  private ArrayList customerTypeForm;
  private Byte customerTypeKey = new Byte("1");
  private Byte durationHours = null;
  private Byte durationMinutes = null;
  private BigDecimal laborCost = null;
  private BigDecimal serviceCost = null;
  private BigDecimal fee1Cost = new BigDecimal("0");
  private BigDecimal fee2Cost = new BigDecimal("0");
  private BigDecimal markUp1Rate = new BigDecimal("0");
  private BigDecimal markUp2Rate = new BigDecimal("0");
  private String permissionStatus = "";
  private Integer attachmentCount = null;
  private Integer vendorKey = null;
  private String vendorName = "";  
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;
  private String serviceNameFilter = "";
  private String serviceNumFilter = "";
  private String categoryNameFilter = "";
  private Integer currentPage = new Integer("1");

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceCategory(String serviceCategory) {
    this.serviceCategory = serviceCategory;
  }

  public String getServiceCategory() {
    return serviceCategory;
  }

  public void setServiceNum(String serviceNum) {
    this.serviceNum = serviceNum;
  }

  public String getServiceNum() {
    return serviceNum;
  }
    
  public void setServiceDescription(String serviceDescription) {
    this.serviceDescription = serviceDescription;
  }

  public String getServiceDescription() {
    return serviceDescription;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public String getOther() {
    return other;
  }

  public void setPriceTypeForm(ArrayList priceTypeList){
  this.priceTypeForm=priceTypeList;
  }

  public ArrayList getPriceTypeForm(){
  return priceTypeForm;
  }

  public void setPriceTypeKey(Byte priceTypeKey) {
    this.priceTypeKey = priceTypeKey;
  }

  public Byte getPriceTypeKey() {
    return priceTypeKey;
  }

  public void setBillableId(Boolean billableId) {
    this.billableId = billableId;
  }

  public Boolean getBillableId() {
    return billableId;
  }

  public void setReleaseId(Boolean releaseId) {
    this.releaseId = releaseId;
  }

  public Boolean getReleaseId() {
    return releaseId;
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

  public void setDurationHours(Byte durationHours) {
    this.durationHours = durationHours;
  }

  public Byte getDurationHours() {
    return durationHours;
  }

  public void setDurationMinutes(Byte durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public Byte getDurationMinutes() {
    return durationMinutes;
  }

  public void setLaborCost(BigDecimal laborCost) {
    this.laborCost = laborCost;
  }

  public BigDecimal getLaborCost() {
    return laborCost;
  }

  public void setServiceCost(BigDecimal serviceCost) {
    this.serviceCost = serviceCost;
  }

  public BigDecimal getServiceCost() {
    return serviceCost;
  }

  public void setFee1Cost(BigDecimal fee1Cost) {
    this.fee1Cost = fee1Cost;
  }

  public BigDecimal getFee1Cost() {
    return fee1Cost;
  }

  public void setFee2Cost(BigDecimal fee2Cost) {
    this.fee2Cost = fee2Cost;
  }

  public BigDecimal getFee2Cost() {
    return fee2Cost;
  }

  public void setMarkUp1Rate(BigDecimal markUp1Rate) {
    this.markUp1Rate = markUp1Rate;
  }

  public BigDecimal getMarkUp1Rate() {
    return markUp1Rate;
  }

  public void setMarkUp2Rate(BigDecimal markUp2Rate) {
    this.markUp2Rate = markUp2Rate;
  }

  public BigDecimal getMarkUp2Rate() {
    return markUp2Rate;
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

  public void setServiceNameFilter(String serviceNameFilter) {
    this.serviceNameFilter = serviceNameFilter;
  }

  public String getServiceNameFilter() {
    return serviceNameFilter;
  }

  public void setServiceNumFilter(String serviceNumFilter) {
    this.serviceNumFilter = serviceNumFilter;
  }

  public String getServiceNumFilter() {
    return serviceNumFilter;
  }

  public void setCategoryNameFilter(String categoryNameFilter) {
    this.categoryNameFilter = categoryNameFilter;
  }

  public String getCategoryNameFilter() {
    return categoryNameFilter;
  }

  public void setCurrentPage(Integer currentPage ) {
    this.currentPage = currentPage;
  }

  public Integer getCurrentPage() {
    return currentPage;
  }
}

