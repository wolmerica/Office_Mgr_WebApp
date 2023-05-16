/*
 * PriceByServiceAddAction.java
 *
 * Created on August 09, 2006, 12:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricebyservice;

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

public class PriceByServiceAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void insertPriceByService(HttpServletRequest request,
                                   Integer sKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    PreparedStatement ps3 = null;    
    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;    
    
    Byte ptKey = null;    
    Integer pbsKey = new Integer("1");
    Integer cabsKey = null;
    
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
      
//--------------------------------------------------------------------------------
// Prepare a query to select all the customer type rows.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey "
                   + "FROM customerattributebyservice "
                   + "WHERE servicedictionary_key=?";
      ps = conn.prepareStatement(query); 
      ps.setInt(1, sKey); 
      rs = ps.executeQuery();        
      
//--------------------------------------------------------------------------------
// Prepare a query to select all the pricetype rows from the price type table.
//--------------------------------------------------------------------------------
      query = "SELECT thekey, precedence "
            + "FROM pricetype "
            + "WHERE domain_id = 1 "      // Service domain is one
            + "ORDER BY precedence";
      ps = conn.prepareStatement(query);        

//--------------------------------------------------------------------------------
// Get the maximum key from the price attribute by service table.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS pbs_cnt, MAX(thekey)+1 AS pbs_key "
            + "FROM pricebyservice";
      ps2 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Prepare a query to insert a new row into the price by service table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO pricebyservice "
            + "(thekey,pricetype_key,customerattributebyservice_key,"
            + "computed_price,previous_price,over_ride_price,"
            + "update_user,update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";
      ps3 = conn.prepareStatement(query);  

//--------------------------------------------------------------------------------
// Customer attribute by service look-up.
//--------------------------------------------------------------------------------         
      while ( rs.next() ) {
        cabsKey = rs.getInt("thekey");
          
//--------------------------------------------------------------------------------
// Iterate through the service price type rows.
//--------------------------------------------------------------------------------
        rs2 = ps.executeQuery();          
        while ( rs2.next() ) {
          ptKey = rs2.getByte("thekey");            
          
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the price by service table.
//--------------------------------------------------------------------------------
          rs3 = ps2.executeQuery();
          if ( rs3.next() ) {
            if ( rs3.getInt("pbs_cnt") > 0 ) {
              pbsKey = rs3.getInt("pbs_key");
            }
          }
          else {
            throw new Exception("Price By Service MAX() not found!");
          }

//--------------------------------------------------------------------------------
// Insert row into Price By Service for each Customer Type.
//--------------------------------------------------------------------------------
          ps3.setInt(1, pbsKey);
          ps3.setByte(2, ptKey);
          ps3.setInt(3, cabsKey);
          ps3.setBigDecimal(4, new BigDecimal("0"));
          ps3.setBigDecimal(5, new BigDecimal("0"));
          ps3.setBigDecimal(6, new BigDecimal("0"));
          ps3.setString(7, request.getSession().getAttribute("USERNAME").toString());
          ps3.executeUpdate();
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

      insertPriceByService(request, theKey);
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