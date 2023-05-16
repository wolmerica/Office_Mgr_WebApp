/*
 * SystemListForm.java
 *
 * Created on August 26, 2005, 11:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
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

public class SystemListForm extends MasterForm {

  private String key;
  private String clientName;
  private String makeModel;
  private String processor;
  private String memory;
  private String operatingSystem;
  private String systemDate;
  private String lastServiceDate;
  private String activeId;
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

  public void setMemory(String memory) {
    this.memory = memory;
  }

  public String getMemory() {
    return memory;
  }

  public void setOperatingSystem(String operatingSystem) {
    this.operatingSystem = operatingSystem;
  }

  public String getOperatingSystem() {
    return operatingSystem;
  }

  public void setsystemDate(String systemDate) {
    this.systemDate = systemDate;
  }

  public String getSystemDate() {
    return systemDate;
  }

  public void setLastServiceDate(String lastServiceDate) {
    this.lastServiceDate = lastServiceDate;
  }

  public String getLastServiceDate() {
    return lastServiceDate;
  }
  
  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
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

