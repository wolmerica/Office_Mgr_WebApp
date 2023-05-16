/*
 * WorkOrderForm.java
 *
 * Created on October 13, 2007, 11:30 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.workorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.math.BigDecimal;

public class WorkOrderForm extends MasterForm
{
  private String key;
  private String scheduleKey;
  private String locationId;
  private String subject;
  private String resourceName;
  private String clientName;
  private String attributeToEntity;
  private String attributeToName;
  private String releaseId;
  private String thirdPartyId;
  private String vendorKey;
  private String sourceTypeKey;
  private String sourceKey;
  private String sourceName;
  private String sourceNum;
  private String categoryName;
  private String size;
  private String sizeUnit;
  private String priceTypeKey;
  private String priceTypeName;
  private String estimatedPrice;
  private String extendedPrice;
  private String availableQty;
  private String orderQty;
  private String startStamp;
  private String startDate;
  private String startHour;
  private String startMinute;
  private String endStamp;
  private String endDate;
  private String endHour;
  private String endMinute;
  private String activeId;
  private String allowDeleteId;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public WorkOrderForm() {
    addRequiredFields(new String[] { "orderQty" } );
    addRange("orderQty", new Short("1"), new Short("1000"));
    addRange("estimatedPrice", new BigDecimal("0.00"), new BigDecimal("99999"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setScheduleKey(String scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public String getScheduleKey() {
    return scheduleKey;
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

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setAttributeToEntity(String attributeToEntity) {
    this.attributeToEntity = attributeToEntity;
  }

  public String getAttributeToEntity() {
    return attributeToEntity;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
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
  
  public void setVendorKey(String vendorKey) {
    this.vendorKey = vendorKey;
  }

  public String getVendorKey() {
    return vendorKey;
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

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceNum(String sourceNum) {
    this.sourceNum = sourceNum;
  }

  public String getSourceNum() {
    return sourceNum;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getCategoryName() {
    return categoryName;
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

  public void setEstimatedPrice(String estimatedPrice) {
    this.estimatedPrice = estimatedPrice;
  }

  public String getEstimatedPrice() {
    return estimatedPrice;
  }

  public void setExtendedPrice(String extendedPrice) {
    this.extendedPrice = extendedPrice;
  }

  public String getExtendedPrice() {
    return extendedPrice;
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

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
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