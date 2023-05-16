/*
 * CustomerAttributeByServiceHeadDO.java
 *
 * Created on January 31, 2005, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.customerattributebyservice;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class CustomerAttributeByServiceHeadDO extends AbstractDO implements Serializable
{
  private Integer serviceDictionaryKey;
  private String serviceName;
  private ArrayList customerAttributeByServiceForm;

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

  public void setCustomerAttributeByServiceForm(ArrayList customerAttributeByServiceForm){
    this.customerAttributeByServiceForm = customerAttributeByServiceForm;
  }

  public ArrayList getCustomerAttributeByServiceForm(){
    return customerAttributeByServiceForm;
  }
}