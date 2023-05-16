<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<%
  String attTitle = "app.pricetype.addtitle";            
  String attAction = "/PriceTypeAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="priceTypeForm" property="key" value="">
  <%
    attTitle = "app.pricetype.editTitle";
    attAction = "/PriceTypeEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="priceTypeForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="priceTypeForm" property="permissionStatus" value="READONLY">
    [<bean:write name="priceTypeForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="5">
      <form action="PriceTypeList.do" name="priceTypeList" method="post">                                  
      <input type="submit" value="<bean:message key="app.pricetype.backMessage" />">
      </form> 
    </td>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>">  
   <logic:notEqual name="priceTypeForm" property="key" value="">   
      <html:hidden property="key" />
   </logic:notEqual>        
  <tr>
    <td><strong><bean:message key="app.pricetype.name" />:</strong></td>
    <td>
      <html:text property="name" readonly="<%=attDisableEdit%>" />
      <html:errors property="name" />       
    </td>
    <td><strong><bean:message key="app.pricetype.precedence" />:</strong></td>
    <td>
      <html:text property="precedence" size="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="precedence" />   
    </td>
  </tr>
    <tr>
      <td><strong><bean:message key="app.pricetype.fullSizeId" />:</strong></td>
      <td>
        <html:radio property="fullSizeId" value="false" disabled="<%=attDisableEdit%>"/>No
        <html:radio property="fullSizeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
        <html:errors property="fullSizeId" />                   
      </td>
      <logic:equal name="priceTypeForm" property="domainId" value="true">
        <td><strong><bean:message key="app.pricetype.serviceRate" />:</strong></td>
        <td>
          <html:text property="serviceRate" size="4" maxlength="5" readonly="<%=attDisableEdit%>" />
          <bean:message key="app.localePercent" />
          <html:errors property="serviceRate" />                   
        </td>     
      </logic:equal>      
      <logic:equal name="priceTypeForm" property="domainId" value="false">      
        <td><strong><bean:message key="app.pricetype.unitCostBaseId" />:</strong></td>
        <td>
          <html:radio property="unitCostBaseId" value="false" disabled="<%=attDisableEdit%>"/>No
          <html:radio property="unitCostBaseId" value="true" disabled="<%=attDisableEdit%>"/>Yes
          <html:errors property="unitCostBaseId" />                   
        </td>
      </logic:equal>        
    </tr>
  <tr>
    <td><strong><bean:message key="app.pricetype.blueBookId" />:</strong></td>
    <td>
      <html:radio property="blueBookId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="blueBookId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="blueBookId" />                   
    </td> 
    <td><strong><bean:message key="app.pricetype.markUpRate" />:</strong></td>
    <td>
      <html:text property="markUpRate" size="4" maxlength="5" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />
      <html:errors property="markUpRate" />                   
    </td>     
  </tr>  
  <tr>
    <td><strong><bean:message key="app.pricetype.domainId" />:</strong></td>
    <td>
      <html:radio property="domainId" value="false" disabled="true" />No
      <html:radio property="domainId" value="true" disabled="true" />Yes
      <html:errors property="domainId" />                   
    </td>   
    <td><strong><bean:message key="app.pricetype.activeId" />:</strong></td>
    <td>
      <html:radio property="activeId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="activeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="activeId" />                   
    </td> 
    <td></td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.pricetype.createUser" />:</strong></td>
    <td><bean:write name="priceTypeForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.pricetype.createStamp" />:</strong></td>
    <td><bean:write name="priceTypeForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.pricetype.updateUser" />:</strong></td>
    <td><bean:write name="priceTypeForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.pricetype.updateStamp" />:</strong></td>
    <td><bean:write name="priceTypeForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save PriceType Values" disabled="<%=attDisableEdit%>" />             
</html:form> 
    </td>
  </tr>
</table>