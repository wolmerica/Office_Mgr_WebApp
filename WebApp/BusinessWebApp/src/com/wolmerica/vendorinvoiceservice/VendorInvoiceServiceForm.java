/*
 * VendorInvoiceServiceForm.java
 *
 * Created on November 08, 2008, 12:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoiceservice;

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

public class VendorInvoiceServiceForm extends MasterForm
{
  private String key;
  private String vendorInvoiceKey;
  private String purchaseOrderServiceKey;
  private String serviceDictionaryKey;
  private String serviceName;
  private String priceTypeKey;
  private String priceTypeName;
  private String orderQty;
  private String receiveQty;
  private String backOrderQty;
  private String firstCost;
  private String variantAmount;    
  private String serviceTaxCost;
  private String handlingCost;
  private String unitCost;
  private String extendCost;
  private String enableServiceTaxId;
  private String serviceTaxId;
  private String serviceAction;
  private String noteLine1;

  public VendorInvoiceServiceForm() {
    addRequiredFields(new String[] { "orderQty", "receiveQty", "backOrderQty", "firstCost" } );
    addRange("orderQty", new Short("0"), new Short("9999"));
    addRange("receiveQty", new Short("0"), new Short("9999"));
    addRange("backOrderQty", new Short("0"), new Short("9999"));
    addRange("firstCost", new BigDecimal("0.00"), new BigDecimal("99999"));
    addRange("variantAmount", new BigDecimal("0.00"), new BigDecimal("9999"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setVendorInvoiceKey(String vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public String getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setPurchaseOrderServiceKey(String purchaseOrderServiceKey) {
    this.purchaseOrderServiceKey = purchaseOrderServiceKey;
  }

  public String getPurchaseOrderServiceKey() {
    return purchaseOrderServiceKey;
  }

  public void setServiceDictionaryKey(String serviceDictionaryKey) {
    this.serviceDictionaryKey = serviceDictionaryKey;
  }

  public String getServiceDictionaryKey() {
    return serviceDictionaryKey;
  }

public void setServiceName(String serviceName) {
   this.serviceName = serviceName;
 }
   
 public String getServiceName() {
   return serviceName; 
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

 public void setOrderQty(String orderQty) {
    this.orderQty = orderQty;
  }

  public String getOrderQty() {
    return orderQty;
  }

  public void setReceiveQty(String receiveQty) {
    this.receiveQty = receiveQty;
  }

  public String getReceiveQty() {
    return receiveQty;
  }

  public void setBackOrderQty(String backOrderQty) {
    this.backOrderQty = backOrderQty;
  }

  public String getBackOrderQty() {
    return backOrderQty;
  }

  public void setFirstCost(String firstCost) {
    this.firstCost = firstCost;
  }

  public String getFirstCost() {
    return firstCost;
  }
  
  public void setVariantAmount(String variantAmount) {
    this.variantAmount = variantAmount;
  }

  public String getVariantAmount() {
    return variantAmount;
  }
  
  public void setServiceTaxCost(String serviceTaxCost) {
    this.serviceTaxCost = serviceTaxCost;
  }

  public String getServiceTaxCost() {
    return serviceTaxCost;
  }

  public void setHandlingCost(String handlingCost) {
    this.handlingCost = handlingCost;
  }

  public String getHandlingCost() {
    return handlingCost;
  }

  public void setUnitCost(String unitCost) {
    this.unitCost = unitCost;
  }

  public String getUnitCost() {
    return unitCost;
  }

  public void setExtendCost(String extendCost) {
    this.extendCost = extendCost;
  }

  public String getExtendCost() {
    return extendCost;
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

  public void setServiceAction(String serviceAction) {
    this.serviceAction = serviceAction;
  }

  public String getServiceAction() {
    return serviceAction;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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

    return errors;
  }
}