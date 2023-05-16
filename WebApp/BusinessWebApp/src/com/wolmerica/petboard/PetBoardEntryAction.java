/*
 * PetBoardEntryAction.java
 *
 * Created on June 20, 2007  10:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.petboard;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;
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
import java.util.Date;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class PetBoardEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  
  
  private PetBoardDO setCheckOutDateAndTime(HttpServletRequest request,
                                            PetBoardDO formDO)
   throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;
      
    DataSource ds = null;
    Connection conn = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Get the check-out date, hour, and minute values from work order record.
//--------------------------------------------------------------------------------
      String query = "SELECT DATE(MAX(end_stamp)) AS check_out_date,"
                   + "HOUR(MAX(end_stamp)) AS check_out_hour,"
                   + "MINUTE(MAX(end_stamp)) AS check_out_minute "
                   + "FROM workorder "
                   + "WHERE schedule_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, formDO.getScheduleKey());
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Populate the boarding check out values if they exist.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        formDO.setCheckOutDate(rs.getDate("check_out_date"));
        formDO.setCheckOutHour(rs.getByte("check_out_hour"));
        formDO.setCheckOutMinute(rs.getByte("check_out_minute"));
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
    }
    return formDO;
  }  
 

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
// Required schedule key value.
//--------------------------------------------------------------------------------
      Integer scheduleKey = null;
      if (request.getParameter("scheduleKey") != null)
        scheduleKey = new Integer(request.getParameter("scheduleKey"));
      else
        throw new Exception("Request getParameter [scheduleKey] not found!");

//--------------------------------------------------------------------------------
// Required check-in date value.
//--------------------------------------------------------------------------------
      String dateString = "";
      if (request.getParameter("checkInDate") != null)
        dateString = new String(request.getParameter("checkInDate"));
      else
        throw new Exception("Request getParameter [checkInDate] not found!");
//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date checkInDate = (Date) dateFormatter.unformat(dateString);

//--------------------------------------------------------------------------------
// Required check-in hour value.
//--------------------------------------------------------------------------------
      Byte checkInHour = null;
      if (request.getParameter("checkInHour") != null)
        checkInHour = new Byte(request.getParameter("checkInHour"));
      else
        throw new Exception("Request getParameter [checkInHour] not found!");

//--------------------------------------------------------------------------------
// Required check-in minute value.
//--------------------------------------------------------------------------------
      Byte checkInMinute = null;
      if (request.getParameter("checkInMinute") != null)
        checkInMinute = new Byte(request.getParameter("checkInMinute"));
      else
        throw new Exception("Request getParameter [checkInMinute] not found!");

//--------------------------------------------------------------------------------
// Required pet key value.
//--------------------------------------------------------------------------------
      Integer petKey = null;
      if (request.getParameter("petKey") != null)
        petKey = new Integer(request.getParameter("petKey"));
      else
        throw new Exception("Request getParameter [petKey] not found!");
      
//--------------------------------------------------------------------------------
// Required pet name value.
//--------------------------------------------------------------------------------
      String petName = "";
      if (request.getParameter("petName") != null)
        petName = request.getParameter("petName");
      else
        throw new Exception("Request getParameter [petName] not found!");

//--------------------------------------------------------------------------------
// Required client name value.
//--------------------------------------------------------------------------------
      String clientName = "";
      if (request.getParameter("clientName") != null)
        clientName = request.getParameter("clientName");
      else
        throw new Exception("Request getParameter [clientName] not found!");

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
        PetBoardDO formDO = new PetBoardDO();

        formDO.setScheduleKey(scheduleKey);
        formDO.setCheckInDate(checkInDate);
        formDO.setCheckInHour(checkInHour);
        formDO.setCheckInMinute(checkInMinute);
        formDO.setPetKey(petKey);
        formDO.setPetName(petName);
        formDO.setClientName(clientName);
        formDO.setCheckOutTo(clientName);         
        formDO.setPermissionStatus(usToken);        
        formDO = setCheckOutDateAndTime(request, formDO);
        
        request.getSession().setAttribute("petboard", formDO);
        PetBoardForm formStr = new PetBoardForm();

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