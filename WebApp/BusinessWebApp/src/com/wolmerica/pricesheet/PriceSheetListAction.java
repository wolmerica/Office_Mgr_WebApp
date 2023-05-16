/*
 * PriceSheetListAction.java
 *
 * Created on July 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricesheet;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
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
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class PriceSheetListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private PriceSheetListHeadDO getPriceSheet(HttpServletRequest request,
                                             Byte sheetMode)

   throws Exception, SQLException {

    PriceSheetListHeadDO formHDO = new PriceSheetListHeadDO();
    PriceSheetDO ledgerRow = null;
    ArrayList<PriceSheetDO> ledgerRows = new ArrayList<PriceSheetDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    String clientName = "";
    BigDecimal bdGrandTotal = new BigDecimal ("0");

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();


      String query = null;

      switch(sheetMode)
      {
          /* Post Daily PriceSheet */
         case 1: query = "SELECT customerinvoice.thekey,"
                       + "customer_key,client_name,"
                       + "sourcetype_key, source_key,"
                       + "invoice_num,invoice_total "
                       + "FROM customerinvoice, customer "
                       + "WHERE scenario_key = 4 "
                       + "AND NOT customerinvoice.active_id "
                       + "AND source_key = -1 "
                       + "AND customer_key = customer.thekey "
                       + "ORDER BY invoice_num DESC";
                 break;
          /* History Summary */
          default: query = "SELECT -1 AS thekey,"
                         + "-1 AS customer_key,"
                         + "client_name,"
                         + "source_key,"
                         + "COUNT(*) AS invoice_num,"
                         + "sum(invoice_total) as invoice_total "
                         + "FROM customerinvoice, customer "
                         + "WHERE scenario_key = 4 "
                         + "AND NOT customerinvoice.active_id "                  
                         + "AND sourcetype_key IN "
                                             + "(SELECT thekey " 
                                             + "FROM accountingtype "
                                             + "WHERE name = 'SOURCE' "
                                             + "AND description = 'Price Sheet') "
                         + "AND source_key > -1 "
                         + "AND customer_key = customer.thekey "
                         + "GROUP BY client_name, source_key "
                         + "ORDER BY source_key DESC";
                 break;
      }
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      Integer recordCount = 0;
      Integer firstRecord = 1;
      while ( rs.next() ) {
        ++recordCount;
        ledgerRow = new PriceSheetDO();
        ledgerRow.setCustomerInvoiceKey(rs.getInt("thekey"));
        ledgerRow.setCustomerKey(rs.getInt("customer_key"));
        ledgerRow.setClientName(rs.getString("client_name"));
        ledgerRow.setInvoiceNumber(rs.getString("invoice_num"));
        ledgerRow.setPriceSheetKey(rs.getInt("source_key"));
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
        ledgerRows.add(new PriceSheetDO());
      }
      //===========================================================================
      // No Pagination logic for ledger.
      // Store the filter, row count, previous and next page number values.
      //===========================================================================
      formHDO.setMode(sheetMode);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setGrandTotal(bdGrandTotal);
      formHDO.setPriceSheetForm(ledgerRows);
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
// sheetMode is defined as an Byte to use a "switch case" statement
// in the getPriceSheet method.
//--------------------------------------------------------------------------------
      Byte sheetMode = 1;
      if (!(request.getParameter("mode") == null)) {
        sheetMode = new Byte(request.getParameter("mode"));
      }

//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        PriceSheetListHeadDO formHDO = getPriceSheet(request,
                                                     sheetMode);
        request.getSession().setAttribute("pricesheetHDO", formHDO);

        // Create the wrapper object for employee list.
        PriceSheetListHeadForm formHStr = new PriceSheetListHeadForm();
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
