/*
 * ServiceSaleSummaryListAction.java
 *
 * Created on August 21, 2006, 3:38 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.servicesalereport;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.customer.CustomerActionMapping;
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
import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.log4j.Logger;

public class ServiceSaleSummaryListAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");

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


  private ServiceSaleListHeadDO getServiceSaleSummary(HttpServletRequest request,
                                                      Integer customerKey,
                                                      Integer primaryKey,
                                                      String fromDate,
                                                      String toDate)
   throws Exception, SQLException {

    ServiceSaleListHeadDO formHDO = new ServiceSaleListHeadDO();
    ServiceSaleDO serviceSaleRow = null;
    ArrayList serviceSaleRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    BigDecimal bdOrderQty = new BigDecimal("0");
    MathContext mc = new MathContext(4);
    
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
      
//--------------------------------------------------------------------------------
// The account name will be passed from the URL or assigned from the session.
//--------------------------------------------------------------------------------
      if (request.getParameter("acctName") != null)
        formHDO.setAcctName(request.getParameter("acctName"));          
      else
        formHDO.setAcctName(request.getSession().getAttribute("ACCTNAME").toString());

      formHDO.setCustomerKey(customerKey);

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

//--------------------------------------------------------------------------------
// Select totals from customer invoice grouped on year and month.
//--------------------------------------------------------------------------------
      String query = "SELECT YEAR(ci.create_stamp) AS ci_year,"
                   + "MONTH(ci.create_stamp) AS ci_month,"
                   + "MONTHNAME(ci.create_stamp) AS ci_monthname,"
                   + "COUNT(*) AS order_qty,"
                   + "SUM(ci.sub_total - item_total) AS service_total "
                   + "FROM customer c,customerinvoice ci "
                   + "WHERE ci.thekey IN (SELECT DISTINCT customerinvoice_key FROM customerinvoiceservice) "
                   + "AND c.thekey = ci.customer_key "
                   + "AND NOT ci.active_id";

//--------------------------------------------------------------------------------
// Limit to the resale order services.
// 0 - Direct ship to customer
// 1 - Drop ship to customer
// 2 - Sell inventory to customer
// 3 - Credit for a customer return
//--------------------------------------------------------------------------------
      query = query + " AND ci.scenario_key IN (0,1,2,3)"
                    + " AND DATE(ci.create_stamp) BETWEEN ? AND ?";
      if (customerKey > 0)
        query = query + " AND c.thekey = ?";  
      else
        query = query + " AND c.primary_key = ?";         
      query = query + " GROUP BY ci_year,ci_month,ci_monthname "
            + "ORDER BY ci_year,ci_month";
      cat.debug(this.getClass().getName() + ": query = " + query);
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      if (customerKey > 0)
        ps.setInt(3, customerKey); 
      else
        ps.setInt(3, primaryKey);       
      rs = ps.executeQuery();

      Short recordCount = 0;
      Short firstRecord = 1;
      while ( rs.next() ) {
        ++recordCount;
        serviceSaleRow = new ServiceSaleDO();

        serviceSaleRow.setInvoiceYear(rs.getShort("ci_year"));
        serviceSaleRow.setInvoiceMonthName(rs.getString("ci_monthname"));
        serviceSaleRow.setOrderQty(rs.getShort("order_qty"));
//        serviceSaleRow.setSalesTax(rs.getBigDecimal("sales_tax"));
//        serviceSaleRow.setHandlingCost(rs.getBigDecimal("handling_cost"));
        serviceSaleRow.setServiceTotal(rs.getBigDecimal("service_total"));

//--------------------------------------------------------------------------------
// Sum up the totals in the header record to be displayed at the end of the list.
//--------------------------------------------------------------------------------
        formHDO.setTransactionTotal(formHDO.getTransactionTotal() + serviceSaleRow.getOrderQty());
        formHDO.setSalesTaxTotal(formHDO.getSalesTaxTotal().add(serviceSaleRow.getSalesTax()));
        formHDO.setHandlingTotal(formHDO.getHandlingTotal().add(serviceSaleRow.getHandlingCost()));
        formHDO.setServiceTotal(formHDO.getServiceTotal().add(serviceSaleRow.getServiceTotal()));

        serviceSaleRows.add(serviceSaleRow);
      }
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (serviceSaleRows.isEmpty()) {
        firstRecord = 0;
        serviceSaleRows.add(new ServiceSaleDO());
      }
//--------------------------------------------------------------------------------
// No Pagination logic for serviceSale.
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setServiceSaleForm(serviceSaleRows);

      cat.debug(this.getClass().getName() + ": recordCount = " + formHDO.getLastRecord());
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
  
    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

		// Default target to success
    String target = "success";

    CustomerActionMapping customerMapping =
      (CustomerActionMapping)mapping;

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

//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getYTDFromDate());
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getYTDToDate());

      if (!(request.getParameter("fromDate") == null)) {
        if (request.getParameter("fromDate").length() > 0 ) {
          fromDate = request.getParameter("fromDate");
        }
      }

      if (!(request.getParameter("toDate") == null)) {
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
        ServiceSaleListHeadDO formHDO = getServiceSaleSummary(request,
                                                        customerKey,
                                                        primaryKey,
                                                        fromDate,
                                                        toDate);
        request.getSession().setAttribute("webreportHDO", formHDO);

        ServiceSaleListHeadForm formHStr = new ServiceSaleListHeadForm();
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
