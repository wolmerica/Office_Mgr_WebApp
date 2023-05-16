/*
 * LogoutAction.java
 *
 * Created on December 05, 2006, 8:07 PM
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

import org.apache.log4j.Logger;

public class LogoutAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");

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
      if (!(request.getParameter("type") == null)) {
        theType = new String(request.getParameter("type"));
      }
//--------------------------------------------------------------------------------
// End the session by invoking the invalidate() method.
//--------------------------------------------------------------------------------
      if (!(request.getSession() == null)) {
        target = "login";
        cat.warn(request.getSession().getAttribute("INSTANCENAME") + "|"
             + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1) + "|"                
             + request.getSession().getAttribute("ACCTNUM") + "|"
             + request.getSession().getAttribute("ACCTNAME") + "|"
             + request.getRemoteAddr() + "|"             
             + "|" + theType + "|" + new Timestamp(new Date().getTime()).toString());

        request.getSession().invalidate();
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