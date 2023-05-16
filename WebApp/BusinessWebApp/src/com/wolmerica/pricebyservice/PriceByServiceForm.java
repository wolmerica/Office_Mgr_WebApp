/*
 * PriceByServiceForm.java
 *
 * Created on August 09, 2006, 12:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricebyservice;

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

public class PriceByServiceForm extends MasterForm
{
  private String key;
  private String priceTypeKey;
  private String priceTypeName = "";
  private String customerTypeKey;
  private String customerAttributeByServiceKey;
  private String computedPrice;
  private String previousPrice;
  private String overRidePrice;

  public PriceByServiceForm() {
    addRequiredFields(new String[] { "computedPrice", "overRidePrice" } );
    addRange("computedPrice", new BigDecimal("0"), new BigDecimal("99999"));
    addRange("overRidePrice", new BigDecimal("0"), new BigDecimal("99999"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setPriceTypeKey(String priceTypeKey) {
    this.priceTypeKey = priceTypeKey;
  }

  public String getPriceTypeKey() {
    return priceTypeKey;
  }

  public void setPriceTypeName(String priceTypeName) {
    this.priceTypeName = priceTypeName;
  }

  public String getPriceTypeName() {
    return priceTypeName;
  }

  public void setCustomerTypeKey(String customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public String getCustomerTypeKey() {
    return customerTypeKey;
  }

  public void setCustomerAttributeByServiceKey(String customerAttributeByServiceKey) {
    this.customerAttributeByServiceKey = customerAttributeByServiceKey;
  }

  public String getCustomerAttributeByServiceKey() {
    return customerAttributeByServiceKey;
  }

  public void setComputedPrice(String computedPrice) {
    this.computedPrice = computedPrice;
  }

  public String getComputedPrice() {
    return computedPrice;
  }

  public void setPreviousPrice(String previousPrice) {
    this.previousPrice = previousPrice;
  }

  public String getPreviousPrice() {
    return previousPrice;
  }

  public void setOverRidePrice(String overRidePrice) {
    this.overRidePrice = overRidePrice;
  }

  public String getOverRidePrice() {
    return overRidePrice;
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