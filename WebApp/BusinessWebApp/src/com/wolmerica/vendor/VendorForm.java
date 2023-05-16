/*
 * VendorForm.java
 *
 * Created on August 23, 2005, 10:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 *
 * 02/19/2007 - Introduce the PhoneNumberFormatter with setFormatterType()
 */

package com.wolmerica.vendor;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;
import com.wolmerica.tools.formatter.EmailFormatter;
import com.wolmerica.tools.formatter.PhoneNumberFormatter;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.math.BigDecimal;

public class VendorForm extends MasterForm {

  private String key;
  private String clinicId;
  private String trackResultId;
  private String webServiceId;
  private String activeId;
  private String name;
  private String contactName;
  private String address;
  private String address2;
  private String city;
  private String state;
  private String zip;
  private String phoneNum;
  private String phoneExt;
  private String faxNum;
  private String email;
  private String email2;
  private String webSite;
  private String acctNum;
  private String terms;
  private String markUp;
  private String orderFormKey;
  private String lastPaymentAmount;
  private String lastPaymentDate;
  private String accountBalanceAmount;
  private String accountBalanceDate;
  private String allowPaymentId;
  private String allowRefundId;
  private String permissionStatus;
  private String attachmentCount;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public VendorForm() {
    addRequiredFields(new String[] { "name", "contactName", "address", 
                                     "city", "state", "zip", "orderFormKey",
                                     "phoneNum", "faxNum", "acctNum" });
    addRange("markUp",
              new BigDecimal("0.00"),
              new BigDecimal("999.99"));

    setFormatterType("phoneNum", PhoneNumberFormatter.class);
    setFormatterType("faxNum", PhoneNumberFormatter.class);
    setFormatterType("email", EmailFormatter.class);    
    setFormatterType("email2", EmailFormatter.class);    
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setClinicId(String clinicId) {
    this.clinicId = clinicId;
  }

  public String getClinicId() {
    return clinicId;
  }

  public void setTrackResultId(String trackResultId) {
    this.trackResultId = trackResultId;
  }

  public String getTrackResultId() {
    return trackResultId;
  }

  public void setWebServiceId(String webServiceId) {
    this.webServiceId = webServiceId;
  }

  public String getWebServiceId() {
    return webServiceId;
  }

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
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

  public void setMarkUp(String markUp) {
    this.markUp = markUp;
  }

  public String getMarkUp() {
    return markUp;
  }

  public void setOrderFormKey(String orderFormKey) {
    this.orderFormKey = orderFormKey;
  }

  public String getOrderFormKey() {
    return orderFormKey;
  }

  public void setLastPaymentAmount(String lastPaymentAmount) {
    this.lastPaymentAmount = lastPaymentAmount;
  }

  public String getLastPaymentAmount() {
    return lastPaymentAmount;
  }

  public void setLastPaymentDate(String lastPaymentDate) {
    this.lastPaymentDate = lastPaymentDate;
  }

  public String getLastPaymentDate() {
    return lastPaymentDate;
  }

  public void setAccountBalanceAmount(String accountBalanceAmount) {
    this.accountBalanceAmount = accountBalanceAmount;
  }

  public String getAccountBalanceAmount() {
    return accountBalanceAmount;
  }

  public void setAccountBalanceDate(String accountBalanceDate) {
    this.accountBalanceDate = accountBalanceDate;
  }

  public String getAccountBalanceDate() {
    return accountBalanceDate;
  }

  public void setAllowPaymentId(String allowPaymentId) {
    this.allowPaymentId = allowPaymentId;
  }

  public String getAllowPaymentId() {
    return allowPaymentId;
  }

  public void setAllowRefundId(String allowRefundId) {
    this.allowRefundId = allowRefundId;
  }

  public String getAllowRefundId() {
    return allowRefundId;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }

  public void setAttachmentCount(String attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public String getAttachmentCount() {
    return attachmentCount;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateStamp(String createStamp) {
    this.createStamp = createStamp;
  }

  public String getCreateStamp() {
    return createStamp;
  }

  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getUpdateUser() {
    return updateUser;
  }

  public void setUpdateStamp(String updateStamp) {
    this.updateStamp = updateStamp;
  }

  public String getUpdateStamp() {
    return updateStamp;
  }

  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();

    EmployeesActionMapping employeesMapping = (EmployeesActionMapping)mapping;

    // Does this action require the user to login
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {

        // return null to force action to handle login
        // error
        return null;
      }
    }

    errors = super.validate(mapping, request);

    // Post a global message instructing user to clean up
    // validation errors and resubmit
    if (errors.size() > 0) {
      ActionMessage message =
          new ActionMessage("message.validation");
      ActionMessages messages = new ActionMessages();
          messages.add(ActionMessages.GLOBAL_MESSAGE, message);
      request.setAttribute(Globals.MESSAGE_KEY, messages);
    }

    // Set the disableEdit attribute to false when errors encountered.
    request.setAttribute("disableEdit", false);

    return errors;
  }
}

