/*
 * PurchaseOrderEntryAction.java
 *
 * Created on September 9, 2005, 7:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/23/2005 Implement tools.formatter library.
 */

package com.wolmerica.purchaseorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
import com.wolmerica.schedule.ScheduleDO;
import com.wolmerica.tools.formatter.FormattingException;

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
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class PurchaseOrderEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private CustomerService CustomerService = new DefaultCustomerService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public CustomerService getCustomerService() {
      return CustomerService;
  }

  public void setCustomerService(CustomerService CustomerService) {
      this.CustomerService = CustomerService;
  }

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }
  
  private PurchaseOrderDO buildPurchaseOrderFromSchedule(HttpServletRequest request,
                                                         PurchaseOrderDO formDO,
                                                         Integer vendorKey)
   throws Exception {

    ScheduleDO scheduleRow = (ScheduleDO) request.getSession().getAttribute("schedule");
    
    formDO.setVendorKey(vendorKey);
    formDO.setCustomerKey(scheduleRow.getCustomerKey());
    formDO.setSourceTypeKey(scheduleRow.getSourceTypeKey());
    formDO.setSourceKey(scheduleRow.getSourceKey());
    formDO.setScheduleKey(scheduleRow.getKey());

    return formDO;
  }

  private PurchaseOrderDO buildPurchaseOrderFromVendorResult(HttpServletRequest request,
                                                             PurchaseOrderDO formDO,
                                                             Integer vendorKey,                                                           
                                                             Byte sourceTypeKey,
                                                             Integer sourceKey)
   throws Exception {

    DataSource ds = null;
    Connection conn = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      formDO.setVendorKey(vendorKey);
      formDO.setSourceTypeKey(sourceTypeKey);
      formDO.setSourceKey(sourceKey);
      formDO.setVendorName(getVendorService().getVendorName(conn, vendorKey));

      HashMap atkMap  = getAttributeToService().getAttributeToName(conn, sourceTypeKey, sourceKey);
      formDO.setAttributeToEntity(atkMap.get("sourceName").toString());
      formDO.setAttributeToName(atkMap.get("attributeToName").toString());
      formDO.setCustomerKey(new Integer(atkMap.get("customerKey").toString()));

      formDO.setClientName(getCustomerService().getClientName(conn, formDO.getCustomerKey()));

      formDO.setNoteLine1(getPropertyService().getCustomerProperties(request,"purchaseorder.orphan.message"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }

    return formDO;
  }


    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

    // Default target to success
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
// The vendor key is a required value when the P.O. is initiated from an event.
//--------------------------------------------------------------------------------        
      Integer vendorKey = null;
      Byte sourceTypeKey = null;
      Integer sourceKey = null;

      if (request.getSession().getAttribute("schedule") != null) {      
        if (request.getSession().getAttribute("VKEY") != null) {
          vendorKey = new Integer(request.getSession().getAttribute("VKEY").toString());
        }
        else {
          throw new Exception("Request getSession().getAttribute [VKEY] not found!");
        }      
        cat.debug(this.getClass().getName() + ": get[VKEY] = " + vendorKey.toString());        
      }

      if (request.getParameter("vendorKey") != null) {
        if (!(request.getParameter("vendorKey").equalsIgnoreCase(""))) {
          vendorKey = new Integer(request.getParameter("vendorKey"));
        }
      }

      if (request.getParameter("sourceTypeKey") != null) {
        if (!(request.getParameter("sourceTypeKey").equalsIgnoreCase("")))
          sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }

      if (request.getParameter("sourceKey") != null) {
        if (!(request.getParameter("sourceKey").equalsIgnoreCase("")))
          sourceKey = new Integer(request.getParameter("sourceKey"));
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        PurchaseOrderDO formDO = new PurchaseOrderDO();
        formDO.setPermissionStatus(usToken);

//--------------------------------------------------------------------------------
// Leverage details from a schedule object in the session to populate values.
//--------------------------------------------------------------------------------
        if (request.getParameter("sourceKey") != null) {
          cat.debug("vendorKey.....: " + vendorKey);
          cat.debug("sourceTypeKey.: " + sourceTypeKey);
          cat.debug("sourceKey.....: " + sourceKey);
          formDO = buildPurchaseOrderFromVendorResult(request,
                                                      formDO,
                                                      vendorKey,
                                                      sourceTypeKey,
                                                      sourceKey);
        } else {
//--------------------------------------------------------------------------------
// Leverage details from a schedule object in the session to populate values.
//--------------------------------------------------------------------------------
          if (request.getSession().getAttribute("schedule") != null) {
            formDO = buildPurchaseOrderFromSchedule(request,
                                                    formDO,
                                                    vendorKey);
          }
        }

        request.getSession().setAttribute("purchaseorder", formDO);
        PurchaseOrderForm formStr = new PurchaseOrderForm();
        formStr.populate(formDO);

        form = formStr;
      }
      catch (FormattingException fe) {
        fe.getMessage();
      }
 
      if ( form == null ) {
        cat.debug(this.getClass().getName() + ":---->form is null<----");
      }
      if ("request".equals(mapping.getScope())) {
        cat.debug(this.getClass().getName() + ":---->request.setAttribute<----");
        request.setAttribute(mapping.getAttribute(), form);
      }
      else {
        cat.debug(this.getClass().getName() + ":---->session.setAttribute<----");
        request.getSession().setAttribute(mapping.getAttribute(), form);
      }
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