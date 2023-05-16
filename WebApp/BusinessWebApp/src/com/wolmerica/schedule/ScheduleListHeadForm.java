/*
 * ScheduleListHeadForm.java
 *
 * Created on September 7, 2006, 10:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.schedule;

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

public class ScheduleListHeadForm extends MasterForm
{
  private String customerKeyFilter;
  private String clientName;
  private String fromDate;
  private String toDate;
  private String eventTypeKey;
  private String locationKey;
  private String statusKey;
  private String recordCount;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String currentPage;
  private String nextPage;
  private String workOrderItemTotal;
  private String workOrderServiceTotal;
  private ArrayList scheduleForm;
  private ArrayList permissionListForm;

  public void setCustomerKeyFilter(String customerKeyFilter) {
    this.customerKeyFilter = customerKeyFilter;
  }

  public String getCustomerKeyFilter() {
    return customerKeyFilter;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  public void setFromDate(String fromDate) {
    this.fromDate = fromDate;
  }

  public String getFromDate() {
    return fromDate;
  }

  public void setToDate(String toDate) {
    this.toDate = toDate;
  }

  public String getToDate() {
    return toDate;
  }

  public void setEventTypeKey(String eventTypeKey) {
    this.eventTypeKey = eventTypeKey;
  }

  public String getEventTypeKey() {
    return eventTypeKey;
  }

  public void setLocationKey(String locationKey) {
    this.locationKey = locationKey;
  }

  public String getLocationKey() {
    return locationKey;
  }

  public void setStatusKey(String statusKey) {
    this.statusKey = statusKey;
  }

  public String getStatusKey() {
    return statusKey;
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

  public void setCurrentPage(String currentPage ) {
    this.currentPage = currentPage;
  }

  public String getCurrentPage() {
    return currentPage;
  }

  public void setNextPage(String nextPage ) {
    this.nextPage = nextPage;
  }

  public String getNextPage() {
    return nextPage;
  }

  public void setWorkOrderItemTotal(String workOrderItemTotal) {
    this.workOrderItemTotal = workOrderItemTotal;
  }

  public String getWorkOrderItemTotal() {
    return workOrderItemTotal;
  }

  public void setWorkOrderServiceTotal(String workOrderServiceTotal) {
    this.workOrderServiceTotal = workOrderServiceTotal;
  }

  public String getWorkOrderServiceTotal() {
    return workOrderServiceTotal;
  }

  public void setScheduleForm(ArrayList scheduleList){
  this.scheduleForm=scheduleList;
  }

  public ArrayList getScheduleForm(){
  return scheduleForm;
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