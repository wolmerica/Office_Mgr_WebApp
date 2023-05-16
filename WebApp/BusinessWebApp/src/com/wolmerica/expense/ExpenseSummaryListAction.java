/*
 * ExpenseSummaryListAction.java
 *
 * Created on January 29, 2007, 7:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/07/2006 - Add pagination to employee list display.
 */

package com.wolmerica.expense;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
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

import org.apache.log4j.Logger;

public class ExpenseSummaryListAction extends Action {

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

  
  private ExpenseListHeadDO getExpenseSummaryList(HttpServletRequest request,
                                                  Byte summaryMode,
                                                  String taxPrepDate)
    throws Exception, SQLException {

    ExpenseListHeadDO formHDO = new ExpenseListHeadDO();
    ExpenseListDO expenseRow = null;
    ArrayList<ExpenseListDO> expenseRows = new ArrayList<ExpenseListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

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
      Date myDate = (Date) dateFormatter.unformat(taxPrepDate);
      formHDO.setFromDate(myDate);
      formHDO.setToDate(myDate);
      formHDO.setMode(summaryMode);

//--------------------------------------------------------------------------------
// Prepare a query to create an expense summary by category and name.
//--------------------------------------------------------------------------------
      String query = null;

      switch(summaryMode)
      {
          /* Summary by Category */
         case 1: query = "SELECT category,"
                       + "'' AS name,"
                       + "COUNT(*) AS expense,"
                       + "SUM(payment) AS payment "
                       + "FROM expense "
                       + "WHERE tax_prep_date = ? "
                       + "GROUP BY category "
                       + "ORDER BY category";
                 break;         
          /* Summary by Name */
         case 2: query = "SELECT category,"
                       + "name,"
                       + "COUNT(*) AS expense,"
                       + "SUM(payment) AS payment "
                       + "FROM expense "
                       + "WHERE tax_prep_date = ? "
                       + "GROUP BY category, name "
                       + "ORDER BY category, name";
                 break;
      }

      ps = conn.prepareStatement(query);
      cat.debug(this.getClass().getName() + ": query = " + query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      rs = ps.executeQuery();

      Integer recordCount = 0;
      Integer firstRecord = 1;
      while ( rs.next() ) {
        ++recordCount;
        expenseRow = new ExpenseListDO();
        expenseRow.setExpenseCategory(rs.getString("category"));
        expenseRow.setExpenseName(rs.getString("name"));
        expenseRow.setAttachmentCount(rs.getInt("expense"));
        expenseRow.setExpensePayment(rs.getBigDecimal("payment"));
        expenseRow.setExpenseRate(rs.getBigDecimal("payment"));

        expenseRows.add(expenseRow);

        formHDO.setPaymentGrandTotal(formHDO.getPaymentGrandTotal().add(rs.getBigDecimal("payment")));
        formHDO.setCurrentPage(formHDO.getCurrentPage() + rs.getInt("expense"));
      }

//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (expenseRows.isEmpty()) {
        firstRecord = 0;
        expenseRows.add(new ExpenseListDO());
      }

//--------------------------------------------------------------------------------
// Fill in one permission row value.
//--------------------------------------------------------------------------------
      if (permissionRows.isEmpty()) {
	    permissionRows.add(getUserStateService().getUserListToken(request,conn,
	                       this.getClass().getName(),getUserStateService().getNoKey()));
	  }

//--------------------------------------------------------------------------------
// No Pagination logic for expense summary.
// Store the row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setExpenseListForm(expenseRows);
      formHDO.setPermissionListForm(permissionRows);

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
// summaryMode is defined as an Byte to use a "switch case" statement
// in the getExpenseSummaryList method.
//--------------------------------------------------------------------------------
      Byte summaryMode = 1;
      if (request.getParameter("mode") != null) {
        summaryMode = new Byte(request.getParameter("mode"));
      }

//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String taxPrepDate = getDateRangeService().getDateToString(getDateRangeService().getYearEndDate());

      if (request.getParameter("fromDate") != null) {
        if (request.getParameter("fromDate").length() > 0 ) {
          taxPrepDate = request.getParameter("fromDate");
        }
      }
      cat.debug(this.getClass().getName() + ": taxPrepDate = " + taxPrepDate);
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
        ExpenseListHeadDO formHDO = getExpenseSummaryList(request,
                                                          summaryMode,
                                                          taxPrepDate);
        request.getSession().setAttribute("expenseListHDO", formHDO);

        // Create the wrapper object for vendor invoice list.
        ExpenseListHeadForm formHStr = new ExpenseListHeadForm();
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