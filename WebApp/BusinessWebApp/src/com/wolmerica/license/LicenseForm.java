/*
 * LicenseForm.java
 *
 * Created on April 9, 2010 1:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.license;

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

public class LicenseForm extends MasterForm {

  private String key;
  private String sourceTypeKey;
  private String sourceKey;
  private String licenseUser = "";
  private String licenseKey = "";
  private String noteLine1 = "";
  private String invoiceTypeKey;
  private String invoiceKey;
  private String assignId;
  private String releaseId;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;
  private String clientName;
  private String attributeToEntity;
  private String attributeToName;
  private String licenseForName;

  public LicenseForm() {
    addRequiredFields(new String[] { "sourceTypeKey", "sourceKey",
             "licenseUser", "licenseKey" });
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
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

  public void setLicenseUser(String licenseUser) {
    this.licenseUser = licenseUser;
  }

  public String getLicenseUser() {
    return licenseUser;
  }

  public void setLicenseKey(String licenseKey) {
    this.licenseKey = licenseKey;
  }

  public String getLicenseKey() {
    return licenseKey;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setInvoiceTypeKey(String invoiceTypeKey) {
    this.invoiceTypeKey = invoiceTypeKey;
  }

  public String getInvoiceTypeKey() {
    return invoiceTypeKey;
  }

  public void setInvoiceKey(String invoiceKey) {
    this.invoiceKey = invoiceKey;
  }

  public String getInvoiceKey() {
    return invoiceKey;
  }

  public String getAssignId() {
    return assignId;
  }

  public void setAssignId(String assignId) {
    this.assignId = assignId;
  }

  public String getReleaseId() {
    return releaseId;
  }

  public void setReleaseId(String releaseId) {
    this.releaseId = releaseId;
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

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
  }

  public void setLicenseForName(String licenseForName) {
    this.licenseForName = licenseForName;
  }

  public String getLicenseForName() {
    return licenseForName;
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

