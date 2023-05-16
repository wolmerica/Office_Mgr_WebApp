/*
 * SpeciesListForm.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.species;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.sql.Timestamp;

public class SpeciesDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String speciesName = "";
  private String speciesExtId = "";
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

  public void setSpeciesName(String speciesName) {
    this.speciesName = speciesName;
  }

  public String getSpeciesName() {
    return speciesName;
  }

  public void setSpeciesExtId(String speciesExtId) {
    this.speciesExtId = speciesExtId;
  }

  public String getSpeciesExtId() {
    return speciesExtId;
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

