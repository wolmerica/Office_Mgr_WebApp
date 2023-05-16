/*
 * CustomerInvoiceReportDetailForm.java
 *
 * Created on February 17, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.customerinvoicereportdetail;

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

public class CustomerInvoiceReportDetailForm extends MasterForm {

  private String customerInvoiceDetailKey;
  private String customerInvoiceKey;
  private String dateOfService;
  private String lineDetailNumber;
  private String lineDetailName;
  private String lineDetailCategory;
  private String lineDetailSize;
  private String lineDetailUnit;
  private String orderQty;
  private String costBasis;
  private String thePrice;
  private String discountRate;
  private String discountAmount;
  private String extendPrice;
  private String noteLine1;

  public void setCustomerInvoiceDetailKey(String customerInvoiceDetailKey) {
    this.customerInvoiceDetailKey = customerInvoiceDetailKey;
  }

  public String getCustomerInvoiceDetailKey() {
    return customerInvoiceDetailKey;
  }

  public void setCustomerInvoiceKey(String customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public String getCustomerInvoiceKey() {
    return customerInvoiceKey;
  }

  public void setDateOfService(String dateOfService) {
    this.dateOfService = dateOfService;
  }

  public String getDateOfService() {
    return dateOfService;
  }

  public void setLineDetailNumber(String lineDetailNumber) {
    this.lineDetailNumber = lineDetailNumber;
  }

  public String getLineDetailNumber() {
    return lineDetailNumber;
  }

  public void setLineDetailName(String lineDetailName) {
    this.lineDetailName = lineDetailName;
  }

  public String getLineDetailName() {
    return lineDetailName;
  }

  public void setLineDetailCategory(String lineDetailCategory) {
    this.lineDetailCategory = lineDetailCategory;
  }

  public String getLineDetailCategory() {
    return lineDetailCategory;
  }

  public void setLineDetailSize(String lineDetailSize) {
    this.lineDetailSize = lineDetailSize;
  }

  public String getLineDetailSize() {
    return lineDetailSize;
  }

  public void setLineDetailUnit(String lineDetailUnit) {
    this.lineDetailUnit = lineDetailUnit;
  }

  public String getLineDetailUnit() {
    return lineDetailUnit;
  }

  public void setOrderQty(String orderQty) {
    this.orderQty = orderQty;
  }

  public String getOrderQty() {
    return orderQty;
  }

  public void setCostBasis(String costBasis) {
    this.costBasis = costBasis;
  }

  public String getCostBasis() {
    return costBasis;
  }

  public void setThePrice(String thePrice) {
    this.thePrice = thePrice;
  }

  public String getThePrice() {
    return thePrice;
  }

  public void setDiscountRate(String discountRate) {
    this.discountRate = discountRate;
  }

  public String getDiscountRate() {
    return discountRate;
  }

  public void setDiscountAmount(String discountAmount) {
    this.discountAmount = discountAmount;
  }

  public String getDiscountAmount() {
    return discountAmount;
  }

  public void setExtendPrice(String extendPrice) {
    this.extendPrice = extendPrice;
  }

  public String getExtendPrice() {
    return extendPrice;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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

