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
<script src="./js/overlib_mini.js" language="JavaScript" type="text/javascript" ><!-- overLIB (c) Erik Bosrup --></script>

<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>  

<%
  String currentColor = "ODD";
  int i = -1;      
  String attTitle = "app.petvac.addTitle";            
  String attAction = "/PetVacAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="petVacForm" property="key" value="">
  <%
    attTitle = "app.petvac.editTitle";
    attAction = "/PetVacEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="petVacForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="petVacForm" property="permissionStatus" value="READONLY">
    [<bean:write name="petVacForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <button type="button" onClick="window.location='PetVacListEntry.do?petKey=<bean:write name="petVacForm" property="petKey" />' "><bean:message key="app.petvac.backMessage" /></button>
    </td>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
</table>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <html:form action="<%=attAction%>"> 
  <logic:notEqual name="petVacForm" property="key" value="">
    <html:hidden property="key" /> 
    <input type="hidden" name="sourceTypeKey" value="<bean:message key="app.pet.id"/>" >
  </logic:notEqual>
  <tr> 
    <td><strong><bean:message key="app.petvac.customer" />:</strong></td>
    <td>
      <html:text property="clientName" size="40" maxlength="40" readonly="true" /> 
      <html:errors property="clientName" />
    </td>        
    <td><strong><bean:message key="app.petvac.vacDate" />:</strong></td>
    <td>
      <html:text property="vacDate" size="10" maxlength="10" readonly="true" />
      <a href="ScheduleGet.do?key=<bean:write name="petVacForm" property="scheduleKey" /> "><img src="./images/prev.gif" width="16" height="16" border="0" title="Click to edit vaccination date."></a>
      <html:errors property="vacDate" />
    </td>       
  </tr>
  <tr>
    <td><strong><bean:message key="app.petvac.petName" />:</strong></td>
    <td>
      <html:text property="petName" size="40" maxlength="40" readonly="true" /> 
      <html:errors property="petName" />
    </td>       
    <td><strong><bean:message key="app.petvac.vacExpirationDate" />:</strong></td>
    <td>
      <html:text property="vacExpirationDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>            
        <a href="javascript:show_calendar('petVacForm.vacExpirationDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select a vaccine expiration date."></a>
      <% } %>
      <html:errors property="vacExpirationDate" />
    </td>       
  </tr> 
  <tr>
    <td><strong><bean:message key="app.petvac.resourceName" />:</strong></td>
    <td>
      <html:hidden property="resourceKey" />
      <html:hidden property="resourceName" />
      <span class="ac_holder">      
      <input id="resourceNameAC" size="40" maxlength="40" value="<bean:write name="petVacForm" property="resourceName" />" onFocus="javascript:
      var options = {
		script:'ResourceLookUp.do?json=true&',
		varname:'resourceNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.petVacForm.resourceKey.value=obj.id; document.petVacForm.resourceName.value=obj.value; }
		};
		var json=new AutoComplete('resourceNameAC',options);return true;" value="" />
      </span>
      <html:errors property="resourceKey" />
    </td>
    <logic:notEqual name="petVacForm" property="key" value="">    
      <td><strong><bean:message key="app.petvac.reminderDate" />:</strong></td>    
      <td>
        <logic:equal name="petVacForm" property="reminderKey" value="0">
          <button type="button" onClick="window.location='ScheduleEntry.do?pvKey=<bean:write name="petVacForm" property="key" />' "><bean:message key="app.petvac.reminder.message" /></button>
        </logic:equal>
        <logic:notEqual name="petVacForm" property="reminderKey" value="0">        
          <html:text property="reminderDate" size="10" maxlength="10" readonly="true" />
          <a href="ScheduleGet.do?key=<bean:write name="petVacForm" property="reminderKey" /> "><img src="./images/prev.gif" width="16" height="16" border="0" title="Click edit reminder."></a>
          <html:errors property="reminderDate" />
        </logic:notEqual>
      </td>
    </logic:notEqual>      
  </tr> 
  <tr>
    <td><strong><bean:message key="app.petvac.rabiesId" />:</strong></td>
    <td>
      <html:radio property="rabiesId" value="0" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="rabiesId" value="1" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="rabiesId" />
    </td>
    <logic:equal name="petVacForm" property="rabiesId" value="1">    
      <td><strong><bean:message key="app.petvac.rabiesTagNumber" />:</strong></td>
      <td>
        <html:text property="rabiesTagNumber" size="20" maxlength="20"/> 
        <html:errors property="rabiesTagNumber" />
      </td>
    </logic:equal>
  </tr>
  <tr>
    <td><strong><bean:message key="app.petvac.canineDistemperId" />:</strong></td>
    <td>
      <html:radio property="canineDistemperId" value="0" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="canineDistemperId" value="1" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="canineDistemperId" />
    </td> 
    <logic:equal name="petVacForm" property="rabiesId" value="1">        
      <td><strong><bean:message key="app.petvac.vacRouteNumber" />:</strong></td>
      <td>
        <html:text property="vacRouteNumber" size="20" maxlength="20"/> 
        <html:errors property="vacRouteNumber" />
      </td>
    </logic:equal>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.petvac.corunnaId" />:</strong></td>
    <td>
      <html:radio property="corunnaId" value="0" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="corunnaId" value="1" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="corunnaId" />
    </td>  
    <logic:equal name="petVacForm" property="rabiesId" value="1">    
      <td><strong><bean:message key="app.petvac.vacName" />:</strong></td>
      <td>
        <html:text property="vacName" size="30" maxlength="30"/> 
        <html:errors property="vacName" />
      </td>
    </logic:equal>    
  </tr>  
  <tr>
    <td><strong><bean:message key="app.petvac.felineDistemperId" />:</strong></td>
    <td>
      <html:radio property="felineDistemperId" value="0" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="felineDistemperId" value="1" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="felineDistemperId" />
    </td>   
    <logic:equal name="petVacForm" property="rabiesId" value="1">    
      <td><strong><bean:message key="app.petvac.vacSerialNumber" />:</strong></td>
      <td>
        <html:text property="vacSerialNumber" size="20" maxlength="20"/> 
        <html:errors property="vacSerialNumber" />
      </td>     
    </logic:equal>      
  </tr>
  <tr>    
    <td><strong><bean:message key="app.petvac.felineLeukemiaId" />:</strong></td>
    <td>
      <html:radio property="felineLeukemiaId" value="0" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="felineLeukemiaId" value="1" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="felineLeukemiaId" />
    </td>    
  </tr>  
  <tr>
    <td><strong><bean:message key="app.petvac.otherId" />:</strong></td>
    <td>
      <html:radio property="otherId" value="0" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="otherId" value="1" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="otherId" />
    </td>
    <logic:equal name="petVacForm" property="otherId" value="1">    
      <td>
        <logic:notEqual name="petVacForm" property="bundleKey" value="0">
          <strong><a href="BundleGet.do?key=<bean:write name="petVacForm"
            property="bundleKey" />"><bean:message key="app.petvac.otherName" /></a>:</strong>
        </logic:notEqual>
        <logic:equal name="petVacForm" property="bundleKey" value="0">
          <strong><bean:message key="app.petvac.otherName" />:</strong>
        </logic:equal>       
      </td>      
      <td>
        <html:hidden property="bundleKey" />
        <html:hidden property="bundleName" />
        <span class="ac_holder">      
        <input id="bundleNameAC" size="30" maxlength="30" value="<bean:write name="petVacForm" property="bundleName" />" onFocus="javascript:
        var options = {
		script:'BundleLookUp.do?json=true&',
		varname:'bundleNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.petVacForm.bundleKey.value=obj.id; document.petVacForm.bundleName.value=obj.value; }
		};
		var json=new AutoComplete('bundleNameAC',options);return true;" value="" />
        </span>
        <html:errors property="bundleName" />
      </td>
    </logic:equal>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.petvac.noteLine1" />:</strong></td>
    <td colspan="3">
      <html:text property="noteLine1" size="60" maxlength="60" /> 
      <html:errors property="noteLine1" />
    </td>   
  </tr>        
  <tr>
    <td><strong><bean:message key="app.petvac.createUser" />:</strong></td>
    <td><bean:write name="petVacForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.petvac.createStamp" />:</strong></td>
    <td><bean:write name="petVacForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.petvac.updateUser" />:</strong></td>
    <td><bean:write name="petVacForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.petvac.updateStamp" />:</strong></td>
    <td><bean:write name="petVacForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="8" align="right">
      <html:submit value="Save Vac Values" disabled="<%=attDisableEdit%>" />         
     </html:form> 
    </td>
  </tr>
</table> 
