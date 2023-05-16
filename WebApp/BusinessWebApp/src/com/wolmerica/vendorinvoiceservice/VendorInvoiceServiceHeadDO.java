/*
 * VendorInvoiceServiceHeadDO.java
 *
 * Created on November 08, 2008, 12:32 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/29/2006 Implement tools.formatter library.
 */
package com.wolmerica.vendorinvoiceservice;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;

public class VendorInvoiceServiceHeadDO extends AbstractDO implements Serializable
{
  private String invoiceNumber = "";
  private Integer vendorInvoiceKey = null;
  private String vendorName = "";
  private Integer vendorKey = null;
  private BigDecimal invoiceTotal = new BigDecimal("0");
  private Boolean activeId = true;  
  private Boolean creditId = false;
  private String permissionStatus = "";
  private Integer lastRecord = new Integer("0");  
  private ArrayList vendorInvoiceServiceForm;
  
  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setVendorInvoiceKey(Integer vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public Integer getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setVendorName(String vendorName ) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorKey(Integer vendorKey) {
    this.vendorKey = vendorKey;
  }

  public Integer getVendorKey() {
    return vendorKey;
  }

  public void setInvoiceTotal(BigDecimal invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public BigDecimal getInvoiceTotal(){
    return invoiceTotal;
  }

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }

  public void setCreditId(Boolean creditId) {
    this.creditId = creditId;
  }

  public Boolean getCreditId() {
    return creditId;
  }  
  
  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }  

  public void setLastRecord(Integer lastRecord) {
    this.lastRecord = lastRecord;
  }

  public Integer getLastRecord() {
    return lastRecord;
  }
  
  public void setVendorInvoiceServiceForm(ArrayList vendorInvService){
  this.vendorInvoiceServiceForm=vendorInvService;
  }

  public ArrayList getVendorInvoiceServiceForm(){
  return vendorInvoiceServiceForm;
  }
}