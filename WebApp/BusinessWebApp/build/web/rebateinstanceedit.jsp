<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<%
  String attTitle = "app.rebateinstance.addTitle";            
  String attAction = "/RebateInstanceAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }       
%> 

<logic:notEqual name="rebateInstanceForm" property="key" value="">
  <%
    attTitle = "app.rebateinstance.editTitle";
    attAction = "/RebateInstanceEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>    

<logic:equal name="rebateInstanceForm" property="completeId" value="true">
   <% attDisableEdit = true; %>
</logic:equal>

<logic:notEqual name="rebateInstanceForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="rebateInstanceForm" property="permissionStatus" value="READONLY">
    [<bean:write name="rebateInstanceForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="RebateInstanceList.do" name="rebateInstanceList" method="post">  
        <% if (request.getParameter("idKey") != null) { %>
          <input type="hidden" name="idKey" value="<%=request.getParameter("idKey").toString()%>">
        <% } %>
        <% if (request.getParameter("poKey") != null) { %>          
          <input type="hidden" name="poKey" value="<%=request.getParameter("poKey").toString()%>">
        <% } %>
        <% if (request.getParameter("poiKey") != null) { %>
          <input type="hidden" name="poiKey" value="<%=request.getParameter("poiKey").toString()%>">
        <% } %>                    
        <input type="submit" value="<bean:message key="app.rebateinstance.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
      <logic:notEqual name="rebateInstanceForm" property="key" value="">      
        <html:hidden property="key" />
      </logic:notEqual>
        <% if (request.getParameter("idKey") != null) { %>
          <input type="hidden" name="idKey" value="<%=request.getParameter("idKey").toString()%>">
        <% } %>
        <% if (request.getParameter("poKey") != null) { %>          
          <input type="hidden" name="poKey" value="<%=request.getParameter("poKey").toString()%>">
        <% } %>
        <% if (request.getParameter("poiKey") != null) { %>
          <input type="hidden" name="poiKey" value="<%=request.getParameter("poiKey").toString()%>">
        <% } %>                    
  <tr>
    <td><strong><bean:message key="app.rebateinstance.purchaseOrderNumber" />:</strong></td>
    <td>
      <html:text property="purchaseOrderNumber" size="12" readonly="true" />
      <html:errors property="purchaseOrderNumber" />              
    </td>
  </tr>      
  <tr>
    <td><strong><bean:message key="app.rebateinstance.offerName" />:</strong></td>
    <td>
      <html:text property="offerName" size="35" maxlength="30" readonly="true" />
      <html:errors property="offerName" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.rebateinstance.eligibleId" />:</strong></td>
    <td>
      <html:radio property="eligibleId" value="false" disabled="<%=attDisableEdit%>" />No
      <html:radio property="eligibleId" value="true" disabled="<%=attDisableEdit%>" />Yes
      <html:errors property="eligibleId" />                    
    </td>   
  </tr>	
  <tr>
    <td><strong><bean:message key="app.rebateinstance.submitId" />:</strong></td>
    <td>
      <html:radio property="submitId" value="false" disabled="<%=attDisableEdit%>" />No
      <html:radio property="submitId" value="true" disabled="<%=attDisableEdit%>" />Yes
      <html:errors property="submitId" />                    
    </td>   
  </tr>	  
  <tr>
    <td><strong><bean:message key="app.rebateinstance.completeId" />:</strong></td>
    <td>
      <html:radio property="completeId" value="false" disabled="<%=attDisableEdit%>" />No
      <html:radio property="completeId" value="true" disabled="<%=attDisableEdit%>" />Yes
      <html:errors property="completeId" />                    
    </td>   
  </tr>	
  <tr>
      <td><strong><bean:message key="app.rebateinstance.trackingURL" />:</strong></td>
      <td colspan="3">   
      <html:text property="trackingURL" size="100" maxlength="128" readonly="<%=attDisableEdit%>" />
      <html:errors property="trackingURL" />
    </td> 
  </tr>   
  <tr>
      <td><strong><bean:message key="app.rebateinstance.noteLine1" />:</strong></td>
      <td colspan="3">   
      <html:text property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine1" />
    </td> 
  </tr> 
  <tr>
    <td><strong><bean:message key="app.rebateinstance.createUser" />:</strong></td>
    <td><bean:write name="rebateInstanceForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.rebateinstance.createStamp" />:</strong></td>
    <td><bean:write name="rebateInstanceForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.rebateinstance.updateUser" />:</strong></td>
    <td><bean:write name="rebateInstanceForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.rebateinstance.updateStamp" />:</strong></td>
    <td><bean:write name="rebateInstanceForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Rebate Values" disabled="<%=attDisableEdit%>" />  
</html:form> 
    </td>
</tr>
</table>

