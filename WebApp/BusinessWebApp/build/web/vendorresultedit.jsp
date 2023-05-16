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
  String attTitle = "app.vendorresult.addTitle";            
  String attAction = "/VendorResultAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }

  String vendorKey = null;
  if (request.getParameter("vendorKey") != null) {
    vendorKey = request.getParameter("vendorKey");
  }

  String fromDate = null;
  if (request.getParameter("fromDate") != null) {
    fromDate = request.getParameter("fromDate");
  }

  String toDate = null;
  if (request.getParameter("toDate") != null) {
    toDate = request.getParameter("toDate");
  }

  String customerKey = null;
  if (request.getParameter("customerKey") != null) {
    customerKey = request.getParameter("customerKey");
  }

  String sourceTypeKey = null;
  if (request.getParameter("sourceTypeKey") != null) {
    sourceTypeKey = request.getParameter("sourceTypeKey");
  }

  String sourceKey = null;
  if (request.getParameter("sourceKey") != null) {
    sourceKey = request.getParameter("sourceKey");
  }

  String testDisplayType = null;
  if (request.getParameter("testDisplayType") != null) {
    testDisplayType = request.getParameter("testDisplayType");
  }

  String pageNo = null;
  if (request.getParameter("pageNo") != null) {
    pageNo = request.getParameter("pageNo");
  }

%> 

<logic:notEqual name="vendorResultForm" property="key" value="">
  <%
    attTitle = "app.vendorresult.editTitle";
    attAction = "/VendorResultEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>    

<logic:notEqual name="vendorResultForm" property="permissionStatus" value="LOCKED">
<% attDisableEdit = true; %>    
<bean:message key="app.readonly"/>
  <logic:notEqual name="vendorResultForm" property="permissionStatus" value="READONLY">
    [<bean:write name="vendorResultForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="VendorResultList.do" name="vendorResultList" method="post">
        <%  if (vendorKey != null) { %>
          <input type="hidden" name="vendorKey" value="<%=vendorKey%>">
        <% } %>
        <%  if (fromDate != null) { %>
          <input type="hidden" name="fromDate" value="<%=fromDate%>">
        <% } %>
        <%  if (toDate != null) { %>
          <input type="hidden" name="toDate" value="<%=toDate%>">
        <% } %>
        <%  if (customerKey != null) { %>
          <input type="hidden" name="customerKey" value="<%=customerKey%>">
        <% } %>          
        <%  if (sourceTypeKey != null) { %>
          <input type="hidden" name="sourceTypeKey" value="<%=sourceTypeKey%>">
        <% } %>          
        <%  if (sourceKey != null) { %>
          <input type="hidden" name="sourceKey" value="<%=sourceKey%>">
        <% } %>
        <%  if (testDisplayType != null) { %>
          <input type="hidden" name="testDisplayType" value="<%=testDisplayType%>">
        <% } %>
        <%  if (pageNo != null) { %>
          <input type="hidden" name="pageNo" value="<%=pageNo%>">
        <% } %>
      <input type="submit" value="<bean:message key="app.vendorresult.backMessage" />">
      </form> 
    </td>
    <td colspan="4">
    </td>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
</table>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <html:form action="<%=attAction%>"> 
      <logic:notEqual name="vendorResultForm" property="key" value="">      
        <html:hidden property="key" />
      </logic:notEqual>
        <%  if (vendorKey != null) { %>
          <input type="hidden" name="vendorKey" value="<%=vendorKey%>">
        <% } %>
        <%  if (fromDate != null) { %>
          <input type="hidden" name="fromDate" value="<%=fromDate%>">
        <% } %>
        <%  if (toDate != null) { %>
          <input type="hidden" name="toDate" value="<%=toDate%>">
        <% } %>
        <%  if (customerKey != null) { %>
          <input type="hidden" name="customerKey" value="<%=customerKey%>">
        <% } %>
        <%  if (sourceTypeKey != null) { %>
          <input type="hidden" name="sourceTypeKey" value="<%=sourceTypeKey%>">
        <% } %>
        <%  if (sourceKey != null) { %>
          <input type="hidden" name="sourceKey" value="<%=sourceKey%>">
        <% } %>
        <%  if (testDisplayType != null) { %>
          <input type="hidden" name="testDisplayType" value="<%=testDisplayType%>">
        <% } %>
        <%  if (pageNo != null) { %>
          <input type="hidden" name="pageNo" value="<%=pageNo%>">
        <% } %>
  <tr>
    <td><strong><bean:message key="app.vendorresult.status" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="status" /></td>
    <td><strong><bean:message key="app.vendorresult.siteName" />:</strong></td>
    <td>
      <html:text property="siteName" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="siteName" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.vendorName" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="vendorName" /></td>
    <td><strong><bean:message key="app.vendorresult.receiveDate" />:</strong></td>
    <td>
      <html:text property="receiveDate" size="15" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="receiveDate" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.customerName" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="customerName" /></td>
    <td><strong><bean:message key="app.vendorresult.receiveAssessionId" />:</strong></td>
    <td>
      <html:text property="receiveAssessionId" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="receiveAssessionId" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.patientType" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="attributeToEntityName" /></td>
    <td><strong><bean:message key="app.vendorresult.resultDate" />:</strong></td>
    <td>
      <html:text property="resultDate" size="15" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="resultDate" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.patientName" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="attributeToName" /></td>
    <td><strong><bean:message key="app.vendorresult.resultAssessionId" />:</strong></td>
    <td>
      <html:text property="resultAssessionId" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="resultAssessionId" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.importFilename" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="importFilename" /></td>
    <td><strong><bean:message key="app.vendorresult.unitCode" />:</strong></td>
    <td>
      <html:text property="unitCode" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="unitCode" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.profileNum" />:</strong></td>
    <td>
      <html:text property="profileNum" size="15" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="profileNum" />
    </td>
    <td><strong><bean:message key="app.vendorresult.unitName" />:</strong></td>
    <td>
      <html:text property="unitName" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="unitName" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.abnormalStatus" />:</strong></td>
    <td>
      <html:text property="abnormalStatus" size="1" maxlength="1" readonly="<%=attDisableEdit%>" />
      <html:errors property="abnormalStatus" />
    </td>
    <td><strong><bean:message key="app.vendorresult.testCode" />:</strong></td>
    <td>
      <html:text property="testCode" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="testCode" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.testStatus" />:</strong></td>
    <td>
      <html:text property="testStatus" size="1" maxlength="1" readonly="<%=attDisableEdit%>" />
      <html:errors property="testStatus" />
    </td>
    <td><strong><bean:message key="app.vendorresult.testName" />:</strong></td>
    <td>
      <html:text property="testName" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="testName" />
    </td>
  </tr>
  <tr>
    <td colspan="2"></td>
    <td><strong><bean:message key="app.vendorresult.testValue" />:</strong></td>
    <td>
      <html:text property="testValue" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="testValue" />
    </td>
  </tr>
  <tr>
    <td colspan="2"></td>
    <td><strong><bean:message key="app.vendorresult.testUnits" />:</strong></td>
    <td>
      <html:text property="testUnits" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="testUnits" />
    </td>
  </tr>
  <tr>
    <td colspan="2"></td>
    <td><strong><bean:message key="app.vendorresult.testRange" />:</strong></td>
    <td>
      <html:text property="testRange" size="35" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="testRange" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.testComment" />:</strong></td>
    <td colspan="3">
      <html:textarea property="testComment" cols="60" rows="3" readonly="<%=attDisableEdit%>" />
      <html:errors property="testComment" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.errorMessage" />:</strong></td>
    <td colspan="3">
      <html:textarea property="errorMessage" cols="60" rows="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="errorMessage" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.createUser" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="createUser" /></td>
    <td><strong><bean:message key="app.vendorresult.createStamp" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendorresult.updateUser" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.vendorresult.updateStamp" />:</strong></td>
    <td><bean:write name="vendorResultForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Results" disabled="<%=attDisableEdit%>" />  
  </html:form> 
    </td>
  </tr>
</table>
