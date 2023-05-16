/*
 * ScheduleSendIMAction.java
 *
 * Created on July 31, 2007, 07:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.schedule;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService; 
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;

import com.wolmerica.tools.yahoo.YahooMessengerAPI;

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

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class ScheduleSendIMAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private Integer sendMessage(HttpServletRequest request,
                              String sendTo)
   throws Exception {

    ScheduleListHeadDO formHDO = (ScheduleListHeadDO) request.getSession().getAttribute("scheduleListHDO");

    String errMsg = "";
    Integer sendRecordCnt = 0;

    try {
//--------------------------------------------------------------------------------
// Traverse the ScheduleListHead object.
//--------------------------------------------------------------------------------
      ArrayList eventList = formHDO.getScheduleForm();
      ScheduleDO formDO = null;

      String sendSubject = getPropertyService().getCustomerProperties(request,"schedule.send.subject");
      String sendNewLine = getPropertyService().getCustomerProperties(request,"schedule.send.newline");
//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      String startDate = "";
      int startHour = 0;
      String startMinute = "00";
      String timeOfDay = "AM";

      String sendFrom = getPropertyService().getCustomerProperties(request,"schedule.send.id");
      String sendPassword = getPropertyService().getCustomerProperties(request,"schedule.send.pw");
      String sendMsg = sendSubject + "\n";
      String eventTypeCode = "schedule.send.eventtype.";
      String locationCode = "schedule.send.location.";
      String statusCode = "schedule.send.status.";

      sendRecordCnt = formHDO.getRecordCount();

//--------------------------------------------------------------------------------
// Only traverse the array if the record count is greater than zero.
//--------------------------------------------------------------------------------
      if (sendRecordCnt > 0) {
        for (int j=0; j < eventList.size(); j++) {
          formDO = (ScheduleDO) eventList.get(j);

          startDate = dateFormatter.format(formDO.getStartDate());
          startHour = formDO.getStartHour().intValue();
          if (startHour > 11)
            timeOfDay = "PM";
          else
            timeOfDay = "AM";

          if (startHour > 12)
            startHour = startHour - 12;

          startMinute = formDO.getStartMinute().toString();
          if (formDO.getStartMinute() < 10)
            startMinute = "0" + startMinute;
//--------------------------------------------------------------------------------
// Construct the message to be delivered via the yahoo instant messenger.
//--------------------------------------------------------------------------------
          sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,statusCode + formDO.getStatusKey().toString()) + " ";
          sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,eventTypeCode + formDO.getEventTypeKey().toString()) + sendNewLine;
          sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,locationCode + formDO.getLocationId()) + sendNewLine;
          sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,"schedule.send.datetime") + " " + startDate + " " + startHour + ":" + startMinute + timeOfDay + sendNewLine;
          sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,"schedule.send.subject") + " " + formDO.getSubject() + sendNewLine;
          sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,"schedule.send.client") + " " + formDO.getClientName() + sendNewLine;
          sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,"schedule.send.phone") + " " + formDO.getCustomerPhone() + sendNewLine;
          if (formDO.getAttributeToName().length() > 1)
            sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,"schedule.send.regarding") + " " + formDO.getAttributeToName() + sendNewLine;
          if (formDO.getAttributeToDate() != null) {
            startDate = dateFormatter.format(formDO.getAttributeToDate());
            sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,"schedule.send.dob") + " " + startDate + sendNewLine;
          }
          if (formDO.getAddress().length() > 0)
            sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,"schedule.send.address") + " " + formDO.getAddress() + sendNewLine;
          if (formDO.getCity().length() > 0)
            sendMsg = sendMsg + getPropertyService().getCustomerProperties(request,"schedule.send.city") + " " + formDO.getCity() + ", " + formDO.getState() + sendNewLine;
          sendMsg = sendMsg + sendNewLine;
        }

        cat.debug(this.getClass().getName() + ": Yahoo! IM Message: " + sendMsg);
//--------------------------------------------------------------------------------
// Instantiate the YahooMessengerAPI and send the schedule details.
//--------------------------------------------------------------------------------
        errMsg = new  YahooMessengerAPI().sendYahooIM(sendFrom,
                                                      sendPassword,
                                                      sendTo,
                                                      sendMsg);
      }
      else {
        errMsg = new String ("There are no scheduled events to be sent.");
      }

      if (errMsg.length() > 1)
        throw new Exception("The Yahoo Messenger API incurred an error:");
    }
    catch (Exception e) {
      throw new Exception(e.getMessage()+ " " + errMsg);
    }
    return sendRecordCnt;
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
      String sendTo = "";
      if (request.getParameter("sendTo") != null) {
        sendTo = new String(request.getParameter("sendTo"));
      }
      else {
        throw new Exception("Request getParameter [sendTo] not found!");
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute("sendRecordCnt", sendMessage(request, sendTo));
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());
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
