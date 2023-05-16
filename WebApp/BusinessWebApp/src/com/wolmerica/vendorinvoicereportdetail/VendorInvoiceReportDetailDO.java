/*
 * VendorInvoiceReportDetailDO.java
 *
 * Created on February 17, 2007, 10:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoicereportdetail;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VendorInvoiceReportDetailDO extends AbstractDO implements Serializable
{
  private Integer vendorInvoiceDetailKey = null;
  private Integer vendorInvoiceKey = null;
  private Date invoiceDate = new Date();
  private String lineDetailNumber = "";
  private String lineDetailName = "";
  private String lineDetailCategory = "";
  private BigDecimal lineDetailSize = null;
  private String lineDetailUnit = "";
  private Short orderQty = new Short("0");
  private BigDecimal thePrice = new BigDecimal("0");
  private BigDecimal theDiscount = new BigDecimal("0");
  private BigDecimal extendPrice = new BigDecimal("0");
  private String noteLine1 = "";

  public void setVendorInvoiceDetailKey(Integer vendorInvoiceDetailKey) {
    this.vendorInvoiceDetailKey = vendorInvoiceDetailKey;
  }

  public Integer getVendorInvoiceDetailKey() {
    return vendorInvoiceDetailKey;
  }

  public void setVendorInvoiceKey(Integer vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public Integer getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setLineDetailNumber(String lineDetailNumber) {
    this.lineDetailNumber = lineDetailNumber;
  }

  public String getLineDetailNumber() {
    return lineDetailNumber;
  }

  public void setLineDetailName(String lineDetailName) {
    this.lineDetailName = lineDetailName;
  }

  public String getLineDetailName() {
    return lineDetailName;
  }

  public void setLineDetailCategory(String lineDetailCategory) {
    this.lineDetailCategory = lineDetailCategory;
  }

  public String getLineDetailCategory() {
    return lineDetailCategory;
  }

  public void setLineDetailSize(BigDecimal lineDetailSize) {
    this.lineDetailSize = lineDetailSize;
  }

  public BigDecimal getLineDetailSize() {
    return lineDetailSize;
  }

  public void setLineDetailUnit(String lineDetailUnit) {
    this.lineDetailUnit = lineDetailUnit;
  }

  public String getLineDetailUnit() {
    return lineDetailUnit;
  }

  public void setOrderQty(Short orderQty) {
    this.orderQty = orderQty;
  }

  public Short getOrderQty() {
    return orderQty;
  }

  public void setThePrice(BigDecimal thePrice) {
    this.thePrice = thePrice;
  }

  public BigDecimal getThePrice() {
    return thePrice;
  }

  public void setTheDiscount(BigDecimal theDiscount) {
    this.theDiscount = theDiscount;
  }

  public BigDecimal getTheDiscount() {
    return theDiscount;
  }

  public void setExtendPrice(BigDecimal extendPrice) {
    this.extendPrice = extendPrice;
  }

  public BigDecimal getExtendPrice() {
    return extendPrice;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }
}

