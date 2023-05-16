/*
 * ItemPurchaseForm.java
 *
 * Created on May 20, 2006, 12:20 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/27/2005 Implement tools.formatter library.
 */

package com.wolmerica.itempurchasereport;

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

public class ItemPurchaseForm extends MasterForm {

  private String invoiceKey;
  private String invoiceDate;
  private String invoiceYear;
  private String invoiceMonthName;
  private String invoiceNumber;
  private String brandName;
  private String genericName;
  private String size;
  private String sizeUnit;
  private String orderQty;
  private String theCost;
  private String theDiscount;
  private String useTaxCost;
  private String handlingCost;
  private String extendCost;

  public void setInvoiceKey(String invoiceKey) {
    this.invoiceKey = invoiceKey;
  }

  public String getInvoiceKey() {
    return invoiceKey;
  }

  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceYear(String invoiceYear) {
    this.invoiceYear = invoiceYear;
  }

  public String getInvoiceYear() {
    return invoiceYear;
  }

  public void setInvoiceMonthName(String invoiceMonthName) {
    this.invoiceMonthName = invoiceMonthName;
  }

  public String getInvoiceMonthName() {
    return invoiceMonthName;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setGenericName(String genericName) {
    this.genericName = genericName;
  }

  public String getGenericName() {
    return genericName;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getSize() {
    return size;
  }

  public void setSizeUnit(String sizeUnit) {
    this.sizeUnit = sizeUnit;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

 public void setOrderQty(String orderQty) {
    this.orderQty = orderQty;
  }

  public String getOrderQty() {
    return orderQty;
  }

  public void setTheCost(String theCost) {
    this.theCost = theCost;
  }

  public String getTheCost() {
    return theCost;
  }

  public void setTheDiscount(String theDiscount) {
    this.theDiscount = theDiscount;
  }

  public String getTheDiscount() {
    return theDiscount;
  }

  public void setUseTaxCost(String useTaxCost) {
    this.useTaxCost = useTaxCost;
  }

  public String getUseTaxCost() {
    return useTaxCost;
  }

  public void setHandlingCost(String handlingCost) {
    this.handlingCost = handlingCost;
  }

  public String getHandlingCost() {
    return handlingCost;
  }

  public void setExtendCost(String extendCost) {
    this.extendCost = extendCost;
  }

  public String getExtendCost() {
    return extendCost;
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

