/*
 * ExpenseDO.java
 *
 * Created on July 02, 2006, 3:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.expense;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class ExpenseDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Date expenseDate = new Date();
  private Date dueDate = new Date();
  private Date paymentDate = new Date();
  private String expenseName = "";
  private String expenseCategory = "";
  private BigDecimal expensePayment = new BigDecimal("0");  
  private BigDecimal expenseRate = new BigDecimal("0");
  private BigDecimal expenseAmount = new BigDecimal("0");
  private Date taxPrepDate = new Date();
  private Boolean reconciledId = false;
  private String permissionStatus = "";
  private Integer attachmentCount = null;
  private String noteLine1 = "";  
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

  public void setExpenseDate(Date expenseDate) {
    this.expenseDate = expenseDate;
  }

  public Date getExpenseDate() {
    return expenseDate;
  }  

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public Date getDueDate() {
    return dueDate;
  }  

  public void setPaymentDate(Date paymentDate) {
    this.paymentDate = paymentDate;
  }

  public Date getPaymentDate() {
    return paymentDate;
  }    
  
  public void setExpenseName(String expenseName) {
    this.expenseName = expenseName;
  }

  public String getExpenseName() {
    return expenseName;
  }

  public void setExpenseCategory(String expenseCategory) {
    this.expenseCategory = expenseCategory;
  }

  public String getExpenseCategory() {
    return expenseCategory;
  }
  
  public void setExpensePayment(BigDecimal expensePayment) {
    this.expensePayment = expensePayment;
  }

  public BigDecimal getExpensePayment(){
    return expensePayment;
  }  

  public void setExpenseRate(BigDecimal expenseRate) {
    this.expenseRate = expenseRate;
  }

  public BigDecimal getExpenseRate(){
    return expenseRate;
  }

  public void setExpenseAmount(BigDecimal expenseAmount) {
    this.expenseAmount = expenseAmount;
  }

  public BigDecimal getExpenseAmount(){
    return expenseAmount;
  }
  
  public void setTaxPrepDate(Date taxPrepDate) {
    this.taxPrepDate = taxPrepDate;
  }

  public Date getTaxPrepDate() {
    return taxPrepDate;
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
  
  public void setAttachmentCount(Integer attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public Integer getAttachmentCount() {
    return attachmentCount;
  }
  
  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
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