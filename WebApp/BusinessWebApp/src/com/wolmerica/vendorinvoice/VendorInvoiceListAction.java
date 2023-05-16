/*
 * VendorInvoiceListAction.java
 *
 * Created on October 23, 2005, 6:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/07/2006 - Add pagination to employee list display.
 */

package com.wolmerica.vendorinvoice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.property.PropertyService; 
import com.wolmerica.service.property.DefaultPropertyService; 
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
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

import org.apache.log4j.Logger;

public class VendorInvoiceListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

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


  private VendorInvoiceListHeadDO getVendorInvoiceList(HttpServletRequest request,
                                                       String vendorNameFilter,
                                                       String invoiceNumberFilter,
                                                       Integer pageNo)
    throws Exception, SQLException {

    VendorInvoiceListHeadDO formHDO = new VendorInvoiceListHeadDO();
    VendorInvoiceListDO vendorInvoiceRow = null;
    ArrayList<VendorInvoiceListDO> vendorInvoiceRows = new ArrayList<VendorInvoiceListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ServletContext context = servlet.getServletContext();

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT vendorinvoice.thekey,"
                   + "vendorinvoice.purchaseorder_key,"
                   + "vendorinvoice.master_key,"
                   + "purchaseorder.customer_key,"
                   + "purchaseorder.purchase_order_num,"
                   + "vendor.name,"
                   + "vendorinvoice.invoice_num,"
                   + "vendorinvoice.active_id,"
                   + "vendorinvoice.invoice_date,"
                   + "vendorinvoice.invoice_due_date,"
                   + "vendorinvoice.grand_total "
                   + "FROM vendorinvoice, purchaseorder, vendor "
                   + "WHERE UPPER(vendor.name) LIKE ? "
                   + "AND UPPER(invoice_num) LIKE ? "
                   + "AND purchaseorder_key = purchaseorder.thekey "
                   + "AND purchaseorder.vendor_key = vendor.thekey "
                   + "ORDER BY vendorinvoice.thekey DESC";
      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + vendorNameFilter.toUpperCase().trim() + "%");
      ps.setString(2, "%" + invoiceNumberFilter.toUpperCase().trim() + "%");
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"vendorinvoice.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          vendorInvoiceRow = new VendorInvoiceListDO();

          vendorInvoiceRow.setKey(rs.getInt("thekey"));
          vendorInvoiceRow.setPurchaseOrderKey(rs.getInt("purchaseorder_key"));
          vendorInvoiceRow.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
          vendorInvoiceRow.setVendorName(rs.getString("name"));
          vendorInvoiceRow.setInvoiceNumber(rs.getString("invoice_num"));
          vendorInvoiceRow.setActiveId(rs.getBoolean("active_id"));
          vendorInvoiceRow.setInvoiceDate(rs.getDate("invoice_date"));
          vendorInvoiceRow.setInvoiceDueDate(rs.getDate("invoice_due_date"));
          vendorInvoiceRow.setInvoiceTotal(rs.getBigDecimal("grand_total"));

          if (vendorInvoiceRow.getKey() == rs.getInt("master_key")) {
            vendorInvoiceRow.setMasterInvoiceId(true);
          }
          else {
            vendorInvoiceRow.setMasterInvoiceId(false);
          }
//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRows.add(getUserStateService().getUserListToken(request,conn,
                                  this.getClass().getName(),vendorInvoiceRow.getKey()));
          vendorInvoiceRows.add(vendorInvoiceRow);
        }
      }

      //===========================================================================
      // Pagination logic to figure out what the previous and next page
      // values will be for the next screen to be displayed.
      //===========================================================================
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
      //===========================================================================
      // Store the filter, row count, previous and next page number values.
      //===========================================================================
      formHDO.setVendorNameFilter(vendorNameFilter);
      formHDO.setInvoiceNumberFilter(invoiceNumberFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (vendorInvoiceRows.isEmpty()) {
        vendorInvoiceRows.add(new VendorInvoiceListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setVendorInvoiceListForm(vendorInvoiceRows);
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
      String vendorNameFilter = "";
      if (!(request.getParameter("vendorNameFilter") == null)) {
        vendorNameFilter = request.getParameter("vendorNameFilter");
      }

      String invoiceNumberFilter = "";
      if (!(request.getParameter("invoiceNumberFilter") == null)) {
        invoiceNumberFilter = request.getParameter("invoiceNumberFilter");
      }

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
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
        VendorInvoiceListHeadDO formHDO = getVendorInvoiceList(request,
                                                               vendorNameFilter,
                                                               invoiceNumberFilter,
                                                               pageNo);
        request.getSession().setAttribute("vendorInvoiceListHDO", formHDO);

        // Create the wrapper object for vendor invoice list.
        VendorInvoiceListHeadForm formHStr = new VendorInvoiceListHeadForm();
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