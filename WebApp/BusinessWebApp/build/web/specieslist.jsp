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
 
<logic:equal name="speciesListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="speciesListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="speciesListHeadForm"
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
    <td colspan="10">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <form action="SpeciesList.do" name="speciesFilter" method="post">
      <th>
        <bean:message key="app.species.filter" />
        <input type="text" name="speciesNameFilter" size="15" maxlength="20" value="<bean:write name="speciesListHeadForm" property="speciesNameFilter" />" >
        &nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">        
      </th>
    </form>
  </tr>
  <tr align="left">
    <th><bean:message key="app.species.name" /></th>
    <th><bean:message key="app.species.extId" /></th>
    <th><bean:message key="app.species.updateUser" /></th>
    <th><bean:message key="app.species.updateStamp" /></th>
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>        
        <button type="button" onClick="window.location='SpeciesEntry.do' "><bean:message key="app.species.addTitle" /></button>
      <% } %>
    </th>      
  </tr>    
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr> 
<logic:notEqual name="speciesListHeadForm" property="lastRecord" value="0">

<!-- Iterate over all the speciess -->
  <logic:iterate id="speciesForm"
                 name="speciesListHeadForm"
                 property="speciesListForm"
                 scope="session"
                 type="com.wolmerica.species.SpeciesForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="speciesListHeadForm"
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
            <bean:write name="speciesForm" property="speciesName" />
	  </td>
	  <td>
            <bean:write name="speciesForm" property="speciesExtId" />
	  </td>
	  <td>
            <bean:write name="speciesForm" property="updateUser" />
	  </td>
	  <td>
            <bean:write name="speciesForm" property="updateStamp" />
	  </td>
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="SpeciesGet.do?key=<bean:write name="speciesForm"
              property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="SpeciesGet.do?key=<bean:write name="speciesForm"
              property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>              
	  </td>
	  <td align="right">
            <logic:equal name="speciesForm" property="allowDeleteId" value="true">
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>
                <a href="SpeciesDelete.do?key=<bean:write name="speciesForm"
                  property="key" />" onclick="return confirm('<bean:message key="app.species.delete.message" />')" ><bean:message key="app.delete" /></a>
              <% } %>
            </logic:equal>
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="10">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='SpeciesList.do?pageNo=<bean:write name="speciesListHeadForm"
        property="previousPage" />&speciesNameFilter=<bean:write name="speciesListHeadForm"
        property="speciesNameFilter" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="3">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="speciesListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="speciesListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="speciesListHeadForm" property="recordCount" />
    </td>
    <td>
      <button type="button" <%=attDisableNextButton%> onClick="window.location='SpeciesList.do?pageNo=<bean:write name="speciesListHeadForm"
        property="nextPage" />&speciesNameFilter=<bean:write name="speciesListHeadForm"
        property="speciesNameFilter" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>