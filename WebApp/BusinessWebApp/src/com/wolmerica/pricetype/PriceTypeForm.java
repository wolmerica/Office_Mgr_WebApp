/*
 * PriceTypeForm.java
 *
 * Created on March 14, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.pricetype;

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
import java.math.BigDecimal;

public class PriceTypeForm extends MasterForm {

  private String key;
  private String name;
  private String domainId;
  private String fullSizeId;
  private String unitCostBaseId;
  private String blueBookId;
  private String markUpRate;
  private String precedence;
  private String serviceRate;
  private String activeId;
  private String permissionStatus;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public PriceTypeForm() {
    addRequiredFields(new String[] { "name", "markUpRate", "precedence" });

    addRange("markUpRate",
              new BigDecimal("0.00"),
              new BigDecimal("999.99"));

    addRange("precedence",
              new Byte("0"),
              new Byte("16"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setDomainId(String domainId) {
    this.domainId = domainId;
  }

  public String getDomainId() {
    return domainId;
  }

  public void setFullSizeId(String fullSizeId) {
    this.fullSizeId = fullSizeId;
  }

  public String getFullSizeId() {
    return fullSizeId;
  }

  public void setUnitCostBaseId(String unitCostBaseId) {
    this.unitCostBaseId = unitCostBaseId;
  }

  public String getUnitCostBaseId() {
    return unitCostBaseId;
  }

  public void setBlueBookId(String blueBookId) {
    this.blueBookId = blueBookId;
  }

  public String getBlueBookId() {
    return blueBookId;
  }

  public void setMarkUpRate(String markUpRate) {
    this.markUpRate = markUpRate;
  }

  public String getMarkUpRate() {
    return markUpRate;
  }

  public void setPrecedence(String precedence) {
    this.precedence = precedence;
  }

  public String getPrecedence() {
    return precedence;
  }

  public void setServiceRate(String serviceRate) {
    this.serviceRate = serviceRate;
  }

  public String getServiceRate() {
    return serviceRate;
  }

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
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