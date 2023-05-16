/*
 * ResourceDO.java
 *
 * Created on September 10, 2006, 4:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.resource;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class ResourceDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String resourceName = "";
  private String description = ""; 
  private String permissionStatus = "";  
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;
  private Boolean allowDeleteId = false;  
  private Integer scheduleKey = null;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getResourceName() {
    return resourceName;
  }  

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
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
  
  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }    
  
  public void setScheduleKey(Integer scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public Integer getScheduleKey() {
    return scheduleKey;
  }  
}
