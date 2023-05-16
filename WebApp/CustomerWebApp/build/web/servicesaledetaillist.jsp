<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<%
  String currentColor = "ODD";
  int i = 0;
  
  boolean attDisableCustomerLookUp = true;
  if (request.getSession().getAttribute("MULTIACCT") != null) {
     if (request.getSession().getAttribute("MULTIACCT").toString().compareToIgnoreCase("true") == 0) {
       attDisableCustomerLookUp = false;
     }
  }   
      
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
%>   
 
<logic:equal name="serviceSaleListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="serviceSaleListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
 
<!-- Service Sales History by Date Range -->
    <td colspan = "8">
      <form action="ServiceSaleDetailList.do" name="serviceSaleListHeadForm" method="post">
        <input type="hidden" name="mode" value="-1">          
        <input type="hidden" name="customerKeyFilter" value="<bean:write name="serviceSaleListHeadForm" property="customerKey" />">
        <strong><bean:message key="app.activityFor" />:</strong>
        <input type="text" name="acctName" value="<bean:write name="serviceSaleListHeadForm" property="acctName" />" size="35" maxlength="35" readonly="true">
        <% if (!(attDisableCustomerLookUp)) { %>
             <a href="blank" onclick="popup = putinpopup('CustomerLookUp.do'); return false" target="_blank"><img src="./images/prev.gif" width="16" height="16" border="0" alt="Click here to pick an account."></a>            
        <% } %>        
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.servicesale.dateRange" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="serviceSaleListHeadForm" property="fromDate" />" size="10" maxlength="10" readonly="true" >
        <a href="javascript:show_calendar('serviceSaleListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <strong>&nbsp;<bean:message key="app.servicesale.toDate" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="serviceSaleListHeadForm" property="toDate" />" size="10" maxlength="10" readonly="true" >
        <a href="javascript:show_calendar('serviceSaleListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </form>                
    </td>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.servicesale.invoiceDate" /></th>    
    <th><bean:message key="app.servicesale.invoiceNumber" /></th>
    <th><bean:message key="app.servicesale.serviceName" /></th>
    <th><bean:message key="app.servicesale.priceTypeName" /></th>
    <th align="right"><bean:message key="app.servicesale.orderQty" /></th>
    <th align="right"><bean:message key="app.servicesale.servicePrice" /></th>
    <th align="right"><bean:message key="app.servicesale.discountAmount" /></th>    
    <th align="right"><bean:message key="app.servicesale.serviceTotal" /></th>        
    <th>&nbsp;</th>
    <th>&nbsp;</th>
  </tr>     
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
    
<logic:notEqual name="serviceSaleListHeadForm" property="recordCount" value="0"> 
  <!-- iterate over the Service Sales rows -->       
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
            <bean:write name="serviceSaleForm" property="invoiceDate" />
	  </td>
	  <td>
	    <bean:write name="serviceSaleForm" property="invoiceNumber" />
	  </td>          
	  <td>
            <bean:write name="serviceSaleForm" property="serviceName" />
	  </td>
          <td><bean:write name="serviceSaleForm" property="priceTypeName" /></td>          
	  <td align="right">
            <bean:write name="serviceSaleForm" property="orderQty" />
	  </td>                    
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="serviceSaleForm" property="thePrice" />
	  </td>
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="serviceSaleForm" property="discountAmount" />
	  </td>          
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="serviceSaleForm" property="serviceTotal" />
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
    <td align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="serviceSaleListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="serviceSaleListHeadForm" property="recordCount" />      
    </td>
    <td colspan="4" align="center">
      <a href="#top">Top&nbsp;<img border="0" src="./images/arrowtop.gif" align="absmiddle" width="16" height="15"></a>
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="serviceSaleListHeadForm" property="serviceTotal" />
    </td>
  </tr>
</table>
