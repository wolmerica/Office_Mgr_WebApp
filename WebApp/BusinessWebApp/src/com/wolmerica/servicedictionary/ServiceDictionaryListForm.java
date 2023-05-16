/*
 * ServiceDictionaryListForm.java
 *
 * Created on June 20, 2006, 07:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.servicedictionary;

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

public class ServiceDictionaryListForm extends MasterForm {

  private String key;
  private String serviceName;
  private String serviceNum;
  private String serviceCategory;
  private String profileNum;
  private String releaseId;
  private String priceTypeName;
  private String durationHours;
  private String durationMinutes;
  private String serviceCost;
  private String serviceHoursSold;
  private String allowDeleteId;

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }
  
  public void setServiceNum(String serviceNum) {
    this.serviceNum = serviceNum;
  }

  public String getServiceNum() {
    return serviceNum;
  }

  public void setServiceCategory(String serviceCategory) {
    this.serviceCategory = serviceCategory;
  }

  public String getServiceCategory() {
    return serviceCategory;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }
  
  public void setReleaseId(String releaseId) {
    this.releaseId = releaseId;
  }

  public String getReleaseId() {
    return releaseId;
  }
  
  public void setPriceTypeName(String priceTypeName) {
    this.priceTypeName = priceTypeName;
  }

  public String getPriceTypeName() {
    return priceTypeName;
  }

  public void setDurationHours(String durationHours) {
    this.durationHours = durationHours;
  }

  public String getDurationHours() {
    return durationHours;
  }

  public void setDurationMinutes(String durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public String getDurationMinutes() {
    return durationMinutes;
  }

  public void setServiceCost(String serviceCost) {
    this.serviceCost = serviceCost;
  }

  public String getServiceCost() {
    return serviceCost;
  }

  public void setServiceHoursSold(String serviceHoursSold) {
    this.serviceHoursSold = serviceHoursSold;
  }

  public String getServiceHoursSold() {
    return serviceHoursSold;
  }

  public void setAllowDeleteId(String allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public String getAllowDeleteId() {
    return allowDeleteId;
  }

    @Override  
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

