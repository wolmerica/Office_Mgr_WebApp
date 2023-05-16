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
 
<logic:equal name="taxMarkUpListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="taxMarkUpListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="taxMarkUpListHeadForm"
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
      <tr align="left">
        <th><bean:message key="app.taxmarkup.name" /></th>      
        <th><bean:message key="app.taxmarkup.description" /></th>
        <th><bean:message key="app.taxmarkup.value" /></th>
        <th><bean:message key="app.taxmarkup.updateUser" /></th>
        <th><bean:message key="app.taxmarkup.updateStamp" /></th>
        <th>&nbsp;</th>
	<th>&nbsp;</th>
        <th>&nbsp;</th>
      </tr>     
      <tr>
        <td colspan="7">
	  <hr>
	</td>
      </tr>
    
<logic:notEqual name="taxMarkUpListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the tax mark-up -->       
  <logic:iterate id="taxMarkUpForm"
                 name="taxMarkUpListHeadForm"
                 property="taxMarkUpForm"
                 scope="session"
                 type="com.wolmerica.taxmarkup.TaxMarkUpForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="taxMarkUpListHeadForm"
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
            <bean:write name="taxMarkUpForm" property="name" />
	  </td>
	  <td>
            <bean:write name="taxMarkUpForm" property="description" />
	  </td>
	  <td>
            <logic:equal name="taxMarkUpForm" property="percentageId" value="false">
              <bean:message key="app.localeCurrency" />         
            </logic:equal>
            <bean:write name="taxMarkUpForm" property="value" />
            <logic:equal name="taxMarkUpForm" property="percentageId" value="true">            
              <bean:message key="app.localePercent" /> 
            </logic:equal>
	  </td>
	  <td>
            <bean:write name="taxMarkUpForm" property="updateUser" />
	  </td>
	  <td>
            <bean:write name="taxMarkUpForm" property="updateStamp" />
	  </td>
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>	  
              <a href="TaxMarkUpGet.do?key=<bean:write name="taxMarkUpForm"
                property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="TaxMarkUpGet.do?key=<bean:write name="taxMarkUpForm"
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
    <td colspan="7">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='TaxMarkUpList.do?taxMarkUpNameFilter=<bean:write name="taxMarkUpListHeadForm" 
        property="taxMarkUpNameFilter" />&pageNo=<bean:write name="taxMarkUpListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>                
    </td>
    <td colspan="3">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="taxMarkUpListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="taxMarkUpListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="taxMarkUpListHeadForm" property="recordCount" />      
    </td>
    <td colspan="3" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='TaxMarkUpList.do?taxMarkUpNameFilter=<bean:write name="taxMarkUpListHeadForm" 
        property="taxMarkUpNameFilter" />&pageNo=<bean:write name="taxMarkUpListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>                
    </td>  
  </tr>
</table>

