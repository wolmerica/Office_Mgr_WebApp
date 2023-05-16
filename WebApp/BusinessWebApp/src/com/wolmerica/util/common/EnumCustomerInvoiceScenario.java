/*
 * EnumCustomerInvoiceScenario.java
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

public enum EnumCustomerInvoiceScenario {

  ShipToOffice("-1"), DirectShip("0"), DropShip("1"),
  SellInventory("2"), ReturnCredit("3"),  AddInventory("4"),
  OfficeUse("5"), InventoryLoss("6");

  String value;

  EnumCustomerInvoiceScenario(String value) {
    this.value = value;
  }

  public String getId() {
    return name();
  }

  public String getValue() {
    return value;
  }
}