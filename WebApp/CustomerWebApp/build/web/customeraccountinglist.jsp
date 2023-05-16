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
  Integer permOffset = new Integer("0");
  
  boolean attDisableCustomerLookUp = true;
  if (request.getSession().getAttribute("MULTIACCT") != null) {
     if (request.getSession().getAttribute("MULTIACCT").toString().compareToIgnoreCase("true") == 0) {
       attDisableCustomerLookUp = false;
     }
  }  
%>   
 
<logic:equal name="customerAccountingListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="customerAccountingListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>            
    <td colspan = "8">            
      <form action="CustomerAccountingList.do" name="customerAccountingListHeadForm" method="post">
        <input type="hidden" name="customerKeyFilter" value="<bean:write name="customerAccountingListHeadForm" property="customerKeyFilter" />">
        <strong><bean:message key="app.activityFor" />:</strong>
        <input type="text" name="acctName" value="<bean:write name="customerAccountingListHeadForm" property="accountName" />" size="35" maxlength="35" readonly="true" >
        <% if (!(attDisableCustomerLookUp)) { %>
             <a href="blank" onclick="popup = putinpopup('CustomerLookUp.do'); return false" target="_blank"><img src="./images/prev.gif" width="16" height="16" border="0" alt="Click here to pick an account."></a>            
        <% } %>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.dateFrom" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="customerAccountingListHeadForm" property="fromDate" />" size="8" maxlength="10" maxlength="10" readonly="true" >
          <a href="javascript:show_calendar('customerAccountingListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click here to select the from date."></a>
        <strong>&nbsp;&nbsp;<bean:message key="app.dateTo" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="customerAccountingListHeadForm" property="toDate" />" size="8" maxlength="10" maxlength="10" readonly="true" >
          <a href="javascript:show_calendar('customerAccountingListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click here to select the to date."></a>
        &nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </form>        
    </td>
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.customeraccounting.acctName" /></th>
    <th><bean:message key="app.customeraccounting.acctNum" /></th>        
    <th><bean:message key="app.customeraccounting.postDate" /></th>
    <th><bean:message key="app.customeraccounting.sourceTypeDescription" /></th>
    <th><bean:message key="app.customeraccounting.number" /></th>
    <th><bean:message key="app.customeraccounting.reconciledId" /></th>        
    <th align="right"><bean:message key="app.customeraccounting.amount" /></th>
    <th align="right"><bean:message key="app.customeraccounting.amountDue" /></th>
  </tr>    
  <tr>
    <td colspan="10">
      <hr>
    </td>
  <tr> 
      
<logic:notEqual name="customerAccountingListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the customer accounting -->       
  <logic:iterate id="customerAccountingListForm"
                 name="customerAccountingListHeadForm"
                 property="customerAccountingListForm"
                 scope="session"
                 type="com.wolmerica.customeraccounting.CustomerAccountingListForm">
        
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
            <bean:write name="customerAccountingListForm" property="acctName" />
	  </td>
	  <td>
            <bean:write name="customerAccountingListForm" property="acctNum" />
	  </td>
	  <td>
            <bean:write name="customerAccountingListForm" property="postDate" />
	  </td>
	  <td>
            <bean:write name="customerAccountingListForm" property="sourceTypeDescription" />
	  </td>    
	  <td>
          <logic:equal name="customerAccountingListForm" property="sourceTypeDescription" value="Customer Invoice">
            <logic:notEqual name="customerAccountingListForm" property="sourceKey" value="-1">          
              <a href="blank" onclick="popup = putinpopup('PdfCustomerInvoice.do?presentationType=pdf&key=<bean:write name="customerAccountingListForm"             
              property="sourceKey" />'); return false" target="_PDF"><bean:write name="customerAccountingListForm" property="number" /></a>          
            </logic:notEqual>
          </logic:equal>
          <logic:equal name="customerAccountingListForm" property="sourceTypeDescription" value="Slip Number">
              <a href="LedgerList.do?mode=5&slipNum=<bean:write name="customerAccountingListForm"
	      property="number" />"><bean:write name="customerAccountingListForm" property="number" /></a>
          </logic:equal>
          <logic:notEqual name="customerAccountingListForm" property="sourceTypeDescription" value="Customer Invoice">
            <logic:notEqual name="customerAccountingListForm" property="sourceTypeDescription" value="Slip Number">                        
              <bean:write name="customerAccountingListForm" property="number" />            
            </logic:notEqual>              
          </logic:notEqual>
	  </td>          
	  <td align="center">
            <html:checkbox name="customerAccountingListForm" property="reconciledId" disabled="true" />
          </td>            
	  <td align="right">
            <bean:message key="app.localeCurrency" />          
            <bean:write name="customerAccountingListForm" property="amount" />
	  </td>
	  <td align="right">
            <bean:message key="app.localeCurrency" />          
            <bean:write name="customerAccountingListForm" property="amountDue" />
	  </td>  
	</tr>	
  </logic:iterate>
</logic:notEqual>
 
  <tr>
    <td colspan="10">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='CustomerAccountingList.do?customerKeyFilter=<bean:write name="customerAccountingListHeadForm" 
        property="customerKeyFilter" />&fromDate=<bean:write name="customerAccountingListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="customerAccountingListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="customerAccountingListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>         
    </td>
    <td colspan="2">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="customerAccountingListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="customerAccountingListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="customerAccountingListHeadForm" property="recordCount" />      
    </td>
    <td align="right"><strong><bean:message key="app.customeraccounting.balanceTotal" />:</strong></td>
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="customerAccountingListHeadForm" property="balanceTotal" />
    </td>    
    <td align="right"><strong><bean:message key="app.customeraccounting.pageTotal" />:</strong></td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="customerAccountingListHeadForm" property="pageTotal" />
    </td>
    <td colspan="2" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='CustomerAccountingList.do?customerKeyFilter=<bean:write name="customerAccountingListHeadForm" 
        property="customerKeyFilter" />&fromDate=<bean:write name="customerAccountingListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="customerAccountingListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="customerAccountingListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>

