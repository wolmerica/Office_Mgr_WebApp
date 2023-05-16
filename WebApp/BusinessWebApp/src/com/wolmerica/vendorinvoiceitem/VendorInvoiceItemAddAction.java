/*
 * VendorInvoiceItemAddAction.java
 *
 * Created on December 05, 2005, 4:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * Use VendorInvoiceItemAdd to insert the first VendorInvoice related to a
 * purchase order.
 */

package com.wolmerica.vendorinvoiceitem;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
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

import org.apache.log4j.Logger;

public class VendorInvoiceItemAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  public void insertVendorInvoiceItem(HttpServletRequest request,
                                      Integer viKey,
                                      Integer poKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//===========================================================================
// The items that have not yet been checked in are extracted from the
// purchase order items and put into a vendor invoice item for check-in.
//===========================================================================
      String query = "SELECT purchaseorderitem.thekey,"
                   + "purchaseorderitem.itemdictionary_key,"
                   + "purchaseorderitem.order_qty,"
                   + "itemdictionary.first_cost,"
                   + "itemdictionary.unit_cost,"
                   + "itemdictionary.carry_factor "
                   + "FROM purchaseorderitem, itemdictionary "
                   + "WHERE purchaseorder_key=? "
                   + "AND itemdictionary_key = itemdictionary.thekey ";
      cat.debug(this.getClass().getName() + ": Query #1 = " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, poKey);
      rs = ps.executeQuery();

//===========================================================================
// The existing vendor invoice items are checked to find out if an item was
// checked in before and what quantities were checked in.
//===========================================================================
      query = "SELECT count(*) AS record_cnt, SUM(receive_qty) AS receive_qty "
            + "FROM vendorinvoiceitem "
            + "WHERE purchaseorderitem_key=? ";

      cat.debug(this.getClass().getName() + ": Query #2 = " + query);
      ps = conn.prepareStatement(query);

//===========================================================================
// Preparation of a insert query for the vendor invoice item.
//===========================================================================
      query = "INSERT INTO vendorinvoiceitem "
            + "(thekey,"
            + "vendorinvoice_key,"
            + "purchaseorderitem_key,"
            + "pending_qty,"
            + "receive_qty,"
            + "first_cost,"
            + "variant_amount,"
            + "use_tax_cost,"
            + "handling_cost,"
            + "unit_cost,"
            + "sales_tax_id,"
            + "carry_factor,"
            + "expiration_date,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (NULL,?,?,?,?,?,?,?,?,?,"
            + "?,?,'9999-12-31',?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      cat.debug(this.getClass().getName() + ": Insert #1 = " + query);
      ps2 = conn.prepareStatement(query);
      Integer orderQty = 0;
      Integer pendingQty = 0;
      Integer receiveQty = 0;
      Integer receiveCnt = 0;

      while ( rs.next() ) {
//===========================================================================
// Retrieve the order quantity that was in the original purchase order item.
//===========================================================================
        orderQty = rs.getInt("order_qty");
        cat.debug(this.getClass().getName() + ": ItemDictionary_Key : " + rs.getString("itemdictionary_key"));

//===========================================================================
// Request a sum of all the received quantities that have been entered into
// the vendor invoice item.  Is there an outstanding quantity pending check-in.
//===========================================================================
        ps.setInt(1, rs.getInt("thekey"));
        rs2 = ps.executeQuery();

        if ( rs2.next() ) {
          receiveQty = 0;
          receiveCnt = rs2.getInt("record_cnt");
          if ( receiveCnt > 0 ) {
            receiveQty = rs2.getInt("receive_qty");
          }

//===========================================================================
// Items that still have a quantity to be checked in are added to vendor invoice item.
//===========================================================================
          if (orderQty > receiveQty) {
            pendingQty = orderQty - receiveQty;
            cat.debug(this.getClass().getName()
                  + ": Add Item=" + rs.getString("itemdictionary_key")
                  + " Pending Qty=" + pendingQty.toString());

            ps2.setInt(1, viKey);
            ps2.setInt(2, rs.getInt("thekey"));
            ps2.setInt(3, pendingQty);
            ps2.setInt(4, new Integer("0"));
            ps2.setBigDecimal(5, rs.getBigDecimal("first_cost").setScale(2, BigDecimal.ROUND_HALF_UP));
            ps2.setBigDecimal(6, new BigDecimal("0"));
            ps2.setBigDecimal(7, new BigDecimal("0"));
            ps2.setBigDecimal(8, new BigDecimal("0"));
            ps2.setBigDecimal(9, rs.getBigDecimal("unit_cost").setScale(2, BigDecimal.ROUND_HALF_UP));
            ps2.setBoolean(10, true);
            ps2.setBigDecimal(11, rs.getBigDecimal("carry_factor").setScale(2, BigDecimal.ROUND_HALF_UP));
            ps2.setString(12, request.getSession().getAttribute("USERNAME").toString());
            ps2.setString(13, request.getSession().getAttribute("USERNAME").toString());

            ps2.executeUpdate();
	  }
        }
      }
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
      if (rs2 != null) {
        try {
          rs2.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs2 = null;
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
      if (ps2 != null) {
        try {
          ps2.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps2 = null;
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

  public void balanceVendorInvoiceItem(HttpServletRequest request,
                                       Integer poKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;

    try {
//===========================================================================
// There is the case where vendorinvoiceitems may need to be balanced so the
// pending_qty equals receive_qty.  Remove records with receive_qty of zero.
//===========================================================================
      String query = "SELECT vendorinvoiceitem.thekey, "
                   + "receive_qty "
                   + "FROM vendorinvoiceitem, vendorinvoice "
                   + "WHERE purchaseorder_key=? "
                   + "AND vendorinvoice_key = vendorinvoice.thekey "
                   + "AND pending_qty != receive_qty";

      ds = getDataSource(request);
      conn = ds.getConnection();
      cat.debug(this.getClass().getName() + ": Query #1 = " + query);
      ps = conn.prepareStatement(query);

      ps.setInt(1, poKey);
      rs = ps.executeQuery();

//===========================================================================
// Preparation of a delete query for the vendor invoice item.
//===========================================================================
      query = "DELETE FROM vendorinvoiceitem "
            + "WHERE thekey=?";

      cat.debug(this.getClass().getName() + ": Delete #1 = " + query);
      ps = conn.prepareStatement(query);

//===========================================================================
// Preparation of a update query for the vendor invoice item.
//===========================================================================
      query = "UPDATE vendorinvoiceitem SET "
            + "pending_qty = receive_qty "
            + "WHERE thekey=?";

      cat.debug(this.getClass().getName() + ": Update #1 = " + query);
      ps2 = conn.prepareStatement(query);

      while ( rs.next() ) {
        if (rs.getInt("receive_qty") == 0) {
          ps.setInt(1, rs.getInt("thekey"));
          ps.executeUpdate();
        }
        else {
          ps2.setInt(1, rs.getInt("thekey"));
          ps2.executeUpdate();
        }
      }
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
      if (ps2 != null) {
        try {
          ps2.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps2 = null;
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
// Purchase Order Key
//--------------------------------------------------------------------------------
      Integer poKey = null;
      if (request.getAttribute("poKey") != null) {
        poKey = new Integer(request.getAttribute("poKey").toString());
      }
      else {
        throw new Exception("Request getAttribute [poKey] not found!");
      }

//--------------------------------------------------------------------------------
// Vendor Invoice Key
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
          throw new Exception("Request getAttribute/getParameter [key] not found!");
        }
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

//--------------------------------------------------------------------------------
// Set the pending_qty equal to the receive_qty or delete rows where
// the receive_qty equals zero.
//--------------------------------------------------------------------------------
      balanceVendorInvoiceItem(request,
                               poKey);

      insertVendorInvoiceItem(request,
                              theKey,
                              poKey);
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