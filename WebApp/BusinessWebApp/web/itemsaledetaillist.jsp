<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<%
  String currentColor = "ODD";
  int i = 0;
%>   
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
 
<!-- Item sale detail by vendor, customer, scenario, manufacturer and date range -->
    <td colspan = "8">
      <html:form action="ItemSaleDetailList.do">
        <html:hidden property="vendorKey" />
        <html:hidden property="vendorName" />
        <strong><bean:message key="app.itemsale.vendor" />:</strong>
        <span class="ac_holder">
        <input id="vendorNameAC" size="30" maxlength="30" value="<bean:write name="itemSaleListHeadForm" property="vendorName" />" onFocus="javascript:
        var options = {
		script:'VendorLookUp.do?json=true&',
		varname:'nameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.itemSaleListHeadForm.vendorKey.value=obj.id; document.itemSaleListHeadForm.vendorName.value=obj.value; }
		};
		var json=new AutoComplete('vendorNameAC',options);return true;" value="" />
        </span>      
        &nbsp;&nbsp;
        <html:hidden property="customerKey" />
        <html:hidden property="clientName" />
        <strong><bean:message key="app.itemsale.customer" />:</strong>
        <span class="ac_holder">
        <input id="clientNameAC" size="40" maxlength="40" value="<bean:write name="itemSaleListHeadForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.itemSaleListHeadForm.customerKey.value=obj.id; document.itemSaleListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>
        &nbsp;&nbsp;
        <strong><bean:message key="app.itemsale.mode" />:</strong>
        <select name="mode">
           <option value="-1" <logic:equal name="itemSaleListHeadForm" property="mode" value="-1">selected</logic:equal> >
           <bean:message key="app.itemsale.allActivity" />            
           <option value="0" <logic:equal name="itemSaleListHeadForm" property="mode" value="0">selected</logic:equal> >
           <bean:message key="app.itemsale.directShip" />
           <option value="1" <logic:equal name="itemSaleListHeadForm" property="mode" value="1">selected</logic:equal> >
           <bean:message key="app.itemsale.dropShip" /> 
           <option value="2" <logic:equal name="itemSaleListHeadForm" property="mode" value="2">selected</logic:equal> >
           <bean:message key="app.itemsale.inventorySale" />
           <option value="3"<logic:equal name="itemSaleListHeadForm" property="mode" value="3">selected</logic:equal> >
           <bean:message key="app.itemsale.itemCredit" />
        </select>
    </td>
  </tr>
  <tr>
    <td colspan = "8">    
        <strong><bean:message key="app.itemsale.manufacturer" />:</strong>
        <html:hidden property="manufacturer" />
        <span class="ac_holder">
        <input id="manufactAC" size="15" maxlength="15" value="<bean:write name="itemSaleListHeadForm" property="manufacturer" />" onFocus="javascript:
        var options = {
		script:'ManufacturerLookUp.do?json=true&',
		varname:'lookUpNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.itemSaleListHeadForm.manufacturer.value=obj.value; }
		};
		var json=new AutoComplete('manufactAC',options);return true;" value="" />
        </span>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.itemsale.dateRange" />:</strong>
        <html:text property="fromDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('itemSaleListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.itemsale.toDate" />&nbsp;</strong>
        <html:text property="toDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('itemSaleListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </html:form>
    </td>
  </tr>
  <tr>
    <td colspan="9">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.itemsale.invoiceDate" /></th>    
    <th><bean:message key="app.itemsale.invoiceNumber" /></th>
    <th><bean:message key="app.itemsale.brandName" /></th>
    <th><bean:message key="app.itemsale.genericName" /></th>
    <th>
      <bean:message key="app.itemsale.size" />/
      <bean:message key="app.itemsale.sizeUnit" />
    </th>
    <th align="right"><bean:message key="app.itemsale.orderQty" /></th>
    <th align="right"><bean:message key="app.itemsale.itemPrice" /></th>
    <th align="right"><bean:message key="app.itemsale.discountAmount" /></th>    
    <th align="right"><bean:message key="app.itemsale.itemTotal" /></th>        
    <th>&nbsp;</th>
    <th>&nbsp;</th>
  </tr>     
  <tr>
    <td colspan="9">
      <hr>
    </td>
  </tr>
    
<logic:notEqual name="itemSaleListHeadForm" property="recordCount" value="0"> 
  <!-- iterate over the Item Sales rows -->       
  <logic:iterate id="itemSaleForm"
                 name="itemSaleListHeadForm"
                 property="itemSaleForm"
                 scope="session"
                 type="com.wolmerica.itemsalereport.ItemSaleForm">
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
            <bean:write name="itemSaleForm" property="invoiceDate" />
	  </td>
	  <td>
            <a href="CustomerInvoiceGet.do?key=<bean:write name="itemSaleForm"
	      property="invoiceKey" />"><bean:write name="itemSaleForm" property="invoiceNumber" /> </a>          
	  </td>          
	  <td>
            <bean:write name="itemSaleForm" property="brandName" />
	  </td>
	  <td>
            <bean:write name="itemSaleForm" property="genericName" />
	  </td>	  
          <td>
            <bean:write name="itemSaleForm" property="size" />
            <bean:write name="itemSaleForm" property="sizeUnit" />
          </td>          
	  <td align="right">
            <bean:write name="itemSaleForm" property="orderQty" />
	  </td>                    
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="itemSaleForm" property="thePrice" />
	  </td>
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="itemSaleForm" property="discountAmount" />
	  </td>          
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="itemSaleForm" property="itemTotal" />
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="9">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2"></td>
    <td align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="itemSaleListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="itemSaleListHeadForm" property="recordCount" />      
    </td>
    <td colspan="5" align="center">
      <a href="#top">Top&nbsp;<img border="0" src="./images/arrowtop.gif" align="absmiddle" width="16" height="15"></a>
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="itemSaleListHeadForm" property="itemTotal" />
    </td>
  </tr>
</table>
