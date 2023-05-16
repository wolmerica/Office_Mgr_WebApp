/*
 * LedgerForm.java
 *
 * Created on September 23, 2005, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.ledger;

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
import java.math.BigDecimal;

public class LedgerForm extends MasterForm {

   private String key;
   private String customerInvoiceKey;
   private String customerKey;
   private String clientName;
   private String invoiceNumber;
   private String invoiceTotal;
   private String postStamp;
   private String slipNumber;
   private String archivedId;
   private String permissionStatus;
   private String createUser;
   private String createStamp;
   private String updateUser;
   private String updateStamp;

  public LedgerForm() {
    addRequiredFields(new String[] { "customerKey", "invoiceNumber", "invoiceTotal" });
    addRange("invoiceTotal",
              new BigDecimal("0.00"),
              new BigDecimal("99999.99"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setCustomerInvoiceKey(String customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public String getCustomerInvoiceKey() {
    return customerInvoiceKey;
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

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceTotal(String invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public String getInvoiceTotal() {
    return invoiceTotal;
  }

  public void setSlipNumber(String slipNumber) {
    this.slipNumber = slipNumber;
  }

  public String getSlipNumber() {
    return slipNumber;
  }

 public void setPostStamp(String postStamp) {

    this.postStamp = postStamp;
  }

public String getPostStamp() {

    return postStamp;
  }

  public void setArchivedId(String archivedId) {

    this.archivedId = archivedId;
  }

  public String getArchivedId() {

    return archivedId;
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

