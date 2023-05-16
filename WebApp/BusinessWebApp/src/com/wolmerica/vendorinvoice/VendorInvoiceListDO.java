/*
 * VendorInvoiceListForm.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoice;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class VendorInvoiceListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer purchaseOrderKey = null;
  private String purchaseOrderNumber = "";
  private String vendorName = "";
  private String invoiceNumber = "";
  private java.sql.Date invoiceDate = null;
  private java.sql.Date invoiceDueDate = null;
  private BigDecimal invoiceTotal = new BigDecimal("0");
  private Boolean salesTaxId = false;
  private Boolean activeId = true;
  private Boolean masterInvoiceId;
  private Boolean directShipId = false;
  private Short customerInvoiceCount = null;
  private String balanceQty = "";
  private String balanceAmount = "";

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

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceDate(java.sql.Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public java.sql.Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDueDate(java.sql.Date invoiceDueDate) {
    this.invoiceDueDate = invoiceDueDate;
  }

  public java.sql.Date getInvoiceDueDate() {
    return invoiceDueDate;
  }

  public void setInvoiceTotal(BigDecimal invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public BigDecimal getInvoiceTotal(){
    return invoiceTotal;
  }

  public void setSalesTaxId(Boolean salesTaxId) {
    this.salesTaxId = salesTaxId;
  }

  public Boolean getSalesTaxId() {
    return salesTaxId;
  }

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }

  public void setMasterInvoiceId(Boolean masterInvoiceId) {
    this.masterInvoiceId = masterInvoiceId;
  }

  public Boolean getMasterInvoiceId() {
    return masterInvoiceId;
  }

  public void setDirectShipId(Boolean directShipId) {
    this.directShipId = directShipId;
  }

  public Boolean getDirectShipId() {
    return directShipId;
  }

  public void setCustomerInvoiceCount(Short customerInvoiceCount) {
    this.customerInvoiceCount = customerInvoiceCount;
  }

  public Short getCustomerInvoiceCount() {
    return customerInvoiceCount;
  }

  public void setBalanceQty(String balanceQty) {
    this.balanceQty = balanceQty;
  }

  public String getBalanceQty() {
    return balanceQty;
  }

  public void setBalanceAmount(String balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public String getBalanceAmount() {
    return balanceAmount;
  }
}