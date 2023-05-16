/*
 * PurchaseOrderForm.java
 *
 * Created on September 5, 2005, 5:32 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/23/2005 Implement tools.formatter library.
 */

package com.wolmerica.purchaseorder;

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

public class PurchaseOrderForm extends MasterForm {

  private String key;
  private String purchaseOrderNumber;
  private String orderStatus;
  private String priorityKey;
  private String vendorKey;
  private String vendorName;
  private String vendorItemCount;
  private String vendorServiceCount;
  private String customerKey;
  private String clientName;
  private String submitOrderStamp;
  private String salesOrderNumber;
  private String noteLine1;
  private String itemTotal;
  private String itemQty;
  private String serviceTotal;
  private String serviceQty;
  private String orderTotal;
  private String balanceQty;
  private String balanceAmount;
  private String permissionStatus;
  private String attachmentCount;
  private String attributeToEntity;
  private String sourceTypeKey;
  private String sourceKey;
  private String attributeToName;  
  private String scheduleKey;
  private String logisticsCount;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public PurchaseOrderForm() {
    addRequiredFields(new String[] { "vendorKey", "customerKey" });
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public void setPriorityKey(String priorityKey) {
    this.priorityKey = priorityKey;
  }

  public String getPriorityKey() {
    return priorityKey;
  }

  public String getOrderStatus() {
    return orderStatus;
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

  public void setVendorItemCount(String vendorItemCount) {
    this.vendorItemCount = vendorItemCount;
  }

  public String getVendorItemCount() {
    return vendorItemCount;
  }  
  
  public void setVendorServiceCount(String vendorServiceCount) {
    this.vendorServiceCount = vendorServiceCount;
  }

  public String getVendorServiceCount() {
    return vendorServiceCount;
  }

  public void setCustomerKey(String customerKey) {
    this.customerKey = customerKey;
  }

  public String getCustomerKey() {
    return customerKey;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setSubmitOrderStamp(String submitOrderStamp) {
    this.submitOrderStamp = submitOrderStamp;
  }

  public String getSubmitOrderStamp() {
    return submitOrderStamp;
  }

  public void setSalesOrderNumber(String salesOrderNumber) {
    this.salesOrderNumber = salesOrderNumber;
  }

  public String getSalesOrderNumber() {
    return salesOrderNumber;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setItemTotal(String itemTotal) {
    this.itemTotal = itemTotal;
  }

  public String getItemTotal() {
    return itemTotal;
  }

  public void setItemQty(String itemQty) {
    this.itemQty = itemQty;
  }

  public String getItemQty() {
    return itemQty;
  }  
  
  public void setServiceTotal(String serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public String getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceQty(String serviceQty) {
    this.serviceQty = serviceQty;
  }

  public String getServiceQty() {
    return serviceQty;
  }
  
  public void setOrderTotal(String orderTotal) {
    this.orderTotal = orderTotal;
  }

  public String getOrderTotal() {
    return orderTotal;
  }
  
  public void setBalanceQty(String balanceQty) {
    this.balanceQty = balanceQty;
  }

  public String getBalanceQty() {
    return balanceQty;
  }

  public void setBalanceAmount(String balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public String getBalanceAmount() {
    return balanceAmount;
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

  public void setAttributeToEntity(String attributeToEntity) {
    this.attributeToEntity = attributeToEntity;
  }

  public String getAttributeToEntity() {
    return attributeToEntity;
  }

  public void setSourceTypeKey(String sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public String getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceKey(String sourceKey) {
    this.sourceKey = sourceKey;
  }

  public String getSourceKey() {
    return sourceKey;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
  }  

  public void setScheduleKey(String scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public String getScheduleKey() {
    return scheduleKey;
  }
    
  public void setLogisticsCount(String logisticsCount) {
    this.logisticsCount = logisticsCount;
  }

  public String getLogisticsCount() {
    return logisticsCount;
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

