/*
 * VendorResultListForm.java
 *
 * Created on January 21, 2010, 9:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.vendorresult;

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

public class VendorResultListHeadForm extends MasterForm
{
  private String vendorKey;
  private String vendorName;
  private String customerKey;
  private String clientName;
  private String sourceTypeKey;
  private String sourceKey;
  private String attributeToName;
  private String fromDate;
  private String toDate;
  private String testDisplayType;
  private String xmlFileName;
  private String xmlImportName;
  private String attachmentCount;
  private String recordCount;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String nextPage;
  private ArrayList vendorResultListForm;
  private ArrayList permissionListForm;

  public void setVendorKey(String vendorKey) {
    this.vendorKey = vendorKey;
  }

  public String getVendorKey() {
    return vendorKey;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setCustomerKey(String customerKey) {
    this.customerKey = customerKey;
  }

  public String getCustomerKey() {
    return customerKey;
  }
  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
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
  public void setAttributeToName(String attributeToName) {
    this.attributeToName = attributeToName;
  }

  public String getAttributeToName() {
    return attributeToName;
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

  public void setTestDisplayType(String testDisplayType) {
    this.testDisplayType = testDisplayType;
  }

  public String getTestDisplayType() {
    return testDisplayType;
  }

  public void setXmlFileName(String xmlFileName) {
    this.xmlFileName = xmlFileName;
  }

  public String getXmlFileName() {
    return xmlFileName;
  }
 
  public void setXmlImportName(String xmlImportName) {
    this.xmlImportName = xmlImportName;
  }

  public String getXmlImportName() {
    return xmlImportName;
  }

  public void setAttachmentCount(String attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public String getAttachmentCount() {
    return attachmentCount;
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

  public void setVendorResultListForm(ArrayList vendorResultList){
  this.vendorResultListForm=vendorResultList;
  }

  public ArrayList getVendorResultListForm(){
  return vendorResultListForm;
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