/*
 * PetVacForm.java
 *
 * Created on May 07, 2007, 09:54 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petvac;

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

public class PetVacForm extends MasterForm {
  private String key;
  private String scheduleKey;
  private String clientName;
  private String petKey;
  private String petName;
  private String resourceKey;
  private String resourceName;
  private String vacDate;
  private String rabiesId;
  private String rabiesTagNumber;
  private String vacRouteNumber;
  private String vacName;
  private String vacSerialNumber;
  private String vacExpirationDate;
  private String canineDistemperId;
  private String corunnaId;
  private String felineDistemperId;
  private String felineLeukemiaId;
  private String otherId;
  private String bundleKey;
  private String bundleName;
  private String reminderKey;
  private String reminderDate;
  private String noteLine1;
  private String permissionStatus;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public PetVacForm() {
    addRequiredFields(new String[] { "scheduleKey", "vacDate", "clientName",
                                     "petName", "resourceKey", "vacExpirationDate" });
//    addRange("rabiesId", new String("0"), new String("1"));
//    addRange("canineDistemperId", new String("0"), new String("1"));
//    addRange("corunnaId", new String("0"), new String("1"));
//    addRange("felineDistemperId", new String("0"), new String("1"));
//    addRange("felineLeukemiaId", new String("0"), new String("1"));
//    addRange("otherId", new String("0"), new String("1"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setScheduleKey(String scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public String getScheduleKey() {
    return scheduleKey;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setPetKey(String petKey) {
    this.petKey = petKey;
  }

  public String getPetKey() {
    return petKey;
  }  
  
  public void setPetName(String petName) {
    this.petName = petName;
  }

  public String getPetName() {
    return petName;
  }

  public void setResourceKey(String resourceKey) {
    this.resourceKey = resourceKey;
  }

  public String getResourceKey() {
    return resourceKey;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setVacDate(String vacDate) {
    this.vacDate = vacDate;
  }

  public String getVacDate() {
    return vacDate;
  }

  public void setRabiesId(String rabiesId) {
    this.rabiesId = rabiesId;
  }

  public String getRabiesId() {
    return rabiesId;
  }

  public void setRabiesTagNumber(String rabiesTagNumber) {
    this.rabiesTagNumber = rabiesTagNumber;
  }

  public String getRabiesTagNumber() {
    return rabiesTagNumber;
  }

  public void setVacRouteNumber(String vacRouteNumber) {
    this.vacRouteNumber = vacRouteNumber;
  }

  public String getVacRouteNumber() {
    return vacRouteNumber;
  }

  public void setVacName(String vacName) {
    this.vacName = vacName;
  }

  public String getVacName() {
    return vacName;
  }

  public void setVacSerialNumber(String vacSerialNumber) {
    this.vacSerialNumber = vacSerialNumber;
  }

  public String getVacSerialNumber() {
    return vacSerialNumber;
  }

  public void setVacExpirationDate(String vacExpirationDate) {
    this.vacExpirationDate = vacExpirationDate;
  }

  public String getVacExpirationDate() {
    return vacExpirationDate;
  }

  public void setCanineDistemperId(String canineDistemperId) {
    this.canineDistemperId = canineDistemperId;
  }

  public String getCanineDistemperId() {
    return canineDistemperId;
  }

  public void setCorunnaId(String corunnaId) {
    this.corunnaId = corunnaId;
  }

  public String getCorunnaId() {
    return corunnaId;
  }

  public void setFelineDistemperId(String felineDistemperId) {
    this.felineDistemperId = felineDistemperId;
  }

  public String getFelineDistemperId() {
    return felineDistemperId;
  }

  public void setFelineLeukemiaId(String felineLeukemiaId) {
    this.felineLeukemiaId = felineLeukemiaId;
  }

  public String getFelineLeukemiaId() {
    return felineLeukemiaId;
  }

  public void setOtherId(String otherId) {
    this.otherId = otherId;
  }

  public String getOtherId() {
    return otherId;
  }

  public void setBundleKey(String bundleKey) {
    this.bundleKey = bundleKey;
  }

  public String getBundleKey() {
    return bundleKey;
  }

  public void setBundleName(String bundleName) {
    this.bundleName = bundleName;
  }

  public String getBundleName() {
    return bundleName;
  }

  public void setReminderKey(String reminderKey) {
    this.reminderKey = reminderKey;
  }

  public String getReminderKey() {
    return reminderKey;
  }

  public void setReminderDate(String reminderDate) {
    this.reminderDate = reminderDate;
  }

  public String getReminderDate() {
    return reminderDate;
  }

  public void setNoteLine1(String noteLine1) {

    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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