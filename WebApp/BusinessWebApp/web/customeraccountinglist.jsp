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

<script type="text/javascript">
function Reconcile(expenseKey)
{
  var actionURL = 'CustomerAccountingReconcile.do?key=' + expenseKey;
  /*  
    alert(actionURL);
  */
  retrieveURL(actionURL); 
}

var req;
var which;

function retrieveURL(url) {

  if (url != "") {
    if (window.XMLHttpRequest) { // Non-IE browsers

      req = new XMLHttpRequest();
      req.onreadystatechange = processStateChange;
      try {
        req.open("GET", url, true);
      } catch (e) {
        alert(e);
      }
      req.send(null);
    } else if (window.ActiveXObject) { // IE
      req = new ActiveXObject("Microsoft.XMLHTTP");

      if (req) {
        req.onreadystatechange = processStateChange;
        req.open("GET", url, true);
        req.send();
      }
    }
  }
}

function processStateChange() {
  if (req.readyState == 4) { // Complete
    if (req.status == 200) { // OK response
      // do nothing...
    } else {
      alert("Problem: " + req.statusText);
    }
  }
}
</script>

<%
  String currentColor = "ODD";
  int i = 0;
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
  Integer permOffset = new Integer("0");
  String permAddId = "false";
  String permViewId = "false";
  String permEditId = "false";
  String permDeleteId = "false"; 
  String permLockAvailableId = "false";
  String permLockedBy = "";  
%>   
 
<logic:equal name="customerAccountingListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="customerAccountingListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="customerAccountingListHeadForm"
               property="permissionListForm"
               scope="session"
               offset="<%=permOffset.toString()%>"
               length="1"
               type="com.wolmerica.permission.PermissionListForm">
  <% 
    permAddId = permissionListForm.getAddId();              
  %>
</logic:iterate>   
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>            
    <td colspan="8" nowrap>
      <html:form action="CustomerAccountingList.do">
        <html:hidden property="customerKeyFilter" />
        <html:hidden property="accountName" />
        <strong>Customer:</strong>
        <span class="ac_holder">
        <input id="accountNameAC" size="25" maxlength="40" value="<bean:write name="customerAccountingListHeadForm" property="accountName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&clinicId=false&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.customerAccountingListHeadForm.customerKeyFilter.value=obj.id; document.customerAccountingListHeadForm.accountName.value=obj.value; }
		};
		var json=new AutoComplete('accountNameAC',options);return true;" value="" />
        </span>
        <strong><bean:message key="app.dateFrom" />:</strong>
        <html:text property="fromDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('customerAccountingListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;&nbsp;<bean:message key="app.dateTo" />&nbsp;</strong>
        <html:text property="toDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('customerAccountingListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click here to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </html:form>
    </td>
    <td colspan = "2">
      <logic:notEqual name="customerAccountingListHeadForm" property="customerKeyFilter" value="">        
        <a href="#" class="submenu" onclick="popup = putinpopup('PdfBillingActivity.do?key=<bean:write name="customerAccountingListHeadForm" 
          property="customerKeyFilter" />&fromDate=<bean:write name="customerAccountingListHeadForm" 
          property="fromDate" />&toDate=<bean:write name="customerAccountingListHeadForm" 
          property="toDate" />&presentationType=pdf'); return false" target="_PDF">Billing Activity</a>
      </logic:notEqual>
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
    <logic:notEqual name="customerAccountingListHeadForm" property="customerKeyFilter" value="">
    <logic:notEqual name="customerAccountingListHeadForm" property="balanceTotal" value="0.00">
    <th colspan="2" align="right">      
      <% if (permAddId.equalsIgnoreCase("true")) { %>         
        <button type="button" onClick="window.location='CustomerAccountingEntry.do?key=<bean:write name="customerAccountingListHeadForm" 
          property="customerKeyFilter" />&accountBalanceAmount=<bean:write name="customerAccountingListHeadForm" 
          property="balanceTotal" />&customerKeyFilter=<bean:write name="customerAccountingListHeadForm" 
          property="customerKeyFilter" />&fromDate=<bean:write name="customerAccountingListHeadForm" 
          property="fromDate" />&toDate=<bean:write name="customerAccountingListHeadForm" 
          property="toDate" />&pageNo=<bean:write name="customerAccountingListHeadForm" 
          property="currentPage" />' ">
          <logic:equal name="customerAccountingListHeadForm" property="allowPaymentId" value="true">
            <bean:message key="app.customer.accountingPaymentMessage" /></button>
          </logic:equal>  
          <logic:equal name="customerAccountingListHeadForm" property="allowRefundId" value="true">
            <bean:message key="app.customer.accountingRefundMessage" /></button>              
          </logic:equal>     
      <% } %>
    </th>
    </logic:notEqual>      
    </logic:notEqual>
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
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="customerAccountingListHeadForm"
                       property="permissionListForm"
                       scope="session"
                       offset="<%=permOffset.toString()%>"
                       length="1"
                       type="com.wolmerica.permission.PermissionListForm">
          <% 
            permViewId = permissionListForm.getViewId();
            permEditId = permissionListForm.getEditId();
            permDeleteId = permissionListForm.getDeleteId();
            permLockAvailableId = permissionListForm.getLockAvailableId();
            permLockedBy = permissionListForm.getLockedBy();            
          %>
        </logic:iterate>   
        
        <logic:equal name="customerAccountingListForm" property="reconciledId" value="true">
          <% if (permEditId.equalsIgnoreCase("true")) { 
               permViewId = permEditId;
               permEditId = "false"; 
               permDeleteId = "false";
             }
          %>
        </logic:equal>        
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
              <a href="CustomerInvoiceGet.do?key=<bean:write name="customerAccountingListForm"
	      property="sourceKey" />"><bean:write name="customerAccountingListForm" property="number" /></a>          
            </logic:notEqual>
          </logic:equal>
          <logic:equal name="customerAccountingListForm" property="sourceTypeDescription" value="Slip Number">
              <a href="LedgerListEntry.do?mode=5&slipNum=<bean:write name="customerAccountingListForm"
	      property="number" />"><bean:write name="customerAccountingListForm" property="number" /></a>
          </logic:equal>
          <logic:notEqual name="customerAccountingListForm" property="sourceTypeDescription" value="Customer Invoice">
            <logic:notEqual name="customerAccountingListForm" property="sourceTypeDescription" value="Slip Number">                        
              <bean:write name="customerAccountingListForm" property="number" />            
            </logic:notEqual>              
          </logic:notEqual>
	  </td>
	  <td align="center">
            <% if (permEditId.equalsIgnoreCase("true")) { %>              
              <input type="checkbox" name="<bean:write name="customerAccountingListForm" property="key" />" onclick="Reconcile(<bean:write name="customerAccountingListForm" property="key" />);" >
            <% } else { %>  
              <html:checkbox name="customerAccountingListForm" property="reconciledId" disabled="true" />
            <% } %>         
          </td>            
	  <td align="right" nowrap>
            <bean:message key="app.localeCurrency" />          
            <bean:write name="customerAccountingListForm" property="amount" />
	  </td>
	  <td align="right" nowrap>
            <bean:message key="app.localeCurrency" />          
            <bean:write name="customerAccountingListForm" property="amountDue" />
	  </td>	  
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
              <a href="CustomerAccountingGet.do?key=<bean:write name="customerAccountingListForm"
                property="key" />
<logic:notEqual name="customerAccountingListHeadForm" property="customerKeyFilter" value="">
&customerKeyFilter=<bean:write name="customerAccountingListHeadForm" property="customerKeyFilter" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="customerAccountingListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="toDate" value="">
&toDate=<bean:write name="customerAccountingListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="customerAccountingListHeadForm" property="currentPage" />
</logic:notEqual>
"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="CustomerAccountingGet.do?key=<bean:write name="customerAccountingListForm"
                property="key" />
<logic:notEqual name="customerAccountingListHeadForm" property="customerKeyFilter" value="">
&customerKeyFilter=<bean:write name="customerAccountingListHeadForm" property="customerKeyFilter" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="customerAccountingListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="toDate" value="">
&toDate=<bean:write name="customerAccountingListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="customerAccountingListHeadForm" property="currentPage" />
</logic:notEqual>
"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>
	  </td>
	  <td align="right">
          <logic:equal name="customerAccountingListForm" property="transactionTypeDescription" value="Payment">
            <% if (permDeleteId.equalsIgnoreCase("true")) { %>
              <a href="CustomerAccountingDelete.do?key=<bean:write name="customerAccountingListForm"
                property="key" />
<logic:notEqual name="customerAccountingListHeadForm" property="customerKeyFilter" value="">
&customerKeyFilter=<bean:write name="customerAccountingListHeadForm" property="customerKeyFilter" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="customerAccountingListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="toDate" value="">
&toDate=<bean:write name="customerAccountingListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="customerAccountingListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="customerAccountingListHeadForm" property="currentPage" />
</logic:notEqual>
" onclick="return confirm('<bean:message key="app.customeraccounting.delete.message" />')" ><bean:message key="app.delete" /></a>
            <% } %>
          </logic:equal>
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

