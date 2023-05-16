/*
 * CustomerInvoiceEntryAction.java
 *
 * Created on December 4, 2005, 1:37 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.customerinvoice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.customertype.CustomerTypeDropList;
import com.wolmerica.schedule.ScheduleDO;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.taxmarkup.TaxMarkUpDropList;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.util.common.EnumCustomerInvoiceScenario;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class CustomerInvoiceEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private CustomerInvoiceDO buildCustomerInvoiceForm(HttpServletRequest request,
                                                     Integer viKey)
   throws Exception, SQLException {

    CustomerInvoiceDO formDO = new CustomerInvoiceDO();
    ArrayList customerTypeRows = new ArrayList();
    ArrayList taxMarkUpRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = null;

      if (viKey >= 0) {
//===========================================================================
// Retrieve the vendor and ship-to information from the purchase order
// associated with the entered vendor invoice key.
//===========================================================================
        query = "SELECT purchaseorder.thekey AS pokey,"
              + "vendorinvoice.thekey AS vikey,"
              + "purchaseorder.vendor_key,"
              + "purchaseorder.customer_key,"
              + "customer.client_name,"
              + "customer.customertype_key,"
              + "customer.clinic_id,"
              + "vendor.name,"
              + "customertype.attribute_to_entity,"                    
              + "purchaseorder.sourcetype_key,"
              + "purchaseorder.source_key,"              
              + "packaging,"
              + "freight,"
              + "miscellaneous "
              + "FROM purchaseorder,vendorinvoice,vendor,customer,customertype "
              + "WHERE vendorinvoice.thekey=? "
              + "AND purchaseorder_key = purchaseorder.thekey "
              + "AND purchaseorder.vendor_key = vendor.thekey "              
              + "AND purchaseorder.customer_key = customer.thekey "
              + "AND customer.customertype_key = customertype.thekey";
        ps = conn.prepareStatement(query);
        cat.debug(this.getClass().getName() + ": Query #1 = " + query);
        ps.setInt(1, viKey);
        rs = ps.executeQuery();

        if (rs.next()) {
          formDO.setVendorInvoiceKey(viKey);
          formDO.setCustomerKey(rs.getInt("customer_key"));
          formDO.setClientName(rs.getString("client_name"));
          formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
//--------------------------------------------------------------------------------
// The scenario key is set inside the "CustomerInvoiceAddAction" module but we
// need to initialize the value to accommodate certiain user interface behavior.
//--------------------------------------------------------------------------------
          if (rs.getBoolean("clinic_id"))
            formDO.setScenarioKey(new Byte(EnumCustomerInvoiceScenario.ShipToOffice.getValue()));
          else
            formDO.setScenarioKey(new Byte(EnumCustomerInvoiceScenario.DirectShip.getValue()));
          formDO.setVendorName(rs.getString("name"));
          formDO.setAttributeToEntity(rs.getString("attribute_to_entity"));
          formDO.setSourceTypeKey(rs.getByte("sourcetype_key"));
          formDO.setSourceKey(rs.getInt("source_key"));          
          formDO.setPackagingCost(rs.getBigDecimal("packaging"));
          formDO.setFreightCost(rs.getBigDecimal("freight"));
          formDO.setMiscellaneousCost(rs.getBigDecimal("miscellaneous"));
          
//--------------------------------------------------------------------------------
// The source type key value is based on the FeatureResources.properties file.
//--------------------------------------------------------------------------------
          if (formDO.getSourceTypeKey().compareTo(new Byte("-1")) > 0) {
//--------------------------------------------------------------------------------
// Retrieve the pet, system, or vehicle attributed to this invoice.
//--------------------------------------------------------------------------------
            HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                      formDO.getSourceTypeKey(),
                                                      formDO.getSourceKey());
            formDO.setAttributeToName(nameMap.get("attributeToName").toString());
          }          
        }
        else {
          throw new Exception("VendorInvoice  " + viKey.toString() + " not found!");
        }
      }
//--------------------------------------------------------------------------------
// Retrieve the active customer type values available to assign to a customer.
//--------------------------------------------------------------------------------
      CustomerTypeDropList ctdl = new CustomerTypeDropList();
      customerTypeRows = ctdl.getActiveCustomerTypeList(conn);
      formDO.setCustomerTypeForm(customerTypeRows);
//--------------------------------------------------------------------------------
// Retrieve the tax mark-up values available.
//--------------------------------------------------------------------------------
      TaxMarkUpDropList tmudl = new TaxMarkUpDropList();
      taxMarkUpRows = tmudl.getSalesTaxMarkUpList(conn);
      formDO.setTaxMarkUpForm(taxMarkUpRows);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
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

  private CustomerInvoiceDO buildCustomerInvoiceFromSchedule(HttpServletRequest request,
                                                             CustomerInvoiceDO formDO)
   throws Exception, SQLException {

      ScheduleDO scheduleRow = (ScheduleDO) request.getSession().getAttribute("schedule");

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      String startDate = "";
      try {      
        DateFormatter dateFormatter = new DateFormatter();
        startDate = dateFormatter.format(scheduleRow.getStartDate());  
        }
      catch (FormattingException fe) {
            fe.getMessage();
      }      
      
      formDO.setCustomerKey(scheduleRow.getCustomerKey());
      formDO.setCustomerTypeKey(scheduleRow.getCustomerTypeKey());
      formDO.setClientName(scheduleRow.getClientName());
      formDO.setSourceTypeKey(scheduleRow.getSourceTypeKey());
      formDO.setSourceKey(scheduleRow.getSourceKey());
      formDO.setAttributeToName(scheduleRow.getAttributeToName());
      formDO.setNoteLine1(startDate + " " + scheduleRow.getSubject());
      formDO.setScheduleKey(scheduleRow.getKey());

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
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

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
// A non-negative vendor invoice key indicates we have an associated P.O.
//--------------------------------------------------------------------------------
      CustomerInvoiceDO formDO = null;
      formDO = buildCustomerInvoiceForm(request, theKey);
      formDO.setPermissionStatus(usToken);

//--------------------------------------------------------------------------------
// Leverage details from a schedule object in the session to populate values.
//--------------------------------------------------------------------------------
      if (request.getSession().getAttribute("schedule") != null) {
        formDO = buildCustomerInvoiceFromSchedule(request, formDO);
      }

//--------------------------------------------------------------------------------
// Transfer bean values back to the form as formatted strings.
//--------------------------------------------------------------------------------
      try {
        CustomerInvoiceForm formStr = new CustomerInvoiceForm();
        request.getSession().setAttribute("customerinvoice", formDO);
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