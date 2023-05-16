/*
 * CustomerAccountingListHeadDO.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.customeraccounting;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerAccountingListHeadDO extends AbstractDO implements Serializable
{
  private Integer customerKeyFilter = null;
  private String clientName = "";
  private String accountName = "";
  private Date fromDate = null;
  private Date toDate = null;
  private Integer recordCount = new Integer("0");
  private BigDecimal pageTotal = new BigDecimal("0");
  private BigDecimal balanceTotal = new BigDecimal("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer currentPage = new Integer("1");
  private Integer nextPage = new Integer("0");
  private ArrayList customerAccountingListForm;

  public void setCustomerKeyFilter(Integer customerKeyFilter) {
    this.customerKeyFilter = customerKeyFilter;
  }

  public Integer getCustomerKeyFilter() {
    return customerKeyFilter;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setAccountName(String accountName) {
    this.accountName =accountName;
  }

  public String getAccountName() {
    return accountName;
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
  
  public void setCustomerAccountingListForm(ArrayList customerAccountingListForm){
  this.customerAccountingListForm = customerAccountingListForm;
  }

  public ArrayList getCustomerAccountingListForm(){
  return customerAccountingListForm;
  }

}