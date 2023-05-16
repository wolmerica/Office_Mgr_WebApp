/*
 * VendorInvoiceReportDetailForm.java
 *
 * Created on February 21, 2007, 10:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoicereportdetail;

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

public class VendorInvoiceReportDetailForm extends MasterForm {

  private String vendorInvoiceDetailKey;
  private String vendorInvoiceKey;
  private String invoiceDate;
  private String lineDetailNumber;
  private String lineDetailName;
  private String lineDetailCategory;
  private String lineDetailSize;
  private String lineDetailUnit;
  private String orderQty;
  private String thePrice;
  private String theDiscount;
  private String extendPrice;
  private String noteLine1;

  public void setVendorInvoiceDetailKey(String vendorInvoiceDetailKey) {
    this.vendorInvoiceDetailKey = vendorInvoiceDetailKey;
  }

  public String getVendorInvoiceDetailKey() {
    return vendorInvoiceDetailKey;
  }

  public void setVendorInvoiceKey(String vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public String getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getInvoiceDate() {
    return invoiceDate;
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

  public void setThePrice(String thePrice) {
    this.thePrice = thePrice;
  }

  public String getThePrice() {
    return thePrice;
  }

  public void setTheDiscount(String theDiscount) {
    this.theDiscount = theDiscount;
  }

  public String getTheDiscount() {
    return theDiscount;
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

