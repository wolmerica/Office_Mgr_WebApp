<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
   
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
  
  String itemFilterURL = "";
  if (request.getParameter("clientNameFilter") != null) 
    if (request.getParameter("clientNameFilter") != "")
      itemFilterURL = itemFilterURL + "&clientNameFilter=" + request.getParameter("clientNameFilter");    
  if (request.getParameter("yearFilter") != null)
    if (request.getParameter("yearFilter") != "")             
      itemFilterURL = itemFilterURL + "&yearFilter=" + request.getParameter("yearFilter");
  if (request.getParameter("makeFilter") != null)
    if (request.getParameter("makeFilter") != "")             
      itemFilterURL = itemFilterURL + "&makeFilter=" + request.getParameter("makeFilter");      
  if (request.getParameter("pageNo") != null)
    itemFilterURL = itemFilterURL + "&pageNo=" + request.getParameter("pageNo");  
%>   
 
<logic:equal name="vehicleListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="vehicleListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="vehicleListHeadForm"
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
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <form action="VehicleList.do" name="vehicleFilter" method="post">       
      <th>
        <bean:message key="app.vehicle.clientName" />
        <input type="text" name="clientNameFilter" size="3" maxlength="10" value="<bean:write name="vehicleListHeadForm" property="clientNameFilter" />" >
      </th>
      <th align="right">
        <bean:message key="app.vehicle.year" />
        <input type="text" name="yearFilter" size="1" maxlength="4" value="<bean:write name="vehicleListHeadForm" property="yearFilter" />" > 
      </th>
      <th>
        <bean:message key="app.vehicle.make" />&nbsp;&nbsp;
        <input type="text" name="makeFilter" size="3" maxlength="10" value="<bean:write name="vehicleListHeadForm" property="makeFilter" />" > 
      </th>                        
      <th><bean:message key="app.vehicle.model" />
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </th>      
    </form>         
    <th align="right"><bean:message key="app.vehicle.odometer" /></th>   
    <th><bean:message key="app.vehicle.lastServiceDate" /></th>
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>        
        <button type="button" onClick="window.location='VehicleEntry.do' "><bean:message key="app.vehicle.addTitle" /></button>
     <% } %>
    </th>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>  
<logic:notEqual name="vehicleListHeadForm" property="lastRecord" value="0"> 

<!-- Iterate over all the vehicles -->  
  <logic:iterate id="vehicleListForm"
                 name="vehicleListHeadForm"
                 property="vehicleListForm"
                 scope="session"
                 type="com.wolmerica.vehicle.VehicleListForm">                 
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="vehicleListHeadForm"
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
            <bean:write name="vehicleListForm" property="clientName" />
	  </td>            
	  <td align="right">
            <bean:write name="vehicleListForm" property="year" />
	  </td>
	  <td>
            <bean:write name="vehicleListForm" property="make" />
	  </td>	            
	  <td>
            <logic:equal name="vehicleListForm" property="activeId" value="false">
              <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerCancel" />">
            </logic:equal>              
            <bean:write name="vehicleListForm" property="model" />
	  </td>	  
	  <td align="right">
            <bean:write name="vehicleListForm" property="odometer" />
	  </td>
	  <td>
            <logic:notEqual name="vehicleListForm" property="lastServiceDate" value="12/31/1990">              
              <bean:write name="vehicleListForm" property="lastServiceDate" />
            </logic:notEqual>
	  </td>	  
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="VehicleGet.do?key=<bean:write name="vehicleListForm" 
              property="key" />&sourceTypeKey=<bean:write name="vehicleListHeadForm" 
              property="sourceTypeKey" /><%=itemFilterURL%> "><bean:message key="app.view" /></a>              
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="VehicleGet.do?key=<bean:write name="vehicleListForm" 
              property="key" />&sourceTypeKey=<bean:write name="vehicleListHeadForm" 
              property="sourceTypeKey" /><%=itemFilterURL%> "><bean:message key="app.edit" /></a>             
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>              
	  </td>
	  <td align="right">
            <logic:equal name="vehicleListForm" property="allowDeleteId" value="true">          
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>
               <a href="VehicleDelete.do?key=<bean:write name="vehicleListForm"
                 property="key" /><%=itemFilterURL%>" onclick="return confirm('<bean:message key="app.vehicle.delete.message" />')" ><bean:message key="app.delete" /></a>
              <% } %>
            </logic:equal>
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="8">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='VehicleList.do?clientNameFilter=<bean:write name="vehicleListHeadForm" 
        property="clientNameFilter" />&yearFilter=<bean:write name="vehicleListHeadForm" 
        property="yearFilter" />&makeFilter=<bean:write name="vehicleListHeadForm" 
        property="makeFilter" />&pageNo=<bean:write name="vehicleListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="5" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="vehicleListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="vehicleListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="vehicleListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='VehicleList.do?clientNameFilter=<bean:write name="vehicleListHeadForm" 
        property="clientNameFilter" />&yearFilter=<bean:write name="vehicleListHeadForm" 
        property="yearFilter" />&makeFilter=<bean:write name="vehicleListHeadForm" 
        property="makeFilter" />&pageNo=<bean:write name="vehicleListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>