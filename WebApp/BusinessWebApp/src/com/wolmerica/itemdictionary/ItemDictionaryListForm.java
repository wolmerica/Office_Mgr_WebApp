/*
 * ItemDictionaryListForm.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.itemdictionary;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;

public class ItemDictionaryListForm extends MasterForm {

  private String key;
  private String brandName;
  private String size;
  private String sizeUnit;
  private String manufacturer;
  private String itemNum;
  private String profileNum;
  private String genericName;
  private String firstCost;
  private String unitCost;
  private String qtyOnHand;
  private String sellableItemId;
  private String sourceTypeKey;
  private String sourceKey;
  private String rebateCount;
  private String rebateInstanceCount;
  private String belowThresholdId;
  private String itemUnitsSold;
  private String allowDeleteId;

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getSize() {
    return size;
  }

  public void setSizeUnit(String sizeUnit) {
    this.sizeUnit = sizeUnit;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setItemNum(String itemNum) {
    this.itemNum = itemNum;
  }

  public String getItemNum() {
    return itemNum;
  }

  public void setProfileNum(String profileNum) {
    this.profileNum = profileNum;
  }

  public String getProfileNum() {
    return profileNum;
  }
  
  public void setGenericName(String genericName) {
    this.genericName = genericName;
  }

  public String getGenericName() {
    return genericName;
  }

  public void setFirstCost(String firstCost) {
    this.firstCost = firstCost;
  }

  public String getFirstCost() {
    return firstCost;
  }

  public void setUnitCost(String unitCost) {
    this.unitCost = unitCost;
  }

  public String getUnitCost() {
    return unitCost;
  }

  public void setQtyOnHand(String qtyOnHand) {
    this.qtyOnHand = qtyOnHand;
  }

  public String getQtyOnHand() {
    return qtyOnHand;
  }

  public void setSellableItemId(String sellableItemId) {
    this.sellableItemId = sellableItemId;
  }

  public String getSellableItemId() {
    return sellableItemId;
  }

  public void setSourceTypeKey(String sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public String getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setSourceKey(String sourceKey) {
    this.sourceKey = sourceKey;
  }

  public String getSourceKey() {
    return sourceKey;
  }

  public void setRebateCount(String rebateCount) {
    this.rebateCount = rebateCount;
  }

  public String getRebateCount() {
    return rebateCount;
  }

  public void setRebateInstanceCount(String rebateInstanceCount) {
    this.rebateInstanceCount = rebateInstanceCount;
  }

  public String getRebateInstanceCount() {
    return rebateInstanceCount;
  }

  public void setBelowThresholdId(String belowThresholdId) {
    this.belowThresholdId = belowThresholdId;
  }

  public String getBelowThresholdId() {
    return belowThresholdId;
  }

  public void setItemUnitsSold(String itemUnitsSold) {
    this.itemUnitsSold = itemUnitsSold;
  }

  public String getItemUnitsSold() {
    return itemUnitsSold;
  }

  public void setAllowDeleteId(String allowDeleteId) {
    this.allowDeleteId = allowDeleteId;
  }

  public String getAllowDeleteId() {
    return allowDeleteId;
  }

  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();

    EmployeesActionMapping employeesMapping = (EmployeesActionMapping)mapping;

    // Does this action require the user to login
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {

        // return null to force action to handle login
        // error
        return null;
      }
    }

    errors = super.validate(mapping, request);

    // Post a global message instructing user to clean up
    // validation errors and resubmit
    if (errors.size() > 0) {
      ActionMessage message =
          new ActionMessage("message.validation");
      ActionMessages messages = new ActionMessages();
          messages.add(ActionMessages.GLOBAL_MESSAGE, message);
      request.setAttribute(Globals.MESSAGE_KEY, messages);
    }

    // Set the disableEdit attribute to false when errors encountered.
    request.setAttribute("disableEdit", false);

    return errors;
  }
}

