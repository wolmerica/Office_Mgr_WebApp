/*
 * PriceTypeDO.java
 *
 * Created on March 14, 2006, 8:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.pricetype;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PriceTypeDO extends AbstractDO implements Serializable {

  private Byte key = null;
  private String name = "";
  private Boolean domainId = false;
  private Boolean fullSizeId = false;
  private Boolean unitCostBaseId = false;
  private Boolean blueBookId = false;
  private BigDecimal markUpRate = new BigDecimal("0");
  private Byte precedence = null;
  private BigDecimal serviceRate = new BigDecimal("0");  
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
  
  public void setDomainId(Boolean domainId) {
    this.domainId = domainId;
  }

  public Boolean getDomainId() {
    return domainId;
  }  

  public void setFullSizeId(Boolean fullSizeId) {
    this.fullSizeId = fullSizeId;
  }

  public Boolean getFullSizeId() {
    return fullSizeId;
  }

  public void setUnitCostBaseId(Boolean unitCostBaseId) {
    this.unitCostBaseId = unitCostBaseId;
  }

  public Boolean getUnitCostBaseId() {
    return unitCostBaseId;
  }

  public void setBlueBookId(Boolean blueBookId) {
    this.blueBookId = blueBookId;
  }

  public Boolean getBlueBookId() {
    return blueBookId;
  }

  public void setMarkUpRate(BigDecimal markUpRate) {
    this.markUpRate = markUpRate;
  }

  public BigDecimal getMarkUpRate() {
    return markUpRate;
  }

  public void setPrecedence(Byte precedence) {
    this.precedence = precedence;
  }

  public Byte getPrecedence() {
    return precedence;
  }

  public void setServiceRate(BigDecimal serviceRate) {
    this.serviceRate = serviceRate;
  }

  public BigDecimal getServiceRate() {
    return serviceRate;
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

