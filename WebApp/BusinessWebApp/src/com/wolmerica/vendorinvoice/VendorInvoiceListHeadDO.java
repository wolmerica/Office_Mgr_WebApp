/*
 * VendorInvoiceListHeadDO.java
 *
 * Created on February 12, 2006, 4:12 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoice;

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

public class VendorInvoiceListHeadDO extends AbstractDO implements Serializable
{
  private String vendorNameFilter = "";
  private String invoiceNumberFilter = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");
  private ArrayList vendorInvoiceListForm;
  private ArrayList permissionListForm;

  public void setVendorNameFilter(String vendorNameFilter) {
    this.vendorNameFilter = vendorNameFilter;
  }

  public String getVendorNameFilter() {
    return vendorNameFilter;
  }

  public void setInvoiceNumberFilter(String invoiceNumberFilter) {
    this.invoiceNumberFilter = invoiceNumberFilter;
  }

  public String getInvoiceNumberFilter() {
    return invoiceNumberFilter;
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

  public void setVendorInvoiceListForm(ArrayList vendorInvoiceListForm){
    this.vendorInvoiceListForm=vendorInvoiceListForm;
  }

  public ArrayList getVendorInvoiceListForm(){
    return vendorInvoiceListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}

