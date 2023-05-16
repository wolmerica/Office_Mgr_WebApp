/*
 * CustomerInvoiceServiceForm.java
 *
 * Created on August 16, 2006, 12:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoiceservice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
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

public class CustomerInvoiceServiceForm extends MasterForm
{
  private String key;
  private String customerInvoiceKey;
  private String serviceDictionaryKey;
  private String priceTypeKey;
  private String priceTypeName;
  private String serviceNum;
  private String serviceName;
  private String availableQty;
  private String orderQty;
  private String remainingQty;
  private String thePrice;
  private String extendPrice;
  private String discountRate;
  private String discountAmount;
  private String costBasis;
  private String enableServiceTaxId;
  private String serviceTaxId;
  private String genesisKey;
  private String masterKey;  
  private String noteLine1;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;


  public CustomerInvoiceServiceForm() {
    addRequiredFields(new String[] { "orderQty", "thePrice" } );
    addRange("orderQty", new Short("0"), new Short("1000"));
    addRange("thePrice", new BigDecimal("0.00"), new BigDecimal("99999"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setCustomerInvoiceKey(String customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public String getCustomerInvoiceKey() {
    return customerInvoiceKey;
  }

  public void setServiceDictionaryKey(String serviceDictionaryKey) {
    this.serviceDictionaryKey = serviceDictionaryKey;
  }

  public String getServiceDictionaryKey() {
    return serviceDictionaryKey;
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

  public void setServiceNum(String serviceNum) {
    this.serviceNum = serviceNum;
  }

  public String getServiceNum() {
    return serviceNum;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setAvailableQty(String availableQty) {
    this.availableQty = availableQty;
  }

  public String getAvailableQty() {
    return availableQty;
  }

  public void setOrderQty(String orderQty) {
    this.orderQty = orderQty;
  }

  public String getOrderQty() {
    return orderQty;
  }

  public void setRemainingQty(String remainingQty) {
    this.remainingQty = remainingQty;
  }

  public String getRemainingQty() {
    return remainingQty;
  }

  public void setThePrice(String thePrice) {
    this.thePrice = thePrice;
  }

  public String getThePrice() {
    return thePrice;
  }

  public void setExtendPrice(String extendPrice) {
    this.extendPrice = extendPrice;
  }

  public String getExtendPrice() {
    return extendPrice;
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

  public void setCostBasis(String costBasis) {
    this.costBasis = costBasis;
  }

  public String getCostBasis() {
    return costBasis;
  }

  public void setEnableServiceTaxId(String enableServiceTaxId) {
    this.enableServiceTaxId = enableServiceTaxId;
  }

  public String getEnableServiceTaxId() {
    return enableServiceTaxId;
  }

  public void setServiceTaxId(String serviceTaxId) {
    this.serviceTaxId = serviceTaxId;
  }

  public String getServiceTaxId() {
    return serviceTaxId;
  }
  
  public void setGenesisKey(String genesisKey) {
    this.genesisKey = genesisKey;
  }

  public String getGenesisKey() {
    return genesisKey;
  }

  public void setMasterKey(String masterKey) {
    this.masterKey = masterKey;
  }

  public String getMasterKey() {
    return masterKey;
  }  

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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

    return errors;
  }
}