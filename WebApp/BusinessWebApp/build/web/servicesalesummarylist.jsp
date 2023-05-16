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
 
<!-- ServiceSale History Summary by Date Range -->
    <td colspan = "7">
      <html:form action="ServiceSaleSummaryList.do">
        <html:hidden property="customerKey" />
        <html:hidden property="clientName" />
        <strong><bean:message key="app.servicesale.customer" />:</strong>
        <span class="ac_holder">
        <input id="clientNameAC" size="40" maxlength="40" value="<bean:write name="serviceSaleListHeadForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.serviceSaleListHeadForm.customerKey.value=obj.id; document.serviceSaleListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>
        <html:errors property="customerKey" />
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.servicesale.mode" />:</strong>
        <select name="mode">
           <option value="-1" <logic:equal name="serviceSaleListHeadForm" property="mode" value="-1">selected</logic:equal> >
           <bean:message key="app.servicesale.allActivity" />            
           <option value="0" <logic:equal name="serviceSaleListHeadForm" property="mode" value="0">selected</logic:equal> >
           <bean:message key="app.servicesale.directShip" />
           <option value="1" <logic:equal name="serviceSaleListHeadForm" property="mode" value="1">selected</logic:equal> >
           <bean:message key="app.servicesale.dropShip" /> 
           <option value="2" <logic:equal name="serviceSaleListHeadForm" property="mode" value="2">selected</logic:equal> >
           <bean:message key="app.servicesale.inventorySale" />
           <option value="3"<logic:equal name="serviceSaleListHeadForm" property="mode" value="3">selected</logic:equal> >
           <bean:message key="app.servicesale.serviceCredit" />
        </select>
    </td>
  </tr>
  <tr>
    <td colspan = "7">    
        <strong><bean:message key="app.servicesale.dateRange" />:</strong>
        <html:text property="fromDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('serviceSaleListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.servicesale.toDate" />&nbsp;</strong>
        <html:text property="toDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('serviceSaleListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </html:form>
    </td>
  </tr>
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.servicesale.invoiceMonthName" /></th>
    <th><bean:message key="app.servicesale.invoiceYear" /></th>    
    <th align="right"><bean:message key="app.servicesale.transactionCnt" /></th>       
    <th align="right"><bean:message key="app.servicesale.serviceTax" /></th>
    <th align="right"><bean:message key="app.servicesale.handlingCost" /></th>    
    <th align="right"><bean:message key="app.servicesale.serviceTotal" /></th>
    <th>&nbsp;</th>
    <th>&nbsp;</th>
  </tr>     
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
    
<logic:notEqual name="serviceSaleListHeadForm" property="recordCount" value="0"> 
  <!-- iterate over the employees -->       
  <logic:iterate id="serviceSaleForm"
                 name="serviceSaleListHeadForm"
                 property="serviceSaleForm"
                 scope="session"
                 type="com.wolmerica.servicesalereport.ServiceSaleForm">
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
            <bean:write name="serviceSaleForm" property="invoiceMonthName" />
	  </td>
	  <td>
            <bean:write name="serviceSaleForm" property="invoiceYear" />
	  </td>
	  <td align="right">
            <bean:write name="serviceSaleForm" property="orderQty" />
	  </td>           
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="serviceSaleForm" property="serviceTax" />
	  </td>                  
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="serviceSaleForm" property="handlingCost" />
	  </td>        
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="serviceSaleForm" property="serviceTotal" />
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="7">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="serviceSaleListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="serviceSaleListHeadForm" property="recordCount" />      
    </td>
    <td align="right">
      <bean:write name="serviceSaleListHeadForm" property="transactionTotal" />
    </td>      
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="serviceSaleListHeadForm" property="serviceTaxTotal" />
    </td>        
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="serviceSaleListHeadForm" property="handlingTotal" />
    </td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="serviceSaleListHeadForm" property="serviceTotal" />
    </td>
  </tr>
</table>
