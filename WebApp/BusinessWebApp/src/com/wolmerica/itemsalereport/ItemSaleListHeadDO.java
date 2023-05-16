/*
 * ItemSaleListHeadDO.java
 *
 * Created on July 6, 2006, 9:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.itemsalereport;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class ItemSaleListHeadDO extends AbstractDO implements Serializable
{
  private Integer vendorKey = null;
  private String vendorName = "";
  private Integer customerKey = null;
  private String clientName = "";    
  private Byte mode = new Byte("1");
  private String manufacturer = "";
  private Date fromDate = null;
  private Date toDate = null;
  private Short recordCount = new Short("0");
  private Integer transactionTotal = new Integer("0");
  private BigDecimal salesTaxTotal = new BigDecimal("0");
  private BigDecimal handlingTotal = new BigDecimal("0");
  private BigDecimal itemTotal = new BigDecimal("0");
  private Short firstRecord = new Short("0");
  private Short lastRecord = new Short("0");
  private Short previousPage = new Short("0");
  private Short nextPage = new Short("0");
  private ArrayList itemSaleForm;

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

  public void setCustomerKey(Integer customerKey) {
    this.customerKey = customerKey;
  }

  public Integer getCustomerKey() {
    return customerKey;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }
  
  public void setMode(Byte mode) {
    this.mode = mode;
  }

  public Byte getMode() {
    return mode;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getManufacturer() {
    return manufacturer;
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

  public void setTransactionTotal(Integer transactionTotal) {
    this.transactionTotal = transactionTotal;
  }

  public Integer getTransactionTotal() {
    return transactionTotal;
  }

  public void setSalesTaxTotal(BigDecimal salesTaxTotal) {
    this.salesTaxTotal = salesTaxTotal;
  }

  public BigDecimal getSalesTaxTotal() {
    return salesTaxTotal;
  }

  public void setHandlingTotal(BigDecimal handlingTotal) {
    this.handlingTotal = handlingTotal;
  }

  public BigDecimal getHandlingTotal() {
    return handlingTotal;
  }

  public void setItemTotal(BigDecimal itemTotal) {
    this.itemTotal = itemTotal;
  }

  public BigDecimal getItemTotal() {
    return itemTotal;
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

  public void setItemSaleForm(ArrayList itemSaleList){
  this.itemSaleForm = itemSaleList;
  }

  public ArrayList getItemSaleForm(){
  return itemSaleForm;
  }
}