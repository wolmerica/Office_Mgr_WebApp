/*
 * CustomerListHeadForm.java
 *
 * Created on March 08, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.ledger;

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

public class LedgerListHeadForm extends MasterForm
{
  private String mode;
  private String customerKey;
  private String clientName;
  private String fromDate;
  private String toDate;
  private String slipNumber;
  private String recordCount;
  private String grandTotal;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String nextPage;
  private ArrayList ledgerForm;

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getMode() {
    return mode;
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

  public void setSlipNumber(String slipNumber) {
    this.slipNumber = slipNumber;
  }

  public String getSlipNumber() {
    return slipNumber;
  }

  public void setRecordCount(String recordCount) {
    this.recordCount = recordCount;
  }

  public String getRecordCount() {
    return recordCount;
  }

  public void setGrandTotal(String grandTotal) {
    this.grandTotal = grandTotal;
  }

  public String getGrandTotal() {
    return grandTotal;
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

  public void setLedgerForm(ArrayList ledgerList){
  this.ledgerForm=ledgerList;
  }

  public ArrayList getLedgerForm(){
  return ledgerForm;
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