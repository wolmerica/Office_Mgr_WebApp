<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" type="text/javascript"></script>

<%
  String attTitle = "app.vendorinvoice.editTitle";
  String attAction = "/VendorInvoiceEdit";
  String attDisableCreditButton = "";   
  String attDisablePostButton = "disabled";  

  boolean attDisableEdit = true;
  boolean attDisableCreditEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }  
  boolean attDisableDropListLookup = attDisableEdit;   
%> 

<logic:equal name="vendorInvoiceForm" property="creditId" value="false">
<% 
  attDisableCreditEdit = attDisableEdit; 
%>        
</logic:equal>   
<logic:equal name="vendorInvoiceForm" property="balanceAmount" value="0.00">
<% 
  attDisablePostButton = ""; 
%>    
</logic:equal>
<logic:equal name="vendorInvoiceForm" property="creditId" value="true">
  <%
    attDisableDropListLookup = true; 
  %>
</logic:equal>
<logic:notEqual name="vendorInvoiceForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="vendorInvoiceForm" property="permissionStatus" value="READONLY">
    [<bean:write name="vendorInvoiceForm" property="permissionStatus" />]
  </logic:notEqual>
  <%
  attDisableCreditButton = "disabled";   
  %>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <button type="button" onClick="window.location='PurchaseOrderGet.do?key=<bean:write name="vendorInvoiceForm" 
        property="purchaseOrderKey" />' "><bean:message key="app.vendorinvoice.backToOrder" /></button>          
    </td>
    <td colspan="2" align="right"> 
      <button type="button" onClick="window.location='VendorInvoiceServiceGet.do?key=<bean:write name="vendorInvoiceForm" 
        property="key" />' "><bean:message key="app.vendorinvoice.serviceDetail" /></button>    
    </td>
    <td align="right"> 
      <button type="button" onClick="window.location='VendorInvoiceItemGet.do?key=<bean:write name="vendorInvoiceForm" 
        property="key" />' "><bean:message key="app.vendorinvoice.itemDetail" /></button>    
    </td>
    
    <logic:notEqual name="vendorInvoiceForm" property="key" value="">    
    <td align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.vendorinvoice.id"/>&sourceKey=<bean:write name="vendorInvoiceForm"
                property="key" />&sourceName=<bean:write name="vendorInvoiceForm"
                property="invoiceNumber" />" >[<bean:write name="vendorInvoiceForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."> </a>
    </td>    
    </logic:notEqual>
  </tr>
  <tr>
    <td colspan="6">
      <hr>
    </td>
  </tr>

<html:form action="<%=attAction%>"> 
<logic:present name="vendorInvoiceForm" property="key" >
  <html:hidden property="key" />
</logic:present>
  <tr>
       <html:hidden property="purchaseOrderKey" />        
     <td><strong><bean:message key="app.vendorinvoice.invoiceNumber" />:</strong></td>
     <td>
       <html:text property="invoiceNumber" size="25" maxlength="30" readonly="<%=attDisableCreditEdit%>" />
       <html:errors property="invoiceNumber" />             
     </td>       
     <td><strong><bean:message key="app.vendorinvoice.purchaseOrderNumber" />:</strong></td>
     <td>
       <html:text property="purchaseOrderNumber" size="12" maxlength="10" readonly="true" />
       <html:errors property="purchaseOrderNumber" />
    </td>
  </tr>     
  <tr>
    <td><strong><bean:message key="app.vendorinvoice.invoiceDate" />:</strong></td>
    <td>
      <html:text property="invoiceDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableCreditEdit)) { %>             
        <a href="javascript:show_calendar('vendorInvoiceForm.invoiceDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the invoice date."></a>
      <% } %>
      <html:errors property="invoiceDate" />            
    </td>
    <td><strong><bean:message key="app.vendorinvoice.vendorName" />:</strong></td>
    <td>
      <html:text property="vendorName" size="35" maxlength="40" readonly="true" />
      <html:errors property="vendorName" />            
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorinvoice.invoiceDueDate" />:</strong></td>
    <td>
      <html:text property="invoiceDueDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableCreditEdit)) { %>            
        <a href="javascript:show_calendar('vendorInvoiceForm.invoiceDueDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the invoice due date."></a>
      <% } %>
      <html:errors property="invoiceDueDate" />             
    </td>   
      <html:hidden property="lineItemTotal" />
    <td><strong><bean:message key="app.vendorinvoice.lineItemTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="lineItemTotal" size="9" maxlength="10" readonly="true" />
      <html:errors property="lineItemTotal" />            
    </td>
  </tr> 
  <tr>
    <td><strong><bean:message key="app.vendorinvoice.salesTaxKey" />:</strong></td>
    <td>
      <html:select property="salesTaxKey" disabled="<%=attDisableDropListLookup%>">
      <!-- iterate over the sales tax keys -->       
        <logic:iterate id="taxMarkUpForm"
                    name="vendorInvoiceForm"
                    property="taxMarkUpForm"
                    scope="session"
                    type="com.wolmerica.taxmarkup.TaxMarkUpForm">         
          <% if (taxMarkUpForm.getName().contains("SALES")) { %>                        
            <html:option value="<%=taxMarkUpForm.getKey()%>" > <%=taxMarkUpForm.getDescription()%> </html:option>
          <% } %>            
        </logic:iterate>
      </html:select>
      <html:errors property="salesTaxKey" />
    </td>                 
    <td><strong><bean:message key="app.vendorinvoice.serviceTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="serviceTotal" size="9" maxlength="10" readonly="true" />
      <html:errors property="serviceTotal" />
    </td>
  </tr>
  <tr>       
    <td><strong><bean:message key="app.vendorinvoice.taxableTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="taxableTotal" size="9" maxlength="10" readonly="true" />
      <html:errors property="taxableTotal" />            
    </td>     
    <td><strong><bean:message key="app.vendorinvoice.subTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="subTotal" size="9" maxlength="10" readonly="true" />
      <html:errors property="subTotal" />
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.vendorinvoice.nonTaxableTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="nonTaxableTotal" size="9" maxlength="10" readonly="true" />
      <html:errors property="nonTaxableTotal" />
    </td> 
    <td><strong><bean:message key="app.vendorinvoice.salesTaxCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="salesTaxCost" size="9" maxlength="10" readonly="true" />
      <html:errors property="salesTaxCost" />
    </td>
  </tr>
  <tr>
    <logic:equal name="vendorInvoiceForm" property="serviceTaxRate" value="0.00">
      <td></td>
      <td></td>    
    </logic:equal>    
    <logic:notEqual name="vendorInvoiceForm" property="serviceTaxRate" value="0.00">  
      <td><strong><bean:message key="app.vendorinvoice.serviceTaxableTotal" />:</strong></td>
      <td>
        <bean:message key="app.localeCurrency" />
        <html:text property="serviceTaxableTotal" size="9" maxlength="10" readonly="true" />
        <html:errors property="serviceTaxableTotal" />
      </td> 
      <td><strong><bean:message key="app.vendorinvoice.serviceTaxCost" />:</strong></td>
      <td>
        <bean:message key="app.localeCurrency" />
        <html:text property="serviceTaxCost" size="9" maxlength="10" readonly="true" />
        <html:errors property="serviceTaxCost" />
      </td>
    </tr>
    <tr>
      <td><strong><bean:message key="app.vendorinvoice.serviceNonTaxableTotal" />:</strong></td>
      <td>
        <bean:message key="app.localeCurrency" />
        <html:text property="serviceNonTaxableTotal" size="9" maxlength="10" readonly="true" />
        <html:errors property="serviceNonTaxableTotal" />
      </td> 
    </logic:notEqual>
    
    <td><strong><bean:message key="app.vendorinvoice.packagingCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="packagingCost" size="9" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="packagingCost" />
    </td>
  </tr>
  <tr>
    <logic:equal name="vendorInvoiceForm" property="creditId" value="true">
      <td><strong><bean:message key="app.vendorinvoice.rmaNumber" />:</strong></td>
      <td>
        <html:text property="rmaNumber" size="25" maxlength="20" readonly="<%=attDisableEdit%>" />
        <html:errors property="rmaNumber" />            
      </td>            
    </logic:equal>      
    <logic:equal name="vendorInvoiceForm" property="creditId" value="false">
      <td></td>
      <td></td>
    </logic:equal>          
    <td><strong><bean:message key="app.vendorinvoice.freightCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="freightCost" size="9" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="freightCost" />            

      <logic:equal name="vendorInvoiceForm" property="creditId" value="true">    
        &nbsp;&nbsp;&nbsp;
        [<bean:write name="vendorInvoiceForm" property="logisticsCount" />]
        <a href="" onClick="popup = logisticspopup('LogisticsGet.do?sourceTypeKey=<bean:message key="app.vendorinvoice.id" />&sourceKey=<bean:write name="vendorInvoiceForm" 
          property="key" />&sourceName=<bean:write name="vendorInvoiceForm"
          property="invoiceNumber" />&disableEdit=<%=attDisableEdit%>', 'PopupPage'); return false" target="_blank"><bean:message key="app.vendorinvoice.shippingInfo" /></a>           
      </logic:equal>    
    </td>
  </tr>	  
  <tr>
    <td></td>
    <td></td>      
    <td><strong><bean:message key="app.vendorinvoice.miscellaneousCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="miscellaneousCost" size="9" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="miscellaneousCost" />            
    </td>
  </tr>  
  <tr>
    <td></td>
    <td></td>      
    <td><strong><bean:message key="app.vendorinvoice.invoiceTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="invoiceTotal" size="9" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="invoiceTotal" />            
    </td>
  </tr>   
  <tr>
    <td><strong><bean:message key="app.vendorinvoice.noteLine1" />:</strong></td>
    <td colspan="3">
      <html:text property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine1" />        
    </td>        
  </tr>  
  <tr>
    <td><strong><bean:message key="app.vendorinvoice.createUser" />:</strong></td>
    <td><bean:write name="vendorInvoiceForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.vendorinvoice.createStamp" />:</strong></td>
    <td><bean:write name="vendorInvoiceForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorinvoice.updateUser" />:</strong></td>
    <td><bean:write name="vendorInvoiceForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.vendorinvoice.updateStamp" />:</strong></td>
    <td><bean:write name="vendorInvoiceForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="5" align="right">
      <html:submit value="Save Vendor Invoice" disabled="<%=attDisableEdit%>" />         
      </html:form> 
    </td>      
  </tr>
  <tr>
    <logic:equal name="vendorInvoiceForm" property="allowCreditId" value="true">
      <td colspan="2" align="left"> 
        <form action="VendorInvoiceCreditEntry.do" name="vendorInvoiceCreditEntry" method="post">                                  
          <input type="hidden" name="key" value="<bean:write name="vendorInvoiceForm" property="key" />">
          <input type="hidden" name="creditKey" value="<bean:write name="vendorInvoiceForm" property="key" />">
          <input type="hidden" name="poKey" value="<bean:write name="vendorInvoiceForm" property="purchaseOrderKey"/>">
          <input type="submit" value="<bean:message key="app.vendorinvoice.credit" />" <%=attDisableCreditButton%> onclick="return confirm('<bean:message key="app.vendorinvoice.credit.message" />')" >
        </form>         
      </td>
    </logic:equal>
    <logic:equal name="vendorInvoiceForm" property="activeId" value="true">    
      <logic:equal name="vendorInvoiceForm" property="creditId" value="true">
        <logic:notEqual name="vendorInvoiceForm" property="invoiceTotal" value="0.00">
          <td colspan="2" align="left"> 
            <form action="StockItemReturnComplete.do" name="stockItemReturnComplete" method="post">                                  
              <input type="hidden" name="key" value="<bean:write name="vendorInvoiceForm" property="key" />">
              <input type="hidden" name="poKey" value="<bean:write name="vendorInvoiceForm" property="purchaseOrderKey"/>">            
              <input type="hidden" name="ciState" value="false">              
              <input type="submit" value="<bean:message key="app.vendorinvoice.postcredit" />" <%=attDisablePostButton%> onclick="return confirm('<bean:message key="app.vendorinvoice.return.message" />')" >
            </form>   
          </td>
        </logic:notEqual>
      </logic:equal>
    </logic:equal>
  </tr>
</table>