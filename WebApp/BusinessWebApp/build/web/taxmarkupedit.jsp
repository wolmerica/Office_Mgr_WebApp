<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<%
  String attTitle = "app.taxmarkup.editTitle";            
  String attAction = "/TaxMarkUpEdit";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }       
%>   
 
<logic:notEqual name="taxMarkUpForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="taxMarkUpForm" property="permissionStatus" value="READONLY">
    [<bean:write name="taxMarkUpForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="5">
      <form action="TaxMarkUpList.do" name="taxMarkUpList" method="post">                                  
      <input type="submit" value="<bean:message key="app.taxmarkup.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>">  
  <html:hidden property="key" />    
    <tr>
      <td><strong><bean:message key="app.taxmarkup.name" />:</strong></td>
      <td>
        <html:text property="name" size="25" readonly="true"/>
        <html:errors property="name" />               
      </td>
      <td><strong><bean:message key="app.taxmarkup.value" />:</strong></td>
      <td>      
        <html:text property="value" size="3" readonly="<%=attDisableEdit%>" />
        <html:errors property="value" />       
      </td>
    </tr>
    <tr>
      <td><strong><bean:message key="app.taxmarkup.description" />:</strong></td>
      <td>
        <html:text property="description" size="25" readonly="<%=attDisableEdit%>"/>
        <html:errors property="description" />               
      </td>    
      <td><strong><bean:message key="app.taxmarkup.percentageId" />:</strong></td>
      <td>
        <html:radio property="percentageId" value="false" disabled="<%=attDisableEdit%>"/>No
        <html:radio property="percentageId" value="true" disabled="<%=attDisableEdit%>"/>Yes
        <html:errors property="percentageId" />                   
      </td>
    </tr>
    <tr>
      <td><strong><bean:message key="app.taxmarkup.precedence" />:</strong></td>
      <td>
        <html:text property="precedence" size="2" readonly="<%=attDisableEdit%>" />
        <html:errors property="precedence" />   
      </td>
      <td><strong><bean:message key="app.taxmarkup.activeId" />:</strong></td>
      <td>
        <html:radio property="activeId" value="false" disabled="<%=attDisableEdit%>"/>No
        <html:radio property="activeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
        <html:errors property="activeId" />                   
      </td> 
    </tr>
    <tr>
      <td><strong><bean:message key="app.taxmarkup.createUser" />:</strong></td>
      <td><bean:write name="taxMarkUpForm" property="createUser" /></td>	          	
      <td><strong><bean:message key="app.taxmarkup.createStamp" />:</strong></td>
      <td><bean:write name="taxMarkUpForm" property="createStamp" /></td>	             	
    </tr>
    <tr>
      <td><strong><bean:message key="app.taxmarkup.updateUser" />:</strong></td>
      <td><bean:write name="taxMarkUpForm" property="updateUser" /></td>	     
      <td><strong><bean:message key="app.taxmarkup.updateStamp" />:</strong></td>
      <td><bean:write name="taxMarkUpForm" property="updateStamp" /></td>	     
    </tr>	
    <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Tax & MarkUp Values" disabled="<%=attDisableEdit%>" />  
</html:form> 
    </td>
    </tr>
  </table>
