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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.log4j.Logger;

public class ServiceSaleSummaryListAction extends Action {

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
  

  private ServiceSaleListHeadDO getServiceSaleSummary(HttpServletRequest request,
                                                      Byte mode,
                                                      Integer customerKey,
                                                      String fromDate,
                                                      String toDate)
   throws Exception, SQLException {

    ServiceSaleListHeadDO formHDO = new ServiceSaleListHeadDO();
    ServiceSaleDO serviceSaleRow = null;
    ArrayList<ServiceSaleDO> serviceSaleRows = new ArrayList<ServiceSaleDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    BigDecimal bdOrderQty = new BigDecimal("0");
    MathContext mc = new MathContext(4);
    String query = "";
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      if (customerKey != null) {
        query = "SELECT client_name "
              + "FROM customer "
              + "WHERE thekey = ?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, customerKey);
        rs = ps.executeQuery();
        if ( rs.next() ) {
          formHDO.setClientName(rs.getString("client_name"));
        }
        else {
          throw new Exception("Customer  " + customerKey.toString() + " not found!");
        }
      }
      formHDO.setMode(mode);
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
      query = "SELECT YEAR(ci.create_stamp) AS ci_year,"
            + "MONTH(ci.create_stamp) AS ci_month,"
            + "MONTHNAME(ci.create_stamp) AS ci_monthname,"
            + "COUNT(*) AS order_qty,"
            + "SUM(ci.sub_total - item_total) AS service_total,"
            + "SUM(ci.service_tax) AS service_tax "
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
      if (mode >= 0)
        query = query + " AND ci.scenario_key = " + mode;
      else
        query = query + " AND ci.scenario_key IN (0,1,2,3)";
      if (customerKey != null)
        query = query + " AND c.primary_key = " + customerKey;
      query = query + " AND DATE(ci.create_stamp) BETWEEN ? AND ? "
            + "GROUP BY ci_year,ci_month,ci_monthname "
            + "ORDER BY ci_year,ci_month";
      cat.debug(this.getClass().getName() + ": query = " + query);
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();

      Short recordCount = 0;
      Short firstRecord = 1;
      while ( rs.next() ) {
        ++recordCount;
        serviceSaleRow = new ServiceSaleDO();

        serviceSaleRow.setInvoiceYear(rs.getShort("ci_year"));
        serviceSaleRow.setInvoiceMonthName(rs.getString("ci_monthname"));
        serviceSaleRow.setOrderQty(rs.getShort("order_qty"));
        serviceSaleRow.setServiceTax(rs.getBigDecimal("service_tax"));
//        serviceSaleRow.setHandlingCost(rs.getBigDecimal("handling_cost"));
        serviceSaleRow.setServiceTotal(rs.getBigDecimal("service_total"));

//--------------------------------------------------------------------------------
// Sum up the totals in the header record to be displayed at the end of the list.
//--------------------------------------------------------------------------------
        formHDO.setTransactionTotal(formHDO.getTransactionTotal() + serviceSaleRow.getOrderQty());
        formHDO.setServiceTaxTotal(formHDO.getServiceTaxTotal().add(serviceSaleRow.getServiceTax()));
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
// serviceSaleMode is defined as an Byte to use a "switch case" statement
// in the getServiceSale method.
//--------------------------------------------------------------------------------
      Byte mode = -1;
      if (!(request.getParameter("mode") == null)) {
        mode = new Byte(request.getParameter("mode"));
      }

//--------------------------------------------------------------------------------
// No customerKey indicates the user wants all the customers.
// A value of "ALL" is assigned and evaluated in getServiceSale()
//--------------------------------------------------------------------------------
      Integer customerKey = null;
      if (request.getParameter("customerKey") != null) {
        if (!(request.getParameter("customerKey").equalsIgnoreCase("")))
          customerKey = new Integer(request.getParameter("customerKey"));
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        ServiceSaleListHeadDO formHDO = getServiceSaleSummary(request, mode,
                                                        customerKey,
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
