<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

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
  String attTitle = "app.prospect.addTitle";            
  String attAction = "/ProspectAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }       
%> 

<logic:notEqual name="prospectForm" property="key" value="">
  <%
    attTitle = "app.prospect.editTitle";
    attAction = "/ProspectEdit";
    attKeyEdit = true;
  %>
  <logic:equal name="prospectForm" property="activeId" value="true">
    <% attDisableEdit = true; %>
  </logic:equal>  
</logic:notEqual>    

<logic:notEqual name="prospectForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="prospectForm" property="permissionStatus" value="READONLY">
    [<bean:write name="prospectForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="ProspectList.do" name="prospectList" method="post">                                  
      <input type="submit" value="<bean:message key="app.prospect.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="prospectForm" property="key" value="">    
    <td colspan="6" align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.prospect.id"/>&sourceKey=<bean:write name="prospectForm"
                property="key" />&sourceName=<bean:write name="prospectForm"
                property="name" />" >[<bean:write name="prospectForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."></a>
    </td>
    </logic:notEqual>
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <!-- Display tabs to show and hide prospect content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff; font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('addressTab','addressInfo');">
              <bean:message key="app.prospect.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="prospectForm" property="key" value="">
            <td align="center">
            <div id="addressTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('addressTab','addressInfo');">
                <bean:message key="app.prospect.addressTab" />
              </a>
            </div>
            </td>
          </logic:notEqual>
        </tr>
      </table>
    </td>
  </tr>
</table>

<div id="mainInfo" style="display:none;">
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <html:form action="<%=attAction%>">
      <logic:notEqual name="prospectForm" property="key" value="">      
        <html:hidden property="key" />
      </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.prospect.name" />:</strong></td>
    <td>
      <html:text property="name" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="name" />        
    </td>
    <td><strong><bean:message key="app.prospect.contactName" />:</strong></td>
    <td>
      <html:text property="contactName" size="35" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="contactName" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.prospect.lineOfBusiness" />:</strong></td>
    <td>
      <html:text property="lineOfBusiness" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="lineOfBusiness" />        
    </td>
  </tr>  
  <tr>
    <td><strong><bean:message key="app.prospect.address" />:</strong></td>
    <td>
      <html:text property="address" size="35" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="address" />        
    </td>
    <td><strong><bean:message key="app.prospect.address2" />:</strong></td>
    <td>
      <html:text property="address2" size="35" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="address2" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.prospect.city" />:</strong></td>
    <td>
      <html:text property="city" size="35" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="city" />        
    </td>
    <td><strong><bean:message key="app.prospect.state" />:</strong></td>
    <td>
      <html:text property="state" size="2" maxlength="2" readonly="<%=attDisableEdit%>" />
      <html:errors property="state" />        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.prospect.zip" />:</strong></td>
    <td>
      <html:text property="zip" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <html:errors property="zip" />  
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <logic:notEqual name="prospectForm" property="key" value="">
        <a href="blank" onclick="popup = putinpopup('http://maps.yahoo.com/pmaps?addr=<bean:write name="prospectForm" 
          property="address" />&csz=<bean:write name="prospectForm" 
          property="city" />%2C<bean:write name="prospectForm" 
          property="zip" />&country=us&new=1'); return false" target="_blank"><bean:message key="app.map" /></a>    
      </logic:notEqual>       
    </td>
    <td><strong><bean:message key="app.prospect.phoneNum" />:</strong></td>
    <td>
      <html:text property="phoneNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="phoneNum" />   
      &nbsp;&nbsp;
      <strong><bean:message key="app.prospect.reminderPref" />:</strong>
      <html:select property="reminderPrefKey" disabled="<%=attDisableEdit%>">
        <html:option value="0"><bean:message key="app.prospect.phoneNum" /></html:option>
        <html:option value="1"><bean:message key="app.prospect.mobileNum" /></html:option>        
        <html:option value="2"><bean:message key="app.prospect.email" /></html:option>
        <html:option value="3"><bean:message key="app.prospect.textmsg" /></html:option>
      </html:select>    
      <html:errors property="reminderPrefKey" />      
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.prospect.faxNum" />:</strong></td>
    <td>
      <html:text property="faxNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="faxNum" />        
    </td>
    <td><strong><bean:message key="app.prospect.mobileNum" />:</strong></td>
    <td>
      <html:text property="mobileNum" size="14" maxlength="14" readonly="<%=attDisableEdit%>" />
      <html:errors property="mobileNum" />        
    </td>    
  </tr>	  
  <tr>
    <td><strong><bean:message key="app.prospect.webSite" />:</strong></td>
    <td>
      <html:text property="webSite" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="webSite" />        
    </td>  
    <td><strong><bean:message key="app.prospect.email" />:</strong></td>
    <td>
      <html:text property="email" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="email" />        
    </td>    
  </tr>
  <tr>
    <td><strong><bean:message key="app.prospect.referredBy" />:</strong></td>
    <td>
      <html:text property="referredBy" size="35" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="referredBy" />                   
    </td> 
    <td><strong><bean:message key="app.prospect.email2" />:</strong></td>
    <td>
      <html:text property="email2" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="email2" />        
    </td>    
  </tr>  
  <tr>
    <td><strong><bean:message key="app.prospect.noteLine1" />:</strong></td>
    <td>   
      <html:text property="noteLine1" size="40" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine1" />
    </td>       
    <td><strong><bean:message key="app.prospect.activeId" />:</strong></td>
    <td>
      <html:radio property="activeId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="activeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="activeId" />                   
    </td>    
  </tr>
  <tr>
    <td><strong><bean:message key="app.prospect.createUser" />:</strong></td>
    <td><bean:write name="prospectForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.prospect.createStamp" />:</strong></td>
    <td><bean:write name="prospectForm" property="createStamp" /></td>	             	
  </tr>
  <tr>
    <td><strong><bean:message key="app.prospect.updateUser" />:</strong></td>
    <td><bean:write name="prospectForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.prospect.updateStamp" />:</strong></td>
    <td><bean:write name="prospectForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
      <html:submit value="Save Prospect Values" disabled="<%=attDisableEdit%>" />  
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

<logic:notEqual name="prospectForm" property="key" value="">
<!-- ================================================================= -->
<!-- Display the address for the prospect in a label format            -->
<!-- ================================================================= -->
  <div id="addressInfo" style="display:none;">
    <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
      <tr>
        <td></td>
        <td colspan="4">
          <strong><bean:message key="app.prospect.name" />:</strong>
          &nbsp;&nbsp;
          <bean:write name="prospectForm" property="name" />
        </td>
      </tr>
      <tr>
        <td colspan="8">
          <hr>
        </td>
      </tr>
    </table>

    <br>
    <strong><bean:message key="app.prospect.attention" />:</strong>&nbsp;&nbsp;<bean:write name="prospectForm" property="contactName" />
    <br>
    <bean:write name="prospectForm" property="name" />
    <br>
    <bean:write name="prospectForm" property="address" />&nbsp;<bean:write name="prospectForm" property="address2" />
    <br>
    <bean:write name="prospectForm" property="city" />,&nbsp;<bean:write name="prospectForm" property="state" />&nbsp;<bean:write name="prospectForm" property="zip" />
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
