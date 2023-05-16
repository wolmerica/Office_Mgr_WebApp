/*
 * SystemForm.java
 *
 * Created on August 26, 2005, 11:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.system;

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

public class SystemForm extends MasterForm {

  private String key;
  private String customerKey;
  private String clientName;
  private String makeModel;
  private String processor;
  private String operatingSystem;
  private String memory;
  private String primaryDrive;
  private String auxilaryDrive;
  private String softwarePackage;
  private String systemDate;
  private String macAddress = "";
  private String macAddress2 = "";  
  private String serialNumber = "";
  private String noteLine1 = "";
  private String noteLine2 = "";
  private String noteLine3 = "";  
  private String documentServerURL = "";
  private String photoFileName;
  private String permissionStatus;
  private String attachmentCount;
  private String activeId;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;
  private String clientNameFilter;
  private String makeModelFilter;  
  private String currentPage;    

  public SystemForm() {
    addRequiredFields(new String[] { "clientKey", "makeModel", "processor",
                              "operatingSystem", "memory", "primaryDrive", "systemDate" });
  }

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

  public void setMakeModel(String makeModel) {
    this.makeModel = makeModel;
  }

  public String getMakeModel() {
    return makeModel;
  }

  public void setProcessor(String processor) {
    this.processor = processor;
  }

  public String getProcessor() {
    return processor;
  }

  public void setOperatingSystem(String operatingSystem) {
    this.operatingSystem = operatingSystem;
  }

  public String getOperatingSystem() {
    return operatingSystem;
  }

  public void setMemory(String memory) {
    this.memory = memory;
  }

  public String getMemory() {
    return memory;
  }

  public void setPrimaryDrive(String primaryDrive) {
    this.primaryDrive = primaryDrive;
  }

  public String getPrimaryDrive() {
    return primaryDrive;
  }

  public void setAuxilaryDrive(String auxilaryDrive) {
    this.auxilaryDrive = auxilaryDrive;
  }

  public String getAuxilaryDrive() {
    return auxilaryDrive;
  }

  public void setSoftwarePackage(String softwarePackage) {
    this.softwarePackage = softwarePackage;
  }

  public String getSoftwarePackage() {
    return softwarePackage;
  }

  public void setSystemDate(String systemDate) {
    this.systemDate = systemDate;
  }

  public String getSystemDate() {
    return systemDate;
  }
  
  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public void setMacAddress2(String macAddress2) {
    this.macAddress2 = macAddress2;
  }

  public String getMacAddress2() {
    return macAddress2;
  }  
  
  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public String getSerialNumber() {
    return serialNumber;
  }  

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setNoteLine2(String noteLine2) {
    this.noteLine2 = noteLine2;
  }

  public String getNoteLine2() {
    return noteLine2;
  }  

  public void setNoteLine3(String noteLine3) {
    this.noteLine3 = noteLine3;
  }

  public String getNoteLine3() {
    return noteLine3;
  }  
  
  public void setDocumentServerURL(String documentServerURL) {
    this.documentServerURL = documentServerURL;
  }

  public String getDocumentServerURL() {
    return documentServerURL;
  }

  public void setPhotoFileName(String photoFileName) {
    this.photoFileName = photoFileName;
  }

  public String getPhotoFileName() {
    return photoFileName;
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

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
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
  
  public void setClientNameFilter(String clientNameFilter) {
    this.clientNameFilter = clientNameFilter;
  }

  public String getClientNameFilter() {
    return clientNameFilter;
  }

  public void setMakeModelFilter(String makeModelFilter) {
    this.makeModelFilter = makeModelFilter;
  }

  public String getMakeModelFilter() {
    return makeModelFilter;
  }
  
  public void setCurrentPage(String currentPage ) {
    this.currentPage = currentPage;
  }

  public String getCurrentPage() {
    return currentPage;
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