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
  var actionURL = 'ExpenseReconcile.do?key=' + expenseKey;
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
 
<logic:equal name="expenseListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="expenseListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="expenseListHeadForm"
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
    <td colspan="9">
      <html:form action="ExpenseList.do">
        <html:hidden property="expenseNameFilter" />
        <strong><bean:message key="app.expense.name" />:</strong>
        <span class="ac_holder">
        <input id="expenseNameAC" size="30" maxlength="30" value="<bean:write name="expenseListHeadForm" property="expenseNameFilter" />" onFocus="javascript:
        var options = {
		script:'ExpenseLookUp.do?json=true&',
		varname:'lookUpNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.expenseListHeadForm.expenseNameFilter.value=obj.value; }
		};
		var json=new AutoComplete('expenseNameAC',options);return true;" value="" />
        </span>
        <strong><bean:message key="app.dateFrom" />:</strong>
        <html:text property="fromDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('expenseListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;&nbsp;<bean:message key="app.dateTo" />&nbsp;</strong>
        <html:text property="toDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('expenseListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click here to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
        &nbsp;&nbsp;&nbsp;
        <input type ="button" value="<bean:message key="app.clear" />" onclick="document.expenseListHeadForm.expenseNameAC.value='';document.expenseListHeadForm.expenseNameFilter.value='';" >
      </html:form>
    </td>
  </tr>
  <tr>
    <td colspan="9">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.expense.name" /></th>
    <th><bean:message key="app.expense.category" /></th>
    <th><bean:message key="app.expense.date" /></th>
    <th align="center"><bean:message key="app.expense.reconciledId" /></th>        
    <th><bean:message key="app.expense.dueDate" /></th>    
    <th align="right"><bean:message key="app.expense.payment" /></th>      
    <th align="right"><bean:message key="app.expense.amount" /></th>
    <th colspan="2" align="right">
    <% if (permAddId.equalsIgnoreCase("true")) { %>        
      <button type="button" onClick="window.location='ExpenseEntry.do?expenseNameFilter=<bean:write name="expenseListHeadForm" 
        property="expenseNameFilter" />&fromDate=<bean:write name="expenseListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="expenseListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="expenseListHeadForm" 
        property="currentPage" />' "><bean:message key="app.expense.addTitle" /></button>    
    <% } %>
    </th>      
  </tr>    
  <tr>
    <td colspan="9">
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
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="expenseListHeadForm"
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

        <logic:equal name="expenseListForm" property="reconciledId" value="true">
          <%
            if (permEditId.compareToIgnoreCase("true") == 0) 
              permViewId = "true";
            permEditId = "false";
            permDeleteId = "false";
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
            <bean:write name="expenseListForm" property="expenseName" />
	  </td>
	  <td>
            <bean:write name="expenseListForm" property="expenseCategory" />
	  </td>
	  <td>
            <bean:write name="expenseListForm" property="expenseDate" />
	  </td>          
	  <td align="center">
            <% if (permEditId.equalsIgnoreCase("true")) { %>              
              <input type="checkbox" name="<bean:write name="expenseListForm" property="key" />" onclick="Reconcile(<bean:write name="expenseListForm" property="key" />);" >
            <% } else { %>  
              <html:checkbox name="expenseListForm" property="reconciledId" disabled="true" />
            <% } %>
          </td>          
	  <td>
            <bean:write name="expenseListForm" property="dueDate" />
	  </td>          
          <td align="right">
            <bean:message key="app.localeCurrency" />          
            <bean:write name="expenseListForm" property="expensePayment" />
	  </td>         
          <td align="right">
            <bean:message key="app.localeCurrency" />          
            <bean:write name="expenseListForm" property="expenseAmount" />
	  </td>
	  <td align="center">
            <% if (permViewId.equalsIgnoreCase("true")) { %>	  
              <a href="ExpenseGet.do?key=<bean:write name="expenseListForm"
                property="key" />
<logic:notEqual name="expenseListHeadForm" property="expenseNameFilter" value="">
&expenseNameFilter=<bean:write name="expenseListHeadForm" property="expenseNameFilter" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="expenseListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="toDate" value="">
&toDate=<bean:write name="expenseListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="expenseListHeadForm" property="currentPage" />
</logic:notEqual>                
"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="ExpenseGet.do?key=<bean:write name="expenseListForm"
                property="key" />
<logic:notEqual name="expenseListHeadForm" property="expenseNameFilter" value="">
&expenseNameFilter=<bean:write name="expenseListHeadForm" property="expenseNameFilter" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="expenseListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="toDate" value="">
&toDate=<bean:write name="expenseListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="expenseListHeadForm" property="currentPage" />
</logic:notEqual>                                
"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>
	  </td>
	  <td align="right">
            <% if (permDeleteId.equalsIgnoreCase("true")) { %>	  
              <a href="ExpenseDelete.do?key=<bean:write name="expenseListForm"
                property="key" />
<logic:notEqual name="expenseListHeadForm" property="expenseNameFilter" value="">
&expenseNameFilter=<bean:write name="expenseListHeadForm" property="expenseNameFilter" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="fromDate" value="">
&fromDate=<bean:write name="expenseListHeadForm" property="fromDate" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="toDate" value="">
&toDate=<bean:write name="expenseListHeadForm" property="toDate" />
</logic:notEqual>
<logic:notEqual name="expenseListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="expenseListHeadForm" property="currentPage" />
</logic:notEqual>                
"  onclick="return confirm('<bean:message key="app.expense.delete.message" />')" ><bean:message key="app.delete" /></a>
            <% } %>
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="9">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='ExpenseList.do?expenseNameFilter=<bean:write name="expenseListHeadForm" 
        property="expenseNameFilter" />&fromDate=<bean:write name="expenseListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="expenseListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="expenseListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>    
    </td>
    <td colspan="2" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="expenseListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="expenseListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="expenseListHeadForm" property="recordCount" />      
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="expenseListHeadForm" property="paymentPageTotal" />
    </td>    
    <td align="right">
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;    
      <bean:message key="app.localeCurrency" />
      <bean:write name="expenseListHeadForm" property="paymentGrandTotal" />
    </td>        
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="expenseListHeadForm" property="expensePageTotal" />
    </td>    
    <td align="right">
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:message key="app.localeCurrency" />
      <bean:write name="expenseListHeadForm" property="expenseGrandTotal" />
    </td>        
    <td align="right" colspan="3">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='ExpenseList.do?expenseNameFilter=<bean:write name="expenseListHeadForm" 
        property="expenseNameFilter" />&fromDate=<bean:write name="expenseListHeadForm" 
        property="fromDate" />&toDate=<bean:write name="expenseListHeadForm" 
        property="toDate" />&pageNo=<bean:write name="expenseListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>    
    </td>  
  </tr>
</table>