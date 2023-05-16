/*
 * PurchaseOrderListForm.java
 *
 * Created on March 08, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.purchaseorder;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;

public class PurchaseOrderListHeadDO extends AbstractDO implements Serializable
{
  private String vendorNameFilter = "";
  private String orderNumberFilter = "";
  private Integer itemKeyFilter = null;
  private Integer scheduleKeyFilter = null;  
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");
  private ArrayList purchaseOrderListForm;
  private ArrayList permissionListForm;

  public void setVendorNameFilter(String vendorNameFilter) {
    this.vendorNameFilter = vendorNameFilter;
  }

  public String getVendorNameFilter() {
    return vendorNameFilter;
  }

  public void setOrderNumberFilter(String orderNumberFilter) {
    this.orderNumberFilter = orderNumberFilter;
  }

  public String getOrderNumberFilter() {
    return orderNumberFilter;
  }
  
  public void setItemKeyFilter(Integer itemKeyFilter) {
    this.itemKeyFilter = itemKeyFilter;
  }

  public Integer getItemKeyFilter() {
    return itemKeyFilter;
  }  

  public void setScheduleKeyFilter(Integer scheduleKeyFilter) {
    this.scheduleKeyFilter = scheduleKeyFilter;
  }

  public Integer getScheduleKeyFilter() {
    return scheduleKeyFilter;
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

  public void setPurchaseOrderListForm(ArrayList purchaseOrderList){
  this.purchaseOrderListForm=purchaseOrderList;
  }

  public ArrayList getPurchaseOrderListForm(){
  return purchaseOrderListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}