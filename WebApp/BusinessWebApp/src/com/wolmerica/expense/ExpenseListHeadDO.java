/*
 * ExpenseListHeadDO.java
 *
 * Created on July 02, 2006, 3:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.expense;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;

public class ExpenseListHeadDO extends AbstractDO implements Serializable
{
  private String expenseNameFilter = "";
  private Byte mode = new Byte("1");
  private Date fromDate = new Date();
  private Date toDate = new Date();
  private Integer recordCount = new Integer("0");
  private BigDecimal paymentPageTotal = new BigDecimal("0");
  private BigDecimal paymentGrandTotal = new BigDecimal("0");
  private BigDecimal expensePageTotal = new BigDecimal("0");
  private BigDecimal expenseGrandTotal = new BigDecimal("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer currentPage = new Integer("1");
  private Integer nextPage = new Integer("0");
  private ArrayList expenseListForm;
  private ArrayList permissionListForm;

  public void setExpenseNameFilter(String expenseNameFilter) {
    this.expenseNameFilter = expenseNameFilter;
  }

  public String getExpenseNameFilter() {
    return expenseNameFilter;
  }

  public void setMode(Byte mode) {
    this.mode = mode;
  }

  public Byte getMode() {
    return mode;
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

  public void setPaymentPageTotal(BigDecimal paymentPageTotal) {
    this.paymentPageTotal = paymentPageTotal;
  }

  public BigDecimal getPaymentPageTotal() {
    return paymentPageTotal;
  }

  public void setPaymentGrandTotal(BigDecimal paymentGrandTotal) {
    this.paymentGrandTotal = paymentGrandTotal;
  }

  public BigDecimal getPaymentGrandTotal() {
    return paymentGrandTotal;
  }

  public void setExpensePageTotal(BigDecimal expensePageTotal) {
    this.expensePageTotal = expensePageTotal;
  }

  public BigDecimal getExpensePageTotal() {
    return expensePageTotal;
  }

  public void setExpenseGrandTotal(BigDecimal expenseGrandTotal) {
    this.expenseGrandTotal = expenseGrandTotal;
  }

  public BigDecimal getExpenseGrandTotal() {
    return expenseGrandTotal;
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

  public void setExpenseListForm(ArrayList expenseListForm){
    this.expenseListForm=expenseListForm;
  }

  public ArrayList getExpenseListForm(){
    return expenseListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}

