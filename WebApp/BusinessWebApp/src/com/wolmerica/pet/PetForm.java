/*
 * PetForm.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.pet;

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
import java.util.Date;

public class PetForm extends MasterForm {

  private String key;
  private String customerKey;
  private String clientName;
  private String petName;
  private String speciesKey;
  private String speciesName;
  private String breedKey;
  private String breedName;
  private String petColor;
  private String petSexId;
  private String petWeight;
  private String birthDate;
  private String petAge;
  private String lastCheckDate;
  private String neuteredId;
  private String neuteredDate;
  private String disposition;
  private String identificationTagNumber;
  private String rabiesTagNumber;
  private String petMemo;
  private String dvmResourceKey;
  private String dvmResourceName;
  private String documentServerURL = "";
  private String photoFileName;
  private String permissionStatus;
  private String attachmentCount;
  private String activeId;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;
  private String clientNameFilter;
  private String petNameFilter;  
  private String currentPage;  
  
  public PetForm() {
    addRequiredFields(new String[] { "customerKey", "petName", "speciesKey",
                                     "petColor", "birthDate",
                                     "dvmResourceKey" });
    addRange("birthString", null, new Date());
    addRange("neuteredString", null, new Date());
    addRange("petWeight",
              new BigDecimal("0.00"),
              new BigDecimal("9999.99"));
    addRange("petSexId", new Byte("0"), new Byte("1"));
    addRange("neuteredId", new Byte("0"), new Byte("1"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
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

  public void setPetName(String petName) {
    this.petName = petName;
  }

  public String getPetName() {
    return petName;
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

  public String getBreedKey() {
    return breedKey;
  }

  public void setBreedKey(String breedKey) {
    this.breedKey = breedKey;
  }

  public void setBreedName(String breedName) {
    this.breedName = breedName;
  }

  public String getBreedName() {
    return breedName;
  }

  public void setPetColor(String petColor) {
    this.petColor = petColor;
  }

  public String getPetColor() {
    return petColor;
  }

  public void setPetSexId(String petSexId) {
    this.petSexId = petSexId;
  }

  public String getPetSexId() {
    return petSexId;
  }

  public void setPetWeight(String petWeight) {
    this.petWeight = petWeight;
  }

  public String getPetWeight() {
    return petWeight;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setPetAge(String petAge) {
    this.petAge = petAge;
  }

  public String getPetAge() {
    return petAge;
  }

  public void setLastCheckDate(String lastCheckDate) {
    this.lastCheckDate = lastCheckDate;
  }

  public String getLastCheckDate() {
    return lastCheckDate;
  }

  public void setNeuteredId(String neuteredId) {
    this.neuteredId = neuteredId;
  }

  public String getNeuteredId() {
    return neuteredId;
  }

  public void setNeuteredDate(String neuteredDate) {
    this.neuteredDate = neuteredDate;
  }

  public String getNeuteredDate() {
    return neuteredDate;
  }

  public void setDisposition(String disposition) {
    this.disposition = disposition;
  }

  public String getDisposition() {
    return disposition;
  }

  public void setIdentificationTagNumber(String identificationTagNumber) {
    this.identificationTagNumber = identificationTagNumber;
  }

  public String getIdentificationTagNumber() {
    return identificationTagNumber;
  }

  public void setRabiesTagNumber(String rabiesTagNumber) {
    this.rabiesTagNumber = rabiesTagNumber;
  }

  public String getRabiesTagNumber() {
    return rabiesTagNumber;
  }

  public void setDvmResourceKey(String dvmResourceKey) {
    this.dvmResourceKey = dvmResourceKey;
  }

  public String getDvmResourceKey() {
    return dvmResourceKey;
  }

  public void setDvmResourceName(String dvmResourceName) {
    this.dvmResourceName = dvmResourceName;
  }

  public String getDvmResourceName() {
    return dvmResourceName;
  }

  public void setPetMemo(String petMemo) {
    this.petMemo = petMemo;
  }

  public String getPetMemo() {
    return petMemo;
  }

  public void setDocumentServerURL(String documentServerURL) {
    this.documentServerURL = documentServerURL;
  }

  public String getDocumentServerURL() {
    return documentServerURL;
  }

  public void setPhotoFileName(String photoFileName) {
    this.photoFileName = photoFileName;
  }

  public String getPhotoFileName() {
    return photoFileName;
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

  public void setActiveId(String activeId) {
    this.activeId = activeId;
  }

  public String getActiveId() {
    return activeId;
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


  public void setClientNameFilter(String clientNameFilter) {
    this.clientNameFilter = clientNameFilter;
  }

  public String getClientNameFilter() {
    return clientNameFilter;
  }

  public void setPetNameFilter(String petNameFilter) {
    this.petNameFilter = petNameFilter;
  }

  public String getPetNameFilter() {
    return petNameFilter;
  }
  
  public void setCurrentPage(String currentPage ) {
    this.currentPage = currentPage;
  }

  public String getCurrentPage() {
    return currentPage;
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

    // Set the disableEdit attribute to false when errors encountered.
    request.setAttribute("disableEdit", false);

    return errors;
  }
}