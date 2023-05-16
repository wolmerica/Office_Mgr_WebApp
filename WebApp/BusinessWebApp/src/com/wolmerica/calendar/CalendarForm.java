/*
 * CalendarForm.java
 *
 * Created on January 15, 2007, 09:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
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
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.Date;

public class CalendarForm extends MasterForm {

  private String key;
  private String theDate;
  private String weekNumber;
  private String dayOfWeek;  
  private String dayOfMonth;
  private String currentMonthId;

  public CalendarForm() {
    addRequiredFields(new String[] { "key", "theDate", "weekNumber", "dayOfWeek" });
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setTheDate(String theDate) {
    this.theDate = theDate;
  }

  public String getTheDate() {
    return theDate;
  }

  public void setWeekNumber(String weekNumber) {
    this.weekNumber = weekNumber;
  }

  public String getWeekNumber() {
    return weekNumber;
  }

  public void setDayOfWeek(String dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public String getDayOfWeek() {
    return dayOfWeek;
  }
  
  public void setDayOfMonth(String dayOfMonth) {
    this.dayOfMonth = dayOfMonth;
  }

  public String getDayOfMonth() {
    return dayOfMonth;
  }    
  
  public void setCurrentMonthId(String currentMonthId) {
    this.currentMonthId = currentMonthId;
  }

  public String getCurrentMonthId() {
    return currentMonthId;
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