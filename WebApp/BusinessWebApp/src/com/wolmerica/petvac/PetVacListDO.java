/*
 * PetVacListForm.java
 *
 * Created on May 08, 2007, 9:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.petvac;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.Date;

public class PetVacListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer clientKey = null;
  private String clientName = "";
  private Integer petKey = null;
  private String petName = "";
  private Date vacDate = null;
  private Boolean rabiesId = false;
  private Date vacExpirationDate = null;
  private Boolean canineDistemperId = false;
  private Boolean corunnaId = false;
  private Boolean felineDistemperId = false;
  private Boolean felineLeukemiaId = false;
  private Boolean otherId = false;
  private Boolean allowDeleteId = false;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setClientKey(Integer clientKey) {
    this.clientKey = clientKey;
  }

  public Integer getClientKey() {
    return clientKey;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setPetKey(Integer petKey) {
    this.petKey = petKey;
  }

  public Integer getPetKey() {
    return petKey;
  }

  public void setPetName(String petName) {
    this.petName = petName;
  }

  public String getPetName() {
    return petName;
  }

  public void setVacDate(Date vacDate) {
    this.vacDate = vacDate;
  }

  public Date getVacDate() {
    return vacDate;
  }

  public void setRabiesId(Boolean rabiesId) {
    this.rabiesId = rabiesId;
  }

  public Boolean getRabiesId() {
    return rabiesId;
  }

  public void setVacExpirationDate(Date vacExpirationDate) {
    this.vacExpirationDate = vacExpirationDate;
  }

  public Date getVacExpirationDate() {
    return vacExpirationDate;
  }

  public void setCanineDistemperId(Boolean canineDistemperId) {
    this.canineDistemperId = canineDistemperId;
  }

  public Boolean getCanineDistemperId() {
    return canineDistemperId;
  }

  public void setCorunnaId(Boolean corunnaId) {
    this.corunnaId = corunnaId;
  }

  public Boolean getCorunnaId() {
    return corunnaId;
  }

  public void setFelineDistemperId(Boolean felineDistemperId) {
    this.felineDistemperId = felineDistemperId;
  }

  public Boolean getFelineDistemperId() {
    return felineDistemperId;
  }

  public void setFelineLeukemiaId(Boolean felineLeukemiaId) {
    this.felineLeukemiaId = felineLeukemiaId;
  }

  public Boolean getFelineLeukemiaId() {
    return felineLeukemiaId;
  }

  public void setOtherId(Boolean otherId) {
    this.otherId = otherId;
  }

  public Boolean getOtherId() {
    return otherId;
  }  
  
  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }
}