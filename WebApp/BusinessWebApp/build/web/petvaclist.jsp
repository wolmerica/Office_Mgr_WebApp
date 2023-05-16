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
 
<logic:equal name="petVacListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="petVacListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="petVacListHeadForm"
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
    <td colspan = "12">
      <html:form action="PetVacList.do">
        <logic:notEqual name="petVacListHeadForm" property="petKey" value="">
          <input type="hidden" name="petKey" value="<bean:write name="petVacListHeadForm" property="petKey" />">
        </logic:notEqual>
        <html:hidden property="customerKey" />
        <html:hidden property="clientName" />

        <input type="hidden" name="customerKey" value="<bean:write name="petVacListHeadForm" property="customerKey" />">
        <strong><bean:message key="app.petvac.customer" />:</strong>
        <span class="ac_holder">
        <input id="clientNameAC" size="40" maxlength="40" value="<bean:write name="petVacListHeadForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.petVacListHeadForm.customerKey.value=obj.id;  document.petVacListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>
        &nbsp;&nbsp;&nbsp;   
        <strong><bean:message key="app.petvac.dateRange" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="petVacListHeadForm" property="fromDate" />" size="10" >
        <a href="javascript:show_calendar('petVacListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.petvac.toDate" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="petVacListHeadForm" property="toDate" />" size="10" >
        <a href="javascript:show_calendar('petVacListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </html:form>
    </td>
  </tr>
  <tr>
    <td colspan="12">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <form action="PetVacList.do" name="petVacFilter" method="post">
    <th><bean:message key="app.petvac.customer" /></th>     
    <th><bean:message key="app.petvac.petName" /></th>     
    <th><bean:message key="app.petvac.vacDate" /></th>
    <th align="center"><bean:message key="app.petvac.rabiesId" /></th>
    <th align="center"><bean:message key="app.petvac.canineDistemperId" /></th>
    <th align="center"><bean:message key="app.petvac.corunnaId" /></th>
<!--    
    <th align="center"><bean:message key="app.petvac.felineDistemperId" /></th>
    <th align="center"><bean:message key="app.petvac.felineLeukemiaId" /></th>
-->    
    <th align="center"><bean:message key="app.petvac.otherId" /></th>
    <th><bean:message key="app.petvac.vacExpirationDate" /></th>
    <th colspan="2" align="right">

    </th>      
  </tr>    
  <tr>
    <td colspan="12">
      <hr>
    </td>
  </tr> 
<logic:notEqual name="petVacListHeadForm" property="lastRecord" value="0"> 


  <logic:iterate id="petVacListForm"
                 name="petVacListHeadForm"
                 property="petVacListForm"
                 scope="session"
                 type="com.wolmerica.petvac.PetVacListForm">                 
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="petVacListHeadForm"
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
            <bean:write name="petVacListForm" property="clientName" />           
	  </td>            
	  <td>
            <bean:write name="petVacListForm" property="petName" />
	  </td>
	  <td>
            <bean:write name="petVacListForm" property="vacDate" />
	  </td>
	  <td align="center">
            <html:checkbox name="petVacListForm" property="rabiesId" disabled="true" />
	  </td>           
	  <td align="center">
            <html:checkbox name="petVacListForm" property="canineDistemperId" disabled="true" />
	  </td>        
	  <td align="center">
            <html:checkbox name="petVacListForm" property="corunnaId" disabled="true" />
	  </td>
<!-- 
	  <td align="center">
            <html:checkbox name="petVacListForm" property="felineDistemperId" disabled="true" />
	  </td>
	  <td align="center">
            <html:checkbox name="petVacListForm" property="felineLeukemiaId" disabled="true" />
	  </td>
-->          
	  <td align="center">
            <html:checkbox name="petVacListForm" property="otherId" disabled="true" />
	  </td>
	  <td>
            <bean:write name="petVacListForm" property="vacExpirationDate" />            
	  </td>           
	  <td>
            &nbsp;              
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="PetVacGet.do?key=<bean:write name="petVacListForm" 
              property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="PetVacGet.do?key=<bean:write name="petVacListForm" 
              property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>   
            &nbsp;
	  </td>
	  <td>
            <logic:equal name="petVacListForm" property="allowDeleteId" value="true">
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>
               &nbsp;
                <a href="PetVacDelete.do?key=<bean:write name="petVacListForm"
                  property="key" />"  onclick="return confirm('<bean:message key="app.petvac.delete.message" />')" ><bean:message key="app.delete" /></a>
               &nbsp;                  
              <% } %>
            </logic:equal>
	  </td>
          <td>           
             <% if (permViewId.equalsIgnoreCase("true") || permEditId.equalsIgnoreCase("true")) { %>
              &nbsp;
              <a href="#" onclick="popup = putinpopup('PdfVaccinationForm.do?key=<bean:write name="petVacListForm" 
                property="key" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pdf"/></a>
              &nbsp;
            <% } %>   
          </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="12">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='PetVacList.do?pageNo=<bean:write name="petVacListHeadForm" 
        property="previousPage" />&petKey=<bean:write name="petVacListHeadForm" 
        property="petKey" />&customerKey=<bean:write name="petVacListHeadForm" 
        property="customerKey" />' "><bean:message key="app.previous" /></button>              
    </td>
    <td colspan="6" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="petVacListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="petVacListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="petVacListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='PetVacList.do?pageNo=<bean:write name="petVacListHeadForm" 
        property="nextPage" /><logic:notEqual name="petVacListHeadForm" property="petKey" value="">&petKey=<bean:write name="petVacListHeadForm" 
        property="petKey" /></logic:notEqual><logic:notEqual name="petVacListHeadForm" property="customerKey" value="">&customerKey=<bean:write name="petVacListHeadForm" 
        property="customerKey" /></logic:notEqual>' "><bean:message key="app.next" /></button>              
    </td>  
  </tr>
</table>