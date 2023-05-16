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
 
<logic:equal name="breedListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="breedListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="breedListHeadForm"
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
    <td colspan="10">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <html:form action="BreedList.do">
      <th>
        <bean:message key="app.species.filter" />
        <html:hidden property="speciesKey" />
        <html:hidden property="speciesName" />
        <input type="hidden" name="speciesKey" value="<bean:write name="breedListHeadForm" property="speciesKey" />" />
        <span class="ac_holder">
        <input id="speciesNameAC" size="18" maxlength="20" value="<bean:write name="breedListHeadForm" property="speciesName" />" onFocus="javascript:
        var options = {
		script:'SpeciesLookUp.do?json=true&',
		varname:'speciesNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.breedListHeadForm.speciesKey.value=obj.id; document.breedListHeadForm.speciesName.value=obj.value; }
		};
		var json=new AutoComplete('speciesNameAC',options);return true;" value="" />
        </span>
      </th>
      <th>
        <bean:message key="app.breed.filter" />
        <input type="text" name="breedNameFilter" size="18" maxlength="20" value="<bean:write name="breedListHeadForm" property="breedNameFilter" />" >
        &nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">        
      </th>
    </html:form>
  </tr>
  <tr align="left">
    <th><bean:message key="app.breed.speciesName" /></th>
    <th><bean:message key="app.breed.name" /></th>
    <th><bean:message key="app.breed.extId" /></th>
    <th><bean:message key="app.breed.updateUser" /></th>
    <th><bean:message key="app.breed.updateStamp" /></th>
    <th colspan="2" align="right">
      <% if (permAddId.equalsIgnoreCase("true")) { %>        
        <button type="button" onClick="window.location='BreedEntry.do' "><bean:message key="app.breed.addTitle" /></button>
      <% } %>
    </th>      
  </tr>    
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr> 
<logic:notEqual name="breedListHeadForm" property="lastRecord" value="0">

<!-- Iterate over all the breeds -->
  <logic:iterate id="breedForm"
                 name="breedListHeadForm"
                 property="breedListForm"
                 scope="session"
                 type="com.wolmerica.breed.BreedForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="breedListHeadForm"
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
            <bean:write name="breedForm" property="speciesName" />
	  </td>            
	  <td>
            <bean:write name="breedForm" property="breedName" />
	  </td>
	  <td>
            <bean:write name="breedForm" property="breedExtId" />
	  </td>
	  <td>
            <bean:write name="breedForm" property="updateUser" />
	  </td>
	  <td>
            <bean:write name="breedForm" property="updateStamp" />
	  </td>
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="BreedGet.do?key=<bean:write name="breedForm"
              property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="BreedGet.do?key=<bean:write name="breedForm"
              property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>              
	  </td>
	  <td align="right">
            <logic:equal name="breedForm" property="allowDeleteId" value="true">
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>
                <a href="BreedDelete.do?key=<bean:write name="breedForm"
                  property="key" />" onclick="return confirm('<bean:message key="app.breed.delete.message" />')" ><bean:message key="app.delete" /></a>
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
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='BreedList.do?pageNo=<bean:write name="breedListHeadForm"
        property="previousPage" />&breedNameFilter=<bean:write name="breedListHeadForm"
        property="breedNameFilter" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="4">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="breedListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="breedListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="breedListHeadForm" property="recordCount" />
    </td>
    <td>
      <button type="button" <%=attDisableNextButton%> onClick="window.location='BreedList.do?pageNo=<bean:write name="breedListHeadForm"
        property="nextPage" />&breedNameFilter=<bean:write name="breedListHeadForm"
        property="breedNameFilter" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>