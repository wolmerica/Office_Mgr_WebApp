package com.wolmerica.service.authenticate;

import com.wolmerica.employee.EmployeeDO;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.server.DefaultServerService;
import com.wolmerica.service.server.ServerService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.service.userstate.UserStateService;
import java.net.InetAddress;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * @author  Richard Wolschlager
 * @date    April 1, 2014
 */

public class DefaultAuthenticateService implements AuthenticateService {

  Logger cat = Logger.getLogger("WOWAPP");
  String cookieName = "OfficeWizardId";

  private PropertyService propertyService = new DefaultPropertyService();
  private ServerService serverService = new DefaultServerService();
  private UserStateService userStateService = new DefaultUserStateService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }
  

  public ServerService getServerService() {
      return serverService;
  }

  public void setServerService(ServerService serverService) {
      this.serverService = serverService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public HttpServletResponse createWowCookie(HttpServletRequest request,
                                             HttpServletResponse response,
                                             String userName,
                                             String password)
   throws Exception {
      try {    
          String userCredential = userName + "||" + password;
          BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
          textEncryptor.setPassword(cookieName);
          String myEncryptedText = textEncryptor.encrypt(userCredential);

          cat.debug("Start createWowCookie");
          Cookie cookie = new Cookie(cookieName, myEncryptedText);
          
          Integer maxAge = new Integer(getPropertyService().getCustomerProperties(request, "app.cookie.maxAge").toString());
          cookie.setMaxAge(maxAge);
          response.addCookie(cookie);
          cat.debug("Finish createWowCookie");
      } catch (Exception e) {
          throw new Exception("createWowCookie() " + e.getMessage());
      }
      return response;
  }  
  
  public HashMap<String, Object> checkWowCookie(HttpServletRequest request) 
   throws Exception {
      HashMap<String, Object> cookieMap = new HashMap<String, Object>();
            
      try { 
          BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
          textEncryptor.setPassword(cookieName);

          Cookie cookie = null;
          Cookie[] cookies = request.getCookies();
          String userCredential;
          if (cookies != null) {
              for (int i = 0; i < cookies.length; i++) {
                  if (cookies[i].getName().equals(cookieName)) {
                      cookie = cookies[i];                      
                      userCredential = textEncryptor.decrypt(cookie.getValue());
                      cat.debug("Found user credential in Cookie: " + userCredential); 
                      StringTokenizer crumbs = new StringTokenizer(userCredential, "||");
                      if (crumbs.hasMoreElements()) 
                          cookieMap.put("userName", crumbs.nextElement());  
                      if (crumbs.hasMoreElements()) 
                          cookieMap.put("password", crumbs.nextElement());                        
                      cat.debug("crumbs[0] " + cookieMap.get("userName") + " crumbs[1] " + cookieMap.get("password")); 
                  }
              }
          }
      } catch (Exception e) {
          throw new Exception("checkWowCookie() " + e.getMessage());
      }
      return cookieMap;
  }
  
  public Cookie voidWowCookie(HttpServletRequest request) 
   throws Exception {
      Cookie cookie = null;
            
      try { 
          Cookie[] cookies = request.getCookies();
          if (cookies != null) {
              for (int i = 0; i < cookies.length; i++) {
                  if (cookies[i].getName().equals(cookieName)) {
                      cat.debug("Found voidWowCookie()");
                      cookie = cookies[i];                        
                      cookie.setValue("");
                      cookie.setPath("/");
                      cookie.setMaxAge(0);
                  }
              }
          }
      } catch (Exception e) {
          throw new Exception("voidWowCookie() " + e.getMessage());
      }
      return cookie;
  }  
  
  public String getInstanceName(HttpServletRequest request)
   throws Exception {
      String instanceName;
      
      try{
        cat.debug("Start getInstanceName()");
        String defaultInstanceName = getPropertyService().getLicenseProperties("application.default.instance");
        instanceName = new String(request.getRequestURL());
        int sp = instanceName.indexOf("WebApp/")+7;
        int ep = instanceName.length();
        instanceName = instanceName.substring(sp,ep);
        ep = instanceName.indexOf("/");
        if (ep > 0) {
          instanceName = instanceName.substring(0,ep);
        } else {
          instanceName = defaultInstanceName;
        }   
        cat.debug("Finish getInstanceName() " + instanceName);        
      } catch (Exception e) {
          throw new Exception("getInstanceName() " + e.getMessage());
      }
      return instanceName;
  }      
 
  public HashMap<String, Object> authenticateCookie(Connection conn,
                                    HttpServletRequest request)
   throws Exception {
      HashMap<String, Object> userMap = new HashMap<String, Object>();
      
      try {       
        cat.debug("Start authenticateCookie()");     
        HashMap cookieMap = checkWowCookie(request);        
        if ((cookieMap.containsKey("userName")) && (cookieMap.containsKey("password"))) {
            cat.debug("call authenticateUser()");
            userMap = authenticateUser(conn, 
                                       request, 
                                       cookieMap.get("userName").toString(),
                                       cookieMap.get("password").toString());
            cat.debug("cookie target = " + userMap.get("target"));
            if (userMap.containsKey("EmployeeDO"))
                cat.debug("containsKey( EmployeeDO )");
        } else {
            cat.debug("No cookie found");
            userMap.put("target" , "login");  
        }            
        
        cat.debug("Finish authenticateCookie()");
      } catch (Exception e) {
          throw new Exception("authenticateUser() " + e.getMessage());
      }
      return userMap;
  }          
  
  public HashMap<String, Object> authenticateUser(Connection conn,
                                  HttpServletRequest request,
                                  String userName,
                                  String password)  
   throws Exception {
      HashMap<String, Object> userMap = new HashMap<String, Object>();
      
      try {       
        cat.debug("Start authenticateUser()");     
        
        userMap.put("target" , "error"); 
        userMap.put("defaultInstanceName", getPropertyService().getLicenseProperties("application.default.instance"));
        userMap.put("instanceName", getInstanceName(request));
        userMap.put("licenseStatus", getServerService().validateServer());
        cat.debug("validateServer().......: " + getServerService().validateServer());
        
        if (userMap.get("licenseStatus").toString().equalsIgnoreCase("Expired"))
            userMap.put("target" , "error");
        else
            userMap.put("target", "success");        
        cat.debug("target  " + userMap.get("target").toString());
        userMap.putAll(getUser(conn,                
                               userName,
                               password));  
        if (userMap.containsKey("EmployeeDO")) {
            cat.debug("getUser() found " + userName);
//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
// Create a user state record to allow for editing of application records.
// This will also provide a quick way to see who is logged into to application.
//--------------------------------------------------------------------------------
            EmployeeDO formDO = (EmployeeDO) userMap.get("EmployeeDO");        
            formDO.setUserStateKey(getUserStateService().createUserToken(conn,
                                                                         formDO.getUserName(),
                                                                         request.getRemoteAddr()));
            request.getSession().setAttribute(getUserStateService().getUserName(), formDO.getUserName());
            request.getSession().setAttribute(getUserStateService().getAdmin(), formDO.getAdminId());
            request.getSession().setAttribute(getUserStateService().getUser(), formDO.getFirstName());
            request.getSession().setAttribute(getUserStateService().getPermission(), formDO.getPermissionSlip());
            request.getSession().setAttribute(getUserStateService().getToken(), formDO.getUserStateKey());
            request.getSession().setAttribute(getUserStateService().getLicense(), userMap.get("licenseStatus"));
//--------------------------------------------------------------------------------
// 02-28-07  Providing support for multiple instances of the office wizard.  The
// instance name value will be used to reference the correct instance resources.
// The default instance is set in the FeatureResources.properties.
//--------------------------------------------------------------------------------
            request.getSession().setAttribute(getUserStateService().getDefaultInstanceName(), userMap.get("defaultInstanceName"));
            request.getSession().setAttribute(getUserStateService().getInstanceName(), userMap.get("instanceName") );
        }                
        cat.debug("Finish authenticateUser()");
      } catch (Exception e) {
          throw new Exception("authenticateUser() " + e.getMessage());
      }
      finally {
          if (conn != null) {
              try {
                  conn.close();
              } catch (SQLException sqle) {
                  cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
              }
              conn = null;
          }          
      }
      return userMap;
  }  
  
  public HashMap<String, Object> getUser(Connection conn,
                         String userName,
                         String password)
   throws Exception, SQLException {

    HashMap<String, Object> userMap = new HashMap<String, Object>();
    String user = null;
    CallableStatement cStmt = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    EmployeeDO formDO = null;
    
    cat.debug("Start getUser()");

    try {
//--------------------------------------------------------------------------------
// Call a GetLoginAttemptForIPCount() procedure to return the count.
// The LicenseProperties file is much smaller than the FeatureProperties.
//--------------------------------------------------------------------------------
      cat.debug("getUser()_3.2");
      String timeInterval = getPropertyService().getLicenseProperties("application.login.lockInterval");
      cat.debug("getUser()_3.3 " + timeInterval);
      Integer maxAttempts = new Integer(getPropertyService().getLicenseProperties("application.login.maxAttempts"));
      cat.debug("getUser()_3.4 " + maxAttempts);
//      String ipAddress = request.getRemoteAddr();
//      if (ipAddress == null)
//        ipAddress = "192.168.1.1";
//      cat.debug("LoginAction_3.5 " + ipAddress);
      String ipAddress = "192.168.1.1";
      try {
        InetAddress thisIp = InetAddress.getLocalHost();
        ipAddress = thisIp.getHostAddress();
      } catch (Exception e) {
        cat.debug("InetAddress Exception : " + e.getMessage());  
      }
      cat.debug("getUser()_3.5 " + ipAddress);      
      
      try {      
        cStmt = conn.prepareCall("{call GetLoginAttemptForIPCount(?,?,?,?,?,?)}");
      } catch (Exception e) {    
        cat.debug("GetLoginAttempForIPCountException : " + e.getMessage());  
      }  
      cat.debug("getUser()_3.6");
      cStmt.setByte(1, new Byte("1"));
      cStmt.setString(2, ipAddress);
      cStmt.setString(3, timeInterval);
      cStmt.setInt(4, maxAttempts);
      cStmt.setBoolean(5, true);
      cat.debug("getUser()_3.7");
      cStmt.execute();
      cat.debug("getUser()_3.8");
      if (cStmt.getInt("loginAttemptCnt") < maxAttempts) {
        userMap.put("loginAttemptCnt", cStmt.getInt("loginAttemptCnt"));
        cat.debug("getUser()_3.9 - Find user " + userName + " " + password); 
        String query = "SELECT thekey,"
                     + "user_name,"
                     + "admin_id,"
                     + "first_name,"
                     + "last_name,"
                     + "permission_slip "
                     + "FROM employee "
                     + "WHERE user_name = ? "
                     + "AND password = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, userName);
        ps.setString(2, password);
        rs = ps.executeQuery();
      
        if (rs.next()) {
          cat.debug("getUser()_3.10 - Found user");          
          formDO = new EmployeeDO();
          formDO.setKey(rs.getInt("thekey"));
          formDO.setUserName(rs.getString("user_name"));
          formDO.setAdminId(rs.getBoolean("admin_id"));
          formDO.setFirstName(rs.getString("first_name"));
          formDO.setLastName(rs.getString("last_name"));
          formDO.setPermissionSlip(rs.getString("permission_slip"));
          userMap.put("EmployeeDO", formDO);
         
//--------------------------------------------------------------------------------
// Call the SetEmployeeLoginStamp() procedure with two IN parameters.
//--------------------------------------------------------------------------------
          cStmt = conn.prepareCall("{call SetEmployeeLoginStamp(?,?)}");
          cStmt.setInt(1, formDO.getKey());
          cStmt.setString(2, ipAddress);
          cStmt.execute();
        } else {
          cat.debug("getUser()_3.11 - Not Found user");  
          cStmt.setByte(1, new Byte("1"));
          cStmt.setString(2, ipAddress);
          cStmt.setString(3, timeInterval);
          cStmt.setInt(4, maxAttempts);
          cStmt.setBoolean(5, false);
          cStmt.execute();
         
          userMap.put("errorMessage", "errors.login.unknown");
          userMap.put("errorValue", userName);
        }        
      } else {
        cat.debug("getUser()_3.12 - Not Found user"); 
        userMap.put("errorMessage", "errors.login.maximum");
        userMap.put("errorValue", maxAttempts);
        userMap.put("errorSuggestion", "errors.login.suggestion");
      }
      cat.debug("Finish getUser()"); 
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
    }
    cat.debug("Finish getUser()");
    return userMap;
  }  
  
  
}