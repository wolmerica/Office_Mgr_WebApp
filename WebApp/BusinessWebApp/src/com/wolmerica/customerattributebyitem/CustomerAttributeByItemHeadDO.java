/*
 * CustomerAttributeByItemHeadDO.java
 *
 * Created on January 31, 2005, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.customerattributebyitem;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerAttributeByItemHeadDO extends AbstractDO implements Serializable
{
  private Integer itemDictionaryKey;
  private String brandName;
  private BigDecimal size;
  private String sizeUnit;
  private ArrayList customerAttributeByItemForm;

  public void setItemDictionaryKey(Integer itemDictionaryKey) {
    this.itemDictionaryKey = itemDictionaryKey;
  }

  public Integer getItemDictionaryKey() {
    return itemDictionaryKey;
  }

  public void setBrandName(String brandName ) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setSize(BigDecimal size ) {
    this.size = size;
  }

  public BigDecimal getSize() {
    return size;
  }

  public void setSizeUnit(String sizeUnit ) {
    this.sizeUnit = sizeUnit;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

  public void setCustomerAttributeByItemForm(ArrayList customerAttributeByItemForm){
    this.customerAttributeByItemForm = customerAttributeByItemForm;
  }

  public ArrayList getCustomerAttributeByItemForm(){
    return customerAttributeByItemForm;
  }
}