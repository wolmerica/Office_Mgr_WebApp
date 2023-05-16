/*
 * CustomerInvoiceServiceGetAction.java
 *
 * Created on September 9, 2005, 8:14 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/27/2006 - Handle fragmented order_qty values from multiple vendor invoices.
 */

package com.wolmerica.customerinvoiceservice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.log4j.Logger;

public class CustomerInvoiceServiceGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

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

  
  private CustomerInvoiceServiceHeadDO getCustomerInvoiceService(HttpServletRequest request,
                                                                 Integer ciKey,
                                                                 Integer pageNo)
   throws Exception, SQLException {

    CustomerInvoiceServiceHeadDO formHDO = new CustomerInvoiceServiceHeadDO();
    CustomerInvoiceServiceDO customerInvoiceService = null;
    ArrayList<CustomerInvoiceServiceDO> customerInvoiceServices = new ArrayList<CustomerInvoiceServiceDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    boolean ciServiceTaxId = false;
    boolean ciiServiceTaxId = false;
    BigDecimal bdOrderQty = new BigDecimal("0");
    MathContext mc = new MathContext(5);

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT invoice_num,"
                   + "(service_tax_key - 50) AS service_tax_key,"
                   + "customerinvoice.active_id,"
                   + "customer.client_name,"
                   + "customerinvoice.scenario_key,"
                   + "customerinvoice.customertype_key,"
                   + "customerinvoice.thekey,"
                   + "customerinvoice.genesis_key "
                   + "FROM customerinvoice,customer "
                   + "WHERE customerinvoice.thekey=? "
                   + "AND customer_key = customer.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        formHDO.setInvoiceNumber(rs.getString("invoice_num"));
        if (rs.getInt("service_tax_key") >= 0)
          ciServiceTaxId = rs.getBoolean("service_tax_key");
        formHDO.setActiveId(rs.getBoolean("active_id"));
        formHDO.setClientName(rs.getString("client_name"));
        formHDO.setScenarioKey(rs.getByte("scenario_key"));
        formHDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        formHDO.setCreditId((rs.getInt("theKey") != rs.getInt("genesis_key")));
//--------------------------------------------------------------------------------
// The price and quantity adjustment logic was added later so we leveraged active id.
//--------------------------------------------------------------------------------
        if (rs.getByte("active_id") == 5)
          formHDO.setAdjustmentId(true);
      }
      else {
        throw new Exception("CustomerInvoice " + ciKey.toString() + " not found!");
      }
//--------------------------------------------------------------------------------
// Get the customer invoice service records for all the master records.
//--------------------------------------------------------------------------------
      query = "SELECT customerinvoiceservice.thekey,"
            + "customerinvoiceservice.customerinvoice_key,"
            + "customerinvoiceservice.servicedictionary_key,"
            + "customerinvoiceservice.pricetype_key,"
            + "pricetype.name AS pt_name,"
            + "servicedictionary.service_num AS sd_num,"
            + "servicedictionary.name AS sd_name,"
            + "customerinvoiceservice.available_qty,"
            + "customerinvoiceservice.order_qty AS order_qty,"
            + "customerinvoiceservice.available_qty - ABS(customerinvoiceservice.order_qty) AS remaining_qty,"
            + "customerinvoiceservice.service_price,"
            + "customerinvoiceservice.discount_rate,"
            + "customerinvoiceservice.promotion_rate,"
            + "customerinvoiceservice.service_tax_id,"
            + "customerinvoiceservice.note_line1 "
            + "FROM customerinvoiceservice, servicedictionary, pricetype "
            + "WHERE customerinvoice_key = ? "
            + "AND customerinvoiceservice.servicedictionary_key = servicedictionary.thekey "
            + "AND customerinvoiceservice.thekey = customerinvoiceservice.master_key "
            + "AND customerinvoiceservice.pricetype_key = pricetype.thekey "
            + "ORDER by customerinvoiceservice.thekey ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"customerinvoiceservice.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      Short minQty = 0;
      Short maxQty = 0;

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          customerInvoiceService = new CustomerInvoiceServiceDO();
          customerInvoiceService.setKey(rs.getInt("thekey"));
          customerInvoiceService.setCustomerInvoiceKey(ciKey);
          customerInvoiceService.setServiceDictionaryKey(rs.getInt("servicedictionary_key"));
          customerInvoiceService.setPriceTypeKey(rs.getByte("pricetype_key"));
          customerInvoiceService.setPriceTypeName(rs.getString("pt_name"));
          customerInvoiceService.setServiceNum(rs.getString("sd_num"));
          customerInvoiceService.setServiceName(rs.getString("sd_name"));
          customerInvoiceService.setAvailableQty(rs.getShort("available_qty"));
          customerInvoiceService.setOrderQty(rs.getShort("order_qty"));
          customerInvoiceService.setRemainingQty(rs.getShort("remaining_qty"));
          customerInvoiceService.setNoteLine1(rs.getString("note_line1"));

//--------------------------------------------------------------------------------
// Special condition of the customer invoice being enabled for service tax.
//--------------------------------------------------------------------------------
          customerInvoiceService.setEnableServiceTaxId(ciServiceTaxId);
          ciiServiceTaxId = rs.getBoolean("service_tax_id");
          ciiServiceTaxId = (ciiServiceTaxId && ciServiceTaxId);
          customerInvoiceService.setServiceTaxId(ciiServiceTaxId);

//--------------------------------------------------------------------------------
// 08/04/2006 Call the GetCustomerInvoiceServiceTotalsByCII stored procedure to compute values.
//--------------------------------------------------------------------------------
//  1) OrderQty              summation of order quantity for the master_key.
//  2) DiscountAmt           summation of discounts for the service.
//  2) ExtendPrice           final price after discounts.
//--------------------------------------------------------------------------------
          customerInvoiceService = getCustomerInvoiceServiceTotalsByMasterKey(conn,
                                                                              customerInvoiceService,
                                                                              ciKey);

          formHDO.setInvoiceTotal(formHDO.getInvoiceTotal().add(customerInvoiceService.getExtendPrice()));
          customerInvoiceServices.add(customerInvoiceService);
        }
      }
//--------------------------------------------------------------------------------
// Store selected customer invoice service the row list.
//--------------------------------------------------------------------------------
      formHDO.setCustomerInvoiceServiceForm(customerInvoiceServices);
//--------------------------------------------------------------------------------
// Pagination logic to figure out what the previous and next page
// values will be for the next screen to be displayed.
//--------------------------------------------------------------------------------
      Integer prevPage=0;
      Integer nextPage=0;
      if (recordCount > lastRecord)
        nextPage = pageNo + 1;
      else
        lastRecord = recordCount;
      if (firstRecord > 1)
        prevPage = pageNo - 1;
      if (recordCount == 0)
        firstRecord=0;
//--------------------------------------------------------------------------------
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setCustomerInvoiceKey(ciKey);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (customerInvoiceServices.isEmpty()) {
        customerInvoiceServices.add(new CustomerInvoiceServiceDO());
      }
      formHDO.setCustomerInvoiceServiceForm(customerInvoiceServices);
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
      if (rs2 != null) {
        try {
          rs2.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs2 = null;
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
    return formHDO;
  }

  public CustomerInvoiceServiceDO getCustomerInvoiceServiceTotalsByMasterKey(Connection conn,
                                                                             CustomerInvoiceServiceDO formDO,
                                                                             Integer ciKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerInvoiceServiceTotalsByCII(?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, ciKey);
      cStmt.setInt(2, formDO.getKey());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Retrieve the return values
// 09/30/2006 - Make sure the Order Quantity value is positive.
//--------------------------------------------------------------------------------
      formDO.setOrderQty(cStmt.getBigDecimal("orderQty").abs().shortValue());
      formDO.setThePrice(cStmt.getBigDecimal("servicePrice"));
      formDO.setDiscountRate(cStmt.getBigDecimal("discountRate"));
      formDO.setDiscountAmount(cStmt.getBigDecimal("discountAmt"));
      formDO.setExtendPrice(cStmt.getBigDecimal("extendAmt"));

//--------------------------------------------------------------------------------
// Debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": orderQty : " + cStmt.getShort("orderQty"));
      cat.debug(this.getClass().getName() + ": discountRate : " + cStmt.getBigDecimal("discountRate"));
      cat.debug(this.getClass().getName() + ": discountAmt : " + cStmt.getBigDecimal("discountAmt"));
      cat.debug(this.getClass().getName() + ": extendAmt : " + cStmt.getBigDecimal("extendAmt"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerInvoiceServiceTotalsByCII() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerInvoiceServiceTotalsByCII() " + e.getMessage());
      }
    }
    return formDO;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws Exception, IOException, SQLException, ServletException {

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

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }

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
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        CustomerInvoiceServiceHeadDO formHDO = getCustomerInvoiceService(request,
                                                                         theKey,
                                                                         pageNo);
        formHDO.setPermissionStatus(usToken);
//--------------------------------------------------------------------------------
// Make sure that only the active vendor invoice is editable.
//--------------------------------------------------------------------------------
        if (formHDO.getActiveId() && (usToken.compareToIgnoreCase(getUserStateService().getLocked()) == 0))
          request.setAttribute(getUserStateService().getDisableEdit(), false);
        else
          request.setAttribute(getUserStateService().getDisableEdit(), true);
        request.getSession().setAttribute("customerinvoiceserviceHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for customerinvoiceitem.
//--------------------------------------------------------------------------------
        CustomerInvoiceServiceHeadForm formHStr = new CustomerInvoiceServiceHeadForm();
        formHStr.populate(formHDO);

        form = formHStr;
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
