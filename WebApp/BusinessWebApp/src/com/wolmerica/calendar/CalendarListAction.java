/*
 * CalendarListAction.java
 *
 * Created on January 15, 2007, 09:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 01/15/2007 - Define getCalendarMonth() methods to support a monthly view of the schedule.
 * 01/29/2007 - Add getCalendarWeek() and getCalendarDay() methods for more display options.
 */

package com.wolmerica.calendar;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class CalendarListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private UserStateService userStateService = new DefaultUserStateService();

  public void setDateRangeService(DateRangeService dateRangeService) {
      this.dateRangeService = dateRangeService;
  }

  public DateRangeService getDateRangeService() {
      return dateRangeService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private CalendarListHeadDO getCalendarMonth(HttpServletRequest request,
                                              String fromDate)
    throws Exception, SQLException {

    CalendarListHeadDO formHDO = new CalendarListHeadDO();
    CalendarDO calendarRow = null;
    ArrayList<CalendarDO> calendarRows = new ArrayList<CalendarDO>();

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
    DateFormatter dateFormatter = new DateFormatter();
    Date myDate = (Date) dateFormatter.unformat(fromDate);

   // Instantiating a Calendar object.
    Calendar fDOM = Calendar.getInstance();
    formHDO.setCurrentDayOfYear((fDOM.get(Calendar.YEAR) * 1000) + fDOM.get(Calendar.DAY_OF_YEAR));
    Calendar fDOW = Calendar.getInstance();
    cat.debug(this.getClass().getName() + " fromDate value = " + myDate.toString());
//========================================================================
// Construct Calendar value using the fromDate month and year.
//========================================================================
    fDOM.setTime(myDate);
    fDOM.set(fDOM.get(Calendar.YEAR),
             fDOM.get(Calendar.MONTH),
             1);
    cat.debug(this.getClass().getName() + " Calendar value = " + fDOM.getTime());
//========================================================================
// Go back to Sunday, the first day of the calendar week.
//========================================================================
    fDOW.setTime(fDOM.getTime());
    while (fDOW.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
      fDOW.add(Calendar.DATE, -1);

//========================================================================
// Populate the CalendarDO object for one month a week at a time.
//========================================================================
    Boolean addWeekId = true;
    Byte dayCnt = 0;
    Byte dayNum = 0;
    Byte weekNum = 0;
    Integer dayTemp = 0;
    while (addWeekId) {
      weekNum++;
      cat.debug(this.getClass().getName() + " Week # = " + weekNum + " " + fDOW.getTime());
//========================================================================
// Assign the values for the week starting from Sunday to Saturday.
//========================================================================
      for (dayNum=1; dayNum<=7; dayNum++) {
        dayCnt++;
        cat.debug(this.getClass().getName() + " fDOW = " + fDOW.getTime());

        calendarRow = new CalendarDO();
        calendarRow.setCurrentMonthId((fDOW.get(Calendar.MONTH)) == (fDOM.get(Calendar.MONTH)));
        dayTemp = fDOW.get(Calendar.DAY_OF_WEEK);
        calendarRow.setDayOfWeek(dayTemp.byteValue());
        dayTemp = fDOW.get(Calendar.DAY_OF_MONTH);
        calendarRow.setDayOfMonth(dayTemp.byteValue());
        calendarRow.setTheDate(fDOW.getTime());
        calendarRow.setKey((fDOW.get(Calendar.YEAR) * 1000) + fDOW.get(Calendar.DAY_OF_YEAR));
        calendarRow.setWeekNumber(weekNum.byteValue());
        calendarRows.add(calendarRow);

        fDOW.add(Calendar.DATE, 1);
      }
      if ((fDOW.get(Calendar.MONTH)) != (fDOM.get(Calendar.MONTH)))
        addWeekId = false;
    }

    if (calendarRows.isEmpty())
      calendarRows.add(new CalendarDO());
    formHDO.setCalendarForm(calendarRows);
    formHDO.setCurrentMonth(fDOM.get(Calendar.MONTH) + 1);
    formHDO.setCurrentYear(fDOM.get(Calendar.YEAR));
//========================================================================
// Set the current from/to date values.
//========================================================================
    formHDO.setCurrentFromDate(fDOM.getTime());
    fDOM.add(Calendar.MONTH, 1);
    fDOM.add(Calendar.DATE, -1);
    formHDO.setCurrentToDate(fDOM.getTime());
//========================================================================
// Set the previus from/to date values.
//========================================================================
    fDOM.setTime(formHDO.getCurrentFromDate());
    fDOM.add(Calendar.MONTH, -1);
    formHDO.setPreviousFromDate(fDOM.getTime());
    fDOM.add(Calendar.MONTH, 1);
    fDOM.add(Calendar.DATE, -1);
    formHDO.setPreviousToDate(fDOM.getTime());
//========================================================================
// Set the next from/to date values.
//========================================================================
    fDOM.setTime(formHDO.getCurrentFromDate());
    fDOM.add(Calendar.MONTH, 1);
    formHDO.setNextFromDate(fDOM.getTime());
    fDOM.add(Calendar.MONTH, 1);
    fDOM.add(Calendar.DATE, -1);
    formHDO.setNextToDate(fDOM.getTime());

    return formHDO;
  }


  private CalendarListHeadDO getCalendarWeek(HttpServletRequest request,
                                             String fromDate)
    throws Exception, SQLException {

    CalendarListHeadDO formHDO = new CalendarListHeadDO();
    CalendarDO calendarRow = null;
    ArrayList<CalendarDO> calendarRows = new ArrayList<CalendarDO>();

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
    DateFormatter dateFormatter = new DateFormatter();
    Date myDate = (Date) dateFormatter.unformat(fromDate);
    
//========================================================================
// Instantiating a Calendar object.
//========================================================================    
    Calendar fDOM = Calendar.getInstance();
    formHDO.setCurrentDayOfYear((fDOM.get(Calendar.YEAR) * 1000) + fDOM.get(Calendar.DAY_OF_YEAR));
    Calendar fDOW = Calendar.getInstance();
    cat.debug(this.getClass().getName() + " fromDate value = " + myDate.toString());
//========================================================================
// Construct Calendar value using the fromDate year, month, and date.
//========================================================================
    fDOM.setTime(myDate);
    cat.debug(this.getClass().getName() + " Calendar value = " + fDOM.getTime());
//========================================================================
// Go back to Sunday, the first day of the calendar week.
//========================================================================
    while (fDOM.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
      fDOM.add(Calendar.DATE, -1);
    fDOW.setTime(fDOM.getTime());    

//========================================================================
// Populate the CalendarDO object for one week.
//========================================================================
    Byte dayCnt = 0;
    Byte dayNum = 0;
    Byte weekNum = 1;
    Integer dayTemp = 0;
//========================================================================
// Assign the values for the week starting from Sunday to Saturday.
//========================================================================
    for (dayNum=1; dayNum<=7; dayNum++) {
      dayCnt++;
      cat.debug(this.getClass().getName() + " dayCnt = " + dayCnt);
      cat.debug(this.getClass().getName() + " fDOM = " + fDOM.get(Calendar.MONTH));
      cat.debug(this.getClass().getName() + " fDOW = " + fDOW.get(Calendar.MONTH));

      calendarRow = new CalendarDO();
      calendarRow.setCurrentMonthId(true);
      dayTemp = fDOW.get(Calendar.DAY_OF_WEEK);
      calendarRow.setDayOfWeek(dayTemp.byteValue());
      dayTemp = fDOW.get(Calendar.DAY_OF_MONTH);
      calendarRow.setDayOfMonth(dayTemp.byteValue());
      calendarRow.setTheDate(fDOW.getTime());
      calendarRow.setKey((fDOW.get(Calendar.YEAR) * 1000) + fDOW.get(Calendar.DAY_OF_YEAR));
      calendarRow.setWeekNumber(weekNum.byteValue());
      calendarRows.add(calendarRow);

      fDOW.add(Calendar.DATE, 1);
    }

    if (calendarRows.isEmpty())
      calendarRows.add(new CalendarDO());
    formHDO.setCalendarForm(calendarRows);
    formHDO.setCurrentMonth(fDOM.get(Calendar.MONTH) + 1);
    formHDO.setCurrentYear(fDOM.get(Calendar.YEAR));
//========================================================================
// Set the current from/to date values to span one week.
//========================================================================
    formHDO.setCurrentFromDate(fDOM.getTime());
    fDOM.add(Calendar.DATE, 6);
    formHDO.setCurrentToDate(fDOM.getTime());
//========================================================================
// Set the previus from/to date values to span one week.
//========================================================================
    fDOM.setTime(formHDO.getCurrentFromDate());
    fDOM.add(Calendar.DATE, -1);
    formHDO.setPreviousToDate(fDOM.getTime());
    fDOM.add(Calendar.DATE, -6);
    formHDO.setPreviousFromDate(fDOM.getTime());
//========================================================================
// Set the next from/to date values to span one week.
//========================================================================
    fDOM.setTime(formHDO.getCurrentToDate());
    fDOM.add(Calendar.DATE, 1);
    formHDO.setNextFromDate(fDOM.getTime());
    fDOM.add(Calendar.DATE, 6);
    formHDO.setNextToDate(fDOM.getTime());

    return formHDO;
  }


  private CalendarListHeadDO getCalendarDay(HttpServletRequest request,
                                            String fromDate)
    throws Exception, SQLException {

    CalendarListHeadDO formHDO = new CalendarListHeadDO();
    CalendarDO calendarRow = null;
    ArrayList<CalendarDO> calendarRows = new ArrayList<CalendarDO>();

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
    DateFormatter dateFormatter = new DateFormatter();
    Date myDate = (Date) dateFormatter.unformat(fromDate);

   // Instantiating a Calendar object.
    Calendar fDOM = Calendar.getInstance();
    formHDO.setCurrentDayOfYear((fDOM.get(Calendar.YEAR) * 1000) + fDOM.get(Calendar.DAY_OF_YEAR));
    cat.debug(this.getClass().getName() + " fromDate value = " + myDate.toString());
//========================================================================
// Construct Calendar value using the fromDate year, month, and date.
//========================================================================
    fDOM.setTime(myDate);
    fDOM.set(fDOM.get(Calendar.YEAR),
             fDOM.get(Calendar.MONTH),
             fDOM.get(Calendar.DATE));
    cat.debug(this.getClass().getName() + " Calendar value = " + fDOM.getTime());

//========================================================================
// Populate the CalendarDO object for one day.
//========================================================================
    Byte dayCnt = 0;
    Byte dayNum = 0;
    Byte weekNum = 1;
    Integer dayTemp = 0;
//========================================================================
// Assign the values for a single day.
//========================================================================
    calendarRow = new CalendarDO();
    calendarRow.setCurrentMonthId((fDOM.get(Calendar.MONTH)) == (fDOM.get(Calendar.MONTH)));
    dayTemp = fDOM.get(Calendar.DAY_OF_WEEK);
    calendarRow.setDayOfWeek(dayTemp.byteValue());
    dayTemp = fDOM.get(Calendar.DAY_OF_MONTH);
    calendarRow.setDayOfMonth(dayTemp.byteValue());
    calendarRow.setTheDate(fDOM.getTime());
    calendarRow.setKey((fDOM.get(Calendar.YEAR) * 1000) + fDOM.get(Calendar.DAY_OF_YEAR));
    calendarRow.setWeekNumber(weekNum.byteValue());
    calendarRows.add(calendarRow);

    if (calendarRows.isEmpty())
      calendarRows.add(new CalendarDO());
    formHDO.setCalendarForm(calendarRows);
    formHDO.setCurrentMonth(fDOM.get(Calendar.MONTH) + 1);
    formHDO.setCurrentYear(fDOM.get(Calendar.YEAR));
//========================================================================
// Set the current from/to date values to span one day.
//========================================================================
    formHDO.setCurrentFromDate(fDOM.getTime());
    formHDO.setCurrentToDate(formHDO.getCurrentFromDate());
    dayTemp = fDOM.get(Calendar.DAY_OF_WEEK);
    formHDO.setDayOfWeek(dayTemp.byteValue());
//========================================================================
// Set the previus from/to date values to span one day.
//========================================================================
    fDOM.setTime(formHDO.getCurrentFromDate());
    fDOM.add(Calendar.DATE, -1);
    formHDO.setPreviousFromDate(fDOM.getTime());
    formHDO.setPreviousToDate(formHDO.getPreviousFromDate());
//========================================================================
// Set the next from/to date values to span one week.
//========================================================================
    fDOM.setTime(formHDO.getCurrentToDate());
    fDOM.add(Calendar.DATE, 1);
    formHDO.setNextFromDate(fDOM.getTime());
    formHDO.setNextToDate(formHDO.getNextFromDate());

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
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getFWDFromDate());

      if (!(request.getParameter("fromDate") == null)) {
        if (request.getParameter("fromDate").length() > 0 ) {
          fromDate = request.getParameter("fromDate");
        }
      }

//--------------------------------------------------------------------------------
// calendarMode is defined as an Byte to use a "switch case" statement to decide
// which getCalendar method was requested.  The default mode will be month.
//--------------------------------------------------------------------------------
      Byte calendarMode = 3;
      if (!(request.getParameter("mode") == null)) {
        calendarMode = new Byte(request.getParameter("mode"));
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
// Instantiate a new calendar formDO object for a time periond of month, week, or day.
//--------------------------------------------------------------------------------
        CalendarListHeadDO formHDO = null;

        switch(calendarMode)
        {
          /* By Day */
          case 1:
            formHDO = getCalendarDay(request, fromDate);
          break;

          /* By Week */
          case 2:
            formHDO = getCalendarWeek(request, fromDate);
          break;

          /* By Month */
          case 3:
            formHDO = getCalendarMonth(request, fromDate);
          break;
        }

        request.getSession().setAttribute("calendarListHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for schedule list.
//--------------------------------------------------------------------------------
        CalendarListHeadForm formHStr = new CalendarListHeadForm();
        formHStr.populate(formHDO);
        form = formHStr;

        cat.debug(this.getClass().getName() + " fromDate = " + formHStr.getCurrentFromDate());

        request.setAttribute("fromDate", formHStr.getCurrentFromDate());
        request.setAttribute("toDate", formHStr.getCurrentToDate());
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
