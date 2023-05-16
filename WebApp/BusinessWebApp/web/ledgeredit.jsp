<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<%
  String attTitle = "app.ledger.addTitle";            
  String attAction = "/LedgerAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="ledgerForm" property="key" value="">
  <%
    attTitle = "app.ledger.editTitle";
    attAction = "/LedgerEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="ledgerForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="ledgerForm" property="permissionStatus" value="READONLY">
    [<bean:write name="ledgerForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="LedgerListEntry.do" name="ledgerList" method="post">
        <input type="hidden" name="mode" value="1">                     
        <input type="submit" value="<bean:message key="app.ledger.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
<logic:notEqual name="ledgerForm" property="key" value="">
  <html:hidden property="key" />
</logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.ledger.clientName" />:</strong></td>
    <td>
      <html:hidden property="customerKey" />
      <html:hidden property="clientName" />
      <span class="ac_holder">
      <input id="clientNameAC" size="25" maxlength="30" value="<bean:write name="ledgerForm" property="clientName" />" onFocus="javascript:
      var options = {
		script:'CustomerLookUp.do?json=true&clinicId=false&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.ledgerForm.customerKey.value=obj.id; document.ledgerForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
      </span>
      <html:errors property="customerKey" />
    </td>
    <td>
      &nbsp;         
    </td
  </tr>
  <tr>
    <td><strong><bean:message key="app.ledger.invoiceNumber" />:</strong></td>
    <td>
      <html:text property="invoiceNumber" size="10" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="invoiceNumber" />
    </td>
    <td><strong><bean:message key="app.ledger.invoiceTotal" />:</strong></td>
    <td>
      <html:text property="invoiceTotal" size="6" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="invoiceTotal" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.ledger.postStamp" />:</strong></td>
    <td>
      <html:text property="postStamp" size="20" readonly="true" />
      <html:errors property="postStamp" />           
    </td>
    <td><strong><bean:message key="app.ledger.slipNumber" /></td>
    <td>
      <html:text property="slipNumber" size="8" readonly="true" />
      <html:errors property="slipNumber" />	    
    </td> 
  </tr>
  <tr>
    <td><strong><bean:message key="app.ledger.archivedId" />:</strong></td>
    <td>  
      <html:radio property="archivedId" value="0" disabled="true" />No
      <html:radio property="archivedId" value="1" disabled="true" />Yes
      <html:errors property="archivedId" />	    
    </td> 
  </tr>		
  <tr>
    <td><strong><bean:message key="app.ledger.createUser" />:</strong></td>        <td><bean:write name="ledgerForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.ledger.createStamp" />:</strong></td>
    <td><bean:write name="ledgerForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.ledger.updateUser" />:</strong></td>
    <td><bean:write name="ledgerForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.ledger.updateStamp" />:</strong></td>
    <td><bean:write name="ledgerForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Ledger Values" disabled="<%=attDisableEdit%>" />         
      </html:form> 
    </td>
  </tr>
</table> 