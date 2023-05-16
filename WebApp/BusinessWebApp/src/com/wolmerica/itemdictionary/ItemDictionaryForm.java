/*
 * ItemDictionary.java
 *
 * Created on August 29, 2005, 09:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.itemdictionary;

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
import java.util.ArrayList;
import java.math.BigDecimal;

public class ItemDictionaryForm extends MasterForm {

  private String key;
  private String brandName;
  private String genericName;
  private String itemNum;
  private String profileNum;
  private String manufacturer;
  private String vendorKey;
  private String vendorName;
  private String vendorSpecificId;
  private ArrayList customerTypeForm;
  private String customerTypeKey;
  private String licenseKeyId;
  private String reportId;
  private String itemName;
  private String size;
  private String sizeUnit;
  private String dose;
  private String doseUnit;
  private String other;
  private String itemMemo;
  private String carryFactor;
  private String percentUse;
  private String firstCost;
  private String prevFirstCost;
  private String unitCost;
  private String prevUnitCost;
  private String muAdditional;
  private String muVendor;
  private String labelCost;
  private String qtyOnHand;
  private String orderThreshold;
  private String orderedQty;
  private String forecastQty;
  private String rebateCount;
  private String rebateInstanceCount;
  private String permissionStatus;
  private String attachmentCount;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;
  private String brandNameFilter;
  private String itemNumFilter;
  private String genericNameFilter;
  private String currentPage;

  public ItemDictionaryForm() {
    addRequiredFields(new String[] { "brandName", "genericName", 
                                     "itemNum", "profileNum",
                                     "manufacturer", "vendorKey",
                                     "customerTypeKey", "size", "sizeUnit",
                                     "carryFactor", "percentUse",
                                     "firstCost", "unitCost" });
    addRange("size",
              new BigDecimal("0.01"),
              new BigDecimal("9999.99"));
    addRange("dose",
              new BigDecimal("0.00"),
              new BigDecimal("9999.99"));
    addRange("carryFactor",
              new BigDecimal("0.00"),
              new BigDecimal("999.99"));
    addRange("percentUse",
              new BigDecimal("0.00"),
              new BigDecimal("999.99"));
    addRange("firstCost",
              new BigDecimal("0.00"),
              new BigDecimal("999999.99"));
    addRange("prevFirstCost",
              new BigDecimal("0.00"),
              new BigDecimal("999999.99"));
    addRange("unitCost",
              new BigDecimal("0.00"),
              new BigDecimal("999999.99"));
    addRange("prevUnitCost",
              new BigDecimal("0.00"),
              new BigDecimal("999999.99"));
    addRange("muAdditional",
              new BigDecimal("0.00"),
              new BigDecimal("999.99"));
    addRange("muVendor",
              new BigDecimal("0.00"),
              new BigDecimal("999999.99"));
    addRange("labelCost",
              new BigDecimal("0.00"),
              new BigDecimal("999.99"));
    addRange("orderThreshold",
              new BigDecimal("0.00"),
              new BigDecimal("9999.99"));
    addRange("licenseKeyId", new Byte("0"), new Byte("1"));
    addRange("reportId", new Byte("0"), new Byte("1"));

  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
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

  public void setItemNum(String itemNum) {
    this.itemNum = itemNum;
  }

  public String getItemNum() {
    return itemNum;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setVendorKey(String vendorKey) {
    this.vendorKey = vendorKey;
  }

  public String getVendorKey() {
    return vendorKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorSpecificId(String vendorSpecificId) {
    this.vendorSpecificId = vendorSpecificId;
  }

  public String getVendorSpecificId() {
    return vendorSpecificId;
  }

  public void setCustomerTypeForm(ArrayList customerTypeList){
  this.customerTypeForm=customerTypeList;
  }

  public ArrayList getCustomerTypeForm(){
  return customerTypeForm;
  }

  public void setCustomerTypeKey(String customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public String getCustomerTypeKey() {
    return customerTypeKey;
  }

  public void setLicenseKeyId(String licenseKeyId) {
    this.licenseKeyId = licenseKeyId;
  }

  public String getLicenseKeyId() {
    return licenseKeyId;
  }

  public void setReportId(String reportId) {
    this.reportId = reportId;
  }

  public String getReportId() {
    return reportId;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public String getItemName() {
    return itemName;
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

  public void setDose(String dose) {
    this.dose = dose;
  }

  public String getDose() {
    return dose;
  }

  public void setDoseUnit(String doseUnit) {
    this.doseUnit = doseUnit;
  }

  public String getDoseUnit() {
    return doseUnit;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public String getOther() {
    return other;
  }

  public void setItemMemo(String itemMemo) {
    this.itemMemo = itemMemo;
  }

  public String getItemMemo() {
    return itemMemo;
  }

  public void setCarryFactor(String carryFactor) {
    this.carryFactor = carryFactor;
  }

  public String getCarryFactor(){
    return carryFactor;
  }

  public void setPercentUse(String percentUse) {
    this.percentUse = percentUse;
  }

  public String getPercentUse(){
    return percentUse;
  }

  public void setFirstCost(String firstCost) {
    this.firstCost = firstCost;
  }

  public String getFirstCost() {
    return firstCost;
  }

  public void setPrevFirstCost(String prevFirstCost) {
    this.prevFirstCost = prevFirstCost;
  }

  public String getPrevFirstCost() {
    return prevFirstCost;
  }

  public void setUnitCost(String unitCost) {
    this.unitCost = unitCost;
  }

  public String getUnitCost() {
    return unitCost;
  }

  public void setPrevUnitCost(String prevUnitCost) {
    this.prevUnitCost = prevUnitCost;
  }

  public String getPrevUnitCost() {
    return prevUnitCost;
  }

  public void setMuAdditional(String muAdditional) {
    this.muAdditional = muAdditional;
  }

  public String getMuAdditional() {
    return muAdditional;
  }

  public void setMuVendor(String muVendor) {
    this.muVendor = muVendor;
  }

  public String getMuVendor() {
    return muVendor;
  }

  public void setLabelCost(String labelCost) {
    this.labelCost = labelCost;
  }

  public String getLabelCost() {
    return labelCost;
  }

  public void setQtyOnHand(String qtyOnHand) {
    this.qtyOnHand = qtyOnHand;
  }

  public String getQtyOnHand() {
    return qtyOnHand;
  }

  public void setOrderedQty(String orderedQty) {
    this.orderedQty = orderedQty;
  }

  public String getOrderedQty() {
    return orderedQty;
  }

  public void setForecastQty(String forecastQty) {
    this.forecastQty = forecastQty;
  }

  public String getForecastQty() {
    return forecastQty;
  }

  public void setOrderThreshold(String orderthreshold) {
    this.orderThreshold = orderthreshold;
  }

  public String getOrderThreshold() {
    return orderThreshold;
  }

  public void setRebateCount(String rebateCount) {
    this.rebateCount = rebateCount;
  }

  public String getRebateCount() {
    return rebateCount;
  }

  public void setRebateInstanceCount(String rebateInstanceCount) {
    this.rebateInstanceCount = rebateInstanceCount;
  }

  public String getRebateInstanceCount() {
    return rebateInstanceCount;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }

  public void setAttachmentCount(String attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public String getAttachmentCount() {
    return attachmentCount;
  }

  public void setBrandNameFilter(String brandNameFilter) {
    this.brandNameFilter = brandNameFilter;
  }

  public String getBrandNameFilter() {
    return brandNameFilter;
  }

  public void setItemNumFilter(String itemNumFilter) {
    this.itemNumFilter = itemNumFilter;
  }

  public String getItemNumFilter() {
    return itemNumFilter;
  }

  public void setGenericNameFilter(String genericNameFilter) {
    this.genericNameFilter = genericNameFilter;
  }

  public String getGenericNameFilter() {
    return genericNameFilter;
  }

  public void setCurrentPage(String currentPage ) {
    this.currentPage = currentPage;
  }

  public String getCurrentPage() {
    return currentPage;
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

