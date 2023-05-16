/*
 * CustomerListAction.java
 *
 * Created on December 08, 2006, 03:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customer;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.service.accounting.AccountingService;
import com.wolmerica.service.accounting.DefaultAccountingService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class CustomerListAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");

  private AccountingService accountingService = new DefaultAccountingService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AccountingService getAccountingService() {
      return accountingService;
  }

  public void setAccountingService(AccountingService accountingService) {
      this.accountingService = accountingService;
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

  
  private CustomerListHeadDO getCustomerList(HttpServletRequest request,
                                             Integer pageNo)
    throws Exception, SQLException {

    CustomerListHeadDO formHDO = new CustomerListHeadDO();
    CustomerListDO customerRow = null;
    ArrayList<CustomerListDO> customerRows = new ArrayList<CustomerListDO>();
    ArrayList permissionRows = new ArrayList();
    HashMap acctMap;

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT customer.thekey,"
                   + "acct_num,"
                   + "acct_name,"
                   + "customertype.name,"
                   + "city,"
                   + "state,"
                   + "zip,"
                   + "phone_num,"
                   + "mobile_num,"
                   + "fax_num,"
                   + "clinic_id,"
                   + "(customer.thekey != primary_key) AS acct_order "
                   + "FROM customer, customertype "
                   + "WHERE customer.primary_key = ? "
                   + "AND customertype.thekey = customertype_key "
                   + "AND customer.active_id "
                   + "ORDER BY acct_order, last_name";
      ps = conn.prepareStatement(query);
      ps.setInt(1, new Integer(request.getSession().getAttribute("ACCTKEY").toString()));
      rs = ps.executeQuery();
      cat.debug(this.getClass().getName() + ": executeQuery() ");
//--------------------------------------------------------------------------------
// Get the accounting values associated with this CUSTOMER.
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"customer.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      cat.debug(this.getClass().getName() + ": before while() ");
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
//--------------------------------------------------------------------------------
// Set the very first customer value to represent "ALL ACCOUNTS".
//--------------------------------------------------------------------------------
          if (recordCount == 1) {
            customerRow = new CustomerListDO();
            customerRow.setKey(new Integer("-1"));
            customerRow.setAcctNum(request.getSession().getAttribute("ACCTNUM").toString());
            customerRow.setAcctName(new String("ALL ACCOUNTS"));
            customerRows.add(customerRow);
            ++recordCount;
          }
          customerRow = new CustomerListDO();

          customerRow.setKey(rs.getInt("thekey"));
          customerRow.setAcctNum(rs.getString("acct_num"));
          customerRow.setAcctName(rs.getString("acct_name"));
          customerRow.setCustomerTypeName(rs.getString("name"));
          customerRow.setCity(rs.getString("city"));
          customerRow.setState(rs.getString("state"));
          customerRow.setZip(rs.getString("zip"));
          customerRow.setPhoneNum(rs.getString("phone_num"));
          customerRow.setMobileNum(rs.getString("mobile_num"));
          customerRow.setFaxNum(rs.getString("fax_num"));
          customerRow.setClinicId(rs.getBoolean("clinic_id"));

//--------------------------------------------------------------------------------
// Retrieve the balance due amount from the customer accounting records.
//--------------------------------------------------------------------------------
          acctMap = getAccountingService().getCustomerBalance(conn, customerRow.getKey());
          customerRow.setAccountBalanceAmount((BigDecimal)(acctMap.get("balanceAmt")));
          customerRow.setAccountBalanceDate((Date)(acctMap.get("balanceDate")));

          customerRows.add(customerRow);
        }
      }
      cat.debug(this.getClass().getName() + ": after while() ");
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
      formHDO.setNextPage(nextPage);

//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (customerRows.isEmpty()) {
        customerRows.add(new CustomerListDO());
      }
      cat.debug(this.getClass().getName() + ": assign list to HDO ");
      formHDO.setCustomerListForm(customerRows);
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


  public Boolean getCustomerDependency(Connection conn,
                                       Byte stKey,
                                       Integer sKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean allowDeleteId = false;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerDependency(?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, stKey);
      cStmt.setInt(2, sKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": dependencyCnt : " + cStmt.getInt("dependencyCnt"));
      cat.debug(this.getClass().getName() + ": tableName : " + cStmt.getString("tableName"));
//--------------------------------------------------------------------------------
// Retrieve the dependency count return value
//--------------------------------------------------------------------------------
      if (cStmt.getInt("dependencyCnt") == 0)
        allowDeleteId = true;
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception(this.getClass().getName() + " " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception(this.getClass().getName() + " " + e.getMessage());
      }
    }
    return allowDeleteId;
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
      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }
      
//--------------------------------------------------------------------------------
// 2007-09-08 Code to validate instance and clean-up the session.
//--------------------------------------------------------------------------------
      getUserStateService().SessionInstanceValidate(request);
      getUserStateService().SessionAttributeCleanUp(request);

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        CustomerListHeadDO formHDO = getCustomerList(request,
                                                     pageNo);
        request.getSession().setAttribute("customerListHDO", formHDO);

        // Create the wrapper object for customer list.
        CustomerListHeadForm formHStr = new CustomerListHeadForm();
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
