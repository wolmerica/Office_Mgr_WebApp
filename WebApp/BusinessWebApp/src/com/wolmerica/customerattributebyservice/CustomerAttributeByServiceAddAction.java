/*
 * CustomerAttributeByServiceAddAction.java
 *
 * Created on November 16, 2005, 1:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerattributebyservice;

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

public class CustomerAttributeByServiceAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void insertCustomerAttributeByService(HttpServletRequest request,
                                                Integer sKey)

    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    Byte ctKey = new Byte("0");
    Integer cabsKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Select all the customer type records available.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey "
                   + "FROM customertype";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Get the maximum key from the customer attribute by service table.
//--------------------------------------------------------------------------------
        query = "SELECT COUNT(*) AS cabs_cnt, MAX(thekey)+1 AS cabs_key "
              + "FROM customerattributebyservice";
        ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Insert a new row into the customer attribute by service table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO customerattributebyservice "
            + "(thekey,customertype_key,servicedictionary_key,"
            + "fee1_cost,fee2_cost,markup1_rate,"
            + "markup2_rate,discount_threshold,discount_rate,"
            + "update_user, update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";
        ps2 = conn.prepareStatement(query);

      while ( rs.next() ) {
        ctKey = rs.getByte("thekey");

//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the customer attribute by
// service table.  We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
        rs2 = ps.executeQuery();
        if ( rs2.next() ) {
          if ( rs2.getInt("cabs_cnt") > 0 ) {
            cabsKey = rs2.getInt("cabs_key");
          }
        }
        else {
          throw new Exception("CustomerAttributeByService MAX() not found!");
        }

//--------------------------------------------------------------------------------
// Prepare a query to insert a new row into the customer attribute by service table.
//--------------------------------------------------------------------------------
        ps2.setInt(1, cabsKey);
        ps2.setByte(2, ctKey);
        ps2.setInt(3, sKey);
        ps2.setBigDecimal(4, new BigDecimal("0.00"));
        ps2.setBigDecimal(5, new BigDecimal("0.00"));
        ps2.setBigDecimal(6, new BigDecimal("0.0"));
        ps2.setBigDecimal(7, new BigDecimal("0.0"));
        ps2.setBigDecimal(8, new BigDecimal("0.0"));
        ps2.setBigDecimal(9, new BigDecimal("0.0"));
        ps2.setString(10, request.getSession().getAttribute("USERNAME").toString());
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
// Service dictionary key is passed in.
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

      insertCustomerAttributeByService(request, theKey);
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