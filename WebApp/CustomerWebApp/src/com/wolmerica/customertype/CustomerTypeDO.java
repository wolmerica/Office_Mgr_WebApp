/*
 * CustomerTypeDO.java
 *
 * Created on March 14, 2006, 8:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customertype;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerTypeDO extends AbstractDO implements Serializable {

  private Byte key = null;
  private String name = "";
  private Byte precedence = null;
  private Boolean blueBookId = false;
  private Boolean priceSheetId = false;
  private Boolean activeId = true;
  private Integer soldByKey = null;
  private String soldByName = "";
  private String permissionStatus = "";
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;

  public void setKey(Byte key) {
    this.key = key;
  }

  public Byte getKey() {
    return key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setPrecedence(Byte precedence) {
    this.precedence = precedence;
  }

  public Byte getPrecedence() {
    return precedence;
  }

  public void setBlueBookId(Boolean blueBookId) {
    this.blueBookId = blueBookId;
  }

  public Boolean getBlueBookId() {
    return blueBookId;
  }

  public void setPriceSheetId(Boolean priceSheetId) {
    this.priceSheetId = priceSheetId;
  }

  public Boolean getPriceSheetId() {
    return priceSheetId;
  }

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }

  public void setSoldByKey(Integer soldByKey) {
    this.soldByKey = soldByKey;
  }

  public Integer getSoldByKey() {
    return soldByKey;
  }

  public void setSoldByName(String soldByName) {
    this.soldByName = soldByName;
  }

  public String getSoldByName() {
    return soldByName;
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