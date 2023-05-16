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
  if (request.getParameter("makeModelFilter") != null)
    if (request.getParameter("makeModelFilter") != "")             
      itemFilterURL = itemFilterURL + "&makeModelFilter=" + request.getParameter("makeModelFilter");
  if (request.getParameter("pageNo") != null)
    itemFilterURL = itemFilterURL + "&pageNo=" + request.getParameter("pageNo");  
%>   
 
<logic:equal name="systemListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="systemListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="systemListHeadForm"
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
    <form action="SystemList.do" name="systemFilter" method="post">       
      <th>
        <bean:message key="app.system.clientName" />
        <input type="text" name="clientNameFilter" size="8" maxlength="20" value="<bean:write name="systemListHeadForm" property="clientNameFilter" />" >
      </th>
      <th>
        <bean:message key="app.system.makeModel" />&nbsp;&nbsp;
        <input type="text" name="makeModelFilter" size="8" maxlength="20" value="<bean:write name="systemListHeadForm" property="makeModelFilter" />" > 
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </th>      
    </form>                   
    <th><bean:message key="app.system.systemDate" /></th>        
    <th><bean:message key="app.system.operatingSystem" /></th>
    <th><bean:message key="app.system.lastServiceDate" /></th>
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>        
        <button type="button" onClick="window.location='SystemEntry.do' "><bean:message key="app.system.addTitle" /></button>            
     <% } %>
    </th>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>  
<logic:notEqual name="systemListHeadForm" property="lastRecord" value="0"> 

<!-- Iterate over all the systems -->  
  <logic:iterate id="systemListForm"
                 name="systemListHeadForm"
                 property="systemListForm"
                 scope="session"
                 type="com.wolmerica.system.SystemListForm">                 
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="systemListHeadForm"
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
            <bean:write name="systemListForm" property="clientName" />
	  </td>            
	  <td>
            <logic:equal name="systemListForm" property="activeId" value="false">
              <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerCancel" />">
            </logic:equal>
            <bean:write name="systemListForm" property="makeModel" />
	  </td>
	  <td>            
              <bean:write name="systemListForm" property="systemDate" />
	  </td>	  
	  <td>
            <bean:write name="systemListForm" property="operatingSystem" />
	  </td>
	  <td>
            <logic:notEqual name="systemListForm" property="lastServiceDate" value="12/31/1990">              
              <bean:write name="systemListForm" property="lastServiceDate" />
            </logic:notEqual>
	  </td>	  
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="SystemGet.do?key=<bean:write name="systemListForm" 
              property="key" />&sourceTypeKey=<bean:write name="systemListHeadForm" 
              property="sourceTypeKey" /><%=itemFilterURL%> "><bean:message key="app.view" /></a>              
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="SystemGet.do?key=<bean:write name="systemListForm" 
              property="key" />&sourceTypeKey=<bean:write name="systemListHeadForm" 
              property="sourceTypeKey" /><%=itemFilterURL%> "><bean:message key="app.edit" /></a>             
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>              
	  </td>
	  <td align="right">
            <logic:equal name="systemListForm" property="allowDeleteId" value="true">          
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>
               <a href="SystemDelete.do?key=<bean:write name="systemListForm"
                 property="key" /><%=itemFilterURL%>" onclick="return confirm('<bean:message key="app.system.delete.message" />')" ><bean:message key="app.delete" /></a>
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='SystemList.do?clientNameFilter=<bean:write name="systemListHeadForm" 
        property="clientNameFilter" />&makeModelFilter=<bean:write name="systemListHeadForm" 
        property="makeModelFilter" />&pageNo=<bean:write name="systemListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="4" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="systemListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="systemListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="systemListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='SystemList.do?clientNameFilter=<bean:write name="systemListHeadForm" 
        property="clientNameFilter" />&makeModelFilter=<bean:write name="systemListHeadForm" 
        property="makeModelFilter" />&pageNo=<bean:write name="systemListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>