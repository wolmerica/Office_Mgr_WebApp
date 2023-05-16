/*
 * CustomerInvoiceListAction.java
 *
 * Created on October 23, 2005, 6:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/01/2006 Allow optional vendor invoice parameter to limit results.
 * 02/12/2006 Add CustomerInvoiceListHead and CustomerInvoiceList objects.
 */

package com.wolmerica.customerinvoice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
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
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class CustomerInvoiceListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
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

  
  private CustomerInvoiceListHeadDO getCustomerInvoiceList(HttpServletRequest request,
                                                           Boolean internalUse,
                                                           String clientNameFilter,
                                                           String invoiceNumberFilter,
                                                           Integer itemKeyFilter,
                                                           Integer serviceKeyFilter,          
                                                           Integer pageNo,
                                                           Integer viKey)
   throws Exception, SQLException {

    CustomerInvoiceListHeadDO formHDO = new CustomerInvoiceListHeadDO();
    CustomerInvoiceListDO customerInvoiceRow = null;
    ArrayList<CustomerInvoiceListDO> customerInvoiceRows = new ArrayList<CustomerInvoiceListDO>();
    
    PermissionListDO permissionRow = null;
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String query = null;
      ds = getDataSource(request);
      conn = ds.getConnection();

      formHDO.setGenesisKey(viKey);
      formHDO.setMoreToDistribute(true);
//--------------------------------------------------------------------------------
// Get relevant information about P.O. and vendor invoice
// when the links to an existing vendor invoice exists.
//--------------------------------------------------------------------------------
      if (viKey >= 0) {
//--------------------------------------------------------------------------------
// The number of items received in the vendor invoice need to be distributed
// to customers or added to the inventory.  This is a check to see if there
// are more items that need to be distributed from the vendorinvoice.
//--------------------------------------------------------------------------------
        Integer nonDistributedCount = getInvoiceQuantityDifference(conn, viKey);
        cat.debug("nonDistributedCount = " + nonDistributedCount.toString());
        if (nonDistributedCount == 0) {
          formHDO.setMoreToDistribute(false);
        }

        query = "SELECT vendorinvoice.purchaseorder_key,"
              + "vendorinvoice.thekey AS vendorinvoice_key,"
              + "purchaseorder.vendor_key,"
              + "vendor.name AS vendor_name,"
              + "purchaseorder.customer_key,"
              + "customer.client_name "
              + "FROM purchaseorder, vendorinvoice, vendor, customer "
              + "WHERE vendorinvoice.thekey=? "
              + "AND purchaseorder_key = purchaseorder.thekey "
              + "AND vendor_key = vendor.thekey "
              + "AND customer_key = customer.thekey";
        ps = conn.prepareStatement(query);
        ps.setInt(1, viKey);
        rs = ps.executeQuery();

        if ( rs.next() ) {
          formHDO.setPurchaseOrderKey(rs.getInt("purchaseorder_key"));
          formHDO.setVendorInvoiceKey(rs.getInt("vendorinvoice_key"));
          formHDO.setVendorKey(rs.getInt("vendor_key"));
          formHDO.setVendorName(rs.getString("vendor_name"));
          formHDO.setCustomerKey(rs.getInt("customer_key"));
          formHDO.setClientName(rs.getString("client_name"));
        }
        else {
          throw new Exception("VendorInvoice/P.O. " + viKey.toString() + " not found!");
        }
      }

      query = "SELECT customerinvoice.thekey,"
            + "customerinvoice.invoice_num,"
            + "customerinvoice.customer_key,"
            + "customer.client_name,"
            + "customerinvoice.create_stamp,"
            + "customerinvoice.invoice_total,"
            + "customerinvoice.active_id,"
            + "customerinvoice.genesis_key,"
            + "customertype.attribute_to_entity,"
            + "customerinvoice.sourcetype_key,"
            + "customerinvoice.source_key "
            + "FROM customerinvoice, customer, customertype "
            + "WHERE UPPER(customer.client_name) LIKE ? "
            + "AND UPPER(customerinvoice.invoice_num) LIKE ? "
            + "AND customerinvoice.customer_key = customer.thekey "
            + "AND customerinvoice.customertype_key = customertype.thekey";
      if (viKey >= 0) {
        query += " AND vendorinvoice_key = " + viKey;
      }
      else {
//--------------------------------------------------------------------------------
// 06-07-07  Limit to the resale customer invoices only.
//--------------------------------------------------------------------------------
        if (internalUse)
          query += " AND customerinvoice.scenario_key IN (4,5,6)";
        else
          query +=  " AND customerinvoice.scenario_key IN (0,1,2,3)";
      }

//--------------------------------------------------------------------------------
// 07-30-07  Limit the customer invoice results to sales of a particular item.
//--------------------------------------------------------------------------------
      if (itemKeyFilter > 0) {
        query += " AND customerinvoice.thekey IN (SELECT customerinvoice_key "
                                              + " FROM customerinvoiceitem "
                                              + " WHERE itemdictionary_key = " + itemKeyFilter + ")";
      }
//--------------------------------------------------------------------------------
// 07-30-07  Limit the customer invoice results to sales of a particular service..
//--------------------------------------------------------------------------------
      if (serviceKeyFilter > 0) {
        query += " AND customerinvoice.thekey IN (SELECT customerinvoice_key "
                                              + " FROM customerinvoiceservice "
                                              + " WHERE servicedictionary_key = " + serviceKeyFilter + ")";
      }
      query += " ORDER BY customerinvoice.thekey DESC";

      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + clientNameFilter.toUpperCase().trim() + "%");
      ps.setString(2, "%" + invoiceNumberFilter.toUpperCase().trim() + "%");
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"customerinvoice.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          customerInvoiceRow = new CustomerInvoiceListDO();

          customerInvoiceRow.setKey(rs.getInt("thekey"));
          customerInvoiceRow.setCustomerInvoiceNumber(rs.getString("invoice_num"));
          customerInvoiceRow.setCustomerKey(rs.getInt("customer_key"));
          customerInvoiceRow.setClientName(rs.getString("client_name"));
          customerInvoiceRow.setAttributeToEntity(rs.getString("attribute_to_entity"));
          customerInvoiceRow.setSourceTypeKey(rs.getByte("sourcetype_key"));
          customerInvoiceRow.setSourceKey(rs.getInt("source_key"));
          customerInvoiceRow.setCreateStamp(rs.getTimestamp("create_stamp"));
          customerInvoiceRow.setInvoiceTotal(rs.getBigDecimal("invoice_total"));
          customerInvoiceRow.setActiveId(rs.getBoolean("active_id"));
          customerInvoiceRow.setCreditId(!(customerInvoiceRow.getKey().equals(rs.getInt("genesis_key"))));

//--------------------------------------------------------------------------------
// Retrieve the pet, system, or vehicle attributed to this invoice.
//--------------------------------------------------------------------------------
          if (customerInvoiceRow.getSourceTypeKey().compareTo(new Byte("-1")) > 0) {
            HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                      customerInvoiceRow.getSourceTypeKey(),
                                                      customerInvoiceRow.getSourceKey());
            customerInvoiceRow.setAttributeToName(nameMap.get("attributeToName").toString());
          }
          
//--------------------------------------------------------------------------------
// 07-31-2006 Define the permission information in separate class for re-usability.
// 10-03-2009 Disable the delete option for customer invoice adjustments.
//--------------------------------------------------------------------------------
          permissionRow = getUserStateService().getUserListToken(request,conn,
                             this.getClass().getName(),customerInvoiceRow.getKey());
          if (rs.getInt("active_id") == 5) {
            permissionRow.setDeleteId(false);
          }
          permissionRows.add(permissionRow);
          customerInvoiceRows.add(customerInvoiceRow);
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
      formHDO.setInternalUse(internalUse);
      formHDO.setClientNameFilter(clientNameFilter);
      formHDO.setInvoiceNumberFilter(invoiceNumberFilter);
      formHDO.setItemKeyFilter(itemKeyFilter);
      formHDO.setServiceKeyFilter(serviceKeyFilter);
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
      if (customerInvoiceRows.isEmpty()) {
        customerInvoiceRows.add(new CustomerInvoiceListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setCustomerInvoiceListForm(customerInvoiceRows);
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

  
  public Integer getInvoiceQuantityDifference(Connection conn,
                                              Integer viKey)
                                       
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer balanceQty = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetInvoiceQuantityDifference(?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter and execute the procedure.
//--------------------------------------------------------------------------------
      cStmt.setInt(1, viKey);
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": viiQty : " + cStmt.getInt("viiQty"));
      cat.debug(this.getClass().getName() + ": ciiQty : " + cStmt.getInt("ciiQty"));
      cat.debug(this.getClass().getName() + ": balanceQty : " + cStmt.getInt("balanceQty"));
//--------------------------------------------------------------------------------
// Retrieve the balance quantity return value
//--------------------------------------------------------------------------------
      balanceQty = cStmt.getInt("balanceQty");
      
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getInvoiceQuantityDifference() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getInvoiceQuantityDifference() " + e.getMessage());
      }
    }
    return balanceQty;
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
      String clientNameFilter = "";
      if (!(request.getParameter("clientNameFilter") == null)) {
        clientNameFilter = request.getParameter("clientNameFilter");
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
// A CustomerInvoiceList call with "key" parameter is tied to
// a particular vendor invoice.
//--------------------------------------------------------------------------------
      Integer theKey = -1;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
      }

//--------------------------------------------------------------------------------
// Check for limiting the invoice list to a particular item.
//--------------------------------------------------------------------------------
      Integer itemKeyFilter = 0;
      if (request.getParameter("itemKeyFilter") != null) {
        itemKeyFilter = new Integer(request.getParameter("itemKeyFilter"));
      }

//--------------------------------------------------------------------------------
// Check for limiting the invoice list to a particular service.
//--------------------------------------------------------------------------------
      Integer serviceKeyFilter = 0;
      if (request.getParameter("serviceKeyFilter") != null) {
        serviceKeyFilter = new Integer(request.getParameter("serviceKeyFilter"));
      }

//--------------------------------------------------------------------------------
// Define and default the internalOnly value to false.
//--------------------------------------------------------------------------------
      Boolean internalUse = false;
      if (request.getParameter("internalUse") != null) {
        internalUse = request.getParameter("internalUse").toString().equalsIgnoreCase("true");
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
        CustomerInvoiceListHeadDO formHDO = getCustomerInvoiceList(request,
                                                                   internalUse,
                                                                   clientNameFilter,
                                                                   invoiceNumberFilter,
                                                                   itemKeyFilter,
                                                                   serviceKeyFilter,
                                                                   pageNo,
                                                                   theKey);
        request.getSession().setAttribute("customerinvoicelistHDO", formHDO);

        // Create the wrapper object for customerinvoicelist.
        CustomerInvoiceListHeadForm formHStr = new CustomerInvoiceListHeadForm();
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
