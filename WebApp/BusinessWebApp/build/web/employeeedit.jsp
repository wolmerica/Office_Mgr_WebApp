<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>


<script type="text/javascript" language="JavaScript"><!--

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
//--></script>


<%
  String attTitle = "app.employee.addTitle";            
  String attAction = "/EmployeeAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="employeeForm" property="key" value="">
  <%
    attTitle = "app.employee.editTitle";
    attAction = "/EmployeeEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="employeeForm" property="permissionStatus" value="LOCKED">
<% attDisableEdit = true; %>    
<bean:message key="app.readonly"/>
  <logic:notEqual name="employeeForm" property="permissionStatus" value="READONLY">
    [<bean:write name="employeeForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="EmployeeList.do" name="employeeList" method="post">                                  
      <input type="submit" value="<bean:message key="app.employee.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="employeeForm" property="key" value="">        
    <td colspan="6" align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.employee.id"/>&sourceKey=<bean:write name="employeeForm"
                property="key" />&sourceName=<bean:write name="employeeForm"
                property="firstName" />/<bean:write name="employeeForm"
                property="lastName" />" >[<bean:write name="employeeForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."> </a>
    </td>
    </logic:notEqual>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <!-- Display tabs to show and hide employee content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff; font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('permissionTab','permissionInfo');">
              <bean:message key="app.employee.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="employeeForm" property="key" value="">            
          <% if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("false") != 0) { %>
            <td align="center">
              <div id="permissionTab" style="background:#fff; font:12;">
                <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('permissionTab','permissionInfo');">
                  <bean:message key="app.employee.permissionTab" />
                </a>
              </div>
            </td>
          <% } %>
          </logic:notEqual>
        </tr>
      </table>
    </td>  
  </tr>
</table>


<div id="mainInfo" style="display:none;">  
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <html:form action="<%=attAction%>">  
    <logic:notEqual name="employeeForm" property="key" value="">   
      <html:hidden property="key" />
    </logic:notEqual>        
  <tr>
    <td><strong><bean:message key="app.employee.userName" />:</strong></td>
    <td>
      <html:text property="userName" size="8" maxlength="8" readonly="<%=attKeyEdit%>" />
      <html:errors property="userName" />       
    </td>
    <td><strong><bean:message key="app.employee.password" />:</strong></td>
    <td>
      <html:password property="password" size="8" maxlength="8" readonly="<%=attDisableEdit%>" />
      <html:errors property="password" />       
    </td>
    <td rowspan="3">
      <logic:notEqual name="employeeForm" property="key" value="">         
        <div style="width:<bean:message key="app.employee.photo.width" />; height:<bean:message key="app.employee.photo.height" />;  border-width: 1px; border-style: solid; border-color: black; ">
          <logic:notEqual name="employeeForm" property="photoFileName" value="">          
            <img src="<bean:write name="employeeForm" 
              property="documentServerURL" />/<bean:message key="app.employee.id" />/<bean:write name="employeeForm" 
            property="key" />/<bean:write name="employeeForm" 
            property="photoFileName" />" width="<bean:message key="app.employee.photo.width" />" height="<bean:message key="app.employee.photo.height" />" >
          </logic:notEqual>
          <logic:equal name="employeeForm" property="photoFileName" value="">
            <img src="images/NoImage.jpg" width="<bean:message key="app.employee.photo.width" />" height="<bean:message key="app.employee.photo.height" />" >
          </logic:equal>
        </div>
      </logic:notEqual>
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.employee.firstName" />:</strong></td>
    <td>
      <html:text property="firstName" size="25" maxlength="25" readonly="<%=attDisableEdit%>" />
      <html:errors property="firstName" />       
    </td>
    <% if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("false") != 0) { %>    
      <td><strong><bean:message key="app.employee.adminId" />:</strong></td>    
      <td>
        <html:radio property="adminId" value="false" disabled="<%=attDisableEdit%>" />No
        <html:radio property="adminId" value="true" disabled="<%=attDisableEdit%>" />Yes
        <html:errors property="adminId" />                    
      </td>
    <% } %>
    </td>     
  </tr>
  <tr>
    <td><strong><bean:message key="app.employee.lastName" />:</strong></td>
    <td>
      <html:text property="lastName" size="25" maxlength="25" readonly="<%=attDisableEdit%>" />
      <html:errors property="lastName" />       
    </td>
    <td><strong><bean:message key="app.employee.loginStamp" />:</strong></td>
    <td>
      <bean:write name="employeeForm" property="loginStamp" />
      &nbsp;&nbsp;
      <bean:write name="employeeForm" property="loginIpAddress" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.employee.address" />:</strong></td>
    <td>
      <html:text property="address" size="30" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="address" />                   
    </td>
    <td><strong><bean:message key="app.employee.address2" />:</strong></td>
    <td colspan="2">
      <html:text property="address2" size="30" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="address2" />             
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.employee.city" />:</strong></td>
    <td>
      <html:text property="city" size="30" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="city" />                   
    </td>
    <td><strong><bean:message key="app.employee.state" />:</strong></td>
    <td>
      <html:text property="state" size="2" maxlength="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="state" />             
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.employee.zip" />:</strong></td>
    <td>
      <html:text property="zip" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="zip" />                   
    </td>
    <td><strong><bean:message key="app.employee.phoneNum" />:</strong></td>
    <td>
      <html:text property="phoneNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="phoneNum" />   
      &nbsp;&nbsp;
      <strong><bean:message key="app.employee.phoneExt" />:</strong>
      <html:text property="phoneExt" size="4" maxlength="5" readonly="<%=attDisableEdit%>" />
      <html:errors property="phoneExt" />   
    </td>      
  </tr>
  <tr>    
    <td><strong><bean:message key="app.employee.email" />:</strong></td>
    <td>
      <html:text property="email" size="30" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="email" />       
    </td>  
    <td><strong><bean:message key="app.employee.phone" />:</strong></td>
    <td>
      <html:text property="phone" size="15" maxlength="15" readonly="<%=attDisableEdit%>" />
      <html:errors property="phone" />       
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.employee.email2" />:</strong></td>
    <td>
      <html:text property="email2" size="30" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="email2" />       
    </td>      
    <td><strong><bean:message key="app.employee.mobileNum" />:</strong></td>
    <td>
      <html:text property="mobileNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="mobileNum" />
    </td>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.employee.yimId" />:</strong></td>    
    <td>
      <html:text property="yimId" size="15" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="yimId" />       
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.employee.createUser" />:</strong></td>
    <td><bean:write name="employeeForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.employee.createStamp" />:</strong></td>
    <td><bean:write name="employeeForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.employee.updateUser" />:</strong></td>
    <td><bean:write name="employeeForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.employee.updateStamp" />:</strong></td>
    <td><bean:write name="employeeForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="5" align="right">
      <html:submit value="Save Employee Values" disabled="<%=attDisableEdit%>" />             
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
  
<!-- Display user permissions for existing records only (ie: Edit)  -->
<logic:notEqual name="employeeForm" property="key" value="">
<div id="permissionInfo" style="display:none;">   
  <!-- Display user permissions for ADMIN users only.  -->
  <% if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("false") != 0) { %>
    <script src="./js/permission_slip.js" language="JavaScript" type="text/javascript"></script>
    
    <form action="EmployeePermissionSlipEdit.do" name="PermissionForm" method="post" >
      <input type="hidden" name="key" value="<bean:write name="employeeForm" property="key" />" >
      <input type="hidden" name="permissionSlip" value="<bean:write name="employeeForm" property="permissionSlip" />" readonly="true" >
      <input type="hidden" name="tabId" value="permTab" >      
      <table width="400" border="1" cellspacing="<bean:message key="app.table.cellspacing"/>" >          
        <tr>
          <td><strong><bean:message key="app.employee.permFeature"/></strong></td>
          <td><strong><bean:message key="app.employee.permProhibit"/></strong></td>
          <td><strong><bean:message key="app.employee.permList"/></strong></td>
          <td><strong><bean:message key="app.employee.permView"/></strong></td>
          <td><strong><bean:message key="app.employee.permEdit"/></strong></td>
          <td><strong><bean:message key="app.employee.permAdd"/></strong></td>
          <td><strong><bean:message key="app.employee.permDelete"/></strong></td>
        </tr>
        <tr>
          <% String fId="f0";
             int p=0;
             int pMax=6;
          %>
          <td><strong><bean:message key="app.employee.featureMenu"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f27"; %>
          <td><strong><bean:message key="app.employee.featureAttachment"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f25"; %>
          <td><strong><bean:message key="app.employee.featureSchedule"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>    
        <tr>
          <% fId="f26"; %>
          <td><strong><bean:message key="app.employee.featureResource"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>    
        <tr>
          <% fId="f2"; %>
          <td><strong><bean:message key="app.employee.featureEmployee"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f28"; %>
          <td><strong><bean:message key="app.employee.featureProspect"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f1"; %>         
          <td><strong><bean:message key="app.employee.featureCustomer"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f7"; %>
          <td><strong><bean:message key="app.employee.featureVendor"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f3"; %>
          <td><strong><bean:message key="app.employee.featureItem"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f6"; %>
          <td><strong><bean:message key="app.employee.featureService"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f29"; %>
          <td><strong><bean:message key="app.employee.featureBundle"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f34"; %>
          <td><strong><bean:message key="app.employee.featurePromotion"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f36"; %>
          <td><strong><bean:message key="app.employee.featureSpeciesAndBreed"/></strong></td>
          <% for (p=0; p<pMax; p++) { %>
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f4"; %>
          <td><strong><bean:message key="app.employee.featurePet"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f30"; %>
          <td><strong><bean:message key="app.employee.featurePetVac"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f32"; %>
          <td><strong><bean:message key="app.employee.featurePetBoard"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>        
        <tr>
          <% fId="f33"; %>
          <td><strong><bean:message key="app.employee.featurePetExam"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>        
        <tr>
          <% fId="f24"; %>
          <td><strong><bean:message key="app.employee.featureSystem"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f31"; %>
          <td><strong><bean:message key="app.employee.featureVehicle"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>         
        <tr>
          <% fId="f8"; %>
          <td><strong><bean:message key="app.employee.featureCustomerType"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f9"; %>
          <td><strong><bean:message key="app.employee.featurePriceType"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f10"; %>
          <td><strong><bean:message key="app.employee.featureTaxMarkUp"/></strong></td>          
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f11"; %>
          <td><strong><bean:message key="app.employee.featurePurchaseOrder"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f35"; %>
          <td><strong><bean:message key="app.employee.featureVendorResult"/></strong></td>
          <% for (p=0; p<pMax; p++) { %>
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f5"; %>
          <td><strong><bean:message key="app.employee.featureRebate"/></strong></td>          
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>
        <tr>
          <% fId="f12"; %>
          <td><strong><bean:message key="app.employee.featureRebateInstance"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>        
        <tr>
          <% fId="f13"; %>
          <td><strong><bean:message key="app.employee.featureVendorInvoice"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>        
        <tr>
          <% fId="f14"; %>
          <td><strong><bean:message key="app.employee.featureCustomerInvoice"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>                
        <tr>
          <% fId="f21"; %>
          <td><strong><bean:message key="app.employee.featurePriceSheet"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>            
        <tr>
          <% fId="f15"; %>
          <td><strong><bean:message key="app.employee.featureCustomerCreditLedger"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>                
        <tr>
          <% fId="f16"; %>
          <td><strong><bean:message key="app.employee.featureCustomerAccounting"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>                
        <tr>
          <% fId="f17"; %>
          <td><strong><bean:message key="app.employee.featureVendorAccounting"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>    
        <tr>
          <% fId="f18"; %>
          <td><strong><bean:message key="app.employee.featureExpense"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>    
        <tr>
          <% fId="f19"; %>
          <td><strong><bean:message key="app.employee.featureItemPurchaseReport"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>    
        <tr>
          <% fId="f20"; %>
          <td><strong><bean:message key="app.employee.featureItemSaleReport"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>            
        <tr>
          <% fId="f22"; %>
          <td><strong><bean:message key="app.employee.featureServiceLaborReport"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>            
        <tr>
          <% fId="f23"; %>
          <td><strong><bean:message key="app.employee.featureServiceSaleReport"/></strong></td>
          <% for (p=0; p<pMax; p++) { %> 
            <td align="center"><input type="radio" name="<%=fId%>" value="<%=p%>" onclick="setPermissionSlip(this)"></td>
          <% } %>
        </tr>            
      </table>
      <br>
      <input type="submit" value="<bean:message key="app.employee.permSave" />">
    </form>
    <script type="text/javascript">
      initPerm();
    </script>
  <% } %>
</div>  

<% if (request.getParameter("tabId") != null) { %>
  <% if (request.getParameter("tabId").toString().compareToIgnoreCase("permTab") == 0) { %>
    <script type="text/javascript" language="JavaScript"><!--
      ShowContent('permissionTab', 'permissionInfo');
    //--></script>
  <% } %>      
<% } %>  

</logic:notEqual> 
