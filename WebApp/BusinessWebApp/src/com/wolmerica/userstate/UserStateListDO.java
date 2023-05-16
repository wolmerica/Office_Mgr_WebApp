/*
 * UserStateListForm.java
 *
 * Created on July 25, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.userstate;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class UserStateListDO extends AbstractDO implements Serializable {

  private Integer key = null;    
  private String userName;
  private String tokenName;
  private String featureName;
  private Integer featureType;
  private Integer featureKey;
  private Timestamp createStamp = null;
  private Timestamp updateStamp = null;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }  
  
  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }

  public void setTokenName(String tokenName) {
    this.tokenName = tokenName;
  }

  public String getTokenName() {
    return tokenName;
  }

  public void setFeatureName(String featureName) {
    this.featureName = featureName;
  }

  public String getFeatureName() {
    return featureName;
  }

  public void setFeatureType(Integer featureType) {
    this.featureType = featureType;
  }

  public Integer getFeatureType() {
    return featureType;
  }

  public void setFeatureKey(Integer featureKey) {
    this.featureKey = featureKey;
  }

  public Integer getFeatureKey() {
    return featureKey;
  }

  public void setCreateStamp(Timestamp createStamp) {
    this.createStamp = createStamp;
  }

  public Timestamp getCreateStamp() {
    return createStamp;
  }

  public void setUpdateStamp(Timestamp updateStamp) {
    this.updateStamp = updateStamp;
  }

  public Timestamp getUpdateStamp() {
    return updateStamp;
  }
}

