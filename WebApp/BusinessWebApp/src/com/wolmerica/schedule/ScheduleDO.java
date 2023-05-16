/*
 * ScheduleDO.java
 *
 * Created on September 7, 2006, 10:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.schedule;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class ScheduleDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Boolean firstDayId = true;
  private Byte eventTypeKey = new Byte("0");
  private Boolean locationId = true;
  private String subject = "";
  private Integer customerKey = null;
  private String clientName = "";
  private String customerPhone = "0000000000";
  private Byte reminderPrefKey = new Byte("0");
  private Boolean addressId = false;
  private String address = "";
  private String city = "";
  private String state = "";
  private Byte customerTypeKey = new Byte("1");
  private String attributeToEntity = "";
  private Byte sourceTypeKey = new Byte("-1");
  private Integer sourceKey = new Integer("-1");
  private String attributeToName = "";
  private Date attributeToDate = new Date();
  private Timestamp startStamp = null;
  private Date startDate = null;
  private Byte startHour = null;
  private Byte startMinute = null;
  private Timestamp endStamp = null;
  private Date endDate = new Date();
  private Byte endHour = null;
  private Byte endMinute = null;
  private String noteLine1 = "";
  private String customerInvoiceKey = "";
  private Byte statusKey = new Byte("0");
  private Boolean rewindId = false;
  private Boolean activeId = false;
  private Boolean releaseId = false;
  private Boolean thirdPartyId = false;
  private Boolean thirdPartyOrderId = false;  
  private Boolean category1Id = false;
  private Boolean category2Id = false;
  private Boolean category3Id = false;
  private Boolean category4Id = false;
  private Boolean category5Id = false;
  private Boolean category6Id = false;
  private Integer category1Key = null;
  private Integer category2Key = null;
  private Integer category3Key = null;
  private Integer category4Key = null;
  private Integer category5Key = null;
  private Integer category6Key = null;
  private Integer examKey = null;
  private BigDecimal workOrderItemTotal = new BigDecimal("0");
  private BigDecimal workOrderServiceTotal = new BigDecimal("0");
  private String permissionStatus = "";
  private Boolean allowDeleteId = false;
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

  public void setFirstDayId(Boolean firstDayId) {
    this.firstDayId = firstDayId;
  }

  public Boolean getFirstDayId() {
    return firstDayId;
  }

  public void setEventTypeKey(Byte eventTypeKey) {
    this.eventTypeKey = eventTypeKey;
  }

  public Byte getEventTypeKey() {
    return eventTypeKey;
  }

  public void setLocationId(Boolean locationId) {
    this.locationId = locationId;
  }

  public Boolean getLocationId() {
    return locationId;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return subject;
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

  public void setCustomerPhone(String customerPhone) {
    this.customerPhone = customerPhone;
  }

  public String getCustomerPhone() {
    return customerPhone;
  }

  public void setReminderPrefKey(Byte reminderPrefKey) {
    this.reminderPrefKey = reminderPrefKey;
  }

  public Byte getReminderPrefKey() {
    return reminderPrefKey;
  }

  public void setAddressId(Boolean addressId) {
    this.addressId = addressId;
  }

  public Boolean getAddressId() {
    return addressId;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCity() {
    return city;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }

  public void setCustomerTypeKey(Byte customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public Byte getCustomerTypeKey() {
    return customerTypeKey;
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

  public void setAttributeToDate(Date attributeToDate) {
    this.attributeToDate = attributeToDate;
  }

  public Date getAttributeToDate() {
    return attributeToDate;
  }

  public void setStartStamp(Timestamp startStamp) {
    this.startStamp = startStamp;
  }

  public Timestamp getStartStamp() {
    return startStamp;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartHour(Byte startHour) {
    this.startHour = startHour;
  }

  public Byte getStartHour() {
    return startHour;
  }

  public void setStartMinute(Byte startMinute) {
    this.startMinute = startMinute;
  }

  public Byte getStartMinute() {
    return startMinute;
  }

  public void setEndStamp(Timestamp endStamp) {
    this.endStamp = endStamp;
  }

  public Timestamp getEndStamp() {
    return endStamp;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndHour(Byte endHour) {
    this.endHour = endHour;
  }

  public Byte getEndHour() {
    return endHour;
  }

  public void setEndMinute(Byte endMinute) {
    this.endMinute = endMinute;
  }

  public Byte getEndMinute() {
    return endMinute;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setCustomerInvoiceKey(String customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public String getCustomerInvoiceKey() {
    return customerInvoiceKey;
  }

  public void setStatusKey(Byte statusKey) {
    this.statusKey = statusKey;
  }

  public Byte getStatusKey() {
    return statusKey;
  }

  public void setRewindId(Boolean rewindId) {
    this.rewindId = rewindId;
  }

  public Boolean getRewindId() {
    return rewindId;
  }

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }

  public void setReleaseId(Boolean releaseId) {
    this.releaseId = releaseId;
  }

  public Boolean getReleaseId() {
    return releaseId;
  }

  public void setThirdPartyId(Boolean thirdPartyId) {
    this.thirdPartyId = thirdPartyId;
  }

  public Boolean getThirdPartyId() {
    return thirdPartyId;
  }

  public void setThirdPartyOrderId(Boolean thirdPartyOrderId) {
    this.thirdPartyOrderId = thirdPartyOrderId;
  }

  public Boolean getThirdPartyOrderId() {
    return thirdPartyOrderId;
  }  
  
  public void setCategory1Id(Boolean category1Id) {
    this.category1Id = category1Id;
  }

  public Boolean getCategory1Id() {
    return category1Id;
  }

  public void setCategory2Id(Boolean category2Id) {
    this.category2Id = category2Id;
  }

  public Boolean getCategory2Id() {
    return category2Id;
  }

  public void setCategory3Id(Boolean category3Id) {
    this.category3Id = category3Id;
  }

  public Boolean getCategory3Id() {
    return category3Id;
  }

  public void setCategory4Id(Boolean category4Id) {
    this.category4Id = category4Id;
  }

  public Boolean getCategory4Id() {
    return category4Id;
  }

  public void setCategory5Id(Boolean category5Id) {
    this.category5Id = category5Id;
  }

  public Boolean getCategory5Id() {
    return category5Id;
  }

  public void setCategory6Id(Boolean category6Id) {
    this.category6Id = category6Id;
  }

  public Boolean getCategory6Id() {
    return category6Id;
  }
  
  public void setCategory1Key(Integer category1Key) {
    this.category1Key = category1Key;
  }

  public Integer getCategory1Key() {
    return category1Key;
  }  

  public void setCategory2Key(Integer category2Key) {
    this.category2Key = category2Key;
  }

  public Integer getCategory2Key() {
    return category2Key;
  }   
  
  public void setCategory3Key(Integer category3Key) {
    this.category3Key = category3Key;
  }

  public Integer getCategory3Key() {
    return category3Key;
  } 
  
  public void setCategory4Key(Integer category4Key) {
    this.category4Key = category4Key;
  }

  public Integer getCategory4Key() {
    return category4Key;
  } 
  
  public void setCategory5Key(Integer category5Key) {
    this.category5Key = category5Key;
  }

  public Integer getCategory5Key() {
    return category5Key;
  } 
  
  public void setCategory6Key(Integer category6Key) {
    this.category6Key = category6Key;
  }

  public Integer getCategory6Key() {
    return category6Key;
  }   
  
  public void setExamKey(Integer examKey) {
    this.examKey = examKey;
  }

  public Integer getExamKey() {
    return examKey;
  }

  public void setWorkOrderItemTotal(BigDecimal workOrderItemTotal) {
    this.workOrderItemTotal = workOrderItemTotal;
  }

  public BigDecimal getWorkOrderItemTotal() {
    return workOrderItemTotal;
  }

  public void setWorkOrderServiceTotal(BigDecimal workOrderServiceTotal) {
    this.workOrderServiceTotal = workOrderServiceTotal;
  }

  public BigDecimal getWorkOrderServiceTotal() {
    return workOrderServiceTotal;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }

  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
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
