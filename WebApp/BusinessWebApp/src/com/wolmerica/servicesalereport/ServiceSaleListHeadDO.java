/*
 * ServiceSaleListHeadDO.java
 *
 * Created on August 21, 2006, 3:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.servicesalereport;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class ServiceSaleListHeadDO extends AbstractDO implements Serializable
{
  private Byte mode = new Byte("1");
  private String category = "";
  private Integer customerKey = null;
  private String clientName = "";
  private Date fromDate;
  private Date toDate;
  private Short recordCount = new Short("0");
  private Integer transactionTotal = new Integer("0");
  private BigDecimal serviceTaxTotal = new BigDecimal("0");
  private BigDecimal handlingTotal = new BigDecimal("0");
  private BigDecimal serviceTotal = new BigDecimal("0");
  private Short firstRecord = new Short("0");
  private Short lastRecord = new Short("0");
  private Short previousPage = new Short("0");
  private Short nextPage = new Short("0");
  private ArrayList serviceSaleForm;

  public void setMode(Byte mode) {
    this.mode = mode;
  }

  public Byte getMode() {
    return mode;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getCategory() {
    return category;
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

  public void setServiceTaxTotal(BigDecimal serviceTaxTotal) {
    this.serviceTaxTotal = serviceTaxTotal;
  }

  public BigDecimal getServiceTaxTotal() {
    return serviceTaxTotal;
  }

  public void setHandlingTotal(BigDecimal handlingTotal) {
    this.handlingTotal = handlingTotal;
  }

  public BigDecimal getHandlingTotal() {
    return handlingTotal;
  }

  public void setServiceTotal(BigDecimal serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public BigDecimal getServiceTotal() {
    return serviceTotal;
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

  public void setServiceSaleForm(ArrayList serviceSaleList){
  this.serviceSaleForm = serviceSaleList;
  }

  public ArrayList getServiceSaleForm(){
  return serviceSaleForm;
  }
}