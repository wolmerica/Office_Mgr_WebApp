/*
 * ItemOriginListAction.java
 *
 * Created on January 17, 2008, 9:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.itemorigin;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
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
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class ItemOriginListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private CustomerService CustomerService = new DefaultCustomerService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

  public CustomerService getCustomerService() {
      return CustomerService;
  }

  public void setCustomerService(CustomerService CustomerService) {
      this.CustomerService = CustomerService;
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

  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }

  private ItemOriginListHeadDO getItemOriginList(HttpServletRequest request,
                                                 Integer idKey,
                                                 Byte originMode,
                                                 Integer pageNo)
   throws Exception, SQLException {

    ItemOriginListHeadDO formHDO = new ItemOriginListHeadDO();
    ItemOriginDO itemOrigin = null;
    ArrayList<ItemOriginDO> itemOrigins = new ArrayList<ItemOriginDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Check the item dictionary table for any duplicate brand_name, size, and size unit.
//--------------------------------------------------------------------------------
      String query = "SELECT brand_name, size, size_unit "
                   + "FROM itemdictionary "
                   + "WHERE thekey = ? ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, idKey);
      rs = ps.executeQuery();
      if (rs.next()) {
        formHDO.setBrandName(rs.getString("brand_name"));
        formHDO.setSize(rs.getBigDecimal("size"));
        formHDO.setSizeUnit(rs.getString("size_unit"));     
      } else {
        throw new Exception("ItemDictionary  " + idKey.toString() + " not found!");          
      }

//--------------------------------------------------------------------------------
// Construct the query based on the origin mode values.
//--------------------------------------------------------------------------------      
      switch(originMode)
      {
        /* Inventory */
        case 1: query = "SELECT vi.thekey AS transaction_key,"
                      + "vi.invoice_date AS transaction_date,"
                      + "vi.invoice_num AS transaction_name,"
                      + "vii.expiration_date,"
                      + "po.vendor_key,"
                      + "po.customer_key,"
                      + "si.quantity "
                      + "FROM vendorinvoice vi, vendorinvoiceitem vii, stockitem si, purchaseorder po "
                      + "WHERE si.itemdictionary_key = ? "
                      + "AND si.active_id "
                      + "AND si.vendorinvoiceitem_key = vii.thekey "
                      + "AND vii.vendorinvoice_key = vi.thekey "
                      + "AND vi.purchaseorder_key = po.thekey "
                      + "ORDER BY vi.invoice_date";
                break;
        /* Ordered */
        case 2: query = "SELECT po.thekey AS transaction_key,"
                      + "DATE(po.create_stamp) AS transaction_date,"
                      + "po.purchase_order_num AS transaction_name,"
                      + "'9999-12-31' AS expiration_date,"
                      + "po.vendor_key,"
                      + "po.customer_key,"
                      + "poi.order_qty AS quantity "
                      + "FROM purchaseorder po, purchaseorderitem poi "
                      + "WHERE po.order_status != 'Complete' "
                      + "AND po.thekey = poi.purchaseorder_key "
                      + "AND poi.itemdictionary_key = ? "
                      + "ORDER BY po.create_stamp";
                break;
        /* Forecast */
        case 3: Integer forecastDays = new Integer(getPropertyService().getCustomerProperties(request,"itemdictionary.forecast.days"));
                query = "SELECT s.thekey AS transaction_key,"
                      + "DATE(wo.start_stamp) AS transaction_date,"
                      + "s.subject AS transaction_name,"
                      + "'9999-12-31' AS expiration_date,"
                      + "null AS vendor_key,"
                      + "s.customer_key,"
                      + "ROUND(ROUND(pabi.size / id.size, 3) * wo.order_qty,2) AS quantity "
                      + "FROM schedule s, workorder wo, pricebyitem pbi, priceattributebyitem pabi, itemdictionary id "
                      + "WHERE wo.start_stamp BETWEEN CURRENT_TIMESTAMP AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL " + forecastDays.toString() + " DAY) "
                      + "AND pabi.itemdictionary_key = ? "
                      + "AND pabi.itemdictionary_key = id.thekey "
                      + "AND pabi.thekey = pbi.priceattributebyitem_key "
                      + "AND pbi.thekey = wo.source_key "
                      + "AND wo.sourcetype_key = 3 "
                      + "AND wo.schedule_key = s.thekey "
                      + "AND s.customerinvoice_key IS NULL "
                      + "ORDER BY wo.start_stamp";
                break;
      }
      ps = conn.prepareStatement(query);
      ps.setInt(1, idKey);
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"itemorigin.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      Short minQty = 0;
      Short maxQty = 0;
      Integer recordCount = 0;
//--------------------------------------------------------------------------------
// Begin to process the item origin rows.
//--------------------------------------------------------------------------------
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          itemOrigin = new ItemOriginDO();
          itemOrigin.setTransactionKey(rs.getInt("transaction_key"));
          itemOrigin.setTransactionDate(rs.getDate("transaction_date"));
          itemOrigin.setTransactionName(rs.getString("transaction_name"));
          itemOrigin.setExpirationDate(rs.getDate("expiration_date"));
          itemOrigin.setVendorKey(rs.getInt("vendor_key"));
          itemOrigin.setCustomerKey(rs.getInt("customer_key"));
          itemOrigin.setQuantity(rs.getBigDecimal("quantity"));

//--------------------------------------------------------------------------------
// Look-up the vendor name with the vendor key if necessary.
//--------------------------------------------------------------------------------
          if (itemOrigin.getVendorKey() != null)
            itemOrigin.setVendorName(getVendorService().getVendorName(conn, itemOrigin.getVendorKey()));

//--------------------------------------------------------------------------------
// Look-up the client name with the customer key if necessary.
//--------------------------------------------------------------------------------
          if (itemOrigin.getCustomerKey() != null)
            itemOrigin.setClientName(getCustomerService().getClientName(conn, itemOrigin.getCustomerKey()));

          itemOrigins.add(itemOrigin);
        }
      }

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
      formHDO.setOriginMode(originMode);
      formHDO.setItemDictionaryKey(idKey);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
      
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (itemOrigins.isEmpty()) {
        itemOrigins.add(new ItemOriginDO());
      }
      formHDO.setItemOriginForm(itemOrigins);

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
   throws Exception, IOException, SQLException, ServletException {

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
// Required item dictionary parameter.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [key] not found!");
      }

//--------------------------------------------------------------------------------
// Required origin mode is defined as an Byte to use a "switch case" statement.
//--------------------------------------------------------------------------------
      Byte originMode = 1;
      if (request.getParameter("mode") != null) {
        originMode = new Byte(request.getParameter("mode"));
      }
      else {
        throw new Exception("Request getParameter [mode] not found!");
      }
      
//--------------------------------------------------------------------------------
// Get the page number value.
//--------------------------------------------------------------------------------
      Integer pageNo = new Integer("1");
      if (request.getParameter("pageNo") != null) {
        if (!(request.getParameter("pageNo").equalsIgnoreCase("")))
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
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        ItemOriginListHeadDO formHDO = getItemOriginList(request,
                                                         theKey,
                                                         originMode,                                                        
                                                         pageNo);
        request.getSession().setAttribute("itemoriginHDO", formHDO);

        ItemOriginListHeadForm formHStr = new ItemOriginListHeadForm();
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
