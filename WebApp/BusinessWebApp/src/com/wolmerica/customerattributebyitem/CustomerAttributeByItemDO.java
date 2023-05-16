/*
 * CustomerAttributeByItemDO.java
 *
 * Created on January 31, 2006, 10:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerattributebyitem;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerAttributeByItemDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Byte customerTypeKey = null;
  private String customerTypeName = "";
  private BigDecimal labelCost = new BigDecimal("0");
  private BigDecimal adminCost = new BigDecimal("0");
  private BigDecimal markUpRate = new BigDecimal("0");
  private BigDecimal additionalMarkUpRate = new BigDecimal("0");
  private BigDecimal discountThreshold = new BigDecimal("0");
  private BigDecimal discountRate = new BigDecimal("0");

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setCustomerTypeKey(Byte customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public Byte getCustomerTypeKey() {
    return customerTypeKey;
  }

  public void setCustomerTypeName(String customerTypeName) {
    this.customerTypeName = customerTypeName;
  }

  public String getCustomerTypeName() {
    return customerTypeName;
  }

  public void setLabelCost(BigDecimal labelCost) {
    this.labelCost = labelCost;
  }

  public BigDecimal getLabelCost() {
    return labelCost;
  }

  public void setAdminCost(BigDecimal adminCost) {
    this.adminCost = adminCost;
  }

  public BigDecimal getAdminCost() {
    return adminCost;
  }

  public void setMarkUpRate(BigDecimal markUpRate) {
    this.markUpRate = markUpRate;
  }

  public BigDecimal getMarkUpRate() {
    return markUpRate;
  }

  public void setAdditionalMarkUpRate(BigDecimal additionalMarkUpRate) {
    this.additionalMarkUpRate = additionalMarkUpRate;
  }

  public BigDecimal getAdditionalMarkUpRate() {
    return additionalMarkUpRate;
  }

  public void setDiscountThreshold(BigDecimal discountThreshold) {
    this.discountThreshold = discountThreshold;
  }

  public BigDecimal getDiscountThreshold() {
    return discountThreshold;
  }  
  
  public void setDiscountRate(BigDecimal discountRate) {
    this.discountRate = discountRate;
  }

  public BigDecimal getDiscountRate() {
    return discountRate;
  }
}