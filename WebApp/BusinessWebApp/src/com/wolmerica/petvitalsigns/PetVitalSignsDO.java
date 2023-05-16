/*
 * PetVitalSignsDO.java
 *
 * Created on August 25, 2007, 07:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.petvitalsigns;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PetVitalSignsDO extends AbstractDO implements Serializable
{
  private Integer key = null;
  private Integer petExamKey = null;
  private Date treatmentDate = null;
  private Byte startHour = null;
  private Byte startMinute = null;
  private String heartRate = "";
  private String resptRate = "";
  private String noteLine1 = "";
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

  public void setPetExamKey(Integer petExamKey) {
    this.petExamKey = petExamKey;
  }

  public Integer getPetExamKey() {
    return petExamKey;
  }

  public void setTreatmentDate(Date treatmentDate) {
    this.treatmentDate = treatmentDate;
  }

  public Date getTreatmentDate() {
    return treatmentDate;
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

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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