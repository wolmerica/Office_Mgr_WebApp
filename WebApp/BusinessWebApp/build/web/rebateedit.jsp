<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>

<%
  String attTitle = "app.rebate.addTitle";            
  String attAction = "/RebateAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }       
%> 

<logic:notEqual name="rebateForm" property="key" value="">
  <%
    attTitle = "app.rebate.editTitle";
    attAction = "/RebateEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>    

<logic:notEqual name="rebateForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="rebateForm" property="permissionStatus" value="READONLY">
    [<bean:write name="rebateForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="RebateList.do" name="rebateList" method="post">
        <% if (request.getParameter("idKey") != null) { %>
          <input type="hidden" name="idKey" value="<%=request.getParameter("idKey").toString()%>">
        <% } %>
        <% if (request.getParameter("poKey") != null) { %>          
          <input type="hidden" name="poKey" value="<%=request.getParameter("poKey").toString()%>">
        <% } %>
        <% if (request.getParameter("poiKey") != null) { %>
          <input type="hidden" name="poiKey" value="<%=request.getParameter("poiKey").toString()%>">
        <% } %>                    
      <input type="submit" value="<bean:message key="app.rebate.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="rebateForm" property="key" value="">
    <td colspan="6" align="right">      
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.rebate.id"/>&sourceKey=<bean:write name="rebateForm"
                property="key" />&sourceName=<bean:write name="rebateForm"
                property="offerName" />" >[<bean:write name="rebateForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."> </a>
    </td>
    </logic:notEqual>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
      <logic:notEqual name="rebateForm" property="key" value="">      
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
    <td><strong><bean:message key="app.rebate.offerName" />:</strong></td>
    <td>
      <html:text property="offerName" size="45" maxlength="60" readonly="<%=attKeyEdit%>" />
      <html:errors property="offerName" />        
    </td>
    <td><strong><bean:message key="app.rebate.processBy" />:</strong></td>
    <td>
      <html:text property="processBy" size="45" maxlength="60" readonly="<%=attKeyEdit%>" />
      <html:errors property="processBy" />        
    </td>     
  </tr>
  <tr>
    <td><strong><bean:message key="app.rebate.brandName" />:</strong></td>
    <td>
      <html:text property="brandName" size="45" maxlength="60" readonly="true" />
      <html:errors property="brandName" />        
    </td>
    <td><strong><bean:message key="app.rebate.startDate" />:</strong></td>
    <td>
      <html:text property="startDate" size="10" readonly="<%=attKeyEdit%>" />
        <% if (!(attKeyEdit)) { %>
          <a href="javascript:show_calendar('rebateForm.startDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the rebate start date."></a>
        <% } %>
      <html:errors property="startDate" />            
    </td>   
  </tr>
  <tr>
    <td>
      <strong><bean:message key="app.rebate.size" />\</strong>
      <strong><bean:message key="app.rebate.sizeUnit" />:</strong>
    </td>
    <td>
      <html:text property="size" size="3" maxlength="5" readonly="true" />
      <html:errors property="size" />        
    </td>
    <td><strong><bean:message key="app.rebate.endDate" />:</strong></td>
    <td>
      <html:text property="endDate" size="10" readonly="<%=attKeyEdit%>" />
        <% if (!(attKeyEdit)) { %>
          <a href="javascript:show_calendar('rebateForm.endDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the rebate end date."></a>
        <% } %>
      <html:errors property="endDate" />            
    </td>       
  </tr> 
  <tr>
    <td><strong><bean:message key="app.rebate.terms" />:</strong></td>
    <td>
      <html:text property="terms" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="terms" />        
    </td>
    <td><strong><bean:message key="app.rebate.submitDate" />:</strong></td>
    <td>
      <html:text property="submitDate" size="10" readonly="<%=attKeyEdit%>" />
        <% if (!(attKeyEdit)) { %>
          <a href="javascript:show_calendar('rebateForm.submitDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the rebate submit date."></a>
        <% } %>
      <html:errors property="submitDate" />            
    </td>    
  </tr>  
  <tr>
    <td><strong><bean:message key="app.rebate.address" />:</strong></td>
    <td>
      <html:text property="address" size="35" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="address" />        
    </td>
    <td><strong><bean:message key="app.rebate.amount" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />    
      <html:text property="amount" size="4" readonly="<%=attKeyEdit%>" />
      <html:errors property="amount" />        
    </td>     
  </tr>
  <tr>
    <td><strong><bean:message key="app.rebate.address2" />:</strong></td>
    <td>
      <html:text property="address2" size="35" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="address2" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.rebate.city" />:</strong></td>
    <td>
      <html:text property="city" size="35" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="city" />        
    </td>
  </tr>
  <tr>    
    <td><strong><bean:message key="app.rebate.state" />:</strong></td>
    <td>
      <html:text property="state" size="2" maxlength="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="state" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.rebate.zip" />:</strong></td>
    <td>
      <html:text property="zip" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="zip" />        
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.rebate.createUser" />:</strong></td>
    <td><bean:write name="rebateForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.rebate.createStamp" />:</strong></td>
    <td><bean:write name="rebateForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.rebate.updateUser" />:</strong></td>
    <td><bean:write name="rebateForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.rebate.updateStamp" />:</strong></td>
    <td><bean:write name="rebateForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Rebate Values" disabled="<%=attDisableEdit%>" />  
</html:form> 
    </td>
</tr>
</table>

