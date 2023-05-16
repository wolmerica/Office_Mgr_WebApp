/*
 * VendorInvoiceDueListHeadDO.java
 *
 * Created on August 24, 2006, 9:32 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoicedue;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class VendorInvoiceDueListHeadDO extends AbstractDO implements Serializable
{
  private Integer vendorKey = null;
  private String vendorName = "";
  private Date fromDate = null;
  private Date toDate = null;
  private Date payDate1 = null;
  private Date payDate2 = null;
  private Date payDate3 = null;
  private Integer dayAverage1 = new Integer("0");
  private Integer dayAverage2 = new Integer("0");
  private Integer dayAverage3 = new Integer("0");
  private Short recordCount = new Short("0");
  private BigDecimal debitTotal = new BigDecimal("0");
  private BigDecimal creditTotal = new BigDecimal("0");
  private BigDecimal balanceTotal = new BigDecimal("0");
  private Short firstRecord = new Short("0");
  private Short lastRecord = new Short("0");
  private ArrayList vendorInvoiceDueListForm;

  public void setVendorKey(Integer vendorKey) {
    this.vendorKey = vendorKey;
  }

  public Integer getVendorKey() {
    return vendorKey;
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

  public void setPayDate1(Date payDate1) {
    this.payDate1 = payDate1;
  }

  public Date getPayDate1() {
    return payDate1;
  }

  public void setPayDate2(Date payDate2) {
    this.payDate2 = payDate2;
  }

  public Date getPayDate2() {
    return payDate2;
  }

  public void setPayDate3(Date payDate3) {
    this.payDate3 = payDate3;
  }

  public Date getPayDate3() {
    return payDate3;
  }

  public void setDayAverage1(Integer dayAverage1) {
    this.dayAverage1 = dayAverage1;
  }

  public Integer getDayAverage1() {
    return dayAverage1;
  }

  public void setDayAverage2(Integer dayAverage2) {
    this.dayAverage2 = dayAverage2;
  }

  public Integer getDayAverage2() {
    return dayAverage2;
  }

  public void setDayAverage3(Integer dayAverage3) {
    this.dayAverage3 = dayAverage3;
  }

  public Integer getDayAverage3() {
    return dayAverage3;
  }

  public void setRecordCount(Short recordCount) {
    this.recordCount = recordCount;
  }

  public Short getRecordCount() {
    return recordCount;
  }

  public void setDebitTotal(BigDecimal debitTotal) {
    this.debitTotal = debitTotal;
  }

  public BigDecimal getDebitTotal() {
    return debitTotal;
  }

  public void setCreditTotal(BigDecimal creditTotal) {
    this.creditTotal = creditTotal;
  }

  public BigDecimal getCreditTotal() {
    return creditTotal;
  }

  public void setBalanceTotal(BigDecimal balanceTotal) {
    this.balanceTotal = balanceTotal;
  }

  public BigDecimal getBalanceTotal() {
    return balanceTotal;
  }

  public void setFirstRecord(Short firstRecord) {
    this.firstRecord = firstRecord;
  }

  public Short getFirstRecord() {
    return firstRecord;
  }

  public void setLastRecord(Short lastRecord) {
    this.lastRecord = lastRecord;
  }

  public Short getLastRecord() {
    return lastRecord;
  }

  public void setVendorInvoiceDueListForm(ArrayList vendorInvoiceDueListForm){
  this.vendorInvoiceDueListForm = vendorInvoiceDueListForm;
  }

  public ArrayList getVendorInvoiceDueListForm(){
  return vendorInvoiceDueListForm;
  }
}