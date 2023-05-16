<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<style type="text/css">
.collapsible {
          display: none; /* Only important part */
          border: dashed 1px silver;
          padding: 10px;
}
</style>


<script type="text/javascript" language="JavaScript"><!--
//preload image
var collimg = new Image();
collimg.src = "./images/collapse.gif";
var expimg = new Image();
collimg.src = "./images/expand.gif";

function ShowHideLayer(boxID) {
  /* Obtain reference for the selected boxID layer and its button */
  var box = document.getElementById("box"+boxID);
  var boxbtn = document.getElementById("btn"+boxID);

  /* If the selected box is currently invisible, show it */
  if(box.style.display == "none" || box.style.display=="") {
    box.style.display = "block";
    boxbtn.src = "./images/collapse.gif";
  }
  /* otherwise hide it */
  else {
    box.style.display = "none";
    boxbtn.src = "./images/expand.gif";
  }
}
//--></script>


<%
  String attTitle = "app.resource.resourceTitle";
  String attAction = "/ResourceAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="resourceForm" property="key" value="">
  <%
    attTitle = "app.resource.resourceTitle";
    attAction = "/ResourceEdit";
    
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="resourceForm" property="permissionStatus" value="LOCKED">
  <% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="resourceForm" property="permissionStatus" value="READONLY">
    [<bean:write name="resourceForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="ResourceList.do" name="resourceList" method="post">                                  
        <input type="submit" value="<bean:message key="app.resource.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
<logic:notEqual name="resourceForm" property="key" value="">
  <html:hidden property="key" />  
</logic:notEqual>
  <tr>
          <td><strong><bean:message key="app.resource.resourceName" />:</strong></td>
          <td>     
             <html:text property="resourceName" size="20" maxlength="20" readonly="<%=attDisableEdit%>" /> 
             <html:errors property="resourceName" />
          </td>                    
        </tr>
        <tr>
          <td><strong><bean:message key="app.resource.description" />:</strong></td>
          <td>     
             <html:text property="description" size="30" maxlength="30" readonly="<%=attDisableEdit%>" /> 
             <html:errors property="description" />
          </td>                    
        </tr>         
        <tr>
	  <td><strong><bean:message key="app.resource.createUser" />:</strong></td>
          <td><bean:write name="resourceForm" property="createUser" /></td>	          	
	  <td><strong><bean:message key="app.resource.createStamp" />:</strong></td>
          <td><bean:write name="resourceForm" property="createStamp" /></td>	             	    
	</tr>
	<tr>
	  <td><strong><bean:message key="app.resource.updateUser" />:</strong></td>
          <td><bean:write name="resourceForm" property="updateUser" /></td>	               
	  <td><strong><bean:message key="app.resource.updateStamp" />:</strong></td>
          <td><bean:write name="resourceForm" property="updateStamp" /></td>	             
        </tr>	
        <tr>
    <td colspan="8" align="right">
      <html:submit value="Save Resource Values" disabled="<%=attDisableEdit%>" />         
     </html:form> 
    </td>
  </tr>
  <logic:notEqual name="resourceForm" property="key" value="">    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>   
  </logic:notEqual>
</table>

<!-- ================================================================= -->
<!-- Display of the resource schedule                                  -->
<!-- ================================================================= -->
<logic:notEqual name="resourceForm" property="key" value="">   
  <br>
  <strong><bean:message key="app.resource.resourceName" />:</strong>
  &nbsp;&nbsp;
  <bean:write name="resourceForm" property="resourceName" />

  <br>  
  <logic:equal name="resourceInstanceListHeadForm" property="lastRecord" value="0">
    <br>
    <bean:message key="app.schedule.noResults" />
  </logic:equal>
  
  <logic:notEqual name="resourceInstanceListHeadForm" property="lastRecord" value="0"> 

    <jsp:useBean id="workOrderForm" scope="session" 
      class="com.wolmerica.workorder.WorkOrderForm" />

   <%
       String eventTOD = "";     
       String startDate = "";
       int startHour = 8;
       int startMinute = 0;
       String startTime = "";
       String endDate = "";
       int endHour = 10;
       int endMinute = 0;
       String endTime = "";

       String scheduleKey = "";
       String service = "";
       String subject = "";
       String clientName = "";
       String attributeToEntity = "";     
       String attributeToName = "";
       String location = "";
       
       String pStartDate = "";
       String pStartTime = "";
       String pEndDate = "";
       String pEndTime = "";
       String pSubject = "";
       String pClientName = "";
       String pAttributeToEntity = "";
       String pAttributeToName = "";
       String pLocation = "";
       
       int x = 0;
       int eventCnt = 0;
       int y = 0;     
    %>
    
    <logic:iterate id="resourceInstanceForm"
                   name="resourceInstanceListHeadForm"
                   property="resourceInstanceForm"
                   scope="session"
                   type="com.wolmerica.resourceinstance.ResourceInstanceForm">  

      <% x++; %>
      <hr>      
      <a href="javascript:;" onclick="ShowHideLayer('<%=x%>');"><img src="images/expand.gif" alt="Expand Me" name="btn<%=x%>" width="9" height="9" border="0" id="btn<%=x%>" /></a>
      &nbsp;<strong><bean:write name="resourceInstanceForm" property="startDate" /></strong><br>
      <div id="box<%=x%>" class="collapsible">
            
        <logic:notEqual name="resourceInstanceForm" property="eventCount" value="0"> 
      
        <% 
           pStartDate = "";
           pStartTime = "";
           pEndDate = "";
           pEndTime = "";
           pSubject = "";
           pClientName = "";
           pAttributeToEntity = "";
           pAttributeToName = "";
           pLocation = "";
        
           eventCnt = new Integer(resourceInstanceForm.getEventCount()).intValue();          
           y = 0;
           while( y < eventCnt) { 
//--------------------------------------------------------------------------------
// Cast the array value into a workorder form to be evaluated for display purposes.
//--------------------------------------------------------------------------------
             workOrderForm = (com.wolmerica.workorder.WorkOrderForm) resourceInstanceForm.getWorkOrderForm().get(y);
             eventTOD = "AM";                       
             startDate = workOrderForm.getStartDate();
             startHour = new Integer(workOrderForm.getStartHour()).intValue();
             startMinute = new Integer(workOrderForm.getStartMinute()).intValue();
             if (startHour > 11)
               eventTOD = "PM";
             if (startHour > 12)
               startHour = startHour - 12;
             startTime = startHour + ":";
             if (startMinute < 10)
               startTime = startTime + "0";
             startTime = startTime + startMinute + eventTOD;
             
             eventTOD = "AM";                 
             endDate = workOrderForm.getEndDate();
             endHour = new Integer(workOrderForm.getEndHour()).intValue();
             endMinute = new Integer(workOrderForm.getEndMinute()).intValue();
             if (endHour > 11)
               eventTOD = "PM";
             if (endHour > 12)
               endHour = endHour - 12;
             endTime = endHour + ":";
             if (endMinute < 10)
               endTime = endTime + "0";
             endTime = endTime + endMinute + eventTOD;

             scheduleKey = workOrderForm.getScheduleKey();
             service = workOrderForm.getSourceName();             
             subject = workOrderForm.getSubject();
             clientName = workOrderForm.getClientName();
             attributeToEntity = workOrderForm.getAttributeToEntity();
             attributeToName = workOrderForm.getAttributeToName();
             location = "Customer Site";
             if (workOrderForm.getLocationId().equalsIgnoreCase("true")) {
               location = "In Office";
             } 
        %>
               
          <% if (!((pStartDate.equalsIgnoreCase(startDate)) &&
                (pStartTime.equalsIgnoreCase(startTime)) &&
                (pSubject.equalsIgnoreCase(subject)) &&
                (pClientName.equalsIgnoreCase(clientName)) &&
                (pAttributeToEntity.equalsIgnoreCase(attributeToEntity)) &&
                (pAttributeToName.equalsIgnoreCase(attributeToName)) &&
                (pLocation.equalsIgnoreCase(location)))) { %>
        
            <% if (y > 0) { %>
                 <hr>
            <% } %>
                                
               <font size="-1">
                 <strong>Start Date:</strong><%=startDate%>
                 &nbsp;<strong>Time:</strong>&nbsp;<%=startTime%>
                 <br>
                 <strong>End</strong>
                 <% if (!(startDate.equalsIgnoreCase(endDate))) { %>
                   &nbsp;<strong>Date:</strong><%=endDate%>
                 <% } %>
                 &nbsp;<strong>Time:</strong>&nbsp;<%=endTime%>
                 <br>
                 <strong>Location:</strong>&nbsp;<%=location%>
                 <br>               
                 <strong>Subject:</strong>&nbsp;<a href="ScheduleGet.do?key=<%=scheduleKey%> "><%=subject%></a>
                 <br>
                 <strong>Customer:</strong>&nbsp;<%=clientName%>
                 <br>               
                 <strong><%=attributeToEntity%>:</strong>&nbsp;<%=attributeToName%>
                 <br>
                 <strong>Service:</strong>&nbsp;                 
               </font>  
          <% } else { %>
               ;&nbsp;
          <% } %>
             <font size="-1">          
               <%=service%>
             </font>
        <%
             pStartDate = startDate;
             pStartTime = startTime;
             pEndDate = endDate;
             pEndTime = endTime;
             pSubject = subject;
             pClientName = clientName;
             pAttributeToEntity = attributeToEntity;
             pAttributeToName = attributeToName;
             pLocation = location;
                          
             y++;        
           }
        %>   
        </logic:notEqual>   
      </div>
    </logic:iterate> 
  </logic:notEqual>        
</logic:notEqual>
