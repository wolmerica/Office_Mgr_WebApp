/*
 * CustomerAttributeByItemForm.java
 *
 * Created on January 31, 2005, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.customerattributebyitem;

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
import java.math.BigDecimal;

public class CustomerAttributeByItemForm extends MasterForm
{
  private String key;
  private String customerTypeKey;
  private String customerTypeName;
  private String labelCost;
  private String adminCost;
  private String markUpRate;
  private String additionalMarkUpRate;
  private String discountThreshold;
  private String discountRate;

  public CustomerAttributeByItemForm() {
    addRequiredFields(new String[] { "customerTypeKey", "customerTypename",
                   "labelCost", "adminCost", "markUpRate",
                   "additionalMarkUpRate", "discountThreshold", "discountRate" } );
    addRange("labelCost", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("adminCost", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("markUpRate", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("additionalMarkUpRate", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("discountThreshold", new BigDecimal("0"), new BigDecimal("9999.99"));
    addRange("discountRate", new BigDecimal("0"), new BigDecimal("999.99"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setCustomerTypeKey(String customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public String getCustomerTypeKey() {
    return customerTypeKey;
  }

  public void setCustomerTypeName(String customerTypeName) {
    this.customerTypeName = customerTypeName;
  }

  public String getCustomerTypeName() {
    return customerTypeName;
  }

  public void setLabelCost(String labelCost) {
    this.labelCost = labelCost;
  }

  public String getLabelCost() {
    return labelCost;
  }

  public void setAdminCost(String adminCost) {
    this.adminCost = adminCost;
  }

  public String getAdminCost() {
    return adminCost;
  }


  public void setMarkUpRate(String markUpRate) {
    this.markUpRate = markUpRate;
  }

  public String getMarkUpRate() {
    return markUpRate;
  }

  public void setAdditionalMarkUpRate(String additionalMarkUpRate) {
    this.additionalMarkUpRate = additionalMarkUpRate;
  }

  public String getAdditionalMarkUpRate() {
    return additionalMarkUpRate;
  }

  public void setDiscountThreshold(String discountThreshold) {
    this.discountThreshold = discountThreshold;
  }

  public String getDiscountThreshold() {
    return discountThreshold;
  }

  public void setDiscountRate(String discountRate) {
    this.discountRate = discountRate;
  }

  public String getDiscountRate() {
    return discountRate;
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