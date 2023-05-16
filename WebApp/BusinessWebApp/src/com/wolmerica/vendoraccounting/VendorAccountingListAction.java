/*
 * VendorAccountingListAction.java
 *
 * Created on August 21, 2005, 10:51 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendoraccounting;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
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
import java.util.HashMap;
import java.util.Collections;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class VendorAccountingListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

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

 
  private VendorAccountingListHeadDO getVendorAccounting(HttpServletRequest request,
                                                             String lastNameFilter,
                                                             Integer pageNo,
                                                             Integer vendorKeyFilter,
                                                             String fromDate,
                                                             String toDate)
    throws Exception, SQLException {

    VendorAccountingListHeadDO formHDO = new VendorAccountingListHeadDO();
    VendorAccountingListDO vendorAccountingRow = null;
    ArrayList<VendorAccountingListDO> vendorAccountingRows = new ArrayList<VendorAccountingListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();
    HashMap acctMap;

    String accountName = "All";

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      formHDO.setVendorKeyFilter(vendorKeyFilter);

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

      String query = "select vendoraccounting.thekey,"
                   + "vendoraccounting.vendor_key,"
                   + "vendor.acct_num,"
                   + "vendor.name,"
                   + "vendoraccounting.transactiontype_key,"
                   + "t1.name AS transactiontype_name,"
                   + "t1.description AS transactiontype_description,"
                   + "vendoraccounting.sourcetype_key,"
                   + "t2.name AS sourcetype_name,"
                   + "t2.description AS sourcetype_description,"
                   + "vendoraccounting.source_key,"
                   + "vendoraccounting.number,"
                   + "vendoraccounting.amount,"
                   + "vendoraccounting.note,"
                   + "vendoraccounting.reconciled_id,"
                   + "vendoraccounting.create_user,"
                   + "vendoraccounting.create_stamp,"
                   + "vendoraccounting.update_user,"
                   + "vendoraccounting.update_stamp "
                   + "FROM vendoraccounting, vendor, accountingtype t1, accountingtype t2 "
                   + "WHERE vendoraccounting.vendor_key = vendor.thekey "
                   + "AND vendoraccounting.transactiontype_key = t1.thekey "
                   + "AND vendoraccounting.sourcetype_key = t2.thekey "
                   + "AND DATE(vendoraccounting.create_stamp) BETWEEN ? AND ? ";
      if (vendorKeyFilter != null)
        query = query + "AND vendor.thekey = ? ";
      query = query + "ORDER BY vendoraccounting.create_stamp DESC";
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      if (vendorKeyFilter != null)
        ps.setInt(3, vendorKeyFilter);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Get the accounting values associated with this VENDOR.
//--------------------------------------------------------------------------------
      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"vendoraccounting.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          vendorAccountingRow = new VendorAccountingListDO();

          vendorAccountingRow.setKey(rs.getInt("thekey"));
          vendorAccountingRow.setVendorKey(rs.getInt("vendor_key"));
          vendorAccountingRow.setAcctNum(rs.getString("acct_num"));
          vendorAccountingRow.setAcctName(rs.getString("name"));
          accountName = vendorAccountingRow.getAcctName();
          vendorAccountingRow.setTransactionTypeKey(rs.getByte("transactiontype_key"));
          vendorAccountingRow.setTransactionTypeName(rs.getString("transactiontype_name"));
          vendorAccountingRow.setTransactionTypeDescription(rs.getString("transactiontype_description"));
          vendorAccountingRow.setSourceTypeKey(rs.getByte("sourcetype_key"));
          vendorAccountingRow.setSourceTypeName(rs.getString("sourcetype_name"));
          vendorAccountingRow.setSourceTypeDescription(rs.getString("sourcetype_description"));
          vendorAccountingRow.setSourceKey(rs.getInt("source_key"));
          vendorAccountingRow.setNumber(rs.getString("number"));
          vendorAccountingRow.setAmount(rs.getBigDecimal("amount"));
          vendorAccountingRow.setReconciledId(rs.getBoolean("reconciled_id"));
          vendorAccountingRow.setPostDate(rs.getDate("create_stamp"));
//--------------------------------------------------------------------------------
// 09-26-06  Retrieve the amount due value for the vendor accounting record.
//--------------------------------------------------------------------------------
          acctMap = getAccountingService().getVendorAcctTranDueAmount(conn,
                                           vendorAccountingRow.getVendorKey(),
                                           vendorAccountingRow.getKey());
          vendorAccountingRow.setAmountDue((BigDecimal)acctMap.get("balanceDueAmt"));
          formHDO.setPageTotal(formHDO.getPageTotal().add(vendorAccountingRow.getAmount()));
//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRows.add(getUserStateService().getUserListToken(request,conn,
                                  this.getClass().getName(),vendorAccountingRow.getKey()));
          vendorAccountingRows.add(vendorAccountingRow);
        }
      }
//--------------------------------------------------------------------------------
// Take the vendorAccountingRows and permissionRows must be put in reverse order.
//--------------------------------------------------------------------------------
      Collections.reverse(permissionRows);
      Collections.reverse(vendorAccountingRows);

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
      if (vendorKeyFilter != null) {
        formHDO.setVendorName(accountName);
      }
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setCurrentPage(prevPage + 1);
      formHDO.setNextPage(nextPage);

//--------------------------------------------------------------------------------
// Retrieve the balance due amount from the vendor accounting records.
//--------------------------------------------------------------------------------
      query = "SELECT count(*) AS va_count, sum(amount) AS va_amount "
            + "FROM vendoraccounting ";
      if (vendorKeyFilter != null)
        query = query + "WHERE vendor_key = ?";
      ps = conn.prepareStatement(query);
      if (vendorKeyFilter != null)
        ps.setInt(1, vendorKeyFilter);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if (rs.getInt("va_count") > 0)
          formHDO.setBalanceTotal(rs.getBigDecimal("va_amount"));
//--------------------------------------------------------------------------------
// A balance greater than zero indicates a payment is due.
//--------------------------------------------------------------------------------
          if (formHDO.getBalanceTotal().compareTo(new BigDecimal("0")) > 0)
            formHDO.setAllowPaymentId(true);
//--------------------------------------------------------------------------------
// A balance greater than zero indicates a refund is available.
//--------------------------------------------------------------------------------
          if (formHDO.getBalanceTotal().compareTo(new BigDecimal("0")) < 0)
            formHDO.setAllowRefundId(true);

      }

//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (vendorAccountingRows.isEmpty()) {
        vendorAccountingRows.add(new VendorAccountingListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setVendorAccountingListForm(vendorAccountingRows);
      formHDO.setPermissionListForm(permissionRows);

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
      String lastNameFilter = "";
      if (!(request.getParameter("lastNameFilter") == null)) {
        lastNameFilter = request.getParameter("lastNameFilter");
      }

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }
//--------------------------------------------------------------------------------
// No vendorKeyFilter indicates the user wants all the vendors.
//--------------------------------------------------------------------------------
      Integer vendorKeyFilter = null;
      if (request.getParameter("vendorKeyFilter") != null) {
        if (!(request.getParameter("vendorKeyFilter").equalsIgnoreCase("")))
          vendorKeyFilter = new Integer(request.getParameter("vendorKeyFilter"));
      }
//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getBACKFromDate(new Integer(getPropertyService().getCustomerProperties(request,"customeraccounting.days.back")).intValue()));
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getBACKToDate());

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
        VendorAccountingListHeadDO formHDO = getVendorAccounting(request,
                                                                    lastNameFilter,
                                                                    pageNo,
                                                                    vendorKeyFilter,
                                                                    fromDate,
                                                                    toDate);
        request.getSession().setAttribute("vendorAccountingListHDO", formHDO);
        cat.debug(this.getClass().getName() + " set Attribute formHDO ");
        // Create the wrapper object for vendor list.
        VendorAccountingListHeadForm formHStr = new VendorAccountingListHeadForm();
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
