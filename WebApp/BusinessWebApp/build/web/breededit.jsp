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
  String attTitle = "app.breed.addtitle";
  String attAction = "/BreedAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="breedForm" property="key" value="">
  <%
    attTitle = "app.breedTitle";
    attAction = "/BreedEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="breedForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="breedForm" property="permissionStatus" value="READONLY">
    [<bean:write name="breedForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="5">
      <form action="BreedList.do" name="breedList" method="post">
      <input type="submit" value="<bean:message key="app.breed.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>">  
   <logic:notEqual name="breedForm" property="key" value="">
      <html:hidden property="key" />
   </logic:notEqual>        
  <tr>
    <td><strong><bean:message key="app.breed.speciesName" />:</strong></td>
    <td>
      <html:hidden property="speciesKey" />
      <html:hidden property="speciesName" />
      <span class="ac_holder">
      <input id="speciesNameAC" size="30" maxlength="30" value="<bean:write name="breedForm" property="speciesName" />" onFocus="javascript:
      var options = {
		script:'SpeciesLookUp.do?json=true&',
		varname:'speciesNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.breedForm.speciesKey.value=obj.id; document.breedForm.speciesName.value=obj.value; }
		};
		var json=new AutoComplete('speciesNameAC',options);return true;" value="" />
      </span>
      <html:errors property="speciesKey" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.breed.name" />:</strong></td>
    <td>
      <html:text property="breedName" size="20" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="breedName" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.breed.extId" />:</strong></td>
    <td>
      <html:text property="breedExtId" size="8" maxlength="5" readonly="<%=attDisableEdit%>" />
      <html:errors property="breedExtId" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.breed.createUser" />:</strong></td>
    <td><bean:write name="breedForm" property="createUser" /></td>
    <td><strong><bean:message key="app.breed.createStamp" />:</strong></td>
    <td><bean:write name="breedForm" property="createStamp" /></td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.breed.updateUser" />:</strong></td>
    <td><bean:write name="breedForm" property="updateUser" /></td>
    <td><strong><bean:message key="app.breed.updateStamp" />:</strong></td>
    <td><bean:write name="breedForm" property="updateStamp" /></td>
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Breed Values" disabled="<%=attDisableEdit%>" />
</html:form> 
    </td>
  </tr>
</table>