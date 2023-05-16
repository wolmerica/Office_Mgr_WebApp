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
 
<logic:equal name="priceTypeListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="priceTypeListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="priceTypeListHeadForm"
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
    <th>
      <form action="PriceTypeList.do" name="priceTypeFilter" method="post">        
        <bean:message key="app.pricetype.name" />&nbsp;&nbsp;
            <input type="text" name="priceTypeNameFilter" size="5" value="<bean:write name="priceTypeListHeadForm" property="priceTypeNameFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </form>
    </th>
    <th><bean:message key="app.pricetype.precedence" /></th>
    <th><bean:message key="app.pricetype.domainId" /></th>    
    <th><bean:message key="app.pricetype.fullSizeId" /></th>
    <th><bean:message key="app.pricetype.unitCostBaseId" /></th>
    <th><bean:message key="app.pricetype.blueBookId" /></th>
    <th><bean:message key="app.pricetype.markUpRate" /></th>
    <th><bean:message key="app.pricetype.activeId" /></th>  
    <th></th> 
  </tr>     
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
<logic:notEqual name="priceTypeListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the price type -->       
  <logic:iterate id="priceTypeForm"
                 name="priceTypeListHeadForm"
                 property="priceTypeForm"
                 scope="session"
                 type="com.wolmerica.pricetype.PriceTypeForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="priceTypeListHeadForm"
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
            <bean:write name="priceTypeForm" property="name" />
	  </td>
	  <td align="center">
            <bean:write name="priceTypeForm" property="precedence" />
	  </td>
	  <td align="center">
            <html:checkbox name="priceTypeForm" property="domainId" disabled="true" />
          </td>           
	  <td align="center">
            <html:checkbox name="priceTypeForm" property="fullSizeId" disabled="true" />
          </td>                    
	  <td align="center">
            <logic:equal name="priceTypeForm" property="domainId" value="false">           
              <html:checkbox name="priceTypeForm" property="unitCostBaseId" disabled="true" />
            </logic:equal>
          </td>                    
	  <td align="center">
            <html:checkbox name="priceTypeForm" property="blueBookId" disabled="true" />
          </td>                    
	  <td>
            <bean:write name="priceTypeForm" property="markUpRate" />
	  </td>
	  <td align="center">
            <html:checkbox name="priceTypeForm" property="activeId" disabled="true" />
          </td>                    
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>	  
              <a href="PriceTypeGet.do?key=<bean:write name="priceTypeForm"
                property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="PriceTypeGet.do?key=<bean:write name="priceTypeForm"              
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
    <td colspan="10">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='PriceTypeList.do?priceTypeNameFilter=<bean:write name="priceTypeListHeadForm" 
        property="priceTypeNameFilter" />&pageNo=<bean:write name="priceTypeListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>        
    </td>
    <td colspan="5">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="priceTypeListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="priceTypeListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="priceTypeListHeadForm" property="recordCount" />      
    </td>
    <td>
      <button type="button" <%=attDisableNextButton%> onClick="window.location='PriceTypeList.do?priceTypeNameFilter=<bean:write name="priceTypeListHeadForm" 
        property="priceTypeNameFilter" />&pageNo=<bean:write name="priceTypeListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>        
    </td>  
  </tr>
</table>