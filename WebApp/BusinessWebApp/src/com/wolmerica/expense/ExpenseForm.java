/*
 * ExpenseForm.java
 *
 * Created on July 02, 2006, 3:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.expense;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.math.BigDecimal;

public class ExpenseForm extends MasterForm {

  private String key;
  private String expenseDate;
  private String dueDate;
  private String paymentDate;
  private String expenseName;
  private String expenseCategory;
  private String expensePayment;
  private String expenseRate;
  private String expenseAmount;
  private String taxPrepDate;
  private String reconciledId;
  private String permissionStatus;
  private String attachmentCount;
  private String noteLine1;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public ExpenseForm() {
    addRequiredFields(new String[] { "expenseDate", "dueDate", "paymentDate",
                                     "expenseName", "expenseCategory",
                                     "expensePayment", "expenseRate", "taxPrepDate" });
    addRange("expensePayment", new BigDecimal("0.01"), new BigDecimal("99999.99"));
    addRange("expenseRate", new BigDecimal("0.01"), new BigDecimal("100.00"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setExpenseDate(String expenseDate) {
    this.expenseDate = expenseDate;
  }

  public String getExpenseDate() {
    return expenseDate;
  }

  public void setDueDate(String dueDate) {
    this.dueDate = dueDate;
  }

  public String getDueDate() {
    return dueDate;
  }

  public void setPaymentDate(String paymentDate) {
    this.paymentDate = paymentDate;
  }

  public String getPaymentDate() {
    return paymentDate;
  }

  public void setExpenseName(String expenseName) {
    this.expenseName = expenseName;
  }

  public String getExpenseName() {
    return expenseName;
  }

  public void setExpenseCategory(String expenseCategory) {
    this.expenseCategory = expenseCategory;
  }

  public String getExpenseCategory() {
    return expenseCategory;
  }

  public void setExpensePayment(String expensePayment) {
    this.expensePayment = expensePayment;
  }

  public String getExpensePayment(){
    return expensePayment;
  }

  public void setExpenseRate(String expenseRate) {
    this.expenseRate = expenseRate;
  }

  public String getExpenseRate(){
    return expenseRate;
  }

  public void setExpenseAmount(String expenseAmount) {
    this.expenseAmount = expenseAmount;
  }

  public String getExpenseAmount(){
    return expenseAmount;
  }

  public void setTaxPrepDate(String taxPrepDate) {
    this.taxPrepDate = taxPrepDate;
  }

  public String getTaxPrepDate() {
    return taxPrepDate;
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

  public void setAttachmentCount(String attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public String getAttachmentCount() {
    return attachmentCount;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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

