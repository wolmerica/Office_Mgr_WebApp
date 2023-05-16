/*
 * BundleDetailAddToCartAction.java
 *
 * Created on March 30, 2007, 07:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.bundledetail;

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

import org.apache.log4j.Logger;

public class BundleDetailAddToCartAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();


  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private void insertBundleDetail(HttpServletRequest request,
                                  Integer buKey,
                                  Byte sourceTypeKey,
                                  Integer pbKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer budKey = new Integer("1");    

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
     
//===========================================================================
// Get the maximum key from the bundle detail.
//===========================================================================
      String query = "SELECT COUNT(*) AS bud_cnt, MAX(thekey)+1 AS bud_key "
                   + "FROM bundledetail";
      ps = conn.prepareStatement(query);

//===========================================================================
// Query to retrieve the maximum key value in the bundle detail table.
//===========================================================================
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("bud_cnt") > 0 ) {
           budKey = rs.getInt("bud_key");
        }
      }
      else {
        throw new Exception("BundleDetail  " + buKey.toString() + " not found!");
      }

//===========================================================================
// Preparation of a insert query for the bundle detail.
//===========================================================================
      query = "INSERT INTO bundledetail "
            + "(thekey,bundle_key,"
            + "sourcetype_key,source_key," 
            + "order_qty,"
            + "create_user,create_stamp,"
            + "update_user,update_stamp) "
            + "VALUES (?,?,?,?,?,"
            + "?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);

//===========================================================================
// Insert a new customer invoice service row.
// Store the price by key instead of the item dictionary key.
// Price type key is not being populated due to the change.
//===========================================================================
      ps.setInt(1, budKey);
      ps.setInt(2, buKey);
      ps.setByte(3, sourceTypeKey);
      ps.setInt(4, pbKey);
      ps.setInt(5, new Integer("1"));
      ps.setString(6, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(7, request.getSession().getAttribute("USERNAME").toString());
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
// Goods and services bundle key.
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

//--------------------------------------------------------------------------------
// Price by item or service key.
//--------------------------------------------------------------------------------
      Integer pbKey = null;
      Byte sourceTypeKey = null;
      if (!(request.getParameter("pbiKey") == null)) {
        pbKey = new Integer(request.getParameter("pbiKey"));
        sourceTypeKey = new Byte("3");
      }
      else {
        if (!(request.getParameter("pbsKey") == null)) {
          pbKey = new Integer(request.getParameter("pbsKey"));
          sourceTypeKey = new Byte("6");
        }          
        else {
          throw new Exception("Request getParameter/getAttribute [pb*Key] not found!");
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

      insertBundleDetail(request, theKey, sourceTypeKey, pbKey);
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