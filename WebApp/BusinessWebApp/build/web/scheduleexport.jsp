<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<table>
  <tr>
    <td><strong><bean:message key="app.export.label.subject" /></strong></td>
    <td><strong><bean:message key="app.export.label.startdate" /></strong></td>
    <td><strong><bean:message key="app.export.label.starttime" /></strong></td>
    <td><strong><bean:message key="app.export.label.enddate" /></strong></td>
    <td><strong><bean:message key="app.export.label.endtime" /></strong></td>
    <td><strong><bean:message key="app.export.label.alldayevent" /></strong></td>
    <td><strong><bean:message key="app.export.label.reminderonoff" /></strong></td>
    <td><strong><bean:message key="app.export.label.reminderdate" /></strong></td>
    <td><strong><bean:message key="app.export.label.remindertime" /></strong></td>
    <td><strong><bean:message key="app.export.label.categories" /></strong></td>
    <td><strong><bean:message key="app.export.label.description" /></strong></td>
    <td><strong><bean:message key="app.export.label.location" /></strong></td>
    <td><strong><bean:message key="app.export.label.priority" /></strong></td>
    <td><strong><bean:message key="app.export.label.private" /></strong></td>
    <td><strong><bean:message key="app.export.label.sensitivity" /></strong></td>        
    <td><strong><bean:message key="app.export.label.showtimeas" /></strong></td>
  </tr>
<logic:notEqual name="scheduleListHeadForm" property="lastRecord" value="0"> 
  <%  
     boolean showDetail = false;
     int startHour = 0;
     int startMinute = 0;
     int endHour = 0;
     int endMinute = 0;
     String strStartMinute = "00";
     String strStartTOD = "AM";
     String strEndMinute = "00";
     String strEndTOD = "AM";
     String strAllDay = "FALSE";
     String strReminder = "FALSE";
  %>
    
  <logic:iterate id="scheduleForm"
                 name="scheduleListHeadForm"
                 property="scheduleForm"
                 scope="session"
                 type="com.wolmerica.schedule.ScheduleForm">  
                     
    <% 
       showDetail = false;
       startHour = new Byte(scheduleForm.getStartHour()).intValue();
       startMinute = new Byte(scheduleForm.getStartMinute()).intValue();
       endHour = new Byte(scheduleForm.getEndHour()).intValue();
       endMinute = new Byte(scheduleForm.getEndMinute()).intValue();
                
       strStartMinute = ":" + startMinute;
       strStartTOD = "AM"; 
       if (startHour > 11)
         strStartTOD = "PM";
       if (startHour > 12) 
         startHour = startHour - 12;
       if (startMinute < 10)
         strStartMinute = ":0" + startMinute;
         
       strEndMinute = ":" + endMinute;
       strEndTOD = "AM"; 
       if (endHour > 11)
         strEndTOD = "PM";
       if (endHour > 12) 
         endHour = endHour - 12;
       if (endMinute < 10)
         strEndMinute = ":0" + endMinute;
         
       strAllDay = "FALSE";
       strReminder = "FALSE";
    %>     
    
    <logic:notEqual name="scheduleForm" property="eventTypeKey" value="1">
      <logic:notEqual name="scheduleForm" property="eventTypeKey" value="2">
        <logic:notEqual name="scheduleForm" property="eventTypeKey" value="3">
          <logic:notEqual name="scheduleForm" property="eventTypeKey" value="4">
            <% showDetail = true; %>
          </logic:notEqual>
        </logic:notEqual>
      </logic:notEqual>
    </logic:notEqual>      
                     
    <tr>
      <td>
        <% if (showDetail) { %>
          <bean:write name="scheduleForm" property="clientName" />&nbsp;-
        <% } %>
        <logic:equal name="scheduleForm" property="eventTypeKey" value="1">
          [<bean:message key="app.export.holiday" />]
        </logic:equal>          
        <logic:equal name="scheduleForm" property="eventTypeKey" value="2">
          [<bean:message key="app.export.vacation" />]
        </logic:equal>
        <logic:equal name="scheduleForm" property="eventTypeKey" value="3">
          [<bean:message key="app.export.birthday" />]
          <% strAllDay = "TRUE"; %>
        </logic:equal> 
        <logic:equal name="scheduleForm" property="eventTypeKey" value="4">
          [<bean:message key="app.export.anniversary" />]
          <% strAllDay = "TRUE"; %>
        </logic:equal>          
        <logic:equal name="scheduleForm" property="eventTypeKey" value="5">          
          [<bean:message key="app.export.reminder" />]
        </logic:equal>
        <logic:equal name="scheduleForm" property="category1Id" value="true">
          [<bean:message key="app.export.boarding" />]
        </logic:equal>
        <logic:equal name="scheduleForm" property="category2Id" value="true">
          [<bean:message key="app.export.vaccination" />]
        </logic:equal>        
        <logic:equal name="scheduleForm" property="category3Id" value="true">
          [<bean:message key="app.export.surgery" />]
        </logic:equal>          
        <logic:equal name="scheduleForm" property="category4Id" value="true">
          [<bean:message key="app.export.grooming" />]
        </logic:equal>
        &nbsp;<bean:write name="scheduleForm" property="subject" />       
      </td>
      <td><bean:write name="scheduleForm" property="startDate" /></td>
      <td><%=startHour%><%=strStartMinute%> <%=strStartTOD%></td>
      <td><bean:write name="scheduleForm" property="endDate" /></td>
         <td><%=endHour%><%=strEndMinute%> <%=strEndTOD%></td>
      <td><%=strAllDay%></td>
      <td><%=strReminder%></td>
      <td><bean:write name="scheduleForm" property="startDate" /></td>
      <td><%=startHour%><%=strStartMinute%> <%=strStartTOD%></td>
      <td>
        <logic:equal name="scheduleForm" property="eventTypeKey" value="0">
          <bean:message key="app.export.appointment" />
        </logic:equal>     
        <logic:equal name="scheduleForm" property="eventTypeKey" value="1">
          <bean:message key="app.export.holiday" />
        </logic:equal>          
        <logic:equal name="scheduleForm" property="eventTypeKey" value="2">
          <bean:message key="app.export.vacation" />
        </logic:equal>
        <logic:equal name="scheduleForm" property="eventTypeKey" value="3">
          <bean:message key="app.export.birthday" />
        </logic:equal>
        <logic:equal name="scheduleForm" property="eventTypeKey" value="4">
          <bean:message key="app.export.anniversary" />
        </logic:equal>
        <logic:equal name="scheduleForm" property="eventTypeKey" value="5">
          <bean:message key="app.export.reminder" />
        </logic:equal>
        <logic:equal name="scheduleForm" property="category1Id" value="true">
          <bean:message key="app.export.boarding" />
        </logic:equal>
        <logic:equal name="scheduleForm" property="category2Id" value="true">
          <bean:message key="app.export.vaccination" />
        </logic:equal>
        <logic:equal name="scheduleForm" property="category3Id" value="true">
          <bean:message key="app.export.surgery" />
        </logic:equal>
        <logic:equal name="scheduleForm" property="category4Id" value="true">
          <bean:message key="app.export.grooming" />
        </logic:equal>
      </td>
      <td>
        <% if (showDetail) { %>
          <bean:write name="scheduleForm" property="clientName" />&nbsp;-        
          &nbsp;<bean:write name="scheduleForm" property="subject" />                  
          &nbsp;&nbsp;
          <bean:message key="app.export.phone" />:
          &nbsp;<bean:write name="scheduleForm" property="customerPhone" />        
          <logic:notEqual name="scheduleForm" property="attributeToName" value="">
            &nbsp;&nbsp;<bean:message key="app.export.regarding" />:
            &nbsp;<bean:write name="scheduleForm" property="attributeToName" />
            &nbsp;&nbsp;<bean:message key="app.export.regardingDate" />:
            &nbsp;<bean:write name="scheduleForm" property="attributeToDate" />
          </logic:notEqual>
        <% } %>
      </td>
      <td>
        <% if (showDetail) { %>
          <logic:equal name="scheduleForm" property="locationId" value="true">
            <bean:message key="app.export.onsite" />
          </logic:equal>
        <% } %>            
        <logic:equal name="scheduleForm" property="locationId" value="false">
          <bean:write name="scheduleForm" property="address" />
          &nbsp;<bean:write name="scheduleForm" property="city" />
          ,&nbsp;<bean:write name="scheduleForm" property="state" />
        </logic:equal>
      </td>
      <td><bean:message key="app.export.priority" /></td>
      <td><bean:message key="app.export.private" /></td>
      <td><bean:message key="app.export.sensitivity" /></td>        
      <td><bean:message key="app.export.showtimeas" /></td>      
    </tr>
  </logic:iterate> 
</logic:notEqual>    
</table>
