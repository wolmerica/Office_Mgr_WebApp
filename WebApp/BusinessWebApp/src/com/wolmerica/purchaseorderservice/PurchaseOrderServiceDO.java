/*
 * PurchaseOrderServiceDO.java
 *
 * Created on August 13, 2008, 09:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.purchaseorderservice;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;

public class PurchaseOrderServiceDO extends AbstractDO implements Serializable
{

   private Integer key = null;
   private Integer serviceDictionaryKey = null;
   private String serviceNum = "";
   private String serviceName = "";
   private Byte priceTypeKey = new Byte("0");
   private String priceTypeName = "";
   private Short orderQty = null;
   private BigDecimal laborCost = null;
   private BigDecimal extendCost = null;
   private String serviceAction = "";
   private String noteLine1 = "";

   public void setKey(Integer key) {
     this.key = key;
   }

   public Integer getKey() {
     return key;
   }

   public void setServiceDictionaryKey(Integer serviceDictionaryKey) {
     this.serviceDictionaryKey = serviceDictionaryKey;
   }

   public Integer getServiceDictionaryKey() {
     return serviceDictionaryKey;
   }

   public void setServiceNum(String serviceNum) {
     this.serviceNum = serviceNum;
   }

   public String getServiceNum() {
     return serviceNum;
   }

   public void setServiceName(String serviceName) {
     this.serviceName = serviceName;
   }
   
   public String getServiceName() {
     return serviceName; 
   }   
  
   public void setPriceTypeKey(Byte priceTypeKey) {
     this.priceTypeKey = priceTypeKey;
   }

   public Byte getPriceTypeKey() {
     return priceTypeKey;
   }

   public void setPriceTypeName(String priceTypeName) {
     this.priceTypeName = priceTypeName;
   }

   public String getPriceTypeName() {
     return priceTypeName; 
   }
   
   public void setOrderQty(Short orderQty) {
     this.orderQty = orderQty;
   }   

   public Short getOrderQty() {
     return orderQty;
   }

   public void setLaborCost(BigDecimal laborCost) {
     this.laborCost = laborCost;
   }

   public BigDecimal getLaborCost() {
     return laborCost;
   }

   public void setExtendCost(BigDecimal extendCost) {
     this.extendCost = extendCost;
   }

   public BigDecimal getExtendCost() {
     return extendCost;
   }

   public void setServiceAction(String serviceAction) {
     this.serviceAction = serviceAction;
   }

   public String getServiceAction() {
     return serviceAction;
   }

   public void setNoteLine1(String noteLine1) {
     this.noteLine1 = noteLine1;
   }

   public String getNoteLine1() {
     return noteLine1;
   }   
   
}