/*
 * ExpenseListHeadForm.java
 *
 * Created on July 02, 2006, 3:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
import java.util.ArrayList;

public class ExpenseListHeadForm extends MasterForm {

  private String expenseNameFilter;
  private String mode;
  private String fromDate;
  private String toDate;
  private String recordCount;
  private String paymentPageTotal;
  private String paymentGrandTotal;
  private String expensePageTotal;
  private String expenseGrandTotal;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String currentPage;
  private String nextPage;
  private ArrayList expenseListForm;
  private ArrayList permissionListForm;

  public void setExpenseNameFilter(String expenseNameFilter) {
    this.expenseNameFilter = expenseNameFilter;
  }

  public String getExpenseNameFilter() {
    return expenseNameFilter;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getMode() {
    return mode;
  }

  public void setFromDate(String fromDate) {
    this.fromDate = fromDate;
  }

  public String getFromDate() {
    return fromDate;
  }

  public void setToDate(String toDate) {
    this.toDate = toDate;
  }

  public String getToDate() {
    return toDate;
  }

  public void setRecordCount(String recordCount) {
    this.recordCount = recordCount;
  }

  public String getRecordCount() {
    return recordCount;
  }

  public void setPaymentPageTotal(String paymentPageTotal) {
    this.paymentPageTotal = paymentPageTotal;
  }

  public String getPaymentPageTotal() {
    return paymentPageTotal;
  }

  public void setPaymentGrandTotal(String paymentGrandTotal) {
    this.paymentGrandTotal = paymentGrandTotal;
  }

  public String getPaymentGrandTotal() {
    return paymentGrandTotal;
  }

  public void setExpensePageTotal(String expensePageTotal) {
    this.expensePageTotal = expensePageTotal;
  }

  public String getExpensePageTotal() {
    return expensePageTotal;
  }

  public void setExpenseGrandTotal(String expenseGrandTotal) {
    this.expenseGrandTotal = expenseGrandTotal;
  }

  public String getExpenseGrandTotal() {
    return expenseGrandTotal;
  }

  public void setFirstRecord(String firstRecord) {
    this.firstRecord = firstRecord;
  }

  public String getFirstRecord() {
    return firstRecord;
  }

  public void setLastRecord(String lastRecord) {
    this.lastRecord = lastRecord;
  }

  public String getLastRecord() {
    return lastRecord;
  }

  public void setCurrentPage(String currentPage ) {
    this.currentPage = currentPage;
  }

  public String getCurrentPage() {
    return currentPage;
  }

  public void setPreviousPage(String previousPage ) {
    this.previousPage = previousPage;
  }

  public String getPreviousPage() {
    return previousPage;
  }

  public void setNextPage(String nextPage ) {
    this.nextPage = nextPage;
  }

  public String getNextPage() {
    return nextPage;
  }

  public void setExpenseListForm(ArrayList expenseListForm){
    this.expenseListForm=expenseListForm;
  }

  public ArrayList getExpenseListForm(){
    return expenseListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
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

