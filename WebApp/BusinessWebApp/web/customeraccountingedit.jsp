<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<%
  String attTitle = "app.customeraccounting.addTitle";            
  String attAction = "/CustomerAccountingAdd";
    
  boolean attDisableEdit = true;
  if (request.getAttribute("disableEdit") != null) {
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }

  boolean attShowPaymentList = false;
  boolean attDisableSourceEdit = attDisableEdit;  
%> 

<logic:notEqual name="customerAccountingForm" property="key" value="">
  <%
    attTitle = "app.customeraccounting.editTitle";
    attAction = "/CustomerAccountingEdit";
  %>
</logic:notEqual>    

<logic:equal name="customerAccountingForm" property="sourceTypeName" value="SOURCE">
  <%
    attDisableSourceEdit = true;
  %>
</logic:equal>

<logic:equal name="customerAccountingForm" property="transactionTypeDescription" value="Payment">
  <%
    attShowPaymentList = true; 
    attDisableSourceEdit = attDisableEdit;    
  %>
</logic:equal>
<logic:equal name="customerAccountingForm" property="transactionTypeDescription" value="Refund">
  <%
    attShowPaymentList = true; 
    attDisableSourceEdit = attDisableEdit;    
  %>
</logic:equal>

<logic:notEqual name="customerAccountingForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="customerAccountingForm" property="permissionStatus" value="READONLY">
    [<bean:write name="customerAccountingForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="CustomerAccountingListEntry.do" name="customerAccountingList" method="post">
        <% if (request.getParameter("customerKeyFilter") != null) { %>
          <input type="hidden" name="customerKeyFilter" value="<%=request.getParameter("customerKeyFilter")%>">
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
        <input type="submit" value="<bean:message key="app.customeraccounting.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
<logic:notEqual name="customerAccountingForm" property="key" value="">
  <html:hidden property="key" />  
</logic:notEqual>
  <% if (request.getParameter("customerKeyFilter") != null) { %>
    <input type="hidden" name="customerKeyFilter" value="<%=request.getParameter("customerKeyFilter")%>">
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
    <td><strong><bean:message key="app.customeraccounting.acctNum" />:</strong></td>
    <td>
      <html:hidden property="customerKey" />    
      <html:text property="acctNum" size="40" maxlength="40" readonly="true" />
      <html:errors property="acctNum" />   
    </td>    
    <td><strong><bean:message key="app.customeraccounting.sourceTypeDescription" />:</strong></td>
    <td>
      <html:select property="sourceTypeKey" disabled="<%=attDisableSourceEdit%>">
      <!-- iterate over the source type -->       
      <logic:iterate id="accountingTypeForm"
                 name="customerAccountingForm"
                 property="accountingTypeForm"
                 scope="session"
                 type="com.wolmerica.accountingtype.AccountingTypeForm"> 
        <% if (attShowPaymentList) { %>
          <logic:equal name="accountingTypeForm" property="name" value="PAYMENT">
            <html:option value="<%=accountingTypeForm.getKey()%>" > <%=accountingTypeForm.getDescription()%> </html:option>
          </logic:equal>
        <% } else { %>        
          <logic:equal name="accountingTypeForm" property="name" value="SOURCE">
            <html:option value="<%=accountingTypeForm.getKey()%>" > <%=accountingTypeForm.getDescription()%> </html:option>
          </logic:equal>          
        <% } %>    
      </logic:iterate>
      </html:select>
      <html:errors property="sourceTypeKey" />                   
    </td>     
  </tr>
  <tr>
    <td><strong><bean:message key="app.customeraccounting.acctName" />:</strong></td>
    <td>
      <html:text property="acctName" size="40" maxlength="40" readonly="true" />
      <html:errors property="acctName" />    
    </td>
    <td><strong><bean:message key="app.customeraccounting.number" />:</strong></td>
    <td>
      <html:text property="number" size="12" maxlength="20" readonly="<%=attDisableSourceEdit%>" />
      <html:errors property="number" />    
    </td> 
  </tr>
  <tr>
    <td><strong><bean:message key="app.customeraccounting.transactionTypeDescription" />:</strong></td>
    <td>
      <html:hidden property="transactionTypeKey" />    
      <html:text property="transactionTypeDescription" size="40" maxlength="40" readonly="true" />
      <html:errors property="transactionTypeDescription" />    
    </td>   
    <td><strong><bean:message key="app.customeraccounting.postDate" />:</strong></td>
    <td>
      <html:text property="postDate" size="10" maxlength="10" readonly="true" />
      <html:errors property="postDate" />    
    </td>   
  </tr>    
  <tr>
    <td><strong><bean:message key="app.customeraccounting.note" />:</strong></td>
    <td>
      <html:text property="note" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="note" />    
    </td>     
    <td><strong><bean:message key="app.customeraccounting.amount" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />    
      <html:text property="amount" size="10" maxlength="10" readonly="<%=attDisableSourceEdit%>" />
      <html:errors property="amount" />    
    </td>    
  </tr>
  <tr>
    <td></td>
    <td></td>  
    <td>
      <strong><bean:message key="app.customeraccounting.reconciledId" />:</strong>
    </td>
    <td>
      <html:radio property="reconciledId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="reconciledId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="reconciledId" />         
    </td>  
  </tr>
  <tr>
    <td><strong><bean:message key="app.customeraccounting.createUser" />:</strong></td>
    <td><bean:write name="customerAccountingForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.customeraccounting.createStamp" />:</strong></td>
    <td><bean:write name="customerAccountingForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.customeraccounting.updateUser" />:</strong></td>
    <td><bean:write name="customerAccountingForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.customeraccounting.updateStamp" />:</strong></td>
    <td><bean:write name="customerAccountingForm" property="updateStamp" /></td>	     
  </tr>	
  <tr> 
    <td colspan="5" align="right">
      <html:submit value="Save Customer Acct Values" disabled="<%=attDisableEdit%>" />         
</html:form> 
    </td>
  </tr>
</table>
 

