/*
 * ResourceByEventListAction.java
 *
 * Created on January 18, 2007, 2:20 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.resource;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.FormattingException;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

public class ResourceByEventListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private ResourceListHeadDO getResourceByEvent(HttpServletRequest request,
                                                String sFromDate,
                                                String sToDate)
    throws Exception, SQLException {

    ResourceListHeadDO formHDO = new ResourceListHeadDO();
    ResourceDO resourceRow = null;
    ArrayList<ResourceDO> resourceRows = new ArrayList<ResourceDO>();
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the String to a Date like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date dFromDate = (Date) dateFormatter.unformat(sFromDate);
      Date dToDate = (Date) dateFormatter.unformat(sToDate);

//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the workorder records.
//--------------------------------------------------------------------------------
      String query = "SELECT DISTINCT schedule_key "
                   + "FROM workorder "
                   + "WHERE ((DATE(workorder.start_stamp) BETWEEN ? AND ? "
                   + "OR DATE(workorder.end_stamp) BETWEEN ? AND ?) "
                   + "OR (? BETWEEN DATE(workorder.start_stamp) AND DATE(workorder.end_stamp) "
                   + "OR ? BETWEEN DATE(workorder.start_stamp) AND DATE(workorder.end_stamp)))";
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(dFromDate.getTime()));
      ps.setDate(2, new java.sql.Date(dToDate.getTime()));
      ps.setDate(3, new java.sql.Date(dFromDate.getTime()));
      ps.setDate(4, new java.sql.Date(dToDate.getTime()));
      ps.setDate(5, new java.sql.Date(dFromDate.getTime()));
      ps.setDate(6, new java.sql.Date(dToDate.getTime()));
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve resource details for the workorder records.
//--------------------------------------------------------------------------------
      query = "SELECT resource.thekey,"
            + "resource.name "
            + "FROM workorder, resourceinstance, resource "
            + "WHERE workorder.schedule_key = ? "
            + "AND workorder.thekey = resourceinstance.workorder_key "
            + "AND resourceinstance.resource_key = resource.thekey";
      ps = conn.prepareStatement(query);

      Integer recordCount = 0;
      while( rs.next()) {
        ps.setInt(1, rs.getInt("schedule_key"));
        rs2 = ps.executeQuery();
        while (rs2.next()) {
          ++recordCount;
          resourceRow = new ResourceDO();
          resourceRow.setScheduleKey(rs.getInt("schedule_key"));
          resourceRow.setKey(rs2.getInt("thekey"));
          resourceRow.setResourceName(rs2.getString("name"));

          resourceRows.add(resourceRow);
        }
      }

      formHDO.setRecordCount(recordCount);

//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (resourceRows.isEmpty())
        resourceRows.add(new ResourceDO());
      if (permissionRows.isEmpty())
        permissionRows.add(new PermissionListDO());

      formHDO.setResourceForm(resourceRows);
      formHDO.setPermissionListForm(permissionRows);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
      if (rs2 != null) {
        try {
          rs2.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs2 = null;
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
    return formHDO;
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
// The fromDate value must be passed via a parameter or attribute.
//--------------------------------------------------------------------------------
      String fromDate = "";
      if (request.getAttribute("fromDate") != null) {
        fromDate = request.getAttribute("fromDate").toString();
      }
      else
        throw new Exception("Request getParameter [fromDate] not found!");


//--------------------------------------------------------------------------------
// The toDate value must be passed via a parameter or attribute.
//--------------------------------------------------------------------------------
      String toDate = "";
      if (request.getAttribute("toDate") != null) {
        toDate = request.getAttribute("toDate").toString();
      }
      else
        throw new Exception("Request getParameter [toDate] not found!");

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
        ResourceListHeadDO formHDO = getResourceByEvent(request,
                                                        fromDate,
                                                        toDate);

        request.getSession().setAttribute("resourceByEventListHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for resource list.
//--------------------------------------------------------------------------------
        ResourceListHeadForm formHStr = new ResourceListHeadForm();
        formHStr.populate(formHDO);
        form = formHStr;
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
