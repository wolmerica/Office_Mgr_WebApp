/*
 * BundleGetAction.java
 *
 * Created on March 30, 2007, 7:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.bundle;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class BundleGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();


  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private BundleDO buildBundleForm(HttpServletRequest request,
                                   Integer buKey)
   throws Exception, SQLException {

    BundleDO formDO = null;
    ArrayList customerTypeRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey,"
                   + "name,"
                   + "category,"
                   + "customertype_key,"
                   + "duration_hour,"
                   + "duration_minute,"
                   + "note_line1,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM bundle "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, buKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        formDO = new BundleDO();

        formDO.setKey(rs.getInt("thekey"));
        formDO.setName(rs.getString("name"));
        formDO.setCategory(rs.getString("category"));
        formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        formDO.setDurationHour(rs.getByte("duration_hour"));
        formDO.setDurationMinute(rs.getByte("duration_minute"));
        formDO.setNoteLine1(rs.getString("note_line1"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// Retrieve the active customer type values available to assign to a service.
//--------------------------------------------------------------------------------
        CustomerTypeDropList ctdl = new CustomerTypeDropList();
        customerTypeRows = ctdl.getActiveCustomerTypeList(conn);
        formDO.setCustomerTypeForm(customerTypeRows);
      }
      else {
        throw new Exception("Goods & Services Bundle  " + buKey.toString() + " not found!");
      }
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
// Bundle Key
//--------------------------------------------------------------------------------        
      Integer theKey = null;
      if (request.getAttribute("key") != null) {
        theKey = new Integer(request.getAttribute("key").toString());
      }
      else {
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());

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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        BundleDO formDO = buildBundleForm(request,
                                          theKey);
        formDO.setPermissionStatus(usToken);

        request.getSession().setAttribute("bundle", formDO);
        BundleForm formStr = new BundleForm();
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