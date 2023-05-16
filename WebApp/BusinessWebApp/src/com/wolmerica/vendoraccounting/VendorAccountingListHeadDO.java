/*
 * VendorAccountingListHeadDO.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.vendoraccounting;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;

public class VendorAccountingListHeadDO extends AbstractDO implements Serializable
{
  private Integer vendorKeyFilter = null;
  private String vendorName = "";
  private Date fromDate = new Date();
  private Date toDate = new Date();
  private Integer recordCount = new Integer("0");
  private BigDecimal pageTotal = new BigDecimal("0");
  private BigDecimal balanceTotal = new BigDecimal("0");
  private Boolean allowPaymentId = false;
  private Boolean allowRefundId = false;      
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer currentPage = new Integer("1");
  private Integer nextPage = new Integer("0");
  private ArrayList vendorAccountingListForm;
  private ArrayList permissionListForm;

  public void setVendorKeyFilter(Integer vendorKeyFilter) {
    this.vendorKeyFilter = vendorKeyFilter;
  }

  public Integer getVendorKeyFilter() {
    return vendorKeyFilter;
  }

  public void setVendorName(String vendorName) {
    this.vendorName =vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public Date getFromDate() {
    return fromDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

  public Date getToDate() {
    return toDate;
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

  public void setBalanceTotal(BigDecimal balanceTotal) {
    this.balanceTotal = balanceTotal;
  }

  public BigDecimal getBalanceTotal() {
    return balanceTotal;
  }
  
  public void setAllowPaymentId(Boolean allowPaymentId) {
    this.allowPaymentId = allowPaymentId;
  }

  public Boolean getAllowPaymentId() {
    return allowPaymentId;
  }

  public void setAllowRefundId(Boolean allowRefundId) {
    this.allowRefundId = allowRefundId;
  }

  public Boolean getAllowRefundId() {
    return allowRefundId;
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

  public void setCurrentPage(Integer currentPage ) {
    this.currentPage = currentPage;
  }

  public Integer getCurrentPage() {
    return currentPage;
  }
    
  public void setNextPage(Integer nextPage ) {
    this.nextPage = nextPage;
  }

  public Integer getNextPage() {
    return nextPage;
  }

  public void setVendorAccountingListForm(ArrayList vendorAccountingListForm){
  this.vendorAccountingListForm = vendorAccountingListForm;
  }

  public ArrayList getVendorAccountingListForm(){
  return vendorAccountingListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}