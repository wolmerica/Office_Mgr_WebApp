<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>   

<script language="JavaScript" type="text/javascript" >

function setCIIRemainingQty(obj)  {
  var rowName = obj.name;
  var rowBegin = rowName.indexOf("[") + 1;

  rowName = rowName.substr(rowBegin,rowName.length);

  var rowEnd = rowName.indexOf("]");
  rowName = rowName.substr(0,rowEnd);

  var idx = parseInt(rowName);

  var row = (idx * 7) + 1;
  availableObj = 'document.customerInvoiceItemHeadForm.elements[row]';
  availableObj = eval(availableObj);
  if (availableObj == null || availableObj == undefined) {
    alert('unable to find availableQty object');
  }
  var availableQty = parseInt(availableObj.value);

  row = row + 1;
  orderObj = 'document.customerInvoiceItemHeadForm.elements[row]';
  orderObj = eval(orderObj);
  if (orderObj == null || orderObj == undefined) {
    alert('unable to find orderQty object');
  }
  var orderQty = parseInt(orderObj.value);

  row = row + 1;
  remainingObj = 'document.customerInvoiceItemHeadForm.elements[row]';
  remainingObj = eval(remainingObj);
  if (remainingObj == null || remainingObj == undefined) {
    alert('unable to find remainingQty object');
  }

  var remainingQty = (availableQty - orderQty);
  remainingObj.value = remainingQty;

  if (remainingQty < 0) {
    alert('Order quantity exceeds what was is available.');
  }

}

</script>


<%
  String currentColor = "ODD";
  int i = -1;     
  String attAction = "/CustomerInvoiceItemEdit.do";
  boolean attSalesTaxReadOnly = true;
  boolean attDisableSave = false;  
  boolean attDisableEdit = true;
  boolean attDisableCommentEdit = false;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }   
  String attKey = request.getParameter("key").toString();
  if (attKey == null) {
    attKey = request.getAttribute("key").toString();
  }
  String orderQtyBase = "orderQty";
  String remainingQtyBase = "remainingQty";
  String thePriceBase="thePrice";
  String orderQtyError;
  String remainingQtyError;
  String thePriceError;
  
  boolean attDisableAddItems = attDisableEdit;
  boolean attDisableEditQty = attDisableEdit;

  String itemFilterURL = "";
  if (request.getParameter("brandNameFilter") != null) 
    if (request.getParameter("brandNameFilter") != "")
      itemFilterURL = itemFilterURL + "&brandNameFilter=" + request.getParameter("brandNameFilter");
  if (request.getParameter("itemNumFilter") != null)
    if (request.getParameter("itemNumFilter") != "")
      itemFilterURL = itemFilterURL + "&itemNumFilter=" + request.getParameter("itemNumFilter");
  if (request.getParameter("genericNameFilter") != null)
    if (request.getParameter("genericNameFilter") != "")
      itemFilterURL = itemFilterURL + "&genericNameFilter=" + request.getParameter("genericNameFilter");
  if (request.getParameter("idPageNo") != null)
    itemFilterURL = itemFilterURL + "&idPageNo=" + request.getParameter("idPageNo");       
%>

<logic:equal name="customerInvoiceItemHeadForm" property="lastRecord" value="0"> 
  <%
     attDisableSave = true;     
  %>
</logic:equal>
<logic:equal name="customerInvoiceItemHeadForm" property="scenarioKey" value="0">
  <%
    attDisableAddItems = true;
    attDisableEditQty = true;
  %>
</logic:equal>
<logic:equal name="customerInvoiceItemHeadForm" property="scenarioKey" value="1">
  <%
    attDisableAddItems = true;
  %>
</logic:equal>
<logic:equal name="customerInvoiceItemHeadForm" property="scenarioKey" value="3">
  <%
    attDisableAddItems = true;
  %>
</logic:equal>  
<logic:equal name="customerInvoiceItemHeadForm" property="scenarioKey" value="4">
  <%
    attDisableAddItems = true;
    attDisableEditQty = true;
  %>  
</logic:equal>

<logic:equal name="customerInvoiceItemHeadForm" property="adjustmentId" value="true">
  <%
    attDisableAddItems = true;
    attDisableEditQty = true;
  %>
</logic:equal>

<logic:notEqual name="customerInvoiceItemHeadForm" property="permissionStatus" value="LOCKED"> 
<bean:message key="app.readonly"/>
  <logic:notEqual name="customerInvoiceItemHeadForm" property="permissionStatus" value="READONLY">
    [<bean:write name="customerInvoiceItemHeadForm" property="permissionStatus" />]
  </logic:notEqual>
  <%
    attDisableCommentEdit = true;
    attDisableSave = true;
  %>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td colspan="2">
        <form action="CustomerInvoiceGet.do" name="customerInvoiceGet" method="post">                                  
          <input type="hidden" name="key" value="<%=attKey%>">  
          <input type="submit" value="<bean:message key="app.customerinvoiceitem.backMessage" />">
        </form>            
      </td>        
    </tr>
    <tr>
      <td colspan="8">
        <strong><bean:message key="app.customerinvoiceitem.invoiceNumber" />:</strong>
        <bean:write name="customerInvoiceItemHeadForm" property="invoiceNumber" />      
      &nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.customerinvoiceitem.clientName" />:</strong>
        <bean:write name="customerInvoiceItemHeadForm" property="clientName" />
      </td>
      <td colspan="2" align="left">
      <% if (!(attDisableAddItems)) { %>            
        <form action="ItemDictionaryList.do" name="orderAdd" method="post">
          <input type="hidden" name="key" value="<%=request.getParameter("key").toString()%>" >
          <input type="hidden" name="idKey" value="<%=request.getParameter("key").toString()%>" >
          <input type="hidden" name="ctKey" value="<bean:write name="customerInvoiceItemHeadForm" property="customerTypeKey" />" >
        <% if (request.getParameter("brandNameFilter") != null) { %>
          <input type="hidden" name="brandNameFilter" value="<%=request.getParameter("brandNameFilter").toString()%>">
        <% } %>
        <% if (request.getParameter("itemNumFilter") != null) { %>
          <input type="hidden" name="itemNumFilter" value="<%=request.getParameter("itemNumFilter").toString()%>">
        <% } %>
        <% if (request.getParameter("genericNameFilter") != null) { %>
          <input type="hidden" name="genericNameFilter" value="<%=request.getParameter("genericNameFilter").toString()%>">
        <% } %>        
        <% if (request.getParameter("idPageNo") != null) { %>          
          <input type="hidden" name="idPageNo" value="<%=request.getParameter("idPageNo").toString()%>">
        <% } %>          
        <input type="submit" value="Add Item">
        </form>
      <% } else { %>
        <form action="#" name="orderAdd" method="post">
          <input type="submit" value="Add Item" disabled >
        </form>        
      <% } %>                 
      </td>
    </tr>
    <tr>
      <td colspan="11">
        <hr>
      </td>
    </tr>     
    <tr align="left">
      <th><bean:message key="app.customerinvoiceitem.itemNum" /></th>
      <th><bean:message key="app.customerinvoiceitem.brandName" /></th>
      <th>
        <bean:message key="app.customerinvoiceitem.size" />/
        <bean:message key="app.customerinvoiceitem.sizeUnit" />
      </th>
      <logic:equal name="customerInvoiceItemHeadForm" property="creditId" value="false">
        <th><bean:message key="app.customerinvoiceitem.availableQty" /></th>          
        <th><bean:message key="app.customerinvoiceitem.orderQty" /></th>
      </logic:equal>
      <logic:equal name="customerInvoiceItemHeadForm" property="creditId" value="true">          
        <th><bean:message key="app.customerinvoiceitem.creditAvailableQty" /></th>
        <th><bean:message key="app.customerinvoiceitem.creditQty" /></th>
      </logic:equal>
      <th><bean:message key="app.customerinvoiceitem.remainingQty" /></th>          
      <th><bean:message key="app.customerinvoiceitem.thePrice" /></th>
      <th><bean:message key="app.customerinvoiceitem.discount" /></th>      
      <th><bean:message key="app.customerinvoiceitem.extendTotal" /></th>              
      <th><bean:message key="app.customerinvoiceitem.salesTaxId" /></th> 
    </tr>
    <tr>
      <td colspan="11">
        <hr>
      </td>
    <tr>
    <html:form action="<%=attAction%>"> 
      <input type="hidden" name="key" value="<%=attKey%>" >       
<logic:notEqual name="customerInvoiceItemHeadForm" property="lastRecord" value="0"> 
    <!-- iterate over the customer invoice items --> 
    <logic:iterate id="customerInvoiceItemForm"
                   name="customerInvoiceItemHeadForm"
                   property="customerInvoiceItemForm"
                   scope="session"
                   type="com.wolmerica.customerinvoiceitem.CustomerInvoiceItemForm">
      <%
        ++i;      
        if ( i % 2 == 0) { currentColor = "ODD"; } 
        else { currentColor = "EVEN"; }    
        orderQtyError = orderQtyBase + i; 
        remainingQtyError = remainingQtyBase + i;        
        thePriceError = thePriceBase + i;         
      %>
       
      <tr align="left" class="<%=currentColor %>" >
        <td nowrap>
          <bean:write name="customerInvoiceItemForm" property="itemNum" />
        </td>
        <td nowrap>
          <bean:write name="customerInvoiceItemForm" property="brandName" />
        </td>       
        <td nowrap>
          <bean:write name="customerInvoiceItemForm" property="size" />
          <bean:write name="customerInvoiceItemForm" property="sizeUnit" />
        </td>
        <td nowrap>
          <html:text indexed="true" name="customerInvoiceItemForm" property="availableQty" size="2" maxlength="3" readonly="true" />
        </td>
        <td nowrap>
          <html:text indexed="true" name="customerInvoiceItemForm" property="orderQty" size="2" maxlength="3" readonly="<%=attDisableEditQty%>" onchange="setCIIRemainingQty(this)" />
          <html:errors property="<%=orderQtyError%>" />
        </td>
        <td nowrap>
          <html:text indexed="true" name="customerInvoiceItemForm" property="remainingQty" size="2" maxlength="3" readonly="true" />
          <html:errors property="<%=remainingQtyError%>" />
        </td>
        <td nowrap>
          <bean:message key="app.localeCurrency" />
          <html:text indexed="true" name="customerInvoiceItemForm" property="thePrice" size="5" maxlength="8" readonly="true" />
          <% if (!(attDisableEdit)) { %>
            <a href="blank" onclick="popup = pricingpopup('ItemDictionaryGet.do?row=<%=i%>&ctkey=<bean:write name="customerInvoiceItemHeadForm" property="customerTypeKey" />&ptkey=<bean:write name="customerInvoiceItemForm" property="priceTypeKey" />&key=<bean:write name="customerInvoiceItemForm" property="itemDictionaryKey" />', 'PopupPage'); return false" target="_blank"><img src="./images/prev.gif" width="16" height="16" border="0" title="Click Here to modify price."></a>
          <% } %>              
          <html:errors property="<%=thePriceError%>" />              
        </td>
        <td nowrap>
          <bean:message key="app.localeCurrency" />                  
          <bean:write name="customerInvoiceItemForm" property="discountAmount" />
        </td>        
        <td align="right" nowrap>
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceItemForm" property="extendPrice" />
        </td>
        <td nowrap>
          <logic:equal name="customerInvoiceItemForm" property="enableSalesTaxId" value="true">
            <% attSalesTaxReadOnly = attDisableEdit; %>
          </logic:equal>       
          <html:radio name="customerInvoiceItemForm" indexed="true" property="salesTaxId" value="false" disabled="<%=attSalesTaxReadOnly%>" />No
          <html:radio name="customerInvoiceItemForm" indexed="true" property="salesTaxId" value="true" disabled="<%=attSalesTaxReadOnly%>" />Yes    
        </td>
        <td nowrap>
        <% if (!(attDisableAddItems)) { %>                 
          <a href="CustomerInvoiceItemDelete.do?ciiKey=<bean:write name="customerInvoiceItemForm" 
            property="key" />&key=<%=request.getParameter("key")%><%=itemFilterURL%> " onclick="return confirm('<bean:message key="app.customerinvoiceitem.delete.message" />')" ><bean:message key="app.delete" /></a>
        <% } %>           
        </td>
      </tr>    
      <tr class="<%=currentColor %>" >
        <td colspan="2" align="right">
          <logic:equal name="customerInvoiceItemForm" property="licenseKeyId" value="true">
            <a href="" onClick="popup = licensepopup('LicenseOption.do?invoiceTypeKey=<bean:message key="app.customerinvoice.id" />&invoiceKey=<bean:write name="customerInvoiceItemForm"
              property="key" />&sourceName=<bean:write name="customerInvoiceItemForm"
              property="brandName" />%20<bean:write name="customerInvoiceItemForm"
              property="size" /><bean:write name="customerInvoiceItemForm"
              property="sizeUnit" />&disableEdit=<%=attDisableEdit%>', 'PopupPage'); return false" target="_blank">[<bean:write name="customerInvoiceItemForm" property="licenseKeyCount" />]<img src="./images/licensekey.gif" width="18" height="18" border="0" title="Click to assign license keys."></a>
          </logic:equal>
        </td>
        <td colspan="9">
          <strong><bean:message key="app.customerinvoiceitem.noteLine1" />:</strong>
          &nbsp;&nbsp;
          <html:text indexed="true" name="customerInvoiceItemForm" property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableCommentEdit%>"/>
        </td>
      </tr>  
    </logic:iterate>
  </logic:notEqual>     
 
  <%
    if (i == -1) { attDisableEdit=true; }
    ++i;      
    if ( i % 2 == 0) { currentColor = "ODD"; } 
    else { currentColor = "EVEN"; }    
  %>
  <tr align="left" class="<%=currentColor %>" >
    <td></td>
    <td align="right">          
      <strong>
        <bean:message key="app.customerinvoiceitem.invoiceTotal" />
      </strong>
    </td>       
    <td colspan = 6>&nbsp;</td>
    <td align="right">       
      <strong>
        <bean:message key="app.localeCurrency" />
        <bean:write name="customerInvoiceItemHeadForm" property="invoiceTotal" />      
      </strong>
    </td>
    <td align="right" colspan="2">
      <% if (request.getParameter("brandNameFilter") != null) { %>
        <input type="hidden" name="brandNameFilter" value="<%=request.getParameter("brandNameFilter").toString()%>">
      <% } %>
      <% if (request.getParameter("itemNumFilter") != null) { %>
        <input type="hidden" name="itemNumFilter" value="<%=request.getParameter("itemNumFilter").toString()%>">
      <% } %>
      <% if (request.getParameter("genericNameFilter") != null) { %>
        <input type="hidden" name="genericNameFilter" value="<%=request.getParameter("genericNameFilter").toString()%>">
      <% } %>        
      <% if (request.getParameter("idPageNo") != null) { %>          
        <input type="hidden" name="idPageNo" value="<%=request.getParameter("idPageNo").toString()%>">
      <% } %>          
      <html:submit value="Save Item Detail" disabled="<%=attDisableSave%>" />
    </td>
  </tr>    
  </html:form> 
</table>