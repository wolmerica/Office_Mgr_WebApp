/*
 * PurchaseOrderServiceForm.java
 *
 * Created on January 31, 2006, 10:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.purchaseorderservice;

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

public class PurchaseOrderServiceForm extends MasterForm
{

  private String key;
  private String serviceDictionaryKey;
  private String serviceNum;
  private String serviceName; 
  private String priceTypeKey;
  private String priceTypeName;
  private String orderQty;
  private String laborCost;
  private String extendCost;
  private String serviceAction;
  private String noteLine1;

   public PurchaseOrderServiceForm() {
    addRequiredFields(new String[] { "key", "orderQty" } );
    addRange("orderQty", new Short("0"), new Short("9999"));
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setServiceDictionaryKey(String serviceDictionaryKey) {
    this.serviceDictionaryKey = serviceDictionaryKey;
  }

  public String getServiceDictionaryKey() {
    return serviceDictionaryKey;
  }

  public void setServiceNum(String serviceNum) {
    this.serviceNum = serviceNum;
  }

  public String getServiceNum() {
    return serviceNum;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
   return serviceName; 
  }   
    
  public void setPriceTypeKey(String priceTypeKey) {
    this.priceTypeKey = priceTypeKey;
  }

  public String getPriceTypeKey() {
    return priceTypeKey;
  }

  public void setPriceTypeName(String priceTypeName) {
    this.priceTypeName = priceTypeName;
  }

  public String getPriceTypeName() {
    return priceTypeName; 
  }

  public void setOrderQty(String orderQty) {
    this.orderQty = orderQty;
  }  
  
  public String getOrderQty() {
    return orderQty;
  }

  public void setLaborCost(String laborCost) {
    this.laborCost = laborCost;
  }

  public String getLaborCost() {
    return laborCost;
  }

  public void setExtendCost(String extendCost) {
    this.extendCost = extendCost;
  }

  public String getExtendCost() {
    return extendCost;
  }

  public void setServiceAction(String serviceAction) {
    this.serviceAction = serviceAction;
  }

  public String getServiceAction() {
    return serviceAction;
  }

  public void setNoteLine1(String noteLine1) {
    this.noteLine1 = noteLine1;
  }

  public String getNoteLine1() {
    return noteLine1;
  }


    @Override
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