/*
 * CustomerListForm.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.customer;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String codeNum = "";
  private String firstName = "";
  private String lastName = "";  
  private String customerTypeName = "";
  private String clientName = "";
  private String address = "";
  private String address2 = "";  
  private String city = "";
  private String state = "";
  private String zip = "";
  private String phoneNum = "";
  private String phoneExt = "";  
  private String mobileNum = "";
  private String faxNum = "";
  private Boolean clinicId = false;
  private String acctNum = "";
  private String email = "";
  private String email2 = "";
  private String webSite = "";
  private String lineOfBusiness = "";
  private String contactName = "";
  private String referredBy = "";
  private String noteLine1 = "";    
  private String noteLine2 = "";  
  private Date lastServiceDate = null;  
  private BigDecimal accountBalanceAmount = new BigDecimal("0.00");
  private Date accountBalanceDate = null;
  private Boolean allowDeleteId = false;

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

  public void setCustomerTypeName(String customerTypeName) {
    this.customerTypeName = customerTypeName;
  }

  public String getCustomerTypeName() {
    return customerTypeName;
  }

  public void setClientName(String clientName) {

    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
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

  public void setClinicId(Boolean clinicId) {
    this.clinicId = clinicId;
  }

  public Boolean getClinicId() {
    return clinicId;
  }

  public void setAcctNum(String acctNum) {
    this.acctNum = acctNum;
  }

  public String getAcctNum() {
    return acctNum;
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
  
  public void setLastServiceDate(Date lastServiceDate) {
    this.lastServiceDate = lastServiceDate;
  }

  public Date getLastServiceDate() {
    return lastServiceDate;
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

  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }

}

