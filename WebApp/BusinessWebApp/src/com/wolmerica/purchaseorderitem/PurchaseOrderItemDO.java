/*
 * PurchaseOrderItemDO.java
 *
 * Created on January 31, 2006, 10:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.purchaseorderitem;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;

public class PurchaseOrderItemDO extends AbstractDO implements Serializable
{

  private Integer key = null;
  private Integer itemDictionaryKey = null;
  private String itemNum = "";
  private String brandName = "";
  private BigDecimal size = null;
  private String sizeUnit = "";
  private Short orderQty = null;
  private BigDecimal firstCost = null;
  private BigDecimal extendCost = null;
  private String itemAction = "";
  private String noteLine1 = "";
  private Short rebateCount = null;
  private Short rebateInstanceCount = null;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
   return key;
  }

  public void setItemDictionaryKey(Integer itemDictionaryKey) {
    this.itemDictionaryKey = itemDictionaryKey;
  }

  public Integer getItemDictionaryKey() {
    return itemDictionaryKey;
  }

  public void setItemNum(String itemNum) {
    this.itemNum = itemNum;
  }

  public String getItemNum() {
    return itemNum;
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

  public void setOrderQty(Short orderQty) {
    this.orderQty = orderQty;
  }

  public Short getOrderQty() {
    return orderQty;
  }

  public void setFirstCost(BigDecimal firstCost) {
    this.firstCost = firstCost;
  }

  public BigDecimal getFirstCost() {
    return firstCost;
  }

  public void setExtendCost(BigDecimal extendCost) {
    this.extendCost = extendCost;
  }

  public BigDecimal getExtendCost() {
    return extendCost;
  }

  public void setItemAction(String itemAction) {
    this.itemAction = itemAction;
  }

  public String getItemAction() {
    return itemAction;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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
}