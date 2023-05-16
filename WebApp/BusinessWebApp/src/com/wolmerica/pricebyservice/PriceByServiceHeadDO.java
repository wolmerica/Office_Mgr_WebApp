/*
 * PriceByServiceHeadDO.java
 *
 * Created on August 09, 2006, 12:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.pricebyservice;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;

public class PriceByServiceHeadDO extends AbstractDO implements Serializable
{
  private Integer serviceDictionaryKey;
  private String serviceName;
  private ArrayList priceByServiceForm;

  public void setServiceDictionaryKey(Integer serviceDictionaryKey) {
    this.serviceDictionaryKey = serviceDictionaryKey;
  }

  public Integer getServiceDictionaryKey() {
    return serviceDictionaryKey;
  }

  public void setServiceName(String serviceName ) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setPriceByServiceForm(ArrayList priceByServiceForm){
    this.priceByServiceForm=priceByServiceForm;
  }

  public ArrayList getPriceByServiceForm(){
    return priceByServiceForm;
  }
}