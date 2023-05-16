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
  int startHour = 0;
  int startMinute = 0;
  String timeOfDay = "am";
  String prevWOKey = "";
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
%>   
 
<logic:equal name="workOrderListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="workOrderListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>    
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
 
<!-- Work order list resource, customer, and date range -->
    <td colspan = "10">
      <html:form action="WorkOrderList.do">
        <html:hidden property="customerKey" />
        <html:hidden property="clientName" />
        <strong><bean:message key="app.workorder.customer" />:</strong>
        <span class="ac_holder">
        <input id="clientNameAC" size="40" maxlength="40" value="<bean:write name="workOrderListHeadForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.workOrderListHeadForm.customerKey.value=obj.id; document.workOrderListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>
        &nbsp;&nbsp;&nbsp;&nbsp;       
        <html:hidden property="resourceKey" />
        <html:hidden property="resourceName" />
        <strong><bean:message key="app.workorder.resource" />:</strong>
        <span class="ac_holder">
        <input id="resourceNameAC" size="15" maxlength="40" value="<bean:write name="workOrderListHeadForm" property="resourceName" />" onFocus="javascript:
        var options = {
		script:'ResourceLookUp.do?json=true&',
		varname:'resourceNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.workOrderListHeadForm.resourceKey.value=obj.id; document.workOrderListHeadForm.resourceName.value=obj.value; }
		};
		var json=new AutoComplete('resourceNameAC',options);return true;" value="" />
        </span>
    </td>
  </tr>
  <tr>
    <td colspan = "10">  
        <strong><bean:message key="app.workorder.type" />:</strong>
        <select name="sourceTypeKey">
           <option value="0" <logic:equal name="workOrderListHeadForm" property="sourceTypeKey" value="0">selected</logic:equal> >
           <bean:message key="app.workorder.all" />
           <option value="3" <logic:equal name="workOrderListHeadForm" property="sourceTypeKey" value="3">selected</logic:equal> >
           <bean:message key="app.workorder.item" /> 
           <option value="6" <logic:equal name="workOrderListHeadForm" property="sourceTypeKey" value="6">selected</logic:equal> >
           <bean:message key="app.workorder.service" />
        </select>    
        &nbsp;&nbsp;&nbsp;&nbsp;        
        <strong><bean:message key="app.workorder.dateRange" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="workOrderListHeadForm" property="fromDate" />" size="10" >
        <a href="javascript:show_calendar('workOrderListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.workorder.toDate" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="workOrderListHeadForm" property="toDate" />" size="10" >
        <a href="javascript:show_calendar('workOrderListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </html:form>
    </td>
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.workorder.startDate" /></th>  
    <th><bean:message key="app.workorder.startTime" /></th>        
    <th><bean:message key="app.workorder.customer" /></th>
    <th><bean:message key="app.workorder.attributeTo" /></th>
    <th><bean:message key="app.workorder.type" /></th> 
    <th><bean:message key="app.workorder.name" /></th>    
    <th>
      <bean:message key="app.workorder.size" />/
      <bean:message key="app.workorder.sizeUnit" />
    </th>
    <th><bean:message key="app.workorder.quantity" /></th>    
    <th><bean:message key="app.workorder.resource" /></th>
  </tr>     
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
                  
<logic:notEqual name="workOrderListHeadForm" property="recordCount" value="0"> 
  <logic:iterate id="workOrderForm"
                 name="workOrderListHeadForm"
                 property="workOrderForm"
                 scope="session"
                 type="com.wolmerica.workorder.WorkOrderForm">                     
    <%
      if ( i % 2 == 0)
        currentColor = "ODD";
      else
        currentColor = "EVEN";
      i++;
    %>          
    <tr align="left" class="<%=currentColor %>">
      <% if (workOrderForm.getKey().compareTo(prevWOKey) != 0) { %>
        <td>
          <bean:write name="workOrderForm" property="startDate" />
        </td>
	<td>
          <% 
            startHour = new Byte(workOrderForm.getStartHour()).intValue();
            startMinute = new Byte(workOrderForm.getStartMinute()).intValue();
            timeOfDay = "am";               
            if (startHour > 11)  
              timeOfDay = "pm";            
            if (startHour > 12)
              startHour = startHour - 12;
            if (startMinute == 0) { %>
              <%=startHour%>&nbsp;<%=timeOfDay%>
            <% } else { %>
              <%=startHour%>:<bean:write name="workOrderForm" property="startMinute" />&nbsp;<%=timeOfDay%>
            <% } %>              
	</td>
	<td>
          <bean:write name="workOrderForm" property="clientName" />
	</td>
	<td>
          <bean:write name="workOrderForm" property="attributeToName" />
	</td>
        <td>
          <logic:equal name="workOrderForm" property="sourceTypeKey" value="3">
            <bean:message key="app.workorder.item" />              
          </logic:equal>
          <logic:equal name="workOrderForm" property="sourceTypeKey" value="6">
            <bean:message key="app.workorder.service" />
          </logic:equal>          
        </td>	
	<td>
          <bean:write name="workOrderForm" property="sourceName" />
	</td>
        <td>
          <bean:write name="workOrderForm" property="size" />
          <bean:write name="workOrderForm" property="sizeUnit" />
        </td>            
        <td align="right"> 
          <bean:write name="workOrderForm" property="orderQty" />
	</td>	        
	<td>
          <bean:write name="workOrderForm" property="resourceName" />
	</td>	
        <td>
          <logic:equal name="workOrderForm" property="sourceTypeKey" value="6">            
            <logic:equal name="workOrderForm" property="activeId" value="true">
              <img src="./images/resource.gif" height="16" width="16" border="0" onclick="popup = putinpopup('ResourceAllocation.do?key=<bean:write name="workOrderForm" 
                property="key" />&serviceName=<bean:write name="workOrderForm" 
                property="sourceName" />&startDate=<bean:write name="workOrderForm" 
                property="startDate" />&startHour=<bean:write name="workOrderForm" 
                property="startHour" />&startMinute=<bean:write name="workOrderForm" 
                property="startMinute" />&endDate=<bean:write name="workOrderForm" 
                property="endDate" />&endHour=<bean:write name="workOrderForm" 
                property="endHour" />&endMinute=<bean:write name="workOrderForm" 
                property="endMinute" />'); return false" title="<bean:message key="app.workorder.resource" /> ">
            </logic:equal>
          </logic:equal>            
        </td>	  
      <% } else { %>
        <td colspan="8">
        </td>
        <td>
          <bean:write name="workOrderForm" property="resourceName" />
        </td>	
        <td>
        </td>
      <% } %>
    </tr>
    <% prevWOKey = workOrderForm.getKey(); %>        
  </logic:iterate> 
</logic:notEqual>

  <tr>
    <td colspan="10">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="3">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='WorkOrderList.do?sourceTypeKey=<bean:write name="workOrderListHeadForm" 
        property="sourceTypeKey" />&resourceKey=<bean:write name="workOrderListHeadForm" 
        property="resourceKey" />&customerKey=<bean:write name="workOrderListHeadForm" 
        property="customerKey" />&fromDate=<bean:write name="workOrderListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="workOrderListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="workOrderListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="5">
      &nbsp;<strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="workOrderListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="workOrderListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="workOrderListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='WorkOrderList.do?sourceTypeKey=<bean:write name="workOrderListHeadForm" 
        property="sourceTypeKey" />&resourceKey=<bean:write name="workOrderListHeadForm" 
        property="resourceKey" />&customerKey=<bean:write name="workOrderListHeadForm" 
        property="customerKey" />&fromDate=<bean:write name="workOrderListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="workOrderListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="workOrderListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>    
    </td>  
  </tr> 
</table>
