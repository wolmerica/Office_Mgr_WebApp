<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
 
<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>
  
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
  
  // Schedule key is passed for work order services.
  String sKey = null;
  if (request.getParameter("sKey") != null) {
    sKey = request.getParameter("sKey");
  }

  // Promotion key is passed.
  String promoKey = null;
  if (request.getParameter("promoKey") != null) {
    promoKey = request.getParameter("promoKey");
  }

%> 
 
<logic:equal name="bundleListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="bundleListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="bundleListHeadForm"
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
    <td colspan="8">            
      <form action="BundleList.do" name="bundleListHeadForm" method="post">
        <strong><bean:message key="app.bundle.name" />:</strong>
        <input type="text" name="bundleNameFilter" value="<bean:write name="bundleListHeadForm" property="bundleNameFilter" />" size="4" maxlength="8" >
        <strong><bean:message key="app.bundle.category" />:</strong>
        <input type="text" name="bundleCategoryFilter" value="<bean:write name="bundleListHeadForm" property="bundleCategoryFilter" />" size="4" maxlength="8" >
        <% if (sKey != null) { %>
          <input type="hidden" name="sKey" value="<%=sKey%>">
        <% } %>
        <% if (promoKey != null) { %>
          <input type="hidden" name="promoKey" value="<%=promoKey%>">
        <% } %>
        &nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </form>        
    </td>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.bundle.name" /></th>
    <th><bean:message key="app.bundle.category" /></th>
    <th align="center"><bean:message key="app.bundle.releaseId" /></th>    
    <th align="right"><bean:message key="app.bundle.hoursMinutes" /></th>
    <th> </th>    
    <th colspan="2" align="right">
    <% if (permAddId.equalsIgnoreCase("true")) { %>        
      <button type="button" onClick="window.location='BundleEntry.do?bundleNameFilter=<bean:write name="bundleListHeadForm" 
        property="bundleNameFilter" />&bundleCategoryFilter=<bean:write name="bundleListHeadForm" 
        property="bundleCategoryFilter" />&pageNo=<bean:write name="bundleListHeadForm" 
        property="currentPage" />' "><bean:message key="app.bundle.addTitle" /></button>    
    <% } %>
    </th>      
  </tr>    
  <tr>
        <td colspan="8">
	  <hr>
	</td>
      <tr>  
<logic:notEqual name="bundleListHeadForm" property="lastRecord" value="0">       
  <logic:iterate id="bundleListForm"
                 name="bundleListHeadForm"
                 property="bundleListForm"
                 scope="session"
                 type="com.wolmerica.bundle.BundleListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="bundleListHeadForm"
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
        
        <logic:notEqual name="bundleListForm" property="allowDeleteId" value="true">
          <%
            permViewId = permEditId;
            permEditId = "false";
            permDeleteId = "false";
          %>
        </logic:notEqual>
        
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
            <bean:write name="bundleListForm" property="name" />
	  </td>
	  <td>
            <bean:write name="bundleListForm" property="category" />
	  </td>
	  <td align="center">
            <html:checkbox name="bundleListForm" property="releaseId" disabled="true" />
          </td>
	  <td align="right">
            <bean:write name="bundleListForm" property="durationHour" />:
            <logic:equal name="bundleListForm" property="durationMinute" value="0">
              00
            </logic:equal>
            <logic:notEqual name="bundleListForm" property="durationMinute" value="0">
              <bean:write name="bundleListForm" property="durationMinute" />
            </logic:notEqual>
	  </td>
	  <td>
	  
	  </td>
        <% if (sKey != null) { %>
          <td colspan="2" align="center">             
             <a href="WorkOrderAddToCart.do?key=<%=sKey%>&bundleKey=<bean:write name="bundleListForm" property="key" /> "><bean:message key="app.addtocart" /></a>
          </td>       
        <% } %>
        <% if (promoKey != null) { %>
          <td colspan="2" align="center">
             <a href="PromotionDetailAddToCart.do?key=<%=promoKey%>&bundleKey=<bean:write name="bundleListForm" property="key" /> "><bean:message key="app.addtocart" /></a>
          </td>
        <% } %>
        <% if (sKey == null) { %>
          <% if (promoKey == null) { %>
            <td align="center">          
              <% if (permViewId.equalsIgnoreCase("true")) { %>	  
                <a href="BundleGet.do?key=<bean:write name="bundleListForm"
                  property="key" />
<logic:notEqual name="bundleListHeadForm" property="bundleNameFilter" value="">
&bundleNameFilter=<bean:write name="bundleListHeadForm" property="bundleNameFilter" />
</logic:notEqual>
<logic:notEqual name="bundleListHeadForm" property="bundleCategoryFilter" value="">
&bundleCategoryFilter=<bean:write name="bundleListHeadForm" property="bundleCategoryFilter" />
</logic:notEqual>
<logic:notEqual name="bundleListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="bundleListHeadForm" property="currentPage" />
</logic:notEqual>                
"><bean:message key="app.view" /></a>
              <% } %>
              <% if (permEditId.equalsIgnoreCase("true")) { %>
                <a href="BundleGet.do?key=<bean:write name="bundleListForm"
                  property="key" />
<logic:notEqual name="bundleListHeadForm" property="bundleNameFilter" value="">
&bundleNameFilter=<bean:write name="bundleListHeadForm" property="bundleNameFilter" />
</logic:notEqual>
<logic:notEqual name="bundleListHeadForm" property="bundleCategoryFilter" value="">
&bundleCategoryFilter=<bean:write name="bundleListHeadForm" property="bundleCategoryFilter" />
</logic:notEqual>
<logic:notEqual name="bundleListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="bundleListHeadForm" property="currentPage" />
</logic:notEqual>                                
"><bean:message key="app.edit" /></a>
              <% } %>
              <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                   <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
              <% } %>
            </td>
            <td align="right">
              <% if (permDeleteId.equalsIgnoreCase("true")) { %>	  
                <a href="BundleDelete.do?key=<bean:write name="bundleListForm"
                  property="key" />
<logic:notEqual name="bundleListHeadForm" property="bundleNameFilter" value="">
&bundleNameFilter=<bean:write name="bundleListHeadForm" property="bundleNameFilter" />
</logic:notEqual>
<logic:notEqual name="bundleListHeadForm" property="bundleCategoryFilter" value="">
&bundleCategoryFilter=<bean:write name="bundleListHeadForm" property="bundleCategoryFilter" />
</logic:notEqual>
<logic:notEqual name="bundleListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="bundleListHeadForm" property="currentPage" />
</logic:notEqual>                
"  onclick="return confirm('<bean:message key="app.bundle.delete.message" />')" ><bean:message key="app.delete" /></a>
              <% } %>
            </td>
          <% } %>
        <% } %>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="8">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='BundleList.do?bundleNameFilter=<bean:write name="bundleListHeadForm" 
        property="bundleNameFilter" />&bundleCategoryFilter=<bean:write name="bundleListHeadForm" 
        property="bundleCategoryFilter" />&pageNo=<bean:write name="bundleListHeadForm" 
        property="previousPage" /><% if (sKey != null) { %>&sKey=<%=sKey%><% } %><% if (promoKey != null) { %>&promoKey=<%=promoKey%><% } %>' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="4" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="bundleListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="bundleListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="bundleListHeadForm" property="recordCount" />      
    </td>       
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='BundleList.do?bundleNameFilter=<bean:write name="bundleListHeadForm" 
        property="bundleNameFilter" />&bundleCategoryFilter=<bean:write name="bundleListHeadForm" 
        property="bundleCategoryFilter" />&pageNo=<bean:write name="bundleListHeadForm"
        property="nextPage" /><% if (sKey != null) { %>&sKey=<%=sKey%><% } %><% if (promoKey != null) { %>&promoKey=<%=promoKey%><% } %>' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>