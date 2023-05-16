<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>
   
<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<%
  String currentColor = "ODD";
  int i = 0;
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
  Integer permOffset = new Integer("0");
  String permAddId = "false";
  String permViewId = "false";
  String permEditId = "false";
  String permDeleteId = "false";
  String permLockAvailableId = "false";
  String permLockedBy = "";  
  int startHour = 0;
  int startMinute = 0;
  String timeOfDay = "AM";
  boolean attAppointment = false;
  boolean attShowInfoIcon = false;
%>   
 
<logic:equal name="scheduleListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="scheduleListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

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
 
<!-- Service Labors History by Date Range -->
    <td colspan="9">
      <html:form action="ScheduleList.do">
        <html:hidden property="customerKeyFilter" />
        <html:hidden property="clientName" />
        <strong><bean:message key="app.schedule.customer" />:</strong>
        <span class="ac_holder">
        <input id="clientNameAC" size="40" maxlength="40" value="<bean:write name="scheduleListHeadForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.scheduleListHeadForm.customerKeyFilter.value=obj.id; document.scheduleListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>
        &nbsp;&nbsp;
        <strong><bean:message key="app.schedule.dateRange" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="scheduleListHeadForm" property="fromDate" />" size="10" maxlength="10" >
        <a href="javascript:show_calendar('scheduleListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.schedule.toDate" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="scheduleListHeadForm" property="toDate" />" size="10" maxlength="10" >
        <a href="javascript:show_calendar('scheduleListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        <html:errors property="toDate" />        
    </td>
  </tr>
  <tr>
     <td colspan="9" align="center">
        <strong><bean:message key="app.schedule.eventTypeKey" />:</strong>
        <select name="eventTypeKey">
           <option value="-1" <logic:equal name="scheduleListHeadForm" property="eventTypeKey" value="-1">selected</logic:equal> >
           <bean:message key="app.schedule.allEvents" />            
           <option value="0" <logic:equal name="scheduleListHeadForm" property="eventTypeKey" value="0">selected</logic:equal> >
           <bean:message key="app.schedule.appointment" />
           <option value="5" <logic:equal name="scheduleListHeadForm" property="eventTypeKey" value="5">selected</logic:equal> >
           <bean:message key="app.schedule.reminder" /> 
           <option value="4" <logic:equal name="scheduleListHeadForm" property="eventTypeKey" value="4">selected</logic:equal> >
           <bean:message key="app.schedule.anniversary" />
           <option value="3" <logic:equal name="scheduleListHeadForm" property="eventTypeKey" value="3">selected</logic:equal> >
           <bean:message key="app.schedule.birthday" />
           <option value="1" <logic:equal name="scheduleListHeadForm" property="eventTypeKey" value="1">selected</logic:equal> >
           <bean:message key="app.schedule.holiday" />
           <option value="2" <logic:equal name="scheduleListHeadForm" property="eventTypeKey" value="2">selected</logic:equal> >
           <bean:message key="app.schedule.vacation" />           
        </select>        
        &nbsp;&nbsp;
        <strong><bean:message key="app.schedule.statusKey" />:</strong>
        <select name="statusKey">
           <option value="-1" <logic:equal name="scheduleListHeadForm" property="statusKey" value="-1">selected</logic:equal> >
           <bean:message key="app.schedule.allStatus" />            
           <option value="0" <logic:equal name="scheduleListHeadForm" property="statusKey" value="0">selected</logic:equal> >
           <bean:message key="app.schedule.notifyCustomer" />
           <option value="2" <logic:equal name="scheduleListHeadForm" property="statusKey" value="2">selected</logic:equal> >
           <bean:message key="app.schedule.pending" /> 
           <option value="3" <logic:equal name="scheduleListHeadForm" property="statusKey" value="3">selected</logic:equal> >
           <bean:message key="app.schedule.inProgress" />           
           <option value="4" <logic:equal name="scheduleListHeadForm" property="statusKey" value="4">selected</logic:equal> >
           <bean:message key="app.schedule.customerCancel" />
           <option value="5" <logic:equal name="scheduleListHeadForm" property="statusKey" value="5">selected</logic:equal> >
           <bean:message key="app.schedule.customerReschedule" />
           <option value="6" <logic:equal name="scheduleListHeadForm" property="statusKey" value="6">selected</logic:equal> >
           <bean:message key="app.schedule.officeCancel" />
           <option value="7" <logic:equal name="scheduleListHeadForm" property="statusKey" value="7">selected</logic:equal> >
           <bean:message key="app.schedule.officeReschedule" />
           <option value="8" <logic:equal name="scheduleListHeadForm" property="statusKey" value="8">selected</logic:equal> >
           <bean:message key="app.schedule.complete" />
        </select>        
        &nbsp;&nbsp;
        <strong><bean:message key="app.schedule.locationId" />:</strong>
        <select name="locationKey">
           <option value="-1" <logic:equal name="scheduleListHeadForm" property="locationKey" value="-1">selected</logic:equal> >
           <bean:message key="app.schedule.allLocation" />
           <option value="1" <logic:equal name="scheduleListHeadForm" property="locationKey" value="1">selected</logic:equal> >
           <bean:message key="app.schedule.onSite" />
           <option value="0" <logic:equal name="scheduleListHeadForm" property="locationKey" value="0">selected</logic:equal> >
           <bean:message key="app.schedule.offSite" /> 
        </select>        
        &nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </html:form>
    </td>    
  </tr>
  <tr>
    <td colspan="9">
      <button type="button" onClick="window.location='EventCalendarList.do?fromDate=<bean:write name="scheduleListHeadForm"
        property="fromDate" />&toDate=<bean:write name="scheduleListHeadForm"
        property="toDate" />&mode=3' "><bean:message key="app.schedule.calendarByMonth" /></button>
      <br>
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.schedule.clientName" /></th>      
    <th><bean:message key="app.schedule.phoneNum" /></th>      
    <th><bean:message key="app.schedule.attributeToName" /></th>    
    <th><bean:message key="app.schedule.attributeToDate" /></th>    
    <th><bean:message key="app.schedule.subject" /></th>    
    <th><bean:message key="app.schedule.startDate" /></th>     
    <th align="right"><bean:message key="app.schedule.startTime" /></th>
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>        
        <button type="button" onClick="window.location='ScheduleEntry.do?customerKeyFilter=<bean:write name="scheduleListHeadForm"      
          property="customerKeyFilter" />&fromDate=<bean:write name="scheduleListHeadForm" 
          property="fromDate" />&toDate=<bean:write name="scheduleListHeadForm" 
          property="toDate" />&pageNo=<bean:write name="scheduleListHeadForm" 
          property="currentPage" />' "><bean:message key="app.schedule.addTitle" /></button>
      <% } %>
    </th>      
  </tr>    
  <tr>
        <td colspan="9">
	  <hr>
	</td>
      </tr>  
<logic:notEqual name="scheduleListHeadForm" property="lastRecord" value="0"> 
  <logic:iterate id="scheduleForm"
                 name="scheduleListHeadForm"
                 property="scheduleForm"
                 scope="session"
                 type="com.wolmerica.schedule.ScheduleForm">                 
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
        
        <logic:notEqual name="scheduleForm" property="customerInvoiceKey" value="">
          <% permDeleteId = "false"; %>
        </logic:notEqual>        

<!-- Make adjustments to not allow edits to events that are aged more than 3 days -->  
        <logic:equal name="scheduleForm" property="activeId" value="false">
          <% 
            if (permEditId.equalsIgnoreCase("true")) {
              permViewId = permEditId;
            }
            permEditId = "false";
            permDeleteId = "false";
          %> 
        </logic:equal>   

        <% attAppointment = false;
           attShowInfoIcon = false; %>
        <logic:equal name="scheduleForm" property="eventTypeKey" value="0">
          <% attAppointment = true; %>    
        </logic:equal>
        <logic:notEqual name="scheduleForm" property="examKey" value="0">
          <% attShowInfoIcon = true; %>
        </logic:notEqual>
        <logic:notEqual name="scheduleForm" property="category1Key" value="0">
          <% attShowInfoIcon = true; %>
        </logic:notEqual>
        <logic:notEqual name="scheduleForm" property="category2Key" value="0">
          <% attShowInfoIcon = true; %>
        </logic:notEqual>
             
<!-- Set values for row shading -->         
        <%     
          if ( i % 2 == 0) {
             currentColor = "ODD";
          } 
          else {
             currentColor = "EVEN";
          }
          i++;
        %>   
            
	<tr align="left" class="<%=currentColor %>">
	  <td>
            <bean:write name="scheduleForm" property="clientName" />           
	  </td>
          <td>
            <bean:write name="scheduleForm" property="customerPhone" />
	  </td>
	  <td>
	    <bean:write name="scheduleForm" property="attributeToName" />
	  </td>  
	  <td>
            <bean:write name="scheduleForm" property="attributeToDate" />
	  </td> 	  
	  <td>
            <logic:equal name="scheduleForm" property="eventTypeKey" value="1"> 
              <img src="./images/holiday.jpg" height="15" width="15" border="0" title="<bean:message key="app.schedule.holiday" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="eventTypeKey" value="2"> 
              <img src="./images/vacation.jpg" height="15" width="15" border="0" title="<bean:message key="app.schedule.vacation" />" >                  
            </logic:equal>            
            <logic:equal name="scheduleForm" property="eventTypeKey" value="3"> 
              <img src="./images/birthday.gif" height="15" width="15" border="0" title="<bean:message key="app.schedule.birthday" />" >                  
            </logic:equal>
            <logic:equal name="scheduleForm" property="eventTypeKey" value="4"> 
             <img src="./images/anniversary.jpg" height="18" width="18" border="0" title="<bean:message key="app.schedule.anniversary" />" >                  
            </logic:equal>                  
            <logic:equal name="scheduleForm" property="eventTypeKey" value="5"> 
              <img src="./images/reminder.gif" height="15" width="15" border="0" title="<bean:message key="app.schedule.reminder" />" >
            </logic:equal>
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
              <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerCancel" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="statusKey" value="5">
              <img src="./images/reschedule.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerReschedule" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="statusKey" value="6">
              <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.officeCancel" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="statusKey" value="7">
              <img src="./images/reschedule.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.officeReschedule" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="releaseId" value="true">
              <img src="./images/pencil.gif" height="16" width="16" border="0" title="<bean:message key="app.schedule.releaseRequired" />" >
            </logic:equal>
            <% if (attShowInfoIcon) { %>
              <img src="./images/info.gif" height="16" width="16" border="0" title="<bean:message key="app.schedule.examInfo" />" >
            <% } %>
            <logic:notEqual name="scheduleForm" property="customerInvoiceKey" value="">
              <img src="./images/invoice.jpg" height="20" width="20" border="0" title="<bean:message key="app.schedule.invoicedAppointment" />" >
            </logic:notEqual>            
            <bean:write name="scheduleForm" property="subject" />
	  </td>   
	  <td>
            <bean:write name="scheduleForm" property="startDate" />
	  </td>           
	  <td align="right">
            <% startHour = new Byte(scheduleForm.getStartHour()).intValue();
               startMinute = new Byte(scheduleForm.getStartMinute()).intValue();
               timeOfDay = "AM";               
               if (startHour > 11)  
                 timeOfDay = "PM";            
               if (startHour <= 12) { %>
              <bean:write name="scheduleForm" property="startHour" />
            <% } else { startHour = startHour - 12; %>
               <%=startHour%>
            <% } %>
            <% if (startMinute == 0) { %>
                :0<bean:write name="scheduleForm" property="startMinute" />
            <% } else { %>
              :<bean:write name="scheduleForm" property="startMinute" />
            <% } %>
            <%=timeOfDay%>
	  </td>         
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="ScheduleGet.do?key=<bean:write name="scheduleForm" 
               property="key" />
<logic:notEqual name="scheduleListHeadForm" property="customerKeyFilter" value="">
&customerKeyFilter=<bean:write name="scheduleListHeadForm" property="customerKeyFilter" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="scheduleListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="toDate" value="">
&toDate=<bean:write name="scheduleListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="scheduleListHeadForm" property="currentPage" />
</logic:notEqual>
"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="ScheduleGet.do?key=<bean:write name="scheduleForm" 
               property="key" />
<logic:notEqual name="scheduleListHeadForm" property="customerKeyFilter" value="">
&customerKeyFilter=<bean:write name="scheduleListHeadForm" property="customerKeyFilter" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="scheduleListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="toDate" value="">
&toDate=<bean:write name="scheduleListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="scheduleListHeadForm" property="currentPage" />
</logic:notEqual>
"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>              
	  </td>
	  <td align="right">
            <logic:equal name="scheduleForm" property="allowDeleteId" value="true">                 
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>
               <a href="ScheduleDelete.do?key=<bean:write name="scheduleForm"
                 property="key" />
<logic:notEqual name="scheduleListHeadForm" property="customerKeyFilter" value="">
&customerKeyFilter=<bean:write name="scheduleListHeadForm" property="customerKeyFilter" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="scheduleListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="toDate" value="">
&toDate=<bean:write name="scheduleListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="scheduleListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="scheduleListHeadForm" property="currentPage" />
</logic:notEqual>                 
"  onclick="return confirm('<bean:message key="app.schedule.delete.message" />')" ><bean:message key="app.delete" /></a>
              <% } %>
            </logic:equal>  
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="9">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="3">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='ScheduleList.do?fromDate=<bean:write name="scheduleListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="scheduleListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="scheduleListHeadForm" 
        property="previousPage" />&eventTypeKey=<bean:write name="scheduleListHeadForm"      
        property="eventTypeKey" />&statusKey=<bean:write name="scheduleListHeadForm" 
        property="statusKey" />&locationKey=<bean:write name="scheduleListHeadForm" 
        property="locationKey" />' "><bean:message key="app.previous" /></button>     
    </td>
    <td colspan="4">
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
    <td>
      <button type="button" <%=attDisableNextButton%> onClick="window.location='ScheduleList.do?fromDate=<bean:write name="scheduleListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="scheduleListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="scheduleListHeadForm" 
        property="nextPage" />&eventTypeKey=<bean:write name="scheduleListHeadForm"      
        property="eventTypeKey" />&statusKey=<bean:write name="scheduleListHeadForm" 
        property="statusKey" />&locationKey=<bean:write name="scheduleListHeadForm" 
        property="locationKey" />' "><bean:message key="app.next" /></button>     
    </td>  
  </tr>
</table>