/*
 * LoginEntryAction.java
 *
 * Created on December 05, 2006, 12:29 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.login;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.service.server.ServerService;
import com.wolmerica.service.server.DefaultServerService;

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

import org.apache.log4j.Logger;

public class LoginEntryAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");
  
  private ServerService serverService = new DefaultServerService();

  public ServerService getServerService() {
      return serverService;
  }

  public void setServerService(ServerService serverService) {
      this.serverService = serverService;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

    cat.debug(this.getClass().getName() + ": execute()");
//--------------------------------------------------------------------------------
// Default target to success
//--------------------------------------------------------------------------------
    String target = "error";

//--------------------------------------------------------------------------------
// 2007-03-07 Initiate the server validation logic.
//--------------------------------------------------------------------------------
    try {
      if (getServerService().validateServer())
        target = "success";
      else
        throw new Exception("Office Wizard server could not initialize.");
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