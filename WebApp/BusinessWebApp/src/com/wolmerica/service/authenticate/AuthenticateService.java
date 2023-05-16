package com.wolmerica.service.authenticate;

import com.wolmerica.permission.PermissionListDO;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author  Richard Wolschlager
 * @date    April 1, 2014
 */
public interface AuthenticateService {

  public HttpServletResponse createWowCookie(HttpServletRequest request,
                                             HttpServletResponse response,
                                             String userName,
                                             String password)
  throws Exception;

  public HashMap checkWowCookie(HttpServletRequest request)
    throws Exception;
  
  public Cookie voidWowCookie(HttpServletRequest request) 
   throws Exception;  

  public String getInstanceName(HttpServletRequest request)
   throws Exception;
  
  public HashMap authenticateCookie(Connection conn,
                                    HttpServletRequest request)  
   throws Exception;  
  
  public HashMap authenticateUser(Connection conn,
                                  HttpServletRequest request,
                                  String userName,
                                  String password)  
   throws Exception;  
  
  public HashMap getUser(Connection conn,
                         String userName,
                         String password)
   throws Exception, SQLException;  
  
}
