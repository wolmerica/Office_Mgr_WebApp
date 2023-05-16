/*
 * PriceAttributeByItemEditAction.java
 *
 * Created on September 11, 2005, 1:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.priceattributebyitem;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class PriceAttributeByItemEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private void updatePriceAttributeByItem(HttpServletRequest request,
                                            ActionForm form)
   throws Exception, IOException, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;

    Integer ipaKey = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      PriceAttributeByItemHeadDO formHDO = (PriceAttributeByItemHeadDO) request.getSession().getAttribute("priceattributebyitemHDO");

//--------------------------------------------------------------------------------
// Prepare a query to update a row in the price attribute by item table.
//--------------------------------------------------------------------------------
      String query = "UPDATE priceattributebyitem SET "
                   + "size=?,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);

      // Traverse the priceAttributeByItemForm object.
      ArrayList pabiList = formHDO.getPriceAttributeByItemForm();
      PriceAttributeByItemDO formDO = null;

      // Iterate through the rows in the list.
      for (int j=0; j < pabiList.size(); j++) {
        formDO = (PriceAttributeByItemDO) pabiList.get(j);

        if (formDO.getSize().compareTo(new BigDecimal("0")) > 0) {
          if ( formDO.getKey() < 0) {
            // Insert row into Price Attribute By Item.
            insertPriceAttributeByItem(request,
                                       conn,
                                       formHDO.getItemDictionaryKey(),
                                       formDO.getPriceTypeKey(),
                                       formDO.getSize());

          } else {
            // Update row into Price Attribute By Item.
            ps.setBigDecimal(1, formDO.getSize().setScale(2, BigDecimal.ROUND_HALF_UP));
            ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
            ps.setInt(3, formDO.getKey());
            ps.executeUpdate();
          }
        } else {
          // Delete row from Price Attribute By Item.
          deletePriceAttributeByItem(conn,
                                     formHDO.getItemDictionaryKey(),
                                     formDO.getPriceTypeKey());
        }
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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

  private void insertPriceAttributeByItem(HttpServletRequest request,
                                          Connection conn,
                                          Integer idKey,
                                          Byte ptKey,
                                          BigDecimal ptSize)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    PreparedStatement ps3 = null;
    PreparedStatement ps4 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    Integer pabiKey = null;
    Integer pbiKey = null;

    try {
//--------------------------------------------------------------------------------
// Get the maximum key from the price attribute by item table.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS pabi_cnt, MAX(thekey)+1 AS pabi_key "
                   + "FROM priceattributebyitem";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the price attribute by
// item table.  We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("pabi_cnt") > 0 ) {
          pabiKey = rs.getInt("pabi_key");
        }
      }
      else {
        throw new Exception("PriceAttributeByItem MAX() not found!");
      }

//--------------------------------------------------------------------------------
// Prepare a query to insert a new row into the price attribute by item table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO priceattributebyitem "
            + "(thekey,itemdictionary_key, pricetype_key,"
            + "size, update_user, update_stamp) "
            + "VALUES (?,?,?,?,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Select all the rows from the customer type table whether active or not,
//--------------------------------------------------------------------------------
      query = "SELECT thekey "
            + "FROM customertype";
      ps2 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Get the maximum key from the price attribute by item table.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS pbi_cnt, MAX(thekey)+1 AS pbi_key "
            + "FROM pricebyitem";
      ps3 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Prepare a query to insert a new row into the price by item table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO pricebyitem "
            + "(thekey,customertype_key, priceattributebyitem_key,"
            + "computed_price, previous_price, over_ride_price,"
            + "update_user, update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";
      ps4 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Insert row into price attribute by item table.
//--------------------------------------------------------------------------------
      ps.setInt(1, pabiKey);
      ps.setInt(2, idKey);
      ps.setByte(3, ptKey);
      ps.setBigDecimal(4, ptSize);
      ps.setString(5, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// Select all the rows from the customer type table whether active or not,
//--------------------------------------------------------------------------------
      rs = ps2.executeQuery();
      while (rs.next()) {
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the price by item table.
// We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
        rs2 = ps3.executeQuery();
        if ( rs2.next() ) {
          if ( rs2.getInt("pbi_cnt") > 0 ) {
            pbiKey = rs2.getInt("pbi_key");
          }
        }
        else {
          throw new Exception("PriceByItem MAX() not found!");
        }

//--------------------------------------------------------------------------------
// Insert row into Price By Item for each Customer Type.
//--------------------------------------------------------------------------------
        ps4.setInt(1, pbiKey);
        ps4.setByte(2, rs.getByte("thekey"));
        ps4.setInt(3, pabiKey);
        ps4.setBigDecimal(4, new BigDecimal("0"));
        ps4.setBigDecimal(5, new BigDecimal("0"));
        ps4.setBigDecimal(6, new BigDecimal("0"));
        ps4.setString(7, request.getSession().getAttribute("USERNAME").toString());
        ps4.executeUpdate();
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
      if (ps3 != null) {
        try {
          ps3.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps3 = null;
      }
      if (ps4 != null) {
        try {
          ps4.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps4 = null;
      }
    }
  }

  private void deletePriceAttributeByItem(Connection conn,
                                          Integer idKey,
                                          Byte ptKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    PreparedStatement ps3 = null;
    ResultSet rs = null;

    Integer pabiKey = null;

    try {
//--------------------------------------------------------------------------------
// Select the keys from the price attribute by item table.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey "
                   + "FROM priceattributebyitem "
                   + "WHERE itemdictionary_key = ? "
                   + "AND pricetype_key = ?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Delete the rows from price by item table.
//--------------------------------------------------------------------------------
      query = "DELETE FROM pricebyitem "
            + "WHERE priceattributebyitem_key = ?";
      ps2 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Delete the rows from price by item table.
//--------------------------------------------------------------------------------
      query = "DELETE FROM priceattributebyitem "
            + "WHERE itemdictionary_key = ? "
            + "AND pricetype_key = ?";
      ps3 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Get the new key from price attribute by item.
//--------------------------------------------------------------------------------
      ps.setInt(1, idKey);
      ps.setByte(2, ptKey);
      rs = ps.executeQuery();

      if (rs.next()) {
        pabiKey = rs.getInt("thekey");
//--------------------------------------------------------------------------------
// Delete the rows from the price by item table.
//--------------------------------------------------------------------------------
        ps2.setInt(1, pabiKey);
        ps2.executeUpdate();

//--------------------------------------------------------------------------------
// Delete the rows from the price attribute by item table.
//--------------------------------------------------------------------------------
        ps3.setInt(1, idKey);
        ps3.setByte(2, ptKey);
        ps3.executeUpdate();
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
      if (ps3 != null) {
        try {
          ps3.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps3 = null;
      }
    }
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws Exception, SQLException {

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
// Item dictionary key is passed in.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

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

      // Price adjustment - Passes row id via the request attribute.
      Byte theRow = null;
      if (!(request.getParameter("row") == null)) {
        theRow = new Byte(request.getParameter("row"));
      }
      else {
        if (!(request.getAttribute("row") == null)) {
          theRow = new Byte(request.getAttribute("row").toString());
        }
      }
      if (theRow != null)
        request.setAttribute("row", theRow.toString());

      // Check if a customer type key via the request attribute.
      Byte ctKey = null;
      if (!(request.getParameter("ctkey") == null)) {
        ctKey = new Byte(request.getParameter("ctkey"));
      }
      else {
        if (!(request.getAttribute("ctkey") == null)) {
          ctKey = new Byte(request.getAttribute("ctkey").toString());
        }
      }
      if (ctKey != null)
        request.setAttribute("ctkey", ctKey.toString());

      // Check if a price type key via the request attribute.
      Byte ptKey = null;
      if (!(request.getParameter("ptkey") == null)) {
        ptKey = new Byte(request.getParameter("ptkey"));
      }
      else {
        if (!(request.getAttribute("ptkey") == null)) {
          ptKey = new Byte(request.getAttribute("ptkey").toString());
        }
      }
      if (ptKey != null)
        request.setAttribute("ptkey", ptKey.toString());

      updatePriceAttributeByItem(request,form);
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