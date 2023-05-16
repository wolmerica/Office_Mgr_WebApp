/*
 * PetDO.java
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
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PetDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer customerKey = null;
  private String clientName = "";
  private String petName = "";
  private Integer speciesKey = null;
  private String speciesName = "";
  private Integer breedKey = null;
  private String breedName = "";
  private String petColor = "";
  private Byte petSexId = new Byte("0");
  private BigDecimal petWeight = new BigDecimal("0");
  private Date birthDate = null;
  private String petAge = "";
  private Date lastCheckDate = null;
  private Byte neuteredId = new Byte("0");
  private Date neuteredDate = null;
  private String disposition = "";
  private String identificationTagNumber = "";
  private String rabiesTagNumber = "";
  private Integer dvmResourceKey = null;
  private String dvmResourceName = "";
  private String petMemo = "";
  private String documentServerURL = "";
  private String photoFileName = "";
  private String permissionStatus = "";
  private Integer attachmentCount = null;
  private Boolean activeId = true;
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;
  private String clientNameFilter = null;
  private String petNameFilter = "";  
  private Integer currentPage = new Integer("1");  
  
  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setCustomerKey(Integer customerKey) {
    this.customerKey = customerKey;
  }

  public Integer getCustomerKey() {
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

  public void setSpeciesKey(Integer speciesKey) {
    this.speciesKey = speciesKey;
  }

  public Integer getSpeciesKey() {
    return speciesKey;
  }

  public void setSpeciesName(String speciesName) {
    this.speciesName = speciesName;
  }

  public String getSpeciesName() {
    return speciesName;
  }

  public Integer getBreedKey() {
    return breedKey;
  }

  public void setBreedKey(Integer breedKey) {
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

  public void setPetSexId(Byte petSexId) {
    this.petSexId = petSexId;
  }

  public Byte getPetSexId() {
    return petSexId;
  }

  public void setPetWeight(BigDecimal petWeight) {
    this.petWeight = petWeight;
  }

  public BigDecimal getPetWeight() {
    return petWeight;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setPetAge(String petAge) {
    this.petAge = petAge;
  }

  public String getPetAge() {
    return petAge;
  }

  public void setLastCheckDate(Date lastCheckDate) {
    this.lastCheckDate = lastCheckDate;
  }

  public Date getLastCheckDate() {
    return lastCheckDate;
  }

  public void setNeuteredId(Byte neuteredId) {
    this.neuteredId = neuteredId;
  }

  public Byte getNeuteredId() {
    return neuteredId;
  }

  public void setNeuteredDate(Date neuteredDate) {
    this.neuteredDate = neuteredDate;
  }

  public Date getNeuteredDate() {
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

  public void setDvmResourceKey(Integer dvmResourceKey) {
    this.dvmResourceKey = dvmResourceKey;
  }

  public Integer getDvmResourceKey() {
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

  public void setAttachmentCount(Integer attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public Integer getAttachmentCount() {
    return attachmentCount;
  }

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateStamp(Timestamp createStamp) {
    this.createStamp = createStamp;
  }

  public Timestamp getCreateStamp() {
    return createStamp;
  }

  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getUpdateUser() {
    return updateUser;
  }

  public void setUpdateStamp(Timestamp updateStamp) {
    this.updateStamp = updateStamp;
  }

  public Timestamp getUpdateStamp() {
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
  
  public void setCurrentPage(Integer currentPage ) {
    this.currentPage = currentPage;
  }

  public Integer getCurrentPage() {
    return currentPage;
  } 
}
