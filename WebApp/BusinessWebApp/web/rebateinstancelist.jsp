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
 
<logic:equal name="rebateInstanceListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="rebateInstanceListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="rebateInstanceListHeadForm"
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
    <td colspan="2">
      <form action="RebateList.do" name="rebateList" method="post">
        <% if (request.getParameter("idKey") != null) { %>
          <input type="hidden" name="idKey" value="<%=request.getParameter("idKey").toString()%>">
        <% } %>
        <% if (request.getParameter("poKey") != null) { %>          
          <input type="hidden" name="poKey" value="<%=request.getParameter("poKey").toString()%>">
        <% } %>
        <% if (request.getParameter("poiKey") != null) { %>
          <input type="hidden" name="poiKey" value="<%=request.getParameter("poiKey").toString()%>">
        <% } %>                    
      <input type="submit" value="<bean:message key="app.rebateinstance.back" />">
      </form> 
    </td>
  </tr>    
  <tr>
    <td colspan="9">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.rebateinstance.purchaseOrderNumber" /></th>  
      <% if ((request.getParameter("idKey") != null) ||
            (request.getParameter("poKey") != null) || 
            (request.getParameter("poiKey") != null)) { %>
        <th>                
          <bean:message key="app.rebateinstance.offerName" />
        </th>
      <% } else { %>
        <form action="RebateInstanceList.do" name="rebateInstanceList" method="post">
          <th>            
            <bean:message key="app.rebateinstance.offerName" />
            <input type="text" name="offerNameFilter" size="4" maxlength="8" value="<bean:write name="rebateInstanceListHeadForm" property="offerNameFilter" />" >
            <input type="submit" value="<bean:message key="app.runIt" />">
         </th>
        </form>         
      <% } %>
    <th><bean:message key="app.rebateinstance.eligibleId" /></th>
    <th><bean:message key="app.rebateinstance.submitId" /></th>
    <th><bean:message key="app.rebateinstance.completeId" /></th>
    <th><bean:message key="app.rebateinstance.amount" /></th>    
  </tr>
  <tr>
    <td colspan="9">
      <hr>
    </td>
  </tr>
<logic:notEqual name="rebateInstanceListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the rebate instances -->       
  <logic:iterate id="rebateInstanceForm"
                 name="rebateInstanceListHeadForm"
                 property="rebateInstanceForm"
                 scope="session"
                 type="com.wolmerica.rebateinstance.RebateInstanceForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="rebateInstanceListHeadForm"
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
<!-- Make adjustments to not allow edits on non-active rebate instances -->         
        <logic:equal name="rebateInstanceForm" property="completeId" value="true">
          <% 
            if (permEditId.equalsIgnoreCase("true")) {
              permViewId = permEditId;
            }
            permEditId = "false";
            permDeleteId = "false";
          %>
        </logic:equal>        
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
            <bean:write name="rebateInstanceForm" property="purchaseOrderNumber" />
	  </td>        
	  <td>
            <bean:write name="rebateInstanceForm" property="offerName" />             
	  </td>
	  <td align="center">
            <html:checkbox name="rebateInstanceForm" property="eligibleId" disabled="true" />
          </td>
	  <td align="center">
            <html:checkbox name="rebateInstanceForm" property="submitId" disabled="true" />
          </td>
	  <td align="center">
            <html:checkbox name="rebateInstanceForm" property="completeId" disabled="true" />
          </td>    
	  <td align="right">
            <bean:message key="app.localeCurrency" />          
            <bean:write name="rebateInstanceForm" property="amount" />
	  </td>          
	  <td>	  
            <% if (permViewId.equalsIgnoreCase("true")) { %>	  
              <a href="RebateInstanceGet.do?key=<bean:write name="rebateInstanceForm"
                property="key" /><% if (request.getParameter("poiKey") != null) { %>&poiKey=<%=request.getParameter("poiKey").toString()%><% } %><% if (request.getParameter("poKey") != null) { %>&poKey=<%=request.getParameter("poKey").toString()%><% } %><% if (request.getParameter("idKey") != null) { %>&idKey=<%=request.getParameter("idKey").toString()%><% } %>"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="RebateInstanceGet.do?key=<bean:write name="rebateInstanceForm"
                property="key" /><% if (request.getParameter("poiKey") != null) { %>&poiKey=<%=request.getParameter("poiKey").toString()%><% } %><% if (request.getParameter("poKey") != null) { %>&poKey=<%=request.getParameter("poKey").toString()%><% } %><% if (request.getParameter("idKey") != null) { %>&idKey=<%=request.getParameter("idKey").toString()%><% } %>"><bean:message key="app.edit" /></a> 
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>               
	  </td>          
	  <td align="right">
            <logic:equal name="rebateInstanceForm" property="eligibleId" value="false">
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>	  
                <a href="RebateInstanceDelete.do?key=<bean:write name="rebateInstanceForm"
                  property="key" /><% if (request.getParameter("poiKey") != null) { %>&poiKey=<%=request.getParameter("poiKey").toString()%><% } %><% if (request.getParameter("poKey") != null) { %>&poKey=<%=request.getParameter("poKey").toString()%><% } %><% if (request.getParameter("idKey") != null) { %>&idKey=<%=request.getParameter("idKey").toString()%><% } %>" onclick="return confirm('<bean:message key="app.rebateinstance.delete.message" />')"><bean:message key="app.delete" /></a>
              <% } %>
            </logic:equal>  
            <logic:equal name="rebateInstanceForm" property="eligibleId" value="true">
              <logic:notEqual name="rebateInstanceForm" property="trackingURL" value="">	              
                <a href="blank" onclick="popup = putinpopup('<bean:write name="rebateInstanceForm" property="trackingURL" />'); return false" target="_blank"> <bean:message key="app.status" /> </a>
              </logic:notEqual>
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
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='RebateInstanceList.do?offerNameFilter=<bean:write name="rebateInstanceListHeadForm" 
        property="offerNameFilter" />&pageNo=<bean:write name="rebateInstanceListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>        
    </td>
    <td colspan="3" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="rebateInstanceListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="rebateInstanceListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="rebateInstanceListHeadForm" property="recordCount" />      
    </td>
    <td align="right">
      <strong><bean:message key="app.rebateinstance.pageTotal" /></strong>&nbsp;
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />          
      <bean:write name="rebateInstanceListHeadForm" property="pageTotal" />
    </td>    
    <td colspan="2" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='RebateInstanceList.do?offerNameFilter=<bean:write name="rebateInstanceListHeadForm" 
        property="offerNameFilter" />&pageNo=<bean:write name="rebateInstanceListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>