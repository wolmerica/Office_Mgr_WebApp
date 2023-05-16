/*
 * SystemListDO.java
 *
 * Created on August 26, 2005, 11:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.system;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class SystemListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String clientName = "";
  private String makeModel = "";
  private String processor = "";
  private String memory = "";
  private String operatingSystem = "";
  private Date systemDate = null;
  private Date lastServiceDate = null;
  private Boolean activeId = true;
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

  public void setMakeModel(String makeModel) {
    this.makeModel = makeModel;
  }

  public String getMakeModel() {
    return makeModel;
  }

  public void setProcessor(String processor) {
    this.processor = processor;
  }

  public String getProcessor() {
    return processor;
  }

  public void setMemory(String memory) {
    this.memory = memory;
  }

  public String getMemory() {
    return memory;
  }

  public void setOperatingSystem(String operatingSystem) {
    this.operatingSystem = operatingSystem;
  }

  public String getOperatingSystem() {
    return operatingSystem;
  }

  public void setSystemDate(Date systemDate) {
    this.systemDate = systemDate;
  }

  public Date getSystemDate() {
    return systemDate;
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
