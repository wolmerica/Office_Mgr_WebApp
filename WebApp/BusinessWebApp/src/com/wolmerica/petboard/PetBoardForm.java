/*
 * PetBoardForm.java
 *
 * Created on June 20, 2007, 09:54 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petboard;

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

public class PetBoardForm extends MasterForm {
  private String key;
  private String clientName;
  private String petKey;
  private String petName;
  private String scheduleKey;
  private String checkInDate;
  private String checkInHour;
  private String checkInMinute;
  private String checkOutDate;
  private String checkOutHour;
  private String checkOutMinute;
  private String checkOutTo;
  private String boardReason;
  private String boardInstruction;
  private String emergencyName;
  private String emergencyPhone;
  private String vaccinationId;
  private String specialDietId;
  private String medicationId;
  private String serviceId;
  private String permissionStatus;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public PetBoardForm() {
    addRequiredFields(new String[] { "scheduleKey", "petKey", "clientName",
                                     "boardReason", "boardInstruction", "emergencyName",
                                     "emergencyPhone"});
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

  public void setCheckInDate(String checkInDate) {
    this.checkInDate = checkInDate;
  }

  public String getCheckInDate() {
    return checkInDate;
  }

  public void setCheckInHour(String checkInHour) {
    this.checkInHour = checkInHour;
  }

  public String getCheckInHour() {
    return checkInHour;
  }

  public void setCheckInMinute(String checkInMinute) {
    this.checkInMinute = checkInMinute;
  }

  public String getCheckInMinute() {
    return checkInMinute;
  }

  public void setCheckOutDate(String checkOutDate) {
    this.checkOutDate = checkOutDate;
  }

  public String getCheckOutDate() {
    return checkOutDate;
  }

  public void setCheckOutHour(String checkOutHour) {
    this.checkOutHour = checkOutHour;
  }

  public String getCheckOutHour() {
    return checkOutHour;
  }

  public void setCheckOutMinute(String checkOutMinute) {
    this.checkOutMinute = checkOutMinute;
  }

  public String getCheckOutMinute() {
    return checkOutMinute;
  }

  public void setCheckOutTo(String checkOutTo) {
    this.checkOutTo = checkOutTo;
  }

  public String getCheckOutTo() {
    return checkOutTo;
  }

  public void setBoardReason(String boardReason) {
    this.boardReason = boardReason;
  }

  public String getBoardReason() {
    return boardReason;
  }

  public void setBoardInstruction(String boardInstruction) {
    this.boardInstruction = boardInstruction;
  }

  public String getBoardInstruction() {
    return boardInstruction;
  }

  public void setEmergencyName(String emergencyName) {
    this.emergencyName = emergencyName;
  }

  public String getEmergencyName() {
    return emergencyName;
  }

  public void setEmergencyPhone(String emergencyPhone) {
    this.emergencyPhone = emergencyPhone;
  }

  public String getEmergencyPhone() {
    return emergencyPhone;
  }

  public void setVaccinationId(String vaccinationId) {
    this.vaccinationId = vaccinationId;
  }

  public String getVaccinationId() {
    return vaccinationId;
  }

  public void setSpecialDietId(String specialDietId) {
    this.specialDietId = specialDietId;
  }

  public String getSpecialDietId() {
    return specialDietId;
  }

  public void setMedicationId(String medicationId) {
    this.medicationId = medicationId;
  }

  public String getMedicationId() {
    return medicationId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceId() {
    return serviceId;
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