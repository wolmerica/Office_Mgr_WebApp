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
%>   
 
<logic:equal name="employeeListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="employeeListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="employeeListHeadForm"
               property="permissionListForm"
               scope="session"
               offset="<%=permOffset.toString()%>"
               length="1"
               type="com.wolmerica.permission.PermissionListForm">
  <% 
    permAddId = permissionListForm.getAddId();     
    if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("false") == 0)
      permAddId = "false";         
  %>
</logic:iterate> 

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th>
      <form action="EmployeeList.do" name="employeeFilter" method="post">        
        <bean:message key="app.employee.firstName" />&nbsp;&nbsp;
            <input type="text" name="employeeNameFilter" size="12" value="<bean:write name="employeeListHeadForm" property="employeeNameFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </form>
    </th>
    <th><bean:message key="app.employee.lastName" /></th>    
    <th><bean:message key="app.employee.userName" /></th>        
    <th><bean:message key="app.employee.phone" /></th>
    <th><bean:message key="app.employee.email" /></th>
    <th><bean:message key="app.employee.adminId" /></th>    
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>     
        <button type="button" onClick="window.location='EmployeeEntry.do' "><bean:message key="app.employee.addTitle" /></button>
      <% } %>        
    </th>
  </tr>     
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<logic:notEqual name="employeeListHeadForm" property="lastRecord" value="0">     
  <logic:iterate id="employeeListForm"
                 name="employeeListHeadForm"
                 property="employeeListForm"
                 scope="session"
                 type="com.wolmerica.employee.EmployeeListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="employeeListHeadForm"
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
            if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("false") == 0)
              permDeleteId = "false";            
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
            <bean:write name="employeeListForm" property="firstName" />
	  </td>
	  <td>
            <bean:write name="employeeListForm" property="lastName" />
	  </td>
	  <td>
            <bean:write name="employeeListForm" property="userName" />
	  </td>
	  <td>
            <bean:write name="employeeListForm" property="phone" />
	  </td>
	  <td>
            <bean:write name="employeeListForm" property="email" />
	  </td>
	  <td align="center">
            <html:checkbox name="employeeListForm" property="adminId" disabled="true" />
          </td>          
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
              <a href="EmployeeGet.do?key=<bean:write name="employeeListForm"
                property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="EmployeeGet.do?key=<bean:write name="employeeListForm"
                property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>            
	  </td>
	  <td align="right">
            <logic:equal name="employeeListForm" property="allowDeleteId" value="true">          
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>		  
                <a href="EmployeeDelete.do?key=<bean:write name="employeeListForm"
                  property="key" />"  onclick="return confirm('<bean:message key="app.employee.delete.message" />')" ><bean:message key="app.delete" /></a>              	    
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
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='EmployeeList.do?employeeNameFilter=<bean:write name="employeeListHeadForm" 
        property="employeeNameFilter" />&pageNo=<bean:write name="employeeListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="4">
      <logic:notEqual name="employeeListHeadForm" property="lastRecord" value="0">         
        <button type="button" onClick="popup = putinpopup('EmployeeExport.do'); return false" target="_PDF"><bean:message key="app.export" /></button>
      </logic:notEqual>
      &nbsp;<strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="employeeListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="employeeListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="employeeListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='EmployeeList.do?employeeNameFilter=<bean:write name="employeeListHeadForm" 
        property="employeeNameFilter" />&pageNo=<bean:write name="employeeListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>
