/*
 * CustomerAttributeByServiceForm.java
 *
 * Created on August 08, 2006, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.customerattributebyservice;

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

public class CustomerAttributeByServiceForm extends MasterForm
{
  private String key;
  private String customerTypeKey;
  private String customerTypeName;
  private String fee1Cost;
  private String fee2Cost;
  private String markUp1Rate;
  private String markUp2Rate;
  private String discountThreshold;
  private String discountRate;

  public CustomerAttributeByServiceForm() {
    addRequiredFields(new String[] { "customerTypeKey", "customerTypename",
                   "fee1Cost", "fee2Cost", "markUp1Rate",
                   "markUp2Rate", "discountThreshold", "discountRate" } );
    addRange("fee1Cost", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("fee2Cost", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("markUp1Rate", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("markUp2Rate", new BigDecimal("0"), new BigDecimal("999.99"));
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

  public void setFee1Cost(String fee1Cost) {
    this.fee1Cost = fee1Cost;
  }

  public String getFee1Cost() {
    return fee1Cost;
  }

  public void setFee2Cost(String fee2Cost) {
    this.fee2Cost = fee2Cost;
  }

  public String getFee2Cost() {
    return fee2Cost;
  }


  public void setMarkUp1Rate(String markUp1Rate) {
    this.markUp1Rate = markUp1Rate;
  }

  public String getMarkUp1Rate() {
    return markUp1Rate;
  }

  public void setMarkUp2Rate(String markUp2Rate) {
    this.markUp2Rate = markUp2Rate;
  }

  public String getMarkUp2Rate() {
    return markUp2Rate;
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