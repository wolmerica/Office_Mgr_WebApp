/*
 * CustomerInvoiceReportDO.java
 *
 * Created on February 16, 2007, 10:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.customerinvoicereport;

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

public class CustomerInvoiceReportDO extends AbstractDO implements Serializable
{
  private Integer customerInvoiceKey = null;
  private Integer vendorInvoiceKey = new Integer("-1");
  private String vendorName = "";
  private Integer customerKey = null;
  private String clientName = "";
  private Byte scenarioKey = new Byte("-1");
  private String attributeToName = "";
  private Date dateOfService = null;
  private String customerInvoiceNumber = "";
  private String noteLine1 = "";
  private String noteLine2 = "";
  private String noteLine3 = "";
  private Short itemCount = new Short("0");
  private BigDecimal itemGrossAmount = new BigDecimal("0");
  private BigDecimal itemDiscountAmount = new BigDecimal("0");
  private BigDecimal itemNetAmount = new BigDecimal("0");
  private BigDecimal serviceGrossAmount = new BigDecimal("0");
  private BigDecimal serviceDiscountAmount = new BigDecimal("0");
  private BigDecimal serviceNetAmount = new BigDecimal("0");
  private BigDecimal subTotal = new BigDecimal("0");
  private BigDecimal grossProfitAmount = new BigDecimal("0");
  private BigDecimal netProfitAmount = new BigDecimal("0");
  private BigDecimal salesTaxCost = new BigDecimal("0");
  private BigDecimal serviceTaxCost = new BigDecimal("0");
  private BigDecimal handlingCost = new BigDecimal("0");
  private BigDecimal invoiceTotal = new BigDecimal("0");

  public void setCustomerInvoiceKey(Integer customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public Integer getCustomerInvoiceKey() {
    return customerInvoiceKey;
  }

  public void setVendorInvoiceKey(Integer vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public Integer getVendorInvoiceKey() {
    return vendorInvoiceKey;
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

  public void setScenarioKey(Byte scenarioKey) {
    this.scenarioKey = scenarioKey;
  }

  public Byte getScenarioKey() {
    return scenarioKey;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
  }

  public void setDateOfService(Date dateOfService) {
    this.dateOfService = dateOfService;
  }

  public Date getDateOfService() {
    return dateOfService;
  }

  public void setCustomerInvoiceNumber(String customerInvoiceNumber) {
    this.customerInvoiceNumber = customerInvoiceNumber;
  }

  public String getCustomerInvoiceNumber() {
    return customerInvoiceNumber;
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

  public void setItemCount(Short itemCount) {
    this.itemCount = itemCount;
  }

  public Short getItemCount(){
    return itemCount;
  }

  public void setItemGrossAmount(BigDecimal itemGrossAmount) {
    this.itemGrossAmount = itemGrossAmount;
  }

  public BigDecimal getItemGrossAmount(){
    return itemGrossAmount;
  }

  public void setItemDiscountAmount(BigDecimal itemDiscountAmount) {
    this.itemDiscountAmount = itemDiscountAmount;
  }

  public BigDecimal getItemDiscountAmount(){
    return itemDiscountAmount;
  }

  public void setItemNetAmount(BigDecimal itemNetAmount) {
    this.itemNetAmount = itemNetAmount;
  }

  public BigDecimal getItemNetAmount(){
    return itemNetAmount;
  }

  public void setServiceGrossAmount(BigDecimal serviceGrossAmount) {
    this.serviceGrossAmount = serviceGrossAmount;
  }

  public BigDecimal getServiceGrossAmount(){
    return serviceGrossAmount;
  }

  public void setServiceDiscountAmount(BigDecimal serviceDiscountAmount) {
    this.serviceDiscountAmount = serviceDiscountAmount;
  }

  public BigDecimal getServiceDiscountAmount(){
    return serviceDiscountAmount;
  }

  public void setServiceNetAmount(BigDecimal serviceNetAmount) {
    this.serviceNetAmount = serviceNetAmount;
  }

  public BigDecimal getServiceNetAmount(){
    return serviceNetAmount;
  }

  public void setSubTotal(BigDecimal subTotal) {
    this.subTotal = subTotal;
  }

  public BigDecimal getSubTotal(){
    return subTotal;
  }

  public void setGrossProfitAmount(BigDecimal grossProfitAmount) {
    this.grossProfitAmount = grossProfitAmount;
  }

  public BigDecimal getGrossProfitAmount(){
    return grossProfitAmount;
  }

  public void setNetProfitAmount(BigDecimal netProfitAmount) {
    this.netProfitAmount = netProfitAmount;
  }

  public BigDecimal getNetProfitAmount(){
    return netProfitAmount;
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

