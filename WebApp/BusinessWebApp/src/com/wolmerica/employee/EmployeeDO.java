/*
 * EmployeeDO.java
 *
 * Created on August 15, 2005, 8:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.employee;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.sql.Timestamp;

public class EmployeeDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String userName = "";
  private String password = "";
  private Boolean adminId = false;
  private String firstName = "";
  private String lastName = "";
  private String address = "";
  private String address2 = "";
  private String city = "";
  private String state = "";
  private String zip = "";
  private String phone = "";
  private String phoneNum = "";
  private String phoneExt = "";
  private String mobileNum = "";  
  private String email = "";
  private String email2 = "";
  private String yimId = "";
  private String documentServerURL = "";
  private String photoFileName = "";
  private String permissionSlip = "";
  private Integer userStateKey = null;
  private String permissionStatus = "";
  private Integer attachmentCount = null;
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

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {

    return password;
  }

  public void setAdminId(Boolean adminId) {
    this.adminId = adminId;
  }

  public Boolean getAdminId() {
    return adminId;
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

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPhone() {
    return phone;
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
  
  public void setYimId(String yimId) {
    this.yimId = yimId;
  }

  public String getYimId() {
    return yimId;
  }

  public void setDocumentServerURL(String documentServerURL) {
    this.documentServerURL = documentServerURL;
  }

  public String getDocumentServerURL() {
    return documentServerURL;
  }

  public void setPhotoFileName(String photoFileName) {
    this.photoFileName = photoFileName;
  }

  public String getPhotoFileName() {
    return photoFileName;
  }

  public void setPermissionSlip(String permissionSlip) {
    this.permissionSlip = permissionSlip;
  }

  public String getPermissionSlip() {
    return permissionSlip;
  }

  public void setUserStateKey(Integer userStateKey) {
    this.userStateKey = userStateKey;
  }

  public Integer getUserStateKey() {
    return userStateKey;
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
