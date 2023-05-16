/*
 * WorkOrderDO.java
 *
 * Created on October 13, 2007, 11:35 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.workorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class WorkOrderDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer scheduleKey = null;
  private Boolean locationId = true;
  private String subject = "";
  private String resourceName = "";
  private String clientName = "";
  private String attributeToEntity = "";
  private String attributeToName = "";
  private Boolean releaseId = false;
  private Boolean thirdPartyId = false;
  private Integer vendorKey = null;
  private Byte sourceTypeKey = new Byte("0");
  private Integer sourceKey = null;
  private String sourceName = "";
  private String sourceNum = "";
  private String categoryName = "";
  private BigDecimal size = null;
  private String sizeUnit = "";
  private Byte priceTypeKey = new Byte("0");
  private String priceTypeName = "";
  private BigDecimal estimatedPrice = new BigDecimal("0");
  private BigDecimal extendedPrice = new BigDecimal("0");
  private Short availableQty = new Short("0");
  private Short orderQty = new Short("0");
  private Timestamp startStamp = null;
  private Date startDate = null;
  private Byte startHour = null;
  private Byte startMinute = null;
  private Timestamp endStamp = null;
  private Date endDate = null;
  private Byte endHour = null;
  private Byte endMinute = null;
  private Boolean activeId = false;
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

  public void setScheduleKey(Integer scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public Integer getScheduleKey() {
    return scheduleKey;
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

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setAttributeToEntity(String attributeToEntity) {
    this.attributeToEntity = attributeToEntity;
  }

  public String getAttributeToEntity() {
    return attributeToEntity;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
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
  
  public void setVendorKey(Integer vendorKey) {
    this.vendorKey = vendorKey;
  }

  public Integer getVendorKey() {
    return vendorKey;
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

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceNum(String sourceNum) {
    this.sourceNum = sourceNum;
  }

  public String getSourceNum() {
    return sourceNum;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getCategoryName() {
    return categoryName;
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

  public void setEstimatedPrice(BigDecimal estimatedPrice) {
    this.estimatedPrice = estimatedPrice;
  }

  public BigDecimal getEstimatedPrice() {
    return estimatedPrice;
  }

  public void setExtendedPrice(BigDecimal extendedPrice) {
    this.extendedPrice = extendedPrice;
  }

  public BigDecimal getExtendedPrice() {
    return extendedPrice;
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

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
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