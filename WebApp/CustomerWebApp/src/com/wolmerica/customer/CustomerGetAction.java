/*
 * CustomerGetAction.java
 *
 * Created on August 22, 2005, 4:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customer;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.customertype.CustomerTypeDropList;
import com.wolmerica.service.accounting.AccountingService;
import com.wolmerica.service.accounting.DefaultAccountingService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;

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
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class CustomerGetAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");

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


  private CustomerDO buildCustomerForm(HttpServletRequest request,
                                       Integer cKey)
   throws Exception, SQLException {

    CustomerDO formDO = null;
    ArrayList customerTypeRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT code_num,first_name,last_name,"
                   + "ship_to,address,address2,city,state,zip,"
                   + "phone_num,mobile_num,fax_num,"
                   + "acct_num,acct_name,"
                   + "customertype_key,ledger_id,client_name,report_id,"
                   + "primary_key,clinic_id,"
                   + "email,"
                   + "website,"
                   + "line_of_business,"
                   + "contact_name,"
                   + "active_id,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM customer "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, cKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        formDO = new CustomerDO();

        formDO.setKey(cKey);
        formDO.setCodeNum(rs.getString("code_num"));
        formDO.setFirstName(rs.getString("first_name"));
        formDO.setLastName(rs.getString("last_name"));
        formDO.setShipTo(rs.getString("ship_to"));
        formDO.setAddress(rs.getString("address"));
        formDO.setAddress2(rs.getString("address2"));
        formDO.setCity(rs.getString("city"));
        formDO.setState(rs.getString("state"));
        formDO.setZip(rs.getString("zip"));
        formDO.setPhoneNum(rs.getString("phone_num"));
        formDO.setMobileNum(rs.getString("mobile_num"));
        formDO.setFaxNum(rs.getString("fax_num"));
        formDO.setAcctNum(rs.getString("acct_num"));
        formDO.setAcctName(rs.getString("acct_name"));
        formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        formDO.setLedgerId(rs.getBoolean("ledger_id"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setReportId(rs.getBoolean("report_id"));
        formDO.setPrimaryKey(rs.getInt("primary_key"));
        formDO.setClinicId(rs.getBoolean("clinic_id"));
        formDO.setEmail(rs.getString("email"));
        formDO.setWebSite(rs.getString("website"));
        formDO.setLineOfBusiness(rs.getString("line_of_business"));
        formDO.setContactName(rs.getString("contact_name"));
        formDO.setActiveId(rs.getBoolean("active_id"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// Get the primary name with a look-up to customer with primary key.
//--------------------------------------------------------------------------------
        query = "SELECT client_name "
                   + "FROM customer "
                   + "WHERE thekey = ?";
        cat.debug(this.getClass().getName() + ": getPrimaryKey() = " + formDO.getPrimaryKey());
        ps = conn.prepareStatement(query);
        ps.setInt(1, formDO.getPrimaryKey());
        rs = ps.executeQuery();
        if ( rs.next() ) {
          formDO.setPrimaryName(rs.getString("client_name"));
        }
        else {
          throw new Exception("Customer SELECT primary name not found!");
        }
      }
      else {
        throw new Exception("Customer  " + cKey.toString() + " not found!");
      }
//--------------------------------------------------------------------------------
// Retrieve the active customer type values available to assign to a customer.
//--------------------------------------------------------------------------------
      CustomerTypeDropList ctdl = new CustomerTypeDropList();
      customerTypeRows = ctdl.getActiveCustomerTypeList(conn);
      formDO.setCustomerTypeForm(customerTypeRows);

//--------------------------------------------------------------------------------
// Retrieve the ledger balance from the non-archived ledger records.
//--------------------------------------------------------------------------------
      HashMap acctMap;
      if (formDO.getLedgerId()) {
        acctMap = getAccountingService().getCustomerLedgerBalance(conn,formDO.getKey());
        formDO.setLedgerBalanceAmount((BigDecimal)(acctMap.get("accountAmount")));
      }

//--------------------------------------------------------------------------------
// Retrieve the last payment amount from the customer accounting records.
//--------------------------------------------------------------------------------
      acctMap = getAccountingService().getCustomerLastPayment(conn,formDO.getKey());
      formDO.setLastPaymentAmount((BigDecimal)(acctMap.get("lastPaymentAmt")));
      formDO.setLastPaymentDate((Date)(acctMap.get("lastPaymentDate")));

//--------------------------------------------------------------------------------
// Retrieve the balance due amount from the customer accounting records.
//--------------------------------------------------------------------------------
      acctMap = getAccountingService().getCustomerBalance(conn,formDO.getKey());
      formDO.setAccountBalanceAmount((BigDecimal)(acctMap.get("balanceAmt")));
      formDO.setAccountBalanceDate((Date)(acctMap.get("balanceDate")));
//--------------------------------------------------------------------------------
// A balance greater than zero indicates a payment is due.
//--------------------------------------------------------------------------------
      if (formDO.getAccountBalanceAmount().compareTo(new BigDecimal("0")) > 0) {
        formDO.setAllowPaymentId(true);
      }

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
// Customer key is provided from the session.
//--------------------------------------------------------------------------------
      Integer theKey = new Integer(request.getSession().getAttribute("ACCTKEY").toString());
      
//--------------------------------------------------------------------------------
// 2007-09-08 Code to validate instance and clean-up the session.
// Do not clean up 2nd or 3rd steps of a multiple step action.     
//--------------------------------------------------------------------------------
      getUserStateService().SessionInstanceValidate(request);
//      getUserStateService().SessionAttributeCleanUp(request);
         
      try {                    
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        CustomerDO formDO = buildCustomerForm(request, theKey);
        request.getSession().setAttribute("customer", formDO);
        CustomerForm formStr = new CustomerForm();
        formStr.populate(formDO);

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