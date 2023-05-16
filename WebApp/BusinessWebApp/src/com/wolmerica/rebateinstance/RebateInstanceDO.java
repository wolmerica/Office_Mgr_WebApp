/*
 * RebateInstanceDO.java
 *
 * Created on May 29, 2006, 8:28 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.rebateinstance;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class RebateInstanceDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Integer rebateKey = null;
  private String offerName = "";
  private BigDecimal amount = new BigDecimal("0");
  private String purchaseOrderNumber = "";
  private Integer purchaseOrderItemKey = null;
  private String trackingURL = "";
  private String noteLine1 = "";
  private Boolean eligibleId = false;
  private Boolean submitId = false;
  private Boolean completeId = false;
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

  public void setRebateKey(Integer rebateKey) {
    this.rebateKey = rebateKey;
  }

  public Integer getRebateKey() {
    return rebateKey;
  }

  public void setOfferName(String offerName) {
    this.offerName = offerName;
  }

  public String getOfferName() {
    return offerName;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setPurchaseOrderItemKey(Integer purchaseOrderItemKey) {
    this.purchaseOrderItemKey = purchaseOrderItemKey;
  }

  public Integer getPurchaseOrderItemKey() {
    return purchaseOrderItemKey;
  }

  public void setTrackingURL(String trackingURL) {
    this.trackingURL = trackingURL;
  }

  public String getTrackingURL() {
    return trackingURL;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }

  public void setEligibleId(Boolean eligibleId) {
    this.eligibleId = eligibleId;
  }

  public Boolean getEligibleId() {
    return eligibleId;
  }

  public void setSubmitId(Boolean submitId) {
    this.submitId = submitId;
  }

  public Boolean getSubmitId() {
    return submitId;
  }

  public void setCompleteId(Boolean completeId) {
    this.completeId = completeId;
  }

  public Boolean getCompleteId() {
    return completeId;
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