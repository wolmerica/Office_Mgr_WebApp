/*
 * CustomerAccountingGetAction.java
 *
 * Created on August 22, 2005, 4:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customeraccounting;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.accounting.AccountingService;
import com.wolmerica.service.accounting.DefaultAccountingService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.accountingtype.AccountingTypeDropList;

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
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class CustomerAccountingGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AccountingService accountingService = new DefaultAccountingService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AccountingService getAccountingService() {
      return accountingService;
  }

  public void setAccountingService(AccountingService accountingService) {
      this.accountingService = accountingService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private CustomerAccountingDO buildCustomerAccountingForm(HttpServletRequest request,
                                                           Integer caKey)
   throws Exception, SQLException {

    CustomerAccountingDO formDO = null;
    ArrayList accountingTypeRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

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
                   + "AND customeraccounting.thekey = ? "
                   + "AND customeraccounting.transactiontype_key = t1.thekey "
                   + "AND customeraccounting.sourcetype_key = t2.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, caKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        formDO = new CustomerAccountingDO();

        formDO.setKey(rs.getInt("thekey"));
        formDO.setCustomerKey(rs.getInt("customer_key"));
        formDO.setAcctNum(rs.getString("acct_num"));
        formDO.setAcctName(rs.getString("acct_name"));
        formDO.setPostDate(rs.getDate("create_stamp"));
        formDO.setTransactionTypeKey(rs.getByte("transactiontype_key"));
        formDO.setTransactionTypeName(rs.getString("transactiontype_name"));
        formDO.setTransactionTypeDescription(rs.getString("transactiontype_description"));
        formDO.setSourceTypeKey(rs.getByte("sourcetype_key"));
        formDO.setSourceTypeName(rs.getString("sourcetype_name"));
        formDO.setSourceTypeDescription(rs.getString("sourcetype_description"));
        formDO.setSourceKey(rs.getInt("source_key"));
        formDO.setNumber(rs.getString("number"));
        formDO.setAmount(rs.getBigDecimal("amount").abs());
        formDO.setNote(rs.getString("note"));
        formDO.setReconciledId(rs.getBoolean("reconciled_id"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// 09-26-06  Retrieve the amount due value for the customer accounting record.
//--------------------------------------------------------------------------------
        HashMap acctMap = getAccountingService().getCustomerAcctTranDueAmount(conn,
                                                  formDO.getCustomerKey(),
                                                  formDO.getKey());
        formDO.setAmountDue((BigDecimal)acctMap.get("balanceDueAmt"));
      }
      else {
        throw new Exception("Customer Accounting  " + caKey.toString() + " not found!");
      }
//-----------------------------------------------------------------------------
// Retrieve the active accounting type values available.
//-----------------------------------------------------------------------------
      AccountingTypeDropList atdl = new AccountingTypeDropList();
      accountingTypeRows = atdl.getAccountingTypeList(conn);
      formDO.setAccountingTypeForm(accountingTypeRows);
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
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        CustomerAccountingDO formDO = buildCustomerAccountingForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("customeraccountingDO", formDO);
        CustomerAccountingForm formStr = new CustomerAccountingForm();
        formStr.populate(formDO);
        
//--------------------------------------------------------------------------------
// Make sure that the reconciled accounting records are not editable.
//--------------------------------------------------------------------------------        
        if (formDO.getReconciledId()) {
          request.setAttribute("disableEdit", "true");
        }

        form = formStr;
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