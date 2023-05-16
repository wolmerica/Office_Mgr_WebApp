<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
   
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
  
  // Key of either the customer invoice, bundle, work order, or purchase order.
  String oKey = null;
  if (!(request.getParameter("key") == null)) {
    oKey = request.getParameter("key");
  }
  else {
    if (!(request.getAttribute("key") == null)) {
      oKey = request.getAttribute("key").toString();
    }
  }
  // Customer type key is passed for customer invoicing.
  String ctKey = null;
  if (request.getParameter("ctKey") != null) {
    ctKey = request.getParameter("ctKey");
  }
  
  // Bundle key is passed for goods and services bundle.
  String buKey = null;
  if (request.getParameter("buKey") != null) {
    buKey = request.getParameter("buKey");
  }  
  
  // Schedule key is passed for work order services.
  String sKey = null;
  if (request.getParameter("sKey") != null) {
    sKey = request.getParameter("sKey");
  }   
  
  // Purchase order key is passed for service orders.
  String poKey = null;
  if (request.getParameter("poKey") != null) {
    poKey = request.getParameter("poKey");
  }    
  
  String serviceFilterURL = "";
  if (request.getParameter("serviceNameFilter") != null) 
    if (request.getParameter("serviceNameFilter") != "")
      serviceFilterURL += "&serviceNameFilter=" + request.getParameter("serviceNameFilter");
  if (request.getParameter("serviceNumFilter") != null)
    if (request.getParameter("serviceNumFilter") != "")
      serviceFilterURL += "&serviceNumFilter=" + request.getParameter("serviceNumFilter");
  if (request.getParameter("categoryNameFilter") != null)
    if (request.getParameter("categoryNameFilter") != "")             
      serviceFilterURL += "&categoryNameFilter=" + request.getParameter("categoryNameFilter");
  if (ctKey != null)
    serviceFilterURL += "&key=" + oKey + "&ctKey=" +ctKey;
  if (buKey != null)
    serviceFilterURL += "&buKey=" +buKey;
  if (sKey != null) 
    serviceFilterURL += "&sKey=" + sKey;  
  if (poKey != null)
    serviceFilterURL += "&poKey=" +poKey;  
    
  String sdPageNo = null;
  String sdPageFilterURL = "";
  if (request.getParameter("sdPageNo") != null) {
    sdPageNo = request.getParameter("sdPageNo");
    sdPageFilterURL = "&sdPageNo=" + sdPageNo;
  }
    
%>      
 
<logic:equal name="serviceDictionaryListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="serviceDictionaryListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="serviceDictionaryListHeadForm"
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
    <td colspan="10">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th>
      <form action="ServiceDictionaryList.do" name="serviceDictionaryFilter" method="post">
        <% if (ctKey != null) { %>
          <input type="hidden" name="key" value="<%=oKey%>">
          <input type="hidden" name="ctKey" value="<%=ctKey%>">
        <% } %>            
        <% if (buKey != null) { %>
          <input type="hidden" name="buKey" value="<%=buKey%>">
        <% } %>  
        <% if (sKey != null) { %>
          <input type="hidden" name="sKey" value="<%=sKey%>">
        <% } %>
        <% if (poKey != null) { %>
          <input type="hidden" name="poKey" value="<%=poKey%>">
        <% } %>
        <bean:message key="app.servicedictionary.num" />&nbsp;&nbsp;
        <input type="text" name="serviceNumFilter" size="4" value="<bean:write name="serviceDictionaryListHeadForm" property="serviceNumFilter" />" >
    </th>
    <th colspan="2">
       <bean:message key="app.servicedictionary.serviceCategory" />
       <input type="text" name="categoryNameFilter" size="4" value="<bean:write name="serviceDictionaryListHeadForm" property="categoryNameFilter" />" >
    </th>
    <th>
       <bean:message key="app.servicedictionary.name" />&nbsp;&nbsp;
       <input type="text" name="serviceNameFilter" size="4" value="<bean:write name="serviceDictionaryListHeadForm" property="serviceNameFilter" />" >
       &nbsp;&nbsp;&nbsp;
       <input type="submit" value="<bean:message key="app.runIt" />" >
      </form>        
    </th>
    <th align="center"><bean:message key="app.servicedictionary.releaseId" /></th>     
    <th align="right"><bean:message key="app.servicedictionary.durationTime" /></th> 
      <% if (ctKey == null) { %>
        <th align="right"><bean:message key="app.servicedictionary.serviceHoursSold" /></th> 
      <% } else { %>
        <th><bean:message key="app.servicedictionary.priceTypeName" /></th>         
      <% } %>
    <th align="right"><bean:message key="app.servicedictionary.serviceCost" /></th>
    <th colspan="2" align="right">
      <% if (ctKey == null) { %>
        <% if (permAddId.equalsIgnoreCase("true")) { %>        
          <button type="button" onClick="window.location='ServiceDictionaryEntry.do' "><bean:message key="app.servicedictionary.addTitle" /></button>                
        <% } %>
      <% } %>          
    </th>      
  </tr>    
  <tr>
    <td colspan=10">
      <hr>
    </td>
  <tr>  
<logic:notEqual name="serviceDictionaryListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the service dictionary -->       
  <logic:iterate id="serviceDictionaryListForm"
                 name="serviceDictionaryListHeadForm"
                 property="serviceDictionaryListForm"
                 scope="session"
                 type="com.wolmerica.servicedictionary.ServiceDictionaryListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="serviceDictionaryListHeadForm"
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
	  <td>
            <% if (ctKey == null) { %>
              <bean:write name="serviceDictionaryListForm" property="serviceNum" />
            <% } else { %>
              <% if (buKey != null) { %>
                   <a href="BundleDetailAddToCart.do?key=<%=buKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:write name="serviceDictionaryListForm" property="serviceNum" /></a>
              <% } else { %>
                <% if (sKey != null) { %>
                     <a href="WorkOrderAddToCart.do?key=<%=sKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:write name="serviceDictionaryListForm" property="serviceNum" /></a>
                <% } else { %>
                   <% if (poKey != null) { %>
                     <a href="PurchaseOrderServiceAddToCart.do?key=<%=poKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:write name="serviceDictionaryListForm" property="serviceNum" /></a>
                   <% } else { %>
                     <a href="CustomerInvoiceServiceAddToCart.do?key=<%=oKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:write name="serviceDictionaryListForm" property="serviceNum" /></a>
                  <% } %>
                <% } %>
              <% } %>
            <% } %>
	  </td>
	  <td>
            <bean:write name="serviceDictionaryListForm" property="serviceCategory" />
	  </td>
	  <td>
            <bean:write name="serviceDictionaryListForm" property="profileNum" />
	  </td>
	  <td>
            <% if (ctKey == null) { %>              
              <bean:write name="serviceDictionaryListForm" property="serviceName" />
            <% } else { %>
              <% if (buKey != null) { %>
                   <a href="BundleDetailAddToCart.do?key=<%=buKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:write name="serviceDictionaryListForm" property="serviceName" /></a>
              <% } else { %>
                <% if (sKey != null) { %>
                     <a href="WorkOrderAddToCart.do?key=<%=sKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:write name="serviceDictionaryListForm" property="serviceName" /></a>
                <% } else { %>
                   <% if (poKey != null) { %>
                     <a href="PurchaseOrderServiceAddToCart.do?key=<%=poKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:write name="serviceDictionaryListForm" property="serviceName" /></a>
                   <% } else { %>
                     <a href="CustomerInvoiceServiceAddToCart.do?key=<%=oKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:write name="serviceDictionaryListForm" property="serviceName" /></a>
                  <% } %>
                <% } %>
              <% } %>
            <% } %>
	  </td>
	  <td align="center">
            <html:checkbox name="serviceDictionaryListForm" property="releaseId" disabled="true" />
          </td>          
          <td align="right">
            <bean:write name="serviceDictionaryListForm" property="durationHours" />:
            <logic:equal name="serviceDictionaryListForm" property="durationMinutes" value="0">
              00
            </logic:equal>
            <logic:notEqual name="serviceDictionaryListForm" property="durationMinutes" value="0">
              <bean:write name="serviceDictionaryListForm" property="durationMinutes" />
            </logic:notEqual>
	  </td>      
          <% if (ctKey == null) { %>          
            <td align="right">
              <bean:write name="serviceDictionaryListForm" property="serviceHoursSold" />
            </td>
          <% } else { %>          
            <td>
              <bean:write name="serviceDictionaryListForm" property="priceTypeName" />
            </td>
          <% } %>          
          <td align="right">
            <bean:message key="app.localeCurrency" />           
            <bean:write name="serviceDictionaryListForm" property="serviceCost" />
	  </td>
          <% if (ctKey == null) { %>
            <td>  
              <% if (permViewId.equalsIgnoreCase("true")) { %>	  
                <a href="ServiceDictionaryGet.do?key=<bean:write name="serviceDictionaryListForm"
                  property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:message key="app.view" /></a>
              <% } %>
              <% if (permEditId.equalsIgnoreCase("true")) { %>
                <a href="ServiceDictionaryGet.do?key=<bean:write name="serviceDictionaryListForm"
                  property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:message key="app.edit" /></a>
              <% } %>
              <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
                   <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
              <% } %>
            </td>
            <td align="right">
              <logic:equal name="serviceDictionaryListForm" property="allowDeleteId" value="true">                
                <% if (permDeleteId.equalsIgnoreCase("true")) { %>	  
                  <a href="ServiceDictionaryDelete.do?key=<bean:write name="serviceDictionaryListForm"
	            property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> " onclick="return confirm('<bean:message key="app.servicedictionary.delete.message" />')" ><bean:message key="app.delete" /></a>
                <% } %>
              </logic:equal>
            </td>
          <% } else { %>
            <td colspan="2" align="center"> 
              <% if (buKey != null) { %>
                   <a href="BundleDetailAddToCart.do?key=<%=buKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:message key="app.addtocart" /></a>              
              <% } else { %>
                <% if (sKey != null) { %>
                     <a href="WorkOrderAddToCart.do?key=<%=sKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:message key="app.addtocart" /></a>
                <% } else { %>                     
                   <% if (poKey != null) { %>
                     <a href="PurchaseOrderServiceAddToCart.do?key=<%=poKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:message key="app.addtocart" /></a>                                       
                   <% } else { %>
                     <a href="CustomerInvoiceServiceAddToCart.do?key=<%=oKey%>&pbsKey=<bean:write name="serviceDictionaryListForm" property="key" /><%=sdPageFilterURL%><%=serviceFilterURL%> "><bean:message key="app.addtocart" /></a>
                  <% } %>
                <% } %>
              <% } %>                
            </td>
          <% } %>	  
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="10">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='ServiceDictionaryList.do?serviceNameFilter=<bean:write name="serviceDictionaryListHeadForm" 
        property="serviceNameFilter" />&categoryNameFilter=<bean:write name="serviceDictionaryListHeadForm" 
        property="categoryNameFilter" />&sdPageNo=<bean:write name="serviceDictionaryListHeadForm" 
        property="previousPage" /><%=serviceFilterURL%>' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="7" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="serviceDictionaryListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="serviceDictionaryListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="serviceDictionaryListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='ServiceDictionaryList.do?serviceNameFilter=<bean:write name="serviceDictionaryListHeadForm" 
        property="serviceNameFilter" />&categoryNameFilter=<bean:write name="serviceDictionaryListHeadForm" 
        property="categoryNameFilter" />&sdPageNo=<bean:write name="serviceDictionaryListHeadForm" 
        property="nextPage" /><%=serviceFilterURL%>' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>