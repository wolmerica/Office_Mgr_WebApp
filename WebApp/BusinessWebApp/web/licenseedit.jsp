<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<LINK REL=STYLESHEET HREF="css/style_sheet.css">

<%
  String currentColor = "ODD";
  int i = -1;
  String attAction = "/LicenseEdit.do";      
  
  boolean attDisableEdit = false;
  if (request.getParameter("disableEdit") != null) {        
     if (request.getParameter("disableEdit").toString().compareToIgnoreCase("true") == 0) {
       attDisableEdit = true;
     }
  }       
      
  String licenseUserBase = "licenseUser";
  String licenseKeyBase = "licenseKey";  
  String licenseUserError;
  String licenseKeyError;
  
%>   
 
<logic:equal name="licenseListHeadForm" property="sourceTypeKey" value="13">
  <strong><bean:message key="app.employee.featureVendorInvoice" />:</strong>  
</logic:equal>
&nbsp;&nbsp;
<bean:write name="licenseListHeadForm" property="sourceName" />   
    
<table width="500" border="0" cellspacing="1" cellpadding="0" >
  <tr align="left">
    <th><bean:message key="app.license.user" /></th>
    <th><bean:message key="app.license.key" /></th>
    <th><bean:message key="app.license.noteLine1" /></th>
  </tr>     
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
  
<html:form action="<%=attAction%>"> 
  <input type="hidden" name="sourceTypeKey" value="<bean:write name="licenseListHeadForm" property="sourceTypeKey" />" >
  <input type="hidden" name="sourceKey" value="<bean:write name="licenseListHeadForm" property="sourceKey" />" >
  <input type="hidden" name="sourceName" value="<bean:write name="licenseListHeadForm" property="sourceName" />">
  
  <!-- iterate over the license records -->       
  <logic:iterate id="licenseForm"
                 name="licenseListHeadForm"
                 property="licenseForm"
                 scope="session"
                 type="com.wolmerica.license.LicenseForm">
        <%
          i++;        
          if ( i % 2 == 0) {
             currentColor = "ODD";
          } 
          else {
             currentColor = "EVEN";
          }
          licenseUserError = licenseUserBase + i;
          licenseKeyError = licenseKeyBase + i;
        %>    

	<tr align="left" class="<%=currentColor %>">
          <td>
            <html:text indexed="true" name="licenseForm" property="licenseUser" size="12" maxlength="20" readonly="<%=attDisableEdit%>" />
            <html:errors property="<%=licenseUserError%>" />                         
          </td>        
	  <td>
            <html:text indexed="true" name="licenseForm" property="licenseKey" size="37" maxlength="30" readonly="<%=attDisableEdit%>" />
            <html:errors property="<%=licenseKeyError%>" />
	  </td>
	  <td>
            <html:text indexed="true" name="licenseForm" property="noteLine1" size="25" maxlength="60" readonly="<%=attDisableEdit%>"/>
	  </td>
       
          <logic:notEqual name="licenseForm" property="key" value="">
	    <td align="right">
              <% if (!(attDisableEdit) ) { %>
	        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <logic:equal name="licenseListHeadForm" property="sourceTypeKey" value="-1">                  
                  <logic:equal name="licenseForm" property="assignId" value="true">
                    <a href="LicenseAssign.do?assignId=true&key=<bean:write name="licenseForm"
                      property="key" />&invoiceTypeKey=<bean:write name="licenseListHeadForm"
                      property="invoiceTypeKey" />&invoiceKey=<bean:write name="licenseListHeadForm"
                      property="invoiceKey" />&sourceTypeKey=<bean:write name="licenseListHeadForm"
                      property="sourceTypeKey" />&sourceKey=<bean:write name="licenseListHeadForm"
                      property="sourceKey" />&sourceName=<bean:write name="licenseListHeadForm"
                      property="sourceName" />" ><bean:message key="app.assign" /></a>
                  </logic:equal>
                  <logic:equal name="licenseForm" property="releaseId" value="true">
                    <a href="LicenseAssign.do?assignId=false&key=<bean:write name="licenseForm"
                      property="key" />&invoiceTypeKey=<bean:write name="licenseListHeadForm"
                      property="invoiceTypeKey" />&invoiceKey=<bean:write name="licenseListHeadForm"
                      property="invoiceKey" />&sourceTypeKey=<bean:write name="licenseListHeadForm"
                      property="sourceTypeKey" />&sourceKey=<bean:write name="licenseListHeadForm"
                      property="sourceKey" />&sourceName=<bean:write name="licenseListHeadForm"
                      property="sourceName" />" ><bean:message key="app.release" /></a>
                  </logic:equal>
                </logic:equal>
                <logic:notEqual name="licenseListHeadForm" property="sourceTypeKey" value="-1">
                  <a href="LicenseDelete.do?key=<bean:write name="licenseForm"
                    property="key" />&sourceTypeKey=<bean:write name="licenseListHeadForm"
                    property="sourceTypeKey" />&sourceKey=<bean:write name="licenseListHeadForm"
                    property="sourceKey" />&sourceName=<bean:write name="licenseListHeadForm"
                    property="sourceName" />"  onclick="return confirm('<bean:message key="app.license.delete.message" />')" ><bean:message key="app.delete" /></a>
                </logic:notEqual>
              <% } %>                  
            </td>
          </logic:notEqual>                          
	</tr>	
  </logic:iterate> 
  <tr>
    <td colspan="7">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
    </td>
    <td align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="licenseListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="licenseListHeadForm" property="recordCount" />
    </td>
    <td colspan="2" align="right">
      <logic:notEqual name="licenseListHeadForm" property="sourceTypeKey" value="-1">
        <html:submit value="Save License Info" disabled="<%=attDisableEdit%>"/>
      </logic:notEqual>
    </td>  
  </html:form>    
  </tr>
</table>
