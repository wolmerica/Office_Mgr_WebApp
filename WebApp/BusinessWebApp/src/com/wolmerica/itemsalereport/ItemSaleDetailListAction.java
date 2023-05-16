/*
 * ItemSaleDetailListAction.java
 *
 * Created on July 6, 2006, 9:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.itemsalereport;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.log4j.Logger;

public class ItemSaleDetailListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private CustomerService CustomerService = new DefaultCustomerService();
  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

  public CustomerService getCustomerService() {
      return CustomerService;
  }

  public void setCustomerService(CustomerService CustomerService) {
      this.CustomerService = CustomerService;
  }

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

  public VendorService getVendorService() {
      return VendorService;
  }
  
  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }

  private ItemSaleListHeadDO getItemSaleDetail(HttpServletRequest request,
                                               Integer vendorKey,
                                               Integer customerKey,          
                                               Byte mode,
                                               String manufacturer,
                                               String fromDate,
                                               String toDate)
   throws Exception, SQLException {

    ItemSaleListHeadDO formHDO = new ItemSaleListHeadDO();
    ItemSaleDO itemSaleRow = null;
    ArrayList<ItemSaleDO> itemSaleRows = new ArrayList<ItemSaleDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    BigDecimal bdOrderQty = new BigDecimal("0");
    MathContext mc = new MathContext(4);

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      formHDO.setVendorKey(vendorKey);
      formHDO.setCustomerKey(customerKey);            
      formHDO.setMode(mode);
      formHDO.setManufacturer(manufacturer);
      
//--------------------------------------------------------------------------------
// Look-up the vendor name with the vendor key if necessary.
//--------------------------------------------------------------------------------
      if (vendorKey != null) {
        formHDO.setVendorName(getVendorService().getVendorName(conn, vendorKey));
      }      
      
//--------------------------------------------------------------------------------
// Look-up the client name with the customer key if necessary.
//--------------------------------------------------------------------------------
      if (customerKey != null) {
        formHDO.setClientName(getCustomerService().getClientName(conn, customerKey));
      }      

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

//--------------------------------------------------------------------------------
// Prepare a SQL query to select item sale details from customer invoice.
//--------------------------------------------------------------------------------
      String query = "SELECT ci.thekey AS ci_key,"
                   + "cii.thekey AS cii_key,"
                   + "DATE(ci.create_stamp) AS invoice_date,"
                   + "ci.invoice_num,"
                   + "id.brand_name,"
                   + "id.generic_name,"
                   + "pabi.size,id."
                   + "size_unit,"
                   + "cii.order_qty,"
                   + "item_price,"
                   + "c.client_name "
                   + "FROM customer c,customerinvoice ci,"
                   + "customerinvoiceitem cii,vendorinvoiceitem vii," 
                   + "vendorinvoice vi,purchaseorder po,"
                   + "itemdictionary id,priceattributebyitem pabi "
                   + "WHERE c.thekey = ci.customer_key "
//            + "AND c.report_id "
                   + "AND ci.thekey = cii.customerinvoice_key "
                   + "AND cii.thekey = cii.master_key "
                   + "AND cii.vendorinvoiceitem_key = vii.thekey "
                   + "AND vii.vendorinvoice_key = vi.thekey "
                   + "AND vi.purchaseorder_key = po.thekey "
                   + "AND cii.itemdictionary_key = id.thekey "
                   + "AND id.thekey = pabi.itemdictionary_key "
//            + "AND id.report_id "
                   + "AND pabi.pricetype_key = cii.pricetype_key "
                   + "AND NOT ci.active_id";
//--------------------------------------------------------------------------------
// Limit to the resale order items.
// 0 - Direct ship to customer
// 1 - Drop ship to customer
// 2 - Sell inventory to customer
// 3 - Credit for a customer return
//--------------------------------------------------------------------------------
      if (mode >= 0)
        query = query + " AND ci.scenario_key = " + mode;
      else
        query = query + " AND ci.scenario_key IN (0,1,2,3)";
      if (vendorKey != null)
        query = query + " AND po.vendor_key = " + vendorKey;      
      if (customerKey != null)
        query = query + " AND c.primary_key = " + customerKey;
      if (manufacturer.length() > 0)
        query = query + " AND id.manufacturer = '" + manufacturer + "'";
      query = query + " AND DATE(ci.create_stamp) BETWEEN ? AND ? "
            + "ORDER BY ci.create_stamp, ci.invoice_num";
      cat.debug(this.getClass().getName() + ": query = " + query);
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();

      Short recordCount = 0;
      Short firstRecord = 1;
      while ( rs.next() ) {
        ++recordCount;
        itemSaleRow = new ItemSaleDO();

        itemSaleRow.setInvoiceKey(rs.getInt("ci_key"));
        itemSaleRow.setInvoiceDate(rs.getDate("invoice_date"));
        itemSaleRow.setInvoiceNumber(rs.getString("invoice_num"));
        itemSaleRow.setBrandName(rs.getString("brand_name"));
        itemSaleRow.setGenericName(rs.getString("generic_name"));
        itemSaleRow.setSize(rs.getBigDecimal("size"));
        itemSaleRow.setSizeUnit(rs.getString("size_unit"));
        itemSaleRow.setOrderQty(rs.getShort("order_qty"));
        itemSaleRow.setThePrice(rs.getBigDecimal("item_price"));

//--------------------------------------------------------------------------------
// 08/04/2006 Call the GetCustomerInvoiceItemTotalsByCII stored procedure to compute values.
//--------------------------------------------------------------------------------
//  1) OrderQty              summation of order quantity for the master_key.
//  2) DiscountAmt           summation of discounts for the item.
//  2) ExtendPrice           final price after discounts.
//--------------------------------------------------------------------------------
        itemSaleRow = getCustomerInvoiceItemTotalsByMasterKey(conn,
                                                          itemSaleRow,
                                                          rs.getInt("cii_key"));

        formHDO.setItemTotal(formHDO.getItemTotal().add(itemSaleRow.getItemTotal()));

        itemSaleRows.add(itemSaleRow);
      }
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (itemSaleRows.isEmpty()) {
        firstRecord = 0;
        itemSaleRows.add(new ItemSaleDO());
      }
//--------------------------------------------------------------------------------
// No Pagination logic for itemSale.
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(recordCount);
      formHDO.setItemSaleForm(itemSaleRows);

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

  public ItemSaleDO getCustomerInvoiceItemTotalsByMasterKey(Connection conn,
                                                        ItemSaleDO formDO,
                                                        Integer masterKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    MathContext mc = new MathContext(4);
    BigDecimal bdPercentRatio = new BigDecimal("0.01");

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerInvoiceItemTotalsByCII(?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, formDO.getInvoiceKey());
      cStmt.setInt(2, masterKey);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setOrderQty(cStmt.getShort("orderQty"));
      formDO.setDiscountAmount(cStmt.getBigDecimal("discountAmt").negate());
      formDO.setItemTotal(cStmt.getBigDecimal("extendAmt"));

//--------------------------------------------------------------------------------
// Debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": orderQty : " + cStmt.getShort("orderQty"));
      cat.debug(this.getClass().getName() + ": discountAmt : " + cStmt.getBigDecimal("discountAmt"));
      cat.debug(this.getClass().getName() + ": extendAmt : " + cStmt.getBigDecimal("extendAmt"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerInvoiceItemTotalsByMasterKey() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerInvoiceItemTotalsByMasterKey() " + e.getMessage());
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
// No vendorKey indicates the user wants all the vendor.
//--------------------------------------------------------------------------------
      Integer vendorKey = null;
      if (request.getParameter("vendorKey") != null) {
        if (!(request.getParameter("vendorKey").equalsIgnoreCase("")))
          vendorKey = new Integer(request.getParameter("vendorKey"));
      }
      
//--------------------------------------------------------------------------------
// No customerKey indicates the user wants all the customers.
//--------------------------------------------------------------------------------
      Integer customerKey = null;
      if (request.getParameter("customerKey") != null) {
        if (!(request.getParameter("customerKey").equalsIgnoreCase("")))
          customerKey = new Integer(request.getParameter("customerKey"));
      }      
      
//--------------------------------------------------------------------------------
// itemSaleMode is defined as an Byte to use a "switch case" statement
// in the getItemSale method.
//--------------------------------------------------------------------------------
      Byte mode = -1;
      if (!(request.getParameter("mode") == null)) {
        mode = new Byte(request.getParameter("mode"));
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
// Retrieve the manufacturer parameter if it exists.
//--------------------------------------------------------------------------------
      String manufacturer = "";
      if (!(request.getParameter("manufacturer") == null)) {
        manufacturer = new String(request.getParameter("manufacturer"));
      }

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        ItemSaleListHeadDO formHDO = getItemSaleDetail(request, 
                                                       vendorKey,
                                                       customerKey,
                                                       mode,
                                                       manufacturer,
                                                       fromDate,
                                                       toDate);
        request.getSession().setAttribute("webreportHDO", formHDO);

        ItemSaleListHeadForm formHStr = new ItemSaleListHeadForm();
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
