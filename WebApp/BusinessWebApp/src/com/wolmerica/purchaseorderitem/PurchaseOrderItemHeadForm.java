/*
 * PurchaseOrderItemHeadForm.java
 *
 * Created on January 31, 2005, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
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
import java.util.ArrayList;

public class PurchaseOrderItemHeadForm extends MasterForm
{
  private String purchaseOrderNumber;
  private String purchaseOrderKey;
  private String vendorName;
  private String vendorKey;
  private String itemTotal;
  private String orderStatus;
  private String permissionStatus;
  private String recordCount;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String nextPage;
  private ArrayList purchaseOrderItemForm;

  public PurchaseOrderItemHeadForm() {
    addRequiredFields(new String[] { "purchaseOrderNumber", "vendorName" } );
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setPurchaseOrderKey(String purchaseOrderKey ) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public String getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setVendorName(String vendorName ) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorKey(String vendorKey ) {
    this.vendorKey = vendorKey;
  }

  public String getVendorKey() {
    return vendorKey;
  }

  public void setItemTotal(String itemTotal) {
    this.itemTotal = itemTotal;
  }

  public String getItemTotal(){
    return itemTotal;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }

  public void setRecordCount(String recordCount) {
    this.recordCount = recordCount;
  }

  public String getRecordCount() {
    return recordCount;
  }

  public void setFirstRecord(String firstRecord) {
    this.firstRecord = firstRecord;
  }

  public String getFirstRecord() {
    return firstRecord;
  }

  public void setLastRecord(String lastRecord) {
    this.lastRecord = lastRecord;
  }

  public String getLastRecord() {
    return lastRecord;
  }

  public void setPreviousPage(String previousPage ) {
    this.previousPage = previousPage;
  }

  public String getPreviousPage() {
    return previousPage;
  }

  public void setNextPage(String nextPage ) {
    this.nextPage = nextPage;
  }

  public String getNextPage() {
    return nextPage;
  }

  public void setPurchaseOrderItemForm(ArrayList purchaseOrderItemForm){
    this.purchaseOrderItemForm=purchaseOrderItemForm;
  }

  public ArrayList getPurchaseOrderItemForm(){
    return purchaseOrderItemForm;
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