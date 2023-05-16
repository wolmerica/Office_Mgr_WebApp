/*
 * BundleDO.java
 *
 * Created on March 30, 2007, 7:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.bundle;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.sql.Timestamp;

public class BundleDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String name = "";
  private String category = "";
  private ArrayList customerTypeForm;
  private Byte customerTypeKey = new Byte("1");
  private Boolean releaseId = false;
  private Byte durationHour = null;
  private Byte durationMinute = null;
  private String permissionStatus = "";
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

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getCategory() {
    return category;
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

  public void setReleaseId(Boolean releaseId) {
    this.releaseId = releaseId;
  }

  public Boolean getReleaseId() {
    return releaseId;
  }

  public void setDurationHour(Byte durationHour) {
    this.durationHour = durationHour;
  }

  public Byte getDurationHour() {
    return durationHour;
  }

  public void setDurationMinute(Byte durationMinute) {
    this.durationMinute = durationMinute;
  }

  public Byte getDurationMinute() {
    return durationMinute;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
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