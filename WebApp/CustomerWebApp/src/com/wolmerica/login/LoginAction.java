/*
 * LoginAction.java
 *
 * Created on December 05, 2006, 12:32 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.login;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.customer.CustomerDO;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class LoginAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");
  
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

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


  private HashMap<String, Object> getUser(HttpServletRequest request,
                          String acctNum,
                          String lastName)
   throws Exception, SQLException {

    HashMap<String, Object> userMap = new HashMap<String, Object>();
    DataSource ds = null;
    Connection conn = null;
    CallableStatement cStmt = null;    
    PreparedStatement ps = null;
    ResultSet rs = null;

    CustomerDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
      if (conn != null)
        cat.debug("LoginAction_3.1 conn exists");
//--------------------------------------------------------------------------------
// Call a GetLoginAttemptForIPCount() procedure to return the count.
//--------------------------------------------------------------------------------
      cat.debug("LoginAction_3.2");
      String timeInterval = getPropertyService().getFeatureProperties("application.login.lockInterval");
      cat.debug("LoginAction_3.3 " + timeInterval);
      Integer maxAttempts = new Integer(getPropertyService().getFeatureProperties("application.login.maxAttempts"));
      cat.debug("LoginAction_3.4 " + maxAttempts);
      String ipAddress = request.getRemoteAddr();
      if (ipAddress == null)
        ipAddress = "192.168.1.1";
      cat.debug("LoginAction_3.5 " + ipAddress);
      cStmt = conn.prepareCall("{call GetLoginAttemptForIPCount(?,?,?,?,?,?)}");
      cat.debug("LoginAction_3.6");
      cStmt.setByte(1, new Byte("2"));
      cStmt.setString(2, ipAddress);
      cStmt.setString(3, timeInterval);
      cStmt.setInt(4, maxAttempts);
      cStmt.setBoolean(5, true);
      cat.debug("LoginAction_3.7");
      cStmt.execute();
      cat.debug("LoginAction_3.8");

      if (cStmt.getInt("loginAttemptCnt") < maxAttempts) {
        userMap.put("loginAttemptCnt", cStmt.getInt("loginAttemptCnt"));
//--------------------------------------------------------------------------------
// Look-up the customer using the account number and last name provided from the
// login screen.  If a match is found the customer will be given access.
//--------------------------------------------------------------------------------
        String query = "SELECT thekey,"
                     + "last_name,"
                     + "acct_num,"
                     + "acct_name,"
                     + "active_id "
                     + "FROM customer "
                     + "WHERE acct_num = ? "
                     + "AND last_name = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, acctNum);
        ps.setString(2, lastName);
        rs = ps.executeQuery();

        if (rs.next()) {
          formDO = new CustomerDO();
          formDO.setKey(rs.getInt("thekey"));
          formDO.setLastName(rs.getString("last_name"));
          formDO.setAcctNum(rs.getString("acct_num"));
          formDO.setAcctName(rs.getString("acct_name"));
          formDO.setActiveId(rs.getBoolean("active_id"));
          cat.debug(this.getClass().getName() + ": Found customer = " + formDO.getLastName());

//--------------------------------------------------------------------------------
// Find out how many customer accounts are associated with the logged in customer.
//--------------------------------------------------------------------------------
          query = "SELECT COUNT(*) AS acct_cnt "
                + "FROM customer "
                + "WHERE primary_key = ?";
          ps = conn.prepareStatement(query);
          ps.setInt(1, formDO.getKey());
          rs = ps.executeQuery();
          if ( rs.next() ) {
            formDO.setMultiAcctId(rs.getInt("acct_cnt") > 1);
          }
          userMap.put("CustomerDO", formDO);
          cat.debug(this.getClass().getName() + ": MultiAcctId() = " + formDO.getMultiAcctId());
        
//--------------------------------------------------------------------------------
// Call the setCustomerLoginStamp procedure with two IN parameters.
//--------------------------------------------------------------------------------        
          cStmt = conn.prepareCall("{call SetCustomerLoginStamp(?,?)}");
          cStmt.setInt(1, formDO.getKey());
          cStmt.setString(2, request.getRemoteAddr());
          cStmt.execute();
        } else {
          cStmt.setByte(1, new Byte("2"));
          cStmt.setString(2, request.getRemoteAddr());
          cStmt.setString(3, timeInterval);
          cStmt.setInt(4, maxAttempts);
          cStmt.setBoolean(5, false);
          cStmt.execute();

          userMap.put("errorMessage", "errors.login.unknown");
          userMap.put("errorValue", acctNum);
        }
      } else {
        userMap.put("errorMessage", "errors.login.maximum");
        userMap.put("errorValue", maxAttempts);
        userMap.put("errorSuggestion", "errors.login.suggestion");
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getUser() " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("getUser() " + e.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("getUser() " + e.getMessage());
        }
        ps = null;
      }
      if (cStmt != null) {
        try {
          cStmt.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("getUser() " + e.getMessage());
        }
        cStmt = null;
      }      
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("getUser() " + e.getMessage());
        }
        conn = null;
      }
    }
    return userMap;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

//--------------------------------------------------------------------------------
// Default target to success
//--------------------------------------------------------------------------------
    String target = "success";

//--------------------------------------------------------------------------------
// Use the LoginForm to get the request parameters
//--------------------------------------------------------------------------------
    String acctNum = ((LoginForm)form).getAcctNum();
    String lastName = ((LoginForm)form).getLastName();

    try {
      String defaultInstanceName = getPropertyService().getFeatureProperties("application.default.instance");
      String instanceName = new String(request.getRequestURL());
      int sp = instanceName.indexOf("WebApp/")+7;
      int ep = instanceName.length();
      instanceName = instanceName.substring(sp,ep);
      ep = instanceName.indexOf("/");
      if (ep > 0)
        instanceName = instanceName.substring(0,ep);
      else
        instanceName = defaultInstanceName;

      HashMap userMap = getUser(request,
                                acctNum,
                                lastName);

//--------------------------------------------------------------------------------
// Set the target to failure
//--------------------------------------------------------------------------------
      if (!(userMap.containsKey("CustomerDO"))) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        cat.debug(instanceName + "|" + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1) + "|"
           + acctNum + " "
           + lastName + "|"
           + request.getRemoteAddr() + "|"
           + "|customer|" + new Timestamp(new Date().getTime()).toString());

        target = "login";
        ActionMessages actionMessages = new ActionMessages();

        if (userMap.containsKey("errorMessage") && (userMap.containsKey("errorValue"))) {
          actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
            new ActionMessage(userMap.get("errorMessage").toString(), userMap.get("errorValue").toString()));
        }
        if (userMap.containsKey("errorSuggestion")) {
          actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
            new ActionMessage(userMap.get("errorSuggestion").toString()));
        }

//--------------------------------------------------------------------------------
// Report any ActionMessages we have discovered back to the original form
//--------------------------------------------------------------------------------
        if (!actionMessages.isEmpty()) {
          saveMessages(request, actionMessages);
        }
      }
      else
      {
        CustomerDO formDO = (CustomerDO) userMap.get("CustomerDO");
        request.getSession().setAttribute(getUserStateService().getAcctKey(), formDO.getKey());
        request.getSession().setAttribute(getUserStateService().getAcctNum(), formDO.getAcctNum());
        request.getSession().setAttribute(getUserStateService().getAcctName(), formDO.getAcctName());
        request.getSession().setAttribute(getUserStateService().getMultiAcct(), formDO.getMultiAcctId());
        request.getSession().setAttribute(getUserStateService().getDefaultInstanceName(), defaultInstanceName);
//--------------------------------------------------------------------------------
// 02-28-07  Providing support for multiple instances of the office wizard.  The
// instance name value will be used to reference the correct customer resources.
//--------------------------------------------------------------------------------
        request.getSession().setAttribute(getUserStateService().getInstanceName(), instanceName);
        cat.debug(instanceName + "|" + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1) + "|"
           + formDO.getAcctNum() + " " 
           + formDO.getLastName() + "|"
           + request.getRemoteAddr() + "|"
           + "| |" + new Timestamp(new Date().getTime()).toString());
      }
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
      target = "error";
      ActionMessages actionMessages = new ActionMessages();
      actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
        new ActionMessage("error.database.error", e.getMessage()));
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