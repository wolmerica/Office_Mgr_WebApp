<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>   

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<script language="JavaScript" type="text/javascript" >

function setVISBackOrderQty(obj)  {
  var rowName = obj.name;
  var rowBegin = rowName.indexOf("[") + 1;

  rowName = rowName.substr(rowBegin,rowName.length);

  var rowEnd = rowName.indexOf("]");
  rowName = rowName.substr(0,rowEnd);

  var idx = parseInt(rowName);

  var row = (idx * 8) + 1;
  orderObj = 'document.vendorInvoiceServiceHeadForm.elements[row]';
  orderObj = eval(orderObj);
  if (orderObj == null || orderObj == undefined) {
    alert('unable to find orderQty object');
  }
  var orderQty = parseInt(orderObj.value);

  row = row + 1;
  receiveObj = 'document.vendorInvoiceServiceHeadForm.elements[row]';
  receiveObj = eval(receiveObj);
  if (receiveObj == null || receiveObj == undefined) {
    alert('unable to find receiveQty object');
  }
  var receiveQty = parseInt(receiveObj.value);

  row = row + 1;
  backorderObj = 'document.vendorInvoiceServiceHeadForm.elements[row]';
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
  String attAction = "/VendorInvoiceServiceEdit.do";
  
  boolean attDisableSave = false;  
  boolean attDisableEdit = true;
  boolean attDisableCommentEdit = false;
  
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }
  boolean attDisableForCredit = attDisableEdit;  
  boolean attServiceTaxReadOnly = true;
  if (!(attDisableEdit)) {
    attServiceTaxReadOnly = false;    
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
                
<logic:equal name="vendorInvoiceServiceHeadForm" property="creditId" value="true">
<% 
  attDisableForCredit = true; 
%>        
</logic:equal>    

<logic:notEqual name="vendorInvoiceServiceHeadForm" property="permissionStatus" value="LOCKED"> 
<bean:message key="app.readonly"/>
  <logic:notEqual name="vendorInvoiceServiceHeadForm" property="permissionStatus" value="READONLY">
    [<bean:write name="vendorInvoiceServiceHeadForm" property="permissionStatus" />]
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
        <input type="submit" value="<bean:message key="app.vendorinvoiceservice.back" />">
      </form>            
    </td>
  </tr>
  <tr>
    <html:form action="<%=attAction%>"> 
    <input type="hidden" name="key" value="<%=attKey%>" >       
    <td colspan=2>
      <strong><bean:message key="app.vendorinvoiceservice.invoiceNumber" />:</strong>
      <bean:write name="vendorInvoiceServiceHeadForm" property="invoiceNumber" />      
    </td>
    <td colspan=3>
      <strong><bean:message key="app.vendorinvoiceservice.vendorName" />:</strong>
      <bean:write name="vendorInvoiceServiceHeadForm" property="vendorName" />
    </td>
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>     
  <tr align="left">
    <th><bean:message key="app.vendorinvoiceservice.serviceName" /></th>
    <th><bean:message key="app.vendorinvoiceservice.billBy" /></th>  
    <logic:equal name="vendorInvoiceServiceHeadForm" property="creditId" value="false">
      <th><bean:message key="app.vendorinvoiceservice.orderQty" /></th>
      <th><bean:message key="app.vendorinvoiceservice.receiveQty" /></th>
      <th><bean:message key="app.vendorinvoiceservice.backOrderQty" /></th>
      <th><bean:message key="app.vendorinvoiceservice.firstCost" /></th>
      <th><bean:message key="app.vendorinvoiceservice.discountAmount" /></th>
    </logic:equal>
    <logic:equal name="vendorInvoiceServiceHeadForm" property="creditId" value="true">
      <th><bean:message key="app.vendorinvoiceservice.creditAvailableQty" /></th>
      <th><bean:message key="app.vendorinvoiceservice.creditQty" /></th>
      <th><bean:message key="app.vendorinvoiceservice.remainingQty" /></th>      
      <th><bean:message key="app.vendorinvoiceservice.firstCost" /></th>
      <th><bean:message key="app.vendorinvoiceservice.restockFee" /></th>
    </logic:equal>    
    <th><bean:message key="app.vendorinvoiceservice.extendTotal" /></th>
    <th><bean:message key="app.vendorinvoiceservice.serviceTaxId" /></th>        
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
<logic:notEqual name="vendorInvoiceServiceHeadForm" property="lastRecord" value="0">     
  <!-- iterate over the vendor invoice services -->     
  <logic:iterate id="vendorInvoiceServiceForm"
                 name="vendorInvoiceServiceHeadForm"
                 property="vendorInvoiceServiceForm"
                 scope="session"
                 type="com.wolmerica.vendorinvoiceservice.VendorInvoiceServiceForm">
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
      <bean:write name="vendorInvoiceServiceForm" property="serviceName" />
    </td>  
    <td>
      <bean:write name="vendorInvoiceServiceForm" property="priceTypeName" />
    </td>
    <td>
      <html:text indexed="true" name="vendorInvoiceServiceForm" property="orderQty" size="1" readonly="true" />
    </td>
    <td>   
      <html:text indexed="true" name="vendorInvoiceServiceForm" property="receiveQty" size="1" readonly="<%=attDisableEdit%>" onchange="setVISBackOrderQty(this)" />
      <html:errors property="<%=receiveQtyError%>" />
    </td>
    <td>
      <html:text indexed="true" name="vendorInvoiceServiceForm" property="backOrderQty" size="1" readonly="true" />    
      <html:errors property="<%=backOrderQtyError%>" />
    </td>
    <td>         
      <bean:message key="app.localeCurrency" />
      <html:text indexed="true" name="vendorInvoiceServiceForm" property="firstCost" size="3" readonly="<%=attDisableForCredit%>" />          
      <html:errors property="<%=firstCostError%>" />              
    </td>
    <td>         
      <bean:message key="app.localeCurrency" />
      <html:text indexed="true" name="vendorInvoiceServiceForm" property="variantAmount" size="2" readonly="<%=attDisableEdit%>" />          
      <html:errors property="<%=variantAmountError%>" />              
    </td>    
    <td align="right"> 
      <bean:message key="app.localeCurrency" />
      <bean:write name="vendorInvoiceServiceForm" property="extendCost" />
    </td>
    <td>
      <logic:equal name="vendorInvoiceServiceForm" property="enableServiceTaxId" value="false">
        <% attServiceTaxReadOnly = true; %>
      </logic:equal>
          
      <html:radio name="vendorInvoiceServiceForm" indexed="true" property="serviceTaxId" value="false" disabled="<%=attServiceTaxReadOnly%>" />No
      <html:radio name="vendorInvoiceServiceForm" indexed="true" property="serviceTaxId" value="true" disabled="<%=attServiceTaxReadOnly%>" />Yes    
    </td> 
    <logic:equal name="vendorInvoiceServiceHeadForm" property="activeId" value="true">    
      <logic:equal name="vendorInvoiceServiceHeadForm" property="creditId" value="true">   
        <% if (!(attDisableEdit)) { %>          
          <td>
            <a href="VendorInvoiceServiceDelete.do?viiKey=<bean:write name="vendorInvoiceServiceForm" property="key" />&key=<%=request.getParameter("key")%>"
               " onclick="return confirm('<bean:message key="app.vendorinvoiceservice.delete.message" />')" ><bean:message key="app.delete" /></a>
          </td>
        <% } %>
      </logic:equal>        
    </logic:equal>    
  </tr>
  <tr class="<%=currentColor %>" >
    <td></td>
    <td colspan="7">     
      <strong><bean:message key="app.vendorinvoiceservice.noteLine1" />:</strong>
      &nbsp;&nbsp;
      <html:text indexed="true" name="vendorInvoiceServiceForm" property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableCommentEdit%>" />
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
        <bean:message key="app.vendorinvoiceservice.invoiceTotal" />
      </strong>
    </td>       
    <td colspan="6">&nbsp;</td>
    <td align="right">       
      <strong>
        <bean:message key="app.localeCurrency" />
        <bean:write name="vendorInvoiceServiceHeadForm" property="invoiceTotal" />      
      </strong>
    </td>
    <td colspan="3" align="right">
      <html:submit value="Save Service Detail" disabled="<%=attDisableSave%>" />          
    </td>
  </tr>    
  </html:form>                   
</table>      