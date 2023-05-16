/*
 * CustomerInvoiceReportDetailListAction.java
 *
 * Created on February 16, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 02/20/2007 - Sort the details by invoice date descending and remove queries to
 *              populate the customer and entity names as they exist in the summary header.
 */

package com.wolmerica.customerinvoicereportdetail;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class CustomerInvoiceReportDetailListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private UserStateService userStateService = new DefaultUserStateService();

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
  

  private CustomerInvoiceReportDetailListHeadDO getCIReportDetailList(HttpServletRequest request,
                                                                Integer customerKey,
                                                                Byte sourceTypeKey,
                                                                Integer sourceKey,
                                                                String fromDate,
                                                                String toDate)
   throws Exception, SQLException {

    CustomerInvoiceReportDetailListHeadDO formHDO = new CustomerInvoiceReportDetailListHeadDO();
    CustomerInvoiceReportDetailDO cIRDRow = null;
    ArrayList<CustomerInvoiceReportDetailDO> cIRDRows = new ArrayList<CustomerInvoiceReportDetailDO>();

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
// Retrieve the customer invoice records to cooresponds to the input parameters of
// customer, attribute to, from date, and to date.
//--------------------------------------------------------------------------------
      String query = "(SELECT customerinvoiceitem.master_key,"
            + "customerinvoiceitem.thekey AS customerinvoicedetail_key,"
            + "customerinvoice.thekey AS customerinvoice_key,"
            + "DATE(customerinvoice.create_stamp) AS service_date,"
            + "itemdictionary.brand_name,"
            + "itemdictionary.generic_name,"
            + "itemdictionary.size,"
            + "itemdictionary.size_unit,"
            + "itemdictionary.item_num,"
            + "customerinvoiceitem.order_qty,"
            + "customerinvoiceitem.cost_basis,"
            + "customerinvoiceitem.note_line1 "
            + "FROM customerinvoice,customerinvoiceitem,itemdictionary "
            + "WHERE customerinvoice.thekey = customerinvoice_key "
            + "AND DATE(customerinvoice.create_stamp) BETWEEN ? AND ? "
            + "AND itemdictionary_key = itemdictionary.thekey";
      if (customerKey != null) {
        query = query + " AND customer_key = " + customerKey;
        if (sourceTypeKey != null) {
          query = query + " AND sourcetype_key = " + sourceTypeKey;
          if (sourceKey != null) {
            query = query + " AND source_key = " + sourceKey;
          }
        }
      }
      else {
        query = query + " AND customer_key IN (SELECT thekey "
                                           + "FROM customer "
                                           + "WHERE NOT clinic_id)";
      }
      query = query + " ORDER BY customerinvoice_key DESC)";
      query = query + " UNION";
      query = query + " (SELECT customerinvoiceservice.master_key,"
                    + "customerinvoiceservice.thekey AS customerinvoicedetail_key,"
                    + "customerinvoice.thekey AS customerinvoice_key,"
                    + "DATE(customerinvoice.create_stamp) AS service_date,"
                    + "servicedictionary.name,"
                    + "servicedictionary.category,"
                    + "(servicedictionary.duration_hours * 60) + servicedictionary.duration_minutes,"
                    + "pricetype.name,"
                    + "NULL,"
                    + "customerinvoiceservice.order_qty,"
                    + "customerinvoiceservice.cost_basis,"
                    + "customerinvoiceservice.note_line1 "
                    + "FROM customerinvoice,customerinvoiceservice,servicedictionary,pricetype "
                    + "WHERE customerinvoice.thekey = customerinvoice_key "
                    + "AND DATE(customerinvoice.create_stamp) BETWEEN ? AND ? "
                    + "AND servicedictionary_key = servicedictionary.thekey "
                    + "AND customerinvoiceservice.pricetype_key = pricetype.thekey";
      if (customerKey != null) {
        query = query + " AND customer_key = " + customerKey;
        if (sourceTypeKey != null) {
          query = query + " AND sourcetype_key = " + sourceTypeKey;
          if (sourceKey != null) {
            query = query + " AND source_key = " + sourceKey;
          }
        }
      }
      else {
        query = query + " AND customer_key IN (SELECT thekey "
                                           + "FROM customer "
                                           + "WHERE NOT clinic_id)";
      }
      query = query + " ORDER BY customerinvoice_key DESC)";
//      cat.debug(this.getClass().getName() + ": query: " + query);
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      ps.setDate(3, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(4, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();
      cat.debug(this.getClass().getName() + ": open query");

      Short recordCount = 0;
      Short firstRecord = 1;
      while ( rs.next() ) {
        ++recordCount;

        cIRDRow = new CustomerInvoiceReportDetailDO();
        cIRDRow.setCustomerInvoiceDetailKey(rs.getInt("master_key"));
        cIRDRow.setCustomerInvoiceKey(rs.getInt("customerinvoice_key"));
        cIRDRow.setDateOfService(rs.getDate("service_date"));
        cIRDRow.setLineDetailNumber(rs.getString("item_num"));
        cIRDRow.setLineDetailName(rs.getString("brand_name"));
        cIRDRow.setLineDetailCategory(rs.getString("generic_name"));
        cIRDRow.setLineDetailSize(rs.getBigDecimal("size"));
        cIRDRow.setLineDetailUnit(rs.getString("size_unit"));
        cIRDRow.setCostBasis(rs.getBigDecimal("cost_basis"));
        cIRDRow.setNoteLine1(rs.getString("note_line1"));

cat.debug(this.getClass().getName() + ": number..; " + cIRDRow.getLineDetailNumber());
cat.debug(this.getClass().getName() + ": brand...; " + cIRDRow.getLineDetailName());

//--------------------------------------------------------------------------------
// Items will have a non-null lineDetailValue where as services will not.
// 02/16/2007 Call the GetCustomerInvoiceItemTotalsByCII stored procedure to compute values.
//--------------------------------------------------------------------------------
        if (cIRDRow.getLineDetailNumber() != null) {
          cIRDRow = getCustomerInvoiceItemTotalsByMasterKey(conn,
                                                            cIRDRow);
        }
        else {
//--------------------------------------------------------------------------------
// 02/19/2007 Call the GetCustomerInvoiceServiceTotalsByCII stored procedure to compute values.
//--------------------------------------------------------------------------------
          cIRDRow = getCustomerInvoiceServiceTotalsByMasterKey(conn,
                                                               cIRDRow);
        }
        
//--------------------------------------------------------------------------------
// 05/25/2008 Override the order_qty value from the CII stored procedure.
// Only show the master records discount and extended price values.
//--------------------------------------------------------------------------------
        cIRDRow.setOrderQty(rs.getShort("order_qty"));
        if (rs.getInt("master_key") != rs.getInt("customerinvoicedetail_key")) {
          cIRDRow.setDiscountRate(new BigDecimal("0"));
          cIRDRow.setDiscountAmount(new BigDecimal("0"));
          cIRDRow.setExtendPrice(new BigDecimal("0"));
        }

        cIRDRows.add(cIRDRow);
      }

//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (cIRDRows.isEmpty()) {
        firstRecord = 0;
        cIRDRows.add(new CustomerInvoiceReportDetailDO());
      }
//--------------------------------------------------------------------------------
// No Pagination logic for customer invoice report.
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setCustomerInvoiceReportDetailForm(cIRDRows);
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


  public CustomerInvoiceReportDetailDO getCustomerInvoiceItemTotalsByMasterKey(Connection conn,
                                                   CustomerInvoiceReportDetailDO formDO)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerInvoiceItemTotalsByCII(?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, formDO.getCustomerInvoiceKey());
      cStmt.setInt(2, formDO.getCustomerInvoiceDetailKey());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Retrieve the return values
// 09/30/2006 - Make sure the Order Quantity value is positive.
//--------------------------------------------------------------------------------
      formDO.setOrderQty(cStmt.getBigDecimal("orderQty").abs().shortValue());
      formDO.setThePrice(cStmt.getBigDecimal("itemPrice"));
      formDO.setDiscountRate(cStmt.getBigDecimal("discountRate"));
      formDO.setDiscountAmount(cStmt.getBigDecimal("discountAmt").negate());
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
      throw new Exception("getCustomerInvoiceItemTotalsByMasterKey() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerInvoiceItemTotalsByMasterKey() " + e.getMessage());
      }
    }
    return formDO;
  }


  public CustomerInvoiceReportDetailDO getCustomerInvoiceServiceTotalsByMasterKey(Connection conn,
                                                              CustomerInvoiceReportDetailDO formDO)

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
      cStmt.setInt(1, formDO.getCustomerInvoiceKey());
      cStmt.setInt(2, formDO.getCustomerInvoiceDetailKey());

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
      cat.debug(this.getClass().getName() + ": servicePrice : " + cStmt.getBigDecimal("servicePrice"));
      cat.debug(this.getClass().getName() + ": discountRate : " + cStmt.getBigDecimal("discountRate"));
      cat.debug(this.getClass().getName() + ": discountAmt : " + cStmt.getBigDecimal("discountAmt"));
      cat.debug(this.getClass().getName() + ": extendAmt : " + cStmt.getBigDecimal("extendAmt"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerInvoiceServiceTotalsByMasterKey() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerInvoiceServiceTotalsByMasterKey() " + e.getMessage());
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
// No customerKey indicates the user wants all the customers.
// A value of "ALL" is assigned and evaluated in getItemSale()
//--------------------------------------------------------------------------------
      Integer customerKey = null;
      if (request.getParameter("customerKey") != null) {
        if (!(request.getParameter("customerKey").equalsIgnoreCase("")))
          customerKey = new Integer(request.getParameter("customerKey"));
      }
      cat.debug(this.getClass().getName() + ": customerKey.....: " + customerKey);

      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }
      cat.debug(this.getClass().getName() + ": sourceTypeKey...: " + sourceTypeKey);

      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        sourceKey = new Integer (request.getParameter("sourceKey"));
      }
      cat.debug(this.getClass().getName() + ": sourceKey.......: " + sourceKey);

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
        CustomerInvoiceReportDetailListHeadDO formHDO = getCIReportDetailList(request,
                                                                        customerKey,
                                                                        sourceTypeKey,
                                                                        sourceKey,
                                                                        fromDate,
                                                                        toDate);
        request.getSession().setAttribute("customerinvoicereportlistHDO", formHDO);

//--------------------------------------------------------------------------------
// Create the wrapper object for customerinvoicereportlist.
//--------------------------------------------------------------------------------
        CustomerInvoiceReportDetailListHeadForm formHStr = new CustomerInvoiceReportDetailListHeadForm();
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
