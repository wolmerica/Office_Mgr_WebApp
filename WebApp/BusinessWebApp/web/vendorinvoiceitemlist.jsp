<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>   

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<script language="JavaScript" type="text/javascript" >

function setVIIBackOrderQty(obj)  {
  var rowName = obj.name;
  var rowBegin = rowName.indexOf("[") + 1;

  rowName = rowName.substr(rowBegin,rowName.length);

  var rowEnd = rowName.indexOf("]");
  rowName = rowName.substr(0,rowEnd);

  var idx = parseInt(rowName);

  var row = (idx * 9) + 1;
  orderObj = 'document.vendorInvoiceItemHeadForm.elements[row]';
  orderObj = eval(orderObj);

  if (orderObj == null || orderObj == undefined) {
    alert('unable to find orderQty object');
  }
  var orderQty = parseInt(orderObj.value);

  row = row + 1;
  receiveObj = 'document.vendorInvoiceItemHeadForm.elements[row]';
  receiveObj = eval(receiveObj);
  if (receiveObj == null || receiveObj == undefined) {
    alert('unable to find receiveQty object');
  }
  var receiveQty = parseInt(receiveObj.value);

  row = row + 1;
  backorderObj = 'document.vendorInvoiceItemHeadForm.elements[row]';
  backorderObj = eval(backorderObj);
  if (backorderObj == null || backorderObj == undefined) {
    alert('unable to find backorderQty object');
  }

  var backorderQty = (orderQty - receiveQty);
  backorderObj.value = backorderQty;

  if (backorderQty < 0) {
    alert('Receive quantity exceeds what was ordered.');
  }
}

</script>

<%
  String currentColor = "ODD";
  int i = -1;          
  String attAction = "/VendorInvoiceItemEdit.do";
  
  boolean attDisableSave = false;  
  boolean attDisableEdit = true;
  boolean attDisableCommentEdit = false;
  
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }
  boolean attDisableForCredit = attDisableEdit;  
  boolean attCarryFactorReadOnly = true;
  boolean attSalesTaxReadOnly = true;
  if (!(attDisableEdit)) {
    attCarryFactorReadOnly = false;
    attSalesTaxReadOnly = false;    
  }      
  String attKey = request.getParameter("key").toString();
  if (attKey == null) {
    attKey = request.getAttribute("key").toString();
  }
  
  int fldKey = 0;  
  String receiveQtyBase = "receiveQty";
  String backOrderQtyBase = "backOrderQty";  
  String variantAmountBase = "variantAmount";  
  String firstCostBase = "firstCost";
  String expirationDateBase = "expirationDate";
  String receiveQtyError;
  String backOrderQtyError;  
  String variantAmountError;
  String firstCostError;
  String expirationDateError;
%>
                
<logic:equal name="vendorInvoiceItemHeadForm" property="creditId" value="true">
<% 
  attDisableForCredit = true; 
%>        
</logic:equal>    

<logic:notEqual name="vendorInvoiceItemHeadForm" property="permissionStatus" value="LOCKED"> 
<bean:message key="app.readonly"/>
  <logic:notEqual name="vendorInvoiceItemHeadForm" property="permissionStatus" value="READONLY">
    [<bean:write name="vendorInvoiceItemHeadForm" property="permissionStatus" />]
  </logic:notEqual>
  <%
    attDisableCommentEdit = true;
    attDisableSave = true;
  %>
</logic:notEqual>
     
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="VendorInvoiceGet.do" name="vendorInvoiceGet" method="post">                                  
        <input type="hidden" name="key" value="<%=attKey%>">  
        <input type="submit" value="<bean:message key="app.vendorinvoiceitem.back" />">
      </form>            
    </td>
  </tr>
  <tr>
    <html:form action="<%=attAction%>"> 
    <input type="hidden" name="key" value="<%=attKey%>" >       
    <td colspan=2>
      <strong><bean:message key="app.vendorinvoiceitem.invoiceNumber" />:</strong>
      <bean:write name="vendorInvoiceItemHeadForm" property="invoiceNumber" />      
    </td>
    <td colspan=3>
      <strong><bean:message key="app.vendorinvoiceitem.vendorName" />:</strong>
      <bean:write name="vendorInvoiceItemHeadForm" property="vendorName" />
    </td>
  </tr>
  <tr>
    <td colspan="11">
      <hr>
    </td>
  </tr>     
  <tr align="left">
    <th><bean:message key="app.vendorinvoiceitem.brandName" /></th>
    <th>
      <bean:message key="app.vendorinvoiceitem.size" />/
      <bean:message key="app.vendorinvoiceitem.sizeUnit" />      
    </th>   
    <logic:equal name="vendorInvoiceItemHeadForm" property="creditId" value="false">
      <th><bean:message key="app.vendorinvoiceitem.orderQty" /></th>
      <th><bean:message key="app.vendorinvoiceitem.receiveQty" /></th>
      <th><bean:message key="app.vendorinvoiceitem.backOrderQty" /></th>
      <th><bean:message key="app.vendorinvoiceitem.firstCost" /></th>
      <th><bean:message key="app.vendorinvoiceitem.discountAmount" /></th>
    </logic:equal>
    <logic:equal name="vendorInvoiceItemHeadForm" property="creditId" value="true">
      <th><bean:message key="app.vendorinvoiceitem.creditAvailableQty" /></th>
      <th><bean:message key="app.vendorinvoiceitem.creditQty" /></th>
      <th><bean:message key="app.vendorinvoiceitem.remainingQty" /></th>  
      <th><bean:message key="app.vendorinvoiceitem.firstCost" /></th>
      <th><bean:message key="app.vendorinvoiceitem.restockFee" /></th>      
    </logic:equal>    
    <th><bean:message key="app.vendorinvoiceitem.extendTotal" /></th>
    <th><bean:message key="app.vendorinvoiceitem.expirationDate" /></th>            
    <th><bean:message key="app.vendorinvoiceitem.salesTaxId" /></th>        
  </tr>
  <tr>
    <td colspan="11">
      <hr>
    </td>
  </tr>
<logic:notEqual name="vendorInvoiceItemHeadForm" property="lastRecord" value="0">   
  <!-- iterate over the vendor invoice items -->     
  <logic:iterate id="vendorInvoiceItemForm"
                 name="vendorInvoiceItemHeadForm"
                 property="vendorInvoiceItemForm"
                 scope="session"
                 type="com.wolmerica.vendorinvoiceitem.VendorInvoiceItemForm">
  <%
    ++i;      
    if ( i % 2 == 0) { currentColor = "ODD"; }
    else { currentColor = "EVEN"; }
    receiveQtyError = receiveQtyBase + i;
    backOrderQtyError = backOrderQtyBase + i;    
    variantAmountError = variantAmountBase + i;    
    firstCostError = firstCostBase + i;
    expirationDateError = expirationDateBase + i;
  %>                 
                 
  <tr align="left" class="<%=currentColor %>" >
    <td>          
      <bean:write name="vendorInvoiceItemForm" property="brandName" />
    </td>  
    <td>
      <bean:write name="vendorInvoiceItemForm" property="size" />
      <bean:write name="vendorInvoiceItemForm" property="sizeUnit" />
    </td>
    <td>
      <html:text indexed="true" name="vendorInvoiceItemForm" property="orderQty" size="1" readonly="true" />
    </td>
    <td>   
      <html:text indexed="true" name="vendorInvoiceItemForm" property="receiveQty" size="1" readonly="<%=attDisableEdit%>" onchange="setVIIBackOrderQty(this)" />
      <html:errors property="<%=receiveQtyError%>" />
    </td>
    <td>
      <html:text indexed="true" name="vendorInvoiceItemForm" property="backOrderQty" size="1" readonly="true" />    
      <html:errors property="<%=backOrderQtyError%>" />
    </td>
    <td>         
      <bean:message key="app.localeCurrency" />
      <html:text indexed="true" name="vendorInvoiceItemForm" property="firstCost" size="3" readonly="<%=attDisableForCredit%>" />          
      <html:errors property="<%=firstCostError%>" />              
    </td>
    <td>         
      <bean:message key="app.localeCurrency" />
      <html:text indexed="true" name="vendorInvoiceItemForm" property="variantAmount" size="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="<%=variantAmountError%>" />              
    </td>    
    <td align="right"> 
      <bean:message key="app.localeCurrency" />
      <bean:write name="vendorInvoiceItemForm" property="extendCost" />
    </td>
    <td nowrap>
      <html:text indexed="true" name="vendorInvoiceItemForm" property="expirationDate" size="8" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) {
         fldKey = (i * 8) + 6; %>
        <a href="javascript:show_calendar('vendorInvoiceItemHeadForm.elements[<%=fldKey%>]');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select the expiration date."></a>
      <% } %>
      <html:errors property="<%=expirationDateError%>" />            
    </td>
    <td nowrap>
      <logic:equal name="vendorInvoiceItemForm" property="enableSalesTaxId" value="true">
        <% attSalesTaxReadOnly = attDisableEdit; %>
      </logic:equal>
      <html:radio name="vendorInvoiceItemForm" indexed="true" property="salesTaxId" value="false" disabled="<%=attSalesTaxReadOnly%>" />No
      <html:radio name="vendorInvoiceItemForm" indexed="true" property="salesTaxId" value="true" disabled="<%=attSalesTaxReadOnly%>" />Yes
    </td> 
    <logic:equal name="vendorInvoiceItemHeadForm" property="activeId" value="true">    
      <logic:equal name="vendorInvoiceItemHeadForm" property="creditId" value="true">   
        <% if (!(attDisableEdit)) { %>          
          <td>
            <a href="VendorInvoiceItemDelete.do?viiKey=<bean:write name="vendorInvoiceItemForm" property="key" />&key=<%=request.getParameter("key")%>"
                onclick="return confirm('<bean:message key="app.vendorinvoiceitem.delete.message" />')" ><bean:message key="app.delete" /></a>
          </td>
        <% } %>
      </logic:equal>        
    </logic:equal>    
  </tr>
  <tr class="<%=currentColor %>" >
    <td align="right">
      <logic:equal name="vendorInvoiceItemForm" property="licenseKeyId" value="true">
        <a href="" onClick="popup = licensepopup('LicenseGet.do?sourceTypeKey=<bean:message key="app.vendorinvoice.id" />&sourceKey=<bean:write name="vendorInvoiceItemForm"
          property="key" />&sourceName=<bean:write name="vendorInvoiceItemForm"
          property="brandName" />%20<bean:write name="vendorInvoiceItemForm"
          property="size" /><bean:write name="vendorInvoiceItemForm"
          property="sizeUnit" />&disableEdit=<%=attDisableEdit%>', 'PopupPage'); return false" target="_blank">[<bean:write name="vendorInvoiceItemForm" property="licenseKeyCount" />]<img src="./images/licensekey.gif" width="18" height="18" border="0" title="Click to view license keys."></a>
      </logic:equal>
    </td>
    <td colspan="7">     
      <strong><bean:message key="app.vendorinvoiceitem.noteLine1" />:</strong>
      &nbsp;&nbsp;
      <html:text indexed="true" name="vendorInvoiceItemForm" property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableCommentEdit%>" />
    </td>
  </tr>
  </logic:iterate>
</logic:notEqual>
  <%
    ++i;      
    if ( i % 2 == 0) { currentColor = "ODD"; } 
    else { currentColor = "EVEN"; }    
  %>
  <tr align="left" class="<%=currentColor %>" >
    <td>          
      <strong>
        <bean:message key="app.vendorinvoiceitem.invoiceTotal" />
      </strong>
    </td>       
    <td colspan="6">&nbsp;</td>
    <td align="right">       
      <strong>
        <bean:message key="app.localeCurrency" />
        <bean:write name="vendorInvoiceItemHeadForm" property="invoiceTotal" />      
      </strong>
    </td>
    <td colspan="3" align="right">
      <html:submit value="Save Item Detail" disabled="<%=attDisableSave%>" />          
    </td>
  </tr>    
  </html:form>                   
</table>      