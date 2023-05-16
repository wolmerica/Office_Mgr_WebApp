/*
 * ItemPurchaseDO.java
 *
 * Created on May 17, 2006, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.itempurchasereport;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ItemPurchaseDO extends AbstractDO implements Serializable {

  private Integer invoiceKey = null;
  private Date invoiceDate = new Date();
  private Short invoiceYear = null;
  private String invoiceMonthName = null;
  private String invoiceNumber = "";
  private String brandName = "";
  private String genericName = "";
  private BigDecimal size = null;
  private String sizeUnit = "";
  private Short orderQty = new Short("0");
  private BigDecimal theCost = new BigDecimal("0");
  private BigDecimal theDiscount = new BigDecimal("0");
  private BigDecimal useTaxCost = new BigDecimal("0");
  private BigDecimal handlingCost = new BigDecimal("0");
  private BigDecimal extendCost = new BigDecimal("0");

  public void setInvoiceKey(Integer invoiceKey) {
    this.invoiceKey = invoiceKey;
  }

  public Integer getInvoiceKey() {
    return invoiceKey;
  }

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceYear(Short invoiceYear) {
    this.invoiceYear = invoiceYear;
  }

  public Short getInvoiceYear() {
    return invoiceYear;
  }

  public void setInvoiceMonthName(String invoiceMonthName) {
    this.invoiceMonthName = invoiceMonthName;
  }

  public String getInvoiceMonthName() {
    return invoiceMonthName;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setGenericName(String genericName) {
    this.genericName = genericName;
  }

  public String getGenericName() {
    return genericName;
  }

  public void setSize(BigDecimal size) {
    this.size = size;
  }

  public BigDecimal getSize() {
    return size;
  }

  public void setSizeUnit(String sizeUnit) {
    this.sizeUnit = sizeUnit;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

 public void setOrderQty(Short orderQty) {
    this.orderQty = orderQty;
  }

  public Short getOrderQty() {
    return orderQty;
  }

  public void setTheCost(BigDecimal theCost) {
    this.theCost = theCost;
  }

  public BigDecimal getTheCost() {
    return theCost;
  }

  public void setTheDiscount(BigDecimal theDiscount) {
    this.theDiscount = theDiscount;
  }

  public BigDecimal getTheDiscount() {
    return theDiscount;
  }

  public void setUseTaxCost(BigDecimal useTaxCost) {
    this.useTaxCost = useTaxCost;
  }

  public BigDecimal getUseTaxCost() {
    return useTaxCost;
  }

  public void setHandlingCost(BigDecimal handlingCost) {
    this.handlingCost = handlingCost;
  }

  public BigDecimal getHandlingCost() {
    return handlingCost;
  }

  public void setExtendCost(BigDecimal extendCost) {
    this.extendCost = extendCost;
  }

  public BigDecimal getExtendCost() {
    return extendCost;
  }
}

