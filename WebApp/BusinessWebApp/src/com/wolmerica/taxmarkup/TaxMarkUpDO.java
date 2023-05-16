/*
 * TaxMarkUpDO.java
 *
 * Created on September 21, 2005, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/27/2005 Implement tools.formatter library.
 */

package com.wolmerica.taxmarkup;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class TaxMarkUpDO extends AbstractDO implements Serializable {

  private Byte key = null;
  private String name = "";
  private String description = "";
  private BigDecimal value = new BigDecimal("0");
  private Boolean percentageId = true;
  private Byte precedence = null;
  private Boolean activeId = true;   
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

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public BigDecimal getValue() {
    return value;
  }
  
  public void setPercentageId(Boolean percentageId) {
    this.percentageId = percentageId;
  }

  public Boolean getPercentageId() {
    return percentageId;
  }
  
  public void setPrecedence(Byte precedence) {
    this.precedence = precedence;
  }

  public Byte getPrecedence() {
    return precedence;
  }
  
  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
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

