<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" type="text/javascript"></script>
    
<%
  String attTitle = "app.expense.addTitle";            
  String attAction = "/ExpenseAdd";
  String attReplicateAction = "";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="expenseForm" property="key" value="">
  <%
    attTitle = "app.expense.editTitle";
    attReplicateAction = attAction;    
    attAction = "/ExpenseEdit";
    
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="expenseForm" property="permissionStatus" value="LOCKED">
  <% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="expenseForm" property="permissionStatus" value="READONLY">
    [<bean:write name="expenseForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="ExpenseListEntry.do" name="expenseList" method="post">
        <% if (request.getParameter("expenseNameFilter") != null) { %>
          <input type="hidden" name="expenseNameFilter" value="<%=request.getParameter("expenseNameFilter")%>">
        <% } %>
        <% if (request.getParameter("fromDate") != null) { %>
          <input type="hidden" name="fromDate" value="<%=request.getParameter("fromDate")%>">
        <% } %>
        <% if (request.getParameter("toDate") != null) { %>
          <input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>">
        <% } %>
        <% if (request.getParameter("pageNo") != null) { %>
          <input type="hidden" name="pageNo" value="<%=request.getParameter("pageNo")%>">
        <% } %>
        <input type="submit" value="<bean:message key="app.expense.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="expenseForm" property="key" value="">    
    <td colspan="4" align="right">
      <form action="ExpenseEntry.do" name="expenseEntry" method="post" >
        <input type="hidden" name="key" value="<bean:write name="expenseForm" property="key" />" >
        <% if (request.getParameter("expenseNameFilter") != null) { %>
          <input type="hidden" name="expenseNameFilter" value="<%=request.getParameter("expenseNameFilter")%>">
        <% } %>
        <% if (request.getParameter("fromDate") != null) { %>
          <input type="hidden" name="fromDate" value="<%=request.getParameter("fromDate")%>">
        <% } %>
        <% if (request.getParameter("toDate") != null) { %>
          <input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>">
        <% } %>
        <% if (request.getParameter("pageNo") != null) { %>
          <input type="hidden" name="pageNo" value="<%=request.getParameter("pageNo")%>">
        <% } %>        
        <input type="submit" value="<bean:message key="app.expense.replicateMessage" />" >
      </form>       
    </td>
    <td colspan="6" align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.expense.id"/>&sourceKey=<bean:write name="expenseForm"
                property="key" />&sourceName=<bean:write name="expenseForm"
                property="expenseName" />/<bean:write name="expenseForm"
                property="expenseDate" /> ">[<bean:write name="expenseForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."></a>
    </td>    
    </logic:notEqual>    
  </tr>
  <tr>
    <td colspan="11">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
<logic:notEqual name="expenseForm" property="key" value="">
  <html:hidden property="key" />  
</logic:notEqual>
  <% if (request.getParameter("expenseNameFilter") != null) { %>
    <input type="hidden" name="expenseNameFilter" value="<%=request.getParameter("expenseNameFilter")%>">
  <% } %>
  <% if (request.getParameter("fromDate") != null) { %>
    <input type="hidden" name="fromDate" value="<%=request.getParameter("fromDate")%>">
  <% } %>
  <% if (request.getParameter("toDate") != null) { %>
    <input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>">
  <% } %>
  <% if (request.getParameter("pageNo") != null) { %>
    <input type="hidden" name="pageNo" value="<%=request.getParameter("pageNo")%>">
  <% } %>
  <tr>
    <td><strong><bean:message key="app.expense.name" />:</strong></td>
    <td>     
      <html:text property="expenseName" size="30" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="expenseName" />
    </td>                    
  </tr>
  <tr>
    <td><strong><bean:message key="app.expense.category" />:</strong></td>
    <td>     
      <html:text property="expenseCategory" size="30" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="expenseCategory" />
    </td>                    
  </tr>
  <tr>
    <td>
      <strong><bean:message key="app.expense.date" />:</strong>
    </td>           
    <td>
      <html:text property="expenseDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>
        <a href="javascript:show_calendar('expenseForm.expenseDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the expense date."></a>
      <% } %>
      <html:errors property="expenseDate" />            
    </td>  
  </tr>
  <tr>
    <td>
      <strong><bean:message key="app.expense.dueDate" />:</strong>
    </td>           
    <td>
      <html:text property="dueDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>
        <a href="javascript:show_calendar('expenseForm.dueDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the expense date."></a>
      <% } %>
      <html:errors property="dueDate" />
    </td>  
  </tr>
  <tr>
    <td>
      <strong><bean:message key="app.expense.paymentDate" />:</strong>
    </td>           
    <td>
      <html:text property="paymentDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>
        <a href="javascript:show_calendar('expenseForm.paymentDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the expense date."></a>
      <% } %>
      <html:errors property="paymentDate" />            
    </td>  
  </tr>  
  <tr>
    <td><strong><bean:message key="app.expense.payment" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />                  
      <html:text property="expensePayment" size="7" maxlength="10" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="expensePayment" />            
    </td>         
  </tr>
  <tr>
    <td><strong><bean:message key="app.expense.rate" />:</strong></td>
    <td>              
      <html:text property="expenseRate" size="5" maxlength="5" readonly="<%=attDisableEdit%>" /> 
      <bean:message key="app.localePercent" />
      <html:errors property="expenseRate" />
    </td>         
  </tr>
  <tr>
    <td><strong><bean:message key="app.expense.amount" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />                  
      <html:text property="expenseAmount" size="7" maxlength="10" readonly="true" /> 
      <html:errors property="expenseAmount" />            
    </td>         
  </tr>
  <tr>
    <td>
      <strong><bean:message key="app.expense.taxPrepDate" />:</strong>
    </td>        
    <td>
      <html:text property="taxPrepDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>
        <a href="javascript:show_calendar('expenseForm.taxPrepDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the expense date."></a>
      <% } %>
      <html:errors property="taxPrepDate" />
    </td>  
  </tr>
  <tr>
    <td><strong><bean:message key="app.expense.noteLine1" />:</strong></td>
    <td colspan="4">   
      <html:text property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine1" />
    </td>   
  </tr>
  <tr>
    <td>
      <strong><bean:message key="app.expense.reconciledId" />:</strong>
    </td>                
    <td>
      <html:radio property="reconciledId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="reconciledId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="reconciledId" />         
    </td>   
  </tr>         
  <tr>
    <td><strong><bean:message key="app.expense.createUser" />:</strong></td>
    <td><bean:write name="expenseForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.expense.createStamp" />:</strong></td>
    <td><bean:write name="expenseForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.expense.updateUser" />:</strong></td>
    <td><bean:write name="expenseForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.expense.updateStamp" />:</strong></td>
    <td><bean:write name="expenseForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="8" align="right">
      <html:submit value="Save Expense Values" disabled="<%=attDisableEdit%>" />         
     </html:form> 
    </td>
  </tr>
</table>