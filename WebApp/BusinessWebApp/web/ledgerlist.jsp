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
<!-- Post Daily Ledger Page -->
    <logic:equal name="ledgerListHeadForm" property="mode" value="1">
<!--             <td colspan="1"> 
               <form action="LedgerEntry.do" name="ledgerEntry" method="post">        
                  <input type="hidden" name="mode" value="<bean:write name="ledgerListHeadForm" property="mode" />">                                
                  <input type="submit" value="Add Ledger">
               </form>        
             </td>
-->          
      <td colspan = "6">          
        <form action="LedgerPost.do" name="ledgerListHeadForm" method="post">        
          <input type="hidden" name="mode" value="<bean:write name="ledgerListHeadForm" property="mode" />">                                
          <input type="submit" value="Post Daily Ledger" onclick="return confirm('<bean:message key="app.ledger.post.message" />')" >
        </form> 
    </logic:equal>  
<!-- Assign Slip Number -->
    <logic:equal name="ledgerListHeadForm" property="mode" value="2">
      <td colspan = "7">            
        <form action="LedgerSlip.do" name="ledgerListHeadForm" method="post">        
          <input type="hidden" name="mode" value="<bean:write name="ledgerListHeadForm" property="mode" />">                                
          <input type="submit" value="Assign Slip" onclick="return confirm('<bean:message key="app.ledger.slip.message" />')" >
        </form> 
  </logic:equal>  
<!-- Non-Archived ledger search with Arichive Option -->
    <logic:equal name="ledgerListHeadForm" property="mode" value="3">
      <td colspan = "7">            
        <form action="LedgerArchive.do" name="ledgerListHeadForm" method="post">
          <input type="hidden" name="mode" value="<bean:write name="ledgerListHeadForm" property="mode" />">                 
          <input type="submit" value="Archive Ledger"  onclick="return confirm('<bean:message key="app.ledger.archive.message" />')" >
        </form>              
    </logic:equal>  
<!-- Ledger History by Date Range -->
    <logic:equal name="ledgerListHeadForm" property="mode" value="4">
      <td colspan = "7">   
        <html:form action="LedgerList.do">
          <html:hidden property="mode" />
          <html:hidden property="customerKey" />
          <html:hidden property="clientName" />
          <strong>Customer:</strong>
          <span class="ac_holder">
          <input id="clientNameAC" size="30" maxlength="30" value="<bean:write name="ledgerListHeadForm" property="clientName" />" onFocus="javascript:
          var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.ledgerListHeadForm.customerKey.value=obj.id; document.ledgerListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
          </span>
          <strong><bean:message key="app.dateFrom" />:</strong>
          <input type="text" name="fromDate" value="<bean:write name="ledgerListHeadForm" property="fromDate" />" size="8">
          <a href="javascript:show_calendar('ledgerForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
          <html:errors property="fromDate" />
          <strong>&nbsp;&nbsp;<bean:message key="app.dateTo" />&nbsp;</strong>
          <input type="text" name="toDate" value="<bean:write name="ledgerListHeadForm" property="toDate" />" size="8">
          <a href="javascript:show_calendar('ledgerForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
          <html:errors property="toDate" />
          &nbsp;&nbsp;&nbsp;          
          <input type="submit" value="<bean:message key="app.runIt" />">
        </html:form>
    </logic:equal>          
<!-- Non-Archived ledger search with Archive Option -->
    <logic:equal name="ledgerListHeadForm" property="mode" value="5">
      <td colspan = "7" align="center">            
        <h3>Invoices for Slip Number: <bean:write name="ledgerListHeadForm" property="slipNumber" /> </h3>
    </logic:equal>  
    <logic:equal name="ledgerListHeadForm" property="mode" value="6">
      <td colspan = "7">
        <html:form action="LedgerList.do">
          <html:hidden property="mode" />
          <html:hidden property="customerKey" />
          <html:hidden property="clientName" />
          <strong>Customer:</strong>
          <span class="ac_holder">
          <input id="clientNameAC" size="30" maxlength="30" value="<bean:write name="ledgerListHeadForm" property="clientName" />" onFocus="javascript:
          var options = {
		script:'CustomerLookUp.do?json=true&clinicId=false&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.ledgerListHeadForm.customerKey.value=obj.id; document.ledgerListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
          </span>
          <strong><bean:message key="app.dateFrom" />:</strong>
          <input type="text" name="fromDate" value="<bean:write name="ledgerListHeadForm" property="fromDate" />" size="8">
          <a href="javascript:show_calendar('ledgerForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
          <html:errors property="fromDate" />
          <strong>&nbsp;&nbsp;<bean:message key="app.dateTo" />&nbsp;</strong>
          <input type="text" name="toDate" value="<bean:write name="ledgerListHeadForm" property="toDate" />" size="8">
          <a href="javascript:show_calendar('ledgerForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
          <html:errors property="toDate" />
          &nbsp;&nbsp;&nbsp;          
          <input type="submit" value="<bean:message key="app.runIt" />">
        </html:form>
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
                <a href="CustomerInvoiceGet.do?key=<bean:write name="ledgerForm"
	        property="customerInvoiceKey" />"><bean:write name="ledgerForm" property="invoiceNumber" /></a>
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
	  <td>
<!-- Post Daily Ledger Page -->
            <logic:notEqual name="ledgerListHeadForm" property="mode" value="1">
              <logic:notEqual name="ledgerForm" property="key" value="-1">                
                <a href="LedgerGet.do?key=<bean:write name="ledgerForm"
	          property="key" />" ><bean:message key="app.ledger.view" /> </a>
              </logic:notEqual>                 
            </logic:notEqual>
	  </td> 
          <logic:equal name="ledgerListHeadForm" property="mode" value="6">	  
            <td>          
              <logic:notEqual name="ledgerForm" property="slipNumber" value="">              
                <a href="#" onclick="popup = putinpopup('PdfAccountStatement.do?presentationType=pdf&slipNum=<bean:write name="ledgerForm"
                  property="slipNumber" />'); return false" target="_PDF"><bean:message key="app.pdf" /></a>
              </logic:notEqual>                
            </td>
          </logic:equal>  
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
