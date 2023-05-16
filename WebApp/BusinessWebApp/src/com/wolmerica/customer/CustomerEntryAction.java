/*
 * CustomerEntryAction.java
 *
 * Created on September 9, 2005, 7:29 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customer;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.customertype.CustomerTypeDO;
import com.wolmerica.customertype.CustomerTypeDropList;
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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

public class CustomerEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private CustomerDO buildCustomerForm(HttpServletRequest request,
                                       String customerName,
                                       String firstName,
                                       String lastName)
   throws Exception, SQLException {

    CustomerDO formDO = new CustomerDO();
    CustomerTypeDO customerTypeRow = null;
    ArrayList customerTypeRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;

//-----------------------------------------------------------------------------
// Initialize the code and account number value to new.
//-----------------------------------------------------------------------------
    formDO.setCodeNum("New");
    formDO.setAcctNum("New");
    formDO.setLineOfBusiness("Home");

    formDO.setFirstName(firstName);
    formDO.setLastName(lastName);
    formDO.setClientName(customerName);
    formDO.setShipTo(customerName);
    formDO.setContactName(customerName);
    formDO.setAcctName(customerName);

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//-----------------------------------------------------------------------------
// Retrieve the active customer type values available to assign to a customer.
//-----------------------------------------------------------------------------
      CustomerTypeDropList ctdl = new CustomerTypeDropList();
      customerTypeRows = ctdl.getActiveCustomerTypeList(conn);
      formDO.setCustomerTypeForm(customerTypeRows);

    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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
    return formDO;
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
      String customerName = "";
      String firstName = "";
      String lastName = "";
      cat.debug(this.getClass().getName() + ": CustomerEntry params ");
      Enumeration paramNames = request.getParameterNames();
      String paramName = "";
      while (paramNames.hasMoreElements()) {
        paramName = (String)paramNames.nextElement();
        cat.debug(this.getClass().getName() + ": paramName = " + paramName);

        if (paramName.equals("customerName")) {
          customerName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[customerName] = " + customerName);
        }
        if (paramName.equals("firstName")) {
          firstName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[firstName] = " + firstName);
        }
        if (paramName.equals("lastName")) {
          lastName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[lastName] = " + lastName);
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
//--------------------------------------------------------------------------------
        CustomerDO formDO = buildCustomerForm(request,
                                              customerName,
                                              firstName,
                                              lastName);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("customer", formDO);
        CustomerForm formStr = new CustomerForm();
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