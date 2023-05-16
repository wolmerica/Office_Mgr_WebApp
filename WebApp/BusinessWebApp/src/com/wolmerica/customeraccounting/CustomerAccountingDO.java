/*
 * CustomerAccountingDO.java
 *
 * Created on March 28, 2006, 9:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customeraccounting;

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

public class CustomerAccountingDO extends AbstractDO implements Serializable {

   private Integer key = null;
   private Integer customerKey = null;
   private String clientName = "";
   private String acctNum = "";
   private String acctName = "";
   private Byte transactionTypeKey = null;
   private String transactionTypeName = "";
   private String transactionTypeDescription = "";
   private Date postDate = null;
   private ArrayList accountingTypeForm;
   private Byte sourceTypeKey = null;
   private String sourceTypeName = "";
   private String sourceTypeDescription = "";
   private Integer sourceKey = new Integer("-1");
   private String number = "";
   private BigDecimal amount = null;
   private Short daysDue = null;
   private BigDecimal amountDue = null;
   private String note = "";
   private Boolean reconciledId = false;
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

  public void setAcctNum(String acctNum) {
    this.acctNum = acctNum;
  }

  public String getAcctNum() {
    return acctNum;
  }

  public void setAcctName(String acctName) {
    this.acctName = acctName;
  }

  public String getAcctName() {
    return acctName;
  }

  public void setTransactionTypeKey(Byte transactionTypeKey) {
    this.transactionTypeKey = transactionTypeKey;
  }

  public Byte getTransactionTypeKey() {
    return transactionTypeKey;
  }

  public void setTransactionTypeDescription(String transactionTypeDescription) {
    this.transactionTypeDescription = transactionTypeDescription;
  }

  public String getTransactionTypeDescription() {
    return transactionTypeDescription;
  }

  public void setTransactionTypeName(String transactionTypeName) {
    this.transactionTypeName = transactionTypeName;
  }

  public String getTransactionTypeName() {
    return transactionTypeName;
  }

  public void setPostDate(Date postDate) {
    this.postDate = postDate;
  }

  public Date getPostDate() {
    return postDate;
  }

  public void setAccountingTypeForm(ArrayList accountingTypeList){
  this.accountingTypeForm = accountingTypeList;
  }

  public ArrayList getAccountingTypeForm(){
  return accountingTypeForm;
  }

  public void setSourceTypeKey(Byte sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public Byte getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceTypeDescription(String sourceTypeDescription) {
    this.sourceTypeDescription = sourceTypeDescription;
  }

  public String getSourceTypeDescription() {
    return sourceTypeDescription;
  }

  public void setSourceTypeName(String sourceTypeName) {
    this.sourceTypeName = sourceTypeName;
  }

  public String getSourceTypeName() {
    return sourceTypeName;
  }

  public void setSourceKey(Integer sourceKey) {
    this.sourceKey = sourceKey;
  }

  public Integer getSourceKey() {
    return sourceKey;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getNumber() {
    return number;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setDaysDue(Short daysDue) {
    this.daysDue = daysDue;
  }

  public Short getDaysDue() {
    return daysDue;
  }

  public void setAmountDue(BigDecimal amountDue) {
    this.amountDue = amountDue;
  }

  public BigDecimal getAmountDue() {
    return amountDue;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getNote() {
    return note;
  }

  public void setReconciledId(Boolean reconciledId) {
    this.reconciledId = reconciledId;
  }

  public Boolean getReconciledId() {
    return reconciledId;
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