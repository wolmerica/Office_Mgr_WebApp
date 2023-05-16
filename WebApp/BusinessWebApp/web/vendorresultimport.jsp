<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<style type="text/css">
  #statusFrame {position:absolute;
                left: 250px;
                 top: 80px;
                 width: 60px;
                 height: 320px;
                 padding: 1em;
                 z-index: 100
  }
  .hideextra { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
</style>


<script language="JavaScript" type="text/javascript">

  function tryToDownload(url)
  {
     document.VendorResultListBack.backButton.disabled="true";
     document.VendorResultFileImportForm.fileImportButton.disabled="true";
     document.VendorResultWebImportForm.webImportButton.disabled="true";
     oIFrm = document.getElementById('myIFrm');
     oIFrm.src = url;
     oIFrm.style.visibility="visible";
  }
  
  function checkform ( form )
  {
    // ** START **
    if (form.xmlImportName.value == "") {
      alert( "Please select an xml file to import." );
      form.xmlImportName.focus();
      return false ;
    }
    // ** END **
    return true ;
  }
</script>

<%
  String currentColor = "ODD";
  int i = 0;
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
  Integer permOffset = new Integer("0");
  String permAddId = "false";
  String permViewId = "false";
  String permEditId = "false";
  String permDeleteId = "false";
  String permLockAvailableId = "false";
  String permLockedBy = "";

  boolean attMessageFound = false;
  String attResultUploadMessage = "";
  if (request.getAttribute("resultUploadMessage") != null) {
     attMessageFound = true;
     attResultUploadMessage = request.getAttribute("resultUploadMessage").toString();
  }

  boolean attNoLabResults = false;
  if (request.getAttribute("noLabResults") != null) {
     if (request.getAttribute("noLabResults").toString().compareToIgnoreCase("true") == 0) {
       attNoLabResults = true;
     }
  }

%>   
 
<logic:equal name="vendorResultListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="vendorResultListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<div id=statusFrame>
  <iframe id="myIFrm" src="" style="visibility:hidden"width="320" height="60" frameborder="1" scrolling="no" >
  </iframe>
</div>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="VendorResultEntry.do" name="vendorResultListBack" method="post">
        <input type="hidden" name="vendorKey" value="<bean:write name="vendorResultListHeadForm" property="vendorKey" />">
        <input type="submit" name="backButton" value="<bean:message key="app.vendorresult.backMessage" />">
      </form>
    </td>
    <td colspan="6" nowrap>
      <form action="AntechLabResultsLoad.do" name="vendorResultFileImportForm" method="post" onsubmit="return checkform(this);">
        <input type="hidden" name="vendorKey" value="<bean:write name="vendorResultListHeadForm" property="vendorKey" />">
        <strong><bean:message key="app.vendorresult.xmlfilename" />:</strong>
        <input type="hidden" name="xmlFileName" value="<bean:write name="vendorResultListHeadForm" property="xmlFileName" />">
        <span class="ac_holder">
        <input id="xmlImportName" size="40" maxlength="30" value="<bean:write name="vendorResultListHeadForm" property="xmlImportName" />" onFocus="javascript:
        var options = {
		script:'VendorResultXMLFileLookUp.do?json=true&sourceTypeKey=<bean:message key="app.vendorresult.id"/>&sourceKey=<bean:write name="vendorResultListHeadForm" property="vendorKey" />&',
		varname:'lookUpNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.vendorResultFileImportForm.xmlFileName.value=obj.id; }
		};
		var json=new AutoComplete('xmlImportName',options);return true;" value="" />
        </span>
        <input type="submit" name="fileImportButton" value="<bean:message key="app.import.file" />" >
      </form>
    </td>
    <td align="right">
      <form action="AntechLabResultsGet.do" name="vendorResultWebImportForm" method="post">
        <input type="hidden" name="vendorKey" value="<bean:write name="vendorResultListHeadForm" property="vendorKey" />">
        <input type="hidden" name="sourceTypeKey" value="<bean:message key="app.vendorresult.id"/>">
        <input type="submit" name="webImportButton" value="<bean:message key="app.import.web" />" onclick='tryToDownload("./common/download.html")'>
      </form>
    </td>
    <td align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.vendorresult.id"/>&sourceKey=<bean:write name="vendorResultListHeadForm"
        property="vendorKey" />&sourceName=<bean:write name="vendorResultListHeadForm"
        property="vendorName" />" >[<bean:write name="vendorResultListHeadForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."></a>
    </td>
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.vendorresult.patientName" /></th>
    <th><bean:message key="app.vendorresult.customerName" /></th>
    <th><bean:message key="app.vendorresult.vendorName" /></th>
    <th><bean:message key="app.vendorresult.testCode" /></th>
    <th><bean:message key="app.vendorresult.testName" /></th>
    <th><bean:message key="app.vendorresult.status" /></th>
    <th><bean:message key="app.vendorresult.testValue" /></th>
    <th><bean:message key="app.vendorresult.testUnits" /></th>
    <th><bean:message key="app.vendorresult.testRange" /></th>
    <th><bean:message key="app.vendorresult.importStatus" /></th>
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
<logic:notEqual name="vendorResultListHeadForm" property="lastRecord" value="0">
  <logic:iterate id="vendorResultListForm"
                 name="vendorResultListHeadForm"
                 property="vendorResultListForm"
                 scope="session"
                 type="com.wolmerica.vendorresult.VendorResultListForm">
        
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
	  <td>
            <bean:write name="vendorResultListForm" property="attributeToName" />
	  </td>
	  <td>
            <bean:write name="vendorResultListForm" property="customerName" />
	  </td>
	  <td>
            <bean:write name="vendorResultListForm" property="vendorName" />
	  </td>
	  <td>
            <bean:write name="vendorResultListForm" property="testCode" />
	  </td>
	  <td>
            <bean:write name="vendorResultListForm" property="testName" />
	  </td>
	  <td>
            <bean:write name="vendorResultListForm" property="testStatus" />
	  </td>
	  <td>
            <bean:write name="vendorResultListForm" property="testValue" />
            <logic:equal name="vendorResultListForm" property="abnormalStatus" value="L">
              <img src="./images/down_red.gif" width="<bean:message key="app.arrow.width" />" height="<bean:message key="app.arrow.height" />" border="0">
            </logic:equal>
            <logic:equal name="vendorResultListForm" property="abnormalStatus" value="H">
              <img src="./images/up_green.gif" width="<bean:message key="app.arrow.width" />" height="<bean:message key="app.arrow.height" />" border="0">
            </logic:equal>
	  </td>
	  <td>
            <div class="hideextra" style="width:50px">
              <bean:write name="vendorResultListForm" property="testUnits" />
            </div>
<!--
            v:<bean:write name="vendorResultListForm" property="vendorKey" />|
            c:<bean:write name="vendorResultListForm" property="customerKey" />|
            aek:<bean:write name="vendorResultListForm" property="attributeToEntityKey" />|
            aen:<bean:write name="vendorResultListForm" property="attributeToEntityName" />|
            ak:<bean:write name="vendorResultListForm" property="attributeToKey" />|
            an:<bean:write name="vendorResultListForm" property="attributeToName" />|
-->
	  </td>
	  <td>
            <bean:write name="vendorResultListForm" property="testRange" />
	  </td>
          <td>
            <logic:notEqual name="vendorResultListForm" property="errorMessage" value="Success">
              <logic:equal name="vendorResultListForm" property="customerKey" value="0">
                <img src="./images/caution.gif" width="12" height="12" border="0">              
                <a href="CustomerEntry.do?customerName=<bean:write name="vendorResultListForm"
                  property="customerName" />&firstName=<bean:write name="vendorResultListForm"
                  property="customerFirstName" />&lastName=<bean:write name="vendorResultListForm"
                  property="customerLastName" /> ">
                <bean:write name="vendorResultListForm" property="errorMessage" />
                </a>
              </logic:equal>
              <logic:notEqual name="vendorResultListForm" property="customerKey" value="0">
                <logic:equal name="vendorResultListForm" property="attributeToKey" value="0">
                  <img src="./images/caution.gif" width="12" height="12" border="0">                
                  <a href="PetEntry.do?customerKey=<bean:write name="vendorResultListForm"
                    property="customerKey" />&customerName=<bean:write name="vendorResultListForm"
                    property="customerName" />&petName=<bean:write name="vendorResultListForm"
                    property="attributeToName" />&petSexId=<bean:write name="vendorResultListForm"
                    property="petSexId" />&neuteredId=<bean:write name="vendorResultListForm"
                    property="neuteredId" />&petAge=<bean:write name="vendorResultListForm"
                    property="petAge" />&speciesKey=<bean:write name="vendorResultListForm"
                    property="speciesKey" />&speciesName=<bean:write name="vendorResultListForm"
                    property="speciesName" /> ">
                    <bean:write name="vendorResultListForm" property="errorMessage" />
                  </a>
                </logic:equal>
                <logic:notEqual name="vendorResultListForm" property="attributeToKey" value="0">
                  <logic:notEqual name="vendorResultListForm" property="sourceKey" value="-1">
                    <logic:notEqual name="vendorResultListForm" property="key" value="0">
                      <img src="./images/caution.gif" width="12" height="12" border="0">
                      <a href="PurchaseOrderEntry.do?vendorKey=<bean:write name="vendorResultListForm"
                        property="vendorKey" />&sourceTypeKey=<bean:write name="vendorResultListForm"
                        property="attributeToEntityKey" />&sourceKey=<bean:write name="vendorResultListForm"
                        property="attributeToKey" /> ">
                      <bean:write name="vendorResultListForm" property="errorMessage" />
                      </a>
                    </logic:notEqual>
                  </logic:notEqual>
                </logic:notEqual>
              </logic:notEqual>
            </logic:notEqual>
            <logic:equal name="vendorResultListForm" property="errorMessage" value="Success">
              <a href="VendorResultGet.do?key=<bean:write name="vendorResultListForm"
                property="key" /> ">
                <bean:write name="vendorResultListForm" property="errorMessage" />
              </a>
            </logic:equal>
	  </td>
	</tr>
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="10">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="4">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='VendorResultList.do?vendorKey=<bean:write name="vendorResultListHeadForm"
        property="vendorKey" />&customerKey=<bean:write name="vendorResultListHeadForm" 
        property="customerKey" />&sourceTypeKey=<bean:write name="vendorResultListHeadForm" 
        property="sourceTypeKey" />&sourceKey=<bean:write name="vendorResultListHeadForm" 
        property="sourceKey" />&pageNo=<bean:write name="vendorResultListHeadForm"
        property="previousPage" />' "><bean:message key="app.previous" /></button>        
    </td>
    <td colspan="5">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="vendorResultListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="vendorResultListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="vendorResultListHeadForm" property="recordCount" />
    </td>
    <td>
      <button type="button" <%=attDisableNextButton%> onClick="window.location='VendorResultList.do?vendorKey=<bean:write name="vendorResultListHeadForm"
        property="vendorKey" />&customerKey=<bean:write name="vendorResultListHeadForm"
        property="customerKey" />&sourceTypeKey=<bean:write name="vendorResultListHeadForm" 
        property="sourceTypeKey" />&sourceKey=<bean:write name="vendorResultListHeadForm" 
        property="sourceKey" />&pageNo=<bean:write name="vendorResultListHeadForm"
        property="nextPage" />' "><bean:message key="app.next" /></button>        
    </td>  
  </tr>
</table>

<% if (attMessageFound) { %>
  <script type="text/javascript" language="JavaScript">
    alert("<%=attResultUploadMessage%>");
  </script>
<% } %>

<% if (attNoLabResults) { %>
  <script type="text/javascript" language="JavaScript">
    alert("There currently are no new lab results to be downloaded.");
  </script>
<% } %>