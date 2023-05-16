/*
 * PriceByItemForm.java
 *
 * Created on January 31, 2006, 10:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricebyitem;

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

public class PriceByItemForm extends MasterForm
{

  private String key;
  private String priceTypeKey;
  private String customerTypeKey;
  private String priceAttributeByItemKey;
  private String computedPrice;
  private String previousPrice;
  private String overRidePrice;

  public PriceByItemForm() {
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

  public void setCustomerTypeKey(String customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public String getCustomerTypeKey() {
    return customerTypeKey;
  }

  public void setPriceAttributeByItemKey(String priceAttributeByItemKey) {
    this.priceAttributeByItemKey = priceAttributeByItemKey;
  }

  public String getPriceAttributeByItemKey() {
    return priceAttributeByItemKey;
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