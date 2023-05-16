/*
 * PriceAttributeByItemDO.java
 *
 * Created on January 31, 2006, 10:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.priceattributebyitem;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PriceAttributeByItemDO extends AbstractDO implements Serializable
{

  private Integer key;
  private Byte priceTypeKey;
  private String priceTypeName;
  private Boolean fullSize;
  private BigDecimal size;

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

  public void setFullSize(Boolean fullSize) {
    this.fullSize = fullSize;
  }

  public Boolean getfullSize() {
    return fullSize;
  }

  public void setSize(BigDecimal size) {
    this.size = size;
  }

  public BigDecimal getSize() {
    return size;
  }
}