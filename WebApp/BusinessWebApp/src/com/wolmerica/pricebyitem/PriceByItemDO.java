/*
 * PriceByItemDO.java
 *
 * Created on January 31, 2006, 10:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricebyitem;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PriceByItemDO extends AbstractDO implements Serializable
{

  private Integer key = null;
  private Byte priceTypeKey = null;
  private Byte customerTypeKey = null;
  private Integer priceAttributeByItemKey = null;
  private BigDecimal computedPrice = new BigDecimal("0");
  private BigDecimal previousPrice = new BigDecimal("0");  
  private BigDecimal overRidePrice = new BigDecimal("0");

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setPriceTypeKey(Byte priceTypeKey) {
    this.priceTypeKey = priceTypeKey;
  }

  public Byte getPriceTypeKey() {
    return priceTypeKey;
  }

  public void setCustomerTypeKey(Byte customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public Byte getCustomerTypeKey() {
    return customerTypeKey;
  }

  public void setPriceAttributeByItemKey(Integer priceAttributeByItemKey) {
    this.priceAttributeByItemKey = priceAttributeByItemKey;
  }

  public Integer getPriceAttributeByItemKey() {
    return priceAttributeByItemKey;
  }

  public void setComputedPrice(BigDecimal computedPrice) {
    this.computedPrice = computedPrice;
  }

  public BigDecimal getComputedPrice() {
    return computedPrice;
  }

  public void setPreviousPrice(BigDecimal previousPrice) {
    this.previousPrice = previousPrice;
  }

  public BigDecimal getPreviousPrice() {
    return previousPrice;
  }
    
  public void setOverRidePrice(BigDecimal overRidePrice) {
    this.overRidePrice = overRidePrice;
  }

  public BigDecimal getOverRidePrice() {
    return overRidePrice;
  }
}