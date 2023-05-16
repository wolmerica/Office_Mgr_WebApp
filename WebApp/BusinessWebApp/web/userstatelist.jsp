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
 
<logic:equal name="userStateListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="userStateListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr align="left">
    <th><bean:message key="app.userstate.userName" /></th>
    <th><bean:message key="app.userstate.tokenName" /></th>    
    <th><bean:message key="app.userstate.featureName" /></th>    
    <th><bean:message key="app.userstate.featureType" /></th>
    <th><bean:message key="app.userstate.featureKey" /></th>
    <th><bean:message key="app.userstate.createStamp" /></th>
    <th><bean:message key="app.userstate.updateStamp" /></th>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<logic:notEqual name="userStateListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the user state records -->       
  <logic:iterate id="userStateListForm"
                 name="userStateListHeadForm"
                 property="userStateListForm"
                 scope="session"
                 type="com.wolmerica.userstate.UserStateListForm">

<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="userStateListHeadForm"
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
            <bean:write name="userStateListForm" property="userName" />
	  </td>
	  <td>
            <bean:write name="userStateListForm" property="tokenName" />
	  </td>          
	  <td>
            <bean:write name="userStateListForm" property="featureName" />
	  </td>	  
	  <td>
            <bean:write name="userStateListForm" property="featureType" />
	  </td>
	  <td>
            <bean:write name="userStateListForm" property="featureKey" />
	  </td>
	  <td>
            <bean:write name="userStateListForm" property="createStamp" />
	  </td>
	  <td>
            <bean:write name="userStateListForm" property="updateStamp" />
	  </td>
	  <td align="right">
            <% if (permDeleteId.equalsIgnoreCase("true")) { %>
             <a href="UserStateReset.do?key=<bean:write name="userStateListForm"
               property="key" />"  onclick="return confirm('<bean:message key="app.userstate.reset.message" />')" ><bean:message key="app.reset" /></a>
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
    <td colspan="3">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='UserStateList.do?pageNo=<bean:write name="userStateListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>        
    </td>
    <td colspan="3">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="userStateListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="userStateListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="userStateListHeadForm" property="recordCount" />      
    </td>
    <td>
      <button type="button" <%=attDisableNextButton%> onClick="window.location='UserStateList.do?pageNo=<bean:write name="userStateListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>        
    </td>  
  </tr>
</table>