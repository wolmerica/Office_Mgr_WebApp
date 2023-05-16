/*
 * LicenseDO.java
 *
 * Created on April 9, 2010 1:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the vendorInvoiceItemName Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the vendorInvoiceItemName Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.license;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.sql.Timestamp;

public class LicenseDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Byte sourceTypeKey = new Byte("-1");
  private Integer sourceKey = new Integer("-1");
  private String licenseUser = "";
  private String licenseKey = "";
  private String noteLine1 = "";
  private Byte invoiceTypeKey = null;
  private Integer invoiceKey = null;
  private Boolean assignId = false;
  private Boolean releaseId = false;
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;
  private String clientName = "";
  private String attributeToEntity = "";
  private String attributeToName = "";
  private String licenseForName = "";

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
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

  public void setLicenseUser(String licenseUser) {
    this.licenseUser = licenseUser;
  }

  public String getLicenseUser() {
    return licenseUser;
  }

  public void setLicenseKey(String licenseKey) {
    this.licenseKey = licenseKey;
  }

  public String getLicenseKey() {
    return licenseKey;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setInvoiceTypeKey(Byte invoiceTypeKey) {
    this.invoiceTypeKey = invoiceTypeKey;
  }

  public Byte getInvoiceTypeKey() {
    return invoiceTypeKey;
  }

  public void setInvoiceKey(Integer invoiceKey) {
    this.invoiceKey = invoiceKey;
  }

  public Integer getInvoiceKey() {
    return invoiceKey;
  }

  public Boolean getAssignId() {
    return assignId;
  }

  public void setAssignId(Boolean assignId) {
    this.assignId = assignId;
  }

  public Boolean getReleaseId() {
    return releaseId;
  }

  public void setReleaseId(Boolean releaseId) {
    this.releaseId = releaseId;
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

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
  }

  public void setLicenseForName(String licenseForName) {
    this.licenseForName = licenseForName;
  }

  public String getLicenseForName() {
    return licenseForName;
  }
}