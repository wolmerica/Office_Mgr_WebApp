/*
 * VendorInvoiceReportDetailListHeadDO.java
 *
 * Created on February 21, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoicereportdetail;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class VendorInvoiceReportDetailListHeadDO extends AbstractDO implements Serializable
{
  private Integer vendorKey = null;
  private String vendorName = "";
  private Date fromDate = new Date();
  private Date toDate = new Date();
  private Short recordCount = new Short("0");
  private Short firstRecord = new Short("0");
  private Short lastRecord = new Short("0");
  private Short previousPage = new Short("0");
  private Short nextPage = new Short("0");
  private ArrayList vendorInvoiceReportDetailForm;

  public void setVendorKey(Integer vendorKey) {
    this.vendorKey = vendorKey;
  }

  public Integer getVendorKey() {
    return vendorKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
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

  public void setRecordCount(Short recordCount) {
    this.recordCount = recordCount;
  }

  public Short getRecordCount() {
    return recordCount;
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

  public void setPreviousPage(Short previousPage ) {
    this.previousPage = previousPage;
  }

  public Short getPreviousPage() {
    return previousPage;
  }

  public void setNextPage(Short nextPage ) {
    this.nextPage = nextPage;
  }

  public Short getNextPage() {
    return nextPage;
  }

  public void setVendorInvoiceReportDetailForm(ArrayList vendorInvoiceReportDetailForm){
    this.vendorInvoiceReportDetailForm = vendorInvoiceReportDetailForm;
  }

  public ArrayList getVendorInvoiceReportDetailForm(){
    return vendorInvoiceReportDetailForm;
  }
}

