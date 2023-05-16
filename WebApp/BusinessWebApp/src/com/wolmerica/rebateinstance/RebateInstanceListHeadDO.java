/*
 * RebateInstanceListHeadDO.java
 *
 * Created on May 29, 2006, 8:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.rebateinstance;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class RebateInstanceListHeadDO extends AbstractDO implements Serializable
{
  private String offerNameFilter = "";
  private Integer recordCount = new Integer("0");
  private BigDecimal pageTotal = new BigDecimal("0");  
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");
  private ArrayList rebateInstanceForm;
  private ArrayList permissionListForm;

  public void setOfferNameFilter(String offerNameFilter) {
    this.offerNameFilter = offerNameFilter;
  }

  public String getOfferNameFilter() {
    return offerNameFilter;
  }

  public void setRecordCount(Integer recordCount) {
    this.recordCount = recordCount;
  }

  public Integer getRecordCount() {
    return recordCount;
  }
  
  public void setPageTotal(BigDecimal pageTotal) {
    this.pageTotal = pageTotal;
  }

  public BigDecimal getPageTotal() {
    return pageTotal;
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

  public void setRebateInstanceForm(ArrayList rebateInstanceList){
  this.rebateInstanceForm = rebateInstanceList;
  }

  public ArrayList getRebateInstanceForm(){
  return rebateInstanceForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}