/*
 * ItemOriginDO.java
 *
 * Created on January 17, 2008, 08:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.itemorigin;

/**
 *
 * @author Richard
 */

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ItemOriginDO extends AbstractDO implements Serializable
{
   private Integer transactionKey = null;
   private Date transactionDate = null;
   private String transactionName = "";
   private Integer vendorKey = null;
   private String vendorName = "";
   private Integer customerKey = null;
   private String clientName = "";
   private Date expirationDate = null;
   private BigDecimal quantity = null;

   public void setTransactionKey(Integer transactionKey) {
     this.transactionKey = transactionKey;
   }

   public Integer getTransactionKey() {
     return transactionKey;
   }

   public void setTransactionDate(Date transactionDate) {
     this.transactionDate = transactionDate;
   }

   public Date getTransactionDate() {
     return transactionDate;
   }

   public void setTransactionName(String transactionName) {
     this.transactionName = transactionName;
   }

   public String getTransactionName() {
     return transactionName;
   }

   public void setVendorKey(Integer vendorKey) {
     this.vendorKey = vendorKey;
   }

   public Integer getVendorKey() {
     return vendorKey;
   }

   public void setVendorName(String vendorName) {
     this.vendorName = vendorName;
   }

   public String getVendorName() {
     return vendorName;
   }

   public void setCustomerKey(Integer customerKey) {
     this.customerKey = customerKey;
   }

   public Integer getCustomerKey() {
     return customerKey;
   }

   public void setClientName(String clientName) {
     this.clientName = clientName;
   }

   public String getClientName() {
     return clientName;
   }

   public void setExpirationDate(Date expirationDate) {
     this.expirationDate = expirationDate;
   }

   public Date getExpirationDate() {
     return expirationDate;
   }

   public void setQuantity(BigDecimal quantity) {
     this.quantity = quantity;
   }

   public BigDecimal getQuantity() {
     return quantity;
   }
}