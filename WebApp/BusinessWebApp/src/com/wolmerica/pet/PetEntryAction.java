/*
 * PetEntryAction.java
 *
 * Created on December 18, 2005, 7:42 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.pet;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.Logger;

public class PetEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  
  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
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
// Traverse the request parameters and populate the appropriate values.
//--------------------------------------------------------------------------------
      Integer customerKey = null;
      String customerName = "";
      String petName = "";
      Byte petSexId = new Byte("0");
      Byte neuterId = new Byte("0");
      String petAge = "";
      Integer speciesKey = null;
      String speciesName = "";
      cat.debug(this.getClass().getName() + ": PetEntry params ");
      Enumeration paramNames = request.getParameterNames();
      String paramName = "";
      while (paramNames.hasMoreElements()) {
        paramName = (String)paramNames.nextElement();
        cat.debug(this.getClass().getName() + ": paramName = " + paramName);

        if (paramName.equals(new String("customerKey"))) {
          customerKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[customerKey] = " + customerKey.toString());
        }
        if (paramName.equals(new String("customerName"))) {
          customerName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[customerName] = " + customerName);
        }
        if (paramName.equals(new String("petName"))) {
          petName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[petName] = " + petName);
        }
        if (paramName.equals(new String("petSexId"))) {
          petSexId = new Byte(request.getParameter("petSexId").toString());
          cat.debug(this.getClass().getName() + ": get[petSexId] = " + petSexId);
        }
        if (paramName.equals(new String("neuterId"))) {
          neuterId = new Byte(request.getParameter("neuterId").toString());
          cat.debug(this.getClass().getName() + ": get[neuterId] = " + neuterId);
        }
        if (paramName.equals(new String("petAge"))) {
          petAge = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[petAge] = " + petAge);
        }
        if (paramName.equals(new String("speciesKey"))) {
          speciesKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[speciesKey] = " + speciesKey.toString());
        }
        if (paramName.equals(new String("speciesName"))) {
          speciesName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[speciesName] = " + speciesName);
        }
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
// Default the neutered and last check date to 1/1/1990.
//--------------------------------------------------------------------------------
        Calendar rightNow = Calendar.getInstance();
        rightNow.set(1990,0,1);

        PetDO formDO = new PetDO();

        formDO.setCustomerKey(customerKey);
        formDO.setClientName(customerName);
        formDO.setNeuteredId(neuterId);
        formDO.setPetName(petName);
        formDO.setPetSexId(petSexId);
        formDO.setSpeciesKey(speciesKey);
        formDO.setSpeciesName(speciesName);

        formDO.setNeuteredDate(new Date(rightNow.getTime().getTime()));
        formDO.setLastCheckDate(new Date(rightNow.getTime().getTime()));

        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("pet", formDO);
        PetForm formStr = new PetForm();

        formStr.populate(formDO);
        form = formStr;
      }
      catch (FormattingException fe) {
        fe.getMessage();
      }

      if ( form == null ) {
        cat.debug(this.getClass().getName() + ":---->form is null<----");
      }
      if ("request".equals(mapping.getScope())) {
        cat.debug(this.getClass().getName() + ":---->request.setAttribute<----");
        request.setAttribute(mapping.getAttribute(), form);
      }
      else {
        cat.debug(this.getClass().getName() + ":---->session.setAttribute<----");
        request.getSession().setAttribute(mapping.getAttribute(), form);
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