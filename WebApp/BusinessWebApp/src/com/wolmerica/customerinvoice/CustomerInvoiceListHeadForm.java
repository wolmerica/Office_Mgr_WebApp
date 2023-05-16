/*
 * CustomerInvoiceListHeadForm.java
 *
 * Created on February 12, 2006, 4:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoice;

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

public class CustomerInvoiceListHeadForm extends MasterForm {

  private String purchaseOrderKey;
  private String vendorInvoiceKey;
  private String genesisKey;
  private String vendorKey;
  private String vendorName;
  private String customerKey;
  private String clientName;
  private String scenarioKey;
  private String moreToDistribute;
  private String internalUse;
  private String clientNameFilter;
  private String invoiceNumberFilter;
  private String itemKeyFilter;
  private String serviceKeyFilter;
  private String recordCount;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String nextPage;
  private ArrayList customerInvoiceListForm;
  private ArrayList permissionListForm;


  public void setPurchaseOrderKey(String purchaseOrderKey) {
    this.purchaseOrderKey = purchaseOrderKey;
  }

  public String getPurchaseOrderKey() {
    return purchaseOrderKey;
  }

  public void setVendorInvoiceKey(String vendorInvoiceKey) {
    this.vendorInvoiceKey = vendorInvoiceKey;
  }

  public String getVendorInvoiceKey() {
    return vendorInvoiceKey;
  }

  public void setGenesisKey(String genesisKey) {
    this.genesisKey = genesisKey;
  }

  public String getGenesisKey() {
    return genesisKey;
  }

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

  public void setScenarioKey(String scenarioKey) {
    this.scenarioKey = scenarioKey;
  }

  public String getScenarioKey() {
    return scenarioKey;
  }

  public void setMoreToDistribute(String moreToDistribute) {
    this.moreToDistribute = moreToDistribute;
  }

  public String getMoreToDistribute() {
    return moreToDistribute;
  }

  public void setInternalUse(String internalUse) {
    this.internalUse = internalUse;
  }

  public String getInternalUse() {
    return internalUse;
  }
  
  public void setClientNameFilter(String clientNameFilter) {
    this.clientNameFilter = clientNameFilter;
  }

  public String getClientNameFilter() {
    return clientNameFilter;
  }

  public void setInvoiceNumberFilter(String invoiceNumberFilter) {
    this.invoiceNumberFilter = invoiceNumberFilter;
  }

  public String getInvoiceNumberFilter() {
    return invoiceNumberFilter;
  }

  public void setItemKeyFilter(String itemKeyFilter) {
    this.itemKeyFilter = itemKeyFilter;
  }

  public String getItemKeyFilter() {
    return itemKeyFilter;
  }  
  
  public void setServiceKeyFilter(String serviceKeyFilter) {
    this.serviceKeyFilter = serviceKeyFilter;
  }

  public String getServiceKeyFilter() {
    return serviceKeyFilter;
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

  public void setCustomerInvoiceListForm(ArrayList customerInvoiceListForm){
    this.customerInvoiceListForm=customerInvoiceListForm;
  }

  public ArrayList getCustomerInvoiceListForm(){
    return customerInvoiceListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
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

