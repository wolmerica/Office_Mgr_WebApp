/*
 * ExpenseAddAction.java
 *
 * Created on July 02, 2006, 3:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.expense;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

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

import org.apache.log4j.Logger;

public class ExpenseAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private Integer insertExpense(HttpServletRequest request,
                                ActionForm form)

   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ExpenseDO formDO = null;

    Integer exKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      formDO = (ExpenseDO) request.getSession().getAttribute("expense");

//--------------------------------------------------------------------------------
// Get the maximum key from the vendor invoice key.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS ex_cnt, MAX(thekey)+1 AS ex_key "
                   + "FROM expense ";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("ex_cnt") > 0 ) {
          exKey = rs.getInt("ex_key");
        }
      }
      else {
        throw new Exception("Expense MAX() not found!");
      }
      cat.debug(this.getClass().getName() + " SELECT MAX() expense = " + exKey.toString());

//===========================================================================
// Prepare a SQL query to insert a row into the expense table.
//===========================================================================
      query = "INSERT INTO expense "
            + "(thekey,"
            + "expense_date,"
            + "due_date,"
            + "payment_date,"
            + "name,"
            + "category,"
            + "payment,"
            + "rate,"
            + "tax_prep_date,"
            + "reconciled_id,"
            + "note_line1,"
            + "create_user,create_stamp,"
            + "update_user,update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, exKey);
      ps.setDate(2, new java.sql.Date(formDO.getExpenseDate().getTime()));
      ps.setDate(3, new java.sql.Date(formDO.getDueDate().getTime()));
      ps.setDate(4, new java.sql.Date(formDO.getPaymentDate().getTime()));
      ps.setString(5, formDO.getExpenseName());
      ps.setString(6, formDO.getExpenseCategory());
      ps.setBigDecimal(7, formDO.getExpensePayment());
      ps.setBigDecimal(8, formDO.getExpenseRate());
      ps.setDate(9, new java.sql.Date(formDO.getTaxPrepDate().getTime()));
      ps.setBoolean(10, formDO.getReconciledId());
      ps.setString(11, formDO.getNoteLine1());
      ps.setString(12, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(13, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();
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
    cat.debug(this.getClass().getName() + ": return exKey = " + exKey);
    return exKey;
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

      Integer theKey = null;
      theKey = insertExpense(request, form);
      request.setAttribute("key", theKey.toString());      
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