/*
 * PurchaseOrderItemHeadDO.java
 *
 * Created on January 31, 2005, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.purchaseorderitem;

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

public class PurchaseOrderItemHeadDO extends AbstractDO implements Serializable
{
  private String purchaseOrderNumber = "";
  private Integer purchaseOrderKey = null;
  private String vendorName = "";
  private Integer vendorKey = null;
  private BigDecimal itemTotal = new BigDecimal("0");
  private String orderStatus = "";  
  private String permissionStatus = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");  
  private ArrayList purchaseOrderItemForm;

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setPurchaseOrderKey(Integer purchaseOrderKey ) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public Integer getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setVendorName(String vendorName ) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorKey(Integer vendorKey ) {
    this.vendorKey = vendorKey;
  }

  public Integer getVendorKey() {
    return vendorKey;
  }

  public void setItemTotal(BigDecimal itemTotal) {
    this.itemTotal = itemTotal;
  }

  public BigDecimal getItemTotal(){
    return itemTotal;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getOrderStatus() {
    return orderStatus;
  }
  
  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }  
    
  public void setRecordCount(Integer recordCount) {
    this.recordCount = recordCount;
  }

  public Integer getRecordCount() {
    return recordCount;
  }

  public void setFirstRecord(Integer firstRecord) {
    this.firstRecord = firstRecord;
  }

  public Integer getFirstRecord() {
    return firstRecord;
  }

  public void setLastRecord(Integer lastRecord) {
    this.lastRecord = lastRecord;
  }

  public Integer getLastRecord() {
    return lastRecord;
  }

  public void setPreviousPage(Integer previousPage ) {
    this.previousPage = previousPage;
  }

  public Integer getPreviousPage() {
    return previousPage;
  }

  public void setNextPage(Integer nextPage ) {
    this.nextPage = nextPage;
  }

  public Integer getNextPage() {
    return nextPage;
  }
    
  public void setPurchaseOrderItemForm(ArrayList purchaseOrderItemForm){
    this.purchaseOrderItemForm=purchaseOrderItemForm;
  }

  public ArrayList getPurchaseOrderItemForm(){
    return purchaseOrderItemForm;
  }

}