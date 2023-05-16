/*
 * PriceAttributeByItemAddAction.java
 *
 * Created on November 15, 2005, 1:05 PM
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
import com.wolmerica.itemdictionary.ItemDictionaryForm;

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

public class PriceAttributeByItemAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private void insertPriceAttributeByItem(HttpServletRequest request,
                                          Integer idKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    ItemDictionaryForm form = null;

    BigDecimal idSize = null;
    Byte ptKey = null;
    Integer pabiKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Select the item size from the item dictionary table.
//--------------------------------------------------------------------------------
      String query = "SELECT size "
                   + "FROM itemdictionary "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, idKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
	    idSize = rs.getBigDecimal("size");
      }
      else {
        throw new Exception("Item Dictionary key " + idKey.toString() + " not found!");
      }
//--------------------------------------------------------------------------------
// Select all the rows from the price type table whether active or not,
//--------------------------------------------------------------------------------
      query = "SELECT thekey, precedence "
            + "FROM pricetype "
            + "WHERE full_size_id "
            + "AND domain_id = 0 "      // Item domain is zero
            + "ORDER BY precedence";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Get the maximum key from the price attribute by item table.
//--------------------------------------------------------------------------------
        query = "SELECT COUNT(*) AS pabi_cnt, MAX(thekey)+1 AS pabi_key "
              + "FROM priceattributebyitem";
        ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Prepare a query to insert a new row into the price attribute by item table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO priceattributebyitem"
            + "(thekey,itemdictionary_key, pricetype_key,"
            + "size, update_user, update_stamp) "
            + "VALUES (?,?,?,?,?,CURRENT_TIMESTAMP)";
      ps2 = conn.prepareStatement(query);
//------------------------------------------------------------------------------------
// Traverse through Price Type values that are full size.
//------------------------------------------------------------------------------------
      while ( rs.next() ) {
        ptKey = rs.getByte("thekey");
        cat.debug(this.getClass().getName() + ": Insert ptKey = " + ptKey);

//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the price attribute by
// item table.  We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
        rs2 = ps.executeQuery();
        if ( rs2.next() ) {
          if ( rs2.getInt("pabi_cnt") > 0 ) {
            pabiKey = rs2.getInt("pabi_key");
          }
        }
        else {
          throw new Exception("Price Attribute By Item MAX() not found!");
        }

        ps2.setInt(1, pabiKey);
        ps2.setInt(2, idKey);
        ps2.setInt(3, ptKey);
        ps2.setBigDecimal(4, idSize.setScale(2, BigDecimal.ROUND_HALF_UP));
        ps2.setString(5, request.getSession().getAttribute("USERNAME").toString());
        ps2.executeUpdate();
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

      insertPriceAttributeByItem(request, theKey);
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