/*
 * LedgerDO.java
 *
 * Created on September 23, 2005, 9:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.ledger;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class LedgerDO extends AbstractDO implements Serializable {

   private Integer key = null;
   private Integer customerInvoiceKey = null;
   private Integer customerKey = null;
   private String clientName = "";
   private String invoiceNumber = "";
   private BigDecimal invoiceTotal = null;
   private Timestamp postStamp = null;
   private String slipNumber = "";
   private Byte archivedId = new Byte("0");
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

  public void setSlipNumber(String slipNumber) {
    this.slipNumber = slipNumber;
  }

  public String getSlipNumber() {
    return slipNumber;
  }

 public void setPostStamp(Timestamp postStamp) {

    this.postStamp = postStamp;
  }

public Timestamp getPostStamp() {

    return postStamp;
  }

  public void setArchivedId(Byte archivedId) {

    this.archivedId = archivedId;
  }

  public Byte getArchivedId() {

    return archivedId;
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

