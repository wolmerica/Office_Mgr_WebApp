<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

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

function defaultField(what,text) {
  if (what.value == '')
     what.value = text;
}     

//--></script>

<%
  String currentColor = "ODD";
  int i = -1;   
  String attTitle = "app.customer.viewTitle";            
  String attAction = "/CustomerLoad";
  
  boolean attDisableEdit = true;
%> 

<br>
<strong>
  <a href="CustomerAccountingList.do"><bean:message key="app.menu.Invoice&Payment" /></a>&nbsp;&nbsp;
  |&nbsp;&nbsp;<a href="CustomerInvoiceReportList.do"><bean:message key="app.menu.InvoiceReport" /></a>&nbsp;&nbsp;  
  |&nbsp;&nbsp;<a href="ItemSaleDetailList.do"><bean:message key="app.menu.ItemDetail" /></a>&nbsp;&nbsp;
  |&nbsp;&nbsp;<a href="ItemSaleSummaryList.do"><bean:message key="app.menu.ItemSummary" /></a>&nbsp;&nbsp;
  |&nbsp;&nbsp;<a href="ServiceSaleDetailList.do"><bean:message key="app.menu.ServiceDetail" /></a>&nbsp;&nbsp;
  |&nbsp;&nbsp;<a href="ServiceSaleSummaryList.do"><bean:message key="app.menu.ServiceSummary" /></a>
</strong>

<br>
<br>

<div style="width:575px; border-width: 1px; border-style: solid; border-color: black; ">
  <table width="575" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
    <tr align="center" class="EVEN">
      <td colspan="8" >
        <strong>
          <bean:message key="app.customer.accountCredentials" />
        </strong>
      </td>
    </tr>
    <tr>
      <td><strong><bean:message key="app.customer.acctName" />:</strong></td>
      <td>
        <bean:write name="customerForm" property="acctName" /> 
      </td>  
      <td><strong><bean:message key="app.customer.acctNum" />:</strong></td>
      <td>
        <bean:write name="customerForm" property="acctNum" />         
      </td>  
      <td><strong><bean:message key="app.customer.acctType" />:</strong></td>      
      <td>
        <logic:equal name="customerForm" property="ledgerId" value="false"> 
          <bean:message key="app.customer.acctCash" />            
        </logic:equal>
        <logic:equal name="customerForm" property="ledgerId" value="true">           
          <bean:message key="app.customer.acctCredit" />            
        </logic:equal>
      </td>       
    </tr>
  </table>
</div>

<br>

<div style="width:350px; border-width: 1px; border-style: solid; border-color: black; ">

  <table width="350"  border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr align="center" class="EVEN">
      <td colspan="8" >
        <strong>
          <bean:message key="app.customer.accountHeader" />
        </strong>
      </td>
    </tr>
    
    <logic:equal name="customerForm" property="ledgerId" value="true">  
      <tr>
        <td></td>
        <td><strong><bean:message key="app.customer.ledgerBalanceAmount" />:</strong></td>
        <td align="center">
          &nbsp;<bean:write name="customerForm" property="ledgerBalanceDate" />    
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />  
          <bean:write name="customerForm" property="ledgerBalanceAmount" />
        </td>
      </tr>
    </logic:equal>
    <tr>
      <td></td>   
      <td><strong><bean:message key="app.customer.lastPaymentAmount" />:</strong></td>
      <td align="center">
        &nbsp;<bean:write name="customerForm" property="lastPaymentDate" />    
      </td>    
      <td align="right">
        <bean:message key="app.localeCurrency" />    
        <bean:write name="customerForm" property="lastPaymentAmount" />
      </td>
    </tr>
    <tr>
      <td></td>  
      <td><strong><bean:message key="app.customer.accountBalanceAmount" />:</strong></td>
      <td align="center">
        &nbsp;<bean:write name="customerForm" property="accountBalanceDate" />    
      </td>    
      <td align="right">
        <bean:message key="app.localeCurrency" />
        <bean:write name="customerForm" property="accountBalanceAmount" />
      </td>
    </tr>  
  </table>

</div>
<br>
<!-- ================================================================= -->
<!-- Graphical display of the customer schedule                        -->
<!-- ================================================================= -->
<div style="width:575px; border-width: 0px; border-style: solid; border-color: black; ">
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
  
       String service = "";
       String subject = "";
       String attributeToEntity = "";     
       String attributeToName = "";
       String location = "";
       
       String pStartDate = "";
       String pStartTime = "";
       String pEndDate = "";
       String pEndTime = "";
       String pSubject = "";
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

             service = workOrderForm.getSourceName();             
             subject = workOrderForm.getSubject();
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
                 <strong>Subject:</strong>&nbsp;<%=subject%>
                 <br>               
                 <% if (attributeToName.length() > 0) { %>
                   <strong><%=attributeToEntity%>:</strong>&nbsp;<%=attributeToName%>
                   <br>
                 <% } %>
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
  <br>        
</div>

