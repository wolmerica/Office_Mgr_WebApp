/*
 * ScheduleGetAction.java
 *
 * Created on September 7, 2006, 10:00 PM
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
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.service.schedule.ScheduleService;
import com.wolmerica.service.schedule.DefaultScheduleService;
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
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class ScheduleGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private ScheduleService scheduleService = new DefaultScheduleService();
  private UserStateService userStateService = new DefaultUserStateService();

  public ScheduleService getScheduleService() {
      return scheduleService;
  }

  public void setScheduleService(ScheduleService scheduleService) {
      this.scheduleService = scheduleService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

//--------------------------------------------------------------------------------
// Introduce a schedule kit in the tools area to provide a common code base
// for retrieving schedule records, one at a time.
// a) ScheduleGetAction.java
// b) ResourceInstanceGetAction.java  (multiple calls for arraylist)
//--------------------------------------------------------------------------------
  private ScheduleDO getSchedule(HttpServletRequest request,
                                 Integer schKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ScheduleDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      formDO = getScheduleService().buildScheduleForm(conn, schKey);
//--------------------------------------------------------------------------------
// The date values must have a value even if we do not edit it in the screen.     
//--------------------------------------------------------------------------------      
      formDO.setAttributeToDate(new Date());

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
  
    private JSONObject getScheduleJSONResponse(ScheduleDO formDO)
    throws Exception {

      JSONObject jsonObj = null;
    
      try {   
        DateFormatter dateFormatter = new DateFormatter();

        jsonObj = new JSONObject();
        jsonObj.put("id", formDO.getKey());
        jsonObj.put("eventTypeKey", formDO.getEventTypeKey());
        jsonObj.put("subject", formDO.getSubject());
        jsonObj.put("customerKey", formDO.getCustomerKey());
        jsonObj.put("clientName", formDO.getClientName());
        jsonObj.put("customerPhone", formDO.getCustomerPhone());
        jsonObj.put("reminderPrefKey", formDO.getReminderPrefKey());
        jsonObj.put("addressId", formDO.getAddressId());
        jsonObj.put("address", formDO.getAddress());
        jsonObj.put("city", formDO.getCity());
        jsonObj.put("state", formDO.getState());        
        jsonObj.put("customerTypeKey", formDO.getCustomerTypeKey());    
        jsonObj.put("attributeToEntity", formDO.getAttributeToEntity());
        jsonObj.put("attributeToDate", dateFormatter.format(formDO.getAttributeToDate()));
        jsonObj.put("sourceTypeKey", formDO.getSourceTypeKey());
        jsonObj.put("sourceKey", formDO.getSourceKey());
        jsonObj.put("attributeToName", formDO.getAttributeToName());
        jsonObj.put("startDate", dateFormatter.format(formDO.getStartDate()));
        jsonObj.put("startHour", formDO.getStartHour());
        jsonObj.put("startMinute", formDO.getStartMinute());
        jsonObj.put("endDate", dateFormatter.format(formDO.getEndDate()));
        jsonObj.put("endHour", formDO.getEndHour());
        jsonObj.put("endMinute", formDO.getEndMinute());
        jsonObj.put("noteLine1", formDO.getNoteLine1());
        jsonObj.put("customerInvoiceKey", formDO.getCustomerInvoiceKey());
        jsonObj.put("statusKey", formDO.getStatusKey());        

        
        String startTime;
        int startHour = formDO.getStartHour();
        String timeOfDay = "AM";
        if (startHour > 11) {
          timeOfDay = "PM";
          startHour = startHour - 12;
        }
        startTime = String.format("%02d", startHour);
        startTime = startTime + ":" + String.format("%02d", formDO.getStartMinute());
        startTime = startTime + " " + timeOfDay; 
        jsonObj.put("startTime", startTime);
      } catch (Exception e) {
          cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
          throw new Exception("getScheduleListJSONResponse() " + e.getMessage());
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
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (request.getAttribute("key") != null) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

      Boolean jsonId = false;
      if (request.getParameter("json") != null) {
        jsonId = Boolean.valueOf(request.getParameter("json").toString());
        cat.warn("jsonId = " + jsonId);
      }
      
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
        ScheduleDO formDO = getSchedule(request, theKey);
        formDO.setPermissionStatus(usToken);
        
        if (jsonId) {
          cat.warn("JSONObject created");
          target = "json";
          JSONObject jsonObj = getScheduleJSONResponse(formDO);

          response.setContentType("application/json");
          response.setHeader("Cache-Control", "no-cache");
          response.setDateHeader("Expires", 0);
          response.setHeader("Pragma", "no-cache");
          response.setStatus(HttpServletResponse.SC_OK);
          response.setContentLength(jsonObj.toString().length());
          response.getWriter().write(jsonObj.toString());
          response.getWriter().flush();
        }
        else {
//--------------------------------------------------------------------------------
// Make sure the event aged events are not allowed to be edited.
//--------------------------------------------------------------------------------
          if (formDO.getActiveId())
            request.setAttribute(getUserStateService().getDisableEdit(), false);
          else
            request.setAttribute(getUserStateService().getDisableEdit(), true);
          request.getSession().setAttribute("schedule", formDO);
          ScheduleForm formStr = new ScheduleForm();
          formStr.populate(formDO);

          form = formStr;
        }  
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