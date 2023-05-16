/*
 * VendorResultListForm.java
 *
 * Created on April 29, 2010, 10:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 *
 * 02/19/2007 - Introduce the PhoneNumberFormatter with setFormatterType()
 */

package com.wolmerica.vendorresult;

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

public class VendorResultListForm extends MasterForm {

  private String key;
  private String purchaseOrderKey;
  private String purchaseOrderNum;
  private String vendorKey;
  private String vendorName;
  private String customerKey;
  private String customerName;
  private String customerFirstName;
  private String customerLastName;
  private String attributeToEntityKey;
  private String attributeToEntityName;
  private String attributeToKey;
  private String attributeToName;
  private String speciesKey;
  private String speciesName;
  private String breedKey;
  private String breedName;
  private String petAge;
  private String petSexId;
  private String neuteredId;
  private String sourceKey;
  private String status;
  private String importFilename;
  private String orderStatus;
  private String siteName;
  private String receiveDate;
  private String receiveAssessionId;
  private String resultDate;
  private String resultAssessionId;
  private String profileNum;
  private String unitStatus;
  private String unitCode;
  private String unitName;
  private String abnormalStatus;
  private String testStatus;
  private String testCode;
  private String testName;
  private String testValue;
  private String testUnits;
  private String testRange;
  private String testComment;
  private String errorMessage;
  private String permissionStatus;
  private String attachmentCount;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setPurchaseOrderKey(String purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public String getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setPurchaseOrderNum(String purchaseOrderNum) {
    this.purchaseOrderNum = purchaseOrderNum;
  }

  public String getPurchaseOrderNum() {
    return purchaseOrderNum;
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

  public void setCustomerKey(String customerKey) {
    this.customerKey = customerKey;
  }

  public String getCustomerKey() {
    return customerKey;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerFirstName(String customerFirstName) {
    this.customerFirstName = customerFirstName;
  }

  public String getCustomerFirstName() {
    return customerFirstName;
  }

  public void setCustomerLastName(String customerLastName) {
    this.customerLastName = customerLastName;
  }

  public String getCustomerLastName() {
    return customerLastName;
  }

  public void setAttributeToEntityKey(String attributeToEntityKey) {
    this.attributeToEntityKey = attributeToEntityKey;
  }

  public String getAttributeToEntityKey() {
    return attributeToEntityKey;
  }

  public void setAttributeToEntityName(String attributeToEntityName) {
    this.attributeToEntityName = attributeToEntityName;
  }

  public String getAttributeToEntityName() {
    return attributeToEntityName;
  }

  public void setAttributeToKey(String attributeToKey) {
    this.attributeToKey = attributeToKey;
  }

  public String getAttributeToKey() {
    return attributeToKey;
  }

  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
  }

  public void setSpeciesKey(String speciesKey) {
    this.speciesKey = speciesKey;
  }

  public String getSpeciesKey() {
    return speciesKey;
  }

  public void setSpeciesName(String speciesName) {
    this.speciesName = speciesName;
  }

  public String getSpeciesName() {
    return speciesName;
  }

  public void setBreedKey(String breedKey) {
    this.breedKey = breedKey;
  }

  public String getBreedKey() {
    return breedKey;
  }

  public void setBreedName(String breedName) {
    this.breedName = breedName;
  }

  public String getBreedName() {
    return breedName;
  }

  public void setPetAge(String petAge) {
    this.petAge = petAge;
  }

  public String getPetAge() {
    return petAge;
  }

  public void setPetSexId(String petSexId) {
    this.petSexId = petSexId;
  }

  public String getPetSexId() {
    return petSexId;
  }

  public void setNeuteredId(String neuteredId) {
    this.neuteredId = neuteredId;
  }

  public String getNeuteredId() {
    return neuteredId;
  }

  public void setSourceKey(String sourceKey) {
    this.sourceKey = sourceKey;
  }

  public String getSourceKey() {
    return sourceKey;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setImportFilename(String importFilename) {
    this.importFilename = importFilename;
  }

  public String getImportFilename() {
    return importFilename;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  public String getSiteName() {
    return siteName;
  }

  public void setReceiveDate(String receiveDate) {
    this.receiveDate = receiveDate;
  }

  public String getReceiveDate() {
    return receiveDate;
  }

  public void setReceiveAssessionId(String receiveAssessionId) {
    this.receiveAssessionId = receiveAssessionId;
  }

  public String getReceiveAssessionId() {
    return receiveAssessionId;
  }

  public void setResultDate(String resultDate) {
    this.resultDate = resultDate;
  }

  public String getResultDate() {
    return resultDate;
  }

  public void setResultAssessionId(String resultAssessionId) {
    this.resultAssessionId = resultAssessionId;
  }

  public String getResultAssessionId() {
    return resultAssessionId;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }

  public void setUnitStatus(String unitStatus) {
    this.unitStatus = unitStatus;
  }

  public String getUnitStatus() {
    return unitStatus;
  }

  public void setUnitCode(String unitCode) {
    this.unitCode = unitCode;
  }

  public String getUnitCode() {
    return unitCode;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public String getUnitName() {
    return unitName;
  }

  public void setAbnormalStatus(String abnormalStatus) {
    this.abnormalStatus = abnormalStatus;
  }

  public String getAbnormalStatus() {
    return abnormalStatus;
  }

  public void setTestStatus(String testStatus) {
    this.testStatus = testStatus;
  }

  public String getTestStatus() {
    return testStatus;
  }

  public void setTestCode(String testCode) {
    this.testCode = testCode;
  }

  public String getTestCode() {
    return testCode;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestValue(String testValue) {
    this.testValue = testValue;
  }

  public String getTestValue() {
    return testValue;
  }

  public void setTestUnits(String testUnits) {
    this.testUnits = testUnits;
  }

  public String getTestUnits() {
    return testUnits;
  }

  public void setTestRange(String testRange) {
    this.testRange = testRange;
  }

  public String getTestRange() {
    return testRange;
  }

  public void setTestComment(String testComment) {
    this.testComment = testComment;
  }

  public String getTestComment() {
    return testComment;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
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

