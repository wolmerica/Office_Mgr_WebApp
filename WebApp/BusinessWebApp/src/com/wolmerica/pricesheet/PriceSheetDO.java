/*
 * PriceSheetDO.java
 *
 * Created on July 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.pricesheet;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class PriceSheetDO extends AbstractDO implements Serializable {

   private Integer customerInvoiceKey = null;
   private Integer customerKey = null;
   private String clientName = "";
   private String invoiceNumber = "";
   private BigDecimal invoiceTotal = null;
   private Integer priceSheetKey = null;
   private String createUser = "";
   private Timestamp createStamp = null;
   private String updateUser = "";
   private Timestamp updateStamp = null;


  public void setCustomerInvoiceKey(Integer customerInvoiceKey) {
    this.customerInvoiceKey = customerInvoiceKey;
  }

  public Integer getCustomerInvoiceKey() {
    return customerInvoiceKey;
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

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceTotal(BigDecimal invoiceTotal) {
    this.invoiceTotal = invoiceTotal;
  }

  public BigDecimal getInvoiceTotal() {
    return invoiceTotal;
  }

  public void setPriceSheetKey(Integer priceSheetKey) {
    this.priceSheetKey = priceSheetKey;
  }

  public Integer getPriceSheetKey() {
    return priceSheetKey;
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

