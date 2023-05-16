/*
 * VehicleListDO.java
 *
 * Created on June 10, 2007, 11:06 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.vehicle;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class VehicleListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String clientName = "";
  private Short year = null;
  private String make = "";
  private String model = "";
  private Integer odometer = null;
  private String color = "";
  private Date vehicleDate = null;
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

  public void setYear(Short year) {
    this.year = year;
  }

  public Short getYear() {
    return year;
  }

  public void setMake(String make) {
    this.make = make;
  }

  public String getMake() {
    return make;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getModel() {
    return model;
  }

  public void setOdometer(Integer odometer) {
    this.odometer = odometer;
  }

  public Integer getOdometer() {
    return odometer;
  }
  
  public void setColor(String color) {
    this.color = color;
  }

  public String getColor() {
    return color;
  }  

  public void setVehicleDate(Date vehicleDate) {
    this.vehicleDate = vehicleDate;
  }

  public Date getVehicleDate() {
    return vehicleDate;
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
