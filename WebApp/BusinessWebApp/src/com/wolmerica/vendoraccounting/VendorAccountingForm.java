/*
 * VendorAccountingForm.java
 *
 * Created on March 28, 2006, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendoraccounting;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.util.ArrayList;
import java.math.BigDecimal;

public class VendorAccountingForm extends MasterForm {

   private String key;
   private String vendorKey;
   private String vendorName;
   private String acctNum;
   private String acctName;
   private String transactionTypeKey;
   private String transactionTypeName;
   private String transactionTypeDescription;
   private String postDate;
   private ArrayList accountingTypeForm;
   private String sourceTypeKey;
   private String sourceTypeName;
   private String sourceTypeDescription;
   private String sourceKey;
   private String number;
   private String amount;
   private String daysDue;
   private String amountDue;
   private String note;
   private String reconciledId;
   private String permissionStatus;
   private String createUser;
   private String createStamp;
   private String updateUser;
   private String updateStamp;

  public VendorAccountingForm() {
    addRequiredFields(new String[] { "vendorKey", "acctName",
                                     "transactionTypeKey", "sourceTypeKey",
                                     "number", "amount" });
    addRange("amount",
              new BigDecimal("0.00"),
              new BigDecimal("999999.99"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setVendorKey(String vendorKey) {
    this.vendorKey = vendorKey;
  }

  public String getVendorKey() {
    return vendorKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
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

  public void setTransactionTypeKey(String transactionTypeKey) {
    this.transactionTypeKey = transactionTypeKey;
  }

  public String getTransactionTypeKey() {
    return transactionTypeKey;
  }

  public void setTransactionTypeName(String transactionTypeName) {
    this.transactionTypeName = transactionTypeName;
  }

  public String getTransactionTypeName() {
    return transactionTypeName;
  }

  public void setTransactionTypeDescription(String transactionTypeDescription) {
    this.transactionTypeDescription = transactionTypeDescription;
  }

  public String getTransactionTypeDescription() {
    return transactionTypeDescription;
  }

  public void setPostDate(String postDate) {
    this.postDate = postDate;
  }

  public String getPostDate() {
    return postDate;
  }

  public void setAccountingTypeForm(ArrayList accountingTypeList){
  this.accountingTypeForm = accountingTypeList;
  }

  public ArrayList getAccountingTypeForm(){
  return accountingTypeForm;
  }

  public void setSourceTypeKey(String sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public String getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceTypeName(String sourceTypeName) {
    this.sourceTypeName = sourceTypeName;
  }

  public String getSourceTypeName() {
    return sourceTypeName;
  }

  public void setSourceTypeDescription(String sourceTypeDescription) {
    this.sourceTypeDescription = sourceTypeDescription;
  }

  public String getSourceTypeDescription() {
    return sourceTypeDescription;
  }

  public void setSourceKey(String sourceKey) {
    this.sourceKey = sourceKey;
  }

  public String getSourceKey() {
    return sourceKey;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getNumber() {
    return number;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getAmount() {
    return amount;
  }

  public void setDaysDue(String daysDue) {
    this.daysDue = daysDue;
  }

  public String getDaysDue() {
    return daysDue;
  }

  public void setAmountDue(String amountDue) {
    this.amountDue = amountDue;
  }

  public String getAmountDue() {
    return amountDue;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getNote() {
    return note;
  }

  public void setReconciledId(String reconciledId) {
    this.reconciledId = reconciledId;
  }

  public String getReconciledId() {
    return reconciledId;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
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