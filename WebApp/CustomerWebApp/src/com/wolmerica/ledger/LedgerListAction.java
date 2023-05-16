/*
 * LedgerListAction.java
 *
 * Created on September 23, 2005, 10:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.ledger;

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

import org.apache.log4j.Logger;

public class LedgerListAction extends Action {

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


  private LedgerListHeadDO getLedger(HttpServletRequest request,
                                     Byte ledgerMode,
                                     Integer customerKey,
                                     String fromDate,
                                     String toDate,
                                     String slipNumber)
   {

    LedgerListHeadDO formHDO = new LedgerListHeadDO();
    LedgerDO ledgerRow = null;
    ArrayList<LedgerDO> ledgerRows = new ArrayList<LedgerDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    String clientName = "";
    BigDecimal bdGrandTotal = new BigDecimal ("0");

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      formHDO.setMode(ledgerMode);
      formHDO.setCustomerKey(customerKey);
      formHDO.setSlipNumber(slipNumber);

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

      String query = "SELECT ledger.thekey, "
                   + "customerinvoice_key, customer_key, "
                   + "client_name, invoice_num, invoice_total,"
                   + "post_stamp, slip_num, archived_id "
                   + "FROM ledger, customer "
                   + "WHERE customer_key = customer.thekey "
                   + "AND slip_num = ? "
                   + "ORDER BY invoice_num";
      ps = conn.prepareStatement(query);
      ps.setString(1, slipNumber.trim());
      rs = ps.executeQuery();

      Integer recordCount = 0;
      Integer firstRecord = 1;
      while ( rs.next() ) {
        ++recordCount;
        ledgerRow = new LedgerDO();

        ledgerRow.setKey(rs.getInt("thekey"));
        ledgerRow.setCustomerInvoiceKey(rs.getInt("customerinvoice_key"));
        ledgerRow.setCustomerKey(rs.getInt("customer_key"));
        ledgerRow.setClientName(rs.getString("client_name"));
        clientName = ledgerRow.getClientName();
        ledgerRow.setInvoiceNumber(rs.getString("invoice_num"));
        ledgerRow.setPostStamp(rs.getTimestamp("post_stamp"));
        ledgerRow.setSlipNumber(rs.getString("slip_num"));
        ledgerRow.setArchivedId(rs.getByte("archived_id"));
        ledgerRow.setInvoiceTotal(rs.getBigDecimal("invoice_total"));
        bdGrandTotal = bdGrandTotal.add(ledgerRow.getInvoiceTotal());

        ledgerRows.add(ledgerRow);
      }
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (ledgerRows.isEmpty()) {
        firstRecord = 0;
        ledgerRows.add(new LedgerDO());
      }
      //===========================================================================
      // No Pagination logic for ledger.
      // Store the filter, row count, previous and next page number values.
      //===========================================================================
      if (customerKey != null) {
        formHDO.setClientName(clientName);
      }
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setGrandTotal(bdGrandTotal);
      formHDO.setLedgerForm(ledgerRows);
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
// ledgerMode is defined as an Byte to use a "switch case" statement
// in the getLedger method.
//--------------------------------------------------------------------------------
      Byte ledgerMode = 1;
      if (!(request.getParameter("mode") == null)) {
        ledgerMode = new Byte(request.getParameter("mode"));
      }
//--------------------------------------------------------------------------------
// No customerKey indicates the user wants all the customers.
// A value of "ALL" is assigned and evaluated in getLedger()
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
//--------------------------------------------------------------------------------
// Retrieve the slipNumber parameter if it exists.
//--------------------------------------------------------------------------------
      String slipNum = "";
      if (!(request.getParameter("slipNum") == null)) {
        slipNum = new String(request.getParameter("slipNum"));
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
        LedgerListHeadDO formHDO = getLedger(request, ledgerMode,
                                             customerKey, fromDate,
                                             toDate, slipNum);
        request.getSession().setAttribute("ledgerlistHDO", formHDO);

        // Create the wrapper object for employee list.
        LedgerListHeadForm formHStr = new LedgerListHeadForm();
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
