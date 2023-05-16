/*
 * ItemOriginForm.java
 *
 * Created on January 17, 2007, 8:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.itemorigin;

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

public class ItemOriginForm extends MasterForm
{
   private String transactionKey;
   private String transactionDate;
   private String transactionName;
   private String vendorKey;
   private String vendorName;
   private String customerKey;
   private String clientName;
   private String expirationDate;
   private String quantity;

   public void setTransactionKey(String transactionKey) {
     this.transactionKey = transactionKey;
   }

   public String getTransactionKey() {
     return transactionKey;
   }

   public void setTransactionDate(String transactionDate) {
     this.transactionDate = transactionDate;
   }

   public String getTransactionDate() {
     return transactionDate;
   }

   public void setTransactionName(String transactionName) {
     this.transactionName = transactionName;
   }

   public String getTransactionName() {
     return transactionName;
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

   public void setExpirationDate(String expirationDate) {
     this.expirationDate = expirationDate;
   }

   public String getExpirationDate() {
     return expirationDate;
   }

   public void setQuantity(String quantity) {
     this.quantity = quantity;
   }

   public String getQuantity() {
     return quantity;
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

    return errors;
  }
}