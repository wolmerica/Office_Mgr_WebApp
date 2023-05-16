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
  if (request.getParameter("petNameFilter") != null)
    if (request.getParameter("petNameFilter") != "")             
      itemFilterURL = itemFilterURL + "&petNameFilter=" + request.getParameter("petNameFilter");
  if (request.getParameter("pageNo") != null)
    itemFilterURL = itemFilterURL + "&pageNo=" + request.getParameter("pageNo");    
%>   
 
<logic:equal name="petListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="petListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="petListHeadForm"
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
    <td colspan="11">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <form action="PetList.do" name="petFilter" method="post">
      <th>
        <bean:message key="app.pet.clientName" />
        <input type="text" name="clientNameFilter" size="8" maxlength="20" value="<bean:write name="petListHeadForm" property="clientNameFilter" />" >
      </th>        
      <th>
        <bean:message key="app.pet.petName" />&nbsp;&nbsp;
        <input type="text" name="petNameFilter" size="8" maxlength="20" value="<bean:write name="petListHeadForm" property="petNameFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />">        
      </th>
    </form>      
    <th><bean:message key="app.pet.speciesName" /></th>
    <th><bean:message key="app.pet.breedName" /></th>
    <th><bean:message key="app.pet.petSexId" /></th>
    <th><bean:message key="app.pet.age" /></th>
    <th><bean:message key="app.pet.lastCheckDate" /></th>    
    <th><bean:message key="app.pet.lastServiceDate" /></th>
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>        
        <button type="button" onClick="window.location='PetEntry.do' "><bean:message key="app.pet.addTitle" /></button>      
      <% } %>
    </th>      
  </tr>    
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr> 
<logic:notEqual name="petListHeadForm" property="lastRecord" value="0"> 

<!-- Iterate over all the pets -->  
  <logic:iterate id="petListForm"
                 name="petListHeadForm"
                 property="petListForm"
                 scope="session"
                 type="com.wolmerica.pet.PetListForm">                 
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="petListHeadForm"
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
            <bean:write name="petListForm" property="clientName" />           
	  </td>            
	  <td>
            <logic:equal name="petListForm" property="activeId" value="false">
              <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerCancel" />">
            </logic:equal>              
            <bean:write name="petListForm" property="petName" />
	  </td>
	  <td>
            <bean:write name="petListForm" property="speciesName" />
	  </td>
	  <td>
            <bean:write name="petListForm" property="breedName" />
	  </td>
	  <td align="center">
            <html:checkbox name="petListForm" property="petSexId" disabled="true" />
          </td>
	  <td align="center">
            <bean:write name="petListForm" property="petAge" />
          </td>
	  <td>
            <bean:write name="petListForm" property="lastCheckDate" />
	  </td>          
	  <td>
            <logic:notEqual name="petListForm" property="lastServiceDate" value="12/31/1990">              
              <bean:write name="petListForm" property="lastServiceDate" />
            </logic:notEqual>
	  </td>	            
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="PetGet.do?key=<bean:write name="petListForm" 
              property="key" />&sourceTypeKey=<bean:write name="petListHeadForm" 
              property="sourceTypeKey" /><%=itemFilterURL%> "><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="PetGet.do?key=<bean:write name="petListForm" 
              property="key" />&sourceTypeKey=<bean:write name="petListHeadForm" 
              property="sourceTypeKey" /><%=itemFilterURL%> "><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>              
	  </td>
	  <td align="right">
            <logic:equal name="petListForm" property="allowDeleteId" value="true">
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>
                <a href="PetDelete.do?key=<bean:write name="petListForm"
                  property="key" /><%=itemFilterURL%>" onclick="return confirm('<bean:message key="app.pet.delete.message" />')" ><bean:message key="app.delete" /></a>
              <% } %>
            </logic:equal>
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="11">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='PetList.do?pageNo=<bean:write name="petListHeadForm" 
        property="previousPage" />&petNameFilter=<bean:write name="petListHeadForm" 
        property="petNameFilter" />&clientNameFilter=<bean:write name="petListHeadForm" 
        property="clientNameFilter" />' "><bean:message key="app.previous" /></button>              
    </td>
    <td colspan="6">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="petListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="petListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="petListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='PetList.do?pageNo=<bean:write name="petListHeadForm" 
        property="nextPage" />&petNameFilter=<bean:write name="petListHeadForm" 
        property="petNameFilter" />&clientNameFilter=<bean:write name="petListHeadForm" 
        property="clientNameFilter" />' "><bean:message key="app.next" /></button>              
    </td>  
  </tr>
</table>