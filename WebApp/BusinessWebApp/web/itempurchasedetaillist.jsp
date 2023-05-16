<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script src="./js/date_picker.js" type="text/javascript"></script>

<%
  String currentColor = "ODD";
  int i = 0;
  boolean attOmitCost = false;  
  if (request.getParameter("omitCost") != null) {
     if (request.getParameter("omitCost").toString().compareToIgnoreCase("true") == 0) {
       attOmitCost = true;
     }
  }
  
%>   
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
 
<!-- Item Purchase History by vendor, customer, scenario, manufacturer and date range -->
    <td colspan = "8">
      <html:form action="ItemPurchaseDetailList.do">
        <html:hidden property="vendorKey" />
        <html:hidden property="vendorName" />
        <input type="hidden" name="omitCost">        
        <strong><bean:message key="app.itempurchase.vendor" />:</strong>
        <span class="ac_holder">
        <input id="vendorNameAC" size="30" maxlength="30" value="<bean:write name="itemPurchaseListHeadForm" property="vendorName" />" onFocus="javascript:
        var options = {
		script:'VendorLookUp.do?json=true&',
		varname:'nameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.itemPurchaseListHeadForm.vendorKey.value=obj.id; document.itemPurchaseListHeadForm.vendorName.value=obj.value; }
		};
		var json=new AutoComplete('vendorNameAC',options);return true;" value="" />
        </span>      
        &nbsp;&nbsp;&nbsp;&nbsp;          
        <html:hidden property="customerKey" />
        <html:hidden property="clientName" />
        <strong><bean:message key="app.itempurchase.customer" />:</strong>
        <span class="ac_holder">
        <input id="clientNameAC" size="30" maxlength="30" value="<bean:write name="itemPurchaseListHeadForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.itemPurchaseListHeadForm.customerKey.value=obj.id; document.itemPurchaseListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>      
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.itempurchase.mode" />:</strong>
        <select name="mode">
           <option value="1" <logic:equal name="itemPurchaseListHeadForm" property="mode" value="1">selected</logic:equal> >
           <bean:message key="app.itempurchase.resale" />
           <option value="2" <logic:equal name="itemPurchaseListHeadForm" property="mode" value="2">selected</logic:equal> >
           <bean:message key="app.itempurchase.stock" /> 
           <option value="3" <logic:equal name="itemPurchaseListHeadForm" property="mode" value="3">selected</logic:equal> >
           <bean:message key="app.itempurchase.officeUse" />
           <option value="4"<logic:equal name="itemPurchaseListHeadForm" property="mode" value="4">selected</logic:equal> >
           <bean:message key="app.itempurchase.officeLoss" />
        </select>
    </td>
  </tr>
  <tr>
    <td colspan = "7">    
        <strong><bean:message key="app.itempurchase.manufacturer" />:</strong>
        <html:hidden property="manufacturer" />
        <span class="ac_holder">
        <input id="manufactAC" size="15" maxlength="15" value="<bean:write name="itemPurchaseListHeadForm" property="manufacturer" />" onFocus="javascript:
        var options = {
		script:'ManufacturerLookUp.do?json=true&',
		varname:'lookUpNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.itemPurchaseListHeadForm.manufacturer.value=obj.value; }
		};
		var json=new AutoComplete('manufactAC',options);return true;" value="" />
        </span>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.itempurchase.dateRange" />:</strong>
        <html:text property="fromDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('itemPurchaseListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.itempurchase.toDate" />&nbsp;</strong>
        <html:text property="toDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('itemPurchaseListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;
        <input type="checkbox" name="omitCostCheckBox" onclick="document.itemPurchaseListHeadForm.omitCost.value=document.itemPurchaseListHeadForm.omitCostCheckBox.checked;"><bean:message key="app.itempurchase.omitCost" />
        &nbsp;&nbsp;&nbsp;        
        <input type="submit" value="<bean:message key="app.runIt" />" >
      </html:form>
      <script type="text/javascript">
          document.itemPurchaseListHeadForm.omitCostCheckBox.checked = <%=attOmitCost%>;
          document.itemPurchaseListHeadForm.omitCost.value = document.itemPurchaseListHeadForm.omitCostCheckBox.checked;
      </script>
      
    </td>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.itempurchase.invoiceDate" /></th>    
    <th><bean:message key="app.itempurchase.invoiceNumber" /></th>
    <th><bean:message key="app.itempurchase.brandName" /></th>
    <th><bean:message key="app.itempurchase.genericName" /></th>
    <th>
      <bean:message key="app.itempurchase.size" />/
      <bean:message key="app.itempurchase.sizeUnit" />
    </th>
    <th align="right"><bean:message key="app.itempurchase.orderQty" /></th>
    <th align="right">
      <% if (!attOmitCost) { %>
        <bean:message key="app.itempurchase.itemCost" />
      <% } %>
    </th>
    <th align="right">
      <% if (!attOmitCost) { %>
        <bean:message key="app.itempurchase.extendCost" />
      <% } %>        
    </th>        
    <th>&nbsp;</th>
    <th>&nbsp;</th>
  </tr>     
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
    
<logic:notEqual name="itemPurchaseListHeadForm" property="recordCount" value="0"> 
  <!-- iterate over the employees -->       
  <logic:iterate id="itemPurchaseForm"
                 name="itemPurchaseListHeadForm"
                 property="itemPurchaseForm"
                 scope="session"
                 type="com.wolmerica.itempurchasereport.ItemPurchaseForm">
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
            <bean:write name="itemPurchaseForm" property="invoiceDate" />
	  </td>
	  <td>
            <a href="VendorInvoiceGet.do?key=<bean:write name="itemPurchaseForm"
	      property="invoiceKey" />"><bean:write name="itemPurchaseForm" property="invoiceNumber" /> </a>            
	  </td>          
	  <td>
            <bean:write name="itemPurchaseForm" property="brandName" />
	  </td>
	  <td>
            <bean:write name="itemPurchaseForm" property="genericName" />
	  </td>	  
          <td>
            <bean:write name="itemPurchaseForm" property="size" />
            <bean:write name="itemPurchaseForm" property="sizeUnit" />
          </td>          
	  <td align="right">
            <bean:write name="itemPurchaseForm" property="orderQty" />
	  </td>                    
	  <td align="right">
            <% if (!attOmitCost) { %>
              <bean:message key="app.localeCurrency" />
              <bean:write name="itemPurchaseForm" property="theCost" />
            <% } %>            
	  </td>
	  <td align="right">
            <% if (!attOmitCost) { %>
              <bean:message key="app.localeCurrency" />
              <bean:write name="itemPurchaseForm" property="extendCost" />
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
    <td colspan="2"></td>
    <td>
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="itemPurchaseListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="itemPurchaseListHeadForm" property="recordCount" />      
    </td>
    <td colspan="4" align="center">
      <a href="#top">Top&nbsp;<img border="0" src="./images/arrowtop.gif" align="absmiddle" width="16" height="15"></a>
    </td>
    <td align="right">
      <% if (!attOmitCost) { %>        
        <bean:message key="app.localeCurrency" />
        <bean:write name="itemPurchaseListHeadForm" property="extendTotal" />
      <% } %>
    </td>
  </tr>
</table>
