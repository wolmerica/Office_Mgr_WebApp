/*
 * VendornvoiceReportDetailListAction.java
 *
 * Created on February 21, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.vendorinvoicereportdetail;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.DateFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.io.IOException;
import java.math.BigDecimal;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

public class VendorInvoiceReportDetailListAction extends Action {

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

  
  private VendorInvoiceReportDetailListHeadDO getVIReportDetailList(HttpServletRequest request,
                                                                Integer vendorKey,
                                                                String fromDate,
                                                                String toDate)
   throws Exception, SQLException {

    VendorInvoiceReportDetailListHeadDO formHDO = new VendorInvoiceReportDetailListHeadDO();
    VendorInvoiceReportDetailDO vIRDRow = null;
    ArrayList<VendorInvoiceReportDetailDO> vIRDRows = new ArrayList<VendorInvoiceReportDetailDO>();

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

//--------------------------------------------------------------------------------
// Retrieve the vendor invoice records to cooresponds to the input parameters of
// vendor, from date, and to date.
//--------------------------------------------------------------------------------
      String query = "(SELECT vendorinvoiceitem.thekey AS vendorinvoiceitem_key,"
            + "vendorinvoice.thekey AS vendorinvoice_key,"
            + "invoice_date,"
            + "itemdictionary.brand_name,"
            + "itemdictionary.generic_name,"
            + "itemdictionary.size,"
            + "itemdictionary.size_unit,"
            + "itemdictionary.item_num,"
            + "vendorinvoiceitem.receive_qty,"
            + "vendorinvoiceitem.first_cost,"
            + "vendorinvoiceitem.variant_amount,"
            + "vendorinvoiceitem.note_line1 "
            + "FROM vendorinvoice,vendorinvoiceitem,purchaseorderitem,itemdictionary "
            + "WHERE vendorinvoice.thekey = vendorinvoice_key "
            + "AND invoice_date BETWEEN ? AND ? "
            + "AND purchaseorderitem_key = purchaseorderitem.thekey "
            + "AND itemdictionary_key = itemdictionary.thekey";
      if (vendorKey != null) {
        query = query + " AND vendorinvoice.purchaseorder_key IN (SELECT thekey "
                             + "FROM purchaseorder "
                             + "WHERE vendor_key = " + vendorKey.toString() + ")";
      }
      query = query + " ORDER BY vendorinvoice_key DESC)";
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();

      Short recordCount = 0;
      Short firstRecord = 1;
      BigDecimal bdReceiveQty = null;
      while ( rs.next() ) {
        ++recordCount;

        vIRDRow = new VendorInvoiceReportDetailDO();
        vIRDRow.setVendorInvoiceDetailKey(rs.getInt("vendorinvoiceitem_key"));
        vIRDRow.setVendorInvoiceKey(rs.getInt("vendorinvoice_key"));
        vIRDRow.setInvoiceDate(rs.getDate("invoice_date"));
        vIRDRow.setLineDetailNumber(rs.getString("item_num"));
        vIRDRow.setLineDetailName(rs.getString("brand_name"));
        vIRDRow.setLineDetailCategory(rs.getString("generic_name"));
        vIRDRow.setLineDetailSize(rs.getBigDecimal("size"));
        vIRDRow.setLineDetailUnit(rs.getString("size_unit"));
        vIRDRow.setOrderQty(rs.getShort("receive_qty"));
        vIRDRow.setThePrice(rs.getBigDecimal("first_cost"));
        vIRDRow.setTheDiscount(rs.getBigDecimal("variant_amount").multiply(new BigDecimal("-1")));
        bdReceiveQty = rs.getBigDecimal("receive_qty");
        vIRDRow.setExtendPrice(vIRDRow.getThePrice().add(vIRDRow.getTheDiscount()));
        vIRDRow.setExtendPrice(vIRDRow.getExtendPrice().multiply(bdReceiveQty));
        vIRDRow.setNoteLine1(rs.getString("note_line1"));

        vIRDRows.add(vIRDRow);
      }

//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (vIRDRows.isEmpty()) {
        firstRecord = 0;
        vIRDRows.add(new VendorInvoiceReportDetailDO());
      }
//--------------------------------------------------------------------------------
// No Pagination logic for vendor invoice report.
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setVendorInvoiceReportDetailForm(vIRDRows);
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
// No vendorKey indicates the user wants all the vendors.
// A value of "ALL" is assigned and evaluated in getItemSale()
//--------------------------------------------------------------------------------
      Integer vendorKey = null;
      if (request.getParameter("vendorKey") != null) {
        if (!(request.getParameter("vendorKey").equalsIgnoreCase("")))
          vendorKey = new Integer(request.getParameter("vendorKey"));
      }

//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getMTDFromDate());
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getMTDToDate());

      if (request.getParameter("fromDate") != null) {
        if (request.getParameter("fromDate").length() > 0 ) {
          fromDate = request.getParameter("fromDate");
        }
      }

      if (request.getParameter("toDate") != null) {
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
        VendorInvoiceReportDetailListHeadDO formHDO = getVIReportDetailList(request,
                                                                            vendorKey,
                                                                            fromDate,
                                                                            toDate);
        request.getSession().setAttribute("vendorinvoicereportlistHDO", formHDO);

//--------------------------------------------------------------------------------
// Create the wrapper object for vendorinvoicereportlist.
//--------------------------------------------------------------------------------
        VendorInvoiceReportDetailListHeadForm formHStr = new VendorInvoiceReportDetailListHeadForm();
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
