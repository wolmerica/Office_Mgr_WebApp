/*
 * PetExamListForm.java
 *
 * Created on August 23, 2007, 9:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.petexam;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.Date;

public class PetExamListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer clientKey = null;
  private String clientName = "";
  private Integer petKey = null;
  private String petName = "";
  private Date treatmentDate = null;
  private String subject = "";
  private Boolean releaseId = false;
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

  public void setTreatmentDate(Date treatmentDate) {
    this.treatmentDate = treatmentDate;
  }

  public Date getTreatmentDate() {
    return treatmentDate;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return subject;
  }

  public void setReleaseId(Boolean releaseId) {
    this.releaseId = releaseId;
  }

  public Boolean getReleaseId() {
    return releaseId;
  }

  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }
}