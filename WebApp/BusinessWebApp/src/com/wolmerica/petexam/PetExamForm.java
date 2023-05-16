/*
 * PetExamForm.java
 *
 * Created on August 23, 2007, 08:49 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petexam;

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

public class PetExamForm extends MasterForm {
  private String key;
  private String clientName;
  private String petKey;
  private String petName;
  private String scheduleKey;
  private String treatmentDate;
  private String subject;
  private String dvmResourceKey;
  private String dvmResourceName;
  private String techResourceKey;
  private String techResourceName;
  private String heartRate;
  private String resptRate;
  private String capRefillTime;
  private String temperature;
  private String bodyWeight;
  private String generalCondition;
  private String medicalData;
  private String startHour;
  private String startMinute;
  private String surgeryHour;
  private String surgeryMinute;
  private String endHour;
  private String endMinute;
  private String reflexHour;
  private String reflexMinute;
  private String recoveryHour;
  private String recoveryMinute;
  private String noteLine1;
  private String releaseId;
  private String permissionStatus;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public PetExamForm() {
    addRequiredFields(new String[] { "petName", "treatmentDate", "dvmResourceKey",
                                     "techResourceKey" });
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
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

  public void setScheduleKey(String scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public String getScheduleKey() {
    return scheduleKey;
  }

  public void setTreatmentDate(String treatmentDate) {
    this.treatmentDate = treatmentDate;
  }

  public String getTreatmentDate() {
    return treatmentDate;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return subject;
  }

  public void setDvmResourceKey(String dvmResourceKey) {
    this.dvmResourceKey = dvmResourceKey;
  }

  public String getDvmResourceKey() {
    return dvmResourceKey;
  }

  public void setDvmResourceName(String dvmResourceName) {
    this.dvmResourceName = dvmResourceName;
  }

  public String getDvmResourceName() {
    return dvmResourceName;
  }

  public void setTechResourceKey(String techResourceKey) {
    this.techResourceKey = techResourceKey;
  }

  public String getTechResourceKey() {
    return techResourceKey;
  }

  public void setTechResourceName(String techResourceName) {
    this.techResourceName = techResourceName;
  }

  public String getTechResourceName() {
    return techResourceName;
  }

  public void setHeartRate(String heartRate) {
    this.heartRate = heartRate;
  }

  public String getHeartRate() {
    return heartRate;
  }

  public void setResptRate(String resptRate) {
    this.resptRate = resptRate;
  }

  public String getResptRate() {
    return resptRate;
  }

  public void setCapRefillTime(String capRefillTime) {
    this.capRefillTime = capRefillTime;
  }

  public String getCapRefillTime() {
    return capRefillTime;
  }

  public void setTemperature(String temperature) {
    this.temperature = temperature;
  }

  public String getTemperature() {
    return temperature;
  }

  public void setBodyWeight(String bodyWeight) {
    this.bodyWeight = bodyWeight;
  }

  public String getBodyWeight() {
    return bodyWeight;
  }

  public void setGeneralCondition(String generalCondition) {
    this.generalCondition = generalCondition;
  }

  public String getGeneralCondition() {
    return generalCondition;
  }

  public void setMedicalData(String medicalData) {
    this.medicalData = medicalData;
  }

  public String getMedicalData() {
    return medicalData;
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

  public void setSurgeryHour(String surgeryHour) {
    this.surgeryHour = surgeryHour;
  }

  public String getSurgeryHour() {
    return surgeryHour;
  }

  public void setSurgeryMinute(String surgeryMinute) {
    this.surgeryMinute = surgeryMinute;
  }

  public String getSurgeryMinute() {
    return surgeryMinute;
  }

  public void setEndHour(String endHour) {
    this.endHour = endHour;
  }

  public String getEndHour() {
    return endHour;
  }

  public void setEndMinute(String endMinute) {
    this.endMinute = endMinute;
  }

  public String getEndMinute() {
    return endMinute;
  }

  public void setReflexHour(String reflexHour) {
    this.reflexHour = reflexHour;
  }

  public String getReflexHour() {
    return reflexHour;
  }

  public void setReflexMinute(String reflexMinute) {
    this.reflexMinute = reflexMinute;
  }

  public String getReflexMinute() {
    return reflexMinute;
  }

  public void setRecoveryHour(String recoveryHour) {
    this.recoveryHour = recoveryHour;
  }

  public String getRecoveryHour() {
    return recoveryHour;
  }

  public void setRecoveryMinute(String recoveryMinute) {
    this.recoveryMinute = recoveryMinute;
  }

  public String getRecoveryMinute() {
    return recoveryMinute;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setReleaseId(String releaseId) {
    this.releaseId = releaseId;
  }

  public String getReleaseId() {
    return releaseId;
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