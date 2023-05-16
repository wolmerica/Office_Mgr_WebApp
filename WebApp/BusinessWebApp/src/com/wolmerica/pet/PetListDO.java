/*
 * PetListForm.java
 *
 * Created on March 08, 2006, 9:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.pet;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.Date;

public class PetListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String petName = "";
  private String clientName = "";
  private String speciesName = "";
  private String breedName = "";
  private Boolean petSexId = false;
  private String petAge = "";
  private Date lastCheckDate = null;
  private Date lastServiceDate = null;
  private Boolean activeId = true;
  private Boolean allowDeleteId = false;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setPetName(String petName) {
    this.petName = petName;
  }

  public String getPetName() {
    return petName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setSpeciesName(String speciesName) {
    this.speciesName = speciesName;
  }

  public String getSpeciesName() {
    return speciesName;
  }

  public void setBreedName(String breedName) {
    this.breedName = breedName;
  }

  public String getBreedName() {
    return breedName;
  }

  public void setPetSexId(Boolean petSexId) {
    this.petSexId = petSexId;
  }

  public Boolean getPetSexId() {
    return petSexId;
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

  public void setLastServiceDate(Date lastServiceDate) {
    this.lastServiceDate = lastServiceDate;
  }

  public Date getLastServiceDate() {
    return lastServiceDate;
  }

  public void setActiveId(Boolean activeId) {
    this.activeId = activeId;
  }

  public Boolean getActiveId() {
    return activeId;
  }

  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }
}