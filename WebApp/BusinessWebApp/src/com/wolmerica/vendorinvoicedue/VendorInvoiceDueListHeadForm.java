/*
 * VendorInvoiceDueListForm.java
 *
 * Created on August 24, 2006, 9:32 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoicedue;

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

public class VendorInvoiceDueListHeadForm extends MasterForm
{
  private String vendorKey;
  private String vendorName;
  private String fromDate;
  private String toDate;
  private String payDate1;
  private String payDate2;
  private String payDate3;
  private String dayAverage1;
  private String dayAverage2;
  private String dayAverage3;
  private String recordCount;
  private String debitTotal;
  private String creditTotal;
  private String balanceTotal;
  private String firstRecord;
  private String lastRecord;
  private ArrayList vendorInvoiceDueListForm;

  public VendorInvoiceDueListHeadForm() {
    addRequiredFields(new String[] { "vendorKey", "fromDate", "toDate",
                                     "payDate1", "payDate2", "payDate3" } );
  }

  public void setVendorKey(String vendorKey) {
    this.vendorKey = vendorKey;
  }

  public String getVendorKey() {
    return vendorKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName =vendorName;
  }

  public String getVendorName() {
    return vendorName;
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

  public void setPayDate1(String payDate1) {
    this.payDate1 = payDate1;
  }

  public String getPayDate1() {
    return payDate1;
  }

  public void setPayDate2(String payDate2) {
    this.payDate2 = payDate2;
  }

  public String getPayDate2() {
    return payDate2;
  }

  public void setPayDate3(String payDate3) {
    this.payDate3 = payDate3;
  }

  public String getPayDate3() {
    return payDate3;
  }

  public void setDayAverage1(String dayAverage1) {
    this.dayAverage1 = dayAverage1;
  }

  public String getDayAverage1() {
    return dayAverage1;
  }

  public void setDayAverage2(String dayAverage2) {
    this.dayAverage2 = dayAverage2;
  }

  public String getDayAverage2() {
    return dayAverage2;
  }

  public void setDayAverage3(String dayAverage3) {
    this.dayAverage3 = dayAverage3;
  }

  public String getDayAverage3() {
    return dayAverage3;
  }

  public void setRecordCount(String recordCount) {
    this.recordCount = recordCount;
  }

  public String getRecordCount() {
    return recordCount;
  }

  public void setDebitTotal(String debitTotal) {
    this.debitTotal = debitTotal;
  }

  public String getDebitTotal() {
    return debitTotal;
  }

  public void setCreditTotal(String creditTotal) {
    this.creditTotal = creditTotal;
  }

  public String getCreditTotal() {
    return creditTotal;
  }

  public void setBalanceTotal(String balanceTotal) {
    this.balanceTotal = balanceTotal;
  }

  public String getBalanceTotal() {
    return balanceTotal;
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

  public void setVendorInvoiceDueListForm(ArrayList vendorInvoiceDueListForm){
  this.vendorInvoiceDueListForm = vendorInvoiceDueListForm;
  }

  public ArrayList getVendorInvoiceDueListForm(){
  return vendorInvoiceDueListForm;
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