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
 
<logic:equal name="itemSaleListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="itemSaleListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
 
<!-- ItemSale History Summary by Date Range -->
    <td colspan = "7">
      <form action="ItemSaleSummaryList.do" name="itemSaleListHeadForm" method="post">
        <input type="hidden" name="mode" value="-1">          
        <input type="hidden" name="customerKeyFilter" value="<bean:write name="itemSaleListHeadForm" property="customerKey" />">
        <strong><bean:message key="app.activityFor" />:</strong>
        <input type="text" name="acctName" value="<bean:write name="itemSaleListHeadForm" property="acctName" />" size="35" maxlength="35" readonly="true">
        <% if (!(attDisableCustomerLookUp)) { %>
          <a href="blank" onclick="popup = putinpopup('CustomerLookUp.do'); return false" target="_blank"><img src="./images/prev.gif" width="16" height="16" border="0" alt="Click here to pick an account."></a>            
        <% } %>        
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.itemsale.dateRange" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="itemSaleListHeadForm" property="fromDate" />" size="10" maxlength="10" readonly="true" >
        <a href="javascript:show_calendar('itemSaleListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <strong>&nbsp;<bean:message key="app.itemsale.toDate" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="itemSaleListHeadForm" property="toDate" />" size="10" maxlength="10" readonly="true" >
        <a href="javascript:show_calendar('itemSaleListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </form>                
    </td>
  </tr>
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.itemsale.invoiceMonthName" /></th>  
    <th><bean:message key="app.itemsale.invoiceYear" /></th>    
    <th align="right"><bean:message key="app.itemsale.transactionCnt" /></th>       
    <th align="right"><bean:message key="app.itemsale.salesTax" /></th>
    <th align="right"><bean:message key="app.itemsale.handlingCost" /></th>    
    <th align="right"><bean:message key="app.itemsale.itemTotal" /></th>
    <th>&nbsp;</th>
    <th>&nbsp;</th>
  </tr>     
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
    
<logic:notEqual name="itemSaleListHeadForm" property="recordCount" value="0"> 
  <!-- iterate over the employees -->       
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
            <bean:write name="itemSaleForm" property="invoiceMonthName" />
	  </td>
	  <td>
            <bean:write name="itemSaleForm" property="invoiceYear" />
	  </td>
	  <td align="right">
            <bean:write name="itemSaleForm" property="orderQty" />
	  </td>           
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="itemSaleForm" property="salesTax" />
	  </td>                  
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="itemSaleForm" property="handlingCost" />
	  </td>        
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="itemSaleForm" property="itemTotal" />
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
      <bean:write name="itemSaleListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="itemSaleListHeadForm" property="recordCount" />      
    </td>
    <td align="right">
      <bean:write name="itemSaleListHeadForm" property="transactionTotal" />
    </td>      
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="itemSaleListHeadForm" property="salesTaxTotal" />
    </td>        
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="itemSaleListHeadForm" property="handlingTotal" />
    </td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="itemSaleListHeadForm" property="itemTotal" />
    </td>
  </tr>
</table>
