/*
 * PetVacDO.java
 *
 * Created on May 07, 2007, 09:12 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petvac;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PetVacDO extends AbstractDO implements Serializable {
  private Integer key = null;
  private Integer scheduleKey = null;
  private String clientName;
  private Integer petKey = null;  
  private String petName = "";
  private Integer resourceKey = null;
  private String resourceName = "";
  private Date vacDate = null;
  private Byte rabiesId = new Byte("0");
  private String rabiesTagNumber = "";
  private String vacRouteNumber = "";
  private String vacName = "";
  private String vacSerialNumber = "";
  private Date vacExpirationDate = null;
  private Byte canineDistemperId = new Byte("0");
  private Byte corunnaId = new Byte("0");
  private Byte felineDistemperId = new Byte("0");
  private Byte felineLeukemiaId = new Byte("0");
  private Byte otherId = new Byte("0");
  private Integer bundleKey = null;
  private String bundleName = "";
  private Integer reminderKey = null;
  private Date reminderDate = new Date();
  private String noteLine1 = "";
  private String permissionStatus = "";
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

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }
  
  public void setPetKey(Integer petKey) {
    this.petKey = petKey;
  }

  public Integer getPetKey() {
    return petKey;
  }  

  public void setPetName(String petName) {
    this.petName = petName;
  }

  public String getPetName() {
    return petName;
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

  public String getResourceName() {
    return resourceName;
  }

  public void setVacDate(Date vacDate) {
    this.vacDate = vacDate;
  }

  public Date getVacDate() {
    return vacDate;
  }

  public void setRabiesId(Byte rabiesId) {
    this.rabiesId = rabiesId;
  }

  public Byte getRabiesId() {
    return rabiesId;
  }

  public void setRabiesTagNumber(String rabiesTagNumber) {
    this.rabiesTagNumber = rabiesTagNumber;
  }

  public String getRabiesTagNumber() {
    return rabiesTagNumber;
  }

  public void setVacRouteNumber(String vacRouteNumber) {
    this.vacRouteNumber = vacRouteNumber;
  }

  public String getVacRouteNumber() {
    return vacRouteNumber;
  }

  public void setVacName(String vacName) {
    this.vacName = vacName;
  }

  public String getVacName() {
    return vacName;
  }

  public void setVacSerialNumber(String vacSerialNumber) {
    this.vacSerialNumber = vacSerialNumber;
  }

  public String getVacSerialNumber() {
    return vacSerialNumber;
  }

  public void setVacExpirationDate(Date vacExpirationDate) {
    this.vacExpirationDate = vacExpirationDate;
  }

  public Date getVacExpirationDate() {
    return vacExpirationDate;
  }

  public void setCanineDistemperId(Byte canineDistemperId) {
    this.canineDistemperId = canineDistemperId;
  }

  public Byte getCanineDistemperId() {
    return canineDistemperId;
  }

  public void setCorunnaId(Byte corunnaId) {
    this.corunnaId = corunnaId;
  }

  public Byte getCorunnaId() {
    return corunnaId;
  }

  public void setFelineDistemperId(Byte felineDistemperId) {
    this.felineDistemperId = felineDistemperId;
  }

  public Byte getFelineDistemperId() {
    return felineDistemperId;
  }

  public void setFelineLeukemiaId(Byte felineLeukemiaId) {
    this.felineLeukemiaId = felineLeukemiaId;
  }

  public Byte getFelineLeukemiaId() {
    return felineLeukemiaId;
  }

  public void setOtherId(Byte otherId) {
    this.otherId = otherId;
  }

  public Byte getOtherId() {
    return otherId;
  }

  public void setBundleKey(Integer bundleKey) {
    this.bundleKey = bundleKey;
  }

  public Integer getBundleKey() {
    return bundleKey;
  }

  public void setBundleName(String bundleName) {
    this.bundleName = bundleName;
  }

  public String getBundleName() {
    return bundleName;
  }

  public void setReminderKey(Integer reminderKey) {
    this.reminderKey = reminderKey;
  }

  public Integer getReminderKey() {
    return reminderKey;
  }

  public void setReminderDate(Date reminderDate) {
    this.reminderDate = reminderDate;
  }

  public Date getReminderDate() {
    return reminderDate;
  }

  public void setNoteLine1(String noteLine1) {

    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
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
