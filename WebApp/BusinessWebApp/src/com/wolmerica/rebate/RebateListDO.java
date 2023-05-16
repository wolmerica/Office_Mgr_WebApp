/*
 * RebateListDO.java
 *
 * Created on May 27, 2006, 10:5 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.rebate;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class RebateListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer itemDictionaryKey = null;
  private String brandName = "";
  private BigDecimal size = null;
  private String sizeUnit = "";
  private String offerName = "";
  private Date startDate = null;
  private Date endDate = null;
  private Date submitDate = null;
  private BigDecimal amount = new BigDecimal("0");
  private Short rebateInstanceCount = null;  
  private Boolean allowDeleteId = false;    

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

  public void setOfferName(String offerName) {
    this.offerName = offerName;
  }

  public String getOfferName() {
    return offerName;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setSubmitDate(Date submitDate) {
    this.submitDate = submitDate;
  }

  public Date getSubmitDate() {
    return submitDate;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getAmount() {
    return amount;
  }
  
  public void setRebateInstanceCount(Short rebateInstanceCount) {
    this.rebateInstanceCount = rebateInstanceCount;
  }

  public Short getRebateInstanceCount() {
    return rebateInstanceCount;
  }
  
  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }   
  
}

