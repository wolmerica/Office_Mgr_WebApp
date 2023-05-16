/*
 * ItemDictionaryListDO.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.itemdictionary;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;

public class ItemDictionaryListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String brandName = "";
  private BigDecimal size = new BigDecimal("0");
  private String sizeUnit = "";
  private String manufacturer = "";
  private String itemNum = "";
  private String profileNum = "";
  private String genericName = "";
  private BigDecimal firstCost = new BigDecimal("0");
  private BigDecimal unitCost = new BigDecimal("0");
  private Integer qtyOnHand = new Integer("0");
  private Boolean sellableItemId = false;
  private Byte sourceTypeKey = new Byte("5");
  private Integer sourceKey = null;
  private Short rebateCount = null;
  private Short rebateInstanceCount = null;
  private Boolean belowThresholdId = false;
  private BigDecimal itemUnitsSold = null;
  private Boolean allowDeleteId = false;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
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

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setItemNum(String itemNum) {
    this.itemNum = itemNum;
  }

  public String getItemNum() {
    return itemNum;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }
  
  public void setGenericName(String genericName) {
    this.genericName = genericName;
  }

  public String getGenericName() {
    return genericName;
  }

  public void setFirstCost(BigDecimal firstCost) {
    this.firstCost = firstCost;
  }

  public BigDecimal getFirstCost() {
    return firstCost;
  }

  public void setUnitCost(BigDecimal unitCost) {
    this.unitCost = unitCost;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public void setQtyOnHand(Integer qtyOnHand) {
    this.qtyOnHand = qtyOnHand;
  }

  public Integer getQtyOnHand() {
    return qtyOnHand;
  }

  public void setSellableItemId(Boolean sellableItemId) {
    this.sellableItemId = sellableItemId;
  }

  public Boolean getSellableItemId() {
    return sellableItemId;
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

  public void setRebateCount(Short rebateCount) {
    this.rebateCount = rebateCount;
  }

  public Short getRebateCount() {
    return rebateCount;
  }

  public void setRebateInstanceCount(Short rebateInstanceCount) {
    this.rebateInstanceCount = rebateInstanceCount;
  }

  public Short getRebateInstanceCount() {
    return rebateInstanceCount;
  }

  public void setBelowThresholdId(Boolean belowThresholdId) {
    this.belowThresholdId = belowThresholdId;
  }

  public Boolean getBelowThresholdId() {
    return belowThresholdId;
  }

  public void setItemUnitsSold(BigDecimal itemUnitsSold) {
    this.itemUnitsSold = itemUnitsSold;
  }

  public BigDecimal getItemUnitsSold() {
    return itemUnitsSold;
  }

  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }

}

