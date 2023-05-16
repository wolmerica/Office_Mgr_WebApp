/*
 * PurchaseOrderDO.java
 *
 * Created on September 5, 2005, 5:32 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/23/2005 Implement tools.formatter library.
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

public class PurchaseOrderDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String purchaseOrderNumber = "";
  private String orderStatus = "";
  private Byte priorityKey = new Byte("0");
  private Integer vendorKey = null;
  private String vendorName = "";
  private Integer vendorItemCount = null;
  private Integer vendorServiceCount = null;
  private Integer customerKey = null;
  private String clientName = "";
  private Timestamp submitOrderStamp = null;
  private String salesOrderNumber = "";
  private String noteLine1 = "";
  private BigDecimal itemTotal = new BigDecimal("0");
  private Short itemQty = 0;  
  private BigDecimal serviceTotal = new BigDecimal("0");  
  private Short serviceQty = 0;  
  private BigDecimal orderTotal = new BigDecimal("0");  
  private String balanceQty = "";
  private String balanceAmount = "";
  private String permissionStatus = "";
  private Integer attachmentCount = null;
  private String attributeToEntity = "";
  private Byte sourceTypeKey = new Byte("-1");
  private Integer sourceKey = new Integer("-1");
  private String attributeToName = "";  
  private Integer scheduleKey = null;  
  private Integer logisticsCount = null;    
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;

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

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setPriorityKey(Byte priorityKey) {
    this.priorityKey = priorityKey;
  }

  public Byte getPriorityKey() {
    return priorityKey;
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

  public void setVendorItemCount(Integer vendorItemCount) {
    this.vendorItemCount = vendorItemCount;
  }

  public Integer getVendorItemCount() {
    return vendorItemCount;
  }

  public void setVendorServiceCount(Integer vendorServiceCount) {
    this.vendorServiceCount = vendorServiceCount;
  }

  public Integer getVendorServiceCount() {
    return vendorServiceCount;
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

  public void setSubmitOrderStamp(Timestamp submitOrderStamp) {
    this.submitOrderStamp = submitOrderStamp;
  }

  public Timestamp getSubmitOrderStamp() {
    return submitOrderStamp;
  }
  
  public void setSalesOrderNumber(String salesOrderNumber) {
    this.salesOrderNumber = salesOrderNumber;
  }

  public String getSalesOrderNumber() {
    return salesOrderNumber;
  }
    
  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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
  
  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }    
  
  public void setAttachmentCount(Integer attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public Integer getAttachmentCount() {
    return attachmentCount;
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
  
  public void setLogisticsCount(Integer logisticsCount) {
    this.logisticsCount = logisticsCount;
  }

  public Integer getLogisticsCount() {
    return logisticsCount;
  }  
  
  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateStamp(Timestamp createStamp) {
    this.createStamp = createStamp;
  }

  public Timestamp getCreateStamp() {
    return createStamp;
  }
  
  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getUpdateUser() {
    return updateUser;
  }

  public void setUpdateStamp(Timestamp updateStamp) {
    this.updateStamp = updateStamp;
  }

  public Timestamp getUpdateStamp() {
    return updateStamp;
  }
}

