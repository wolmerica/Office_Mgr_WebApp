/*
 * CustomerInvoiceReportListAction.java
 *
 * Created on February 16, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.customerinvoicereport;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.customerinvoice.CustomerInvoiceDO;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.customerinvoice.CustomerInvoiceService;
import com.wolmerica.service.customerinvoice.DefaultCustomerInvoiceService;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;
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
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

public class CustomerInvoiceReportListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");


  private AttributeToService attributeToService = new DefaultAttributeToService();
  private CustomerService customerService = new DefaultCustomerService();
  private CustomerInvoiceService customerInvoiceService = new DefaultCustomerInvoiceService();
  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private UserStateService userStateService = new DefaultUserStateService();

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

  public DateRangeService getDateRangeService() {
      return dateRangeService;
  }

  public void setDateRangeService(DateRangeService dateRangeService) {
      this.dateRangeService = dateRangeService;
  }
  
  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private CustomerInvoiceReportListHeadDO getCIReportList(HttpServletRequest request,
                                                          Integer customerKey,
                                                          Byte sourceTypeKey,
                                                          Integer sourceKey,
                                                          String fromDate,
                                                          String toDate)
   throws Exception, SQLException {

    CustomerInvoiceReportListHeadDO formHDO = new CustomerInvoiceReportListHeadDO();
    CustomerInvoiceReportDO customerInvoiceReportRow = null;
    CustomerInvoiceDO customerInvoiceRow = new CustomerInvoiceDO();
    ArrayList<CustomerInvoiceReportDO> customerInvoiceReportRows = new ArrayList<CustomerInvoiceReportDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

//--------------------------------------------------------------------------------
// Get the client name value when the customer key has a value.
//--------------------------------------------------------------------------------
      if (customerKey != null) {
        formHDO.setCustomerKey(customerKey);
        formHDO.setClientName(getCustomerService().getClientName(conn, customerKey));
      }

//--------------------------------------------------------------------------------
// Get the attribute to name value when the source type and source key have a value.
//--------------------------------------------------------------------------------
      HashMap nameMap = null;
      if (sourceKey != null) {
        formHDO.setSourceTypeKey(sourceTypeKey);
        formHDO.setSourceKey(sourceKey);
        nameMap = getAttributeToService().getAttributeToName(conn,
                                          sourceTypeKey,
                                          sourceKey);
        formHDO.setAttributeToName(nameMap.get("attributeToName").toString());
      }

//--------------------------------------------------------------------------------
// Retrieve the customer invoice records to cooresponds to the input parameters of
// customer, attribute to, from date, and to date.
//--------------------------------------------------------------------------------
      String query = "SELECT customerinvoice.thekey AS customerinvoice_key,"
                   + "customerinvoice.customer_key,"
                   + "customer.client_name,"
                   + "customerinvoice.sourcetype_key,"
                   + "customerinvoice.source_key,"
                   + "DATE(customerinvoice.create_stamp) AS service_date,"
                   + "customerinvoice.invoice_num,"
                   + "customerinvoice.item_total,"
                   + "customerinvoice.sales_tax_rate,"
                   + "customerinvoice.sales_tax,"
                   + "customerinvoice.sub_total - customerinvoice.item_total AS service_total,"
                   + "customerinvoice.service_tax_rate,"
                   + "customerinvoice.service_tax,"
                   + "customerinvoice.debit_adjustment,"
                   + "customerinvoice.packaging,"
                   + "customerinvoice.freight,"
                   + "customerinvoice.miscellaneous,"
                   + "(customerinvoice.packaging+customerinvoice.freight+customerinvoice.miscellaneous) AS handling_cost,"
                   + "customerinvoice.credit_adjustment,"
                   + "customerinvoice.invoice_total,"
                   + "customerinvoice.note_line1,"
                   + "customerinvoice.note_line2,"
                   + "customerinvoice.note_line3 "
                   + "FROM customerinvoice, customer "
                   + "WHERE customer_key = customer.thekey "
                   + "AND DATE(customerinvoice.create_stamp) BETWEEN ? AND ?";
      if (customerKey != null) {
        query = query + " AND customer_key = ?";
        if (sourceTypeKey != null) {
          query = query + " AND sourcetype_key = ?";
          if (sourceKey != null) {
            query = query + " AND source_key = ?";
          }
        }
      }
      else {
        query = query + " AND customer_key IN (SELECT thekey "
                                           + "FROM customer "
                                           + "WHERE NOT clinic_id)";
      }
      query = query + " ORDER BY customerinvoice_key";
      cat.debug(this.getClass().getName() + query);
      ps = conn.prepareStatement(query);

      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      if (customerKey != null) {
        ps.setInt(3, customerKey);
        if (sourceTypeKey != null) {
          ps.setInt(4, sourceTypeKey);
          if (sourceKey != null) {
            ps.setInt(5, sourceKey);
	      }
        }
      }
      rs = ps.executeQuery();

      Short recordCount = 0;
      Short firstRecord = 1;
      while ( rs.next() ) {
        ++recordCount;

        customerInvoiceReportRow = new CustomerInvoiceReportDO();
        customerInvoiceReportRow.setCustomerInvoiceKey(rs.getInt("customerinvoice_key"));
        customerInvoiceReportRow.setCustomerKey(rs.getInt("customer_key"));
        customerInvoiceReportRow.setClientName(rs.getString("client_name"));
        customerInvoiceReportRow.setDateOfService(rs.getDate("service_date"));
        customerInvoiceReportRow.setCustomerInvoiceNumber(rs.getString("invoice_num"));
        customerInvoiceReportRow.setItemNetAmount(rs.getBigDecimal("item_total"));
        customerInvoiceReportRow.setSalesTaxCost(rs.getBigDecimal("sales_tax"));
        customerInvoiceReportRow.setServiceNetAmount(rs.getBigDecimal("service_total"));
        customerInvoiceReportRow.setServiceTaxCost(rs.getBigDecimal("service_tax"));
        customerInvoiceReportRow.setDebitAdjustmentAmt(rs.getBigDecimal("debit_adjustment"));
        customerInvoiceReportRow.setPackagingCost(rs.getBigDecimal("packaging"));
        customerInvoiceReportRow.setFreightCost(rs.getBigDecimal("freight"));
        customerInvoiceReportRow.setMiscellaneousCost(rs.getBigDecimal("miscellaneous"));
        customerInvoiceReportRow.setHandlingCost(rs.getBigDecimal("handling_cost"));
        customerInvoiceReportRow.setInvoiceTotal(rs.getBigDecimal("invoice_total"));
        customerInvoiceReportRow.setCreditAdjustmentAmt(rs.getBigDecimal("credit_adjustment"));
        customerInvoiceReportRow.setNoteLine1(rs.getString("note_line1"));
        customerInvoiceReportRow.setNoteLine2(rs.getString("note_line2"));
        customerInvoiceReportRow.setNoteLine3(rs.getString("note_line3"));

        nameMap = getAttributeToService().getAttributeToName(conn,
                                          rs.getByte("sourcetype_key"),
                                          rs.getInt("source_key"));
        customerInvoiceReportRow.setAttributeToName(nameMap.get("attributeToName").toString());

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
        customerInvoiceRow.setSalesTaxRate(rs.getBigDecimal("sales_tax_rate"));
        customerInvoiceRow.setServiceTaxRate(rs.getBigDecimal("service_tax_rate"));
        customerInvoiceRow = getCustomerInvoiceService().getCustomerInvoiceTotalsByCiKey(conn,
                                                                                         customerInvoiceRow,
                                                                                         customerInvoiceReportRow.getCustomerInvoiceKey());
        //customerInvoiceReportRow.setItemNetAmount(customerInvoiceRow.getItemNetAmount());
        customerInvoiceReportRow.setItemDiscountAmount(customerInvoiceRow.getItemDiscountAmount());
        //customerInvoiceReportRow.setServiceNetAmount(customerInvoiceRow.getServiceNetAmount());
        customerInvoiceReportRow.setServiceDiscountAmount(customerInvoiceRow.getServiceDiscountAmount());
        customerInvoiceReportRow.setGrossProfitAmount(customerInvoiceRow.getGrossProfitAmount());
        customerInvoiceReportRow.setNetProfitAmount(customerInvoiceRow.getNetProfitAmount());
        customerInvoiceReportRow.setCostBasisTotal(customerInvoiceReportRow.getInvoiceTotal().subtract(customerInvoiceReportRow.getNetProfitAmount().add(customerInvoiceReportRow.getSalesTaxCost().add(customerInvoiceReportRow.getServiceTaxCost()))));

        customerInvoiceReportRows.add(customerInvoiceReportRow);

//--------------------------------------------------------------------------------
// Set the header totals with all detail value summations.
//--------------------------------------------------------------------------------
        formHDO.setItemNetAmount(formHDO.getItemNetAmount().add(customerInvoiceReportRow.getItemNetAmount()));
        formHDO.setItemDiscountAmount(formHDO.getItemDiscountAmount().add(customerInvoiceReportRow.getItemDiscountAmount()));
        formHDO.setSalesTaxCost(formHDO.getSalesTaxCost().add(customerInvoiceReportRow.getSalesTaxCost()));
        formHDO.setServiceNetAmount(formHDO.getServiceNetAmount().add(customerInvoiceReportRow.getServiceNetAmount()));
        formHDO.setServiceDiscountAmount(formHDO.getServiceDiscountAmount().add(customerInvoiceReportRow.getServiceDiscountAmount()));
        formHDO.setServiceTaxCost(formHDO.getServiceTaxCost().add(customerInvoiceReportRow.getServiceTaxCost()));
        formHDO.setPackagingCost(formHDO.getPackagingCost().add(customerInvoiceReportRow.getPackagingCost()));
        formHDO.setFreightCost(formHDO.getFreightCost().add(customerInvoiceReportRow.getFreightCost()));
        formHDO.setMiscellaneousCost(formHDO.getMiscellaneousCost().add(customerInvoiceReportRow.getMiscellaneousCost()));
        formHDO.setHandlingCost(formHDO.getHandlingCost().add(customerInvoiceReportRow.getHandlingCost()));
        formHDO.setInvoiceTotal(formHDO.getInvoiceTotal().add(customerInvoiceReportRow.getInvoiceTotal()));
        formHDO.setCostBasisTotal(formHDO.getCostBasisTotal().add(customerInvoiceReportRow.getCostBasisTotal()));
        formHDO.setNetProfitAmount(formHDO.getNetProfitAmount().add(customerInvoiceReportRow.getNetProfitAmount()));
      }

//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (customerInvoiceReportRows.isEmpty()) {
        firstRecord = 0;
        customerInvoiceReportRows.add(new CustomerInvoiceReportDO());
      }
//--------------------------------------------------------------------------------
// No Pagination logic for customer invoice report.
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setCustomerInvoiceReportForm(customerInvoiceReportRows);
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
    return formHDO;
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
// No customerKey indicates the user wants all the customers.
// A value of "ALL" is assigned and evaluated in getItemSale()
//--------------------------------------------------------------------------------
      Integer customerKey = null;
      if (request.getParameter("customerKey") != null) {
        if (!(request.getParameter("customerKey").equalsIgnoreCase("")))
          customerKey = new Integer(request.getParameter("customerKey"));
      }

      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        if (!(request.getParameter("sourceTypeKey").equalsIgnoreCase("")))
          sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }

      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        if (!(request.getParameter("sourceKey").equalsIgnoreCase("")))
          sourceKey = new Integer (request.getParameter("sourceKey"));
      }

//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getMTDFromDate());
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getMTDToDate());

      if (request.getParameter("fromDate") != null) {
        if (request.getParameter("fromDate").length() > 0 ) {
          fromDate = request.getParameter("fromDate");
        }
      }

      if (request.getParameter("toDate") != null) {
        if (request.getParameter("toDate").length() > 0 ) {
          toDate = request.getParameter("toDate");
        }
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
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        CustomerInvoiceReportListHeadDO formHDO = getCIReportList(request,
                                                                  customerKey,
                                                                  sourceTypeKey,
                                                                  sourceKey,
                                                                  fromDate,
                                                                  toDate);
        request.getSession().setAttribute("customerinvoicereportlistHDO", formHDO);

//--------------------------------------------------------------------------------
// Create the wrapper object for customerinvoicereportlist.
//--------------------------------------------------------------------------------
        CustomerInvoiceReportListHeadForm formHStr = new CustomerInvoiceReportListHeadForm();
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
