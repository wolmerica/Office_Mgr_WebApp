/*
 * VendorInvoiceDueListForm.java
 *
 * Created on March 28, 2006, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
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

public class VendorInvoiceDueListForm extends MasterForm {

  private String key;
  private String purchaseOrderKey;
  private String masterKey;
  private String genesisKey;
  private String purchaseOrderNumber;
  private String invoiceNumber;
  private String invoiceDate;
  private String invoiceDueDate;
  private String dayCount;
  private String dayAverage;
  private String invoiceAmount;

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setPurchaseOrderKey(String purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public String getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setMasterKey(String masterKey) {
    this.masterKey = masterKey;
  }

  public String getMasterKey() {
    return masterKey;
  }

  public void setGenesisKey(String genesisKey) {
    this.genesisKey = genesisKey;
  }

  public String getGenesisKey() {
    return genesisKey;
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDueDate(String invoiceDueDate) {
    this.invoiceDueDate = invoiceDueDate;
  }

  public String getInvoiceDueDate() {
    return invoiceDueDate;
  }

  public void setDayCount(String dayCount) {
    this.dayCount = dayCount;
  }

  public String getDayCount() {
    return dayCount;
  }

  public void setDayAverage(String dayAverage) {
    this.dayAverage = dayAverage;
  }

  public String getDayAverage() {
    return dayAverage;
  }

  public void setInvoiceAmount(String invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public String getInvoiceAmount(){
    return invoiceAmount;
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