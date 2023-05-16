<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
   
<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

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
 
<logic:equal name="attachmentListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="attachmentListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="attachmentListHeadForm"
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
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr>
    <td colspan="2"> 
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="0">
        <button type="button" onClick="window.location='HelpOperationList.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.help.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureHelp" />:</strong>
      </logic:equal>        
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="1">
        <button type="button" onClick="window.location='CustomerGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.customer.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureCustomer" />:</strong>
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="2">
        <button type="button" onClick="window.location='EmployeeGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.employee.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureEmployee" />:</strong>
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="3">
        <button type="button" onClick="window.location='ItemDictionaryGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.itemdictionary.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureItem" />:</strong>        
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="4">
        <button type="button" onClick="window.location='PetGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.pet.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featurePet" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="5">
        <button type="button" onClick="window.location='RebateGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.rebate.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureRebate" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="6">
        <button type="button" onClick="window.location='ServiceDictionaryGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.servicedictionary.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureService" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="7">
        <button type="button" onClick="window.location='VendorGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.vendor.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureVendor" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="11">
        <button type="button" onClick="window.location='PurchaseOrderGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.purchaseorder.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featurePurchaseOrder" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="13">
        <button type="button" onClick="window.location='VendorInvoiceGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.vendorinvoice.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureVendorInvoice" />:</strong>  
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="18">
        <button type="button" onClick="window.location='ExpenseGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.expense.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureExpense" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="24">
        <button type="button" onClick="window.location='SystemGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.system.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureSystem" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="28">
        <button type="button" onClick="window.location='ProspectGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.prospect.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureProspect" />:</strong>
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="31">
        <button type="button" onClick="window.location='VehicleGet.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&key=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />' "><bean:message key="app.vehicle.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureVehicle" />:</strong>
      </logic:equal>
      <logic:equal name="attachmentListHeadForm" property="sourceTypeKey" value="35">
        <button type="button" onClick="window.location='AntechLabResultsImport.do?sourceTypeKey=<bean:write name="attachmentListHeadForm"
          property="sourceTypeKey" />&vendorKey=<bean:write name="attachmentListHeadForm"
          property="sourceKey" />&fromDate=01/01/2006&toDate=01/02/2006' "><bean:message key="app.vendorresult.backToEdit" /></button>
        </td>
        <td colspan="6">
          <strong><bean:message key="app.employee.featureVendorResult" />:</strong>
      </logic:equal>
      &nbsp;&nbsp;
      <bean:write name="attachmentListHeadForm" property="sourceName" />   
    </td>
  </tr> 
  <tr>
    <td colspan = "7">            
      <form action="AttachmentList.do" name="attachmentListHeadForm" method="post">
        <input type="hidden" name="sourceTypeKey" value="<bean:write name="attachmentListHeadForm" property="sourceTypeKey" />">
        <input type="hidden" name="sourceKey" value="<bean:write name="attachmentListHeadForm" property="sourceKey" />">
        <input type="hidden" name="sourceName" value="<bean:write name="attachmentListHeadForm" property="sourceName" />">
        <strong><bean:message key="app.attachment.subject" />:</strong>        
        <input type="text" name="subjectFilter" value="<bean:write name="attachmentListHeadForm" property="subjectFilter" />" size="10" maxlength="10" >
        <strong>&nbsp;&nbsp;<bean:message key="app.dateFrom" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="attachmentListHeadForm" property="fromDate" />" size="8" maxlength="10" >
          <a href="javascript:show_calendar('attachmentListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click to select the from date."></a>
        <strong>&nbsp;&nbsp;<bean:message key="app.dateTo" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="attachmentListHeadForm" property="toDate" />" size="8" maxlength="10" >
          <a href="javascript:show_calendar('attachmentListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click to select the to date."></a>
        &nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </form>        
    </td>        
  </tr>  
  <tr align="left">
    <th><bean:message key="app.attachment.subject" /></th>
    <th><bean:message key="app.attachment.attachmentDate" /></th>            
    <th><bean:message key="app.attachment.name" /></th>
    <th><bean:message key="app.attachment.type" /></th>        
    <th align="right"><bean:message key="app.attachment.size" /></th>        
    <th colspan="3" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>        
        <button type="button" onClick="window.location='AttachmentEntry.do?sourceTypeKey=<bean:write name="attachmentListHeadForm" 
          property="sourceTypeKey" />&sourceKey=<bean:write name="attachmentListHeadForm" 
          property="sourceKey" />&sourceName=<bean:write name="attachmentListHeadForm" 
          property="sourceName" />&subjectFilter=<bean:write name="attachmentListHeadForm" 
          property="subjectFilter" />&fromDate=<bean:write name="attachmentListHeadForm" 
          property="fromDate" />&toDate=<bean:write name="attachmentListHeadForm" 
          property="toDate" />&pageNo=<bean:write name="attachmentListHeadForm" 
          property="nextPage" />' "><bean:message key="app.attachment.addTitle" /></button>
      <% } %>
    </th>      
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr>  
<logic:notEqual name="attachmentListHeadForm" property="lastRecord" value="0"> 

<!-- Iterate over all the attachments -->  
  <logic:iterate id="attachmentForm"
                 name="attachmentListHeadForm"
                 property="attachmentForm"
                 scope="session"
                 type="com.wolmerica.attachment.AttachmentForm">                 
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="attachmentListHeadForm"
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
            <bean:write name="attachmentForm" property="subject" />
	  </td>
	  <td>
            <bean:write name="attachmentForm" property="attachmentDate" />           
	  </td>
	  <td>
            <a href="blank" onclick="popup = putinpopup('<bean:write name="attachmentListHeadForm" 
                           property="documentServerURL" /><bean:write name="attachmentListHeadForm" 
                           property="sourceTypeKey" />/<bean:write name="attachmentListHeadForm" 
                           property="sourceKey" />/<bean:write name="attachmentForm" 
                           property="fileName" />'); return false" target="_blank"><bean:write name="attachmentForm" property="fileName" /> </a>            
	  </td>
	  <td>
            <bean:write name="attachmentForm" property="fileType" />
	  </td>
	  <td align="right">
            <bean:write name="attachmentForm" property="fileSize" />
	  </td>
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="AttachmentGet.do?key=<bean:write name="attachmentForm" 
              property="key" />&sourceTypeKey=<bean:write name="attachmentListHeadForm" 
              property="sourceTypeKey" />&sourceKey=<bean:write name="attachmentListHeadForm" 
              property="sourceKey" />&sourceName=<bean:write name="attachmentListHeadForm" 
              property="sourceName" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="AttachmentGet.do?key=<bean:write name="attachmentForm" 
              property="key" />&sourceTypeKey=<bean:write name="attachmentListHeadForm" 
              property="sourceTypeKey" />&sourceKey=<bean:write name="attachmentListHeadForm" 
              property="sourceKey" />&sourceName=<bean:write name="attachmentListHeadForm" 
              property="sourceName" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>              
	  </td>
	  <td align="right">
            <% if (permDeleteId.equalsIgnoreCase("true")) { %>
             <a href="AttachmentDelete.do?key=<bean:write name="attachmentForm"
              property="key" />&sourceTypeKey=<bean:write name="attachmentListHeadForm" 
              property="sourceTypeKey" />&sourceKey=<bean:write name="attachmentListHeadForm" 
              property="sourceKey" />&sourceName=<bean:write name="attachmentListHeadForm" 
              property="sourceName" />" onclick="return confirm('<bean:message key="app.attachment.delete.message" />')" ><bean:message key="app.delete" /></a>
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
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='AttachmentList.do?sourceTypeKey=<bean:write name="attachmentListHeadForm" 
        property="sourceTypeKey" />&sourceKey=<bean:write name="attachmentListHeadForm" 
        property="sourceKey" />&sourceName=<bean:write name="attachmentListHeadForm" 
        property="sourceName" />&subjectFilter=<bean:write name="attachmentListHeadForm" 
        property="subjectFilter" />&fromDate=<bean:write name="attachmentListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="attachmentListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="attachmentListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>                    
    </td>
    <td colspan="4">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="attachmentListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="attachmentListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="attachmentListHeadForm" property="recordCount" />      
    </td>
    <td>
      <button type="button" <%=attDisableNextButton%> onClick="window.location='AttachmentList.do?sourceTypeKey=<bean:write name="attachmentListHeadForm" 
        property="sourceTypeKey" />&sourceKey=<bean:write name="attachmentListHeadForm" 
        property="sourceKey" />&sourceName=<bean:write name="attachmentListHeadForm" 
        property="sourceName" />&subjectFilter=<bean:write name="attachmentListHeadForm" 
        property="subjectFilter" />&fromDate=<bean:write name="attachmentListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="attachmentListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="attachmentListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>                    
    </td>  
  </tr>
</table>