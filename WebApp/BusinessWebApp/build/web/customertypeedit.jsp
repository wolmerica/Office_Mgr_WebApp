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
  String attTitle = "app.customertype.addtitle";            
  String attAction = "/CustomerTypeAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="customerTypeForm" property="key" value="">
  <%
    attTitle = "app.customertype.editTitle";
    attAction = "/CustomerTypeEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="customerTypeForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="customerTypeForm" property="permissionStatus" value="READONLY">
    [<bean:write name="customerTypeForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="5">
      <form action="CustomerTypeList.do" name="customerTypeList" method="post">                                  
      <input type="submit" value="<bean:message key="app.customertype.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>">  
   <logic:notEqual name="customerTypeForm" property="key" value="">   
      <html:hidden property="key" />
   </logic:notEqual>        
  <tr>
    <td><strong><bean:message key="app.customertype.name" />:</strong></td>
    <td>
      <html:text property="name" readonly="<%=attDisableEdit%>" />
      <html:errors property="name" />       
    </td>
    <td><strong><bean:message key="app.customertype.precedence" />:</strong></td>
    <td>
      <html:text property="precedence" size="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="precedence" />       
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.customertype.blueBookId" />:</strong></td>
    <td>
      <html:radio property="blueBookId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="blueBookId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="blueBookId" />                   
    </td> 
    <td><strong><bean:message key="app.customertype.soldByName" />:</strong></td>
    <td>           
      <html:hidden property="soldByKey" />
      <html:hidden property="soldByName" />
      <span class="ac_holder">
      <input id="soldByNameAC" size="30" maxlength="30" value="<bean:write name="customerTypeForm" property="soldByName" />" onFocus="javascript:
      var options = {
		script:'CustomerLookUp.do?json=true&clinicId=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.customerTypeForm.soldByKey.value=obj.id; document.customerTypeForm.soldByName.value=obj.value; }
		};
		var json=new AutoComplete('soldByNameAC',options);return true;" value="" />
      </span>
      <html:errors property="soldByKey" />
    </td>    
  </tr>    
  <tr>
    <td><strong><bean:message key="app.customertype.priceSheetId" />:</strong></td>
    <td>
      <html:radio property="priceSheetId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="priceSheetId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="priceSheetId" />                   
    </td> 
    <td><strong><bean:message key="app.customertype.attributeToEntity" />:</strong></td>
    <td>       
      <html:select property="attributeToEntity" disabled="<%=attKeyEdit%>">
        <html:option value="Pet"> Pet </html:option>
        <html:option value="System"> System </html:option>
        <html:option value="Vehicle"> Vehicle </html:option>        
      </html:select>    
      <html:errors property="attributeToEntity" />
    </td>      
  </tr>    
  <tr>
    <td><strong><bean:message key="app.customertype.activeId" />:</strong></td>
    <td>
      <html:radio property="activeId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="activeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="activeId" />                   
    </td> 
  </tr>  
  <tr>
    <td><strong><bean:message key="app.customertype.createUser" />:</strong></td>
    <td><bean:write name="customerTypeForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.customertype.createStamp" />:</strong></td>
    <td><bean:write name="customerTypeForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.customertype.updateUser" />:</strong></td>
    <td><bean:write name="customerTypeForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.customertype.updateStamp" />:</strong></td>
    <td><bean:write name="customerTypeForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save CustomerType Values" disabled="<%=attDisableEdit%>" />             
</html:form> 
    </td>
  </tr>
</table>