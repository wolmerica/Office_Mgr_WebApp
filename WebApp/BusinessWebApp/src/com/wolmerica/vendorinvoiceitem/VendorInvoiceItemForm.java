/*
 * VendorInvoiceItemForm.java
 *
 * Created on October 23, 2005, 11:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoiceitem;

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

public class VendorInvoiceItemForm extends MasterForm
{
  private String key;
  private String vendorInvoiceKey;
  private String purchaseOrderItemKey;
  private String itemDictionaryKey;
  private String brandName;
  private String size;
  private String sizeUnit;
  private String orderQty;
  private String receiveQty;
  private String backOrderQty;
  private String firstCost;
  private String variantAmount;  
  private String useTaxCost;
  private String handlingCost;
  private String unitCost;
  private String extendCost;
  private String enableSalesTaxId;
  private String salesTaxId;
  private String enableCarryFactorId;
  private String carryFactor;
  private String expirationDate;
  private String itemAction;
  private String noteLine1;
  private String licenseKeyId;
  private String licenseKeyCount;

  public VendorInvoiceItemForm() {
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

  public void setPurchaseOrderItemKey(String purchaseOrderItemKey) {
    this.purchaseOrderItemKey = purchaseOrderItemKey;
  }

  public String getPurchaseOrderItemKey() {
    return purchaseOrderItemKey;
  }

  public void setItemDictionaryKey(String itemDictionaryKey) {
    this.itemDictionaryKey = itemDictionaryKey;
  }

  public String getItemDictionaryKey() {
    return itemDictionaryKey;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
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

  public void setEnableSalesTaxId(String enableSalesTaxId) {
    this.enableSalesTaxId = enableSalesTaxId;
  }

  public String getEnableSalesTaxId() {
    return enableSalesTaxId;
  }

  public void setSalesTaxId(String salesTaxId) {
    this.salesTaxId = salesTaxId;
  }

  public String getSalesTaxId() {
    return salesTaxId;
  }

  public void setEnableCarryFactorId(String enableCarryFactorId) {
    this.enableCarryFactorId = enableCarryFactorId;
  }

  public String getEnableCarryFactorId() {
    return enableCarryFactorId;
  }

  public void setCarryFactor(String carryFactor) {
    this.carryFactor = carryFactor;
  }

  public String getCarryFactor() {
    return carryFactor;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public void setItemAction(String itemAction) {
    this.itemAction = itemAction;
  }

  public String getItemAction() {
    return itemAction;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setLicenseKeyId(String licenseKeyId) {
    this.licenseKeyId = licenseKeyId;
  }

  public String getLicenseKeyId() {
    return licenseKeyId;
  }

  public void setLicenseKeyCount(String licenseKeyCount) {
    this.licenseKeyCount = licenseKeyCount;
  }

  public String getLicenseKeyCount() {
    return licenseKeyCount;
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