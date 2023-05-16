/*
 * VendorInvoiceDueListAction.java
 *
 * Created on August 21, 2005, 10:51 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoicedue;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
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
import java.math.BigDecimal;
import java.util.*;

import org.apache.log4j.Logger;

public class VendorInvoiceDueListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  
  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }


  private VendorInvoiceDueListHeadDO getVendorInvoiceDue(HttpServletRequest request)
    throws Exception, SQLException {

    VendorInvoiceDueListHeadDO formHDO = (VendorInvoiceDueListHeadDO) request.getSession().getAttribute("vendorInvoiceDueListHDO");
    VendorInvoiceDueListDO vendorInvoiceDueRow = null;
    ArrayList<VendorInvoiceDueListDO> vendorInvoiceDueRows = new ArrayList<VendorInvoiceDueListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
      
      formHDO.setDebitTotal(new BigDecimal("0"));
      formHDO.setCreditTotal(new BigDecimal("0"));
      formHDO.setBalanceTotal(new BigDecimal("0"));
      
      String query = "SELECT vendorinvoice.thekey,"
                   + "vendorinvoice.master_key,"
                   + "vendorinvoice.genesis_key,"
                   + "vendorinvoice.purchaseorder_key,"
                   + "purchaseorder.purchase_order_num,"
                   + "vendorinvoice.invoice_num,"
                   + "vendorinvoice.invoice_date,"
                   + "vendorinvoice.invoice_due_date,"
                   + "vendorinvoice.grand_total,"
                   + "DATEDIFF(?,vendorinvoice.invoice_due_date) AS day_count1,"
                   + "DATEDIFF(?,vendorinvoice.invoice_due_date) AS day_count2,"
                   + "DATEDIFF(?,vendorinvoice.invoice_due_date) AS day_count3 "
                   + "FROM vendorinvoice, purchaseorder "
                   + "WHERE vendorinvoice.purchaseorder_key = purchaseorder.thekey "
                   + "AND purchaseorder.vendor_key = ? "
                   + "AND vendorinvoice.invoice_date BETWEEN ? AND ?";
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getPayDate1().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getPayDate2().getTime()));
      ps.setDate(3, new java.sql.Date(formHDO.getPayDate3().getTime()));
      ps.setInt(4, formHDO.getVendorKey());
      ps.setDate(5, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(6, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();

      Short recordCount = 0;
      Short firstRecord = 0;
      Integer daySum1 = new Integer("0");
      Integer daySum2 = new Integer("0");
      Integer daySum3 = new Integer("0");
      while ( rs.next() ) {
        ++recordCount;
        vendorInvoiceDueRow = new VendorInvoiceDueListDO();

        vendorInvoiceDueRow.setKey(rs.getInt("thekey"));
        vendorInvoiceDueRow.setMasterKey(rs.getInt("master_key"));
        vendorInvoiceDueRow.setGenesisKey(rs.getInt("genesis_key"));
        vendorInvoiceDueRow.setPurchaseOrderKey(rs.getInt("purchaseorder_key"));
        vendorInvoiceDueRow.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
        vendorInvoiceDueRow.setInvoiceNumber(rs.getString("invoice_num"));
        vendorInvoiceDueRow.setInvoiceDate(rs.getDate("invoice_date"));
        vendorInvoiceDueRow.setInvoiceDueDate(rs.getDate("invoice_due_date"));
        vendorInvoiceDueRow.setInvoiceAmount(rs.getBigDecimal("grand_total"));

        cat.debug(this.getClass().getName() + " invoice_num = " + vendorInvoiceDueRow.getInvoiceNumber());

        daySum1 = daySum1 + rs.getInt("day_count1");
        daySum2 = daySum2 + rs.getInt("day_count2");
        daySum3 = daySum3 + rs.getInt("day_count3");
        vendorInvoiceDueRow.setDayCount(rs.getInt("day_count3"));
        vendorInvoiceDueRow.setDayAverage(daySum3 / recordCount.intValue());

        if (vendorInvoiceDueRow.getKey() == vendorInvoiceDueRow.getGenesisKey()) {
          formHDO.setDebitTotal(formHDO.getDebitTotal().add(vendorInvoiceDueRow.getInvoiceAmount()));
        } else {
          formHDO.setCreditTotal(formHDO.getCreditTotal().add(vendorInvoiceDueRow.getInvoiceAmount()));
        }
        formHDO.setBalanceTotal(formHDO.getBalanceTotal().add(vendorInvoiceDueRow.getInvoiceAmount()));

        vendorInvoiceDueRows.add(vendorInvoiceDueRow);
      }

//--------------------------------------------------------------------------------
// Store the three day averages, record count, first record and last record.
//--------------------------------------------------------------------------------
      if (recordCount > 0) {
        firstRecord = new Short("1");
        formHDO.setDayAverage1(daySum1 / recordCount.intValue());
        formHDO.setDayAverage2(daySum2 / recordCount.intValue());
        formHDO.setDayAverage3(daySum3 / recordCount.intValue());
      }
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setVendorName(getVendorService().getVendorName(conn, formHDO.getVendorKey()));

//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (vendorInvoiceDueRows.isEmpty()) {
        vendorInvoiceDueRows.add(new VendorInvoiceDueListDO());
      }
      formHDO.setVendorInvoiceDueListForm(vendorInvoiceDueRows);

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
        VendorInvoiceDueListHeadDO formHDO = getVendorInvoiceDue(request);

        request.getSession().setAttribute("vendorInvoiceDueListHDO", formHDO);
        cat.debug(this.getClass().getName() + " set Attribute formHDO ");
        // Create the wrapper object for vendor list.
        VendorInvoiceDueListHeadForm formHStr = new VendorInvoiceDueListHeadForm();
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
