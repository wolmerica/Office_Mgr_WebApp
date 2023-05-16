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
import com.wolmerica.customer.CustomerActionMapping;
import com.wolmerica.tools.formatter.MasterForm;
import com.wolmerica.tools.formatter.EmailFormatter;
import com.wolmerica.tools.formatter.PhoneNumberFormatter;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.Date;

public class CustomerListForm extends MasterForm {

  private String key;
  private String acctNum;
  private String customerTypeName;
  private String acctName;
  private String city;
  private String state;
  private String zip;
  private String phoneNum;
  private String mobileNum;
  private String faxNum;
  private String clinicId;
  private String accountBalanceAmount;
  private String accountBalanceDate;
  private String allowDeleteId;
  
  public CustomerListForm() {
    addRequiredFields(new String[] { "acctName", "address",
                                     "city", "state", "zip",
                                     "phoneNum", "acctNum" });
    setFormatterType("phoneNum", PhoneNumberFormatter.class);
    setFormatterType("mobileNum", PhoneNumberFormatter.class);
    setFormatterType("faxNum", PhoneNumberFormatter.class);
  }  
  
  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
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

  public void setClinicId(String clinicId) {
    this.clinicId = clinicId;
  }

  public String getClinicId() {
    return clinicId;
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

  public void setAllowDeleteId(String allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public String getAllowDeleteId() {
    return allowDeleteId;
  }   
    
  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();

    CustomerActionMapping customerMapping =
      (CustomerActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( customerMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("ACCTKEY") == null ) {

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

