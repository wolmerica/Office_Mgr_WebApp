<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<style type="text/css">
.collapsible {
          display: none; /* Only important part */
          border: dashed 1px silver;
          padding: 10px;
.workorder {
          display: block;
          border: dashed 1px silver;
          padding: 10px;
}
</style>


<script type="text/javascript">
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

function ToggleResource(chkBox, schKey, woKey, resKey )
{
  var params = '?key=' + schKey + '&woKey=' + woKey + '&resourceKey=' + resKey;
  var actionURL = 'ResourceInstance';
  if (chkBox.checked) {  
    actionURL = actionURL + 'Add.do' + params;
  }
  else {
    actionURL = actionURL + 'Delete.do' + params;    
  }
  /*
    alert(actionURL);
  */
  retrieveURL(actionURL); 
}

var req;
var which;

function retrieveURL(url) {

  if (url != "") {
    if (window.XMLHttpRequest) { // Non-IE browsers

      req = new XMLHttpRequest();
      req.onreadystatechange = processStateChange;
      try {
        req.open("GET", url, true);
      } catch (e) {
        alert(e);
      }
      req.send(null);
    } else if (window.ActiveXObject) { // IE
      req = new ActiveXObject("Microsoft.XMLHTTP");

      if (req) {
        req.onreadystatechange = processStateChange;
        req.open("GET", url, true);
        req.send();
      }
    }
  }
}

function processStateChange() {
  if (req.readyState == 4) { // Complete
    if (req.status == 200) { // OK response
      // do nothing...
    } else {
      alert("Problem: " + req.statusText);
    }
  }
}

</script>

<h3>
  Resource Allocation
</h3>

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

   eventTOD = "AM"; 
   if (request.getParameter("startDate") != null)                      
     startDate = request.getParameter("startDate").toString();
   if (request.getParameter("startHour") != null)                      
     startHour = new Integer(request.getParameter("startHour").toString());
   if (request.getParameter("startMinute") != null)                      
     startMinute = new Integer(request.getParameter("startMinute").toString());
   if (startHour > 11)
     eventTOD = "PM";
   if (startHour > 12)
     startHour = startHour - 12;
   startTime = startHour + ":";
   if (startMinute < 10)
     startTime = startTime + "0";
   startTime = startTime + startMinute + eventTOD;
   eventTOD = "AM";                 
   if (request.getParameter("endDate") != null)                      
     endDate = request.getParameter("endDate").toString();
   if (request.getParameter("endHour") != null)                      
     endHour = new Integer(request.getParameter("endHour").toString());
   if (request.getParameter("endMinute") != null)                      
     endMinute = new Integer(request.getParameter("endMinute").toString());
   if (endHour > 11)
     eventTOD = "PM";
   if (endHour > 12)
     endHour = endHour - 12;
   endTime = endHour + ":";
   if (endMinute < 10)
     endTime = endTime + "0";
   endTime = endTime + endMinute + eventTOD;
%>
<font size="-1">
  <strong>Start Date:</strong><%=startDate%>
  &nbsp;<strong>Time:</strong>&nbsp;<%=startTime%>
  <br>
  <strong>End Date:</strong><%=endDate%>
  &nbsp;<strong>Time:</strong>&nbsp;<%=endTime%>
  <br>
  <strong>Service:</strong>&nbsp;<%=request.getParameter("serviceName").toString()%>
  <hr>
</font>
<logic:equal name="resourceInstanceListHeadForm" property="lastRecord" value="0">
  <tr>
    <td></td>
    <td colspan="48"><bean:message key="app.schedule.noResults" /></td>
  </tr>
</logic:equal>
  
<logic:notEqual name="resourceInstanceListHeadForm" property="lastRecord" value="0"> 
  <%
     String service = "";
     String subject = "";
     String clientName = "";
     String attributeToEntity = "";     
     String attributeToName = "";
     String location = "";
     int x = 0;
     int eventCnt = 0;
     int y = 0;     
  %>
    
  <jsp:useBean id="workOrderForm" scope="session" 
    class="com.wolmerica.workorder.WorkOrderForm" />
        
  <logic:iterate id="resourceInstanceForm"
                 name="resourceInstanceListHeadForm"
                 property="resourceInstanceForm"
                 scope="session"
                 type="com.wolmerica.resourceinstance.ResourceInstanceForm">  

    <a href="javascript:;" onclick="ShowHideLayer('<bean:write name="resourceInstanceForm" property="resourceKey" />');"><img src="images/expand.gif" alt="Expand Me" name="btn<bean:write name="resourceInstanceForm" property="resourceKey" />" width="9" height="9" border="0" id="btn<bean:write name="resourceInstanceForm" property="resourceKey" />" /></a>
    <input type="checkbox" <logic:notEqual name="resourceInstanceForm" property="scheduleKey" value="0">checked</logic:notEqual> name="<bean:write name="resourceInstanceForm" property="resourceKey" />" onclick="ToggleResource(this,<bean:write name="resourceInstanceForm" property="scheduleKey" />,<bean:write name="resourceInstanceForm" property="workOrderKey" />,<bean:write name="resourceInstanceForm" property="resourceKey" />);">&nbsp;[<bean:write name="resourceInstanceForm" property="eventCount" />]&nbsp;<bean:write name="resourceInstanceForm" property="resourceName" /><br>
    <div id="box<bean:write name="resourceInstanceForm" property="resourceKey" />" class="collapsible">
      
      <logic:notEqual name="resourceInstanceForm" property="eventCount" value="0"> 
        <% 
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
          <font size="-1">          
            <strong>Start Date:</strong><%=startDate%>
            &nbsp;<strong>Time:</strong>&nbsp;<%=startTime%>
            <br>
            <strong>End Date:</strong><%=endDate%>
            &nbsp;<strong>Time:</strong>&nbsp;<%=endTime%>
            <br>
            <strong>Service:</strong>&nbsp;<%=service%>
            <strong>Location:</strong>&nbsp;<%=location%>          
            <strong>Subject:</strong>&nbsp;<%=subject%>
            <strong>Customer:</strong>&nbsp;<%=clientName%>        
            <strong><%=attributeToEntity%>:</strong>&nbsp;<%=attributeToName%>
          </font>
          <br>
        <%
           y++;        
           }
        %>   
      </logic:notEqual>   

    </div>
  </logic:iterate> 
</logic:notEqual>