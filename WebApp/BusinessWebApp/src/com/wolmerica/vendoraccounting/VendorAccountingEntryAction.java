/*
 * VendorAccountingEntryAction.java
 *
 * Created on September 9, 2005, 7:29 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendoraccounting;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.accountingtype.AccountingTypeDO;
import com.wolmerica.accountingtype.AccountingTypeDropList;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.CurrencyFormatter;

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

public class VendorAccountingEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private VendorAccountingDO buildVendorAccountingForm(HttpServletRequest request,
                                                       Integer vendorKey,
                                                       BigDecimal balanceAmount)
   throws Exception, SQLException {

    VendorAccountingDO formDO = new VendorAccountingDO();
    AccountingTypeDO accountingTypeRow = null;
    ArrayList accountingTypeRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      if (vendorKey != null) {
        if (balanceAmount.compareTo(new BigDecimal("0.00")) > 0) {
          formDO.setTransactionTypeName(new String("TRANSACTION"));
          formDO.setTransactionTypeDescription(new String("Payment"));
        }
        else {
          formDO.setTransactionTypeName(new String("TRANSACTION"));
          formDO.setTransactionTypeDescription(new String("Refund"));            
        }
          
        formDO.setVendorKey(vendorKey);
        formDO.setAmount(balanceAmount.abs());
//===========================================================================
// Retrieve the account type key using payment or refund name and description.
//===========================================================================
        String query = "SELECT thekey "
                     + "FROM accountingtype "
                     + "WHERE name = ? "
                     + "AND description = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, formDO.getTransactionTypeName());
        ps.setString(2, formDO.getTransactionTypeDescription());
        rs = ps.executeQuery();
        if ( rs.next() ) {
          formDO.setTransactionTypeKey(rs.getByte("thekey"));
        }
        else {
          throw new Exception("Transaction description  " + formDO.getTransactionTypeDescription() + " not found!");
        }                   
        
//===========================================================================
// Retrieve the account type key using cash name and description.
//===========================================================================
        formDO.setSourceTypeName(new String("PAYMENT"));
        formDO.setSourceTypeDescription(new String("Cash"));   
        query = "SELECT thekey "
              + "FROM accountingtype "
              + "WHERE name = ? "
              + "AND description = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, formDO.getSourceTypeName());
        ps.setString(2, formDO.getSourceTypeDescription());
        rs = ps.executeQuery();
        if ( rs.next() ) {
          formDO.setSourceTypeKey(rs.getByte("thekey"));
        }
        else {
          throw new Exception("Cash description  " + formDO.getTransactionTypeDescription() + " not found!");
        }                   
//===========================================================================
// Get the maximum key from the vendor accounting.
//===========================================================================
        query = "SELECT name,"
              + "acct_num "
              + "FROM vendor "
              + "WHERE thekey = ?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, vendorKey);
        rs = ps.executeQuery();
        if ( rs.next() ) {
          formDO.setAcctName(rs.getString("name"));
          formDO.setAcctNum(rs.getString("acct_num"));
        }
        else {
          throw new Exception("Vendor  " + vendorKey.toString() + " not found!");
        }
      }
      formDO.setSourceKey(new Integer("-1"));
      formDO.setPostDate(new Date());
      formDO.setNumber(new String("USD"));
//-----------------------------------------------------------------------------
// Retrieve the active vendor type values available to assign to a vendor.
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
// No vendorKey indicates the user wants all the vendors.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        if (request.getParameter("key") != "")
          theKey = new Integer(request.getParameter("key"));
      }
//--------------------------------------------------------------------------------
// Call the CurrencyFormatter to convert the balance String to a BigDecimal.
//--------------------------------------------------------------------------------
      CurrencyFormatter currFormatter = new CurrencyFormatter();
      BigDecimal balanceAmount = new BigDecimal("0");
      if (!(request.getParameter("accountBalanceAmount") == null)) {
        balanceAmount = (BigDecimal) currFormatter.unformat(request.getParameter("accountBalanceAmount"));
      }
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
      try {      
        VendorAccountingDO formDO = buildVendorAccountingForm(request,
                                                            theKey,
                                                            balanceAmount);
        formDO.setPermissionStatus(usToken);   
        request.getSession().setAttribute("vendoraccountingDO", formDO);
        VendorAccountingForm formStr = new VendorAccountingForm();
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