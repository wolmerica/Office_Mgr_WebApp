/*
 * PetExamDO.java
 *
 * Created on August 23, 2007, 08:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petexam;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PetExamDO extends AbstractDO implements Serializable {
  private Integer key = null;
  private String clientName;
  private Integer petKey = null;
  private String petName = "";
  private Integer scheduleKey = null;
  private Date treatmentDate = null;
  private String subject = "";
  private Integer dvmResourceKey = null;
  private String dvmResourceName = "";
  private Integer techResourceKey = null;
  private String techResourceName = "";
  private String heartRate = "";
  private String resptRate = "";
  private String capRefillTime = "";
  private BigDecimal temperature = new BigDecimal("0");
  private BigDecimal bodyWeight = new BigDecimal("0");
  private String generalCondition = "";
  private String medicalData = "";
  private Byte startHour;
  private Byte startMinute;
  private Byte surgeryHour;
  private Byte surgeryMinute;
  private Byte endHour;
  private Byte endMinute;
  private Byte reflexHour;
  private Byte reflexMinute;
  private Byte recoveryHour;
  private Byte recoveryMinute;
  private String noteLine1 = "";
  private Boolean releaseId = false;
  private String permissionStatus = "";
  private String createUser = "";
  private Timestamp createStamp = null;
  private String updateUser = "";
  private Timestamp updateStamp = null;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
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

  public void setScheduleKey(Integer scheduleKey) {
    this.scheduleKey = scheduleKey;
  }

  public Integer getScheduleKey() {
    return scheduleKey;
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

  public void setTechResourceKey(Integer techResourceKey) {
    this.techResourceKey = techResourceKey;
  }

  public Integer getTechResourceKey() {
    return techResourceKey;
  }

  public void setTechResourceName(String techResourceName) {
    this.techResourceName = techResourceName;
  }

  public String getTechResourceName() {
    return techResourceName;
  }

  public void setHeartRate(String heartRate) {
    this.heartRate = heartRate;
  }

  public String getHeartRate() {
    return heartRate;
  }

  public void setResptRate(String resptRate) {
    this.resptRate = resptRate;
  }

  public String getResptRate() {
    return resptRate;
  }

  public void setCapRefillTime(String capRefillTime) {
    this.capRefillTime = capRefillTime;
  }

  public String getCapRefillTime() {
    return capRefillTime;
  }

  public void setTemperature(BigDecimal temperature) {
    this.temperature = temperature;
  }

  public BigDecimal getTemperature() {
    return temperature;
  }

  public void setBodyWeight(BigDecimal bodyWeight) {
    this.bodyWeight = bodyWeight;
  }

  public BigDecimal getBodyWeight() {
    return bodyWeight;
  }

  public void setGeneralCondition(String generalCondition) {
    this.generalCondition = generalCondition;
  }

  public String getGeneralCondition() {
    return generalCondition;
  }

  public void setMedicalData(String medicalData) {
    this.medicalData = medicalData;
  }

  public String getMedicalData() {
    return medicalData;
  }

  public void setStartHour(Byte startHour) {
    this.startHour = startHour;
  }

  public Byte getStartHour() {
    return startHour;
  }

  public void setStartMinute(Byte startMinute) {
    this.startMinute = startMinute;
  }

  public Byte getStartMinute() {
    return startMinute;
  }

  public void setSurgeryHour(Byte surgeryHour) {
    this.surgeryHour = surgeryHour;
  }

  public Byte getSurgeryHour() {
    return surgeryHour;
  }

  public void setSurgeryMinute(Byte surgeryMinute) {
    this.surgeryMinute = surgeryMinute;
  }

  public Byte getSurgeryMinute() {
    return surgeryMinute;
  }

  public void setEndHour(Byte endHour) {
    this.endHour = endHour;
  }

  public Byte getEndHour() {
    return endHour;
  }

  public void setEndMinute(Byte endMinute) {
    this.endMinute = endMinute;
  }

  public Byte getEndMinute() {
    return endMinute;
  }

  public void setReflexHour(Byte reflexHour) {
    this.reflexHour = reflexHour;
  }

  public Byte getReflexHour() {
    return reflexHour;
  }

  public void setReflexMinute(Byte reflexMinute) {
    this.reflexMinute = reflexMinute;
  }

  public Byte getReflexMinute() {
    return reflexMinute;
  }

  public void setRecoveryHour(Byte recoveryHour) {
    this.recoveryHour = recoveryHour;
  }

  public Byte getRecoveryHour() {
    return recoveryHour;
  }

  public void setRecoveryMinute(Byte recoveryMinute) {
    this.recoveryMinute = recoveryMinute;
  }

  public Byte getRecoveryMinute() {
    return recoveryMinute;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setReleaseId(Boolean releaseId) {
    this.releaseId = releaseId;
  }

  public Boolean getReleaseId() {
    return releaseId;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
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
}
