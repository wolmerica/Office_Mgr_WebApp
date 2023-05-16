/*
 * ServiceDictionaryListDO.java
 *
 * Created on June 20, 2006, 07:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.servicedictionary;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;

public class ServiceDictionaryListDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private String serviceName = "";
  private String serviceNum = "";
  private String serviceCategory = "";
  private String profileNum = "";
  private Boolean releaseId = false;  
  private String priceTypeName = "";
  private Byte durationHours = null;
  private Byte durationMinutes = null;
  private BigDecimal serviceCost = null;
  private BigDecimal serviceHoursSold = null;
  private Boolean allowDeleteId = false;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceNum(String serviceNum) {
    this.serviceNum = serviceNum;
  }

  public String getServiceNum() {
    return serviceNum;
  }
  
  public void setServiceCategory(String serviceCategory) {
    this.serviceCategory = serviceCategory;
  }

  public String getServiceCategory() {
    return serviceCategory;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }
  
  public void setReleaseId(Boolean releaseId) {
    this.releaseId = releaseId;
  }

  public Boolean getReleaseId() {
    return releaseId;
  }
  
  public void setPriceTypeName(String priceTypeName) {
    this.priceTypeName = priceTypeName;
  }

  public String getPriceTypeName() {
    return priceTypeName;
  }
    
  public void setDurationHours(Byte durationHours) {
    this.durationHours = durationHours;
  }

  public Byte getDurationHours() {
    return durationHours;
  }

  public void setDurationMinutes(Byte durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public Byte getDurationMinutes() {
    return durationMinutes;
  }

  public void setServiceCost(BigDecimal serviceCost) {
    this.serviceCost = serviceCost;
  }

  public BigDecimal getServiceCost() {
    return serviceCost;
  }

  public void setServiceHoursSold(BigDecimal serviceHoursSold) {
    this.serviceHoursSold = serviceHoursSold;
  }

  public BigDecimal getServiceHoursSold() {
    return serviceHoursSold;
  }
  
  public void setAllowDeleteId(Boolean allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public Boolean getAllowDeleteId() {
    return allowDeleteId;
  }    
  
}

