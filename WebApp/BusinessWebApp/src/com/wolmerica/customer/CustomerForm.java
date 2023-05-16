/*
 * CustomerForm.java
 *
 * Created on August 21, 2005, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 *
 * 02/19/2007 - Introduce the PhoneNumberFormatter with setFormatterType()
 */

package com.wolmerica.customer;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
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
import java.util.ArrayList;

public class CustomerForm extends MasterForm {

  private String key;
  private String codeNum;
  private String firstName;
  private String lastName;
  private String shipTo;
  private String address;
  private String address2;
  private String city;
  private String state;
  private String zip;
  private String phoneNum;
  private String phoneExt;
  private String mobileNum;
  private String faxNum;
  private String acctNum;
  private String acctName;
  private ArrayList customerTypeForm;
  private String customerTypeKey;
  private String attributeToEntity;
  private String ledgerId;
  private String clientName;
  private String reportId;
  private String primaryKey;
  private String primaryName;
  private String clinicId;
  private String ledgerBalanceAmount;
  private String ledgerBalanceDate;
  private String lastPaymentAmount;
  private String lastPaymentDate;
  private String accountBalanceAmount;
  private String accountBalanceDate;
  private String email;
  private String email2;  
  private String webSite;
  private String lineOfBusiness;
  private String contactName;
  private String allowPaymentId;
  private String allowRefundId;
  private String permissionStatus;
  private String attachmentCount;
  private String referredBy;
  private String noteLine1;
  private String noteLine2;  
  private String activeId;
  private String reminderPrefKey;
  private String loginStamp;
  private String loginIpAddress;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public CustomerForm() {
    addRequiredFields(new String[] { "codeNum", "firstName", "lastName",
                                     "shipTo", "clientName",
                                     "acctName", "contactName",
                                     "lineOfBusiness", "address",
                                     "city", "state", "zip",
                                     "phoneNum", "acctNum" });
    setFormatterType("phoneNum", PhoneNumberFormatter.class);
    setFormatterType("mobileNum", PhoneNumberFormatter.class);
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

  public void setCustomerTypeKey(String customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public String getCustomerTypeKey() {
    return customerTypeKey;
  }

  public void setAttributeToEntity(String attributeToEntity) {
    this.attributeToEntity = attributeToEntity;
  }

  public String getAttributeToEntity() {
    return attributeToEntity;
  }

  public void setLedgerId(String ledgerId) {
    this.ledgerId = ledgerId;
  }

  public String getLedgerId() {
    return ledgerId;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setReportId(String reportId) {
    this.reportId = reportId;
  }

  public String getReportId() {
    return reportId;
  }

  public void setPrimaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryName(String primaryName) {
    this.primaryName = primaryName;
  }

  public String getPrimaryName() {
    return primaryName;
  }

  public void setClinicId(String clinicId) {
    this.clinicId = clinicId;
  }

  public String getClinicId() {
    return clinicId;
  }

  public void setLedgerBalanceAmount(String ledgerBalanceAmount) {
    this.ledgerBalanceAmount = ledgerBalanceAmount;
  }

  public String getLedgerBalanceAmount() {
    return ledgerBalanceAmount;
  }

  public void setLedgerBalanceDate(String ledgerBalanceDate) {
    this.ledgerBalanceDate = ledgerBalanceDate;
  }

  public String getLedgerBalanceDate() {
    return ledgerBalanceDate;
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

  public void setAllowPaymentId(String allowPaymentId) {
    this.allowPaymentId = allowPaymentId;
  }

  public String getAllowPaymentId() {
    return allowPaymentId;
  }

  public void setAllowRefundId(String allowRefundId) {
    this.allowRefundId = allowRefundId;
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

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
  }

  public void setReminderPrefKey(String reminderPrefKey) {
    this.reminderPrefKey = reminderPrefKey;
  }

  public String getReminderPrefKey() {
    return reminderPrefKey;
  }

  public void setLoginStamp(String loginStamp) {
    this.loginStamp = loginStamp;
  }

  public String getLoginStamp() {
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

    @Override
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