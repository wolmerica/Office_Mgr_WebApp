/*
 * BundleDetailDO.java
 *
 * Created on March 30, 2007, 07:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.bundledetail;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class BundleDetailDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer bundleKey = null;
  private Boolean releaseId = false;
  private Byte sourceTypeKey = new Byte("0");
  private Integer sourceKey = null;
  private String sourceName = "";
  private String sourceNum = "";
  private String categoryName = "";
  private BigDecimal size = null;
  private String sizeUnit = "";
  private Byte priceTypeKey = new Byte("0");
  private String priceTypeName = "";
  private BigDecimal computedPrice = new BigDecimal("0");
  private Short orderQty = new Short("0");
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

  public void setBundleKey(Integer bundleKey) {
    this.bundleKey = bundleKey;
  }

  public Integer getBundleKey() {
    return bundleKey;
  }

  public void setReleaseId(Boolean releaseId) {
    this.releaseId = releaseId;
  }

  public Boolean getReleaseId() {
    return releaseId;
  }

  public void setSourceTypeKey(Byte sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public Byte getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceKey(Integer sourceKey) {
    this.sourceKey = sourceKey;
  }

  public Integer getSourceKey() {
    return sourceKey;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceNum(String sourceNum) {
    this.sourceNum = sourceNum;
  }

  public String getSourceNum() {
    return sourceNum;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getCategoryName() {
    return categoryName;
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

  public void setPriceTypeKey(Byte priceTypeKey) {
    this.priceTypeKey = priceTypeKey;
  }

  public Byte getPriceTypeKey() {
    return priceTypeKey;
  }

  public void setPriceTypeName(String priceTypeName) {
    this.priceTypeName = priceTypeName;
  }

  public String getPriceTypeName() {
    return priceTypeName;
  }

  public void setComputedPrice(BigDecimal computedPrice) {
    this.computedPrice = computedPrice;
  }

  public BigDecimal getComputedPrice() {
    return computedPrice;
  }

  public void setOrderQty(Short orderQty) {
    this.orderQty = orderQty;
  }

  public Short getOrderQty() {
    return orderQty;
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