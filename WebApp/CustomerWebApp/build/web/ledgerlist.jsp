<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<%
  String currentColor = "ODD";
  int i = 0;
      
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
%>   
 
<logic:equal name="ledgerListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="ledgerListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>       
<!-- Non-Archived ledger search with Archive Option -->
    <logic:equal name="ledgerListHeadForm" property="mode" value="5">
      <td colspan = "7" align="center">            
        <h3>Invoices for Slip Number: <bean:write name="ledgerListHeadForm" property="slipNumber" /> </h3>
    </logic:equal>  
    </td>
  </tr>
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.ledger.clientName" /></th>
    <th align="right"><bean:message key="app.ledger.invoiceNumber" /></th>
    <th align="right"><bean:message key="app.ledger.invoiceTotal" /></th>
    <th align="right"><bean:message key="app.ledger.postStamp" /></th>
    <th align="right"><bean:message key="app.ledger.slipNumber" /></th>        
    <th>&nbsp;</th>
    <th>&nbsp;</th>
  </tr>     
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
    
<logic:notEqual name="ledgerListHeadForm" property="recordCount" value="0"> 
  <!-- iterate over the employees -->       
  <logic:iterate id="ledgerForm"
                 name="ledgerListHeadForm"
                 property="ledgerForm"
                 scope="session"
                 type="com.wolmerica.ledger.LedgerForm">
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
            <bean:write name="ledgerForm" property="clientName" />
	  </td>
	  <td align="right">
            <logic:notEqual name="ledgerForm" property="customerInvoiceKey" value="-1">
<!-- Ledger History by Date Range -->                          
              <logic:equal name="ledgerListHeadForm" property="mode" value="6">
                <bean:write name="ledgerForm" property="invoiceNumber" />
              </logic:equal>
<!-- Not Ledger History by Date Range -->              
              <logic:notEqual name="ledgerListHeadForm" property="mode" value="6">
                <a href="#" onclick="popup = putinpopup('PdfCustomerInvoice.do?presentationType=pdf&key=<bean:write name="ledgerForm"
	        property="customerInvoiceKey" />'); return false" target="_PDF"><bean:write name="ledgerForm" property="invoiceNumber" /></a>          
              </logic:notEqual>
	    </logic:notEqual>           
            <logic:equal name="ledgerForm" property="customerInvoiceKey" value="-1">    
              <bean:write name="ledgerForm" property="invoiceNumber" />
	    </logic:equal>
	  </td>
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="ledgerForm" property="invoiceTotal" />
	  </td>
	  <td align="right">
            <bean:write name="ledgerForm" property="postStamp" />
	  </td>
	  <td align="right">
            <logic:notEqual name="ledgerForm" property="slipNumber" value="">
              <a href="LedgerList.do?mode=5&slipNum=<bean:write name="ledgerForm"
	      property="slipNumber" />"><bean:write name="ledgerForm" property="slipNumber" /></a>
            </logic:notEqual>            
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
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='LedgerList.do?pageNo=<bean:write name="ledgerListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td>
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="ledgerListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="ledgerListHeadForm" property="recordCount" />      
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="ledgerListHeadForm" property="grandTotal" />
    </td>
    <td colspan="3" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='LedgerList.do?pageNo=<bean:write name="ledgerListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>
