/*
 * VendorResultListForm.java
 *
 * Created on January 21, 2010, 9:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.vendorresult;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class VendorResultListHeadDO extends AbstractDO implements Serializable
{
  private Integer vendorKey = null;
  private String vendorName = "";
  private Integer customerKey = null;
  private String clientName = "";
  private Byte sourceTypeKey = null;
  private Integer sourceKey = null;
  private String attributeToName = "";
  private Date fromDate;
  private Date toDate;
  private Boolean testDisplayType = false;
  private String xmlFileName = "";
  private String xmlImportName = "";
  private Integer attachmentCount = null;
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");
  private ArrayList vendorResultListForm;
  private ArrayList permissionListForm;

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

  public void setTestDisplayType(Boolean testDisplayType) {
    this.testDisplayType = testDisplayType;
  }

  public Boolean getTestDisplayType() {
    return testDisplayType;
  }

  public void setXmlFileName(String xmlFileName) {
    this.xmlFileName = xmlFileName;
  }

  public String getXmlFileName() {
    return xmlFileName;
  }
  
  public void setXmlImportName(String xmlImportName) {
    this.xmlImportName = xmlImportName;
  }

  public String getXmlImportName() {
    return xmlImportName;
  }

  public void setAttachmentCount(Integer attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public Integer getAttachmentCount() {
    return attachmentCount;
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

  public void setVendorResultListForm(ArrayList vendorResultList){
  this.vendorResultListForm=vendorResultList;
  }

  public ArrayList getVendorResultListForm(){
  return vendorResultListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}