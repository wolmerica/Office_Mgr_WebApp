/*
 * PetVacListForm.java
 *
 * Created on March 08, 2006, 9:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
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

public class PetVacListForm extends MasterForm {

  private String key;
  private String clientKey;
  private String clientName;
  private String petKey;
  private String petName;
  private String vacDate;
  private String rabiesId;
  private String vacExpirationDate;
  private String canineDistemperId;
  private String corunnaId;
  private String felineDistemperId;
  private String felineLeukemiaId;
  private String otherId;
  private String allowDeleteId;

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setClientKey(String clientKey) {
    this.clientKey = clientKey;
  }

  public String getClientKey() {
    return clientKey;
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
  
  public void setAllowDeleteId(String allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public String getAllowDeleteId() {
    return allowDeleteId;
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

