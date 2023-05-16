/*
 * VendorInvoiceDueListDO.java
 *
 * Created on March 28, 2006, 9:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoicedue;

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

public class VendorInvoiceDueListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer purchaseOrderKey = null;
  private Integer masterKey = new Integer("-1");
  private Integer genesisKey = new Integer("-1");
  private String purchaseOrderNumber = "";
  private String invoiceNumber = "";
  private Date invoiceDate = new Date();
  private Date invoiceDueDate = new Date();
  private Integer dayCount = new Integer("0");
  private Integer dayAverage = new Integer("0");
  private BigDecimal invoiceAmount = new BigDecimal("0");

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setPurchaseOrderKey(Integer purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public Integer getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setMasterKey(Integer masterKey) {
    this.masterKey = masterKey;
  }

  public Integer getMasterKey() {
    return masterKey;
  }

  public void setGenesisKey(Integer genesisKey) {
    this.genesisKey = genesisKey;
  }

  public Integer getGenesisKey() {
    return genesisKey;
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDueDate(Date invoiceDueDate) {
    this.invoiceDueDate = invoiceDueDate;
  }

  public Date getInvoiceDueDate() {
    return invoiceDueDate;
  }

  public void setDayCount(Integer dayCount) {
    this.dayCount = dayCount;
  }

  public Integer getDayCount() {
    return dayCount;
  }

  public void setDayAverage(Integer dayAverage) {
    this.dayAverage = dayAverage;
  }

  public Integer getDayAverage() {
    return dayAverage;
  }

  public void setInvoiceAmount(BigDecimal invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public BigDecimal getInvoiceAmount(){
    return invoiceAmount;
  }
}