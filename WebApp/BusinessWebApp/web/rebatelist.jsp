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
 
<logic:equal name="rebateListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="rebateListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="rebateListHeadForm"
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
<% if ((request.getParameter("idKey") != null) || (request.getParameter("poKey") != null)) { %>
  <tr>
    <td colspan="2">
      <% if (request.getParameter("poiKey") != null) { %>
        <form action="PurchaseOrderItemGet.do" name="purchaseOrderItemGet" method="post">        
          <input type="hidden" name="key" value="<%=request.getParameter("poKey").toString()%>">
          <input type="submit" value="<bean:message key="app.purchaseorderitem.listBackMessage" />">
        </form>         
      <% } else if (request.getParameter("poKey") != null) { %>          
        <form action="PurchaseOrderList.do" name="purchaseOrderList" method="post">
          <input type="hidden" name="key" value="<%=request.getParameter("poKey").toString()%>">
          <input type="submit" value="<bean:message key="app.purchaseorder.backMessage" />">
        </form>         
      <% } else if (request.getParameter("idKey") != null) { %>    
        <form action="ItemDictionaryList.do" name="itemDictionaryList" method="post">
          <input type="hidden" name="key" value="<%=request.getParameter("idKey").toString()%>">
          <input type="submit" value="<bean:message key="app.itemdictionary.backMessage" />">
        </form>         
      <% } %>
    </td>    
  </tr>
  <% if (request.getParameter("idKey") != null) { %>
    <tr>
      <td colspan="8">
        <strong><bean:message key="app.itemdictionary.brandName" />:</strong>
        &nbsp;&nbsp;<%=request.getParameter("idBrandName")%>
        &nbsp;&nbsp;
        <strong><bean:message key="app.itemdictionary.size" />/<bean:message key="app.itemdictionary.sizeUnit" />:</strong>
        &nbsp;&nbsp;<%=request.getParameter("idSize")%>/<%=request.getParameter("idSizeUnit")%>       
      </td>
    </tr>
  <% } %>      
  <tr>
    <td colspan="8">
      <hr>
    </td>    
  </tr>
<% } %>  
  <tr align="left">
    <th>
    <% if (request.getParameter("idKey") != null) { %>      
        <bean:message key="app.rebate.offerName" />
    <% } else { %>
      <form action="RebateList.do" name="rebateFilter" method="post">
        <bean:message key="app.rebate.offerName" />&nbsp;&nbsp;
            <input type="text" name="offerNameFilter" size="6" value="<bean:write name="rebateListHeadForm" property="offerNameFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </form>
    <% } %>
    </th>
    <th><bean:message key="app.rebate.brandName" /></th>
    <th>
      <bean:message key="app.rebate.size" />/
      <bean:message key="app.rebate.sizeUnit" />
    </th>
    <th><bean:message key="app.rebate.endDate" /></th>        
    <th><bean:message key="app.rebate.applied" /></th>       
    <th><bean:message key="app.rebate.amount" /></th>  
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>
        <% if (request.getParameter("idKey") != null) { %>              
          <button type="button" onClick="window.location='RebateEntry.do?idKey=<%=request.getParameter("idKey").toString()%><% if (request.getParameter("poKey") != null) { %>&poKey=<%=request.getParameter("poKey").toString()%><% } %><% if (request.getParameter("poiKey") != null) { %>&poiKey=<%=request.getParameter("poiKey").toString()%><% } %>' "><bean:message key="app.rebate.addTitle" /></button>
        <% } %>
      <% } %>        
    </th>   
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<logic:notEqual name="rebateListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the rebates -->       
  <logic:iterate id="rebateListForm"
                 name="rebateListHeadForm"
                 property="rebateListForm"
                 scope="session"
                 type="com.wolmerica.rebate.RebateListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="rebateListHeadForm"
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
            <bean:write name="rebateListForm" property="offerName" />
	  </td>
	  <td>
            <bean:write name="rebateListForm" property="brandName" />
	  </td>
          <td>
            <bean:write name="rebateListForm" property="size" />
            <bean:write name="rebateListForm" property="sizeUnit" />
          </td>          
	  <td>
            <bean:write name="rebateListForm" property="endDate" />
	  </td> 
	  <td align="center">
            <bean:write name="rebateListForm" property="rebateInstanceCount" />
	  </td>           
	  <td>
            <bean:message key="app.localeCurrency" />          
            <bean:write name="rebateListForm" property="amount" />
	  </td>     
          <% if (request.getParameter("poiKey") != null) { %>     
            <td colspan="2" align="center">
	      <a href="RebateInstanceAdd.do?rebateKey=<bean:write name="rebateListForm"
                property="key" /><% if (request.getParameter("poiKey") != null) { %>&poiKey=<%=request.getParameter("poiKey").toString()%><% } %><% if (request.getParameter("poKey") != null) { %>&poKey=<%=request.getParameter("poKey").toString()%><% } %><% if (request.getParameter("idKey") != null) { %>&idKey=<%=request.getParameter("idKey").toString()%><% } %>"><bean:message key="app.rebate.apply" /></a>
            </td>
          <% } else { %>
            <td>
              <% if (permViewId.equalsIgnoreCase("true")) { %>	  
                <a href="RebateGet.do?key=<bean:write name="rebateListForm"
                  property="key" /><% if (request.getParameter("poiKey") != null) { %>&poiKey=<%=request.getParameter("poiKey").toString()%><% } %><% if (request.getParameter("poKey") != null) { %>&poKey=<%=request.getParameter("poKey").toString()%><% } %><% if (request.getParameter("idKey") != null) { %>&idKey=<%=request.getParameter("idKey").toString()%><% } %>"><bean:message key="app.view" /></a>
              <% } %>
              <% if (permEditId.equalsIgnoreCase("true")) { %>
                <a href="RebateGet.do?key=<bean:write name="rebateListForm"
                  property="key" /><% if (request.getParameter("poiKey") != null) { %>&poiKey=<%=request.getParameter("poiKey").toString()%><% } %><% if (request.getParameter("poKey") != null) { %>&poKey=<%=request.getParameter("poKey").toString()%><% } %><% if (request.getParameter("idKey") != null) { %>&idKey=<%=request.getParameter("idKey").toString()%><% } %>"><bean:message key="app.edit" /></a> 
              <% } %>
              <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
              <% } %>             
            </td>
	    <td align="right">
              <logic:equal name="rebateListForm" property="allowDeleteId" value="true">            
                <% if (permDeleteId.equalsIgnoreCase("true")) { %>              
                  <a href="RebateDelete.do?key=<bean:write name="rebateListForm"
                    property="key" /><% if (request.getParameter("poiKey") != null) { %>&poiKey=<%=request.getParameter("poiKey").toString()%><% } %><% if (request.getParameter("poKey") != null) { %>&poKey=<%=request.getParameter("poKey").toString()%><% } %><% if (request.getParameter("idKey") != null) { %>&idKey=<%=request.getParameter("idKey").toString()%><% } %>" onclick="return confirm('<bean:message key="app.rebate.delete.message" />')" ><bean:message key="app.delete" /></a>
                <% } %>
              </logic:equal>
	    </td>
          <% } %>
	</tr>
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="8">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='RebateList.do?offerNameFilter=<bean:write name="rebateListHeadForm" 
        property="offerNameFilter" />&pageNo=<bean:write name="rebateListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="5" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="rebateListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="rebateListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="rebateListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='RebateList.do?offerNameFilter=<bean:write name="rebateListHeadForm" 
        property="offerNameFilter" />&pageNo=<bean:write name="rebateListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>