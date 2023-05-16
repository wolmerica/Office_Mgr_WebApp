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
 
<logic:equal name="customerTypeListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="customerTypeListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="customerTypeListHeadForm"
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
    <th>
      <form action="CustomerTypeList.do" name="customerTypeFilter" method="post">        
        <bean:message key="app.customertype.name" />&nbsp;&nbsp;
            <input type="text" name="customerTypeNameFilter" size="12" value="<bean:write name="customerTypeListHeadForm" property="customerTypeNameFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </form>
    </th>
    <th><bean:message key="app.customertype.precedence" /></th>
    <th><bean:message key="app.customertype.blueBookId" /></th>     
    <th><bean:message key="app.customertype.priceSheetId" /></th> 
    <th><bean:message key="app.customertype.activeId" /></th>    
    <th><bean:message key="app.customertype.createStamp" /></th>
    <th><bean:message key="app.customertype.updateStamp" /></th>    
    <th></th>
  </tr>     
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<logic:notEqual name="customerTypeListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the customer type -->       
  <logic:iterate id="customerTypeForm"
                 name="customerTypeListHeadForm"
                 property="customerTypeForm"
                 scope="session"
                 type="com.wolmerica.customertype.CustomerTypeForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="customerTypeListHeadForm"
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
            <bean:write name="customerTypeForm" property="name" />
	  </td>
	  <td align="center">
            <bean:write name="customerTypeForm" property="precedence" />
	  </td>
	  <td align="center">
            <html:checkbox name="customerTypeForm" property="blueBookId" disabled="true" />
          </td>            
	  <td align="center">
            <html:checkbox name="customerTypeForm" property="priceSheetId" disabled="true" />
          </td>            
	  <td align="center">
            <html:checkbox name="customerTypeForm" property="activeId" disabled="true" />
          </td>          
	  <td>
            <bean:write name="customerTypeForm" property="createStamp" />
	  </td>          
	  <td>
            <bean:write name="customerTypeForm" property="updateStamp" />
	  </td>                 
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>	  
              <a href="CustomerTypeGet.do?key=<bean:write name="customerTypeForm"
                property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="CustomerTypeGet.do?key=<bean:write name="customerTypeForm"
                property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %> 	      
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='CustomerTypeList.do?customerTypeNameFilter=<bean:write name="customerTypeListHeadForm" 
        property="customerTypeNameFilter" />&pageNo=<bean:write name="customerTypeListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="4">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="customerTypeListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="customerTypeListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="customerTypeListHeadForm" property="recordCount" />      
    </td>
    <td colspan ="3" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='CustomerTypeList.do?customerTypeNameFilter=<bean:write name="customerTypeListHeadForm" 
        property="customerTypeNameFilter" />&pageNo=<bean:write name="customerTypeListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>