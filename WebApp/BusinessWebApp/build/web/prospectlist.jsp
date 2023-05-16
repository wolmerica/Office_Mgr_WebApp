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
 
<logic:equal name="prospectListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="prospectListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="prospectListHeadForm"
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
    <td colspan="9">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th>
      <form action="ProspectList.do" name="prospectFilter" method="post">        
        <bean:message key="app.prospect.name" />&nbsp;&nbsp;
            <input type="text" name="nameFilter" size="12" value="<bean:write name="prospectListHeadForm" property="nameFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </form>
    </th>
    <th><bean:message key="app.prospect.contactName" /></th>
    <th><bean:message key="app.prospect.phoneNum" /></th>
    <th><bean:message key="app.prospect.lineOfBusiness" /></th>
    <th><bean:message key="app.prospect.city" /></th>
    <th><bean:message key="app.prospect.state" /></th>
    <th><bean:message key="app.prospect.lastServiceDate" /></th>
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>
        <button type="button" onClick="window.location='ProspectEntry.do' "><bean:message key="app.prospect.addTitle" /></button>
      <% } %>      
    </th>   
  </tr>
  <tr>
    <td colspan="9">
      <hr>
    </td>
  </tr>
<logic:notEqual name="prospectListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the prospects -->       
  <logic:iterate id="prospectForm"
                 name="prospectListHeadForm"
                 property="prospectForm"
                 scope="session"
                 type="com.wolmerica.prospect.ProspectForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="prospectListHeadForm"
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
            <bean:write name="prospectForm" property="name" />
	  </td>
	  <td>
            <bean:write name="prospectForm" property="contactName" />
	  </td>
	  <td>
            <bean:write name="prospectForm" property="phoneNum" />
	  </td>
	  <td>
            <bean:write name="prospectForm" property="lineOfBusiness" />
	  </td>
	  <td>
            <bean:write name="prospectForm" property="city" />
	  </td>
	  <td>
            <bean:write name="prospectForm" property="state" />
	  </td>
	  <td>
            <logic:notEqual name="prospectForm" property="lastServiceDate" value="12/31/1990">              
              <bean:write name="prospectForm" property="lastServiceDate" />
            </logic:notEqual>
	  </td>
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
              <a href="ProspectGet.do?key=<bean:write name="prospectForm"
                property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="ProspectGet.do?key=<bean:write name="prospectForm"
                property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>  	       
	  </td>
	  <td align="right">
            <logic:equal name="prospectForm" property="allowDeleteId" value="true">          
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>	  
                <a href="ProspectDelete.do?key=<bean:write name="prospectForm"
                  property="key" />"  onclick="return confirm('<bean:message key="app.prospect.delete.message" />')" ><bean:message key="app.delete" /></a>
              <% } %>
            </logic:equal>
	  </td>
	</tr>
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="9">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='ProspectList.do?nameFilter=<bean:write name="prospectListHeadForm" 
        property="nameFilter" />&pageNo=<bean:write name="prospectListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>        
    </td>
    <td colspan="5">
      <logic:notEqual name="prospectListHeadForm" property="lastRecord" value="0">         
        <button type="button" onClick="popup = putinpopup('ProspectExport.do'); return false" target="_PDF"><bean:message key="app.export" /></button>
      </logic:notEqual>
      &nbsp;<strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="prospectListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="prospectListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="prospectListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='ProspectList.do?nameFilter=<bean:write name="prospectListHeadForm" 
        property="nameFilter" />&pageNo=<bean:write name="prospectListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>        
    </td>  
  </tr>
</table>