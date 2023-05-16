/*
 * ExpenseListAction.java
 *
 * Created on July 02, 2006, 3:31 PM
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
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
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

import org.apache.log4j.Logger;

public class ExpenseListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
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


  private ExpenseListHeadDO getExpenseList(HttpServletRequest request,
                                           String expenseNameFilter,
                                           String fromDate,
                                           String toDate,
                                           Integer pageNo,
                                           Byte sourceTypeKey)
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
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

      String query = "SELECT thekey,"
                   + "expense_date,"
                   + "due_date,"
                   + "payment_date,"
                   + "name,"
                   + "category,"
                   + "payment,"
                   + "rate, "
                   + "ROUND(payment * (rate/100),2) AS expense,"
                   + "tax_prep_date,"
                   + "reconciled_id,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM expense "
                   + "WHERE name LIKE ? "
                   + "AND expense_date BETWEEN ? AND ? "
                   + "ORDER BY expense_date DESC, category";
      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + expenseNameFilter.toUpperCase().trim() + "%");
      ps.setDate(2, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(3, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"expense.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          expenseRow = new ExpenseListDO();

          expenseRow.setKey(rs.getInt("thekey"));
          expenseRow.setExpenseDate(rs.getDate("expense_date"));
          expenseRow.setDueDate(rs.getDate("due_date"));
          expenseRow.setPaymentDate(rs.getDate("payment_date"));
          expenseRow.setExpenseName(rs.getString("name"));
          expenseRow.setExpenseCategory(rs.getString("category"));
          expenseRow.setExpensePayment(rs.getBigDecimal("payment"));
          expenseRow.setExpenseRate(rs.getBigDecimal("rate"));
          expenseRow.setExpenseAmount(rs.getBigDecimal("expense"));
          expenseRow.setTaxPrepDate(rs.getDate("tax_prep_date"));
          expenseRow.setReconciledId(rs.getBoolean("reconciled_id"));

          formHDO.setPaymentPageTotal(formHDO.getPaymentPageTotal().add(expenseRow.getExpensePayment()));
          formHDO.setExpensePageTotal(formHDO.getExpensePageTotal().add(expenseRow.getExpenseAmount()));

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
            permissionRow = getUserStateService().getUserListToken(request,conn,
                               this.getClass().getName(),expenseRow.getKey());
            permissionRows.add(permissionRow);

            if (permissionRow.getDeleteId()) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the employee has any dependency on linked to tables.
//--------------------------------------------------------------------------------
              permissionRow.setDeleteId(getAttachmentService().getAttachmentCount(conn,
                                                              sourceTypeKey,
                                                              expenseRow.getKey()) == 0);
            }

          expenseRows.add(expenseRow);
        }
        formHDO.setPaymentGrandTotal(formHDO.getPaymentGrandTotal().add(rs.getBigDecimal("payment")));
        formHDO.setExpenseGrandTotal(formHDO.getExpenseGrandTotal().add(rs.getBigDecimal("expense")));
      }

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
      formHDO.setExpenseNameFilter(expenseNameFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setCurrentPage(prevPage + 1);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (expenseRows.isEmpty()) {
        expenseRows.add(new ExpenseListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setExpenseListForm(expenseRows);
      formHDO.setPermissionListForm(permissionRows);
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
      String expenseNameFilter = "";
      if (!(request.getParameter("expenseNameFilter") == null)) {
        expenseNameFilter = request.getParameter("expenseNameFilter");
      }
      cat.debug(this.getClass().getName() + ": expenseNameFilter : [" + expenseNameFilter + "]");

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
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
        ExpenseListHeadDO formHDO = getExpenseList(request,
                                                   expenseNameFilter,
                                                   fromDate,
                                                   toDate,
                                                   pageNo,
                                                   getUserStateService().getFeatureKey().byteValue());
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