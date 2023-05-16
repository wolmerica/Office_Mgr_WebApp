/*
 * CustomerInvoiceDetailReportDO.java
 *
 * Created on February 17, 2007, 10:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.customerinvoicereportdetail;

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

public class CustomerInvoiceReportDetailDO extends AbstractDO implements Serializable
{
  private Integer customerInvoiceDetailKey = null;
  private Integer customerInvoiceKey = null;
  private Date dateOfService = null;  
  private String lineDetailNumber = "";
  private String lineDetailName = "";
  private String lineDetailCategory = "";
  private BigDecimal lineDetailSize = null;
  private String lineDetailUnit = "";
  private Short orderQty = new Short("0");
  private BigDecimal costBasis = new BigDecimal("0");    
  private BigDecimal thePrice = new BigDecimal("0");
  private BigDecimal discountRate = new BigDecimal("0");  
  private BigDecimal discountAmount = new BigDecimal("0");    
  private BigDecimal extendPrice = new BigDecimal("0");
  private String noteLine1 = "";

  public void setCustomerInvoiceDetailKey(Integer customerInvoiceDetailKey) {
    this.customerInvoiceDetailKey = customerInvoiceDetailKey;
  }

  public Integer getCustomerInvoiceDetailKey() {
    return customerInvoiceDetailKey;
  }  
  
  public void setCustomerInvoiceKey(Integer customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public Integer getCustomerInvoiceKey() {
    return customerInvoiceKey;
  }

  public void setDateOfService(Date dateOfService) {
    this.dateOfService = dateOfService;
  }

  public Date getDateOfService() {
    return dateOfService;
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
  
  public void setCostBasis(BigDecimal costBasis) {
    this.costBasis = costBasis;
  }

  public BigDecimal getCostBasis() {
    return costBasis;
  }  
  
  public void setThePrice(BigDecimal thePrice) {
    this.thePrice = thePrice;
  }

  public BigDecimal getThePrice() {
    return thePrice;
  }
  
  public void setDiscountRate(BigDecimal discountRate) {
    this.discountRate = discountRate;
  }

  public BigDecimal getDiscountRate() {
    return discountRate;
  }  
  
  public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
  }

  public BigDecimal getDiscountAmount() {
    return discountAmount;
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

