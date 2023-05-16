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
.collapsible {
          display: none; /* Only important part */
          border: dashed 1px silver;
          padding: 10px;
}
</style>

<script type="text/javascript" language="JavaScript"><!--
//preload image
var collimg = new Image();
collimg.src = "./images/collapse.gif";
var expimg = new Image();
collimg.src = "./images/expand.gif";

function ShowHideLayer(boxID) {
  /* Obtain reference for the selected boxID layer and its button */
  var box = document.getElementById("box"+boxID);
  var boxbtn = document.getElementById("btn"+boxID);

  /* If the selected box is currently invisible, show it */
  if(box.style.display == "none" || box.style.display=="") {
    box.style.display = "block";
    boxbtn.src = "./images/collapse.gif";
  }
  /* otherwise hide it */
  else {
    box.style.display = "none";
    boxbtn.src = "./images/expand.gif";
  }
}

function HideContent(t,d) {
  document.getElementById(d).style.display = "none";
  document.getElementById(t).style.fontSize = '12';
  document.getElementById(t).style.backgroundColor = '#fff';
}
function ShowContent(t,d) {
  document.getElementById(d).style.display = "block";
  document.getElementById(t).style.fontSize = '15';
  document.getElementById(t).style.backgroundColor = '#ccc';
}
function ReverseContentDisplay(t,d) {
  if(document.getElementById(d).style.display == "none") { document.getElementById(d).style.display = "block"; }
  else { document.getElementById(d).style.display = "none"; }
}

function defaultField(what,text) {
  if (what.value == '')
     what.value = text;
}     

//--></script>
<%
  String currentColor = "ODD";
  int i = -1;   
  String attTitle = "app.customer.addTitle";            
  String attAction = "/CustomerAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (request.getAttribute("disableEdit") != null) {
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }
%> 

<logic:notEqual name="customerForm" property="key" value="">
  <%
    attTitle = "app.customer.editTitle";
    attAction = "/CustomerEdit";
    attKeyEdit = true;
  %>
  <logic:equal name="customerForm" property="activeId" value="false">
    <% attDisableEdit = true; %>
  </logic:equal>
</logic:notEqual>   

<logic:notEqual name="customerForm" property="permissionStatus" value="LOCKED">
<% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="customerForm" property="permissionStatus" value="READONLY">
    [<bean:write name="customerForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <button type="button" onClick="window.location='CustomerList.do?activeId=true' "><bean:message key="app.customer.backMessage" /></button>
    </td>
    <logic:notEqual name="customerForm" property="key" value="">
    <td colspan="4" align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.customer.id"/>&sourceKey=<bean:write name="customerForm"
              property="key" />&sourceName=<bean:write name="customerForm"
              property="clientName" />" >[<bean:write name="customerForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."> </a>
    </td>
    </logic:notEqual>    
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <!-- Display tabs to show and hide customer content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff;font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('accountTab','accountInfo');HideContent('scheduleTab','scheduleInfo');HideContent('formTab','formInfo');HideContent('addressTab','addressInfo');">
              <bean:message key="app.customer.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="customerForm" property="key" value="">
            <td align="center">
              <div id="accountTab" style="background:#fff; font:12;">
                <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('accountTab','accountInfo');HideContent('scheduleTab','scheduleInfo');HideContent('formTab','formInfo');HideContent('addressTab','addressInfo');">
                  <bean:message key="app.customer.accountTab" />
                </a>
              </div>
            </td>
            <td align="center">
            <div id="scheduleTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');HideContent('accountTab','accountInfo');ShowContent('scheduleTab','scheduleInfo');HideContent('formTab','formInfo');HideContent('addressTab','addressInfo');">
              <bean:message key="app.customer.scheduleTab" />
              </a>
            </div>
            </td>
            <td align="center">
            <div id="formTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');HideContent('accountTab','accountInfo');HideContent('scheduleTab','scheduleInfo');ShowContent('formTab','formInfo');HideContent('addressTab','addressInfo');">
                <bean:write name="customerForm" property="attributeToEntity" />
              </a>
            </div>
            </td>
            <td align="center">
            <div id="addressTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');HideContent('accountTab','accountInfo');HideContent('scheduleTab','scheduleInfo');HideContent('formTab','formInfo');ShowContent('addressTab','addressInfo');">
                <bean:message key="app.customer.addressTab" />
              </a>
            </div>
            </td>
          </logic:notEqual>          
        </tr>
      </table>
    </td>
    <logic:notEqual name="customerForm" property="key" value="">    
      <td align="right" colspan="6">
        <button type="button" onClick="window.location='CustomerInvoiceReportEntry.do?customerKey=<bean:write name="customerForm"
          property="key" />' "><bean:message key="app.customer.viewActivity" /></button>
      </td>      
    </logic:notEqual>
  </tr>
</table>

<div id="mainInfo" style="display:none;">  
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <html:form action="<%=attAction%>" onsubmit="defaultField(customerForm.clientName, customerForm.lastName.value + ', ' + customerForm.firstName.value); defaultField(customerForm.shipTo, customerForm.firstName.value + ' ' + customerForm.lastName.value); defaultField(customerForm.contactName, customerForm.shipTo.value); defaultField(customerForm.acctName, customerForm.shipTo.value);"> 
  <logic:notEqual name="customerForm" property="key" value="">
    <html:hidden property="key" />  
  </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.customer.codeNum" />:</strong></td>
    <td>
      <html:text property="codeNum" size="10" maxlength="10" readonly="true" />
      <html:errors property="codeNum" />       
    </td>
    <td><strong><bean:message key="app.customer.clinicId" />:</strong></td>
    <td>
      <html:radio property="clinicId" value="false" disabled="<%=attKeyEdit%>" />No
      <html:radio property="clinicId" value="true" disabled="<%=attKeyEdit%>" />Yes
      <html:errors property="clinicId" />                    
    </td> 	         
  </tr>       
  <tr>
    <td><strong><bean:message key="app.customer.firstName" />:</strong></td>
    <td>
      <html:text property="firstName" size="40" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="firstName" />             
    </td>  
    <td><strong><bean:message key="app.customer.lastName" />:</strong></td>
    <td>
      <html:text property="lastName" size="40" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="lastName" />       
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.customer.clientName" />:</strong></td>
    <td>
      <html:text property="clientName" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="clientName" />                   
    </td>
    <td><strong><bean:message key="app.customer.shipTo" />:</strong></td>
    <td>
      <html:text property="shipTo" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="shipTo" />                   
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.customer.contactName" />:</strong></td>
    <td>
      <html:text property="contactName" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="contactName" />             
    </td>  
    <td><strong><bean:message key="app.customer.lineOfBusiness" />:</strong></td>
    <td>
      <html:text property="lineOfBusiness" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="lineOfBusiness" />       
    </td>
  </tr>    
  <tr>
    <td><strong><bean:message key="app.customer.address" />:</strong></td>
    <td>
      <html:text property="address" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="address" />                   
    </td>
    <td><strong><bean:message key="app.customer.address2" />:</strong></td>
    <td>
      <html:text property="address2" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="address2" />             
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.customer.city" />:</strong></td>
    <td>
      <html:text property="city" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="city" />                   
    </td>
    <td><strong><bean:message key="app.customer.state" />:</strong></td>
    <td>
      <html:text property="state" size="2" maxlength="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="state" />             
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.customer.zip" />:</strong></td>
    <td>
      <html:text property="zip" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="zip" />    
      <logic:notEqual name="customerForm" property="key" value="">
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          
        <a href="blank" onclick="popup = putinpopup('<bean:message key="app.showMap.url" /><bean:write name="customerForm" 
          property="address" /><bean:message key="app.showMap.ctz" /><bean:write name="customerForm" 
          property="city" /><bean:message key="app.showMap.zip" /><bean:write name="customerForm" 
          property="zip" />'); return false" target="_blank"><bean:message key="app.map" /></a>    
      </logic:notEqual>
    </td>
    <td><strong><bean:message key="app.customer.phoneNum" />:</strong></td>
    <td>
      <html:text property="phoneNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="phoneNum" />   
      &nbsp;&nbsp;
      <strong><bean:message key="app.customer.phoneExt" />:</strong>
      <html:text property="phoneExt" size="4" maxlength="5" readonly="<%=attDisableEdit%>" />
      <html:errors property="phoneExt" />   
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.customer.faxNum" />:</strong></td>
    <td>
      <html:text property="faxNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="faxNum" />             
    </td>
    <td><strong><bean:message key="app.customer.mobileNum" />:</strong></td>
    <td>
      <html:text property="mobileNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="mobileNum" />
      &nbsp;&nbsp;
      <strong><bean:message key="app.customer.reminderPref" />:</strong>
      <html:select property="reminderPrefKey" disabled="<%=attDisableEdit%>">
        <html:option value="0"><bean:message key="app.customer.phoneNum" /></html:option>
        <html:option value="1"><bean:message key="app.customer.mobileNum" /></html:option>        
        <html:option value="2"><bean:message key="app.customer.email" /></html:option>
        <html:option value="3"><bean:message key="app.customer.textmsg" /></html:option>
      </html:select>    
      <html:errors property="reminderPrefKey" />      
    </td>    
  </tr>	  
  <tr>
    <td><strong><bean:message key="app.customer.webSite" />:</strong></td>
    <td>
      <html:text property="webSite" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="webSite" />       
    </td>
    <td><strong><bean:message key="app.customer.email" />:</strong></td>
    <td>
      <html:text property="email" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="email" />       
    </td>    
  </tr>  
  <tr>
    <td><strong><bean:message key="app.customer.referredBy" />:</strong></td>
    <td>
      <html:text property="referredBy" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="referredBy" />                   
    </td>
    <td><strong><bean:message key="app.customer.email2" />:</strong></td>
    <td>
      <html:text property="email2" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="email2" />       
    </td>    
  </tr>    
  <tr>
    <td><strong><bean:message key="app.customer.primaryKey" />:</strong></td>
    <td>    
      <html:hidden property="primaryKey" />
      <html:hidden property="primaryName" />
      <span class="ac_holder">
      <input id="primaryNameAC" size="40" maxlength="40" value="<bean:write name="customerForm" property="primaryName" />" onFocus="javascript:
      var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.customerForm.primaryKey.value=obj.id; document.customerForm.primaryName.value=obj.value; }
		};
		var json=new AutoComplete('primaryNameAC',options);return true;" value="" />
      </span>
      <html:errors property="primaryKey" />
    </td>  
    <td><strong><bean:message key="app.customer.customerTypeKey" />:</strong></td>
    <td>
      <html:select property="customerTypeKey" disabled="<%=attDisableEdit%>">
      <!-- iterate over the customer type -->       
      <logic:iterate id="customerTypeForm"
                 name="customerForm"
                 property="customerTypeForm"
                 scope="session"
                 type="com.wolmerica.customertype.CustomerTypeForm">         
        <html:option value="<%=customerTypeForm.getKey()%>" > <%=customerTypeForm.getName()%> </html:option>
      </logic:iterate>
      </html:select>
      <html:errors property="customerTypeKey" />
    </td>
  </tr>		
  <tr>
    <td><strong><bean:message key="app.customer.acctNum" />:</strong></td>
    <td>
      <html:text property="acctNum" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="acctNum" />             
    </td>  
    <td><strong><bean:message key="app.customer.reportId" />:</strong></td>
    <td>
      <html:radio property="reportId" value="false" disabled="<%=attDisableEdit%>" />No
      <html:radio property="reportId" value="true" disabled="<%=attDisableEdit%>" />Yes
      <html:errors property="reportId" />                    
    </td>     
  </tr>
  <tr>
    <td><strong><bean:message key="app.customer.acctName" />:</strong></td>
    <td>
      <html:text property="acctName" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="acctName" />                   
    </td>
    <td><strong><bean:message key="app.customer.ledgerId" />:</strong></td>
    <td>
      <html:radio property="ledgerId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="ledgerId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="ledgerId" />                   
    </td>    
  </tr>	  
  <tr>
    <td><strong><bean:message key="app.customer.noteLine1" />:</strong></td>
    <td>   
      <html:text property="noteLine1" size="40" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine1" />
    </td>       
    <td><strong><bean:message key="app.customer.activeId" />:</strong></td>
    <td>
      <html:radio property="activeId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="activeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="activeId" />                   
    </td>   
  </tr>
  <tr>
    <td><strong><bean:message key="app.customer.noteLine2" />:</strong></td>
    <td>   
      <html:text property="noteLine2" size="40" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine2" />
    </td>
    <td><strong><bean:message key="app.customer.loginStamp" />:</strong></td>
    <td>
      <bean:write name="customerForm" property="loginStamp" />
      &nbsp;&nbsp;
      <bean:write name="customerForm" property="loginIpAddress" />
    </td>
  </tr>  
  <tr>
    <td><strong><bean:message key="app.customer.createUser" />:</strong></td>
    <td><bean:write name="customerForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.customer.createStamp" />:</strong></td>
    <td><bean:write name="customerForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.customer.updateUser" />:</strong></td>
    <td><bean:write name="customerForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.customer.updateStamp" />:</strong></td>
    <td><bean:write name="customerForm" property="updateStamp" /></td>	     
  </tr>
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Customer Values" disabled="<%=attDisableEdit%>" />         
      </html:form> 
    </td>
  </tr>  
  </table>
</div>

<% if (request.getParameter("tabId") == null) { %>
  <script type="text/javascript" language="JavaScript"><!--
    ShowContent('mainTab', 'mainInfo');
  //--></script>
<% } %>

<logic:notEqual name="customerForm" property="key" value=""> 
<div id="accountInfo" style="display:none;">
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <tr>
    <td></td>  
    <td colspan="4">
      <strong><bean:message key="app.customer.clientName" />:</strong>
      &nbsp;&nbsp;
      <bean:write name="customerForm" property="clientName" />
    </td>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>    
  <logic:equal name="customerForm" property="ledgerId" value="true">  
  <tr>
    <td></td>
    <td><strong><bean:message key="app.customer.ledgerBalanceAmount" />:</strong></td>
    <td>
      <bean:write name="customerForm" property="ledgerBalanceDate" />    
    </td>
    <td>
      <bean:message key="app.localeCurrency" />  
      <bean:write name="customerForm" property="ledgerBalanceAmount" />
    </td>
  </tr>
  </logic:equal>
  <tr>
    <td></td>   
    <td><strong><bean:message key="app.customer.lastPaymentAmount" />:</strong></td>
    <td>
      <bean:write name="customerForm" property="lastPaymentDate" />    
    </td>    
    <td>
      <bean:message key="app.localeCurrency" />    
      <bean:write name="customerForm" property="lastPaymentAmount" />
    </td>
    <logic:equal name="customerForm" property="clinicId" value="false">
    <td>
      <form action="CustomerAccountingListEntry.do" name="customerAccountingList" method="post">
        <input type="hidden" name="customerKeyFilter" value="<bean:write name="customerForm" property="key" />">
        <input type="submit" value="<bean:message key="app.customer.accountingListMessage" />">
      </form>     
    </td>    
    </logic:equal>
  </tr>
  <tr>
    <td></td>  
    <td><strong><bean:message key="app.customer.accountBalanceAmount" />:</strong></td>
    <td>
      <bean:write name="customerForm" property="accountBalanceDate" />    
    </td>    
    <td>
      <bean:message key="app.localeCurrency" />
      <bean:write name="customerForm" property="accountBalanceAmount" />
    </td>
      <logic:equal name="customerForm" property="clinicId" value="false"> 
      <logic:notEqual name="customerForm" property="accountBalanceAmount" value="0.00">
      <td>
        <form action="CustomerAccountingEntry.do" name="customerAccountingEntry" method="post">                                  
          <input type="hidden" name="key" value="<bean:write name="customerForm" property="key" />">
          <input type="hidden" name="accountBalanceAmount" value="<bean:write name="customerForm" property="accountBalanceAmount" />">
          <input type="hidden" name="customerKeyFilter" value="<bean:write name="customerForm" property="key" />">          
          <logic:equal name="customerForm" property="allowPaymentId" value="true">          
            <input type="submit" value="<bean:message key="app.customer.accountingPaymentMessage" />">
          </logic:equal>
          <logic:equal name="customerForm" property="allowRefundId" value="true">            
            <input type="submit" value="<bean:message key="app.customer.accountingRefundMessage" />">
          </logic:equal>
        </form>     
      </td>      
      </logic:notEqual>      
    </logic:equal>
  </tr>      
  </table>
</div>

  <% if (request.getParameter("tabId") != null) { %>
    <% if (request.getParameter("tabId").toString().compareToIgnoreCase("accountTab") == 0) { %>
      <script type="text/javascript" language="JavaScript"><!--
        ShowContent('accountTab', 'accountInfo');
      //--></script>
    <% } %>
  <% } %>
</logic:notEqual>  

<logic:notEqual name="customerForm" property="key" value="">  
<!-- ================================================================= -->
<!-- Display of the customer schedule                                  -->
<!-- ================================================================= -->
<div id="scheduleInfo" style="display:none;">     
  <div style="width:575px; border-width: 0px; border-style: solid; border-color: black; ">    
  <br>  
  <strong><bean:message key="app.customer.clientName" />:</strong>
  &nbsp;&nbsp;
  <bean:write name="customerForm" property="clientName" />

  <br>  
  <logic:equal name="resourceInstanceListHeadForm" property="lastRecord" value="0">
    <br>
    <bean:message key="app.schedule.noResults" />
  </logic:equal>
  
  <logic:notEqual name="resourceInstanceListHeadForm" property="lastRecord" value="0"> 
       
    <jsp:useBean id="workOrderForm" scope="session" 
      class="com.wolmerica.workorder.WorkOrderForm" />

   <%
       String eventTOD = "";     
       String startDate = "";
       int startHour = 8;
       int startMinute = 0;
       String startTime = "";
       String endDate = "";
       int endHour = 10;
       int endMinute = 0;
       String endTime = "";

       String scheduleKey = "";
       String service = "";
       String subject = "";
       String attributeToEntity = "";     
       String attributeToName = "";
       String location = "";
       
       String pStartDate = "";
       String pStartTime = "";
       String pEndDate = "";
       String pEndTime = "";
       String pSubject = "";
       String pAttributeToEntity = "";
       String pAttributeToName = "";
       String pLocation = "";
       
       int x = 0;
       int eventCnt = 0;
       int y = 0;     
    %>
    
    <logic:iterate id="resourceInstanceForm"
                   name="resourceInstanceListHeadForm"
                   property="resourceInstanceForm"
                   scope="session"
                   type="com.wolmerica.resourceinstance.ResourceInstanceForm">  

      <% if (x == 0) { %>
        <br>
      <% } else { %>
        <hr>
      <% } 
        x++; %>
      &nbsp;
      <a href="javascript:;" onclick="ShowHideLayer('<%=x%>');"><img src="images/expand.gif" alt="Expand Me" name="btn<%=x%>" width="9" height="9" border="0" id="btn<%=x%>" /></a>
      &nbsp;<strong><bean:write name="resourceInstanceForm" property="startDate" /></strong><br>
      <div id="box<%=x%>" class="collapsible">
            
        <logic:notEqual name="resourceInstanceForm" property="eventCount" value="0"> 
      
        <% 
           pStartDate = "";
           pStartTime = "";
           pEndDate = "";
           pEndTime = "";
           pSubject = "";
           pAttributeToEntity = "";
           pAttributeToName = "";
           pLocation = "";
        
           eventCnt = new Integer(resourceInstanceForm.getEventCount()).intValue();          
           y = 0;
           while( y < eventCnt) { 
//--------------------------------------------------------------------------------
// Cast the array value into a workorder form to be evaluated for display purposes.
//--------------------------------------------------------------------------------
             workOrderForm = (com.wolmerica.workorder.WorkOrderForm) resourceInstanceForm.getWorkOrderForm().get(y);
             eventTOD = "AM";                       
             startDate = workOrderForm.getStartDate();
             startHour = new Integer(workOrderForm.getStartHour()).intValue();
             startMinute = new Integer(workOrderForm.getStartMinute()).intValue();
             if (startHour > 11)
               eventTOD = "PM";
             if (startHour > 12)
               startHour = startHour - 12;
             startTime = startHour + ":";
             if (startMinute < 10)
               startTime = startTime + "0";
             startTime = startTime + startMinute + eventTOD;
             
             eventTOD = "AM";                 
             endDate = workOrderForm.getEndDate();
             endHour = new Integer(workOrderForm.getEndHour()).intValue();
             endMinute = new Integer(workOrderForm.getEndMinute()).intValue();
             if (endHour > 11)
               eventTOD = "PM";
             if (endHour > 12)
               endHour = endHour - 12;
             endTime = endHour + ":";
             if (endMinute < 10)
               endTime = endTime + "0";
             endTime = endTime + endMinute + eventTOD;

             scheduleKey = workOrderForm.getScheduleKey();
             service = workOrderForm.getSourceName();             
             subject = workOrderForm.getSubject();
             attributeToEntity = workOrderForm.getAttributeToEntity();
             attributeToName = workOrderForm.getAttributeToName();
             location = "Customer Site";
             if (workOrderForm.getLocationId().equalsIgnoreCase("true")) {
               location = "In Office";
             } 
        %>
               
          <% if (!((pStartDate.equalsIgnoreCase(startDate)) &&
                (pStartTime.equalsIgnoreCase(startTime)) &&
                (pSubject.equalsIgnoreCase(subject)) &&
                (pAttributeToEntity.equalsIgnoreCase(attributeToEntity)) &&
                (pAttributeToName.equalsIgnoreCase(attributeToName)) &&
                (pLocation.equalsIgnoreCase(location)))) { %>
        
            <% if (y > 0) { %>
                 <hr>
            <% } %>
                                
               <font size="-1">
                 <strong>Start Date:</strong><%=startDate%>
                 &nbsp;<strong>Time:</strong>&nbsp;<%=startTime%>
                 <br>
                 <strong>End</strong>
                 <% if (!(startDate.equalsIgnoreCase(endDate))) { %>
                   &nbsp;<strong>Date:</strong><%=endDate%>
                 <% } %>
                 &nbsp;<strong>Time:</strong>&nbsp;<%=endTime%>
                 <br>
                 <strong>Location:</strong>&nbsp;<%=location%>
                 <br>               
                 <strong>Subject:</strong>&nbsp;<a href="ScheduleGet.do?key=<%=scheduleKey%> "><%=subject%></a>
                 <br>               
                 <strong><%=attributeToEntity%>:</strong>&nbsp;<%=attributeToName%>
                 <br>
                 <strong>Service:</strong>&nbsp;                 
               </font>  
          <% } else { %>
               ;&nbsp;
          <% } %>
             <font size="-1">          
               <%=service%>
             </font>
        <%
             pStartDate = startDate;
             pStartTime = startTime;
             pEndDate = endDate;
             pEndTime = endTime;
             pSubject = subject;
             pAttributeToEntity = attributeToEntity;
             pAttributeToName = attributeToName;
             pLocation = location;
                          
             y++;        
           }
        %>   
        </logic:notEqual>   
      </div>
    </logic:iterate> 
  </logic:notEqual>
  </div>
</div>

  <% if (request.getParameter("tabId") != null) { %>
    <% if (request.getParameter("tabId").toString().compareToIgnoreCase("scheduleTab") == 0) { %>
      <script type="text/javascript" language="JavaScript"><!--
        ShowContent('scheduleTab', 'scheduleInfo');
      //--></script>
    <% } %>
  <% } %>
</logic:notEqual> 

<logic:notEqual name="customerForm" property="key" value="">  
<!-- ================================================================= -->
<!-- Graphical display of the pet related lists                        -->
<!-- ================================================================= -->
  <div id="formInfo" style="display:none;">  
    <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
      <tr>
        <td></td>  
        <td colspan="4">
          <strong><bean:message key="app.customer.clientName" />:</strong>
          &nbsp;&nbsp;
          <bean:write name="customerForm" property="clientName" />
        </td>
      </tr>
      <tr>
        <td colspan="8">
          <hr>
        </td>
      </tr>    
    </table>
  
    <ul>
      <logic:equal name="customerForm" property="attributeToEntity" value="Pet">    
        <li>
          <a href="PetList.do?customerKeyFilter=<bean:write name="customerForm" 
            property="key" /> "><bean:message key="app.customer.viewPetList"/></a>
        </li>    
        <li>
          <a href="PetBoardListEntry.do?customerKey=<bean:write name="customerForm"
            property="key" /> "><bean:message key="app.pet.viewBoardActivity"/></a>
        </li>
        <li>
          <a href="PetExamListEntry.do?customerKey=<bean:write name="customerForm"
            property="key" /> "><bean:message key="app.pet.viewExamActivity"/></a>
        </li>    
        <li>
          <a href="PetVacListEntry.do?customerKey=<bean:write name="customerForm"
            property="key" /> "><bean:message key="app.pet.viewVacActivity"/></a>
        </li>
        <li>
          <a href="VendorResultEntry.do?customerKey=<bean:write name="customerForm"
            property="key" /> "><bean:message key="app.pet.viewLabActivity"/></a>
        </li>
      </logic:equal>

      <logic:equal name="customerForm" property="attributeToEntity" value="System">    
        <li>
          <a href="SystemList.do?customerKeyFilter=<bean:write name="customerForm" 
            property="key" /> "><bean:message key="app.customer.viewSystemList"/></a>
        </li>
      </logic:equal>
  
      <logic:equal name="customerForm" property="attributeToEntity" value="Vehicle">
        <li>
          <a href="VehicleList.do?customerKeyFilter=<bean:write name="customerForm" 
            property="key" /> "><bean:message key="app.customer.viewVehicleList"/></a>
        </li>
      </logic:equal>
      <li>
        <a href="#" onclick="popup = putinpopup('PdfPaymentAgreementForm.do?key=<bean:write name="customerForm" 
          property="key" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.customer.paymentAgreementTitle"/></a>
      </li>  
    </ul>
  </div>

  <% if (request.getParameter("tabId") != null) { %>
    <% if (request.getParameter("tabId").toString().compareToIgnoreCase("formTab") == 0) { %>
      <script type="text/javascript" language="JavaScript"><!--
        ShowContent('formTab', 'formInfo');
      //--></script>
    <% } %>
  <% } %>  
</logic:notEqual>

<logic:notEqual name="customerForm" property="key" value="">
<!-- ================================================================= -->
<!-- Display the address for the customer in a label format            -->
<!-- ================================================================= -->
  <div id="addressInfo" style="display:none;">
    <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
      <tr>
        <td></td>
        <td colspan="4">
          <strong><bean:message key="app.customer.clientName" />:</strong>
          &nbsp;&nbsp;
          <bean:write name="customerForm" property="clientName" />
        </td>
      </tr>
      <tr>
        <td colspan="8">
          <hr>
        </td>
      </tr>
    </table>

    <br>
    <strong><bean:message key="app.customer.attention" />:</strong>&nbsp;&nbsp;<bean:write name="customerForm" property="contactName" />
    <br>
    <bean:write name="customerForm" property="clientName" />
    <br>
    <bean:write name="customerForm" property="address" />&nbsp;<bean:write name="customerForm" property="address2" />
    <br>
    <bean:write name="customerForm" property="city" />,&nbsp;<bean:write name="customerForm" property="state" />&nbsp;<bean:write name="customerForm" property="zip" />
    <br>
    <br>
    
  </div>

  <% if (request.getParameter("tabId") != null) { %>
    <% if (request.getParameter("tabId").toString().compareToIgnoreCase("addressTab") == 0) { %>
      <script type="text/javascript" language="JavaScript"><!--
        ShowContent('addressTab', 'addressInfo');
      //--></script>
    <% } %>
  <% } %>
</logic:notEqual>