/*
 * CustomerAttributeByServiceDO.java
 *
 * Created on August 08, 2006, 2:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerattributebyservice;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerAttributeByServiceDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Byte customerTypeKey = null;
  private String customerTypeName = "";
  private BigDecimal fee1Cost = new BigDecimal("0");
  private BigDecimal fee2Cost = new BigDecimal("0");
  private BigDecimal markUp1Rate = new BigDecimal("0");
  private BigDecimal markUp2Rate = new BigDecimal("0");
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

  public void setFee1Cost(BigDecimal fee1Cost) {
    this.fee1Cost = fee1Cost;
  }

  public BigDecimal getFee1Cost() {
    return fee1Cost;
  }

  public void setFee2Cost(BigDecimal fee2Cost) {
    this.fee2Cost = fee2Cost;
  }

  public BigDecimal getFee2Cost() {
    return fee2Cost;
  }

  public void setMarkUp1Rate(BigDecimal markUp1Rate) {
    this.markUp1Rate = markUp1Rate;
  }

  public BigDecimal getMarkUp1Rate() {
    return markUp1Rate;
  }

  public void setMarkUp2Rate(BigDecimal markUp2Rate) {
    this.markUp2Rate = markUp2Rate;
  }

  public BigDecimal getMarkUp2Rate() {
    return markUp2Rate;
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