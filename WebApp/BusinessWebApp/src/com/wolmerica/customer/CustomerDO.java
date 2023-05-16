/*
 * CustomerDO.java
 *
 * Created on August 21, 2005, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customer;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String codeNum = "";
  private String firstName = "";
  private String lastName = "";
  private String shipTo = "";
  private String address = "";
  private String address2 = "";
  private String city = "";
  private String state = "";
  private String zip = "";
  private String phoneNum = "";
  private String phoneExt = "";
  private String mobileNum = "";
  private String faxNum = "";
  private String acctNum = "";
  private String acctName = "";
  private ArrayList customerTypeForm;
  private Byte customerTypeKey = new Byte("1");
  private String attributeToEntity = "";
  private Boolean ledgerId = false;
  private String clientName = "";
  private Boolean reportId = false;
  private Integer primaryKey = null;
  private String primaryName = "";
  private Boolean clinicId = false;
  private BigDecimal ledgerBalanceAmount = new BigDecimal("0");
  private Date ledgerBalanceDate = new Date();
  private BigDecimal lastPaymentAmount = new BigDecimal("0");
  private Date lastPaymentDate = new Date();
  private BigDecimal accountBalanceAmount = new BigDecimal("0");
  private Date accountBalanceDate = new Date();
  private String email = "";
  private String email2 = "";  
  private String webSite = "";
  private String lineOfBusiness = "";
  private String contactName = "";
  private Boolean allowPaymentId = false;
  private Boolean allowRefundId = false;
  private String permissionStatus = "";
  private Integer attachmentCount = null;
  private String referredBy = "";
  private String noteLine1 = "";
  private String noteLine2 = "";  
  private Boolean activeId = true;
  private Byte reminderPrefKey = new Byte("0");
  private Timestamp loginStamp = null;
  private String loginIpAddress = "";
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

  public void setCodeNum(String codeNum) {
    this.codeNum = codeNum;
  }

  public String getCodeNum() {
    return codeNum;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setShipTo(String shipTo) {
    this.shipTo = shipTo;
  }

  public String getShipTo() {
    return shipTo;
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
  
  public void setMobileNum(String mobileNum) {
    this.mobileNum = mobileNum;
  }

  public String getMobileNum() {
    return mobileNum;
  }

  public void setFaxNum(String faxNum) {
    this.faxNum = faxNum;
  }

  public String getFaxNum() {
    return faxNum;
  }

  public void setAcctNum(String acctNum) {
    this.acctNum = acctNum;
  }

  public String getAcctNum() {
    return acctNum;
  }

  public void setAcctName(String acctName) {
    this.acctName = acctName;
  }

  public String getAcctName() {
    return acctName;
  }

  public void setCustomerTypeForm(ArrayList customerTypeList){
  this.customerTypeForm=customerTypeList;
  }

  public ArrayList getCustomerTypeForm(){
  return customerTypeForm;
  }

  public void setCustomerTypeKey(Byte customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public Byte getCustomerTypeKey() {
    return customerTypeKey;
  }

  public void setAttributeToEntity(String attributeToEntity) {
    this.attributeToEntity = attributeToEntity;
  }

  public String getAttributeToEntity() {
    return attributeToEntity;
  }

  public void setLedgerId(Boolean ledgerId) {
    this.ledgerId = ledgerId;
  }

  public Boolean getLedgerId() {
    return ledgerId;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setReportId(Boolean reportId) {
    this.reportId = reportId;
  }

  public Boolean getReportId() {
    return reportId;
  }

  public void setPrimaryKey(Integer primaryKey) {
    this.primaryKey = primaryKey;
  }

  public Integer getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryName(String primaryName) {
    this.primaryName = primaryName;
  }

  public String getPrimaryName() {
    return primaryName;
  }

  public void setClinicId(Boolean clinicId) {
    this.clinicId = clinicId;
  }

  public Boolean getClinicId() {
    return clinicId;
  }

  public void setLedgerBalanceAmount(BigDecimal ledgerBalanceAmount) {
    this.ledgerBalanceAmount = ledgerBalanceAmount;
  }

  public BigDecimal getLedgerBalanceAmount() {
    return ledgerBalanceAmount;
  }

  public void setLedgerBalanceDate(Date ledgerBalanceDate) {
    this.ledgerBalanceDate = ledgerBalanceDate;
  }

  public Date getLedgerBalanceDate() {
    return ledgerBalanceDate;
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

  public void setLineOfBusiness(String lineOfBusiness) {
    this.lineOfBusiness = lineOfBusiness;
  }

  public String getLineOfBusiness() {
    return lineOfBusiness;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactName() {
    return contactName;
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

  public void setReferredBy(String referredBy) {
    this.referredBy = referredBy;
  }

  public String getReferredBy() {
    return referredBy;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setNoteLine2(String noteLine2) {
    this.noteLine2 = noteLine2;
  }

  public String getNoteLine2() {
    return noteLine2;
  }
  
  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }

  public void setReminderPrefKey(Byte reminderPrefKey) {
    this.reminderPrefKey = reminderPrefKey;
  }

  public Byte getReminderPrefKey() {
    return reminderPrefKey;
  }

  public void setLoginStamp(Timestamp loginStamp) {
    this.loginStamp = loginStamp;
  }

  public Timestamp getLoginStamp() {
    return loginStamp;
  }

  public void setLoginIpAddress(String loginIpAddress) {
    this.loginIpAddress = loginIpAddress;
  }

  public String getLoginIpAddress() {
    return loginIpAddress;
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

