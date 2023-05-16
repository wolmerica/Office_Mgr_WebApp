<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
 
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
%>   
 
<logic:equal name="resourceListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="resourceListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="resourceListHeadForm"
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
    <th><bean:message key="app.resource.resourceName" /></th>
    <th><bean:message key="app.resource.description" /></th>
    <th><bean:message key="app.resource.createStamp" /></th>        
    <th><bean:message key="app.resource.updateStamp" /></th>     
    <th colspan="2" align="right">
    <% if (permAddId.equalsIgnoreCase("true")) { %>        
      <button type="button" onClick="window.location='ResourceEntry.do' "><bean:message key="app.resource.addTitle" /></button>
    <% } %>
    </th>      
  </tr>    
  <tr>
        <td colspan="8">
	  <hr>
	</td>
      <tr>  
<logic:notEqual name="resourceListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the resources -->       
  <logic:iterate id="resourceForm"
                 name="resourceListHeadForm"
                 property="resourceForm"
                 scope="session"
                 type="com.wolmerica.resource.ResourceForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="resourceListHeadForm"
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
            <bean:write name="resourceForm" property="resourceName" />
	  </td>
	  <td>
            <bean:write name="resourceForm" property="description" />
	  </td>
	  <td>
            <bean:write name="resourceForm" property="createStamp" />
	  </td>
	  <td>
            <bean:write name="resourceForm" property="updateStamp" />
	  </td>
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>	  
              <a href="ResourceGet.do?key=<bean:write name="resourceForm"
                property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="ResourceGet.do?key=<bean:write name="resourceForm"
                property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>
	  </td>
	  <td align="right">
            <logic:equal name="resourceForm" property="allowDeleteId" value="true">   
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>              
                <a href="ResourceDelete.do?key=<bean:write name="resourceForm"
                  property="key" />"  onclick="return confirm('<bean:message key="app.resource.delete.message" />')" ><bean:message key="app.delete" /></a>
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='ResourceList.do?resourceNameFilter=<bean:write name="resourceListHeadForm" 
        property="resourceNameFilter" />&pageNo=<bean:write name="resourceListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="3" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="resourceListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="resourceListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="resourceListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='ResourceList.do?resourceNameFilter=<bean:write name="resourceListHeadForm" 
        property="resourceNameFilter" />&pageNo=<bean:write name="resourceListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>