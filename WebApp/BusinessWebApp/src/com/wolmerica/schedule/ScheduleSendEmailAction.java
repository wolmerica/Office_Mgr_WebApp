/*
 * ScheduleSendEmailAction.java
 *
 * Created on February 18, 2008, 07:12 PM
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
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.email.EmailService;
import com.wolmerica.service.email.DefaultEmailService;
import com.wolmerica.service.property.PropertyService; 
import com.wolmerica.service.property.DefaultPropertyService; 
import com.wolmerica.service.schedule.ScheduleService;
import com.wolmerica.service.schedule.DefaultScheduleService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.EmailFormatter;

import java.io.IOException;
import java.security.Security;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


import org.apache.log4j.Logger;

public class ScheduleSendEmailAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private CustomerService CustomerService = new DefaultCustomerService();
  private EmailService EmailService = new DefaultEmailService();
  private PropertyService propertyService = new DefaultPropertyService();
  private ScheduleService scheduleService = new DefaultScheduleService();
  private UserStateService userStateService = new DefaultUserStateService();

  public CustomerService getCustomerService() {
      return CustomerService;
  }

  public void setCustomerService(CustomerService CustomerService) {
      this.CustomerService = CustomerService;
  }

  public EmailService getEmailService() {
      return EmailService;
  }

  public void setEmailService(EmailService EmailService) {
      this.EmailService = EmailService;
  }

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

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

  private HashMap sendListViaEmail(HttpServletRequest request,
                                   String sendTo)
   throws Exception {

    ScheduleListHeadDO formHDO = (ScheduleListHeadDO) request.getSession().getAttribute("scheduleListHDO");

    HashMap<String,Object> sendToMap = new HashMap<String,Object>();
    String errMsg = "";
    Integer sendRecordCnt = 0;

    try {
//--------------------------------------------------------------------------------
// Traverse the ScheduleListHead object.
//--------------------------------------------------------------------------------
      sendRecordCnt = formHDO.getRecordCount();
      cat.debug(this.getClass().getName() + ": sendRecordCnt: " + sendRecordCnt);
      ArrayList<ScheduleDO> eventList = (ArrayList<ScheduleDO>) formHDO.getScheduleForm();
      ScheduleDO formDO = null;

      String emailSubject = getPropertyService().getCustomerProperties(request,"schedule.send.reminder");
      String sendNewLine = getPropertyService().getCustomerProperties(request,"schedule.send.newline");
//--------------------------------------------------------------------------------
// Get the email header and footer.
//--------------------------------------------------------------------------------
      String emailMsg = doEmailHeader(request, "schedule.send.header");
//--------------------------------------------------------------------------------
// Only traverse the array if the record count is greater than zero.
//--------------------------------------------------------------------------------
      if (sendRecordCnt > 0) {
        for (int j=0; j < eventList.size(); j++) {
          formDO = (ScheduleDO) eventList.get(j);

          emailMsg = emailMsg
                   + doEmailFormat(request, formDO)
                   + sendNewLine;
        }
        emailMsg = emailMsg + doEmailHeader(request, "schedule.send.footer");        
        cat.debug(this.getClass().getName() + ": email Message: " + emailMsg);
        cat.debug(this.getClass().getName() + ": email Send To: " + sendTo);
//--------------------------------------------------------------------------------
// Instantiate the JavaMailKit and send the schedule details.
//--------------------------------------------------------------------------------
        //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());        
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        String[] recipients = { sendTo };
        String emailSmtp = getPropertyService().getCustomerProperties(request,"schedule.send.smtp.host");
        String emailPort = getPropertyService().getCustomerProperties(request,"schedule.send.smtp.port");
        String emailFrom = getPropertyService().getCustomerProperties(request,"schedule.send.from");
        String emailUser = getPropertyService().getCustomerProperties(request,"schedule.send.smtp.user");
        String emailPassword = getPropertyService().getCustomerProperties(request,"schedule.send.smtp.password");

        sendToMap = getEmailService().postMail(sendToMap, recipients, emailSubject, emailMsg,
                                 emailFrom, emailSmtp, emailPort, emailUser, emailPassword);
      }
      else {        
        throw new Exception(getPropertyService().getCustomerProperties(request,"schedule.send.noevents"));
      }

    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": throw Exception: " + e.getMessage());        
      throw new Exception(e.getMessage());
    }
    return sendToMap;
  }


  private boolean verifyEmailList(HttpServletRequest request)
   throws Exception {

    boolean result = true;
    String errMsg = "";
    String sendTo = "";
    Integer validRecordCnt = 0;
    EmailFormatter efmt = new EmailFormatter();

    DataSource ds = null;
    Connection conn = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      ScheduleListHeadDO formHDO = (ScheduleListHeadDO) request.getSession().getAttribute("scheduleListHDO");
      String sendNewLine = getPropertyService().getCustomerProperties(request,"schedule.send.newline");

//--------------------------------------------------------------------------------
// Traverse the ScheduleListHead object.
//--------------------------------------------------------------------------------
      ArrayList eventList = formHDO.getScheduleForm();
      ScheduleDO formDO = null;

      for (int j=0; j < eventList.size(); j++) {
        formDO = (ScheduleDO) eventList.get(j);
//--------------------------------------------------------------------------------
// Verify email only if the customer has asked to be notified via email.
//--------------------------------------------------------------------------------
        if ((formDO.getStatusKey() == 0) &&
           ((formDO.getReminderPrefKey() == 2) ||
           (formDO.getReminderPrefKey() == 3))) {
//--------------------------------------------------------------------------------
// Retrieve the customer email address in regard to the event.
//--------------------------------------------------------------------------------
          sendTo = getCustomerService().getCustomerEmail(conn,
                                       formDO.getReminderPrefKey(),
                                       formDO.getCustomerKey());
          cat.debug(this.getClass().getName() + ": sendTo.....: " + sendTo);
          if (!(efmt.isValidEmailAddress(sendTo)))
            errMsg = errMsg + getPropertyService().getCustomerProperties(request,"schedule.send.invalid") + " " + formDO.getClientName() + sendNewLine;
        }
        if (errMsg.length() > 1)
          throw new Exception(errMsg);
      }
    }
    catch (Exception e) {
       cat.error(this.getClass().getName() + ": throw Exception: " + e.getMessage());        
       throw new Exception(e.getMessage());
    }
    return result;
  }


  private HashMap<String, Object> sendReminderViaEmail(HttpServletRequest request)
   throws Exception {

    HashMap<String, Object> sendToMap = new HashMap<String, Object>();
    Integer sendRecordCnt = 0;

    DataSource ds = null;
    Connection conn = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      ScheduleListHeadDO formHDO = (ScheduleListHeadDO) request.getSession().getAttribute("scheduleListHDO");
//--------------------------------------------------------------------------------
// Traverse the ScheduleListHead object.
//--------------------------------------------------------------------------------
      sendRecordCnt = formHDO.getRecordCount();
      cat.debug(this.getClass().getName() + ": sendRecordCnt: " + sendRecordCnt);
      ArrayList eventList = formHDO.getScheduleForm();
      ScheduleDO formDO = null;
//--------------------------------------------------------------------------------
// Get the multi-line header as defined in the customer property file.
//--------------------------------------------------------------------------------
      String emailSubject = getPropertyService().getCustomerProperties(request,"schedule.send.reminder");
//--------------------------------------------------------------------------------
// Get the email header and footer.
//--------------------------------------------------------------------------------
      String emailHeader = doEmailHeader(request, "schedule.send.header");
      String emailFooter = doEmailHeader(request, "schedule.send.footer");
//--------------------------------------------------------------------------------
// Instantiate the JavaMailKit object and set common parameters.
//--------------------------------------------------------------------------------
      //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());    
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      String emailSmtp = getPropertyService().getCustomerProperties(request,"schedule.send.smtp.host");
      String emailPort = getPropertyService().getCustomerProperties(request,"schedule.send.smtp.port");
      String emailFrom = getPropertyService().getCustomerProperties(request,"schedule.send.from");
      String emailUser = getPropertyService().getCustomerProperties(request,"schedule.send.smtp.user");
      String emailPassword = getPropertyService().getCustomerProperties(request,"schedule.send.smtp.password");
      
      String sendTo = emailFrom;
      String emailMsg = "";
      String[] recipients = { sendTo, emailFrom };
//--------------------------------------------------------------------------------
// Only traverse the array if the record count is greater than zero.
//--------------------------------------------------------------------------------
      if (sendRecordCnt > 0) {
        for (int j=0; j < eventList.size(); j++) {
          formDO = (ScheduleDO) eventList.get(j);
//--------------------------------------------------------------------------------
// Send email or text only if the customer has asked for email or text.
//--------------------------------------------------------------------------------
          cat.debug("getStatusKey().....: " + formDO.getStatusKey());
          cat.debug("getReminderPrefKey.: " + formDO.getReminderPrefKey());
          if ((formDO.getStatusKey() == 0) &&
             ((formDO.getReminderPrefKey() == 2) ||
             (formDO.getReminderPrefKey() == 3))) {
//--------------------------------------------------------------------------------
// Construct an email message with a header and footer.
//--------------------------------------------------------------------------------
            if (formDO.getReminderPrefKey() == 2)
              emailMsg = emailHeader
                       + doEmailFormat(request, formDO)
                       + emailFooter;
//--------------------------------------------------------------------------------
// Construct a brief text message.
//--------------------------------------------------------------------------------
            if (formDO.getReminderPrefKey() == 3)
              emailMsg = doTextFormat(request, formDO);
//--------------------------------------------------------------------------------
// Retrieve the customer email address in regard to the event.
//--------------------------------------------------------------------------------
            sendTo = getCustomerService().getCustomerEmail(conn,
                                         formDO.getReminderPrefKey(),
                                         formDO.getCustomerKey());
//--------------------------------------------------------------------------------
// Set the recipients value and post the email.
//--------------------------------------------------------------------------------
            cat.debug("sendTo...: " + sendTo);

            recipients[0] = sendTo;
            sendToMap = getEmailService().postMail(sendToMap, recipients, emailSubject, emailMsg,
                                     emailFrom, emailSmtp, emailPort, emailUser, emailPassword);
          
//--------------------------------------------------------------------------------
// Update the schedule status value to "pending" after posting mail.
//--------------------------------------------------------------------------------
            getScheduleService().setScheduleStatus(conn, formDO.getKey());
          }
        }
      
        Set sendToKeys = sendToMap.keySet();
        Iterator itr = sendToKeys.iterator();
        while(itr.hasNext()) {
          String sendToNow = (String) (itr.next());
          cat.debug("sendTo.: " + sendToNow + " -- " + sendToMap.get(sendToNow));
        }
      }
      if (sendToMap.isEmpty())
        throw new Exception(getPropertyService().getCustomerProperties(request,"schedule.send.noevents"));

    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
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
    return sendToMap;
  }


  private String doEmailHeader(HttpServletRequest request,
                               String messagePrefix)
   throws Exception {

    String emailHdr = "";

    try {
//--------------------------------------------------------------------------------
// Get the multi-line header as defined in the customer property file.
//--------------------------------------------------------------------------------
      String sendNewLine = getPropertyService().getCustomerProperties(request,"schedule.send.newline");
      Integer messageCount = new Integer("0");
      String messageKey = messagePrefix + ".count";
      Integer messageMax = new Integer(getPropertyService().getCustomerProperties(request,messageKey));
      while (++messageCount <= messageMax)
      {
        messageKey = messagePrefix + ".line" + messageCount;
        emailHdr = emailHdr + getPropertyService().getCustomerProperties(request,messageKey) + sendNewLine;
      }
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": throw Exception: " + e.getMessage());
      throw new Exception(e.getMessage());
    }
    return emailHdr;
  }


  private String doEmailFormat(HttpServletRequest request,
                               ScheduleDO formDO)
   throws Exception {

    String emailMsg = "";

    try {
//--------------------------------------------------------------------------------
// Instantiate a property reader.
//--------------------------------------------------------------------------------
      String sendNewLine = getPropertyService().getCustomerProperties(request,"schedule.send.newline");
      String eventTypeCode = "schedule.send.eventtype.";
      String locationCode = "schedule.send.location.";
      String statusCode = "schedule.send.status.";
//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      String startDate = dateFormatter.format(formDO.getStartDate());
      int startHour = formDO.getStartHour().intValue();
      String startMinute = "00";
      String timeOfDay = "AM";
//--------------------------------------------------------------------------------
// Collect details associated with the event such ast the start time.
//--------------------------------------------------------------------------------
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
// Construct the message to be delivered via email.
//--------------------------------------------------------------------------------
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,statusCode + formDO.getStatusKey().toString()) + " ";
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,eventTypeCode + formDO.getEventTypeKey().toString()) + sendNewLine;
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.location") + " ";
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,locationCode + formDO.getLocationId()) + sendNewLine;
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.datetime") + " " + startDate + " " + startHour + ":" + startMinute + timeOfDay + sendNewLine;
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.subject") + " " + formDO.getSubject() + sendNewLine;
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.client") + " " + formDO.getClientName() + sendNewLine;
      if (formDO.getAttributeToName().length() > 1)
        emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.regarding") + " " + formDO.getAttributeToName() + sendNewLine;
      if (formDO.getAttributeToDate() != null) {
        startDate = dateFormatter.format(formDO.getAttributeToDate());
        emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.dob") + " " + startDate + sendNewLine;
      }
      if (formDO.getAddress().length() > 0)
        emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.address") + " " + formDO.getAddress() + sendNewLine;
      if (formDO.getCity().length() > 0)
        emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.city") + " " + formDO.getCity() + ", " + formDO.getState() + sendNewLine;
      if (formDO.getNoteLine1().length() > 1)
        emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.note") + " " + formDO.getNoteLine1() + sendNewLine;
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": throw Exception: " + e.getMessage());
      throw new Exception(e.getMessage());
    }
    return emailMsg;
  }


  private String doTextFormat(HttpServletRequest request,
                              ScheduleDO formDO)
   throws Exception {

    String emailMsg = "";

    try {
//--------------------------------------------------------------------------------
// Instantiate a property reader.
//--------------------------------------------------------------------------------
      String sendNewLine = getPropertyService().getCustomerProperties(request,"schedule.send.newline");
      String eventTypeCode = "schedule.send.eventtype.";
      String locationCode = "schedule.send.location.";
      String statusCode = "schedule.send.status.";
//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      String startDate = dateFormatter.format(formDO.getStartDate());
      int startHour = formDO.getStartHour().intValue();
      String startMinute = "00";
      String timeOfDay = "AM";
//--------------------------------------------------------------------------------
// Collect details associated with the event such ast the start time.
//--------------------------------------------------------------------------------
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
// Construct the message to be delivered via emamil.
//--------------------------------------------------------------------------------
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,eventTypeCode + formDO.getEventTypeKey().toString()) + sendNewLine;
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.datetime") + " " + startDate + " " + startHour + ":" + startMinute + timeOfDay + sendNewLine;
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.subject") + " " + formDO.getSubject() + sendNewLine;
      emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.client") + " " + formDO.getClientName() + sendNewLine;
      if (formDO.getAttributeToName().length() > 1)
        emailMsg = emailMsg + getPropertyService().getCustomerProperties(request,"schedule.send.regarding") + " " + formDO.getAttributeToName() + sendNewLine;
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": throw Exception: " + e.getMessage());
      throw new Exception(e.getMessage());
    }
    return emailMsg;
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
        sendTo = request.getParameter("sendTo");
      }
      else
        throw new Exception("Request getParameter [sendTo] not found!");
      cat.debug(this.getClass().getName() + ": sendTo...: " + sendTo);

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked())) {
//--------------------------------------------------------------------------------
// Send a reminder to:
// 1) particular employee email address
// 2a) verify the customer email address values (do not send unless valid)
// 2b) send each event to the respective customer email address.
//--------------------------------------------------------------------------------
        cat.debug(this.getClass().getName() + ": string compare..: " + sendTo);
        if (sendTo.compareToIgnoreCase("To Customer(s)") == 0) {
          if (verifyEmailList(request))
            request.setAttribute("sendToMap", sendReminderViaEmail(request));
        }
        else {
          request.setAttribute("sendToMap", sendListViaEmail(request, sendTo));
        }
      }
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