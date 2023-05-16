/*
 * EnumPurchaseOrderStatus.java
 *
 * Created on May 11, 2010, 09:59 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.util.common;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------

public enum EnumPurchaseOrderStatus {

  N("New"), O("Ordered"), I("Check-In"), B("Back-Ordered"), C("Complete");

  private String value;

  EnumPurchaseOrderStatus(String value) {
    this.value = value;
  }

  public String getId() {
    return name();
  }

  public String getValue() {
    return value;
  }
}