/*
 * ScheduleDuplicateYearAction.java
 *
 * Created on February 9, 2007, 10:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.schedule;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;

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
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

public class ScheduleDuplicateYearAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer duplicateSchedule(HttpServletRequest request,
                                    String fromDate)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    Integer dupCnt = null;
    
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);

      dupCnt = setScheduleDuplicateYear(conn,
                                        myDate,
                                        request.getSession().getAttribute("USERNAME").toString());
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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
    return dupCnt;
  }


  public Integer setScheduleDuplicateYear(Connection conn,
                                          Date fromDate,
                                          String updateUser)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer scheduleCnt = 0;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call SetScheduleDuplicateYear(?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter and exectue the stored procedure.
//--------------------------------------------------------------------------------
      cStmt.setDate(1, new java.sql.Date(fromDate.getTime()));
      cStmt.setString(2, updateUser);
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": sInsertCnt : " + cStmt.getInt("sInsertCnt"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      scheduleCnt = cStmt.getInt("sInsertCnt");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("setScheduleDuplicateYear() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("setScheduleDuplicateYear() " + e.getMessage());
      }
    }
    return scheduleCnt;
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
      String fromDate = null;
      if (request.getParameter("fromDate") != null) 
        fromDate = request.getParameter("fromDate").toString();     
      else 
        throw new Exception("Request getParameter [fromDate] not found!");

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

      Integer dupCnt = duplicateSchedule(request, fromDate);
      request.setAttribute("duplicateCnt", dupCnt);

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