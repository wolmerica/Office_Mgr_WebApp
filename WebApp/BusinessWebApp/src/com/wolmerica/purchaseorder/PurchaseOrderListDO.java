/*
 * PurchaseOrderListForm.java
 *
 * Created on March 08, 2006, 9:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.purchaseorder;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PurchaseOrderListDO extends AbstractDO implements Serializable {

  private Integer key;
  private String purchaseOrderNumber;
  private Integer vendorKey;
  private String vendorName;
  private Byte orderFormKey;
  private Integer customerKey;
  private String clientName;
  private String attributeToEntity;
  private Byte sourceTypeKey;
  private Integer sourceKey;
  private String attributeToName;
  private Integer scheduleKey;    
  private Timestamp createStamp;
  private String orderStatus;
  private Short rebateCount = null;
  private Short rebateInstanceCount = null;
  private BigDecimal itemTotal = new BigDecimal("0");
  private Short itemQty = 0;  
  private BigDecimal serviceTotal = new BigDecimal("0");  
  private Short serviceQty = 0;  
  private BigDecimal orderTotal = new BigDecimal("0");  
  

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
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

  public void setOrderFormKey(Byte orderFormKey) {
    this.orderFormKey = orderFormKey;
  }

  public Byte getOrderFormKey() {
    return orderFormKey;
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
  
  public void setScheduleKey(Integer scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public Integer getScheduleKey() {
    return scheduleKey;
  }  
  
  public void setCreateStamp(Timestamp createStamp) {
    this.createStamp = createStamp;
  }

  public Timestamp getCreateStamp() {
    return createStamp;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setRebateCount(Short rebateCount) {
    this.rebateCount = rebateCount;
  }

  public Short getRebateCount() {
    return rebateCount;
  }

  public void setRebateInstanceCount(Short rebateInstanceCount) {
    this.rebateInstanceCount = rebateInstanceCount;
  }

  public Short getRebateInstanceCount() {
    return rebateInstanceCount;
  }
  
  public void setItemTotal(BigDecimal itemTotal) {
    this.itemTotal = itemTotal;
  }

  public BigDecimal getItemTotal() {
    return itemTotal;
  }

  public void setItemQty(Short itemQty) {
    this.itemQty = itemQty;
  }

  public Short getItemQty() {
    return itemQty;
  }
  
  public void setServiceTotal(BigDecimal serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public BigDecimal getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceQty(Short serviceQty) {
    this.serviceQty = serviceQty;
  }

  public Short getServiceQty() {
    return serviceQty;
  }
  
  public void setOrderTotal(BigDecimal orderTotal) {
    this.orderTotal = orderTotal;
  }

  public BigDecimal getOrderTotal() {
    return orderTotal;
  }  
}

