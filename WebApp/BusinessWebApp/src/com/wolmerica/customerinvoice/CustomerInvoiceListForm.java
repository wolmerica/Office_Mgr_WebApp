/*
 * CustomerInvoiceListForm.java
 *
 * Created on February 12, 2006, 4:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoice;

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

public class CustomerInvoiceListForm extends MasterForm {

  private String key;
  private String customerInvoiceNumber;
  private String customerKey;
  private String clientName;
  private String attributeToEntity;
  private String sourceTypeKey;
  private String sourceKey;
  private String attributeToName;
  private String scenarioKey;
  private String invoiceTotal;
  private String createStamp;
  private String activeId;
  private String creditId;

  public CustomerInvoiceListForm() {
    addRequiredFields(new String[] { "key", "customerInvoiceNumber",
                    "customerKey", "clientName", "invoiceTotal",
                    "createStamp", "activeId" } );
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setCustomerInvoiceNumber(String customerInvoiceNumber) {
    this.customerInvoiceNumber = customerInvoiceNumber;
  }

  public String getCustomerInvoiceNumber() {
    return customerInvoiceNumber;
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

  public void setAttributeToEntity(String attributeToEntity) {
    this.attributeToEntity = attributeToEntity;
  }

  public String getAttributeToEntity() {
    return attributeToEntity;
  }

  public void setSourceTypeKey(String sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public String getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceKey(String sourceKey) {
    this.sourceKey = sourceKey;
  }

  public String getSourceKey() {
    return sourceKey;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
  }

  public void setScenarioKey(String scenarioKey) {
    this.scenarioKey = scenarioKey;
  }

  public String getScenarioKey() {
    return scenarioKey;
  }

  public void setInvoiceTotal(String invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public String getInvoiceTotal(){
    return invoiceTotal;
  }

  public void setCreateStamp(String createStamp) {
    this.createStamp = createStamp;
  }

  public String getCreateStamp() {
    return createStamp;
  }

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
  }

  public void setCreditId(String creditId) {
    this.creditId = creditId;
  }

  public String getCreditId() {
    return creditId;
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

