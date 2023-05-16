/*
 * PetAnestheticForm.java
 *
 * Created on August 24, 2007, 07:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.petanesthetic;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;

public class PetAnestheticForm extends MasterForm
{
  private String key;
  private String petExamKey;
  private String treatmentDate;
  private String applicationType;
  private String itemDictionaryKey;
  private String brandName;
  private String size;
  private String sizeUnit;
  private String dose;
  private String doseUnit;
  private String route;
  private String startHour;
  private String startMinute;
  private String resourceKey;
  private String resourceName;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;


  public PetAnestheticForm() {
    addRequiredFields(new String[] { "applicationType", "brandName", "dose", 
                                     "doseUnit", "route", "resourceKey" } );
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setPetExamKey(String petExamKey) {
    this.petExamKey = petExamKey;
  }

  public String getPetExamKey() {
    return petExamKey;
  }

  public void setTreatmentDate(String treatmentDate) {
    this.treatmentDate = treatmentDate;
  }

  public String getTreatmentDate() {
    return treatmentDate;
  }  
  
  public void setApplicationType(String applicationType) {
    this.applicationType = applicationType;
  }

  public String getApplicationType() {
    return applicationType;
  }

  public void setItemDictionaryKey(String itemDictionaryKey) {
    this.itemDictionaryKey = itemDictionaryKey;
  }

  public String getItemDictionaryKey() {
    return itemDictionaryKey;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getSize() {
    return size;
  }

  public void setSizeUnit(String sizeUnit) {
    this.sizeUnit = sizeUnit;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

  public void setDose(String dose) {
    this.dose = dose;
  }

  public String getDose() {
    return dose;
  }

  public void setDoseUnit(String doseUnit) {
    this.doseUnit = doseUnit;
  }

  public String getDoseUnit() {
    return doseUnit;
  }

  public void setRoute(String route) {
    this.route = route;
  }

  public String getRoute() {
    return route;
  }

  public void setStartHour(String startHour) {
    this.startHour = startHour;
  }

  public String getStartHour() {
    return startHour;
  }

  public void setStartMinute(String startMinute) {
    this.startMinute = startMinute;
  }

  public String getStartMinute() {
    return startMinute;
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

  public String getresourceName() {
    return resourceName;
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

    return errors;
  }
}