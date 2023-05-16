/*
 * WorkOrderLoadToPurchaseOrderAction.java
 *
 * Created on October 04, 2008 9:30 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.workorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class WorkOrderLoadToPurchaseOrderAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private WorkOrderDO getWorkOrderRow(HttpServletRequest request,
                                      Integer vendorKey,
                                      Integer woCnt)
    throws Exception {
         
    cat.debug(this.getClass().getName() + " a vendorKey : " + vendorKey);             
    WorkOrderListHeadDO formHDO = (WorkOrderListHeadDO) request.getSession().getAttribute("workorderHDO");    
//--------------------------------------------------------------------------------
// Traverse the work order object for the particular record.
//--------------------------------------------------------------------------------
    Integer recordCount = 0;      
    ArrayList woList = formHDO.getWorkOrderForm();
    WorkOrderDO workOrder = new WorkOrderDO();
    WorkOrderDO workOrderReturn = null;
    for (int j=0; j < woList.size(); j++) {
      workOrder = (WorkOrderDO) woList.get(j);
      cat.debug(this.getClass().getName() + " b: getVendorKey() : " + workOrder.getVendorKey());      
      if (vendorKey.intValue() == workOrder.getVendorKey().intValue()) {
        cat.debug(this.getClass().getName() + " c: recordCount=" + recordCount + " vs. woCnt=" + woCnt);     
        ++recordCount;
        if (recordCount == woCnt) {
          cat.debug(this.getClass().getName() + " d: recordCount=" + recordCount + " break");            
          workOrderReturn = workOrder;
          break;
        }    
      }  
    }  
    
    return workOrderReturn;
  }
  
    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {
//--------------------------------------------------------------------------------
// Default target to success
//--------------------------------------------------------------------------------
    String target = "success";

    EmployeesActionMapping employeesMapping =
      (EmployeesActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {
//--------------------------------------------------------------------------------
// The user is not logged in.
//--------------------------------------------------------------------------------
        target = "login";
        ActionMessages actionMessages = new ActionMessages();

        actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
          new ActionMessage("errors.login.required"));
//--------------------------------------------------------------------------------
// Report any ActionMessages we have discovered back to the original form.
//--------------------------------------------------------------------------------
        if (!actionMessages.isEmpty()) {
          saveMessages(request, actionMessages);
        }
//--------------------------------------------------------------------------------
// Forward to the request to the login screen.
//--------------------------------------------------------------------------------
        return (mapping.findForward(target));
      }
    }

    try {
//--------------------------------------------------------------------------------
// Purchase order key in which the work order will populate.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getAttribute("key") != null) {
        theKey = new Integer(request.getAttribute("key").toString());
      }
      else {
        if (request.getParameter("key") != null) {
          theKey = new Integer(request.getParameter("key"));
        }
        else {
          throw new Exception("Request getAttribute [key] not found!");
        }
      }

//--------------------------------------------------------------------------------
// Vendor key value to allow the creation of multiple purchase orders.
//--------------------------------------------------------------------------------
      Integer vendorKey = 0;
      if (request.getSession().getAttribute("VKEY") != null) {
        vendorKey = new Integer(request.getSession().getAttribute("VKEY").toString());
      }
            
//--------------------------------------------------------------------------------
// Work order count will identify the record that will be added to the customer invoice.
// The first record will be identified by one and we increment from that point.
//--------------------------------------------------------------------------------
      Integer woCnt = 1;
      if (request.getSession().getAttribute("WOCNT") == null) {
        request.getSession().setAttribute("WOCNT", woCnt.toString());
      }
      else {
        woCnt = new Integer(request.getSession().getAttribute("WOCNT").toString());
        woCnt = woCnt + 1;
        request.getSession().setAttribute("WOCNT", woCnt.toString());
      }      
      
//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

//--------------------------------------------------------------------------------
// Retreive a work order detail associated with a particular appointment.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": *P.O. Key....: " + theKey.toString());
      cat.debug(this.getClass().getName() + ": *Vendor Key..: " + vendorKey.toString());
      cat.debug(this.getClass().getName() + ": *W.O. Cnt....: " + woCnt.toString());

      WorkOrderDO formDO = getWorkOrderRow(request,
                                           vendorKey,
                                           woCnt);
      
//--------------------------------------------------------------------------------
// Assign the foward target according to the existence of bundle detail data.
// The default target is set to "success" so we redirect as per logic below.
//--------------------------------------------------------------------------------
      if (formDO != null) {
        if (formDO.getSourceTypeKey() == 3) {
          target = new String("item");
          request.setAttribute("pbiKey", formDO.getSourceKey().toString());
          request.setAttribute("orderQty", formDO.getOrderQty().toString());
        }
        if (formDO.getSourceTypeKey() == 6) {
          target = new String("service");
          request.setAttribute("pbsKey", formDO.getSourceKey().toString());
          request.setAttribute("orderQty", formDO.getOrderQty().toString());
        }
      }
      request.setAttribute("key", theKey);      
      cat.debug(this.getClass().getName() + ": " + target);
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
      target = "error";
      ActionMessages actionMessages = new ActionMessages();
      actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
        new ActionMessage("errors.database.error", e.getMessage()));
//--------------------------------------------------------------------------------
// Report any ActionMessages
//--------------------------------------------------------------------------------
      if (!actionMessages.isEmpty()) {
        saveMessages(request, actionMessages);
      }
    }
//--------------------------------------------------------------------------------
// Forward to the appropriate View
//--------------------------------------------------------------------------------
    return (mapping.findForward(target));
  }
}
