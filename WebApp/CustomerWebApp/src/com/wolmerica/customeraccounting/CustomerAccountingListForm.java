/*
 * CustomerAccountingListForm.java
 *
 * Created on March 28, 2006, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customeraccounting;

/**
 *
 * @author Richard
 */
import com.wolmerica.customer.CustomerActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.math.BigDecimal;

public class CustomerAccountingListForm extends MasterForm {

   private String key;
   private String customerKey;
   private String clientName;
   private String acctNum;
   private String acctName;
   private String transactionTypeKey;
   private String transactionTypeName;
   private String transactionTypeDescription;
   private String postDate;
   private String sourceTypeKey;
   private String sourceTypeName;
   private String sourceTypeDescription;
   private String sourceKey;
   private String number;
   private String amount;
   private String daysDue;
   private String amountDue;
   private String reconciledId;

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setCustomerKey(String customerKey) {
    this.customerKey = customerKey;
  }

  public String getCustomerKey() {
    return customerKey;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
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

  public void setReconciledId(String reconciledId) {
    this.reconciledId = reconciledId;
  }

  public String getReconciledId() {
    return reconciledId;
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