/*
 * CustomerAccountingListAction.java
 *
 * Created on December 05, 2006, 08:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customeraccounting;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.customer.CustomerActionMapping;
import com.wolmerica.service.accounting.AccountingService;
import com.wolmerica.service.accounting.DefaultAccountingService;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
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
import java.util.HashMap;
import java.util.Collections;

import org.apache.log4j.Logger;

public class CustomerAccountingListAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");

  private AccountingService accountingService = new DefaultAccountingService();
  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AccountingService getAccountingService() {
      return accountingService;
  }

  public void setAccountingService(AccountingService accountingService) {
      this.accountingService = accountingService;
  }

  public DateRangeService getDateRangeService() {
      return dateRangeService;
  }

  public void setDateRangeService(DateRangeService dateRangeService) {
      this.dateRangeService = dateRangeService;
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

  
  private CustomerAccountingListHeadDO getCustomerAccounting(HttpServletRequest request,
                                                             Integer pageNo,
                                                             Integer customerKey,
                                                             Integer primaryKey,
                                                             String fromDate,
                                                             String toDate)
    throws Exception, SQLException {

    CustomerAccountingListHeadDO formHDO = new CustomerAccountingListHeadDO();
    CustomerAccountingListDO customerAccountingRow = null;
    ArrayList<CustomerAccountingListDO> customerAccountingRows = new ArrayList<CustomerAccountingListDO>();
    HashMap acctMap;

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// The account name will be passed from the URL or assigned from the session.
//--------------------------------------------------------------------------------
      if (request.getParameter("acctName") != null)
        formHDO.setAccountName(request.getParameter("acctName"));
      else
        formHDO.setAccountName(request.getSession().getAttribute("ACCTNAME").toString());
      formHDO.setCustomerKeyFilter(customerKey);

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

      String query = "select customeraccounting.thekey,"
                   + "customeraccounting.customer_key,"
                   + "customer.acct_num,"
                   + "customer.acct_name,"
                   + "customeraccounting.transactiontype_key,"
                   + "t1.name AS transactiontype_name,"
                   + "t1.description AS transactiontype_description,"
                   + "customeraccounting.sourcetype_key,"
                   + "t2.name AS sourcetype_name,"
                   + "t2.description AS sourcetype_description,"
                   + "customeraccounting.source_key,"
                   + "customeraccounting.number,"
                   + "customeraccounting.amount,"
                   + "customeraccounting.note,"
                   + "customeraccounting.reconciled_id,"
                   + "customeraccounting.create_user,"
                   + "customeraccounting.create_stamp,"
                   + "customeraccounting.update_user,"
                   + "customeraccounting.update_stamp "
                   + "FROM customeraccounting, customer, accountingtype t1, accountingtype t2 "
                   + "WHERE customeraccounting.customer_key = customer.thekey "
                   + "AND NOT customer.clinic_id "
                   + "AND customeraccounting.transactiontype_key = t1.thekey "
                   + "AND customeraccounting.sourcetype_key = t2.thekey "
                   + "AND DATE(customeraccounting.create_stamp) BETWEEN ? AND ? ";
      if (customerKey > 0)
        query = query + " AND customer.thekey = ? ";
      else
        query = query + " AND customer.primary_key = ? ";
      query = query + "ORDER BY customeraccounting.thekey DESC";
      cat.debug(this.getClass().getName() + " query = " + query);
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      if (customerKey > 0)
        ps.setInt(3, customerKey);
      else
        ps.setInt(3, primaryKey);
      rs = ps.executeQuery();

      cat.debug(this.getClass().getName() + " execute the Query " + rs.getFetchSize());
//--------------------------------------------------------------------------------
// Get the accounting values associated with this CUSTOMER.
//--------------------------------------------------------------------------------
      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"customeraccounting.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          customerAccountingRow = new CustomerAccountingListDO();

          customerAccountingRow.setKey(rs.getInt("thekey"));
          customerAccountingRow.setCustomerKey(rs.getInt("customer_key"));
          customerAccountingRow.setAcctNum(rs.getString("acct_num"));
          customerAccountingRow.setAcctName(rs.getString("acct_name"));
          customerAccountingRow.setTransactionTypeKey(rs.getByte("transactiontype_key"));
          customerAccountingRow.setTransactionTypeName(rs.getString("transactiontype_name"));
          customerAccountingRow.setTransactionTypeDescription(rs.getString("transactiontype_description"));
          customerAccountingRow.setSourceTypeKey(rs.getByte("sourcetype_key"));
          customerAccountingRow.setSourceTypeName(rs.getString("sourcetype_name"));
          customerAccountingRow.setSourceTypeDescription(rs.getString("sourcetype_description"));
          customerAccountingRow.setSourceKey(rs.getInt("source_key"));
          customerAccountingRow.setNumber(rs.getString("number"));
          customerAccountingRow.setAmount(rs.getBigDecimal("amount"));
          customerAccountingRow.setReconciledId(rs.getBoolean("reconciled_id"));
          customerAccountingRow.setPostDate(rs.getDate("create_stamp"));

          cat.debug(this.getClass().getName() + " before accounting key call");
//--------------------------------------------------------------------------------
// 09-26-06  Retrieve the amount due value for the customer accounting record.
//--------------------------------------------------------------------------------
          acctMap = getAccountingService().getCustomerAcctTranDueAmount(conn,
                                             customerAccountingRow.getCustomerKey(),
                                             customerAccountingRow.getKey());
          customerAccountingRow.setAmountDue((BigDecimal)acctMap.get("balanceDueAmt"));
          formHDO.setPageTotal(formHDO.getPageTotal().add(customerAccountingRow.getAmount()));
          cat.debug(this.getClass().getName() + " after accounting key call");
          customerAccountingRows.add(customerAccountingRow);
        }
      }
//--------------------------------------------------------------------------------
// Take the vendorAccountingRows must be put in reverse order.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + " after while loop ");
      Collections.reverse(customerAccountingRows);

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
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setCurrentPage(prevPage + 1);
      formHDO.setNextPage(nextPage);

//--------------------------------------------------------------------------------
// Retrieve the balance due amount from the customer accounting records.
//--------------------------------------------------------------------------------
      query = "SELECT count(*) AS ca_count, sum(amount) AS ca_amount "
            + "FROM customeraccounting ";
      if (customerKey != null)
        query = query + "WHERE customer_key = ?";
      ps = conn.prepareStatement(query);
      if (customerKey != null)
        ps.setInt(1, customerKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if (rs.getInt("ca_count") > 0)
          formHDO.setBalanceTotal(rs.getBigDecimal("ca_amount"));
      }
      cat.debug(this.getClass().getName() + " before isEmpty ");
//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (customerAccountingRows.isEmpty()) {
        customerAccountingRows.add(new CustomerAccountingListDO());
      }
      formHDO.setCustomerAccountingListForm(customerAccountingRows);

      cat.debug(this.getClass().getName() + " formHDO is set ");
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
      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }

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
        CustomerAccountingListHeadDO formHDO = getCustomerAccounting(request,
                                                                    pageNo,
                                                                    customerKey,
                                                                    primaryKey,
                                                                    fromDate,
                                                                    toDate);
        request.getSession().setAttribute("customerAccountingListHDO", formHDO);
        cat.debug(this.getClass().getName() + " set Attribute formHDO ");
        // Create the wrapper object for customer list.
        CustomerAccountingListHeadForm formHStr = new CustomerAccountingListHeadForm();
        cat.debug(this.getClass().getName() + " before populate ");
        formHStr.populate(formHDO);
        cat.debug(this.getClass().getName() + " after populate ");
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
