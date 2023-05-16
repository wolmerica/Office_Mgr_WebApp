/*
 * ItemOriginListHeadDO.java
 *
 * Created on January 17, 2008, 8:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.itemorigin;

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

public class ItemOriginListHeadDO extends AbstractDO implements Serializable
{
  private Byte originMode = null;
  private Integer itemDictionaryKey = null;
  private String brandName = "";
  private BigDecimal size = new BigDecimal("0");
  private String sizeUnit = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");  
  private ArrayList itemOriginForm;

  public void setOriginMode(Byte originMode) {
    this.originMode = originMode;
  }

  public Byte getOriginMode() {
    return originMode;
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
  
  public void setRecordCount(Integer recordCount) {
    this.recordCount = recordCount;
  }

  public Integer getRecordCount() {
    return recordCount;
  }

  public void setFirstRecord(Integer firstRecord) {
    this.firstRecord = firstRecord;
  }

  public Integer getFirstRecord() {
    return firstRecord;
  }

  public void setLastRecord(Integer lastRecord) {
    this.lastRecord = lastRecord;
  }

  public Integer getLastRecord() {
    return lastRecord;
  }

  public void setPreviousPage(Integer previousPage ) {
    this.previousPage = previousPage;
  }

  public Integer getPreviousPage() {
    return previousPage;
  }

  public void setNextPage(Integer nextPage ) {
    this.nextPage = nextPage;
  }

  public Integer getNextPage() {
    return nextPage;
  }  

  public void setItemOriginForm(ArrayList itemOriginForm){
    this.itemOriginForm=itemOriginForm;
  }

  public ArrayList getItemOriginForm(){
    return itemOriginForm;
  }
}