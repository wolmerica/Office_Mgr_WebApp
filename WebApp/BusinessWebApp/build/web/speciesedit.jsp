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
  String attTitle = "app.species.addtitle";
  String attAction = "/SpeciesAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="speciesForm" property="key" value="">
  <%
    attTitle = "app.speciesTitle";
    attAction = "/SpeciesEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="speciesForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="speciesForm" property="permissionStatus" value="READONLY">
    [<bean:write name="speciesForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="5">
      <form action="SpeciesList.do" name="speciesList" method="post">
      <input type="submit" value="<bean:message key="app.species.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>">  
   <logic:notEqual name="speciesForm" property="key" value="">
      <html:hidden property="key" />
   </logic:notEqual>        
  <tr>
    <td><strong><bean:message key="app.species.name" />:</strong></td>
    <td>
      <html:text property="speciesName" size="20" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="speciesName" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.species.extId" />:</strong></td>
    <td>
      <html:text property="speciesExtId" size="8" maxlength="5" readonly="<%=attDisableEdit%>" />
      <html:errors property="speciesExtId" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.species.createUser" />:</strong></td>
    <td><bean:write name="speciesForm" property="createUser" /></td>
    <td><strong><bean:message key="app.species.createStamp" />:</strong></td>
    <td><bean:write name="speciesForm" property="createStamp" /></td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.species.updateUser" />:</strong></td>
    <td><bean:write name="speciesForm" property="updateUser" /></td>
    <td><strong><bean:message key="app.species.updateStamp" />:</strong></td>
    <td><bean:write name="speciesForm" property="updateStamp" /></td>
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Species Values" disabled="<%=attDisableEdit%>" />
</html:form> 
    </td>
  </tr>
</table>