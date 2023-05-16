/*
 * PetAnestheticDO.java
 *
 * Created on August 24, 2007, 07:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.petanesthetic;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PetAnestheticDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer petExamKey = null;
  private Date treatmentDate = null;  
  private Byte applicationType = new Byte("0");
  private Integer itemDictionaryKey = null;
  private String brandName = "";
  private BigDecimal size = new BigDecimal("0");
  private String sizeUnit = "";
  private BigDecimal dose = new BigDecimal("0");
  private String doseUnit = "";
  private String route = "";
  private Byte startHour = null;
  private Byte startMinute = null;
  private Integer resourceKey = null;
  private String resourceName = "";
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

  public void setPetExamKey(Integer petExamKey) {
    this.petExamKey = petExamKey;
  }

  public Integer getPetExamKey() {
    return petExamKey;
  }
  
  public void setTreatmentDate(Date treatmentDate) {
    this.treatmentDate = treatmentDate;
  }

  public Date getTreatmentDate() {
    return treatmentDate;
  }  

  public void setApplicationType(Byte applicationType) {
    this.applicationType = applicationType;
  }

  public Byte getApplicationType() {
    return applicationType;
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

  public void setRoute(String route) {
    this.route = route;
  }

  public String getRoute() {
    return route;
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

  public void setResourceKey(Integer resourceKey) {
    this.resourceKey = resourceKey;
  }

  public Integer getResourceKey() {
    return resourceKey;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getresourceName() {
    return resourceName;
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