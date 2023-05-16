<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
    
<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>
    
<%
  String currentColor = "ODD";
  int i = 0;
  String attTitle = "app.purchaseorder.addTitle";            
  String attAction = "/PurchaseOrderAdd";
  String attDisableButton = "";
  String attDisablePOButton = "";
  boolean attDisableNoteEdit = true;
  boolean attDisableOrderEdit = true;
  boolean attDisableSave = true;  
  boolean attDisableEdit = true;
  if (request.getAttribute("disableEdit") != null) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {     
       attDisableEdit = false;
       attDisableSave = false;
       attDisableNoteEdit = false;
     }
  }
  boolean attDisableAttributeToLookup = true;
  String attServiceListDisabled = "";
  String attItemListDisabled = "";
%> 

<logic:notEqual name="purchaseOrderForm" property="key" value="">
  <%      
    attTitle = "app.purchaseorder.editTitle";
    attAction = "/PurchaseOrderEdit";
  %>
</logic:notEqual>  

<logic:notEqual name="purchaseOrderForm" property="customerKey" value="">
  <%
     attDisableAttributeToLookup = false;
  %>     
</logic:notEqual>

<logic:equal name="purchaseOrderForm" property="vendorServiceCount" value="0">
  <%
     attServiceListDisabled = "disabled";
  %>     
</logic:equal>

<logic:equal name="purchaseOrderForm" property="vendorItemCount" value="0">
  <%
     attItemListDisabled = "disabled";
  %>
</logic:equal>

<logic:equal name="purchaseOrderForm" property="balanceAmount" value="Unbalanced">
  <% 
    attDisablePOButton = "disabled";
  %>
</logic:equal>

<logic:equal name="purchaseOrderForm" property="orderStatus" value="Ordered">
  <% 
    attDisableOrderEdit = false;
    attDisableSave = false;
    attDisableNoteEdit = false;
  %>    
</logic:equal>

<logic:notEqual name="purchaseOrderForm" property="permissionStatus" value="LOCKED"> 
<bean:message key="app.readonly"/>
  <logic:notEqual name="purchaseOrderForm" property="permissionStatus" value="READONLY">
    [<bean:write name="purchaseOrderForm" property="permissionStatus" />]
  </logic:notEqual>    
  <%
    attDisableSave = true;
    attDisableButton = "disabled";    
    attDisablePOButton = "disabled";
    attDisableOrderEdit = true;
  %>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="PurchaseOrderList.do" name="purchaseOrderList" method="post">                                  
        <input type="submit" value="Back To Order List">
      </form> 
    </td>
    <logic:notEqual name="purchaseOrderForm" property="key" value="">
    <td colspan="2" align="right">
      <button type="button" <%=attServiceListDisabled%> onClick="window.location='PurchaseOrderServiceGet.do?key=<bean:write name="purchaseOrderForm"
        property="key" />' "><bean:message key="app.purchaseorder.serviceDetail" /></button>
    </td>        
    <td align="right">
      <button type="button" <%=attItemListDisabled%> onClick="window.location='PurchaseOrderItemGet.do?key=<bean:write name="purchaseOrderForm"
        property="key" />' "><bean:message key="app.purchaseorder.itemDetail" /></button>          
    </td>
    <td align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.purchaseorder.id"/>&sourceKey=<bean:write name="purchaseOrderForm"
                property="key" />&sourceName=<bean:write name="purchaseOrderForm"
                property="purchaseOrderNumber" />" >[<bean:write name="purchaseOrderForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."></a>
    </td>
    </logic:notEqual>      
  </tr>
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr> 
  <html:form action="<%=attAction%>"> 
    <logic:notEqual name="purchaseOrderForm" property="key" value="">
      <html:hidden property="key" />
    </logic:notEqual> 
  <tr>
    <td><strong><bean:message key="app.purchaseorder.purchaseOrderNumber" />:</strong></td>
    <td>
      <html:text property="purchaseOrderNumber" size="12" maxlength="12" readonly="true" />
      <html:errors property="purchaseOrderNumber" />              
    </td>
    <td><strong><bean:message key="app.purchaseorder.vendorName" />:</strong></td>
    <td colspan="3" align="left">
      <html:hidden property="vendorKey" />
      <html:hidden property="vendorName" />
      <% if (attDisableEdit) { %>      
        <html:text property="vendorName" size="35" maxlength="35" readonly="true" />
      <% } else { %>
        <span class="ac_holder">
        <input id="vendorNameAC" size="35" maxlength="35" value="<bean:write name="purchaseOrderForm" property="vendorName" />" onFocus="javascript:
        var options = {
		script:'VendorLookUp.do?json=true&',
		varname:'nameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.purchaseOrderForm.vendorKey.value=obj.id; document.purchaseOrderForm.vendorName.value=obj.value; }
		};
		var json=new AutoComplete('vendorNameAC',options);return true;" value="" />
        </span>      
        <html:errors property="vendorKey" />
      <% } %>        
    </td> 
  </tr>
  <tr>
    <td><strong><bean:message key="app.purchaseorder.orderStatus" />:</strong></td>
    <td>
      <html:text property="orderStatus" size="12" maxlength="12" readonly="true" />
      <html:errors property="orderStatus" />          
    </td>
    <td><strong><bean:message key="app.purchaseorder.clientName" />:</strong></td>
    <td colspan="3" align="left">
      <html:hidden property="customerKey" />
      <html:hidden property="clientName" />
      <% if (attDisableEdit) { %>      
        <html:text property="clientName" size="35" maxlength="35" readonly="true" />
      <% } else { %>
        <span class="ac_holder">
        <input id="clientNameAC" size="35" maxlength="35" value="<bean:write name="purchaseOrderForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.purchaseOrderForm.customerKey.value=obj.id; document.purchaseOrderForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>
        <html:errors property="customerKey" />
      <% } %>        
    </td> 
  </tr>
  <tr>
    <td><strong><bean:message key="app.purchaseorder.itemTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />    
      <html:text property="itemTotal" size="11" maxlength="11" readonly="true" />
      <html:errors property="itemTotal" />                  
    </td>
    <td>
      <html:hidden property="sourceTypeKey" />    
      <html:hidden property="sourceKey" />
      <html:hidden property="attributeToName" />
      <strong>
        <bean:message key="app.purchaseorder.attributeToName" />&nbsp;
        <bean:write name="purchaseOrderForm" property="attributeToEntity" />
      </strong>
    </td>
    <td colspan="3" align="left">
      <% if (attDisableAttributeToLookup || attDisableEdit) { %>
        <html:text property="attributeToName" size="35" maxlength="35" readonly="true" />
      <% } else { %>
        <logic:equal name="purchaseOrderForm" property="attributeToEntity" value="Pet">
          <span class="ac_holder">
          <input id="attributeToNameAC" size="35" maxlength="35" value="<bean:write name="purchaseOrderForm" property="attributeToName" />" onFocus="javascript:
          var options = {
		script:'PetLookUp.do?json=true&customerKeyFilter=<bean:write name="purchaseOrderForm" property="customerKey" />&',
		varname:'petNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.purchaseOrderForm.sourceTypeKey.value=4; document.purchaseOrderForm.sourceKey.value=obj.id; document.purchaseOrderForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
          </span>
        </logic:equal>        
        <logic:equal name="purchaseOrderForm" property="attributeToEntity" value="System">
          <span class="ac_holder">
          <input id="attributeToNameAC" size="35" maxlength="35" value="<bean:write name="purchaseOrderForm" property="attributeToName" />" onFocus="javascript:
          var options = {
		script:'SystemLookUp.do?json=true&customerKeyFilter=<bean:write name="purchaseOrderForm" property="customerKey" />&',
		varname:'makeModelFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.purchaseOrderForm.sourceTypeKey.value=24; document.purchaseOrderForm.sourceKey.value=obj.id; document.purchaseOrderForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
          </span>
        </logic:equal>
        <logic:equal name="purchaseOrderForm" property="attributeToEntity" value="Vehicle">
          <span class="ac_holder">
          <input id="attributeToNameAC" size="35" maxlength="35" value="<bean:write name="purchaseOrderForm" property="attributeToName" />" onFocus="javascript:
          var options = {
		script:'VehicleLookUp.do?json=true&customerKeyFilter=<bean:write name="purchaseOrderForm" property="customerKey" />&',
		varname:'makeFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.purchaseOrderForm.sourceTypeKey.value=31; document.purchaseOrderForm.sourceKey.value=obj.id; document.purchaseOrderForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
          </span>              
        </logic:equal>
      <% } %>              
      <html:errors property="attributeToName" />      
    </td>
  </tr>  
  <tr>
    <td><strong><bean:message key="app.purchaseorder.serviceTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />    
      <html:text property="serviceTotal" size="11" maxlength="11" readonly="true" />
      <html:errors property="serviceTotal" />                  
    </td>
    <td><strong><bean:message key="app.purchaseorder.priorityKey" />:</strong></td>
    <td colspan="3" align="left">
      <html:select property="priorityKey" disabled="<%=attDisableEdit%>">
        <html:option value="4">Low</html:option>
        <html:option value="3">Medium</html:option>
        <html:option value="2">High</html:option>
        <html:option value="1">Critical</html:option>
      </html:select>
      <html:errors property="priorityKey" />
    </td>
  </tr>    
  <tr>
    <td><strong><bean:message key="app.purchaseorder.orderTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />    
      <html:text property="orderTotal" size="11" maxlength="11" readonly="true" />
      <html:errors property="orderTotal" />                  
    </td>
  </tr>      
  <logic:notEqual name="purchaseOrderForm" property="key" value="">      
    <tr>
      <td><strong><bean:message key="app.purchaseorder.submitOrderStamp" />:</strong></td>
      <td>
        <html:text property="submitOrderStamp" size="20" maxlength="20" readonly="true" />
        <html:errors property="submitOrderStamp" />          
      </td>        
      <td><strong><bean:message key="app.purchaseorder.salesOrderNumber" />:</strong></td>
      <td>
        <html:text property="salesOrderNumber" size="35" maxlength="20" readonly="<%=attDisableOrderEdit%>" />
        <html:errors property="salesOrderNumber" />        
      </td>        
      <td>
        [<bean:write name="purchaseOrderForm" property="logisticsCount" />]
        <a href="" onClick="popup = logisticspopup('LogisticsGet.do?sourceTypeKey=<bean:message key="app.purchaseorder.id" />&sourceKey=<bean:write name="purchaseOrderForm" 
          property="key" />&sourceName=<bean:write name="purchaseOrderForm"
          property="purchaseOrderNumber" />&disableEdit=<%=attDisableOrderEdit%>', 'PopupPage'); return false" target="_blank"><bean:message key="app.purchaseorder.shippingInfo" /></a>           
      </td>      
    </tr>
  </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.purchaseorder.noteLine1" />:</strong></td>
    <td colspan="4">
      <html:textarea property="noteLine1" cols="60" rows="5" readonly="<%=attDisableNoteEdit%>" />
      <html:errors property="noteLine1" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.purchaseorder.createUser" />:</strong></td>
    <td><bean:write name="purchaseOrderForm" property="createUser" /> </td>	          	
    <td><strong><bean:message key="app.purchaseorder.createStamp" />:</strong></td>
    <td><bean:write name="purchaseOrderForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.purchaseorder.updateUser" />:</strong></td>
    <td><bean:write name="purchaseOrderForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.purchaseorder.updateStamp" />:</strong></td>
    <td><bean:write name="purchaseOrderForm" property="updateStamp" /></td>	     
  </tr>		  
  <tr>
    <td colspan="6" align="right">
      <html:submit value="Save P.O. Values" disabled="<%=attDisableSave%>" />         
    </td>     
    </html:form>    
  </tr>
  <tr>
    <td colspan="2" align="left">
      <logic:equal name="purchaseOrderForm" property="orderStatus" value="New">
        <logic:notEqual name="purchaseOrderForm" property="orderTotal" value="0.00">
          <form action="PurchaseOrderSubmit.do" name="purchaseOrderSetState" method="post">                                  
            <input type="hidden" name="key" value="<bean:write name="purchaseOrderForm" property="key" />">
            <input type="hidden" name="poState" value="1">
            <input type="submit" value="Submit Order" <%=attDisableButton%> onclick="return confirm('<bean:message key="app.purchaseorder.submit.message" />')" >
          </form>  
        </logic:notEqual>
      </logic:equal>
      <logic:equal name="purchaseOrderForm" property="orderStatus" value="Ordered">
        <form action="PurchaseOrderReceive.do" name="purchaseOrderSetState" method="post">                                  
          <input type="hidden" name="key" value="<bean:write name="purchaseOrderForm" property="key" />">
          <input type="hidden" name="poState" value="2">
          <input type="submit" value="Order Received" <%=attDisableButton%> onclick="return confirm('<bean:message key="app.purchaseorder.received.message" />')" >
        </form>  
      </logic:equal>
      <logic:equal name="purchaseOrderForm" property="orderStatus" value="Back-Ordered">
        <form action="PurchaseOrderReceive.do" name="purchaseOrderSetState" method="post">                                  
          <input type="hidden" name="key" value="<bean:write name="purchaseOrderForm" property="key" />">
          <input type="hidden" name="poState" value="2">
          <input type="submit" value="Back-Order Received" <%=attDisableButton%> onclick="return confirm('<bean:message key="app.purchaseorder.received.message" />')" >
        </form>  
      </logic:equal>      
      <logic:equal name="purchaseOrderForm" property="orderStatus" value="Check-In">           
        <logic:equal name="purchaseOrderForm" property="balanceQty" value="Unbalanced"> 
          <form action="VendorInvoiceComplete.do" name="purchaseOrderSet" method="post">                                  
            <input type="hidden" name="key" value="<bean:write name="purchaseOrderForm" property="key" />">
            <input type="hidden" name="poState" value="3">
            <input type="submit" value="Submit Back Order" <%=attDisablePOButton%> onclick="return confirm('<bean:message key="app.purchaseorder.backorder.message" />')" >
          </form>   
        </logic:equal>                   
        <logic:equal name="purchaseOrderForm" property="balanceQty" value="Balanced">
          <form action="VendorInvoiceComplete.do" name="purchaseOrderSet" method="post">                                  
            <input type="hidden" name="key" value="<bean:write name="purchaseOrderForm" property="key" />">
            <input type="hidden" name="poState" value="4">
            <input type="submit" value="Check-In Complete" <%=attDisablePOButton%> onclick="return confirm('<bean:message key="app.purchaseorder.complete.message" />')" >
          </form>         
        </logic:equal>                   
      </logic:equal>         
    </td>
  </tr>
<!-- Quantity and Amount balance checks to aide the display of buttons. 
  <tr> 
    <td colspan="3">
      Qty=<bean:write name="purchaseOrderForm" property="balanceQty" />
      $=<bean:write name="purchaseOrderForm" property="balanceAmount" />
    </td>
  </tr>
-->
</table>

<!--=========================================================-->
<!-- Vendor Invoice listing begins here.                     -->
<!--=========================================================-->
<logic:present name="vendorinvoices" >  
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.vendorinvoice.invoiceNumber" /></th>
    <th><bean:message key="app.vendorinvoice.invoiceDate" /></th>
    <th><bean:message key="app.vendorinvoice.invoiceDueDate" /></th>                    
    <th><bean:message key="app.vendorinvoice.balanceQty" />&nbsp;/&nbsp;<bean:message key="app.vendorinvoice.balanceAmount" /></th> 
    <th align="right"><bean:message key="app.vendorinvoice.invoiceTotal" /></th>                    
    <th colspan="2" align="center">
      <logic:equal name="purchaseOrderForm" property="orderStatus" value="Check-In">
        <logic:equal name="purchaseOrderForm" property="balanceQty" value="Unbalanced">           
          <form action="VendorInvoiceAdd.do" name="vendorInvoiceAdd" method="post">                                  
            <input type="hidden" name="poKey" value="<bean:write name="purchaseOrderForm" property="key"/>">
            <input type="submit" <%=attDisableButton%> value="Add Invoice">
          </form>
        </logic:equal>            
      </logic:equal>    
    </th>
  </tr>
    <tr>
      <td colspan="7">
        <hr>
      </td>
    </tr>
    <!-- iterate over the results of the query -->
    <logic:iterate id="vendorinvoice" name="vendorinvoices" scope="request">
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
        <bean:write name="vendorinvoice" property="invoiceNumber" />
      </td>
      <td>
        <bean:write name="vendorinvoice" property="invoiceDate" />
      </td>
      <td>
        <bean:write name="vendorinvoice" property="invoiceDueDate" />
      </td>
      <td>
        <bean:write name="vendorinvoice" property="balanceQty" />&nbsp;/&nbsp;<bean:write name="vendorinvoice" property="balanceAmount" />
      </td>		
      <td align="right">
        <bean:write name="vendorinvoice" property="grandTotal" />
      </td> 	
      <logic:equal name="vendorinvoice" property="activeId" value="true">
        <td align="center">
          <a href="VendorInvoiceGet.do?key=<bean:write name="vendorinvoice" 
            property="key" />"><bean:message key="app.edit" /></a>
        </td>
        <td> 
          <logic:equal name="vendorinvoice" property="masterInvoiceId" value="false">	              
            <a href="VendorInvoiceDeleteFromPO.do?key=<bean:write name="vendorinvoice"
              property="key" />&poKey=<bean:write name="purchaseOrderForm" property="key"/>"
              onclick="return confirm('<bean:message key="app.vendorinvoice.delete.message" />')" ><bean:message key="app.delete" /></a>
          </logic:equal>                
        </td>
	  </logic:equal>

      <logic:equal name="vendorinvoice" property="activeId" value="false">
        <td align="center">
          <a href="VendorInvoiceGet.do?key=<bean:write name="vendorinvoice"
            property="key" />"><bean:message key="app.view" /></a>
        </td>
        <td> 
          <logic:equal name="vendorinvoice" property="masterInvoiceId" value="true">	
            <logic:equal name="vendorinvoice" property="customerInvoiceCount" value="0">	            
              <logic:equal name="vendorinvoice" property="directShipId" value="true">          
                <a href="CustomerInvoiceEntry.do?key=<bean:write name="vendorinvoice" property="key" />"
                  onclick="return confirm('<bean:message key="app.customerinvoice.directship.message" />')" ><bean:message key="app.customerinvoice.directship" /></a>
              </logic:equal>
              <logic:equal name="vendorinvoice" property="directShipId" value="false">          
                <a href="CustomerInvoiceEntry.do?key=<bean:write name="vendorinvoice" property="key" />"
                  onclick="return confirm('<bean:message key="app.customerinvoice.dropship.message" />')" ><bean:message key="app.customerinvoice.dropship" /></a>
              </logic:equal>
            </logic:equal>                                 
            <logic:notEqual name="vendorinvoice" property="customerInvoiceCount" value="0">
              <a href="CustomerInvoiceList.do?key=<bean:write name="vendorinvoice" property="key" />" ><bean:message key="app.customerinvoice.list" /></a>
            </logic:notEqual>                   
          </logic:equal>
        </td>
      </logic:equal>      
    </tr>
    </logic:iterate>
  </table>
</logic:present>
