/*
 * PetBoardDO.java
 *
 * Created on June 20, 2007, 09:12 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petboard;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PetBoardDO extends AbstractDO implements Serializable {
  private Integer key = null;
  private String clientName = "";
  private Integer petKey = null;
  private String petName = "";
  private Integer scheduleKey = null;
  private Date checkInDate = null;
  private Byte checkInHour = null;
  private Byte checkInMinute = null;
  private Date checkOutDate = null;
  private Byte checkOutHour = null;
  private Byte checkOutMinute = null;
  private String checkOutTo = "";
  private String boardReason = "";
  private String boardInstruction = "";
  private String emergencyName = "";
  private String emergencyPhone = "";
  private Boolean vaccinationId = false;
  private Boolean specialDietId = false;
  private Boolean medicationId = false;
  private Boolean serviceId = false;
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

  public void setCheckInDate(Date checkInDate) {
    this.checkInDate = checkInDate;
  }

  public Date getCheckInDate() {
    return checkInDate;
  }

  public void setCheckInHour(Byte checkInHour) {
    this.checkInHour = checkInHour;
  }

  public Byte getCheckInHour() {
    return checkInHour;
  }

  public void setCheckInMinute(Byte checkInMinute) {
    this.checkInMinute = checkInMinute;
  }

  public Byte getCheckInMinute() {
    return checkInMinute;
  }

  public void setCheckOutDate(Date checkOutDate) {
    this.checkOutDate = checkOutDate;
  }

  public Date getCheckOutDate() {
    return checkOutDate;
  }

  public void setCheckOutHour(Byte checkOutHour) {
    this.checkOutHour = checkOutHour;
  }

  public Byte getCheckOutHour() {
    return checkOutHour;
  }

  public void setCheckOutMinute(Byte checkOutMinute) {
    this.checkOutMinute = checkOutMinute;
  }

  public Byte getCheckOutMinute() {
    return checkOutMinute;
  }

  public void setCheckOutTo(String checkOutTo) {
    this.checkOutTo = checkOutTo;
  }

  public String getCheckOutTo() {
    return checkOutTo;
  }

  public void setBoardReason(String boardReason) {
    this.boardReason = boardReason;
  }

  public String getBoardReason() {
    return boardReason;
  }

  public void setBoardInstruction(String boardInstruction) {
    this.boardInstruction = boardInstruction;
  }

  public String getBoardInstruction() {
    return boardInstruction;
  }

  public void setEmergencyName(String emergencyName) {
    this.emergencyName = emergencyName;
  }

  public String getEmergencyName() {
    return emergencyName;
  }

  public void setEmergencyPhone(String emergencyPhone) {
    this.emergencyPhone = emergencyPhone;
  }

  public String getEmergencyPhone() {
    return emergencyPhone;
  }

  public void setVaccinationId(Boolean vaccinationId) {
    this.vaccinationId = vaccinationId;
  }

  public Boolean getVaccinationId() {
    return vaccinationId;
  }

  public void setSpecialDietId(Boolean specialDietId) {
    this.specialDietId = specialDietId;
  }

  public Boolean getSpecialDietId() {
    return specialDietId;
  }

  public void setMedicationId(Boolean medicationId) {
    this.medicationId = medicationId;
  }

  public Boolean getMedicationId() {
    return medicationId;
  }

  public void setServiceId(Boolean serviceId) {
    this.serviceId = serviceId;
  }

  public Boolean getServiceId() {
    return serviceId;
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
