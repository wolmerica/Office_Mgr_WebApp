/*
 * ServiceSaleDO.java
 *
 * Created on August 21, 2006, 2:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.servicesalereport;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class ServiceSaleDO extends AbstractDO implements Serializable {

  private Integer invoiceKey = null;
  private Date invoiceDate = null;
  private Short invoiceYear = null;
  private String invoiceMonthName = null;
  private String invoiceNumber = "";
  private String serviceName = "";
  private String priceTypeName = "";
  private Short orderQty = new Short("0");
  private BigDecimal thePrice = new BigDecimal("0");
  private BigDecimal discountAmount = new BigDecimal("0");
  private BigDecimal salesTax = new BigDecimal("0");
  private BigDecimal handlingCost = new BigDecimal("0");
  private BigDecimal serviceTotal = new BigDecimal("0");

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

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setPriceTypeName(String priceTypeName) {
    this.priceTypeName = priceTypeName;
  }

  public String getPriceTypeName() {
    return priceTypeName;
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

  public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
  }

  public BigDecimal getDiscountAmount() {
    return discountAmount;
  }

  public void setSalesTax(BigDecimal salesTax) {
    this.salesTax = salesTax;
  }

  public BigDecimal getSalesTax() {
    return salesTax;
  }

  public void setHandlingCost(BigDecimal handlingCost) {
    this.handlingCost = handlingCost;
  }

  public BigDecimal getHandlingCost() {
    return handlingCost;
  }

  public void setServiceTotal(BigDecimal serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public BigDecimal getServiceTotal() {
    return serviceTotal;
  }
}

