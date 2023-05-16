/*
 * CustomerInvoiceItemAddToCartAction.java
 *
 * Created on March 23, 2006, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoiceitem;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.itemdictionary.ItemDictionaryListDO;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
        
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
import java.math.MathContext;

import org.apache.log4j.Logger;

public class CustomerInvoiceItemAddToCartAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
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


  private void insertCustomerInvoiceItem(HttpServletRequest request,
                                         Integer ciKey,
                                         Integer pbiKey,
                                         Integer siAvailableQty,
                                         Integer orderQty,
                                         BigDecimal estimatedPrice)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Byte ctKey = null;
    Byte ptKey = null;
    Integer idKey = null;
    BigDecimal pabiSize = null;
    BigDecimal pbiComputedPrice = null;
    Integer ciiKey = null;
    MathContext mc = new MathContext(6);
    BigDecimal siQuantity = new BigDecimal("0");
    Integer viiZeroKey = new Integer("0");
    Integer viiKey = null;
    BigDecimal viiCostBasis = new BigDecimal("0");
    Byte actSourceTypeKey = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// The maximum vendor invoice item key is needed when the available quantity is zero.
//--------------------------------------------------------------------------------      
      String query = "SELECT MAX(thekey) AS vii_key "
                   + "FROM vendorinvoiceitem";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next())
        viiZeroKey = rs.getInt("vii_key");
      else
        throw new Exception("There are no vendor invoice item records in the system.");
//--------------------------------------------------------------------------------
// Retrieve the account type key for the SOURCE of Customer Invoice.
//--------------------------------------------------------------------------------
      query = "SELECT thekey "
            + "FROM accountingtype "
            + "WHERE name = 'SOURCE' "
            + "AND description = 'Customer Invoice'";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next())
        actSourceTypeKey = rs.getByte("thekey");
      else
        throw new Exception("The accounting type record for Customer Invoice not found.");
//--------------------------------------------------------------------------------
// Retrieve the master vendor invoice key from the new customer invoice.
//--------------------------------------------------------------------------------
      query = "SELECT pricebyitem.customertype_key, "
            + "priceattributebyitem.pricetype_key, "
            + "priceattributebyitem.itemdictionary_key, "
            + "priceattributebyitem.size, "
            + getPropertyService().getCustomerProperties(request,"customerinvoiceitem.default.price") + " AS computed_price"
            + " FROM pricebyitem, priceattributebyitem "
            + "WHERE pricebyitem.thekey = ? "
            + "AND priceattributebyitem_key = priceattributebyitem.thekey";
      cat.debug(this.getClass().getName() + ": Query #1 = " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, pbiKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        ctKey = rs.getByte("customertype_key");
        ptKey = rs.getByte("pricetype_key");
        idKey = rs.getInt("itemdictionary_key");
        pabiSize = rs.getBigDecimal("size");
        pbiComputedPrice = rs.getBigDecimal("computed_price");
        cat.debug("Item Dictionary Key = " + idKey);
      }
      else {
        throw new Exception("PriceByItem  " + pbiKey.toString() + " not found!");
      }
//--------------------------------------------------------------------------------
// Check the availability of an item in the stockitem table.
//--------------------------------------------------------------------------------
      query = "SELECT vii.expiration_date,"
            + "vii.thekey,"
            + "id.size,"
            + "si.vendorinvoiceitem_key,"
            + "si.quantity,"
            + "(vii.first_cost- vii.variant_amount) AS first_cost, "
            + "((vii.first_cost - vii.variant_amount) "
            + "+ vii.handling_cost) AS cost_basis "
            + "FROM itemdictionary id, stockitem si, vendorinvoiceitem vii "
            + "WHERE si.itemdictionary_key = ? "
            + "AND si.active_id = ? "
            + "AND id.thekey = si.itemdictionary_key "
            + "AND si.vendorinvoiceitem_key = vii.thekey "
            + "ORDER BY vii.expiration_date, vii.thekey";
      cat.debug(this.getClass().getName() + ": Query #2 = " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, idKey);
      ps.setBoolean(2, true);
      rs = ps.executeQuery();
//------------------------------------------------------------------------------
// Calculate the cost basis to be assigned to the customer invoice item.
//------------------------------------------------------------------------------      
      if ( rs.next() ) {
        viiKey = rs.getInt("vendorinvoiceitem_key");
        viiCostBasis = rs.getBigDecimal("cost_basis").multiply(pabiSize, mc);
        viiCostBasis = viiCostBasis.divide(rs.getBigDecimal("size"), mc);
        cat.debug("viiCostBasis= " + viiCostBasis.toString());
      }
//------------------------------------------------------------------------------
// There is the possiblity that available quantity had changed since the URL was
// created, so we have to once again get the latest available quantity to make
// sure.  We accumulate the quantity value and then make sure it exceeds one.
//------------------------------------------------------------------------------      
      ItemDictionaryListDO idRow = new ItemDictionaryListDO();
      idRow.setKey(idKey);
      idRow.setSize(pabiSize);
      idRow = getItemAndSrvService().getItemAvailability(conn,
                                        idRow.getKey(),
                                        idRow);
      siAvailableQty = idRow.getQtyOnHand();      
      
//--------------------------------------------------------------------------------
// There is nothing further we can do if there is not a single unit of inventory
// which in turn means we need reference to any vendor invoice item to properly
// present to the user that there is not any inventory available to be sold.
//--------------------------------------------------------------------------------
      if (siAvailableQty == 0)
        viiKey = viiZeroKey;

//--------------------------------------------------------------------------------
// Preserve the work order estimated price for the invoice.
//--------------------------------------------------------------------------------
      if (estimatedPrice.compareTo(new BigDecimal("-270")) != 0) {
        pbiComputedPrice = estimatedPrice;
      }

//--------------------------------------------------------------------------------
// Check if we have enough available to sell at a minimum, one unit.
// Default the order quantity to the available quantity for the item.          
//--------------------------------------------------------------------------------
      if (siAvailableQty < orderQty) {
        orderQty = siAvailableQty;        
      } 
      else {
//--------------------------------------------------------------------------------
// Lock stockitem records for this item until this order is complete.
// 04/20/2006 - Add sourcetype_key to stockitem to handle vendor and cust inv.
//--------------------------------------------------------------------------------
        query = "UPDATE stockitem SET "
              + "active_id=?,"
              + "sourcetype_key=?,"
              + "source_key=?,"
              + "update_user=?,"
              + "update_stamp=CURRENT_TIMESTAMP "
              + "WHERE itemdictionary_key = ? "
              + "AND active_id ";
        ps = conn.prepareStatement(query);
        ps.setBoolean(1, false);
        ps.setByte(2, actSourceTypeKey);
        ps.setInt(3, ciKey);
        ps.setString(4, request.getSession().getAttribute("USERNAME").toString());
        ps.setInt(5, idKey);
        ps.executeUpdate();
      }
//--------------------------------------------------------------------------------
// Get the maximum key from the customer invoice item.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS cii_cnt, MAX(thekey)+1 AS cii_key "
            + "FROM customerinvoiceitem ";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the customer invoice
// item table.  We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("cii_cnt") > 0 ) 
          ciiKey = rs.getInt("cii_key");          
      }
      else 
        throw new Exception("The maximum customer invoice item not found.");        
//--------------------------------------------------------------------------------
// Preparation of a insert query for the customer invoice item.
//--------------------------------------------------------------------------------
      query = "INSERT INTO customerinvoiceitem "
            + "(thekey,customerinvoice_key,vendorinvoiceitem_key,"
            + "itemdictionary_key,pricetype_key,"
            + "available_qty,order_qty,"
            + "item_price,cost_basis,discount_rate,"
            + "sales_tax_id,genesis_key,master_key,"
            + "create_user,create_stamp,update_user,update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Insert a new customer invoice item row and set the key
// and master_key values to accordingly.
//--------------------------------------------------------------------------------      
      ps.setInt(1, ciiKey);
      ps.setInt(2, ciKey);
      ps.setInt(3, viiKey);
      ps.setInt(4, idKey);
      ps.setByte(5, ptKey);
      ps.setInt(6, siAvailableQty);
      ps.setInt(7, orderQty);
      ps.setBigDecimal(8, pbiComputedPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(9, viiCostBasis.setScale(3, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(10, new BigDecimal("0"));
      ps.setBoolean(11, false);
      ps.setInt(12, ciiKey);
      ps.setInt(13, ciiKey);
      ps.setString(14, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(15, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();
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
// Customer invoice key for the item to be added too.
//--------------------------------------------------------------------------------        
      Integer theKey = null;
      if (request.getAttribute("key") != null) {
        theKey = new Integer(request.getAttribute("key").toString());
      }
      else {
        if (request.getParameter("key") != null) {
          theKey = new Integer(request.getParameter("key"));
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

//--------------------------------------------------------------------------------
// Price by item key of the item to be added to the invoice.
//--------------------------------------------------------------------------------      
      Integer idKey = null;
      if (request.getParameter("idKey") != null) {
        idKey = new Integer(request.getParameter("idKey"));
      }
      else {
        if (!(request.getAttribute("idKey") == null)) {
          idKey = new Integer(request.getAttribute("idKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [idKey] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[idKey] = " + idKey.toString());
      request.setAttribute("idKey", idKey.toString());
//----------------------------------------------------------------------
// Get availableQty value which indicates maximum quantity available.
//----------------------------------------------------------------------
      Integer availableQty = null;
      if (request.getParameter("availableQty") != null) {
        availableQty = new Integer(request.getParameter("availableQty"));
      }
      else {
        if (request.getAttribute("availableQty") != null) {
          availableQty = new Integer(request.getAttribute("availableQty").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [availableQty] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[availableQty] = " + availableQty.toString());
//----------------------------------------------------------------------
// Get estimated price value if provided from the work order.
//----------------------------------------------------------------------
      BigDecimal estimatedPrice = new BigDecimal("-270");
      if (request.getParameter("estimatedPrice") != null) {
        estimatedPrice = new BigDecimal(request.getParameter("estimatedPrice"));
      }
      else {
        if (request.getAttribute("estimatedPrice") != null) {
          estimatedPrice = new BigDecimal(request.getAttribute("estimatedPrice").toString());
        }
      }
      cat.debug(this.getClass().getName() + ": get[estimatedPrice] = " + estimatedPrice.toString());

//----------------------------------------------------------------------
// Get orderQty value which indicates what is needed for the invoice.
//----------------------------------------------------------------------
      Integer orderQty = 1;
      if (request.getParameter("orderQty") != null) {
        orderQty = new Integer(request.getParameter("orderQty"));
      }
      else {
        if (request.getAttribute("orderQty") != null) {
          orderQty = new Integer(request.getAttribute("orderQty").toString());
        }
      }
      cat.debug(this.getClass().getName() + ": get[orderQty] = " + orderQty.toString());

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

      insertCustomerInvoiceItem(request,
                                theKey,
                                idKey,
                                availableQty,
                                orderQty,
                                estimatedPrice);
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