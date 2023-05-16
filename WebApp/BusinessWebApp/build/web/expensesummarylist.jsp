<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
 
<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<%
  String currentColor = "ODD";
  int i = 0;
%>   
 
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="8">
      <html:form action="ExpenseSummaryList.do">
        <strong><bean:message key="app.expense.summaryBy" />:</strong>
        <select name="mode">
           <option value="1" <logic:equal name="expenseListHeadForm" property="mode" value="1">selected</logic:equal> >
           <bean:message key="app.expense.category" />
           <option value="2" <logic:equal name="expenseListHeadForm" property="mode" value="2">selected</logic:equal> >
           <bean:message key="app.expense.name" /> 
        </select>        
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.expense.taxPrepDate" />:</strong>
        <html:text property="fromDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('expenseListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        &nbsp;&nbsp;&nbsp;&nbsp;
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
    <th><bean:message key="app.expense.category" /></th>      
    <th><bean:message key="app.expense.name" /></th>
    <th></th>
    <th align="right"><bean:message key="app.expense.count" /></th>    
    <th align="right"><bean:message key="app.expense.payment" /></th>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  <tr>  
<logic:notEqual name="expenseListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the expenses -->       
  <logic:iterate id="expenseListForm"
                 name="expenseListHeadForm"
                 property="expenseListForm"
                 scope="session"
                 type="com.wolmerica.expense.ExpenseListForm">
<!-- Set values for row shading -->                  
        <%
          if ( i % 2 == 0) 
             currentColor = "ODD";
          else
             currentColor = "EVEN";
          i++;
        %>                
	<tr align="left" class="<%=currentColor %>">
	  <td>
            <bean:write name="expenseListForm" property="expenseCategory" />
	  </td>            
	  <td>
            <bean:write name="expenseListForm" property="expenseName" />
	  </td>
          <td></td>
          <td align="right">
            <bean:write name="expenseListForm" property="attachmentCount" />
	  </td>          
          <td align="right">
            <bean:message key="app.localeCurrency" />          
            <bean:write name="expenseListForm" property="expensePayment" />
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
    <td></td>
    <td align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="expenseListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="expenseListHeadForm" property="recordCount" />      
    </td>
    <td align="center">
      <a href="#top">Top&nbsp;<img border="0" src="./images/arrowtop.gif" align="absmiddle" width="16" height="15"></a>
    </td>    
    <td align="right">
      <bean:write name="expenseListHeadForm" property="currentPage" />
    </td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="expenseListHeadForm" property="paymentGrandTotal" />
    </td>
  </tr>
</table>