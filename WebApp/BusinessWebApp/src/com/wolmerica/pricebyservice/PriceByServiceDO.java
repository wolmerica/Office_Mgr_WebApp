/*
 * PriceByServiceDO.java
 *
 * Created on August 09, 2006, 12:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricebyservice;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;

public class PriceByServiceDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Byte priceTypeKey = null;  
  private String priceTypeName = "";
  private Byte customerTypeKey = null;  
  private Integer customerAttributeByServiceKey = null;
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
  
  public void setPriceTypeName(String priceTypeName) {
    this.priceTypeName = priceTypeName;
  }

  public String getPriceTypeName() {
    return priceTypeName;
  }  

  public void setCustomerTypeKey(Byte customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public Byte getCustomerTypeKey() {
    return customerTypeKey;
  }  
  
  public void setCustomerAttributeByServiceKey(Integer customerAttributeByServiceKey) {
    this.customerAttributeByServiceKey = customerAttributeByServiceKey;
  }

  public Integer getCustomerAttributeByServiceKey() {
    return customerAttributeByServiceKey;
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