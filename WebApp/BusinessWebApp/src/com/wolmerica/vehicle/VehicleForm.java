/*
 * VehicleForm.java
 *
 * Created on June 10, 2007, 10:38 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.vehicle;

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

public class VehicleForm extends MasterForm {

  private String key;
  private String customerKey;
  private String clientName;
  private String year;
  private String make;
  private String model;
  private String engine;
  private String odometer;
  private String color;
  private String vinNumber;
  private String vehicleDate;
  private String noteLine1;
  private String documentServerURL;
  private String photoFileName;
  private String permissionStatus;
  private String attachmentCount;
  private String activeId;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;
  private String clientNameFilter;
  private String yearFilter;
  private String makeFilter;  
  private String currentPage;  
  
  public VehicleForm() {
    addRequiredFields(new String[] { "clientKey", "year", "make", "model",
                              "odometer", "color", "vehicleDate" });

    addRange("year", new Short("1900"), new Short("2107"));
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

  public void setYear(String year) {
    this.year = year;
  }

  public String getYear() {
    return year;
  }

  public void setMake(String make) {
    this.make = make;
  }

  public String getMake() {
    return make;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getModel() {
    return model;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public String getEngine() {
    return engine;
  }

  public void setOdometer(String odometer) {
    this.odometer = odometer;
  }

  public String getOdometer() {
    return odometer;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getColor() {
    return color;
  }

  public void setVinNumber(String vinNumber) {
    this.vinNumber = vinNumber;
  }

  public String getVinNumber() {
    return vinNumber;
  }

  public void setVehicleDate(String vehicleDate) {
    this.vehicleDate = vehicleDate;
  }

  public String getVehicleDate() {
    return vehicleDate;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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

  public void setYearFilter(String yearFilter) {
    this.yearFilter = yearFilter;
  }

  public String getYearFilter() {
    return yearFilter;
  }

  public void setMakeFilter(String makeFilter) {
    this.makeFilter = makeFilter;
  }

  public String getMakeFilter() {
    return makeFilter;
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