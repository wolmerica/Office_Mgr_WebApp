<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script src="./js/date_picker.js" type="text/javascript"></script>

<style type="text/css">
  .hideextra { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
</style>

<%
  String currentColor = "ODD";
  int i = 0;
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
  String attDisableImportButton = "";
  Integer permOffset = new Integer("0");
  String permAddId = "false";
  String permViewId = "false";
  String permEditId = "false";
  String permDeleteId = "false";
  String permLockAvailableId = "false";
  String permLockedBy = "";

  String vrListFilterURL = "";
  if (request.getParameter("customerKey") != null) {
    vrListFilterURL += "&customerKey=" + request.getParameter("customerKey");
  }

  if (request.getParameter("sourceTypeKey") != null) {
    vrListFilterURL += "&sourceTypeKey=" + request.getParameter("sourceTypeKey");
  }

  if (request.getParameter("sourceKey") != null) {
    vrListFilterURL += "&sourceKey=" + request.getParameter("sourceKey");
  }

  String pageNo = null;
  String vrPageFilterURL = "";
  if (request.getParameter("pageNo") != null) {
    pageNo = request.getParameter("pageNo");
    vrPageFilterURL = "&pageNo=" + pageNo;
  }

  boolean attMessageFound = false;
  String attResultUploadMessage = "";
  if (request.getAttribute("resultUploadMessage") != null) {
     attMessageFound = true;
     attResultUploadMessage = request.getAttribute("resultUploadMessage").toString();
  }
%>   
 
<logic:equal name="vendorResultListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="vendorResultListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<logic:equal name="vendorResultListHeadForm" property="vendorKey" value="">
   <% attDisableImportButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="vendorResultListHeadForm"
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
    <logic:notEqual name="vendorResultListHeadForm" property="sourceKey" value="">
      <td colspan="8" nowrap>
    </logic:notEqual>
    <logic:equal name="vendorResultListHeadForm" property="sourceKey" value="">
      <td colspan="7" nowrap>
    </logic:equal>    
      <html:form action="VendorResultList.do">
        <%  if (pageNo != null) { %>
          <input type="hidden" name="pageNo" value="<%=pageNo%>">
        <% } %>
        <logic:notEqual name="vendorResultListHeadForm" property="customerKey" value="">
          <logic:equal name="vendorResultListHeadForm" property="sourceKey" value="">
            <html:hidden property="customerKey" />
            <strong><bean:message key="app.vendorresult.customerName" />:</strong>
            <input type="text" name="clientName" size="22" maxlength="40" value="<bean:write name="vendorResultListHeadForm" property="clientName" />" readonly="true" >
          </logic:equal>
          <logic:notEqual name="vendorResultListHeadForm" property="sourceKey" value="">
            <input type="hidden" name="sourceKey" value="<bean:write name="vendorResultListHeadForm" property="sourceKey" />">
            <input type="hidden" name="sourceTypeKey" value="<bean:write name="vendorResultListHeadForm" property="sourceTypeKey" />">
            <strong><bean:message key="app.vendorresult.patientName" />:</strong>
            <input type="text" name="attributeToName" size="22" maxlength="40" value="<bean:write name="vendorResultListHeadForm" property="attributeToName" />" readonly="true" >
          </logic:notEqual>
        </logic:notEqual>
        <logic:equal name="vendorResultListHeadForm" property="customerKey" value="">
          <html:hidden property="vendorKey" />
          <html:hidden property="vendorName" />
          <strong><bean:message key="app.vendorresult.vendorName" />:</strong>
          <span class="ac_holder">
            <input id="vendorNameAC" size="25" maxlength="40" value="<bean:write name="vendorResultListHeadForm" property="vendorName" />" onFocus="javascript:
            var options = {
	        script:'VendorLookUp.do?json=true&trackResultId=1&',
	        varname:'nameFilter',
                json:true,
	        shownoresults:true,
                maxresults:10,
	        callback: function (obj) { document.vendorResultListHeadForm.vendorKey.value=obj.id; document.vendorResultListHeadForm.vendorName.value=obj.value; }
	        };
	        var json=new AutoComplete('vendorNameAC',options);return true;" value="" />
          </span>
        </logic:equal>
        &nbsp;&nbsp;
        <html:hidden property="testDisplayType" />
        <input type="checkbox" name="displayTypeCheckBox" onclick="document.vendorResultListHeadForm.testDisplayType.value=document.vendorResultListHeadForm.displayTypeCheckBox.checked;" ><bean:message key="app.vendorresult.displayType"/>
        &nbsp;&nbsp;
        <strong><bean:message key="app.vendorresult.dateRange" />:</strong>
        <html:text property="fromDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('vendorResultListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.vendorresult.toDate" />&nbsp;</strong>
        <html:text property="toDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('vendorResultListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
        <script type="text/javascript">
          document.vendorResultListHeadForm.displayTypeCheckBox.checked = <bean:write name="vendorResultListHeadForm" property="testDisplayType" />;
          document.vendorResultListHeadForm.testDisplayType.value = document.vendorResultListHeadForm.displayTypeCheckBox.checked;
        </script>
      </html:form>
    </td>
    <logic:equal name="vendorResultListHeadForm" property="customerKey" value="">
      <td colspan="4" align="right">
        <form action="AntechLabResultsImport.do" name="vendorResultImportForm" method="post">
          <input type="hidden" name="fromDate" value="04/01/1993">
          <input type="hidden" name="toDate" value="04/01/1993">
          <input type="hidden" name="vendorKey" value="<bean:write name="vendorResultListHeadForm" property="vendorKey" />" >
          <input type="submit" <%=attDisableImportButton%> value="<bean:message key="app.import.result" /> ">
        </form>
      </td>
    </logic:equal>
  </tr>
  <tr>
    <td colspan="11">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <logic:equal name="vendorResultListHeadForm" property="testDisplayType" value="false">
      <th><bean:message key="app.vendorresult.resultDate" /></th>
      <th><bean:message key="app.vendorresult.receiveDate" /></th>
      <th><bean:message key="app.vendorresult.patientName" /></th>
      <th><bean:message key="app.vendorresult.customerName" /></th>
      <th><bean:message key="app.vendorresult.vendorName" /></th>
      <th><bean:message key="app.vendorresult.orderKey" /></th>
      <th><bean:message key="app.vendorresult.orderNum" /></th>
      <th><bean:message key="app.vendorresult.status" /></th>
    </logic:equal>
    <logic:equal name="vendorResultListHeadForm" property="testDisplayType" value="true">
      <th><bean:message key="app.vendorresult.resultDate" /></th>
      <th><bean:message key="app.vendorresult.status" /></th>
      <th><bean:message key="app.vendorresult.patientName" /></th>
      <th><bean:message key="app.vendorresult.customerName" /></th>
      <th><bean:message key="app.vendorresult.testCode" /></th>
      <th><bean:message key="app.vendorresult.testName" /></th>
      <th><bean:message key="app.vendorresult.testValue" /></th>
      <th><bean:message key="app.vendorresult.testRange" /></th>
      <th><bean:message key="app.vendorresult.testUnits" /></th>
    </logic:equal>
    <th colspan="2" align="right">
      <!-- No Add Option -->
    </th>
  </tr>
  <tr>
    <td colspan="11">
      <hr>
    </td>
  </tr>
<logic:notEqual name="vendorResultListHeadForm" property="lastRecord" value="0">
  <logic:iterate id="vendorResultListForm"
                 name="vendorResultListHeadForm"
                 property="vendorResultListForm"
                 scope="session"
                 type="com.wolmerica.vendorresult.VendorResultListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="vendorResultListHeadForm"
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
          <logic:equal name="vendorResultListHeadForm" property="testDisplayType" value="false">
            <td>
              <bean:write name="vendorResultListForm" property="resultDate" />
	    </td>
            <td>
             <bean:write name="vendorResultListForm" property="receiveDate" />
            </td>
	    <td>
              <bean:write name="vendorResultListForm" property="attributeToName" />
	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="customerName" />
	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="vendorName" />
	    </td>
	    <td align="center">
              <bean:write name="vendorResultListForm" property="purchaseOrderKey" />
	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="purchaseOrderNum" />
	    </td>
	    <td colspan="2">
              <bean:write name="vendorResultListForm" property="status" />
	    </td>
          </logic:equal>
          <logic:equal name="vendorResultListHeadForm" property="testDisplayType" value="true">
            <td>
              <bean:write name="vendorResultListForm" property="resultDate" />
 	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="status" />
	    </td>
            <td>
              <bean:write name="vendorResultListForm" property="attributeToName" />
	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="customerName" />
	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="testCode" />
	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="testName" />
	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="testValue" />
              <logic:equal name="vendorResultListForm" property="abnormalStatus" value="A">
                <img src="./images/caution.gif" width="14" height="14" border="0">
              </logic:equal>
              <logic:equal name="vendorResultListForm" property="abnormalStatus" value="H">
                <img src="./images/up_green.gif" width="<bean:message key="app.arrow.width" />" height="<bean:message key="app.arrow.height" />" border="0">
              </logic:equal>
              <logic:equal name="vendorResultListForm" property="abnormalStatus" value="L">
                <img src="./images/down_red.gif" width="<bean:message key="app.arrow.width" />" height="<bean:message key="app.arrow.height" />" border="0">
              </logic:equal>
              <logic:equal name="vendorResultListForm" property="abnormalStatus" value="*">
                <img src="./images/caution.gif" width="14" height="14" border="0">
              </logic:equal>
	    </td>
	    <td>
              <bean:write name="vendorResultListForm" property="testRange" />
	    </td>	    
	    <td>	    
              <div class="hideextra" style="width:50px">
                <bean:write name="vendorResultListForm" property="testUnits" />
              </div>
	    </td>
          </logic:equal>
	  <td>
            <% if (permViewId.equalsIgnoreCase("true")) { %>
              <a href="VendorResultGet.do?key=<bean:write name="vendorResultListForm"
                property="key" />&vendorKey=<bean:write name="vendorResultListHeadForm"
                property="vendorKey" />&fromDate=<bean:write name="vendorResultListHeadForm"
                property="fromDate" />&toDate=<bean:write name="vendorResultListHeadForm"
                property="toDate" />&testDisplayType=<bean:write name="vendorResultListHeadForm"
                property="testDisplayType" /><%=vrPageFilterURL%><%=vrListFilterURL%>" ><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="VendorResultGet.do?key=<bean:write name="vendorResultListForm"
                property="key" />&vendorKey=<bean:write name="vendorResultListHeadForm"
                property="vendorKey" />&fromDate=<bean:write name="vendorResultListHeadForm"
                property="fromDate" />&toDate=<bean:write name="vendorResultListHeadForm"
                property="toDate" />&testDisplayType=<bean:write name="vendorResultListHeadForm"
                property="testDisplayType" /><%=vrPageFilterURL%><%=vrListFilterURL%>" ><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>
	  </td>
	  <td align="right">
            <% if (permDeleteId.equalsIgnoreCase("true")) { %>
              <a href="VendorResultDelete.do?key=<bean:write name="vendorResultListForm"
                property="key" />&vendorKey=<bean:write name="vendorResultListHeadForm"
                property="vendorKey" />&fromDate=<bean:write name="vendorResultListHeadForm"
                property="fromDate" />&toDate=<bean:write name="vendorResultListHeadForm"
                property="toDate" />&testDisplayType=<bean:write name="vendorResultListHeadForm"
                property="testDisplayType" /><%=vrPageFilterURL%><%=vrListFilterURL%>" onclick="return confirm('<bean:message key="app.vendorresult.delete.message" />')" ><bean:message key="app.delete" /></a>
            <% } %>
	  </td>
	</tr>
        <logic:equal name="vendorResultListHeadForm" property="testDisplayType" value="true">
          <logic:notEqual name="vendorResultListForm" property="testComment" value="">
            <tr align="left" class="<%=currentColor %>">
                <td align="right" valign="top">
                <img src="./images/info.gif" width="14" height="14" border="0">
              </td>
              <td colspan="9">
                <bean:write name="vendorResultListForm" property="testComment" />
              </td>
            </tr>
          </logic:notEqual>
        </logic:equal>
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="11">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='VendorResultList.do?vendorKey=<bean:write name="vendorResultListHeadForm"
        property="vendorKey" />&fromDate=<bean:write name="vendorResultListHeadForm"
        property="fromDate" />&toDate=<bean:write name="vendorResultListHeadForm"
        property="toDate" />&testDisplayType=<bean:write name="vendorResultListHeadForm"
        property="testDisplayType" />&pageNo=<bean:write name="vendorResultListHeadForm"
        property="previousPage" /><%=vrListFilterURL%>' "><bean:message key="app.previous" /></button>
    </td>
    <logic:equal name="vendorResultListHeadForm" property="testDisplayType" value="true">
      <td colspan="8" align="center">
    </logic:equal>
    <logic:equal name="vendorResultListHeadForm" property="testDisplayType" value="false">
      <td colspan="7" align="center">
    </logic:equal>
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="vendorResultListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="vendorResultListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="vendorResultListHeadForm" property="recordCount" />
    </td>
    <td colspan="2" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='VendorResultList.do?vendorKey=<bean:write name="vendorResultListHeadForm"
        property="vendorKey" />&fromDate=<bean:write name="vendorResultListHeadForm"
        property="fromDate" />&toDate=<bean:write name="vendorResultListHeadForm"
        property="toDate" />&testDisplayType=<bean:write name="vendorResultListHeadForm"
        property="testDisplayType" />&pageNo=<bean:write name="vendorResultListHeadForm"
        property="nextPage" /><%=vrListFilterURL%>' "><bean:message key="app.next" /></button>
    </td>
  </tr>
</table>

<% if (attMessageFound) { %>
  <script type="text/javascript" language="JavaScript">
    alert("<%=attResultUploadMessage%>");
  </script>
<% } %>