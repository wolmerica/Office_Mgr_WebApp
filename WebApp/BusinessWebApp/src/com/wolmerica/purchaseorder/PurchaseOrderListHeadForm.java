/*
 * PurchaseOrderListForm.java
 *
 * Created on March 08, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.purchaseorder;

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
import java.util.ArrayList;

public class PurchaseOrderListHeadForm extends MasterForm
{
  private String vendorNameFilter;
  private String orderNumberFilter;
  private String itemKeyFilter;
  private String scheduleKeyFilter;
  private String recordCount;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String nextPage;
  private ArrayList purchaseOrderListForm;
  private ArrayList permissionListForm;

  public void setVendorNameFilter(String vendorNameFilter) {
    this.vendorNameFilter = vendorNameFilter;
  }

  public String getVendorNameFilter() {
    return vendorNameFilter;
  }

  public void setOrderNumberFilter(String orderNumberFilter) {
    this.orderNumberFilter = orderNumberFilter;
  }

  public String getOrderNumberFilter() {
    return orderNumberFilter;
  }
  
  public void setItemKeyFilter(String itemKeyFilter) {
    this.itemKeyFilter = itemKeyFilter;
  }

  public String getItemKeyFilter() {
    return itemKeyFilter;
  }  

  public void setScheduleKeyFilter(String scheduleKeyFilter) {
    this.scheduleKeyFilter = scheduleKeyFilter;
  }

  public String getScheduleKeyFilter() {
    return scheduleKeyFilter;
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

  public void setPurchaseOrderListForm(ArrayList purchaseOrderList){
  this.purchaseOrderListForm=purchaseOrderList;
  }

  public ArrayList getPurchaseOrderListForm(){
  return purchaseOrderListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
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

    // Set the disableEdit attribute to false when errors encountered.
    request.setAttribute("disableEdit", false);

    return errors;
  }
}