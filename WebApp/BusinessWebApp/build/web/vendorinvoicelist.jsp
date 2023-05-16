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
 
<logic:equal name="vendorInvoiceListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="vendorInvoiceListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="vendorInvoiceListHeadForm"
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
  <tr align="left">
    <form action="VendorInvoiceList.do" name="vendorInvoiceFilter" method="post">      
      <th>
        <bean:message key="app.vendorinvoice.vendorName" />
        <input type="text" name="vendorNameFilter" size="4" maxlength="10" value="<bean:write name="vendorInvoiceListHeadForm" property="vendorNameFilter" />" >        
      </th>
      <th>
        <bean:message key="app.vendorinvoice.invoiceNumber" />
        <input type="text" name="invoiceNumberFilter" size="4" maxlength="10" value="<bean:write name="vendorInvoiceListHeadForm" property="invoiceNumberFilter" />" >
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </th>
    </form>
    <th><bean:message key="app.vendorinvoice.purchaseOrderNumber" /></th>
    <th><bean:message key="app.vendorinvoice.invoiceDate" /></th> 
    <th><bean:message key="app.vendorinvoice.invoiceDueDate" /></th>  
    <th align="right"><bean:message key="app.vendorinvoice.invoiceTotal" /></th>   
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<logic:notEqual name="vendorInvoiceListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the vendor invoices -->      
  <logic:iterate id="vendorInvoiceListForm"
                 name="vendorInvoiceListHeadForm"
                 property="vendorInvoiceListForm"
                 scope="session"
                 type="com.wolmerica.vendorinvoice.VendorInvoiceListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="vendorInvoiceListHeadForm"
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
<!-- Make adjustments to not allow edits on non-active vendor invoices -->  
        <logic:equal name="vendorInvoiceListForm" property="activeId" value="false">
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
        <bean:write name="vendorInvoiceListForm" property="vendorName" />
      </td>        
      <td>
        <bean:write name="vendorInvoiceListForm" property="invoiceNumber" />
      </td>
      <td>
        <bean:write name="vendorInvoiceListForm" property="purchaseOrderNumber" />
      </td>
      <td>
        <bean:write name="vendorInvoiceListForm" property="invoiceDate" />
      </td> 
      <td>
        <bean:write name="vendorInvoiceListForm" property="invoiceDueDate" />
      </td>		      
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="vendorInvoiceListForm" property="invoiceTotal" />
      </td>  
      <td align="center">
        <% if (permViewId.equalsIgnoreCase("true")) { %>
          <a href="VendorInvoiceGet.do?key=<bean:write name="vendorInvoiceListForm" 
            property="key" />"><bean:message key="app.view" /></a>
        <% } %>
        <% if (permEditId.equalsIgnoreCase("true")) { %>
          <a href="VendorInvoiceGet.do?key=<bean:write name="vendorInvoiceListForm" 
            property="key" />"><bean:message key="app.edit" /></a>
        <% } %>
        <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
             <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
        <% } %>        
      </td>
      <td> 
        <% if (permDeleteId.equalsIgnoreCase("true")) { %>      
          <a href="VendorInvoiceDeleteFromVIList.do?key=<bean:write name="vendorInvoiceListForm" 
            property="key" />&poKey=<bean:write name="vendorInvoiceListForm" 
            property="purchaseOrderKey" />&invoiceNumberFilter=<bean:write name="vendorInvoiceListHeadForm" 
            property="invoiceNumberFilter" /> "
            onclick="return confirm('<bean:message key="app.vendorinvoice.delete.message" />')" ><bean:message key="app.delete" /></a>
        <% } %>
<!-- Display delete or the customer invoice options -->        
        <logic:equal name="vendorInvoiceListForm" property="activeId" value="false">      
          <logic:equal name="vendorInvoiceListForm" property="masterInvoiceId" value="true">
            <logic:equal name="vendorInvoiceListForm" property="customerInvoiceCount" value="0">	            
              <logic:equal name="vendorInvoiceListForm" property="directShipId" value="true">
                <a href="CustomerInvoiceEntry.do?key=<bean:write name="vendorInvoiceListForm" property="key" />"
                  onclick="return confirm('<bean:message key="app.customerinvoice.directship.message" />')" ><bean:message key="app.customerinvoice.directship" /></a>
              </logic:equal>
              <logic:equal name="vendorInvoiceListForm" property="directShipId" value="false">          
                <a href="CustomerInvoiceEntry.do?key=<bean:write name="vendorInvoiceListForm" property="key" />"
                  onclick="return confirm('<bean:message key="app.customerinvoice.dropship.message" />')" ><bean:message key="app.customerinvoice.dropship" /></a>
              </logic:equal>
            </logic:equal>                                 
            <logic:notEqual name="vendorInvoiceListForm" property="customerInvoiceCount" value="0">
              <a href="CustomerInvoiceList.do?key=<bean:write name="vendorInvoiceListForm" property="key" />" ><bean:message key="app.customerinvoice.list" /></a>
            </logic:notEqual>                   
          </logic:equal>
        </logic:equal>          
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='VendorInvoiceList.do?vendorNameFilter=<bean:write name="vendorInvoiceListHeadForm" 
        property="vendorNameFilter" />&invoiceNumberFilter=<bean:write name="vendorInvoiceListHeadForm" 
        property="invoiceNumberFilter" />&pageNo=<bean:write name="vendorInvoiceListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>        
    </td>
    <td colspan="5" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="vendorInvoiceListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="vendorInvoiceListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="vendorInvoiceListHeadForm" property="recordCount" />      
    </td>
    <td colspan="3" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='VendorInvoiceList.do?vendorNameFilter=<bean:write name="vendorInvoiceListHeadForm" 
        property="vendorNameFilter" />&invoiceNumberFilter=<bean:write name="vendorInvoiceListHeadForm" 
        property="invoiceNumberFilter" />&pageNo=<bean:write name="vendorInvoiceListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>        
    </td>  
  </tr>
</table>
