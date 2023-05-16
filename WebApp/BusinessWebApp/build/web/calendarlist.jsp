<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<% 
  String dOW = "sun";
  String mTH = "prev";
  int startHour = 0;
  int startMinute = 0;
  String timeOfDay = "am";
  String eventType = "";
  int i = 0;
  Integer permOffset = new Integer("0");
  String permAddId = "false";
  String permViewId = "false";
  String permEditId = "false";
  String permDeleteId = "false";
  String permLockAvailableId = "false";
  String permLockedBy = "";
  String calendarMode = "3";
  String calendarParam = "";
  if (request.getParameter("mode") != null) {  
    calendarMode = request.getParameter("mode").toString();
    calendarParam = "&mode=" + calendarMode;
   }   
  boolean attAppointment = false;  
  boolean attShowStartTime = false;
  boolean attShowClientName = false;
  boolean attShowInfoIcon = false;
  String attCalDateValue = "";
  String attStartDateValue = "";
  String attEndDateValue = "";
%>

<style type="text/css">

table#calendar {background: white url(fwork.gif) center no-repeat;}
table#calendar a {text-decoration: none;}
<% if (calendarMode.equalsIgnoreCase("1")) { %>
tr#days th {width: 70%;}
tr#days th.sun {width: 70%;}
tr#days th.leftEdge {width: 15%;}
tr#days th.rightEdge {width: 15%;}
<% } else { %>
tr#days th {width: 15%;}
tr#days th.sun {width: 10%;}
<% } %>    
table#calendar tr#days th {color: #CCE; background-color: #224;
   font-weight: bold; text-align: center;
   padding: 1px 0.33em;}
table#calendar tr#title th {background: #AAC; color: black;
   border: 1px solid #242; font-size: 120%;}
table#calendar td {vertical-align: top; padding: 0;
   border: 0px solid gray; border-width: 0 0 1px 1px;}
table#calendar a {font-weight: bold; display: block; margin: 0;}
table#calendar a:link {color: navy;}
table#calendar a:visited {color: purple;}
table#calendar a:hover {background: #FF6;}
table#calendar td.sun {background: #FFCC99;}
table#calendar td.prev, table#calendar td.next {
   background: #AAB; color: #889;}
table#calendar tr#lastweek td {border-bottom: 2px solid #AAB;}
table#calendar td.holiday {background: #FAA;}
table#calendar td#curr<bean:write name="calendarListHeadForm" property="currentDayOfYear" /> {background-color: yellow;}
td#curr<bean:write name="calendarListHeadForm" property="currentDayOfYear" /> div.date {color: #C33; font-weight: bold; background: #FFC;}
div.event {margin: 0.5em;}
div.event span {display: block;} 
div.holiday {font-style: italic;}
span { font-size: 8Pt }
span.time {font-weight: bold;}
span.subject {color: black; }
span.attribute {color: plum; font-weight: bold; font-style: italic;}
span.resource {color: brown; }
span.location {color: teal; font-style: italic;}
div.date { font-size: 10Pt; float: right; text-align: center;
   border: 1px solid gray; border-width: 0 0 1px 1px;
   padding: 0.125em 0.25em 0 0.25em; margin: 0; 
   background: #F3F3F3;}
td.sun div.date {border-width: 0;
   color: gray; background: transparent;}
td.prev div.date, td.next div.date {border-width: 0;
   color: gray; background: transparent;}
div.holiday {border: 1px solid #AA7; border-width: 0 0 1px; background-color: #CCCCFF; padding: 0.5em 0.5em; margin: 0;}
div.vacation {border: 1px solid #AA7; border-width: 0 0 1px; background-color: #FFE; padding: 0.5em 0.5em; margin: 0;}
div.birthday {border: 1px solid #AA7; border-width: 0 0 1px; background-color: #99CCFF; padding: 0.5em 0.5em; margin: 0;}
div.anniversary {border: 1px solid #AA7; border-width: 0 0 1px; background-color: #FFC0CB; padding: 0.5em 0.5em; margin: 0;}
div.reminder {border: 1px solid #AA7; border-width: 0 0 1px; background-color: #FFFFFF; padding: 0.5em 0.5em; margin: 0;}
</style>

<script language="javascript">

<!--
function popupform(myform, windowname)
{
if (! window.focus)return true;
window.open('', windowname, 'height=250,width=320,scrollbars=yes');
myform.target=windowname;
return true;
}
//-->

</script>


<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="scheduleListHeadForm"
               property="permissionListForm"
               scope="session"
               offset="<%=permOffset.toString()%>"
               length="1"
               type="com.wolmerica.permission.PermissionListForm">
  <% 
    permAddId = permissionListForm.getAddId();              
  %>
</logic:iterate> 

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="4">
      <button type="button" onClick="window.location='ScheduleList.do?fromDate=<bean:write name="calendarListHeadForm"
        property="currentFromDate" />&toDate=<bean:write name="calendarListHeadForm"
        property="currentToDate" />' "><bean:message key="app.schedule.backMessage" /></button>
      <% if (calendarMode.equalsIgnoreCase("1") || calendarMode.equalsIgnoreCase("2")) { %>
        &nbsp;&nbsp;
        <button type="button" onClick="window.location='EventCalendarList.do?fromDate=<bean:write name="calendarListHeadForm"
          property="currentFromDate" />&toDate=<bean:write name="calendarListHeadForm"
          property="currentToDate" />&mode=3' "><bean:message key="app.schedule.byMonth" /></button>
      <% } %>  
      <% if (calendarMode.equalsIgnoreCase("1")) { %>  
        &nbsp;&nbsp;      
        <button type="button" onClick="window.location='EventCalendarList.do?fromDate=<bean:write name="calendarListHeadForm"
          property="currentFromDate" />&toDate=<bean:write name="calendarListHeadForm"
          property="currentToDate" />&mode=2' "><bean:message key="app.schedule.byWeek" /></button>  
      <% } %> 
    </td>
    <% if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("true") == 0) { %>    
      <% if (calendarMode.equalsIgnoreCase("3")) { %>
        <td align="right" colspan="2">
          <logic:equal name="calendarListHeadForm" property="currentMonth" value="1">
            <button type="button" onClick="window.location='ScheduleDuplicateYear.do?fromDate=<bean:write name="calendarListHeadForm"
              property="previousFromDate" />' "><bean:message key="app.schedule.duplicateYear" /></button>  
          </logic:equal>
        </td>
      <% } else { %>
        <td colspan="2"></td>
      <% } %>
    <% } %>
    <logic:notEqual name="employeeListHeadForm" property="lastRecord" value="0"> 
    <!-- iterate over the employees -->       
      <td align="right" colspan="2">        
        <form name="SendEmailForm" action="ScheduleSendEmail.do" onsubmit="popupform(this, 'message')">
          <strong><bean:message key="app.calendar.sendTitle" />:</strong>&nbsp;
          <select name="sendTo">        
          <logic:iterate id="employeeListForm"
                         name="employeeListHeadForm"
                         property="employeeListForm"
                         scope="session"
                         type="com.wolmerica.employee.EmployeeListForm">
             <logic:notEqual name="employeeListForm" property="email" value="">                             
               <option value="<bean:write name="employeeListForm" property="email" />"><bean:write name="employeeListForm" property="firstName" /></option>
             </logic:notEqual>
          </logic:iterate>
               <option value="<bean:message key="app.calendar.send.toclient" />"><bean:message key="app.calendar.send.toclient" /></option>
          </select>
          <input type="submit" value="<bean:message key="app.calendar.sendCommand" />" onclick="return confirm('<bean:message key="app.calendar.send.message" />')" >
        </form>            
      </td>    
    </logic:notEqual>    
  </tr>
</table>
<br>  
<table cellspacing="0" width="100%" id="calendar">
  <tr id="title">
    <th id="lastmonth"><a href="EventCalendarList.do?fromDate=<bean:write name="calendarListHeadForm"
	           property="previousFromDate" />&toDate=<bean:write name="calendarListHeadForm"
	           property="previousToDate" /><%=calendarParam%>" >&laquo;</a></th>
    <% if (calendarMode.equalsIgnoreCase("1")) { %>
      <th id="thismonth">    
    <% } else { %>
      <th colspan="5" id="thismonth">
    <% } %>      
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="1">                   
        <bean:message key="app.calendar.month.1" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="2">
        <bean:message key="app.calendar.month.2" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="3">
        <bean:message key="app.calendar.month.3" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="4">
        <bean:message key="app.calendar.month.4" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="5">
        <bean:message key="app.calendar.month.5" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="6">
        <bean:message key="app.calendar.month.6" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="7">
        <bean:message key="app.calendar.month.7" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="8">
        <bean:message key="app.calendar.month.8" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="9">
        <bean:message key="app.calendar.month.9" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="10">
        <bean:message key="app.calendar.month.10" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="11">
        <bean:message key="app.calendar.month.11" />
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="currentMonth" value="12">
        <bean:message key="app.calendar.month.12" />
      </logic:equal>      
      &nbsp;    
      <bean:write name="calendarListHeadForm" property="currentYear" />    
    </th>
    <th id="nextmonth"><a href="EventCalendarList.do?fromDate=<bean:write name="calendarListHeadForm"
	           property="nextFromDate" />&toDate=<bean:write name="calendarListHeadForm"
	           property="nextToDate" /><%=calendarParam%>" >&raquo;</a></th>
  </tr>
  <tr id="days">
    <% if (calendarMode.equalsIgnoreCase("1")) { %>
      <th class="leftEdge"></th>    
      <logic:equal name="calendarListHeadForm" property="dayOfWeek" value="1">
        <th class="sun"><bean:message key="app.calendar.weekday.1" /></th>
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="dayOfWeek" value="2">
        <th class="mon"><bean:message key="app.calendar.weekday.2" /></th>
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="dayOfWeek" value="3">
        <th class="tue"><bean:message key="app.calendar.weekday.3" /></th>
      </logic:equal>      
      <logic:equal name="calendarListHeadForm" property="dayOfWeek" value="4">
        <th class="wed"><bean:message key="app.calendar.weekday.4" /></th>
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="dayOfWeek" value="5">
        <th class="thu"><bean:message key="app.calendar.weekday.5" /></th>
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="dayOfWeek" value="6">
        <th class="fri"><bean:message key="app.calendar.weekday.6" /></th>
      </logic:equal>
      <logic:equal name="calendarListHeadForm" property="dayOfWeek" value="7">
        <th class="sat"><bean:message key="app.calendar.weekday.7" /></th>
      </logic:equal>
      <th class="rightEdge"></th>          
    <% } else { %>
      <th class="sun"><bean:message key="app.calendar.weekday.1" /></th>
      <th class="mon"><bean:message key="app.calendar.weekday.2" /></th>
      <th class="tue"><bean:message key="app.calendar.weekday.3" /></th>
      <th class="wed"><bean:message key="app.calendar.weekday.4" /></th>
      <th class="thu"><bean:message key="app.calendar.weekday.5" /></th>
      <th class="fri"><bean:message key="app.calendar.weekday.6" /></th>
      <th class="sat"><bean:message key="app.calendar.weekday.7" /></th>
    <% } %>  
  </tr>

<!-- iterate over the days in the calendar -->       
<logic:iterate id="calendarForm"
               name="calendarListHeadForm"
               property="calendarForm"
               scope="session"
               type="com.wolmerica.calendar.CalendarForm">
                   
  <logic:equal name="calendarForm" property="dayOfWeek" value="1">                   
    <% dOW = "sun"; %>
  </logic:equal>
  <logic:equal name="calendarForm" property="dayOfWeek" value="2">                   
    <% dOW = "mon"; %>
  </logic:equal>
  <logic:equal name="calendarForm" property="dayOfWeek" value="3">                   
    <% dOW = "tue"; %>
  </logic:equal>
  <logic:equal name="calendarForm" property="dayOfWeek" value="4">                   
    <% dOW = "wed"; %>
  </logic:equal>
  <logic:equal name="calendarForm" property="dayOfWeek" value="5">                   
    <% dOW = "thu"; %>
  </logic:equal>
  <logic:equal name="calendarForm" property="dayOfWeek" value="6">                   
    <% dOW = "fri"; %>
  </logic:equal>
  <logic:equal name="calendarForm" property="dayOfWeek" value="7">                   
    <% dOW = "sat"; %>
  </logic:equal>

  <logic:equal name="calendarForm" property="dayOfWeek" value="1">
    <tr id="week<bean:write name="calendarForm" property="weekNumber" />">
  </logic:equal>  
  
  <logic:equal name="calendarForm" property="currentMonthId" value="false">
    <% if (mTH.compareToIgnoreCase("prev") == 0) 
         mTH = "prev";
       else
         mTH = "next"; 
    %>
  </logic:equal>
  <logic:equal name="calendarForm" property="currentMonthId" value="true">
    <% mTH = "curr"; %>
  </logic:equal>  
      <% if (calendarMode.equalsIgnoreCase("1")) { %>  
        <td class="<%=mTH%> leftEdge"></td>
      <% } %>
      <td class="<%=mTH%> <%=dOW%>" id="<%=mTH%><bean:write name="calendarForm" property="key" />">
        <div class="date">
          <% if (permAddId.equalsIgnoreCase("true")) { %>            
            <a href="ScheduleEntry.do?calendarDate=<bean:write name="calendarForm" 
               property="theDate" />&fromDate=<bean:write name="calendarListHeadForm"
               property="currentFromDate" />&toDate=<bean:write name="calendarListHeadForm"
               property="currentToDate" />"><bean:write name="calendarForm" 
               property="dayOfMonth" /></a>
          <% } else { %>
            <bean:write name="calendarForm" property="dayOfMonth" />
          <% } %>
        </div>
        <% if ((dOW.equalsIgnoreCase("sun")) && (calendarMode.equalsIgnoreCase("3"))) { %>
          <span class="link">
            <a href="EventCalendarList.do?fromDate=<bean:write name="calendarForm"
              property="theDate" />&toDate=<bean:write name="calendarForm"
              property="theDate" />&mode=2" ><bean:message key="app.calendar.week" /></a>
          </span>
        <% } %>
        <% if (calendarMode.equalsIgnoreCase("2")) { %>
          <span class="link">
            <a href="EventCalendarList.do?fromDate=<bean:write name="calendarForm"
              property="theDate" />&toDate=<bean:write name="calendarForm"
              property="theDate" />&mode=1" ><bean:message key="app.calendar.day" /></a>
          </span>
        <% } %>        
        <logic:equal name="calendarForm" property="currentMonthId" value="true">                 
          <logic:notEqual name="scheduleListHeadForm" property="lastRecord" value="0"> 
          <!-- Iterate over all the schedules -->  
            <% i=0; %>
            <logic:iterate id="scheduleForm"
                           name="scheduleListHeadForm"
                           property="scheduleForm"
                           scope="session"
                           type="com.wolmerica.schedule.ScheduleForm">   
              <%
                attCalDateValue = calendarForm.getTheDate().substring(6,10)
                                + calendarForm.getTheDate().substring(0,2)
                                + calendarForm.getTheDate().substring(3,5);
                attStartDateValue = scheduleForm.getStartDate().substring(6,10)
                                  + scheduleForm.getStartDate().substring(0,2)
                                  + scheduleForm.getStartDate().substring(3,5);
                attEndDateValue = scheduleForm.getEndDate().substring(6,10)
                                + scheduleForm.getEndDate().substring(0,2)
                                + scheduleForm.getEndDate().substring(3,5);

                if ((attCalDateValue.compareToIgnoreCase(attStartDateValue) >= 0) &&
                   (attCalDateValue.compareToIgnoreCase(attEndDateValue) <= 0)) {                
              %>
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
                <% permOffset = new Integer(i); %>
                <logic:iterate id="permissionListForm"
                               name="scheduleListHeadForm"
                               property="permissionListForm"
                               scope="session"
                               offset="<%=permOffset.toString()%>"
                               length="1"
                               type="com.wolmerica.permission.PermissionListForm">
                  <% 
                    permViewId = permissionListForm.getViewId();              
                    permEditId = permissionListForm.getEditId();
                    permDeleteId = permissionListForm.getDeleteId();
                    permLockAvailableId = permissionListForm.getLockAvailableId();
                    permLockedBy = permissionListForm.getLockedBy();            
                  %>
                </logic:iterate>   
                
                <% 
                   attAppointment = false; 
                   attShowStartTime = false;
                   attShowClientName = false;
                   attShowInfoIcon = false; 
                   
                   if (attCalDateValue.compareToIgnoreCase(attStartDateValue) == 0) {
                     if (!((scheduleForm.getStartHour().equalsIgnoreCase(scheduleForm.getEndHour())) &&
                        (scheduleForm.getStartMinute().equalsIgnoreCase(scheduleForm.getEndMinute())))) { 
                       attShowStartTime = true;                 
                     }
                   }                   
                %>
                   
                <logic:notEqual name="scheduleForm" property="examKey" value="0">
                  <% attShowInfoIcon = true; %>
                </logic:notEqual>
                <logic:notEqual name="scheduleForm" property="category1Key" value="0">
                  <% attShowInfoIcon = true; %>
                </logic:notEqual>
                <logic:notEqual name="scheduleForm" property="category2Key" value="0">
                  <% attShowInfoIcon = true; %>
                </logic:notEqual>

                <logic:equal name="scheduleForm" property="eventTypeKey" value="0">                 
                  <% attAppointment = true; 
                     attShowStartTime = true;
                     attShowClientName = true; %>  
                     
                  <div class="event">                
                </logic:equal>
                <logic:equal name="scheduleForm" property="eventTypeKey" value="1"> 
                  <div class="event holiday">
                  <img src="./images/holiday.jpg"  height="15" width="15" border="0" title="<bean:message key="app.schedule.holiday" />" >
                </logic:equal>
                <logic:equal name="scheduleForm" property="eventTypeKey" value="2"> 
                  <div class="event vacation">
                </logic:equal>
                <logic:equal name="scheduleForm" property="eventTypeKey" value="3"> 
                  <div class="event birthday">
                  <img src="./images/birthday.gif" height="15" width="15" border="0" title="<bean:message key="app.schedule.birthday" />" >                  
                </logic:equal>   
                <logic:equal name="scheduleForm" property="eventTypeKey" value="4"> 
                  <div class="event anniversary">
                  <img src="./images/anniversary.jpg" height="18" width="18" border="0" title="<bean:message key="app.schedule.anniversary" />" >                  
                </logic:equal>
                <logic:equal name="scheduleForm" property="eventTypeKey" value="5"> 
                  <% attShowStartTime = true;
                     attShowClientName = true; %>
                
                  <div class="event reminder">
                </logic:equal>
                
                <span class="time">
                  <logic:equal name="scheduleForm" property="eventTypeKey" value="1">
                    <% if (attShowStartTime) { %>
                      <bean:message key="app.calendar.closed" />&nbsp;
                    <% } %>
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="eventTypeKey" value="2"> 
                    <img src="./images/vacation.jpg" height="15" width="15" border="0" title="<bean:message key="app.schedule.vacation" />" >                  
                  </logic:equal>     
                  <logic:equal name="scheduleForm" property="eventTypeKey" value="5"> 
                    <img src="./images/reminder.gif" height="15" width="15" border="0" title="<bean:message key="app.schedule.reminder" />" >
                  </logic:equal>
                  <% if (attShowStartTime) {              
                       startHour = new Byte(scheduleForm.getStartHour()).intValue();
                       startMinute = new Byte(scheduleForm.getStartMinute()).intValue();
                       timeOfDay = "am";               
                       if (startHour > 11)  
                         timeOfDay = "pm";            
                       if (startHour > 12)
                         startHour = startHour - 12;
                  %>
                    <% if (startMinute == 0) { %>
                      <%=startHour%>&nbsp;<%=timeOfDay%>
                    <% } else { %>
                       <%=startHour%>:<bean:write name="scheduleForm" property="startMinute" />&nbsp;<%=timeOfDay%>
                    <% } %>
                  <% } %>
                  <logic:equal name="scheduleForm" property="category1Id" value="true"> 
                    <img src="./images/boarding.gif" width="20" height="20" border="0" title="<bean:message key="app.schedule.boarding" />" >
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="category2Id" value="true"> 
                    <img src="./images/needle.gif" width="20" height="20" border="0" title="<bean:message key="app.schedule.vaccination" />" >
                  </logic:equal>                    
                  <logic:equal name="scheduleForm" property="category3Id" value="true">
                    <img src="./images/surgery.gif" width="18" height="18" border="0" title="<bean:message key="app.schedule.surgery" />" >
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="category4Id" value="true">
                    <img src="./images/grooming.gif" width="20" height="20" border="0" title="<bean:message key="app.schedule.grooming" />" >
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="locationId" value="false"> 
                    <img src="./images/truck.jpg" width="16" height="16" border="0" title="<bean:message key="app.schedule.offSite" />" >
                  </logic:equal>
                  <% if (attAppointment) { %>
                    <logic:equal name="scheduleForm" property="statusKey" value="0">
                      <logic:equal name="scheduleForm" property="reminderPrefKey" value="0">
                        <img src="./images/bluephone.gif" height="18" width="18" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                      </logic:equal>
                      <logic:equal name="scheduleForm" property="reminderPrefKey" value="1">
                        <img src="./images/mobilephone.gif" height="18" width="18" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                      </logic:equal>
                      <logic:equal name="scheduleForm" property="reminderPrefKey" value="2">
                        <img src="./images/email.gif" height="18" width="18" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                      </logic:equal>
                      <logic:equal name="scheduleForm" property="reminderPrefKey" value="3">
                        <img src="./images/sms.gif" height="18" width="18" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                      </logic:equal>
                    </logic:equal>
                  <% } else { %>
                    <logic:equal name="scheduleForm" property="eventTypeKey" value="5"> 
                      <logic:equal name="scheduleForm" property="statusKey" value="0">
                        <logic:equal name="scheduleForm" property="reminderPrefKey" value="0">
                          <img src="./images/greenphone.gif" height="20" width="20" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                        </logic:equal>
                        <logic:equal name="scheduleForm" property="reminderPrefKey" value="1">
                          <img src="./images/mobilephone.gif" height="20" width="20" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                        </logic:equal>
                        <logic:equal name="scheduleForm" property="reminderPrefKey" value="2">
                          <img src="./images/email.gif" height="20" width="20" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                        </logic:equal>                        
                      </logic:equal>
                    </logic:equal>            
                  <% } %>  
                  <logic:equal name="scheduleForm" property="statusKey" value="4">
                    <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerCancel" />">
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="statusKey" value="5">
                    <img src="./images/reschedule.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerReschedule" />">
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="statusKey" value="6">
                    <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.officeCancel" />">
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="statusKey" value="7">
                    <img src="./images/reschedule.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.officeReschedule" />">
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="releaseId" value="true">                      
                    <img src="./images/pencil.gif" height="16" width="16" border="0" title="<bean:message key="app.schedule.releaseRequired" />">
                  </logic:equal>
                  <% if (attShowInfoIcon) { %>
                    <img src="./images/info.gif" height="16" width="16" border="0" title="<bean:message key="app.schedule.examInfo" />">
                  <% } %>
                  <logic:notEqual name="scheduleForm" property="customerInvoiceKey" value="">
                    <img src="./images/invoice.jpg" height="20" width="20" border="0" title="<bean:message key="app.schedule.invoicedAppointment" />">
                  </logic:notEqual>
                </span>

                <span class="subject">
                  <a href="ScheduleGet.do?key=<bean:write name="scheduleForm" 
                    property="key" />&fromDate=<bean:write name="calendarListHeadForm"
                    property="currentFromDate" />&toDate=<bean:write name="calendarListHeadForm"
                    property="currentToDate" /> "><bean:write name="scheduleForm" 
                    property="subject" /></a>
                    <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                      <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
                    <% } %>     
                </span>
                <% if (attShowClientName) { %>
                  <span class="client">
                    <bean:write name="scheduleForm" property="clientName" />
                  </span>
                <% } %>
                <% if (calendarMode.equalsIgnoreCase("1") || calendarMode.equalsIgnoreCase("2")) { %>                
                  <logic:notEqual name="scheduleForm" property="attributeToName" value=""> 
                    <span class="attribute">
                      <bean:write name="scheduleForm" property="attributeToName" />
                    </span>                         
                  </logic:notEqual>
                  <logic:notEqual name="scheduleForm" property="eventTypeKey" value="1">
                    <logic:notEqual name="resourceListHeadForm" property="recordCount" value="0"> 
                      <!-- Iterate over all the resources -->  
                      <logic:iterate id="resourceForm"
                                     name="resourceListHeadForm"
                                     property="resourceForm"
                                     scope="session"
                                     type="com.wolmerica.resource.ResourceForm">
                      <% if (scheduleForm.getKey().compareToIgnoreCase(resourceForm.getScheduleKey()) == 0) { %>
                        <span class="resource">
                          <img src="./images/resource.gif" height="16" width="16" border="0" title="<bean:message key="app.workorder.resource" /> ">
                          <bean:write name="resourceForm" property="resourceName" />
                        </span>                 
                      <% } %>
                      </logic:iterate>
                    </logic:notEqual>
                  </logic:notEqual>
                  <logic:equal name="scheduleForm" property="eventTypeKey" value="0">                
                    <span class="location">
                      <logic:equal name="scheduleForm" property="eventTypeKey" value="0"> 
                        <logic:equal name="scheduleForm" property="locationId" value="false">
                          <bean:write name="scheduleForm" property="address" />
                        </logic:equal>
                      </logic:equal>  
                    </span>
                  </logic:equal>
                <% } %>                  
                </div>                             
              <% } %>
              <% i++; %>
            </logic:iterate> 
          </logic:notEqual> 
        </logic:equal>          
      </td>               
  <logic:equal name="calendarForm" property="dayOfWeek" value="7">               
    </tr>
  </logic:equal>  
  
</logic:iterate>
  
  <tr>
    <td>
      <strong><bean:message key="app.calendar.item" />:</strong>
      <bean:message key="app.localeCurrency" />
      <bean:write name="scheduleListHeadForm" property="workOrderItemTotal" />
      &nbsp;&nbsp;
      <strong><bean:message key="app.calendar.service" />:</strong>
      <bean:message key="app.localeCurrency" />
      <bean:write name="scheduleListHeadForm" property="workOrderServiceTotal" />
    </td>
    <td colspan="6" align="center">
      <logic:notEqual name="scheduleListHeadForm" property="lastRecord" value="0">         
       <button type="button" onClick="popup = putinpopup('ScheduleExport.do'); return false" target="_PDF"><bean:message key="app.export" /></button>
      </logic:notEqual>
      &nbsp;<strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="scheduleListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="scheduleListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="scheduleListHeadForm" property="recordCount" />      
    </td>
  </tr>

</table>
