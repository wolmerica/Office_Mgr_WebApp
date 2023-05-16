/*
 * PriceByItemAddAction.java
 *
 * Created on November 16, 2005, 1:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricebyitem;

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

public class PriceByItemAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void insertPriceByItem(HttpServletRequest request,
                                   Integer idKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    PreparedStatement ps3 = null;
    PreparedStatement ps4 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    Byte ptKey = null;    
    Integer pabiKey = null;
    Byte ctKey = null;
    Integer pbiKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Prepare a query to select all the pricetype rows from the price type table.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey, precedence "
                   + "FROM pricetype "
                   + "WHERE full_size_id "
                   + "AND domain_id = 0 "      // Item domain is zero              
                   + "ORDER BY precedence";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Prepare a query to select the price attribute by item rows.
//--------------------------------------------------------------------------------
      query = "SELECT thekey "
            + "FROM priceattributebyitem "
            + "WHERE itemdictionary_key=? "
            + "AND pricetype_key=?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Prepare a query to select all the customer type rows.
//--------------------------------------------------------------------------------
      query = "SELECT thekey, precedence "
            + "FROM customertype "
            + "ORDER BY precedence ";
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
// Iterate through the item price type rows.
//--------------------------------------------------------------------------------
      while ( rs.next() ) {
        ptKey = rs.getByte("thekey");

//--------------------------------------------------------------------------------
// Price attribute by item look-up.
//--------------------------------------------------------------------------------
        ps.setInt(1, idKey);
        ps.setByte(2, ptKey);
        rs2 = ps.executeQuery();
        if ( rs2.next() ) {
          pabiKey = rs2.getInt("thekey");
        }
        else {
          throw new Exception("Price Attribute By Item key " + idKey.toString() + " not found!");
        }
        cat.debug(this.getClass().getName() + ": pabiKey = " + pabiKey.toString());
//--------------------------------------------------------------------------------
// Iterate through the customer type rows.
//--------------------------------------------------------------------------------
        rs2 = ps2.executeQuery();
        while ( rs2.next() ) {
          ctKey = rs2.getByte("thekey");
          cat.debug(this.getClass().getName() + ": ctKey = " + ctKey);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the price by item table.
// We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
          rs3 = ps3.executeQuery();
          if ( rs3.next() ) {
            if ( rs3.getInt("pbi_cnt") > 0 ) {
              pbiKey = rs3.getInt("pbi_key");
            }
          }
          else {
            throw new Exception("PriceByItem MAX() not found!");
          }
          cat.debug(this.getClass().getName() + ": pbiKey = " + pbiKey.toString());

//--------------------------------------------------------------------------------
// Insert row into Price By Item for each Customer Type.
//--------------------------------------------------------------------------------
          ps4.setInt(1, pbiKey);
          ps4.setByte(2, ctKey);
          ps4.setInt(3, pabiKey);
          ps4.setBigDecimal(4, new BigDecimal("0"));
          ps4.setBigDecimal(5, new BigDecimal("0"));
          ps4.setBigDecimal(6, new BigDecimal("0"));
          ps4.setString(7, request.getSession().getAttribute("USERNAME").toString());
          ps4.executeUpdate();
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
      if (rs3 != null) {
        try {
          rs3.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs3 = null;
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


      insertPriceByItem(request, theKey);
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