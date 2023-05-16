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
 
<logic:equal name="customerListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="customerListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="customerListHeadForm"
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
        <th><bean:message key="app.customer.codeNum" /></th>         
        <th>
          <form action="CustomerList.do" name="customerFilter" method="post">        
            <bean:message key="app.customer.clientName" />&nbsp;&nbsp;
            <input type="text" name="lastNameFilter" size="5" maxlength="20" value="<bean:write name="customerListHeadForm" property="lastNameFilter" />" >
            <input type="submit" value="<bean:message key="app.runIt" />" >
          </form> 
        </th>
        <th><bean:message key="app.customer.customerTypeKey" /></th>        
        <th><bean:message key="app.customer.city" /></th>
        <th><bean:message key="app.customer.state" /></th>
        <th><bean:message key="app.customer.acctNum" /></th>
        <th><bean:message key="app.customer.lastServiceDate" /></th>
        <th><bean:message key="app.customer.accountBalanceAmount" /></th>
        <th colspan="2" align="right">
          <% if (permAddId.equalsIgnoreCase("true")) { %>
            <button type="button" onClick="window.location='CustomerEntry.do' "><bean:message key="app.customer.addTitle" /></button>
          <% } %>
        </th>      
      </tr>    
      <tr>
        <td colspan="10">
	  <hr>
	</td>
      <tr> 
      
<logic:notEqual name="customerListHeadForm" property="lastRecord" value="0"> 
  <logic:iterate id="customerListForm"
                 name="customerListHeadForm"
                 property="customerListForm"
                 scope="session"
                 type="com.wolmerica.customer.CustomerListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="customerListHeadForm"
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
      <bean:write name="customerListForm" property="codeNum" />
    </td>  
    <td>
      <bean:write name="customerListForm" property="clientName" />
    </td>
    <td>
      <bean:write name="customerListForm" property="customerTypeName" />
    </td>
    <td>
      <bean:write name="customerListForm" property="city" />
    </td>
    <td>
      <bean:write name="customerListForm" property="state" />
    </td>
    <td>
      <bean:write name="customerListForm" property="acctNum" />
    </td>
    <td>
      <logic:notEqual name="customerListForm" property="lastServiceDate" value="12/31/1990">              
        <bean:write name="customerListForm" property="lastServiceDate" />
      </logic:notEqual>
    </td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="customerListForm" property="accountBalanceAmount" />
    </td>          
    <td>  
      <% if (permViewId.equalsIgnoreCase("true")) { %>	  
           <a href="CustomerGet.do?key=<bean:write name="customerListForm"
             property="key" />"><bean:message key="app.view" /></a>
      <% } %>
      <% if (permEditId.equalsIgnoreCase("true")) { %>
           <a href="CustomerGet.do?key=<bean:write name="customerListForm"
             property="key" />"><bean:message key="app.edit" /></a>
      <% } %>
      <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
           <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
      <% } %>            
    </td>
    <td align="right">
      <logic:equal name="customerListForm" property="allowDeleteId" value="true">
        <% if (permDeleteId.equalsIgnoreCase("true")) { %>
             <a href="CustomerDelete.do?key=<bean:write name="customerListForm"
	       property="key" />"  onclick="return confirm('<bean:message key="app.customer.delete.message" />')" ><bean:message key="app.delete" /></a>
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='CustomerList.do?activeId=true&lastNameFilter=<bean:write name="customerListHeadForm"
        property="lastNameFilter" />&pageNo=<bean:write name="customerListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="6">
      <logic:notEqual name="customerListHeadForm" property="lastRecord" value="0">         
        <button type="button" onClick="popup = putinpopup('CustomerExport.do'); return false" target="_PDF"><bean:message key="app.export" /></button>
      </logic:notEqual>
      &nbsp;<strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="customerListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="customerListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="customerListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='CustomerList.do?activeId=true&lastNameFilter=<bean:write name="customerListHeadForm"
        property="lastNameFilter" />&pageNo=<bean:write name="customerListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>

