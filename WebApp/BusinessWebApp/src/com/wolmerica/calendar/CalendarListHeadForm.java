/*
 * CalendarListHeadForm.java
 *
 * Created on January 15, 2007, 09:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.calendar;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.util.ArrayList;

public class CalendarListHeadForm extends MasterForm
{
  private String currentDayOfYear;
  private String currentMonth;
  private String currentYear;
  private String currentFromDate;
  private String currentToDate;
  private String dayOfWeek;
  private String previousFromDate;
  private String previousToDate;
  private String nextFromDate;
  private String nextToDate;
  private String recordCount;
  private ArrayList calendarForm;

  public void setCurrentDayOfYear(String currentDayOfYear) {
    this.currentDayOfYear = currentDayOfYear;
  }

  public String getCurrentDayOfYear() {
    return currentDayOfYear;
  }

  public void setCurrentMonth(String currentMonth) {
    this.currentMonth = currentMonth;
  }

  public String getCurrentMonth() {
    return currentMonth;
  }

  public void setCurrentYear(String currentYear) {
    this.currentYear = currentYear;
  }

  public String getCurrentYear() {
    return currentYear;
  }

  public void setCurrentFromDate(String currentFromDate) {
    this.currentFromDate = currentFromDate;
  }

  public String getCurrentFromDate() {
    return currentFromDate;
  }

  public void setCurrentToDate(String currentToDate) {
    this.currentToDate = currentToDate;
  }

  public String getCurrentToDate() {
    return currentToDate;
  }

  public void setDayOfWeek(String dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public String getDayOfWeek() {
    return dayOfWeek;
  }

  public void setPreviousFromDate(String previousFromDate) {
    this.previousFromDate = previousFromDate;
  }

  public String getPreviousFromDate() {
    return previousFromDate;
  }

  public void setPreviousToDate(String previousToDate) {
    this.previousToDate = previousToDate;
  }

  public String getPreviousToDate() {
    return previousToDate;
  }

  public void setNextFromDate(String nextFromDate) {
    this.nextFromDate = nextFromDate;
  }

  public String getNextFromDate() {
    return nextFromDate;
  }

  public void setNextToDate(String nextToDate) {
    this.nextToDate = nextToDate;
  }

  public String getNextToDate() {
    return nextToDate;
  }

  public void setRecordCount(String recordCount) {
    this.recordCount = recordCount;
  }

  public String getRecordCount() {
    return recordCount;
  }

  public void setCalendarForm(ArrayList calendarList){
  this.calendarForm=calendarList;
  }

  public ArrayList getCalendarForm(){
  return calendarForm;
  }

  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();

    EmployeesActionMapping employeesMapping = (EmployeesActionMapping)mapping;

    // Does this action require the user to login
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {

        // return null to force action to handle login
        // error
        return null;
      }
    }

    errors = super.validate(mapping, request);

    // Post a global message instructing user to clean up
    // validation errors and resubmit
    if (errors.size() > 0) {
      ActionMessage message =
          new ActionMessage("message.validation");
      ActionMessages messages = new ActionMessages();
          messages.add(ActionMessages.GLOBAL_MESSAGE, message);
      request.setAttribute(Globals.MESSAGE_KEY, messages);
    }

    // Set the disableEdit attribute to false when errors encountered.
    request.setAttribute("disableEdit", false);

    return errors;
  }
}