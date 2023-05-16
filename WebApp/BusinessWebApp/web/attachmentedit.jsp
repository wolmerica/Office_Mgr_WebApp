<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>
    
<%
  String attTitle = "app.attachment.addTitle";            
  String attAction = "/AttachmentAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (request.getAttribute("disableEdit") != null) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="attachmentForm" property="key" value="">
  <%
    attTitle = "app.attachment.editTitle";
    attAction = "/AttachmentEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="attachmentForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="attachmentForm" property="permissionStatus" value="READONLY">
    [<bean:write name="attachmentForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="AttachmentList.do" name="attachmentList" method="post">                                  
        <input type="hidden" name="sourceTypeKey" value="<bean:write name="attachmentForm" property="sourceTypeKey"/>">
        <input type="hidden" name="sourceKey" value="<bean:write name="attachmentForm" property="sourceKey"/>">
        <input type="hidden" name="sourceName" value="<bean:write name="attachmentForm" property="sourceName" />">        
        <input type="submit" value="<bean:message key="app.attachment.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
   
<logic:equal name="attachmentForm" property="key" value="">  
  <html:form action="/AttachmentAdd" method="post" enctype="multipart/form-data">
    <html:hidden property="sourceTypeKey" />
    <html:hidden property="sourceKey" />    
    <html:hidden property="sourceName" />    
    
<!-- Display messages from application. -->
  <logic:messagesPresent message="true">
    <tr>
      <html:messages id="message" message="true">
      <td colspan="4">
        <bean:message key="messages.header" />    
        <bean:write name="message"/>
        <bean:message key="messages.footer" />          
      </td>
      </html:messages>
    </tr>
  </logic:messagesPresent>    
  
  <tr> 
    <td align="center" colspan="2">
      <strong><bean:message key="app.attachment.uploadMessage" />:</strong>
    </td>
  </tr>
  <tr>
    <td>    
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="0">
        <strong><bean:message key="app.employee.featureHelp" />:</strong>
      </logic:equal>        
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="2">
        <strong><bean:message key="app.employee.featureEmployee" />:</strong>
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="3">
        <strong><bean:message key="app.employee.featureItem" />:</strong>        
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="4">
        <strong><bean:message key="app.employee.featurePet" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="5">
        <strong><bean:message key="app.employee.featureRebate" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="6">
        <strong><bean:message key="app.employee.featureService" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="7">
        <strong><bean:message key="app.employee.featureVendor" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="11">
        <strong><bean:message key="app.employee.featurePurchaseOrder" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="13">
        <strong><bean:message key="app.employee.featureVendorInvoice" />:</strong>  
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="18">
        <strong><bean:message key="app.employee.featureExpense" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="24">
        <strong><bean:message key="app.employee.featureSystem" />:</strong>         
      </logic:equal>  
    </td>
    <td colspan ="3">
      <bean:write name="attachmentForm" property="sourceName" />
    </td>
  </tr>
  <tr>
    <td align="right">
      <bean:message key="app.attachment.name" />
    </td>
    <td align="left">
      <html:file property="theFile" size="90" maxlength="120" /> 
    </td>
  </tr>
  <tr>
    <td align="center" colspan="2">
      <html:submit>Upload File</html:submit>
    </td>
  </tr>
  </html:form>    
</logic:equal>

  
<logic:notEqual name="attachmentForm" property="key" value="">   
  <html:form action="<%=attAction%>"> 
    <html:hidden property="key" /> 
    <html:hidden property="sourceTypeKey" />
    <html:hidden property="sourceKey" />    
    <html:hidden property="sourceName" />    
    <tr>
      <td>    
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="1">
        <strong><bean:message key="app.employee.featureCustomer" />:</strong>
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="2">
        <strong><bean:message key="app.employee.featureEmployee" />:</strong>
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="3">
        <strong><bean:message key="app.employee.featureItem" />:</strong>        
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="4">
        <strong><bean:message key="app.employee.featurePet" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="5">
        <strong><bean:message key="app.employee.featureRebate" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="6">
        <strong><bean:message key="app.employee.featureService" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="7">
        <strong><bean:message key="app.employee.featureVendor" />:</strong>         
      </logic:equal>
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="11">
        <strong><bean:message key="app.employee.featurePurchaseOrder" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="13">
        <strong><bean:message key="app.employee.featureVendorInvoice" />:</strong>  
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="18">
        <strong><bean:message key="app.employee.featureExpense" />:</strong>         
      </logic:equal>      
      <logic:equal name="attachmentForm" property="sourceTypeKey" value="24">
        <strong><bean:message key="app.employee.featureSystem" />:</strong>         
      </logic:equal>  
      </td>
      <td colspan ="3">
        <bean:write name="attachmentForm" property="sourceName" />
      </td>
    </tr>
    <tr>
      <td><strong><bean:message key="app.attachment.subject" />:</strong></td>
      <td>     
        <html:text property="subject" size="40" maxlength="40" readonly="<%=attDisableEdit%>" /> 
        <html:errors property="subject" />
      </td>
    </tr>
    <tr>
      <td><strong><bean:message key="app.attachment.attachmentDate" />:</strong></td>
      <td>
        <input type="text" name="attachmentDate" value="<bean:write name="attachmentForm" property="attachmentDate" />" size="10" >
        <a href="javascript:show_calendar('attachmentForm.attachmentDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the date."></a>
      </td>  
    </tr>  
    <tr>  
      <td><strong><bean:message key="app.attachment.name" />:</strong></td>
      <td>     
        <bean:write name="attachmentForm" property="fileName" />
      </td>
    </tr>
    <tr>  
      <td><strong><bean:message key="app.attachment.type" />:</strong></td>
      <td>     
        <bean:write name="attachmentForm" property="fileType" />
      </td>
    </tr>
    <tr>  
      <td><strong><bean:message key="app.attachment.size" />:</strong></td>
      <td>    
        <bean:write name="attachmentForm" property="fileSize" />
      </td>
    </tr>
    <tr>
      <td><strong><bean:message key="app.attachment.createUser" />:</strong></td>
      <td><bean:write name="attachmentForm" property="createUser" /></td>	          	
      <td><strong><bean:message key="app.attachment.createStamp" />:</strong></td>
      <td><bean:write name="attachmentForm" property="createStamp" /></td>	             	    
    </tr>
    <tr>
      <td><strong><bean:message key="app.attachment.updateUser" />:</strong></td>
      <td><bean:write name="attachmentForm" property="updateUser" /></td>	               
      <td><strong><bean:message key="app.attachment.updateStamp" />:</strong></td>
      <td><bean:write name="attachmentForm" property="updateStamp" /></td>	             
    </tr>	
    <tr>
      <td colspan="8" align="right">
        <html:submit value="Save Attachment Values" disabled="<%=attDisableEdit%>" />         
      </td>
    </tr>
  </html:form>     
</logic:notEqual>      
</table>