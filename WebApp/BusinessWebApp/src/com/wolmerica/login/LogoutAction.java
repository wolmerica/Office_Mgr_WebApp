/*
 * LogoutAction.java
 *
 * Created on August 14, 2005, 9:13 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.login;

/**
 *
 * @author Richard
 */

import com.wolmerica.employee.EmployeeDO;
import com.wolmerica.service.authenticate.AuthenticateService;
import com.wolmerica.service.authenticate.DefaultAuthenticateService;
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

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class LogoutAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AuthenticateService authenticateService = new DefaultAuthenticateService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AuthenticateService getAuthenticateService() {
      return authenticateService;
  }

  public void setAuthenticateService(AuthenticateService authenticateService) {
      this.authenticateService = authenticateService;
  }
  
  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private JSONObject getLogoutActionJSONResponse(String status, String errorMessage, String errorValue, String errorSuggestion) 
    throws Exception {
  
      JSONObject jsonObj = null;
    
      try {   
          jsonObj = new JSONObject();
          jsonObj.put("status", status);
          jsonObj.put("errorMessage", errorMessage);
          jsonObj.put("errorValue", errorValue); 
          jsonObj.put("errorSuggestion", errorSuggestion);
      } catch (Exception e) {
          cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
          throw new Exception("getLogoutActionJSONResponse() " + e.getMessage());        
      }
        
      return jsonObj;
  }  
   
  @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws IOException, ServletException {

    // Default target to success
    String target = "success";

    try {
      String theType = null;
      if (request.getParameter("type") != null) {
        theType = new String(request.getParameter("type"));
      }
     
      Boolean jsonId = false;
      if (request.getParameter("json") != null) {
        jsonId = new Boolean(request.getParameter("json").toString());
      }

//--------------------------------------------------------------------------------
// End the session by invoking the invalidate() method.
//--------------------------------------------------------------------------------    
      if (request.isRequestedSessionIdValid()) {
        target = "login";
        
//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
// Call DeleteUserToken() to clean up user state when a user logs out.
//--------------------------------------------------------------------------------
        Integer usKey = (Integer) request.getSession().getAttribute("TOKEN");
        getUserStateService().deleteUserToken(this.getDataSource(request).getConnection(), usKey);
        
        cat.warn(request.getSession().getAttribute(getUserStateService().getInstanceName()) + "|"
               + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1) + "|"
               + request.getSession().getAttribute(getUserStateService().getUser()) + "|"
               + request.getRemoteAddr() + "|"
               + request.getSession().getAttribute(getUserStateService().getLicense()) + "|"
               + theType + "|" + new Timestamp(new Date().getTime()).toString());

        request.getSession().invalidate();
        // TODO Remove cookie
      }
      
      if (jsonId) {
        cat.debug("JSONObject created");       
        target = "json";
        
        JSONObject jsonObj = getLogoutActionJSONResponse("ok", " ", " ", " ");
        
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(jsonObj.toString().length());
        response.getWriter().write(jsonObj.toString());
        response.getWriter().flush();
      }       
      
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