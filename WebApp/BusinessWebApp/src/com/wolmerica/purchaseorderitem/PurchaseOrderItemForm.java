/*
 * PurchaseOrderItemForm.java
 *
 * Created on January 31, 2006, 10:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.purchaseorderitem;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;

public class PurchaseOrderItemForm extends MasterForm
{

  private String key;
  private String itemDictionaryKey;
  private String itemNum;
  private String brandName;
  private String size;
  private String sizeUnit;
  private String orderQty;
  private String firstCost;
  private String extendCost;
  private String itemAction;
  private String noteLine1;
  private String rebateCount = null;
  private String rebateInstanceCount = null;

   public PurchaseOrderItemForm() {
    addRequiredFields(new String[] { "key", "orderQty" } );
    addRange("orderQty", new Short("0"), new Short("9999"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

 public void setItemDictionaryKey(String itemDictionaryKey) {
    this.itemDictionaryKey = itemDictionaryKey;
  }

  public String getItemDictionaryKey() {
    return itemDictionaryKey;
  }

  public void setItemNum(String itemNum) {
    this.itemNum = itemNum;
  }

  public String getItemNum() {
    return itemNum;
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

  public void setOrderQty(String orderQty) {
    this.orderQty = orderQty;
  }

  public String getOrderQty() {
    return orderQty;
  }

  public void setFirstCost(String firstCost) {
    this.firstCost = firstCost;
  }

  public String getFirstCost() {
    return firstCost;
  }

  public void setExtendCost(String extendCost) {
    this.extendCost = extendCost;
  }

  public String getExtendCost() {
    return extendCost;
  }

  public void setItemAction(String itemAction) {
    this.itemAction = itemAction;
  }

  public String getItemAction() {
    return itemAction;
  }


   public void setNoteLine1(String noteLine1) {
     this.noteLine1 = noteLine1;
   }

   public String getNoteLine1() {
     return noteLine1;
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

    return errors;
  }
}