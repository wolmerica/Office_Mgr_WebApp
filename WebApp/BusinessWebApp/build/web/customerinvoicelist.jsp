<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/popup_window.js" type="text/javascript"></script>

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
 
<logic:equal name="customerInvoiceListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="customerInvoiceListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="customerInvoiceListHeadForm"
               property="permissionListForm"
               scope="session"
               offset="<%=permOffset.toString()%>"
               length="1"
               type="com.wolmerica.permission.PermissionListForm">
  <% 
    permAddId = permissionListForm.getAddId();              
  %>
</logic:iterate> 
<!-- Do not show the add button unless there is more items to distribute. -->
<logic:equal name="customerInvoiceListHeadForm" property="moreToDistribute" value="false">
  <% permAddId = "false"; %>
</logic:equal>


<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <logic:notEqual name="customerInvoiceListHeadForm" property="purchaseOrderKey" value="">
    <tr>
      <td colspan="2">
        <form action="PurchaseOrderGet.do" name="purchaseOrderGet" method="post">                                  
          <input type="hidden" name="key" value="<bean:write name="customerInvoiceListHeadForm" property="purchaseOrderKey" />">  
          <input type="submit" value="<bean:message key="app.customerinvoice.back" />">
        </form>            
      </td>
    </tr>
    <tr>
      <td colspan="10">
        <hr>
      </td>
    </tr>     
  </logic:notEqual>
  <tr align="left">
    <logic:equal name="customerInvoiceListHeadForm" property="purchaseOrderKey" value="">
      <form action="CustomerInvoiceList.do" name="customerInvoiceFilter" method="post">      
        <input type="hidden" name="internalUse">      
        <th colspan="2">
          <bean:message key="app.customerinvoice.clientName" />
          <input type="text" name="clientNameFilter" size="4" maxlength="10" value="<bean:write name="customerInvoiceListHeadForm" property="clientNameFilter" />" >
          <input type="checkbox" name="internalUseCheckBox" onclick="document.customerInvoiceFilter.internalUse.value=document.customerInvoiceFilter.internalUseCheckBox.checked;" ><bean:message key="app.customerinvoice.internalUse"/>
        </th>      
        <th>
          <bean:message key="app.customerinvoice.customerInvoiceNumber" />
          <input type="text" name="invoiceNumberFilter" size="4" maxlength="10" value="<bean:write name="customerInvoiceListHeadForm" property="invoiceNumberFilter" />" >
          <input type="submit" value="<bean:message key="app.runIt" />" >
        </th>
      </form>    
      <script type="text/javascript">
          document.customerInvoiceFilter.internalUseCheckBox.checked = <bean:write name="customerInvoiceListHeadForm" property="internalUse" />;
          document.customerInvoiceFilter.internalUse.value = document.customerInvoiceFilter.internalUseCheckBox.checked;
      </script>
    </logic:equal>
    <logic:notEqual name="customerInvoiceListHeadForm" property="purchaseOrderKey" value="">
      <th><bean:message key="app.customerinvoice.clientName" /></th>
      <th><bean:message key="app.customerinvoice.attributeToName" /></th>
      <th><bean:message key="app.customerinvoice.customerInvoiceNumber" /></th>
    </logic:notEqual>      
    <th><bean:message key="app.customerinvoice.createStamp" /></th>    
    <th align="right"><bean:message key="app.customerinvoice.invoiceTotal" /></th>                     
    <th colspan="3" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>      
        <button type="button" onClick="window.location='CustomerInvoiceEntry.do?key=<bean:write name="customerInvoiceListHeadForm" 
          property="genesisKey" />' "><bean:message key="app.customerinvoice.addTitle" /></button>          
        </form>
      <% } %>
    </th>   
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
<logic:notEqual name="customerInvoiceListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the customer invoices -->      
  <logic:iterate id="customerInvoiceListForm"
                 name="customerInvoiceListHeadForm"
                 property="customerInvoiceListForm"
                 scope="session"
                 type="com.wolmerica.customerinvoice.CustomerInvoiceListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="customerInvoiceListHeadForm"
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
<!-- Make adjustments to not allow edits on non-active customer invoices -->  
        <logic:equal name="customerInvoiceListForm" property="activeId" value="false">
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
        ++i;      
        if ( i % 2 == 0) { currentColor = "ODD"; }
        else { currentColor = "EVEN"; }
      %>                              
         
    <tr align="left" class="<%=currentColor %>">
      <td>
        <bean:write name="customerInvoiceListForm" property="clientName" />
      </td>
      <td>
        <bean:write name="customerInvoiceListForm" property="attributeToName" />
      </td>
      <td>
        <bean:write name="customerInvoiceListForm" property="customerInvoiceNumber" />
      </td>
      <td>
        <bean:write name="customerInvoiceListForm" property="createStamp" />
      </td>       
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="customerInvoiceListForm" property="invoiceTotal" />
      </td>
      <td>
        &nbsp;
        <% if (permViewId.equalsIgnoreCase("true")) { %>
          <a href="CustomerInvoiceGet.do?key=<bean:write name="customerInvoiceListForm"
            property="key" />"><bean:message key="app.view" /></a>
        <% } %>
        <% if (permEditId.equalsIgnoreCase("true")) { %>
          <a href="CustomerInvoiceGet.do?key=<bean:write name="customerInvoiceListForm"
            property="key" />"><bean:message key="app.edit" /></a>
        <% } %>
        <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>
             <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
        <% } %>             
        &nbsp;           
      </td>
      <td>
        <% if (permDeleteId.equalsIgnoreCase("true")) { %>      
          &nbsp;
          <a href="CustomerInvoiceDelete.do?ciKey=<bean:write name="customerInvoiceListForm" property="key" />
<logic:notEqual name="customerInvoiceListHeadForm" property="vendorInvoiceKey" value="">
&key=<bean:write name="customerInvoiceListHeadForm" property="vendorInvoiceKey" />
</logic:notEqual>
         " onclick="return confirm('<bean:message key="app.customerinvoice.delete.message" />')" ><bean:message key="app.delete" /></a>
          &nbsp;
        <% } %>
      </td>      
      <td>
        <% if (permViewId.equalsIgnoreCase("true") || permEditId.equalsIgnoreCase("true")) { %>          
          &nbsp;
          <a href="#" onclick="popup = putinpopup('PdfCustomerInvoice.do?presentationType=pdf&key=<bean:write name="customerInvoiceListForm"
            property="key" />'); return false" target="_PDF"><bean:message key="app.pdf" /></a>
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='CustomerInvoiceList.do?internalUse=<bean:write name="customerInvoiceListHeadForm" 
        property="internalUse" />&clientNameFilter=<bean:write name="customerInvoiceListHeadForm" 
        property="clientNameFilter" />&invoiceNumberFilter=<bean:write name="customerInvoiceListHeadForm" 
        property="invoiceNumberFilter" />&itemKeyFilter=<bean:write name="customerInvoiceListHeadForm" 
        property="itemKeyFilter" />&serviceKeyFilter=<bean:write name="customerInvoiceListHeadForm" 
        property="serviceKeyFilter" />&pageNo=<bean:write name="customerInvoiceListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="4" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="customerInvoiceListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="customerInvoiceListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="customerInvoiceListHeadForm" property="recordCount" />      
    </td>
    <td colspan="3" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='CustomerInvoiceList.do?internalUse=<bean:write name="customerInvoiceListHeadForm" 
        property="internalUse" />&clientNameFilter=<bean:write name="customerInvoiceListHeadForm" 
        property="clientNameFilter" />&invoiceNumberFilter=<bean:write name="customerInvoiceListHeadForm" 
        property="invoiceNumberFilter" />&itemKeyFilter=<bean:write name="customerInvoiceListHeadForm" 
        property="itemKeyFilter" />&serviceKeyFilter=<bean:write name="customerInvoiceListHeadForm" 
        property="serviceKeyFilter" />&pageNo=<bean:write name="customerInvoiceListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>