/*
 * ServiceDictionaryForm.java
 *
 * Created on June 20, 2006, 07:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.servicedictionary;

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
import java.util.ArrayList;

public class ServiceDictionaryForm extends MasterForm {

  private String key;
  private String serviceName;
  private String serviceCategory;
  private String serviceNum;
  private String serviceDescription;
  private String profileNum;
  private String other;
  private ArrayList priceTypeForm;
  private String priceTypeKey;
  private String billableId;
  private String releaseId;
  private ArrayList customerTypeForm;
  private String customerTypeKey;
  private String durationHours;
  private String durationMinutes;
  private String boardingDays;
  private String inOfficeId;
  private String followUpDurationDays;
  private String preInstructions;
  private String postInstructions;
  private String laborCost;
  private String serviceCost;
  private String fee1Cost;
  private String fee2Cost;
  private String markUp1Rate;
  private String markUp2Rate;
  private String permissionStatus;
  private String attachmentCount;
  private String vendorKey;
  private String vendorName;
  private String vendorSpecificId;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;
  private String serviceNameFilter;
  private String serviceNumFilter;
  private String categoryNameFilter;
  private String currentPage;

  public ServiceDictionaryForm() {
    addRequiredFields(new String[] { "serviceName", "serviceCategory",
                                     "serviceNum", "profileNum", "vendorKey",
                                     "durationHours", "durationMinutes",
                                     "laborCost", "serviceCost",
                                     "fee1Cost", "fee2Cost",
                                     "markUp1Rate", "markUp2Rate" });
    addRange("durationHours", new Byte("0"), new Byte("8"));
    addRange("durationMinutes", new Byte("0"), new Byte("59"));
    addRange("laborCost", new BigDecimal("0.01"), new BigDecimal("999.99"));
    addRange("serviceCost", new BigDecimal("0.01"), new BigDecimal("999.99"));
    addRange("fee1Cost", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("fee2Cost", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("markUp1Rate", new BigDecimal("0"), new BigDecimal("999.99"));
    addRange("markUp2Rate", new BigDecimal("0"), new BigDecimal("999.99"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceCategory(String serviceCategory) {
    this.serviceCategory = serviceCategory;
  }

  public String getServiceCategory() {
    return serviceCategory;
  }

  public void setServiceNum(String serviceNum) {
    this.serviceNum = serviceNum;
  }

  public String getServiceNum() {
    return serviceNum;
  }
    
  public void setServiceDescription(String serviceDescription) {
    this.serviceDescription = serviceDescription;
  }

  public String getServiceDescription() {
    return serviceDescription;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }  

  public void setOther(String other) {
    this.other = other;
  }

  public String getOther() {
    return other;
  }

  public void setBillableId(String billableId) {
    this.billableId = billableId;
  }

  public String getBillableId() {
    return billableId;
  }

  public void setPriceTypeForm(ArrayList priceTypeList){
  this.priceTypeForm=priceTypeList;
  }

  public ArrayList getPriceTypeForm(){
  return priceTypeForm;
  }

  public void setPriceTypeKey(String priceTypeKey) {
    this.priceTypeKey = priceTypeKey;
  }

  public String getPriceTypeKey() {
    return priceTypeKey;
  }
  
  public void setReleaseId(String releaseId) {
    this.releaseId = releaseId;
  }

  public String getReleaseId() {
    return releaseId;
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

  public void setDurationHours(String durationHours) {
    this.durationHours = durationHours;
  }

  public String getDurationHours() {
    return durationHours;
  }

  public void setDurationMinutes(String durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public String getDurationMinutes() {
    return durationMinutes;
  }

  public void setLaborCost(String laborCost) {
    this.laborCost = laborCost;
  }

  public String getLaborCost() {
    return laborCost;
  }

  public void setServiceCost(String serviceCost) {
    this.serviceCost = serviceCost;
  }

  public String getServiceCost() {
    return serviceCost;
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

  public void setServiceNameFilter(String serviceNameFilter) {
    this.serviceNameFilter = serviceNameFilter;
  }

  public String getServiceNameFilter() {
    return serviceNameFilter;
  }

  public void setServiceNumFilter(String serviceNumFilter) {
    this.serviceNumFilter = serviceNumFilter;
  }

  public String getServiceNumFilter() {
    return serviceNumFilter;
  }

  public void setCategoryNameFilter(String categoryNameFilter) {
    this.categoryNameFilter = categoryNameFilter;
  }

  public String getCategoryNameFilter() {
    return categoryNameFilter;
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

