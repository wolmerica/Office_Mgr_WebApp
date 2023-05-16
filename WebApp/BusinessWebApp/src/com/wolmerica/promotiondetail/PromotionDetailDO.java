/*
 * PromotionDetailDO.java
 *
 * Created on April 04, 2009, 11:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.promotiondetail;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PromotionDetailDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer promotionKey = null;
  private Integer bundleKey = null;
  private String bundleName = "";
  private String bundleCategory = "";
  private Boolean comboDiscountId = false;
  private BigDecimal discountRate = new BigDecimal("0");
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

  public void setPromotionKey(Integer promotionKey) {
    this.promotionKey = promotionKey;
  }

  public Integer getPromotionKey() {
    return promotionKey;
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

  public void setBundleCategory(String bundleCategory) {
    this.bundleCategory = bundleCategory;
  }

  public String getBundleCategory() {
    return bundleCategory;
  }

  public void setComboDiscountId(Boolean comboDiscountId) {
    this.comboDiscountId = comboDiscountId;
  }

  public Boolean getComboDiscountId() {
    return comboDiscountId;
  }

  public void setDiscountRate(BigDecimal discountRate) {
    this.discountRate = discountRate;
  }

  public BigDecimal getDiscountRate(){
    return discountRate;
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