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

function HideContent(t,d) {
  document.getElementById(d).style.display = "none";
  document.getElementById(t).style.fontSize = '12';
  document.getElementById(t).style.backgroundColor = '#fff';
}
function ShowContent(t,d) {
  document.getElementById(d).style.display = "block";
  document.getElementById(t).style.fontSize = '15';
  document.getElementById(t).style.backgroundColor = '#ccc';
}
function ReverseContentDisplay(t,d) {
  if(document.getElementById(d).style.display == "none") { document.getElementById(d).style.display = "block"; }
  else { document.getElementById(d).style.display = "none"; }
}
//--></script>


<%
  String currentColor = "ODD";
  int i = -1;      
  String attTitle = "app.vehicle.addTitle";            
  String attAction = "/VehicleAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="vehicleForm" property="key" value="">
  <%
    attTitle = "app.vehicle.editTitle";
    attAction = "/VehicleEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="vehicleForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="vehicleForm" property="permissionStatus" value="READONLY">
    [<bean:write name="vehicleForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="VehicleList.do" name="vehicleList" method="post">
        <input type="hidden" name="clientNameFilter" value="<bean:write name="vehicleForm" property="clientNameFilter" />" >
        <input type="hidden" name="yearFilter" value="<bean:write name="vehicleForm" property="yearFilter" />" >
        <input type="hidden" name="makeFilter" value="<bean:write name="vehicleForm" property="makeFilter" />" >
        <input type="hidden" name="pageNo" value="<bean:write name="vehicleForm" property="currentPage" />" >
        <input type="submit" value="<bean:message key="app.vehicle.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="vehicleForm" property="key" value="">        
    <td colspan="6" align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.vehicle.id"/>&sourceKey=<bean:write name="vehicleForm"
                property="key" />&sourceName=<bean:write name="vehicleForm"
                property="clientName" />/<bean:write name="vehicleForm"
                property="year" />/<bean:write name="vehicleForm"
                property="make" />/<bean:write name="vehicleForm"
                property="model" />" >[<bean:write name="vehicleForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."> </a>
    </td>
    </logic:notEqual>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <!-- Display tabs to show and hide vehicle content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff; font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('scheduleTab','scheduleInfo');">
              <bean:message key="app.vehicle.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="vehicleForm" property="key" value="">
            <td align="center">
            <div id="scheduleTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('scheduleTab','scheduleInfo');">
              <bean:message key="app.vehicle.scheduleTab" />
              </a>
            </div>
            </td>
          </logic:notEqual>
        </tr>
      </table>
    </td> 
    <logic:notEqual name="vehicleForm" property="key" value="">
      <td align="right" colspan="6">
        <button type="button" onClick="window.location='CustomerInvoiceReportEntry.do?customerKey=<bean:write name="vehicleForm"
          property="customerKey" />&sourceTypeKey=<bean:message key="app.vehicle.id"/>&sourceKey=<bean:write name="vehicleForm" 
          property="key" />' "><bean:message key="app.vehicle.viewActivity" /></button>
      </td>
    </logic:notEqual>
  </tr>
</table>

<div id="mainInfo" style="display:none;">  
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <html:form action="<%=attAction%>"> 
  <logic:notEqual name="vehicleForm" property="key" value="">
    <html:hidden property="key" /> 
    <input type="hidden" name="sourceTypeKey" value="<bean:message key="app.vehicle.id"/>" > 
  </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.vehicle.year" />:</strong></td>
    <td>     
      <html:text property="year" size="3" maxlength="4" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="year" />
    </td>
    <td><strong><bean:message key="app.vehicle.clientName" />:</strong></td>
    <td>
      <html:hidden property="customerKey" />
      <html:hidden property="clientName" />
      <span class="ac_holder">
      <input id="clientNameAC" size="35" maxlength="35" value="<bean:write name="vehicleForm" property="clientName" />" onFocus="javascript:
      var options = {
		script:'CustomerLookUp.do?json=true&clinicId=false&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.vehicleForm.customerKey.value=obj.id; document.vehicleForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
      </span>
      <html:errors property="customerKey" />
    </td>                    
  </tr>
  <tr>  
    <td><strong><bean:message key="app.vehicle.make" />:</strong></td>
    <td>     
      <html:text property="make" size="35" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="model" />
    </td>
    <td><strong><bean:message key="app.vehicle.engine" />:</strong></td>
    <td>     
      <html:text property="engine" size="35" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="engine" />
    </td>
  <tr>  
    <td><strong><bean:message key="app.vehicle.model" />:</strong></td>
    <td>     
      <html:text property="model" size="35" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="model" />
    </td>
    <td><strong><bean:message key="app.vehicle.odometer" />:</strong></td>
    <td>     
      <html:text property="odometer" size="4" maxlength="6" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="odometer" />
    </td>
  </tr>
  <tr>  
    <td><strong><bean:message key="app.vehicle.color" />:</strong></td>
    <td>     
      <html:text property="color" size="35" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="color" />
    </td>
    <td><strong><bean:message key="app.vehicle.vinNumber" />:</strong></td>
    <td>     
      <html:text property="vinNumber" size="35" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="vinNumber" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vehicle.vehicleDate" />:</strong></td>
    <td colspan="2">
      <html:text property="vehicleDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>  
        <a href="javascript:show_calendar('vehicleForm.vehicleDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select vehicle date."></a>
      <% } %>
      <html:errors property="vehicleDate" />
    </td>
    <td rowspan="3">
      <logic:notEqual name="vehicleForm" property="key" value="">         
        <div style="width:<bean:message key="app.vehicle.photo.width" />; height:<bean:message key="app.vehicle.photo.height" />;  border-width: 1px; border-style: solid; border-color: black; ">
          <logic:notEqual name="vehicleForm" property="photoFileName" value="">          
            <img src="<bean:write name="vehicleForm" 
              property="documentServerURL" />/<bean:message key="app.vehicle.id" />/<bean:write name="vehicleForm" 
            property="key" />/<bean:write name="vehicleForm" 
            property="photoFileName" />" width="<bean:message key="app.vehicle.photo.width" />" height="<bean:message key="app.vehicle.photo.height" />" >
          </logic:notEqual>
          <logic:equal name="vehicleForm" property="photoFileName" value="">
            <img src="images/NoImage.jpg" width="<bean:message key="app.vehicle.photo.width" />" height="<bean:message key="app.vehicle.photo.height" />" >
          </logic:equal>
        </div>
      </logic:notEqual>
    </td>              
  </tr>
  <tr>
    <td><strong><bean:message key="app.vehicle.noteLine1" />:</strong></td>
    <td colspan="2">
      <html:text property="noteLine1" size="60" maxlength="60" /> 
      <html:errors property="noteLine1" />
    </td>          
  </tr>  
  <tr>
    <td><strong><bean:message key="app.vehicle.activeId" />:</strong></td>
    <td>
      <html:radio property="activeId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="activeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="activeId" />                   
    </td>   
  </tr>   
  <tr>
    <td><strong><bean:message key="app.vehicle.createUser" />:</strong></td>
    <td><bean:write name="vehicleForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.vehicle.createStamp" />:</strong></td>
    <td><bean:write name="vehicleForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.vehicle.updateUser" />:</strong></td>
    <td><bean:write name="vehicleForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.vehicle.updateStamp" />:</strong></td>
    <td><bean:write name="vehicleForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="8" align="right">
      <input type="hidden" name="clientNameFilter" value="<bean:write name="vehicleForm" property="clientNameFilter" />" >
      <input type="hidden" name="yearFilter" value="<bean:write name="vehicleForm" property="yearFilter" />" >
      <input type="hidden" name="makeFilter" value="<bean:write name="vehicleForm" property="makeFilter" />" >
      <input type="hidden" name="pageNo" value="<bean:write name="vehicleForm" property="currentPage" />" >        
      <html:submit value="Save Vehicle Values" disabled="<%=attDisableEdit%>" />         
     </html:form> 
    </td>
  </tr>
</table>
</div>

<script type="text/javascript" language="JavaScript"><!--
  ShowContent('mainTab', 'mainInfo');
//--></script>

<logic:notEqual name="vehicleForm" property="key" value="">  
<!-- ================================================================= -->
<!-- Graphical display of the vehicle schedule                          -->
<!-- ================================================================= -->
<div id="scheduleInfo" style="display:none;">  
  <div style="width:575px; border-width: 0px; border-style: solid; border-color: black; ">    
  <br>  
  <strong><bean:message key="app.vehicle.make" />:</strong>
  &nbsp;&nbsp;
  <bean:write name="vehicleForm" property="make" />
  <br>  
  <strong><bean:message key="app.vehicle.model" />:</strong>
  &nbsp;&nbsp;
  <bean:write name="vehicleForm" property="model" />
  <br>  
  <strong><bean:message key="app.vehicle.clientName" />:</strong>
  &nbsp;&nbsp;
  <bean:write name="vehicleForm" property="clientName" />
  
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
       String location = "";
       
       String pStartDate = "";
       String pStartTime = "";
       String pEndDate = "";
       String pEndTime = "";
       String pSubject = "";
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

      <% if (x == 0) { %>
        <br>
      <% } else { %>
        <hr>
      <% } 
        x++; %>
      &nbsp;
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
             location = "Customer Site";
             if (workOrderForm.getLocationId().equalsIgnoreCase("true")) {
               location = "In Office";
             } 
        %>
               
          <% if (!((pStartDate.equalsIgnoreCase(startDate)) &&
                (pStartTime.equalsIgnoreCase(startTime)) &&
                (pSubject.equalsIgnoreCase(subject)) &&
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
             pLocation = location;
                          
             y++;        
           }
        %>   
        </logic:notEqual>   
      </div>
    </logic:iterate> 
  </logic:notEqual>
  </div>
</div>
</logic:notEqual> 
