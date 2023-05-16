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
 
<logic:equal name="petBoardListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="petBoardListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="petBoardListHeadForm"
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
    <td colspan = "7">
      <html:form action="PetBoardList.do">
        <logic:notEqual name="petBoardListHeadForm" property="petKey" value="">
          <input type="hidden" name="petKey" value="<bean:write name="petBoardListHeadForm" property="petKey" />">
        </logic:notEqual>
        <html:hidden property="customerKey" />
        <html:hidden property="clientName" />
        <strong><bean:message key="app.petboard.customer" />:</strong>
        <span class="ac_holder">
        <input id="clientNameAC" size="40" maxlength="40" value="<bean:write name="petBoardListHeadForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.petBoardListHeadForm.customerKey.value=obj.id; document.petBoardListHeadForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>        
        &nbsp;&nbsp;&nbsp;   
        <strong><bean:message key="app.petboard.dateRange" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="petBoardListHeadForm" property="fromDate" />" size="10" >
        <a href="javascript:show_calendar('petBoardListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.petboard.toDate" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="petBoardListHeadForm" property="toDate" />" size="10" >
        <a href="javascript:show_calendar('petBoardListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </html:form>
    </td>
  </tr>
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.petboard.customer" /></th>     
    <th><bean:message key="app.petboard.petName" /></th>     
    <th><bean:message key="app.petboard.checkInDate" /></th>
    <th><bean:message key="app.petboard.boardReason" /></th>    

    <th colspan="2" align="right">

    </th>      
  </tr>    
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr> 
<logic:notEqual name="petBoardListHeadForm" property="lastRecord" value="0"> 
  <logic:iterate id="petBoardListForm"
                 name="petBoardListHeadForm"
                 property="petBoardListForm"
                 scope="session"
                 type="com.wolmerica.petboard.PetBoardListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="petBoardListHeadForm"
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
            <bean:write name="petBoardListForm" property="clientName" />           
	  </td>            
	  <td>
            <bean:write name="petBoardListForm" property="petName" />
	  </td>
	  <td>
            <bean:write name="petBoardListForm" property="checkInDate" />
	  </td>
	  <td>
            <bean:write name="petBoardListForm" property="boardReason" />
	  </td>           
	  <td>
            &nbsp;              
            <% if (permViewId.equalsIgnoreCase("true")) { %>
	    <a href="PetBoardGet.do?key=<bean:write name="petBoardListForm" 
              property="key" />"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
	    <a href="PetBoardGet.do?key=<bean:write name="petBoardListForm" 
              property="key" />"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>   
            &nbsp;
	  </td>
	  <td>
            <logic:equal name="petBoardListForm" property="allowDeleteId" value="true">
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>
               &nbsp;
                <a href="PetBoardDelete.do?key=<bean:write name="petBoardListForm"
                  property="key" />"  onclick="return confirm('<bean:message key="app.petboard.delete.message" />')" ><bean:message key="app.delete" /></a>
               &nbsp;                  
              <% } %>
            </logic:equal>
	  </td>
          <td>           
             <% if (permViewId.equalsIgnoreCase("true") || permEditId.equalsIgnoreCase("true")) { %>
              &nbsp;
              <a href="#" onclick="popup = putinpopup('PdfKennelTagForm.do?key=<bean:write name="petBoardListForm" 
                property="key" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pdf"/></a>
              &nbsp;
            <% } %>   
          </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="7">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="3">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='PetBoardList.do?pageNo=<bean:write name="petBoardListHeadForm" 
        property="previousPage" />&petKey=<bean:write name="petBoardListHeadForm" 
        property="petKey" />&customerKey=<bean:write name="petBoardListHeadForm" 
        property="customerKey" />' "><bean:message key="app.previous" /></button>              
    </td>
    <td colspan="1">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="petBoardListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="petBoardListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="petBoardListHeadForm" property="recordCount" />      
    </td>
    <td colspan="3" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='PetBoardList.do?pageNo=<bean:write name="petBoardListHeadForm" 
        property="nextPage" /><logic:notEqual name="petBoardListHeadForm" property="petKey" value="">&petKey=<bean:write name="petBoardListHeadForm" 
        property="petKey" /></logic:notEqual><logic:notEqual name="petBoardListHeadForm" property="customerKey" value="">&customerKey=<bean:write name="petBoardListHeadForm" 
        property="customerKey" /></logic:notEqual>' "><bean:message key="app.next" /></button>              
    </td>  
  </tr>
</table>