/*
 * CustomerInvoiceGetAction.java
 *
 * Created on October 23, 2005, 9:26 PM
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
import com.wolmerica.customertype.CustomerTypeDO;
import com.wolmerica.customertype.CustomerTypeDropList;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.customerinvoice.CustomerInvoiceService;
import com.wolmerica.service.customerinvoice.DefaultCustomerInvoiceService;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.logistic.LogisticService;
import com.wolmerica.service.logistic.DefaultLogisticService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.taxmarkup.TaxMarkUpDO;
import com.wolmerica.taxmarkup.TaxMarkUpDropList;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class CustomerInvoiceGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP"); 

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private CustomerService customerService = new DefaultCustomerService();
  private CustomerInvoiceService customerInvoiceService = new DefaultCustomerInvoiceService();
  private LogisticService logisticService = new DefaultLogisticService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService vendorService = new DefaultVendorService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public CustomerService getCustomerService() {
      return customerService;
  }

  public void setCustomerService(CustomerService customerService) {
      this.customerService = customerService;
  }

  public CustomerInvoiceService getCustomerInvoiceService() {
      return customerInvoiceService;
  }

  public void setCustomerInvoiceService(CustomerInvoiceService customerInvoiceService) {
      this.customerInvoiceService = customerInvoiceService;
  }

  public LogisticService getLogisticService() {
      return logisticService;
  }

  public void setLogisticService(LogisticService logisticService) {
      this.logisticService = logisticService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public VendorService getVendorService() {
      return vendorService;
  }

  public void setVendorService(VendorService vendorService) {
      this.vendorService = vendorService;
  }
  
  
  private CustomerInvoiceDO buildCustomerInvoiceForm(HttpServletRequest request,
                                                     Integer ciKey)
   throws Exception, SQLException {

    CustomerInvoiceDO formDO = null;
    CustomerTypeDO customerTypeRow = null;
    ArrayList customerTypeRows = new ArrayList();
    TaxMarkUpDO taxMarkUpRow = null;
    ArrayList taxMarkUpRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT ci.thekey,"
                   + "ci.vendorinvoice_key,"
                   + "ci.customer_key,"
                   + "c.client_name,"
                   + "ci.invoice_num,"
                   + "ci.scenario_key,"
                   + "ci.customertype_key,"
                   + "ci.note_line1,"
                   + "ci.note_line2,"
                   + "ci.note_line3,"
                   + "ci.item_total,"
                   + "ci.sub_total,"
                   + "ci.sales_tax_key,"
                   + "ci.sales_tax_rate,"
                   + "ci.service_tax_key,"
                   + "ci.service_tax_rate,"
                   + "ci.active_id,"
                   + "ci.genesis_key,"
                   + "ct.attribute_to_entity,"
                   + "ci.sourcetype_key,"
                   + "ci.source_key,"
                   + "ci.create_user,"
                   + "ci.create_stamp,"
                   + "ci.update_user,"
                   + "ci.update_stamp "
                   + "FROM customerinvoice ci, customer c, customertype ct "
                   + "WHERE ci.thekey=? "
                   + "AND ci.customer_key = c.thekey "
                   + "AND ci.customertype_key = ct.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        formDO = new CustomerInvoiceDO();
        formDO.setKey(rs.getInt("thekey"));
        formDO.setVendorInvoiceKey(rs.getInt("vendorinvoice_key"));
        formDO.setCustomerKey(rs.getInt("customer_key"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setScenarioKey(rs.getByte("scenario_key"));
        formDO.setCustomerInvoiceNumber(rs.getString("invoice_num"));
        formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        formDO.setNoteLine1(rs.getString("note_line1"));
        formDO.setNoteLine2(rs.getString("note_line2"));
        formDO.setNoteLine3(rs.getString("note_line3"));
        formDO.setSubTotal(rs.getBigDecimal("sub_total"));
        formDO.setSalesTaxKey(rs.getByte("sales_tax_key"));
        formDO.setSalesTaxRate(rs.getBigDecimal("sales_tax_rate"));
        formDO.setServiceTaxKey(rs.getByte("service_tax_key"));
        formDO.setServiceTaxRate(rs.getBigDecimal("service_tax_rate"));
        formDO.setActiveId(rs.getBoolean("active_id"));
        formDO.setGenesisKey(rs.getInt("genesis_key"));
        formDO.setAttributeToEntity(rs.getString("attribute_to_entity"));
        formDO.setSourceTypeKey(rs.getByte("sourcetype_key"));
        formDO.setSourceKey(rs.getInt("source_key"));
        formDO.setCreditId(!(formDO.getKey().equals(formDO.getGenesisKey())));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));
//--------------------------------------------------------------------------------
// The price adjustment logic was added later so we leveraged active id.
//--------------------------------------------------------------------------------
        if (rs.getByte("active_id") == 5) {
          formDO.setAdjustmentId(true);
        }
        else {
          if (!(formDO.getActiveId()) && !(formDO.getCreditId())) {
	    formDO.setAllowAdjustmentId(getCustomerService().setAdjustmentIndicator(conn,
                              formDO.getKey(),
                              request.getSession().getAttribute("USERNAME").toString(),
                              false));
	  }
        }  
//--------------------------------------------------------------------------------
// Retrieve the pet, system, or vehicle attributed to this invoice.
//--------------------------------------------------------------------------------
        if (formDO.getSourceTypeKey().compareTo(new Byte("-1")) > 0) {
          HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                    formDO.getSourceTypeKey(),
                                                    formDO.getSourceKey());
          formDO.setAttributeToName(nameMap.get("attributeToName").toString());
        }
//--------------------------------------------------------------------------------
// Retrieve the vendor name associated with the original P.O. if it exists.
//--------------------------------------------------------------------------------
        formDO.setVendorName(getVendorService().getVendorNameForVI(conn,
                                                     formDO.getVendorInvoiceKey()));
 
//--------------------------------------------------------------------------------
// 08/04/2006 Call the GetCustomerInvoiceTotalsByCI stored procedure to compute values.
//--------------------------------------------------------------------------------
//  1) LineItemTotal()       summation of line items price before discounts.
//  2) OriginalProfitTotal() profit calculations with pre discounted amounts.
//  3) DiscountTotal()       summation of the item discounts.
//  4) SubTotal()            summation of line items price after discounts.
//  5) FinalProfitTotal()    profit calculations with post discounted amounts.
//  6) TaxableTotal()        summation of the discounted items that are taxable.
//  Update the customerinvoice with the calculated totals for active invoices.        
//--------------------------------------------------------------------------------
        formDO = getCustomerInvoiceService().getCustomerInvoiceTotalsByCiKey(conn,
                                                                             formDO,
                                                                             ciKey);

//--------------------------------------------------------------------------------
// Evaluate the state of non-active and non-credit orders to find out if a credit
// invoice is permissable for the order base outstanding item and service items.
//--------------------------------------------------------------------------------
        formDO = getCustomerInvoiceService().getCustomerInvoiceAllowCredit(conn,
                                                                           formDO);
        
//--------------------------------------------------------------------------------
// Get the logistics record count for the vendor invoice.
//--------------------------------------------------------------------------------
        formDO.setLogisticsCount(getLogisticService().getLogisticsCount(conn,
                                                      getUserStateService().getFeatureKey().byteValue(),
                                                      formDO.getKey()));
      }
      else {
        throw new Exception("CustomerInvoice  " + ciKey.toString() + " not found!");
      }
//-----------------------------------------------------------------------------
// Retrieve the active customer type values available to assign to a customer.
//-----------------------------------------------------------------------------
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
      if (request.getAttribute("key") != null) {
        theKey = new Integer(request.getAttribute("key").toString());
      }
      else {      
        if (request.getParameter("key") != null) {
          theKey = new Integer(request.getParameter("key"));
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
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        CustomerInvoiceDO formDO = buildCustomerInvoiceForm(request, theKey);
        formDO.setPermissionStatus(usToken);
//--------------------------------------------------------------------------------
// Make sure that only the active vendor invoice is editable.
//--------------------------------------------------------------------------------
        if (formDO.getActiveId() && (usToken.compareToIgnoreCase(getUserStateService().getLocked()) == 0))
          request.setAttribute(getUserStateService().getDisableEdit(), false);
        else
          request.setAttribute(getUserStateService().getDisableEdit(), true);
        if (!(request.getParameter("creditKey") == null)) {
          formDO = getCustomerInvoiceService().creditCustomerInvoiceForm(formDO);
        }
        request.getSession().setAttribute("customerinvoice", formDO);
        CustomerInvoiceForm formStr = new CustomerInvoiceForm();
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