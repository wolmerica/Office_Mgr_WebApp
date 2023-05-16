/*
 * LoginAction.java
 *
 * Created on August 14, 2005, 9:13 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.login;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeeDO;
import com.wolmerica.service.authenticate.AuthenticateService;
import com.wolmerica.service.authenticate.DefaultAuthenticateService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.server.ServerService;
import com.wolmerica.service.server.DefaultServerService;
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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class LoginAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AuthenticateService authenticateService = new DefaultAuthenticateService();
  private PropertyService propertyService = new DefaultPropertyService();
  private ServerService serverService = new DefaultServerService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AuthenticateService getAuthenticateService() {
      return authenticateService;
  }

  public void setAuthenticateService(AuthenticateService authenticateService) {
      this.authenticateService = authenticateService;
  }
  
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

    
  private JSONObject getLoginActionJSONResponse(HashMap userMap) 
    throws Exception {
  
      JSONObject jsonObj = null;
    
      try {   
          jsonObj = new JSONObject();
          if (!(userMap.containsKey("EmployeeDO"))) {
              // Login Error             
              jsonObj.put("status", "error");
              jsonObj.put("key", " ");
              jsonObj.put("adminId", " ");
              jsonObj.put("firstName", " ");
              jsonObj.put("lastName", " ");
              jsonObj.put("errorMessage", " ");
              if (userMap.containsKey("errorMessage"))
                  jsonObj.put("errorMessage", userMap.get("errorMessage").toString());   
              jsonObj.put("errorValue", " ");
              if (userMap.containsKey("errorValue"))
                  jsonObj.put("errorValue", userMap.get("errorValue").toString());                 
              jsonObj.put("errorSuggestion", " ");
              if (userMap.containsKey("errorSuggestion"))
                  jsonObj.put("errorSuggestion", userMap.get("errorSuggestion").toString());                   
          } else {
          // Login Success
              EmployeeDO formDO = (EmployeeDO) userMap.get("EmployeeDO");
              jsonObj.put("status", "ok");
              jsonObj.put("key", formDO.getKey());
              jsonObj.put("adminId", formDO.getAdminId());
              jsonObj.put("firstName", formDO.getFirstName());
              jsonObj.put("lastName", formDO.getLastName());
              jsonObj.put("errorMessage", " ");              
              jsonObj.put("errorValue", " ");
              jsonObj.put("errorSuggestion", " ");
          }
      } catch (Exception e) {
          cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
          throw new Exception("getLoginActionJSONResponse() " + e.getMessage());        
      }
        
      return jsonObj;
  }  

  @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

//--------------------------------------------------------------------------------
// Default target to error
//--------------------------------------------------------------------------------
    String target = "error";

//--------------------------------------------------------------------------------
// Use the LoginForm to get the request parameters
//--------------------------------------------------------------------------------
    String userName = ((LoginForm)form).getUserName();
    String password = ((LoginForm)form).getPassword();
    Boolean jsonId = false;
    if (request.getParameter("json") != null) {
        jsonId = new Boolean(request.getParameter("json").toString());
    }
      
    try {
        HashMap userMap = getAuthenticateService().authenticateUser(this.getDataSource(request).getConnection(),
                                                                    request,
                                                                    userName,
                                                                    password);        
        target = userMap.get("target").toString();
//--------------------------------------------------------------------------------
// Set the target to failure
//--------------------------------------------------------------------------------
        if (!(userMap.containsKey("EmployeeDO"))) {
            target = "login";
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            cat.error(userMap.get("instanceName")  + "|"
                 + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1) + "|"
                 + userName + "|"
                 + request.getRemoteAddr() + "|"
                 + "employee" + "|" + new Timestamp(new Date().getTime()).toString());
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
        } else {
            cat.warn(request.getSession().getAttribute(getUserStateService().getInstanceName()) + "|"
                   + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1) + "|"
                   + request.getSession().getAttribute(getUserStateService().getUser()) + "|"
                   + request.getRemoteAddr() + "|"
                   + request.getSession().getAttribute(getUserStateService().getLicense()) + "|"
                   + new Timestamp(new Date().getTime()).toString());
            
            getUserStateService().releaseExpiredUserToken(this.getDataSource(request).getConnection());
        }
      
        if (jsonId) {
            cat.debug("JSONObject created");            
            target = "json";
            JSONObject jsonObj = getLoginActionJSONResponse(userMap);

            response.setContentType("application/json");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setHeader("Pragma", "no-cache");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentLength(jsonObj.toString().length());
            response.getWriter().write(jsonObj.toString());
            response.getWriter().flush();
        }          
        cat.debug(this.getClass().getName() + ": Final target : " + target);        
    } catch (Exception e) {
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