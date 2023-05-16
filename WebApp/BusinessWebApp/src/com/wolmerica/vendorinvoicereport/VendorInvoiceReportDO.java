/*
 * VendorInvoiceReportDO.java
 *
 * Created on February 21, 2007, 10:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoicereport;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VendorInvoiceReportDO extends AbstractDO implements Serializable
{
  private Integer purchaseOrderKey = null;
  private Integer vendorInvoiceKey = null;
  private Integer vendorKey = null;
  private String vendorName = "";
  private Date invoiceDate = new Date();
  private String invoiceNumber = "";
  private String noteLine1 = "";
  private String noteLine2 = "";
  private String noteLine3 = "";
  private BigDecimal subTotal = new BigDecimal("0");
  private BigDecimal salesTaxCost = new BigDecimal("0");
  private BigDecimal serviceTaxCost = new BigDecimal("0");
  private BigDecimal handlingCost = new BigDecimal("0");
  private BigDecimal invoiceTotal = new BigDecimal("0");

  public void setPurchaseOrderKey(Integer purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public Integer getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setVendorInvoiceKey(Integer vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public Integer getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

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

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setNoteLine2(String noteLine2) {
    this.noteLine2 = noteLine2;
  }

  public String getNoteLine2() {
    return noteLine2;
  }

  public void setNoteLine3(String noteLine3) {
    this.noteLine3 = noteLine3;
  }

  public String getNoteLine3() {
    return noteLine3;
  }

  public void setSubTotal(BigDecimal subTotal) {
    this.subTotal = subTotal;
  }

  public BigDecimal getSubTotal(){
    return subTotal;
  }

  public void setSalesTaxCost(BigDecimal salesTaxCost) {
    this.salesTaxCost = salesTaxCost;
  }

  public BigDecimal getSalesTaxCost(){
    return salesTaxCost;
  }

  public void setServiceTaxCost(BigDecimal serviceTaxCost) {
    this.serviceTaxCost = serviceTaxCost;
  }

  public BigDecimal getServiceTaxCost(){
    return serviceTaxCost;
  }

  public void setHandlingCost(BigDecimal handlingCost) {
    this.handlingCost = handlingCost;
  }

  public BigDecimal getHandlingCost(){
    return handlingCost;
  }

  public void setInvoiceTotal(BigDecimal invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public BigDecimal getInvoiceTotal(){
    return invoiceTotal;
  }

}

