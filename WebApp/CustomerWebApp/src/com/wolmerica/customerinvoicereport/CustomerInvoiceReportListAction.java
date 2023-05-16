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
import com.wolmerica.customer.CustomerActionMapping;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.DateFormatter;

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
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class CustomerInvoiceReportListAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private CustomerService customerService = new DefaultCustomerService();
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
                                                          Integer primaryKey,
                                                          Byte sourceTypeKey,
                                                          Integer sourceKey,
                                                          String fromDate,
                                                          String toDate)
   throws Exception, SQLException {

    CustomerInvoiceReportListHeadDO formHDO = new CustomerInvoiceReportListHeadDO();
    CustomerInvoiceReportDO customerInvoiceReportRow = null;
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
        formHDO.setClientName(getCustomerService().getClientName(conn,customerKey));
      }

//--------------------------------------------------------------------------------
// Get the attribute to name value when the source type and source key have a value.
//--------------------------------------------------------------------------------
      if (sourceKey != null) {
        formHDO.setSourceTypeKey(sourceTypeKey);
        formHDO.setSourceKey(sourceKey);
        HashMap nameMap = getAttributeToService().getAttributeToName(conn,sourceTypeKey,sourceKey);
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
                   + "customerinvoice.sales_tax,"
                   + "customerinvoice.sub_total - customerinvoice.item_total AS service_total,"
                   + "customerinvoice.service_tax,"
                   + "(customerinvoice.packaging+customerinvoice.freight+customerinvoice.miscellaneous) AS handling_cost,"
                   + "customerinvoice.invoice_total,"
                   + "customerinvoice.note_line1,"
                   + "customerinvoice.note_line2,"
                   + "customerinvoice.note_line3 "
                   + "FROM customerinvoice, customer "
                   + "WHERE customer_key = customer.thekey "
                   + "AND DATE(customerinvoice.create_stamp) BETWEEN ? AND ?";
      if (customerKey > 0)
        query = query + " AND customer.thekey = ?";
      else
        query = query + " AND customer.primary_key = ?";
      if (sourceTypeKey != null) {
        query = query + " AND sourcetype_key = ?";
        if (sourceKey != null) {
          query = query + " AND source_key = ?";
        }
      }
      query = query + " ORDER BY customerinvoice_key DESC";
      cat.debug(this.getClass().getName() + query);
      ps = conn.prepareStatement(query);

      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      if (customerKey > 0)
        ps.setInt(3, customerKey);
      else
        ps.setInt(3, primaryKey);
      if (sourceTypeKey != null) {
        ps.setInt(4, sourceTypeKey);
        if (sourceKey != null) {
          ps.setInt(5, sourceKey);
        }
      }
      rs = ps.executeQuery();

      Short recordCount = 0;
      Short firstRecord = 1;
      HashMap nameMap = null;
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
        customerInvoiceReportRow.setHandlingCost(rs.getBigDecimal("handling_cost"));
        customerInvoiceReportRow.setInvoiceTotal(rs.getBigDecimal("invoice_total"));
        customerInvoiceReportRow.setNoteLine1(rs.getString("note_line1"));
        customerInvoiceReportRow.setNoteLine2(rs.getString("note_line2"));
        customerInvoiceReportRow.setNoteLine3(rs.getString("note_line3"));

        nameMap = getAttributeToService().getAttributeToName(conn,rs.getByte("sourcetype_key"),rs.getInt("source_key"));
        customerInvoiceReportRow.setAttributeToName(nameMap.get("attributeToName").toString());

        customerInvoiceReportRows.add(customerInvoiceReportRow);

//--------------------------------------------------------------------------------
// Set the header totals with all detail value summations.
//--------------------------------------------------------------------------------
        formHDO.setItemNetAmount(formHDO.getItemNetAmount().add(rs.getBigDecimal("item_total")));
        formHDO.setSalesTaxCost(formHDO.getSalesTaxCost().add(rs.getBigDecimal("sales_tax")));
        formHDO.setServiceNetAmount(formHDO.getServiceNetAmount().add(rs.getBigDecimal("service_total")));
        formHDO.setServiceTaxCost(formHDO.getServiceTaxCost().add(rs.getBigDecimal("service_tax")));
        formHDO.setHandlingCost(formHDO.getHandlingCost().add(rs.getBigDecimal("handling_cost")));
        formHDO.setInvoiceTotal(formHDO.getInvoiceTotal().add(rs.getBigDecimal("invoice_total")));
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

//--------------------------------------------------------------------------------
// Default target to success
//--------------------------------------------------------------------------------
    String target = "success";

    CustomerActionMapping customerMapping = (CustomerActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( customerMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("ACCTKEY") == null ) {

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
// CustomerKey will be accepted as an input parameter first and from the session next.
//--------------------------------------------------------------------------------
      Integer customerKey = null;
      if (request.getParameter("customerKeyFilter") != null) {
        customerKey = new Integer(request.getParameter("customerKeyFilter").toString());
      }
      else {
        customerKey = new Integer(request.getSession().getAttribute("ACCTKEY").toString());
      }

//--------------------------------------------------------------------------------
// PrimaryKey is provided from the session for the multi account customers.
//--------------------------------------------------------------------------------
      Integer primaryKey = null;
      if (request.getSession().getAttribute("MULTIACCT").toString().compareToIgnoreCase("true") == 0) {
        primaryKey = new Integer(request.getSession().getAttribute("ACCTKEY").toString());
      }

      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }

      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        sourceKey = new Integer (request.getParameter("sourceKey"));
      }

//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getYTDFromDate());
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getYTDToDate());

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
// 2007-09-08 Code to validate instance and clean-up the session.  
//--------------------------------------------------------------------------------
      getUserStateService().SessionInstanceValidate(request);
      getUserStateService().SessionAttributeCleanUp(request);

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        CustomerInvoiceReportListHeadDO formHDO = getCIReportList(request,
                                                                  customerKey,
                                                                  primaryKey,
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
