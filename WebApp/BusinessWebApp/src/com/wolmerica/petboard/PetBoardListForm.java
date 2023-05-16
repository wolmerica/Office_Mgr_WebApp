/*
 * PetBoardListForm.java
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

public class PetBoardListForm extends MasterForm {
  private String key;
  private String clientName;
  private String petKey;
  private String petName;
  private String scheduleKey;
  private String checkInDate;
  private String boardReason;
  private String boardInstruction;
  private String allowDeleteId;

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