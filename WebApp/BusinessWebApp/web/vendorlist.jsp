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
 
<logic:equal name="vendorListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="vendorListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="vendorListHeadForm"
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
      <form action="VendorList.do" name="vendorFilter" method="post">        
        <bean:message key="app.vendor.name" />&nbsp;&nbsp;
            <input type="text" name="nameFilter" size="5" maxlength="20" value="<bean:write name="vendorListHeadForm" property="nameFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </form>
    </th>
    <th><bean:message key="app.vendor.contactName" /></th>
    <th><bean:message key="app.vendor.phoneNum" /></th>
    <th><bean:message key="app.vendor.faxNum" /></th>
    <th><bean:message key="app.vendor.acctNum" /></th>
    <th><bean:message key="app.vendor.lastOrderDate" /></th>
    <th><bean:message key="app.vendor.accountBalanceAmount" /></th>
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>
        <button type="button" onClick="window.location='VendorEntry.do' "><bean:message key="app.vendor.addTitle" /></button>            
      <% } %>      
    </th>   
  </tr>
  <tr>
    <td colspan="9">
      <hr>
    </td>
  </tr>
<logic:notEqual name="vendorListHeadForm" property="lastRecord" value="0">      
  <logic:iterate id="vendorListForm"
                 name="vendorListHeadForm"
                 property="vendorListForm"
                 scope="session"
                 type="com.wolmerica.vendor.VendorListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="vendorListHeadForm"
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
            <bean:write name="vendorListForm" property="name" />
	  </td>
	  <td>
            <bean:write name="vendorListForm" property="contactName" />
	  </td>
	  <td>
            <bean:write name="vendorListForm" property="phoneNum" />
	  </td>
	  <td>
            <bean:write name="vendorListForm" property="faxNum" />
	  </td> 
          <td>
            <bean:write name="vendorListForm" property="acctNum" />
	  </td>          
	  <td>
            <bean:write name="vendorListForm" property="lastOrderDate" />
	  </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="vendorListForm" property="accountBalanceAmount" />          
          </td>
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
              <a href="VendorGet.do?key=<bean:write name="vendorListForm"
                property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="VendorGet.do?key=<bean:write name="vendorListForm"
                property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>  	       
	  </td>
	  <td align="right">
            <logic:equal name="vendorListForm" property="allowDeleteId" value="true">
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>	  
                <a href="VendorDelete.do?key=<bean:write name="vendorListForm"
                  property="key" />"  onclick="return confirm('<bean:message key="app.vendor.delete.message" />')" ><bean:message key="app.delete" /></a>
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='VendorList.do?nameFilter=<bean:write name="vendorListHeadForm" 
        property="nameFilter" />&pageNo=<bean:write name="vendorListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>        
    </td>
    <td colspan="5">
      <logic:notEqual name="vendorListHeadForm" property="lastRecord" value="0">         
        <button type="button" onClick="popup = putinpopup('VendorExport.do'); return false" target="_PDF"><bean:message key="app.export" /></button>
      </logic:notEqual>
      &nbsp;<strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="vendorListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="vendorListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="vendorListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='VendorList.do?nameFilter=<bean:write name="vendorListHeadForm" 
        property="nameFilter" />&pageNo=<bean:write name="vendorListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>        
    </td>  
  </tr>
</table>