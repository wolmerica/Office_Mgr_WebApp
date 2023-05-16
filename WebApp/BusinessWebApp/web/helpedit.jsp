<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
   
<%
  String currentColor = "ODD";
  int i = 0;
%>   
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
       <tr>
        <td colspan="5">
	  <hr>
	</td>
      </tr>
      <tr>
        <td>
          <h3>
            <logic:equal name="helpHeadForm" property="levelKey" value="1">
              <bean:message key="app.help.package" />  
            </logic:equal>            
            <logic:equal name="helpHeadForm" property="levelKey" value="2">
              <bean:write name="helpHeadForm" property="operationName" />&nbsp;
              <bean:message key="app.help.operation" />            
            </logic:equal>           
            <logic:equal name="helpHeadForm" property="levelKey" value="3">
              <bean:write name="helpHeadForm" property="operationName" />
            </logic:equal>
          </h3>
        </td>
      </tr>
      <tr align="left">
        <th><bean:message key="app.help.count" /></th>
        <th><bean:message key="app.help.description" /></th>     
          <logic:equal name="helpHeadForm" property="levelKey" value="1">
            <td></td>
          </logic:equal>            
          <logic:equal name="helpHeadForm" property="levelKey" value="2">
	    <td>
	      <a href="HelpPackageList.do?key=<bean:write name="helpHeadForm"
	         property="packageKey" />"><bean:message key="app.help.viewPackage" /></a>
            </td>
          </logic:equal>           
          <logic:equal name="helpHeadForm" property="levelKey" value="3">
	    <td>
	      <a href="HelpOperationList.do?key=<bean:write name="helpHeadForm"
	         property="packageKey" />"><bean:message key="app.help.viewOperation" /></a>
            </td>
          </logic:equal>     
      </tr>
      <tr>
        <td colspan="5">
	  <hr>
	</td>
      <tr>       
      
<logic:notEqual name="helpHeadForm" property="recordCount" value="0"> 
  <!-- iterate over the help -->       
  <logic:iterate id="helpForm"
                 name="helpHeadForm"
                 property="helpForm"
                 scope="session"
                 type="com.wolmerica.help.HelpForm">
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
            <bean:write name="helpForm" property="step" />
	  </td>
	  <td>
            <bean:write name="helpForm" property="description" />
	  </td>
          <logic:equal name="helpHeadForm" property="levelKey" value="1">
	    <td>
	      <a href="HelpOperationList.do?key=<bean:write name="helpForm"
	         property="key" />"><bean:message key="app.help.viewOperation" /></a>
            </td>
            <td></td>
            <td></td>            
          </logic:equal>
          <logic:equal name="helpHeadForm" property="levelKey" value="2">      
            <td>
              <logic:notEqual name="helpForm" property="htmlFileName" value="">                  
                <a href="#" class="helpForm" onclick="popup = putinpopup('<bean:write name="helpForm" 
                  property="documentServerURL" />/<bean:message key="app.help.id" />/<bean:write name="helpForm" 
                  property="key" />/<bean:write name="helpForm" 
                  property="htmlFileName" />'); return false" target="_PDF"><bean:message key="app.text" /></a>
              </logic:notEqual>            
            </td>
            <td>
              <logic:notEqual name="helpForm" property="videoFileName" value="">
                <a href="<bean:write name="helpForm" 
                  property="documentServerURL" />/<bean:message key="app.help.id" />/<bean:write name="helpForm" 
                  property="key" />/<bean:write name="helpForm" 
                  property="videoFileName" />"><bean:message key="app.video" /></a>
              </logic:notEqual>
            </td> 
            <td align="right">
              <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.help.id"/>&sourceKey=<bean:write name="helpForm"
                property="key" />&sourceName=<bean:write name="helpForm" 
                property="description" /> ">[<bean:write name="helpForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."> </a>
            </td>
          </logic:equal>           
          <logic:equal name="helpHeadForm" property="levelKey" value="3">
	  <td></td>
          <td></td>
          <td></td>          
          </logic:equal>                   
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
</table>

