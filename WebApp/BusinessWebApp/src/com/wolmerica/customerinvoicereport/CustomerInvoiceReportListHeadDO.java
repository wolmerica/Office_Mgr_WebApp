/*
 * CustomerInvoiceReportListHeadDO.java
 *
 * Created on February 16, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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

public class CustomerInvoiceReportListHeadDO extends AbstractDO implements Serializable
{
  private Integer customerKey = null;
  private String clientName = "";
  private String attributeToEntity = "";
  private Byte sourceTypeKey = null;
  private Integer sourceKey = null;
  private String attributeToName = "";
  private Date fromDate = new Date();
  private Date toDate = new Date();
  private Short recordCount = new Short("0");
  private Short firstRecord = new Short("0");
  private Short lastRecord = new Short("0");
  private Short previousPage = new Short("0");
  private Short nextPage = new Short("0");
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
  private BigDecimal packagingCost = new BigDecimal("0");
  private BigDecimal freightCost = new BigDecimal("0");
  private BigDecimal miscellaneousCost = new BigDecimal("0");
  private BigDecimal handlingCost = new BigDecimal("0");
  private BigDecimal invoiceTotal = new BigDecimal("0");
  private BigDecimal costBasisTotal = new BigDecimal("0");
  private ArrayList customerInvoiceReportForm;

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

  public void setAttributeToEntity(String attributeToEntity) {
    this.attributeToEntity = attributeToEntity;
  }

  public String getAttributeToEntity() {
    return attributeToEntity;
  }

  public void setSourceTypeKey(Byte sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public Byte getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceKey(Integer sourceKey) {
    this.sourceKey = sourceKey;
  }

  public Integer getSourceKey() {
    return sourceKey;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
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

  public void setPackagingCost(BigDecimal packagingCost) {
    this.packagingCost = packagingCost;
  }

  public BigDecimal getPackagingCost(){
    return packagingCost;
  }

  public void setFreightCost(BigDecimal freightCost) {
    this.freightCost = freightCost;
  }

  public BigDecimal getFreightCost(){
    return freightCost;
  }

  public void setMiscellaneousCost(BigDecimal miscellaneousCost) {
    this.miscellaneousCost = miscellaneousCost;
  }

  public BigDecimal getMiscellaneousCost(){
    return miscellaneousCost;
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

  public void setCostBasisTotal(BigDecimal costBasisTotal) {
    this.costBasisTotal = costBasisTotal;
  }

  public BigDecimal getCostBasisTotal(){
    return costBasisTotal;
  }

  public void setCustomerInvoiceReportForm(ArrayList customerInvoiceReportForm){
    this.customerInvoiceReportForm = customerInvoiceReportForm;
  }

  public ArrayList getCustomerInvoiceReportForm(){
    return customerInvoiceReportForm;
  }
}

