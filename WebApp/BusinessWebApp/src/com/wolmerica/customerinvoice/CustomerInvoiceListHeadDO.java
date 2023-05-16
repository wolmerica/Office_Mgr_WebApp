/*
 * CustomerInvoiceListHeadDO.java
 *
 * Created on February 12, 2006, 4:12 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoice;

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

public class CustomerInvoiceListHeadDO extends AbstractDO implements Serializable
{
  private Integer purchaseOrderKey = null;
  private Integer vendorInvoiceKey = null;
  private Integer genesisKey = null;
  private Integer vendorKey = null;
  private String vendorName = "";
  private Integer customerKey = null;
  private String clientName = "";
  private Byte scenarioKey = new Byte("0");
  private Boolean moreToDistribute = false;
  private Boolean internalUse = false;
  private String clientNameFilter = "";
  private String invoiceNumberFilter = "";
  private Integer itemKeyFilter = null;
  private Integer serviceKeyFilter = null;
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");
  private ArrayList customerInvoiceListForm;
  private ArrayList permissionListForm;

  public void setPurchaseOrderKey(Integer purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public Integer getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setVendorInvoiceKey(Integer vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public Integer getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setGenesisKey(Integer genesisKey) {
    this.genesisKey = genesisKey;
  }

  public Integer getGenesisKey() {
    return genesisKey;
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

  public void setMoreToDistribute(Boolean moreToDistribute) {
    this.moreToDistribute = moreToDistribute;
  }

  public Boolean getMoreToDistribute() {
    return moreToDistribute;
  }

  public void setInternalUse(Boolean internalUse) {
    this.internalUse = internalUse;
  }

  public Boolean getInternalUse() {
    return internalUse;
  }
  
  public void setClientNameFilter(String clientNameFilter) {
    this.clientNameFilter = clientNameFilter;
  }

  public String getClientNameFilter() {
    return clientNameFilter;
  }

  public void setInvoiceNumberFilter(String invoiceNumberFilter) {
    this.invoiceNumberFilter = invoiceNumberFilter;
  }

  public String getInvoiceNumberFilter() {
    return invoiceNumberFilter;
  }

  public void setItemKeyFilter(Integer itemKeyFilter) {
    this.itemKeyFilter = itemKeyFilter;
  }

  public Integer getItemKeyFilter() {
    return itemKeyFilter;
  }  
  
  public void setServiceKeyFilter(Integer serviceKeyFilter) {
    this.serviceKeyFilter = serviceKeyFilter;
  }

  public Integer getServiceKeyFilter() {
    return serviceKeyFilter;
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

  public void setCustomerInvoiceListForm(ArrayList customerInvoiceListForm){
    this.customerInvoiceListForm=customerInvoiceListForm;
  }

  public ArrayList getCustomerInvoiceListForm(){
    return customerInvoiceListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}

