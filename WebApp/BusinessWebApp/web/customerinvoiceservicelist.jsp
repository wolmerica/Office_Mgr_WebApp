<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>   

<script language="JavaScript" type="text/javascript" >

function setCISRemainingQty(obj)  {
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
  String attAction = "/CustomerInvoiceServiceEdit.do";
  boolean attServiceTaxReadOnly = true;
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
  
  boolean attDisableAddServices = attDisableEdit;
  boolean attDisableEditQty = attDisableEdit;

  String serviceFilterURL = "";
  if (request.getParameter("serviceNameFilter") != null) 
    if (request.getParameter("serviceNameFilter") != "")
      serviceFilterURL = serviceFilterURL + "&serviceNameFilter=" + request.getParameter("serviceNameFilter");
  if (request.getParameter("serviceNumFilter") != null)
    if (request.getParameter("serviceNumFilter") != "")
      serviceFilterURL = serviceFilterURL + "&serviceNumFilter=" + request.getParameter("serviceNumFilter");
   if (request.getParameter("categoryNameFilter") != null)
    if (request.getParameter("categoryNameFilter") != "")
      serviceFilterURL = serviceFilterURL + "&categoryNameFilter=" + request.getParameter("categoryNameFilter");
  if (request.getParameter("sdPageNo") != null)
    serviceFilterURL = serviceFilterURL + "&sdPageNo=" + request.getParameter("sdPageNo");       
%>

<logic:equal name="customerInvoiceServiceHeadForm" property="lastRecord" value="0"> 
  <%
     attDisableSave = true;     
  %>
</logic:equal>
<logic:equal name="customerInvoiceServiceHeadForm" property="scenarioKey" value="0">
  <%
    attDisableAddServices = true;
    attDisableEditQty = true;
  %>
</logic:equal>
<logic:equal name="customerInvoiceServiceHeadForm" property="scenarioKey" value="1">
  <%
    attDisableAddServices = true;
  %>
</logic:equal>
<logic:equal name="customerInvoiceServiceHeadForm" property="scenarioKey" value="3">
  <%
    attDisableAddServices = true;
  %>
</logic:equal>  
<logic:equal name="customerInvoiceServiceHeadForm" property="scenarioKey" value="4">
  <%
    attDisableAddServices = true;
    attDisableEditQty = true;
  %>  
</logic:equal>

<logic:notEqual name="customerInvoiceServiceHeadForm" property="permissionStatus" value="LOCKED"> 
<bean:message key="app.readonly"/>
  <logic:notEqual name="customerInvoiceServiceHeadForm" property="permissionStatus" value="READONLY">
    [<bean:write name="customerInvoiceServiceHeadForm" property="permissionStatus" />]
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
        <input type="submit" value="<bean:message key="app.customerinvoiceservice.backMessage" />">
      </form>            
    </td>        
  </tr>
  <tr>
    <td colspan="8">
      <strong><bean:message key="app.customerinvoiceservice.invoiceNumber" />:</strong>
      <bean:write name="customerInvoiceServiceHeadForm" property="invoiceNumber" />      
      &nbsp;&nbsp;&nbsp;
      <strong><bean:message key="app.customerinvoiceservice.clientName" />:</strong>
      <bean:write name="customerInvoiceServiceHeadForm" property="clientName" />
    </td>
    <td colspan="2" align="right">
    <% if (!(attDisableAddServices)) { %>            
      <form action="ServiceDictionaryList.do" name="orderAdd" method="post">
        <input type="hidden" name="key" value="<%=request.getParameter("key").toString()%>" >
        <input type="hidden" name="ddKey" value="<%=request.getParameter("key").toString()%>" >
        <input type="hidden" name="ctKey" value="<bean:write name="customerInvoiceServiceHeadForm" property="customerTypeKey" />" >
      <% if (request.getParameter("serviceNameFilter") != null) { %>
        <input type="hidden" name="serviceNameFilter" value="<%=request.getParameter("serviceNameFilter").toString()%>">
      <% } %>
      <% if (request.getParameter("serviceNumFilter") != null) { %>
        <input type="hidden" name="serviceNumFilter" value="<%=request.getParameter("serviceNumFilter").toString()%>">
      <% } %>
      <% if (request.getParameter("categoryNameFilter") != null) { %>
        <input type="hidden" name="categoryNameFilter" value="<%=request.getParameter("categoryNameFilter").toString()%>">
      <% } %>        
      <% if (request.getParameter("sdPageNo") != null) { %>          
        <input type="hidden" name="sdPageNo" value="<%=request.getParameter("sdPageNo").toString()%>">
      <% } %>          
      <input type="submit" value="Add Service">
      </form>
    <% } else { %>
      <form action="#" name="orderAdd" method="post">
        <input type="submit" value="Add Service" disabled >
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
    <th><bean:message key="app.customerinvoiceservice.serviceNum" /></th>
    <th><bean:message key="app.customerinvoiceservice.serviceName" /></th>
    <th>
      <bean:message key="app.customerinvoiceservice.billBy" />
    </th>
    <logic:equal name="customerInvoiceServiceHeadForm" property="creditId" value="false">
      <th><bean:message key="app.customerinvoiceservice.availableQty" /></th>          
      <th><bean:message key="app.customerinvoiceservice.orderQty" /></th>
    </logic:equal>
    <logic:equal name="customerInvoiceServiceHeadForm" property="creditId" value="true">          
      <th><bean:message key="app.customerinvoiceservice.creditAvailableQty" /></th>
      <th><bean:message key="app.customerinvoiceservice.creditQty" /></th>
    </logic:equal>
    <th><bean:message key="app.customerinvoiceservice.remainingQty" /></th>          
    <th><bean:message key="app.customerinvoiceservice.thePrice" /></th>
    <th><bean:message key="app.customerinvoiceservice.discount" /></th>      
    <th><bean:message key="app.customerinvoiceservice.extendTotal" /></th>              
    <th><bean:message key="app.customerinvoiceservice.serviceTaxId" /></th> 
  </tr>
  <tr>
    <td colspan="11">
      <hr>
    </td>
  <tr>
<html:form action="<%=attAction%>"> 
  <input type="hidden" name="key" value="<%=attKey%>" >
<logic:notEqual name="customerInvoiceServiceHeadForm" property="lastRecord" value="0"> 
    <!-- iterate over the customer invoice services --> 
    <logic:iterate id="customerInvoiceServiceForm"
                   name="customerInvoiceServiceHeadForm"
                   property="customerInvoiceServiceForm"
                   scope="session"
                   type="com.wolmerica.customerinvoiceservice.CustomerInvoiceServiceForm">
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
          <bean:write name="customerInvoiceServiceForm" property="serviceNum" />
        </td>
        <td nowrap>
          <bean:write name="customerInvoiceServiceForm" property="serviceName" />
        </td>       
        <td nowrap>
          <bean:write name="customerInvoiceServiceForm" property="priceTypeName" />
        </td>
        <td nowrap>
          <html:text indexed="true" name="customerInvoiceServiceForm" property="availableQty" size="2" maxlength="3" readonly="true" />
        </td>
        <td nowrap>
          <html:text indexed="true" name="customerInvoiceServiceForm" property="orderQty" size="2" maxlength="3" readonly="<%=attDisableEditQty%>" onchange="setCISRemainingQty(this)" />
          <html:errors property="<%=orderQtyError%>" />             
        </td>
        <td nowrap>
          <html:text indexed="true" name="customerInvoiceServiceForm" property="remainingQty" size="2" maxlength="3" readonly="true" />
          <html:errors property="<%=remainingQtyError%>" />
        </td>
        <td nowrap>
          <bean:message key="app.localeCurrency" />
          <html:text indexed="true" name="customerInvoiceServiceForm" property="thePrice" size="5" maxlength="8" readonly="true" />
          <% if (!(attDisableEdit)) { %>
            <a href="blank" onclick="popup = pricingpopup('ServiceDictionaryGet.do?row=<%=i%>&ctkey=<bean:write name="customerInvoiceServiceHeadForm" property="customerTypeKey" />&ptkey=<bean:write name="customerInvoiceServiceForm" property="priceTypeKey" />&key=<bean:write name="customerInvoiceServiceForm" property="serviceDictionaryKey" />'); return false" target="_blank"><img src="./images/prev.gif" width="16" height="16" border="0" title="Click here to modify price."></a>
          <% } %>              
          <html:errors property="<%=thePriceError%>" />              
        </td>
        <td nowrap>
          <bean:message key="app.localeCurrency" />                  
          <bean:write name="customerInvoiceServiceForm" property="discountAmount" />
        </td>        
        <td align="right" nowrap>
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceServiceForm" property="extendPrice" />
        </td>
        <td nowrap>
          <logic:equal name="customerInvoiceServiceForm" property="enableServiceTaxId" value="true">
            <% attServiceTaxReadOnly = attDisableEdit; %>
          </logic:equal>       
          <html:radio name="customerInvoiceServiceForm" indexed="true" property="serviceTaxId" value="false" disabled="<%=attServiceTaxReadOnly%>" />No
          <html:radio name="customerInvoiceServiceForm" indexed="true" property="serviceTaxId" value="true" disabled="<%=attServiceTaxReadOnly%>" />Yes    
        </td>
        <td nowrap>
        <% if (!(attDisableAddServices)) { %>                 
          <a href="CustomerInvoiceServiceDelete.do?cisKey=<bean:write name="customerInvoiceServiceForm" 
            property="key" />&key=<%=request.getParameter("key")%><%=serviceFilterURL%> " onclick="return confirm('<bean:message key="app.customerinvoiceservice.delete.message" />')" ><bean:message key="app.delete" /></a>
        <% } %>           
        </td>
      </tr>    
      <tr class="<%=currentColor %>" >
        <td colspan="2"></td>
        <td colspan="9">
          <strong><bean:message key="app.customerinvoiceservice.noteLine1" />:</strong>
          &nbsp;&nbsp;
          <html:text indexed="true" name="customerInvoiceServiceForm" property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableCommentEdit%>"/>
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
        <bean:message key="app.customerinvoiceservice.invoiceTotal" />
      </strong>
    </td>       
    <td colspan = 6>&nbsp;</td>
    <td align="right">       
       <strong>
         <bean:message key="app.localeCurrency" />
         <bean:write name="customerInvoiceServiceHeadForm" property="invoiceTotal" />      
       </strong>
    </td>
    <td align="right" colspan="2">
      <% if (request.getParameter("serviceNameFilter") != null) { %>
        <input type="hidden" name="serviceNameFilter" value="<%=request.getParameter("serviceNameFilter").toString()%>">
      <% } %>
      <% if (request.getParameter("serviceNumFilter") != null) { %>
        <input type="hidden" name="serviceNumFilter" value="<%=request.getParameter("serviceNumFilter").toString()%>">
      <% } %>
      <% if (request.getParameter("categoryNameFilter") != null) { %>
        <input type="hidden" name="categoryNameFilter" value="<%=request.getParameter("categoryNameFilter").toString()%>">
      <% } %>        
      <% if (request.getParameter("sdPageNo") != null) { %>          
        <input type="hidden" name="sdPageNo" value="<%=request.getParameter("sdPageNo").toString()%>">
      <% } %>          
      <html:submit value="Save Service Detail" disabled="<%=attDisableSave%>" />
    </td>
  </tr>    
  </html:form>        
  </table>