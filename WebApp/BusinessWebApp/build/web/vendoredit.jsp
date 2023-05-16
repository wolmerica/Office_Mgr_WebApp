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
  String attTitle = "app.vendor.addTitle";            
  String attAction = "/VendorAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }
  boolean attDisableWebServiceEdit = true;
%> 

<logic:notEqual name="vendorForm" property="key" value="">
  <%
    attTitle = "app.vendor.editTitle";
    attAction = "/VendorEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>    

<logic:notEqual name="vendorForm" property="permissionStatus" value="LOCKED">
<% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="vendorForm" property="permissionStatus" value="READONLY">
    [<bean:write name="vendorForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<logic:equal name="vendorForm" property="trackResultId" value="true">
  <% attDisableWebServiceEdit = attDisableEdit; %>
</logic:equal>
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="VendorList.do" name="vendorList" method="post">                                  
      <input type="submit" value="<bean:message key="app.vendor.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="vendorForm" property="key" value="">    
    <td colspan="6" align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.vendor.id"/>&sourceKey=<bean:write name="vendorForm"
                property="key" />&sourceName=<bean:write name="vendorForm"
                property="name" />" >[<bean:write name="vendorForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."> </a>
    </td>
    </logic:notEqual>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <!-- Display tabs to show and hide vendor content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff; font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('accountTab','accountInfo');HideContent('addressTab','addressInfo');">
              <bean:message key="app.vendor.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="vendorForm" property="key" value="">          
          <td align="center">
            <div id="accountTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('accountTab','accountInfo');HideContent('addressTab','addressInfo');">
              <bean:message key="app.vendor.accountTab" />
              </a>
            </div>
          </td>
          <td align="center">
            <div id="addressTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');HideContent('accountTab','accountInfo');ShowContent('addressTab','addressInfo');">
              <bean:message key="app.vendor.addressTab" />
              </a>
          </div>
          </td>
          </logic:notEqual>          
        </tr>
      </table>
    </td>  
    <logic:notEqual name="vendorForm" property="key" value="">    
      <td align="right" colspan="6">
        <button type="button" onClick="window.location='VendorInvoiceReportEntry.do?vendorKey=<bean:write name="vendorForm"
          property="key" />' "><bean:message key="app.vendor.viewActivity" /></button>
      </td>
    </logic:notEqual>    
  </tr>
</table> 
  
<div id="mainInfo" style="display:none;">  
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <html:form action="<%=attAction%>"> 
      <logic:notEqual name="vendorForm" property="key" value="">      
        <html:hidden property="key" />
      </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.vendor.name" />:</strong></td>
    <td>
      <html:text property="name" size="40" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="name" />        
    </td>
    <td><strong><bean:message key="app.vendor.clinicId" />:</strong></td>
    <td>
      <html:radio property="clinicId" value="false" disabled="<%=attKeyEdit%>" />No
      <html:radio property="clinicId" value="true" disabled="<%=attKeyEdit%>" />Yes
      <html:errors property="clinicId" />                    
    </td> 	         
  </tr>
  <tr>    
    <td><strong><bean:message key="app.vendor.contactName" />:</strong></td>
    <td>
      <html:text property="contactName" size="35" maxlength="35" readonly="<%=attDisableEdit%>" />
      <html:errors property="contactName" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendor.address" />:</strong></td>
    <td>
      <html:text property="address" size="35" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="address" />        
    </td>
    <td><strong><bean:message key="app.vendor.address2" />:</strong></td>
    <td>
      <html:text property="address2" size="35" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="address2" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendor.city" />:</strong></td>
    <td>
      <html:text property="city" size="35" maxlength="30" readonly="<%=attDisableEdit%>" />
      <html:errors property="city" />        
    </td>
    <td><strong><bean:message key="app.vendor.state" />:</strong></td>
    <td>
      <html:text property="state" size="2" maxlength="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="state" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendor.zip" />:</strong></td>
    <td>
      <html:text property="zip" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="zip" />        
    </td>
    <td><strong><bean:message key="app.vendor.phoneNum" />:</strong></td>
    <td>
      <html:text property="phoneNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="phoneNum" />
      &nbsp;&nbsp;
      <strong><bean:message key="app.vendor.phoneExt" />:</strong>
      <html:text property="phoneExt" size="4" maxlength="5" readonly="<%=attDisableEdit%>" />
      <html:errors property="phoneExt" />
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.vendor.email" />:</strong></td>
    <td>
      <html:text property="email" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="email" />        
    </td>  
    <td><strong><bean:message key="app.vendor.faxNum" />:</strong></td>
    <td>
      <html:text property="faxNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="faxNum" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendor.email2" />:</strong></td>
    <td>
      <html:text property="email2" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="email2" />        
    </td>  
    <td><strong><bean:message key="app.vendor.acctNum" />:</strong></td>
    <td>
      <html:text property="acctNum" size="25" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="acctNum" />        
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.vendor.webSite" />:</strong></td>
    <td>
      <html:text property="webSite" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="webSite" />        
    </td>      
    <td><strong><bean:message key="app.vendor.markUp" />:</strong></td>
    <td>
      <html:text property="markUp" size="4" readonly="<%=attDisableEdit%>" />
      <html:errors property="markUp" />      
    </td>    
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendor.terms" />:</strong></td>
    <td>
      <html:text property="terms" size="40" maxlength="80" readonly="<%=attDisableEdit%>" />
      <html:errors property="terms" />
    </td>
    <td><strong><bean:message key="app.vendor.orderFormKey" />:</strong></td>
    <td>
      <html:select property="orderFormKey" disabled="<%=attDisableEdit%>">
        <html:option value="0">Default</html:option>
        <html:option value="1">Attachment</html:option>
        <html:option value="2">Barcode</html:option>
      </html:select>
      <html:errors property="orderFormKey" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendor.activeId" />:</strong></td>
    <td>
      <html:radio property="activeId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="activeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="activeId" />
    </td>
    <td><strong><bean:message key="app.vendor.trackResultId" />:</strong></td>
    <td>
      <html:radio property="trackResultId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="trackResultId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="trackResultId" />
    </td>
  </tr>
  <tr>
    <td></td>
    <td></td>
    <td><strong><bean:message key="app.vendor.webServiceId" />:</strong></td>
    <td>
      <html:radio property="webServiceId" value="false" disabled="<%=attDisableWebServiceEdit%>"/>No
      <html:radio property="webServiceId" value="true" disabled="<%=attDisableWebServiceEdit%>"/>Yes
      <html:errors property="webServiceId" />
    </td>
  </tr>   
  <tr>
    <td><strong><bean:message key="app.vendor.createUser" />:</strong></td>
    <td><bean:write name="vendorForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.vendor.createStamp" />:</strong></td>
    <td><bean:write name="vendorForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.vendor.updateUser" />:</strong></td>
    <td><bean:write name="vendorForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.vendor.updateStamp" />:</strong></td>
    <td><bean:write name="vendorForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Vendor Values" disabled="<%=attDisableEdit%>" />  
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
  
<logic:notEqual name="vendorForm" property="key" value="">  
  <div id="accountInfo" style="display:none;">
    <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td></td>
      <td colspan="4">
        <strong><bean:message key="app.vendor.name" />:</strong>
        &nbsp;&nbsp;
        <bean:write name="vendorForm" property="name" />
      </td>
    </tr>
    <tr>
      <td colspan="8">
        <hr>
      </td>
    </tr>
    <tr>
      <td></td>
      <td><strong><bean:message key="app.vendor.lastPaymentAmount" />:</strong></td>
      <td>
        <bean:write name="vendorForm" property="lastPaymentDate" />
      </td>
      <td>
        <bean:message key="app.localeCurrency" />
        <bean:write name="vendorForm" property="lastPaymentAmount" />
      </td>
      <td>
        <form action="VendorAccountingListEntry.do" name="vendorAccountingList" method="post">
          <input type="hidden" name="vendorKeyFilter" value="<bean:write name="vendorForm" property="key" />">
          <input type="submit" value="<bean:message key="app.vendor.accountingListMessage" />">
        </form>
      </td>
    </tr>
    <tr>
      <td></td>
      <td><strong><bean:message key="app.vendor.accountBalanceAmount" />:</strong></td>
      <td>
        <bean:write name="vendorForm" property="accountBalanceDate" />
      </td>
      <td>
        <bean:message key="app.localeCurrency" />
        <bean:write name="vendorForm" property="accountBalanceAmount" />
      </td>
      <logic:notEqual name="vendorForm" property="accountBalanceAmount" value="0.00">
      <td>
        <form action="VendorAccountingEntry.do" name="vendorAccountingEntry" method="post">
          <input type="hidden" name="key" value="<bean:write name="vendorForm" property="key" />">
          <input type="hidden" name="accountBalanceAmount" value="<bean:write name="vendorForm" property="accountBalanceAmount" />">
          <input type="hidden" name="vendorKeyFilter" value="<bean:write name="vendorForm" property="key" />">
          <logic:equal name="vendorForm" property="allowPaymentId" value="true">
            <input type="submit" value="<bean:message key="app.vendor.accountingPaymentMessage" />">
          </logic:equal>
          <logic:equal name="vendorForm" property="allowRefundId" value="true">
            <input type="submit" value="<bean:message key="app.vendor.accountingRefundMessage" />">
          </logic:equal>
        </form>
      </td>
      </logic:notEqual>
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

<logic:notEqual name="vendorForm" property="key" value="">
<!-- ================================================================= -->
<!-- Display the address for the vendor in a label format            -->
<!-- ================================================================= -->
  <div id="addressInfo" style="display:none;">
    <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
      <tr>
        <td></td>
        <td colspan="4">
          <strong><bean:message key="app.vendor.name" />:</strong>
          &nbsp;&nbsp;
          <bean:write name="vendorForm" property="name" />
        </td>
      </tr>
      <tr>
        <td colspan="8">
          <hr>
        </td>
      </tr>
    </table>
    <br>
    <strong><bean:message key="app.vendor.attention" />:</strong>&nbsp;&nbsp;<bean:write name="vendorForm" property="contactName" />
    <br>
    <bean:write name="vendorForm" property="name" />
    <br>
    <bean:write name="vendorForm" property="address" />&nbsp;<bean:write name="vendorForm" property="address2" />
    <br>
    <bean:write name="vendorForm" property="city" />,&nbsp;<bean:write name="vendorForm" property="state" />&nbsp;<bean:write name="vendorForm" property="zip" />
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