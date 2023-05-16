/*
 * PetBoardListDO.java
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

public class PetBoardListDO extends AbstractDO implements Serializable {
  private Integer key = null;
  private String clientName;
  private Integer petKey = null;
  private String petName = "";
  private Integer scheduleKey = null;  
  private Date checkInDate = null;
  private String boardReason = "";
  private String boardInstruction = "";
  private Boolean allowDeleteId = false;

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

  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }
}
