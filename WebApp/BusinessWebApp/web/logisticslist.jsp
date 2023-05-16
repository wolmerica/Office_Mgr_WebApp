<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<LINK REL=STYLESHEET HREF="css/style_sheet.css">

<%
  String currentColor = "ODD";
  int i = -1;
  String attAction = "/LogisticsEdit.do";      
  
  boolean attDisableEdit = false;
  if (request.getParameter("disableEdit") != null) {        
     if (request.getParameter("disableEdit").toString().compareToIgnoreCase("true") == 0) {
       attDisableEdit = true;
     }
  }       
      
  String shippingMethodBase = "shippingMethod";
  String trackingNumberBase = "trackingNumber";  
  String shippingMethodError;
  String trackingNumberError;
  
%>   
 
<logic:equal name="logisticsListHeadForm" property="sourceTypeKey" value="11">
  <strong><bean:message key="app.employee.featurePurchaseOrder" />:</strong>         
</logic:equal>
<logic:equal name="logisticsListHeadForm" property="sourceTypeKey" value="13">
  <strong><bean:message key="app.employee.featureVendorInvoice" />:</strong>  
</logic:equal>
<logic:equal name="logisticsListHeadForm" property="sourceTypeKey" value="14">
  <strong><bean:message key="app.employee.featureCustomerInvoice" />:</strong>  
</logic:equal>
&nbsp;&nbsp;
<bean:write name="logisticsListHeadForm" property="sourceName" />   
    
<table width="500" border="0" cellspacing="1" cellpadding="0" >
  <tr align="left">
    <th><bean:message key="app.logistics.shippingMethod" /></th>
    <th><bean:message key="app.logistics.trackingNumber" /></th>
    <th><bean:message key="app.logistics.noteLine1" /></th>
  </tr>     
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
  
<html:form action="<%=attAction%>"> 
  <input type="hidden" name="sourceTypeKey" value="<bean:write name="logisticsListHeadForm" property="sourceTypeKey" />" >
  <input type="hidden" name="sourceKey" value="<bean:write name="logisticsListHeadForm" property="sourceKey" />" >
  <input type="hidden" name="sourceName" value="<bean:write name="logisticsListHeadForm" property="sourceName" />">
  
  <!-- iterate over the logistics records -->       
  <logic:iterate id="logisticsForm"
                 name="logisticsListHeadForm"
                 property="logisticsForm"
                 scope="session"
                 type="com.wolmerica.logistics.LogisticsForm">
        <%
          i++;        
          if ( i % 2 == 0) {
             currentColor = "ODD";
          } 
          else {
             currentColor = "EVEN";
          }
          shippingMethodError = shippingMethodBase + i;
          trackingNumberError = trackingNumberBase + i;
        %>    

	<tr align="left" class="<%=currentColor %>">
          <td>
            <html:select indexed="true" name="logisticsForm" property="shippingMethod" disabled="<%=attDisableEdit%>">
              <html:option value="UPS">UPS</html:option>
              <html:option value="FEDEX">FedEx</html:option>
              <html:option value="USPS">USPS</html:option>
              <html:option value="DHL">DHL</html:option>        
              <html:option value="OTHER">Other</html:option>
            </html:select>    
            <html:errors property="<%=shippingMethodError%>" />                         
          </td>        
	  <td>
            <html:text indexed="true" name="logisticsForm" property="trackingNumber" size="25" maxlength="40" readonly="<%=attDisableEdit%>" />
            <html:errors property="<%=trackingNumberError%>" />
	  </td>
	  <td>
            <html:text indexed="true" name="logisticsForm" property="noteLine1" size="25" maxlength="60" readonly="<%=attDisableEdit%>"/>
	  </td>
       
          <logic:notEqual name="logisticsForm" property="key" value="">
            <td align="left" nowrap>
              <logic:notEqual name="logisticsForm" property="trackingNumber" value="OTHER">
                <logic:equal name="logisticsForm" property="shippingMethod" value="UPS">
                  <a href="<bean:message key="app.packageTrack.url.UPS" /><bean:write name="logisticsForm" 
                    property="trackingNumber" /> "><bean:message key="app.packageTrack.message" /></a>
                </logic:equal>
                <logic:equal name="logisticsForm" property="shippingMethod" value="FEDEX">
                  <a href="<bean:message key="app.packageTrack.url.FEDEX" /><bean:write name="logisticsForm"
                    property="trackingNumber" /> "><bean:message key="app.packageTrack.message" /></a>
                </logic:equal>
                <logic:equal name="logisticsForm" property="shippingMethod" value="USPS">
                  <a href="<bean:message key="app.packageTrack.url.USPS" /><bean:write name="logisticsForm"
                    property="trackingNumber" /> "><bean:message key="app.packageTrack.message" /></a>
                </logic:equal>
                <logic:equal name="logisticsForm" property="shippingMethod" value="DHL">
                  <a href="<bean:message key="app.packageTrack.url.DHL" /><bean:write name="logisticsForm"
                    property="trackingNumber" /> "><bean:message key="app.packageTrack.message" /></a>
                </logic:equal>
              </logic:notEqual>          
            </td>        
	    <td align="right">
              <% if (!(attDisableEdit) ) { %>
	        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="LogisticsDelete.do?key=<bean:write name="logisticsForm"
                  property="key" />&sourceTypeKey=<bean:write name="logisticsListHeadForm"
                  property="sourceTypeKey" />&sourceKey=<bean:write name="logisticsListHeadForm"
                  property="sourceKey" />&sourceName=<bean:write name="logisticsListHeadForm"
                  property="sourceName" />"  onclick="return confirm('<bean:message key="app.logistics.delete.message" />')" ><bean:message key="app.delete" /></a>
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
      <bean:write name="logisticsListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="logisticsListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2" align="right">
      <html:submit value="Save Shipping Info" disabled="<%=attDisableEdit%>"/>
    </td>  
  </html:form>    
  </tr>
</table>
