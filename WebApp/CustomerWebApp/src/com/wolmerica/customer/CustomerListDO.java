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
  private String acctNum = "";
  private String customerTypeName = "";
  private String acctName = "";
  private String city = "";
  private String state = "";
  private String zip = "";
  private String phoneNum = "";
  private String mobileNum = "";
  private String faxNum = "";
  private Boolean clinicId = false;
  private BigDecimal accountBalanceAmount = null;
  private Date accountBalanceDate = null;
  private Boolean allowDeleteId = false;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setAcctNum(String acctNum) {
    this.acctNum = acctNum;
  }

  public String getAcctNum() {
    return acctNum;
  }

  public void setCustomerTypeName(String customerTypeName) {
    this.customerTypeName = customerTypeName;
  }

  public String getCustomerTypeName() {
    return customerTypeName;
  }

  public void setAcctName(String acctName) {

    this.acctName = acctName;
  }

  public String getAcctName() {
    return acctName;
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

