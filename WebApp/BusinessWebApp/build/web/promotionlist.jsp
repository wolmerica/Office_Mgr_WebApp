<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
 
<script src="./js/date_picker.js" type="text/javascript"></script>
  
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
  
%> 
 
<logic:equal name="promotionListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="promotionListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="promotionListHeadForm"
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

    <td colspan="8">            
      <form action="PromotionList.do" name="promotionListHeadForm" method="post">
        <strong><bean:message key="app.promotion.name" />:</strong>
        <input type="text" name="promoNameFilter" value="<bean:write name="promotionListHeadForm" property="promoNameFilter" />" size="4" maxlength="8" >
        <strong><bean:message key="app.promotion.category" />:</strong>
        <input type="text" name="promoCategoryFilter" value="<bean:write name="promotionListHeadForm" property="promoCategoryFilter" />" size="4" maxlength="8" >
        <strong>&nbsp;&nbsp;<bean:message key="app.dateFrom" />:</strong>
        <input type="text" name="promoFromDate" value="<bean:write name="promotionListHeadForm" property="promoFromDate" />" size="8" maxlength="10" >
          <a href="javascript:show_calendar('promotionListHeadForm.promoFromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click to select the from date."></a>
        <strong>&nbsp;&nbsp;<bean:message key="app.dateTo" />&nbsp;</strong>
        <input type="text" name="promoToDate" value="<bean:write name="promotionListHeadForm" property="promoToDate" />" size="8" maxlength="10" >
          <a href="javascript:show_calendar('promotionListHeadForm.promoToDate');"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click to select the to date."></a>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
    <th><bean:message key="app.promotion.name" /></th>
    <th><bean:message key="app.promotion.category" /></th>
    <th align="right"><bean:message key="app.promotion.startDate" /></th>
    <th align="right"><bean:message key="app.promotion.endDate" /></th>    
    <th align="right"><bean:message key="app.promotion.discountRate" /></th>
    <th colspan="2" align="right">
    <% if (permAddId.equalsIgnoreCase("true")) { %>        
      <button type="button" onClick="window.location='PromotionEntry.do?promoNameFilter=<bean:write name="promotionListHeadForm" 
        property="promoNameFilter" />&promoCategoryFilter=<bean:write name="promotionListHeadForm" 
        property="promoCategoryFilter" />&promoFromDate=<bean:write name="promotionListHeadForm" 
        property="promoFromDate" />&promoToDate=<bean:write name="promotionListHeadForm" 
        property="promoToDate" />&pageNo=<bean:write name="promotionListHeadForm" 
        property="currentPage" />' "><bean:message key="app.promotion.addTitle" /></button>    
    <% } %>
    </th>      
  </tr>    
  <tr>
        <td colspan="8">
	  <hr>
	</td>
      <tr>  
<logic:notEqual name="promotionListHeadForm" property="lastRecord" value="0">       
  <logic:iterate id="promotionListForm"
                 name="promotionListHeadForm"
                 property="promotionListForm"
                 scope="session"
                 type="com.wolmerica.promotion.PromotionListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="promotionListHeadForm"
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
        
        <logic:notEqual name="promotionListForm" property="allowDeleteId" value="true">
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
        <bean:write name="promotionListForm" property="name" />
	  </td>
	  <td>
        <bean:write name="promotionListForm" property="category" />
	  </td>
	  <td align="right">
        <bean:write name="promotionListForm" property="startDate" />
	  </td>
	  <td align="right">
        <bean:write name="promotionListForm" property="endDate" />
	  </td>
	  <td align="right">
        <bean:write name="promotionListForm" property="discountRate" />
        <bean:message key="app.localePercent" />
	  </td>	  
          <td align="center">          
            <% if (permViewId.equalsIgnoreCase("true")) { %>	  
              <a href="PromotionGet.do?key=<bean:write name="promotionListForm"
                property="key" />
<logic:notEqual name="promotionListHeadForm" property="promoNameFilter" value="">
&promoNameFilter=<bean:write name="promotionListHeadForm" property="promoNameFilter" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoCategoryFilter" value="">
&promoCategoryFilter=<bean:write name="promotionListHeadForm" property="promoCategoryFilter" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoFromDate" value="">
&promoFromDate=<bean:write name="promotionListHeadForm" property="promoFromDate" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoToDate" value="">
&toFromDate=<bean:write name="promotionListHeadForm" property="promoToDate" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="promotionListHeadForm" property="currentPage" />
</logic:notEqual>                
"><bean:message key="app.view" /></a>
            <% } %>
            <% if (permEditId.equalsIgnoreCase("true")) { %>
              <a href="PromotionGet.do?key=<bean:write name="promotionListForm"
                property="key" />
<logic:notEqual name="promotionListHeadForm" property="promoNameFilter" value="">
&promoNameFilter=<bean:write name="promotionListHeadForm" property="promoNameFilter" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoCategoryFilter" value="">
&promoCategoryFilter=<bean:write name="promotionListHeadForm" property="promoCategoryFilter" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoFromDate" value="">
&promoFromDate=<bean:write name="promotionListHeadForm" property="promoFromDate" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoToDate" value="">
&toFromDate=<bean:write name="promotionListHeadForm" property="promoToDate" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="promotionListHeadForm" property="currentPage" />
</logic:notEqual>                                
"><bean:message key="app.edit" /></a>
            <% } %>
            <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                 <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
            <% } %>
          </td>
          <td align="right">
            <% if (permDeleteId.equalsIgnoreCase("true")) { %>	  
              <a href="PromotionDelete.do?key=<bean:write name="promotionListForm"
                property="key" />
<logic:notEqual name="promotionListHeadForm" property="promoNameFilter" value="">
&promoNameFilter=<bean:write name="promotionListHeadForm" property="promoNameFilter" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoCategoryFilter" value="">
&promoCategoryFilter=<bean:write name="promotionListHeadForm" property="promoCategoryFilter" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoFromDate" value="">
&promoFromDate=<bean:write name="promotionListHeadForm" property="promoFromDate" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="promoToDate" value="">
&toFromDate=<bean:write name="promotionListHeadForm" property="promoToDate" />
</logic:notEqual>
<logic:notEqual name="promotionListHeadForm" property="currentPage" value="">
&pageNo=<bean:write name="promotionListHeadForm" property="currentPage" />
</logic:notEqual>                
"  onclick="return confirm('<bean:message key="app.promotion.delete.message" />')" ><bean:message key="app.delete" /></a>
            <% } %>
          </td>
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='PromotionList.do?promoNameFilter=<bean:write name="promotionListHeadForm" 
        property="promoNameFilter" />&promoCategoryFilter=<bean:write name="promotionListHeadForm" 
        property="promoCategoryFilter" />&promoFromDate=<bean:write name="promotionListHeadForm" 
        property="promoFromDate" />&promoToDate=<bean:write name="promotionListHeadForm" 
        property="promoToDate" />&pageNo=<bean:write name="promotionListHeadForm" 
        property="previousPage" /> "><bean:message key="app.previous" />
      </button>    
    </td>
    <td colspan="4" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="promotionListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="promotionListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="promotionListHeadForm" property="recordCount" />      
    </td>       
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='PromotionList.do?promoNameFilter=<bean:write name="promotionListHeadForm" 
        property="promoNameFilter" />&promoCategoryFilter=<bean:write name="promotionListHeadForm" 
        property="promoCategoryFilter" />&promoFromDate=<bean:write name="promotionListHeadForm" 
        property="promoFromDate" />&promoToDate=<bean:write name="promotionListHeadForm" 
        property="promoToDate" />&pageNo=<bean:write name="promotionListHeadForm" 
        property="nextPage" /> "><bean:message key="app.next" />
      </button>
    </td>  
  </tr>
</table>