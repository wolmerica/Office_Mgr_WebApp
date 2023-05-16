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
    <td colspan = "8">      
      <html:form action="VendorInvoiceDueList.do">     
      <html:hidden property="vendorKey" />
      <html:hidden property="vendorName" />
      <strong><bean:message key="app.vendorinvoicedue.vendorName" />:</strong>
      <span class="ac_holder">
      <input id="vendorNameAC" size="30" maxlength="30" value="<bean:write name="vendorInvoiceDueListHeadForm" property="vendorName" />" onFocus="javascript:
      var options = {
		script:'VendorLookUp.do?json=true&',
		varname:'nameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.vendorInvoiceDueListHeadForm.vendorKey.value=obj.id; document.vendorInvoiceDueListHeadForm.vendorName.value=obj.value; }
		};
		var json=new AutoComplete('vendorNameAC',options);return true;" value="" />
      </span>      
    </td>
  </tr>
  <tr>
    <td colspan = "2">        
      <strong><bean:message key="app.vendorinvoicedue.fromDate" />:</strong>
      <html:text property="fromDate" size="8" maxlength="10" />
      <a href="javascript:show_calendar('vendorInvoiceDueListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
      <html:errors property="fromDate" />                       
    </td>
    <td colspan = "2">
      <strong><bean:message key="app.vendorinvoicedue.toDate" />:</strong>    
      <html:text property="toDate" size="8" maxlength="10" />
      <a href="javascript:show_calendar('vendorInvoiceDueListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
      <html:errors property="toDate" />         
    </td>
  </tr>
  <tr>
    <td colspan = "2">     
      <strong><bean:message key="app.vendorinvoicedue.payDate1" />:</strong>
      <html:text property="payDate1" size="8" maxlength="10" />
      <a href="javascript:show_calendar('vendorInvoiceDueListHeadForm.payDate1');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
      <html:errors property="payDate1" />   
    </td>
    <td>
      <strong><bean:message key="app.vendorinvoicedue.payDate2" />:</strong>
      <html:text property="payDate2" size="8" maxlength="10" />
      <a href="javascript:show_calendar('vendorInvoiceDueListHeadForm.payDate2');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
      <html:errors property="payDate2" />
    </td>
    <td colspan = "2">       
      <strong><bean:message key="app.vendorinvoicedue.payDate3" />:</strong>
      <html:text property="payDate3" size="8" maxlength="10" />
      <a href="javascript:show_calendar('vendorInvoiceDueListHeadForm.payDate3');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
      <html:errors property="payDate3" />      
    </td>   
    <td>
      <input type="submit" value="<bean:message key="app.runIt" />">       
      </html:form>
    </td>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
  
    <th><bean:message key="app.vendorinvoicedue.invoiceNumber" /></th>
    <th><bean:message key="app.vendorinvoicedue.invoiceDate" /></th>
    <th><bean:message key="app.vendorinvoicedue.invoiceAmount" /></th>
    <th><bean:message key="app.vendorinvoicedue.invoiceDueDate" /></th>
    <th><bean:message key="app.vendorinvoicedue.dayCount" /></th>
    <th><bean:message key="app.vendorinvoicedue.dayAverage" /></th>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  <tr> 
      
<logic:notEqual name="vendorInvoiceDueListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the vendor invoice due date -->       
  <logic:iterate id="vendorInvoiceDueListForm"
                 name="vendorInvoiceDueListHeadForm"
                 property="vendorInvoiceDueListForm"
                 scope="session"
                 type="com.wolmerica.vendorinvoicedue.VendorInvoiceDueListForm">
        
<!-- Set values for row shading -->                  
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
      <bean:write name="vendorInvoiceDueListForm" property="invoiceNumber" />
    </td>
    <td>
      <bean:write name="vendorInvoiceDueListForm" property="invoiceDate" />
    </td>
    <td>
      <bean:write name="vendorInvoiceDueListForm" property="invoiceAmount" />
    </td>
    <td>
      <bean:write name="vendorInvoiceDueListForm" property="invoiceDueDate" />
    </td>    
    <td>
      <bean:write name="vendorInvoiceDueListForm" property="dayCount" />
    </td>    
    <td>
      <bean:write name="vendorInvoiceDueListForm" property="dayAverage" />
    </td>        
  </tr>
  </logic:iterate> 
 
  <tr>
    <td colspan="8">
      <hr>
    </td>      
  </tr>
  <tr> 
    <td colspan="2"></td>  
    <td align="right"><strong><bean:message key="app.vendorinvoicedue.payDate" />:</strong></td>
    <td align="right"><strong><bean:message key="app.vendorinvoicedue.dayCountAverage" />:</strong></td>
  </tr>
  <tr> 
    <td align="right"><strong><bean:message key="app.vendorinvoicedue.debitTotal" />:</strong></td>  
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="vendorInvoiceDueListHeadForm" property="debitTotal" />
    </td>  
    <td align="right">
      <bean:write name="vendorInvoiceDueListHeadForm" property="payDate1" />
    </td>  
    <td align="right">
      <bean:write name="vendorInvoiceDueListHeadForm" property="dayAverage1" />
    </td>      
  </tr>
  <tr> 
    <td align="right"><strong><bean:message key="app.vendorinvoicedue.creditTotal" />:</strong></td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="vendorInvoiceDueListHeadForm" property="creditTotal" />
    </td>  
    <td align="right">
      <bean:write name="vendorInvoiceDueListHeadForm" property="payDate2" />
    </td>  
    <td align="right">
      <bean:write name="vendorInvoiceDueListHeadForm" property="dayAverage2" />
    </td>          
  </tr>      
  <tr> 
    <td align="right"><strong><bean:message key="app.vendorinvoicedue.balanceTotal" />:</strong></td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="vendorInvoiceDueListHeadForm" property="balanceTotal" />
    </td>
    <td align="right">
      <bean:write name="vendorInvoiceDueListHeadForm" property="payDate3" />
    </td>  
    <td align="right">
      <bean:write name="vendorInvoiceDueListHeadForm" property="dayAverage3" />
    </td>          
  </tr> 
</logic:notEqual>       
  <tr>
    <td colspan="8">
      <hr>
    </td>      
  </tr>    
  <tr>
    <td colspan="2" align="right">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="vendorInvoiceDueListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="vendorInvoiceDueListHeadForm" property="recordCount" />      
    </td>
  </tr>

</table>

