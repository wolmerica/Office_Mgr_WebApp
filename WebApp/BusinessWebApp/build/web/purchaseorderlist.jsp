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
 
<logic:equal name="purchaseOrderListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="purchaseOrderListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="purchaseOrderListHeadForm"
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
    <form action="PurchaseOrderList.do" name="orderNumberFilter" method="post">
      <logic:notEqual name="purchaseOrderListHeadForm" property="itemKeyFilter" value="0">
        <input type="hidden" name="itemKeyFilter" value="<bean:write name="purchaseOrderListHeadForm" property="itemKeyFilter" />" >
      </logic:notEqual>        
      <logic:notEqual name="purchaseOrderListHeadForm" property="scheduleKeyFilter" value="0">
        <input type="hidden" name="scheduleKeyFilter" value="<bean:write name="purchaseOrderListHeadForm" property="scheduleKeyFilter" />" >
      </logic:notEqual>        
      <th nowrap>
        <bean:message key="app.purchaseorder.vendorName" />
        <input type="text" name="vendorNameFilter" size="4" maxlength="10" value="<bean:write name="purchaseOrderListHeadForm" property="vendorNameFilter" />" >
      </th>        
      <th nowrap>        
        <bean:message key="app.purchaseorder.purchaseOrderNumber" />
        <input type="text" name="orderNumberFilter" size="4" maxlength="10" value="<bean:write name="purchaseOrderListHeadForm" property="orderNumberFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />" >                
      </th>
    </form>     
    <th><bean:message key="app.purchaseorder.clientName" /></th>
    <th><bean:message key="app.purchaseorder.attributeToName" /></th>
    <th><bean:message key="app.purchaseorder.rebate" /></th>    
    <th><bean:message key="app.purchaseorder.orderStatus" /></th>
    <th><bean:message key="app.purchaseorder.orderTotal" /></th>    
    <th colspan="2" align="center" nowrap>
      <% if (permAddId.equalsIgnoreCase("true")) { %>
        <button type="button" onClick="window.location='PurchaseOrderEntry.do' "><bean:message key="app.purchaseorder.addTitle" /></button>      
      <% } %>
    </th> 
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
<logic:notEqual name="purchaseOrderListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the purchase orders -->       
  <logic:iterate id="purchaseOrderListForm"
                 name="purchaseOrderListHeadForm"
                 property="purchaseOrderListForm"
                 scope="session"
                 type="com.wolmerica.purchaseorder.PurchaseOrderListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="purchaseOrderListHeadForm"
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
<!-- Make adjustments to not allow edits on complete orders -->          
        <logic:equal name="purchaseOrderListForm" property="orderStatus" value="Complete">
          <% 
            if (permEditId.equalsIgnoreCase("true")) {
              permViewId = permEditId;
            }
            permEditId = "false";
          %> 
        </logic:equal>
<!-- Only allow deletes for purchase orders in "New" state -->         
        <logic:notEqual name="purchaseOrderListForm" property="orderStatus" value="New">
          <% permDeleteId = "false"; %> 
        </logic:notEqual>
                
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
            <bean:write name="purchaseOrderListForm" property="vendorName" />
	  </td>
          <td>
            <bean:write name="purchaseOrderListForm" property="purchaseOrderNumber" />
	  </td>
	  <td>
            <bean:write name="purchaseOrderListForm" property="clientName" />         
	  </td>
	  <td>
            <bean:write name="purchaseOrderListForm" property="attributeToName" />
	  </td>
          <td align="center"> 
            <logic:notEqual name="purchaseOrderListForm" property="rebateCount" value="0">    
              <a href="RebateList.do?poKey=<bean:write name="purchaseOrderListForm" property="key" />"><bean:write name="purchaseOrderListForm" property="rebateCount" /><bean:message key="app.purchaseorder.rebateAvailable" /></a>
              <logic:notEqual name="purchaseOrderListForm" property="rebateInstanceCount" value="0">    
                &nbsp;
                <a href="RebateInstanceList.do?poKey=<bean:write name="purchaseOrderListForm" property="key" />"><bean:write name="purchaseOrderListForm" property="rebateInstanceCount" /><bean:message key="app.purchaseorder.rebateOpen"/></a>
              </logic:notEqual>
            </logic:notEqual>
          </td>                          
	  <td>
            <bean:write name="purchaseOrderListForm" property="orderStatus" />
	  </td>	 
          <td align="right"> 
            <bean:message key="app.localeCurrency" />    
            <bean:write name="purchaseOrderListForm" property="orderTotal" />
          </td>           
          <td>
            &nbsp; 
            <% if (permViewId.equalsIgnoreCase("true")) { %>
              <a href="PurchaseOrderGet.do?key=<bean:write name="purchaseOrderListForm"
                property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="PurchaseOrderGet.do?key=<bean:write name="purchaseOrderListForm"
                property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>             
            &nbsp;
	  </td>
          <td>
            &nbsp;
            <logic:equal name="purchaseOrderListForm" property="orderFormKey" value="0">
              <a href="#" onclick="popup = putinpopup('PdfPurchaseOrder.do?presentationType=pdf&key=<bean:write name="purchaseOrderListForm"
                property="key" />'); return false" target="_PDF">PDF</a>
            </logic:equal>
            <logic:equal name="purchaseOrderListForm" property="orderFormKey" value="1">
              <a href="#" onclick="popup = putinpopup('PdfPopulateExistingForm.do?presentationType=pdf&key=<bean:write name="purchaseOrderListForm"
                property="key" />'); return false" target="_PDF">PDF</a>
            </logic:equal>
<!-- Do not allow PDF creation until the purchase order is submitted.
-->
            <logic:equal name="purchaseOrderListForm" property="orderFormKey" value="2">
              <logic:notEqual name="purchaseOrderListForm" property="orderStatus" value="New">
                <a href="#" onclick="popup = putinpopup('PdfAntechLabRequestForm.do?presentationType=pdf&key=<bean:write name="purchaseOrderListForm"
                  property="key" />'); return false" target="_PDF">PDF</a>
              </logic:notEqual>
            </logic:equal>
            &nbsp;
	  </td>	  	  
	  <td>
            <% if (permDeleteId.equalsIgnoreCase("true")) { %>	  
              &nbsp;
              <a href="PurchaseOrderDelete.do?key=<bean:write name="purchaseOrderListForm"
                property="key" />"  onclick="return confirm('<bean:message key="app.purchaseorder.delete.message" />')" > <bean:message key="app.delete" /></a>             
              &nbsp;
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
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='PurchaseOrderList.do?vendorNameFilter=<bean:write name="purchaseOrderListHeadForm" 
        property="vendorNameFilter" />&orderNumberFilter=<bean:write name="purchaseOrderListHeadForm" 
        property="orderNumberFilter" />&itemKeyFilter=<bean:write name="purchaseOrderListHeadForm" 
        property="itemKeyFilter" />&scheduleKeyFilter=<bean:write name="purchaseOrderListHeadForm" 
        property="scheduleKeyFilter" />&pageNo=<bean:write name="purchaseOrderListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="6" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="purchaseOrderListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="purchaseOrderListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="purchaseOrderListHeadForm" property="recordCount" />      
    </td>
    <td colspan="3" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='PurchaseOrderList.do?vendorNameFilter=<bean:write name="purchaseOrderListHeadForm" 
        property="vendorNameFilter" />&orderNumberFilter=<bean:write name="purchaseOrderListHeadForm" 
        property="orderNumberFilter" />&itemKeyFilter=<bean:write name="purchaseOrderListHeadForm" 
        property="itemKeyFilter" />&scheduleKeyFilter=<bean:write name="purchaseOrderListHeadForm" 
        property="scheduleKeyFilter" />&pageNo=<bean:write name="purchaseOrderListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>
