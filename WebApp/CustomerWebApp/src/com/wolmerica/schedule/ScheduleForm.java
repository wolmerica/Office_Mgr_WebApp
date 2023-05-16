/*
 * ScheduleForm.java
 *
 * Created on September 7, 2006, 10:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.schedule;

/**
 *
 * @author Richard
 */
import com.wolmerica.customer.CustomerActionMapping;
import com.wolmerica.tools.formatter.MasterForm;
import com.wolmerica.tools.formatter.PhoneNumberFormatter;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;

public class ScheduleForm extends MasterForm {

  private String key;
  private String firstDayId;
  private String eventTypeKey;
  private String locationId;
  private String subject;
  private String customerKey;
  private String clientName;
  private String customerPhone;
  private String reminderPrefKey;
  private String addressId;
  private String address;
  private String city;
  private String state;
  private String customerTypeKey;
  private String attributeToEntity;
  private String sourceTypeKey;
  private String sourceKey;
  private String attributeToName;
  private String attributeToDate;
  private String startStamp;
  private String startDate;
  private String startHour;
  private String startMinute;
  private String endStamp;
  private String endDate;
  private String endHour;
  private String endMinute;
  private String noteLine1;
  private String customerInvoiceKey;
  private String statusKey;
  private String rewindId;
  private String activeId;
  private String releaseId;
  private String thirdPartyId;
  private String thirdPartyOrderId;
  private String category1Id;
  private String category2Id;
  private String category3Id;
  private String category4Id;
  private String category5Id;
  private String category6Id;
  private String category1Key;
  private String category2Key;
  private String category3Key;
  private String category4Key;
  private String category5Key;
  private String category6Key;
  private String examKey;
  private String workOrderItemTotal;
  private String workOrderServiceTotal;
  private String permissionStatus;
  private String allowDeleteId;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public ScheduleForm() {
    addRequiredFields(new String[] { "eventTypeKey", "locationId",
                              "statusKey", "subject", "customerKey",
                              "startDate", "startHour", "startMinute" });
    setFormatterType("customerPhone", PhoneNumberFormatter.class);
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setFirstDayId(String firstDayId) {
    this.firstDayId = firstDayId;
  }

  public String getFirstDayId() {
    return firstDayId;
  }

  public void setEventTypeKey(String eventTypeKey) {
    this.eventTypeKey = eventTypeKey;
  }

  public String getEventTypeKey() {
    return eventTypeKey;
  }

  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  public String getLocationId() {
    return locationId;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return subject;
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

  public void setCustomerPhone(String customerPhone) {
    this.customerPhone = customerPhone;
  }

  public String getCustomerPhone() {
    return customerPhone;
  }

  public void setReminderPrefKey(String reminderPrefKey) {
    this.reminderPrefKey = reminderPrefKey;
  }

  public String getReminderPrefKey() {
    return reminderPrefKey;
  }

  public void setAddressId(String addressId) {
    this.addressId = addressId;
  }

  public String getAddressId() {
    return addressId;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCity() {
    return city;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }

  public void setCustomerTypeKey(String customerTypeKey) {
    this.customerTypeKey = customerTypeKey;
  }

  public String getCustomerTypeKey() {
    return customerTypeKey;
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

  public void setAttributeToDate(String attributeToDate) {
    this.attributeToDate = attributeToDate;
  }

  public String getAttributeToDate() {
    return attributeToDate;
  }

  public void setStartStamp(String startStamp) {
    this.startStamp = startStamp;
  }

  public String getStartStamp() {
    return startStamp;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartHour(String startHour) {
    this.startHour = startHour;
  }

  public String getStartHour() {
    return startHour;
  }

  public void setStartMinute(String startMinute) {
    this.startMinute = startMinute;
  }

  public String getStartMinute() {
    return startMinute;
  }

  public void setEndStamp(String endStamp) {
    this.endStamp = endStamp;
  }

  public String getEndStamp() {
    return endStamp;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndHour(String endHour) {
    this.endHour = endHour;
  }

  public String getEndHour() {
    return endHour;
  }

  public void setEndMinute(String endMinute) {
    this.endMinute = endMinute;
  }

  public String getEndMinute() {
    return endMinute;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setCustomerInvoiceKey(String customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public String getCustomerInvoiceKey() {
    return customerInvoiceKey;
  }

  public void setStatusKey(String statusKey) {
    this.statusKey = statusKey;
  }

  public String getStatusKey() {
    return statusKey;
  }

  public void setRewindId(String rewindId) {
    this.rewindId = rewindId;
  }

  public String getRewindId() {
    return rewindId;
  }

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
  }

  public void setReleaseId(String releaseId) {
    this.releaseId = releaseId;
  }

  public String getReleaseId() {
    return releaseId;
  }

  public void setThirdPartyId(String thirdPartyId) {
    this.thirdPartyId = thirdPartyId;
  }

  public String getThirdPartyId() {
    return thirdPartyId;
  }

  public void setThirdPartyOrderId(String thirdPartyOrderId) {
    this.thirdPartyOrderId = thirdPartyOrderId;
  }

  public String getThirdPartyOrderId() {
    return thirdPartyOrderId;
  }

  public void setCategory1Id(String category1Id) {
    this.category1Id = category1Id;
  }

  public String getCategory1Id() {
    return category1Id;
  }

  public void setCategory2Id(String category2Id) {
    this.category2Id = category2Id;
  }

  public String getCategory2Id() {
    return category2Id;
  }

  public void setCategory3Id(String category3Id) {
    this.category3Id = category3Id;
  }

  public String getCategory3Id() {
    return category3Id;
  }

  public void setCategory4Id(String category4Id) {
    this.category4Id = category4Id;
  }

  public String getCategory4Id() {
    return category4Id;
  }

  public void setCategory5Id(String category5Id) {
    this.category5Id = category5Id;
  }

  public String getCategory5Id() {
    return category5Id;
  }

  public void setCategory6Id(String category6Id) {
    this.category6Id = category6Id;
  }

  public String getCategory6Id() {
    return category6Id;
  }

  public void setCategory1Key(String category1Key) {
    this.category1Key = category1Key;
  }

  public String getCategory1Key() {
    return category1Key;
  }

  public void setCategory2Key(String category2Key) {
    this.category2Key = category2Key;
  }

  public String getCategory2Key() {
    return category2Key;
  }

  public void setCategory3Key(String category3Key) {
    this.category3Key = category3Key;
  }

  public String getCategory3Key() {
    return category3Key;
  }

  public void setCategory4Key(String category4Key) {
    this.category4Key = category4Key;
  }

  public String getCategory4Key() {
    return category4Key;
  }

  public void setCategory5Key(String category5Key) {
    this.category5Key = category5Key;
  }

  public String getCategory5Key() {
    return category5Key;
  }

  public void setCategory6Key(String category6Key) {
    this.category6Key = category6Key;
  }

  public String getCategory6Key() {
    return category6Key;
  }

  public void setExamKey(String examKey) {
    this.examKey = examKey;
  }

  public String getExamKey() {
    return examKey;
  }

  public void setWorkOrderItemTotal(String workOrderItemTotal) {
    this.workOrderItemTotal = workOrderItemTotal;
  }

  public String getWorkOrderItemTotal() {
    return workOrderItemTotal;
  }

  public void setWorkOrderServiceTotal(String workOrderServiceTotal) {
    this.workOrderServiceTotal = workOrderServiceTotal;
  }

  public String getWorkOrderServiceTotal() {
    return workOrderServiceTotal;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }

  public void setAllowDeleteId(String allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public String getAllowDeleteId() {
    return allowDeleteId;
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

    CustomerActionMapping employeesMapping = (CustomerActionMapping)mapping;

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