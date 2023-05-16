/*
 * VendorDO.java
 *
 * Created on August 23, 2005, 10:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendor;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class VendorDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Boolean clinicId = false;
  private Boolean trackResultId = false;
  private Boolean webServiceId = false;
  private Boolean activeId = true;
  private String name = "";
  private String contactName = "";
  private String address = "";
  private String address2 = "";
  private String city = "";
  private String state = "";
  private String zip = "";
  private String phoneNum = "";
  private String phoneExt = "";
  private String faxNum= "";
  private String email= "";
  private String email2= "";
  private String webSite = "";
  private String acctNum = "";
  private String terms = "";
  private BigDecimal markUp = new BigDecimal("0");
  private Byte orderFormKey = new Byte("0");
  private BigDecimal lastPaymentAmount = new BigDecimal("0");
  private Date lastPaymentDate = new Date();
  private BigDecimal accountBalanceAmount = new BigDecimal("0");
  private Date accountBalanceDate = new Date();
  private Boolean allowPaymentId = false;
  private Boolean allowRefundId = false;
  private String permissionStatus = "";
  private Integer attachmentCount = null;
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

  public void setClinicId(Boolean clinicId) {
    this.clinicId = clinicId;
  }

  public Boolean getClinicId() {
    return clinicId;
  }

  public void setTrackResultId(Boolean trackResultId) {
    this.trackResultId = trackResultId;
  }

  public Boolean getTrackResultId() {
    return trackResultId;
  }

  public void setWebServiceId(Boolean webServiceId) {
    this.webServiceId = webServiceId;
  }

  public Boolean getWebServiceId() {
    return webServiceId;
  }

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactName() {
    return contactName;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }
  
  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public String getAddress2() {
    return address2;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCity() {
    return city;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getZip() {
    return zip;
  }

  public void setPhoneNum(String phoneNum) {
    this.phoneNum = phoneNum;
  }

  public String getPhoneNum() {
    return phoneNum;
  }
  
  public void setPhoneExt(String phoneExt) {
    this.phoneExt = phoneExt;
  }

  public String getPhoneExt() {
    return phoneExt;
  }
  
  public void setFaxNum(String faxNum) {
    this.faxNum = faxNum;
  }

  public String getFaxNum() {
    return faxNum;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail2(String email2) {
    this.email2 = email2;
  }

  public String getEmail2() {
    return email2;
  }
  
  public void setWebSite(String webSite) {
    this.webSite = webSite;
  }

  public String getWebSite() {
    return webSite;
  }

  public void setAcctNum(String acctNum) {
    this.acctNum = acctNum;
  }

  public String getAcctNum() {
    return acctNum;
  }

  public void setTerms(String terms) {
    this.terms = terms;
  }

  public String getTerms() {
    return terms;
  }

  public void setMarkUp(BigDecimal markUp) {
    this.markUp = markUp;
  }

  public BigDecimal getMarkUp() {
    return markUp;
  }

  public void setOrderFormKey(Byte orderFormKey) {
    this.orderFormKey = orderFormKey;
  }

  public Byte getOrderFormKey() {
    return orderFormKey;
  }

  public void setLastPaymentAmount(BigDecimal lastPaymentAmount) {
    this.lastPaymentAmount = lastPaymentAmount;
  }

  public BigDecimal getLastPaymentAmount() {
    return lastPaymentAmount;
  }

  public void setLastPaymentDate(Date lastPaymentDate) {
    this.lastPaymentDate = lastPaymentDate;
  }

  public Date getLastPaymentDate() {
    return lastPaymentDate;
  }

  public void setAccountBalanceAmount(BigDecimal accountBalanceAmount) {
    this.accountBalanceAmount = accountBalanceAmount;
  }

  public BigDecimal getAccountBalanceAmount() {
    return accountBalanceAmount;
  }

  public void setAccountBalanceDate(Date accountBalanceDate) {
    this.accountBalanceDate = accountBalanceDate;
  }

  public Date getAccountBalanceDate() {
    return accountBalanceDate;
  }

  public void setAllowPaymentId(Boolean allowPaymentId) {
    this.allowPaymentId = allowPaymentId;
  }

  public Boolean getAllowPaymentId() {
    return allowPaymentId;
  }

  public void setAllowRefundId(Boolean allowRefundId) {
    this.allowRefundId = allowRefundId;
  }

  public Boolean getAllowRefundId() {
    return allowRefundId;
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

