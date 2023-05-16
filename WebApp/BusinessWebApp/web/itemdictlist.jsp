<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<%
  String currentColor = "ODD";
  int i = 0;
  String url = "ItemDictionaryList.do?";
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";  
  Integer permOffset = new Integer("0");
  String permAddId = "false";
  String permViewId = "false";
  String permEditId = "false";
  String permDeleteId = "false";
  String permLockAvailableId = "false";
  String permLockedBy = "";  
  
  // Key of either the item dictionary, order, or bundle.
  String oKey = null;
  if (!(request.getParameter("key") == null)) {
    oKey = request.getParameter("key");
  }
  else {
    if (!(request.getAttribute("key") == null)) {
      oKey = request.getAttribute("key").toString();
    }
  }
  // Item Dictionary key when an order is processed.
  String idKey = null;
  if (request.getParameter("idKey") != null) {
    idKey = request.getParameter("idKey");
  }
  else {
    if (request.getAttribute("idKey") != null) {
      idKey = request.getAttribute("idKey").toString();
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

  // Purchase order key is passed for item orders.
  String poKey = null;
  if (request.getParameter("poKey") != null) {
    poKey = request.getParameter("poKey");
  }
  
  // Pet Exam key is passed for pet anesthesia agents.
  String peKey = null;
  if (request.getParameter("peKey") != null) {
    peKey = request.getParameter("peKey");
  }  
  
  String itemFilterURL = "";
  if (request.getParameter("brandNameFilter") != null) 
    if (request.getParameter("brandNameFilter") != "")
      itemFilterURL = itemFilterURL + "&brandNameFilter=" + request.getParameter("brandNameFilter");    
  if (request.getParameter("itemNumFilter") != null) 
    if (request.getParameter("itemNumFilter") != "")
      itemFilterURL = itemFilterURL + "&itemNumFilter=" + request.getParameter("itemNumFilter");
   if (request.getParameter("genericNameFilter") != null)
    if (request.getParameter("genericNameFilter") != "")             
      itemFilterURL = itemFilterURL + "&genericNameFilter=" + request.getParameter("genericNameFilter");
  if (request.getParameter("idPageNo") != null)
    itemFilterURL = itemFilterURL + "&idPageNo=" + request.getParameter("idPageNo");
  if (ctKey != null)
    itemFilterURL = itemFilterURL + "&ctKey=" + ctKey;
  if (buKey != null) 
    itemFilterURL = itemFilterURL + "&buKey=" +buKey;
  if (sKey != null)
    itemFilterURL = itemFilterURL + "&sKey=" + sKey; 
  if (idKey != null) 
    itemFilterURL = itemFilterURL + "&key=" + oKey + "&idKey=" + idKey;
  if (peKey != null)
    itemFilterURL = itemFilterURL + "&peKey=" + peKey;
  if (poKey != null)
    itemFilterURL = itemFilterURL + "&poKey=" + poKey;
%>   

<logic:equal name="itemDictionaryListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="itemDictionaryListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>
    
<!-- 
  Iterate one row in the permission list for the Add permission.
  The row permissions are stored in a separate arraylist for flexibility.
 -->  
<logic:iterate id="permissionListForm"
               name="itemDictionaryListHeadForm"
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
    <form action="ItemDictionaryList.do" name="itemFilter" method="post">    
    <th>
      <% if (idKey != null) { %>
        <input type="hidden" name="key" value="<%=oKey%>" >
        <input type="hidden" name="idKey" value="<%=idKey%>">
      <% } %>
      <% if (ctKey != null) { %>
        <input type="hidden" name="ctKey" value="<%=ctKey%>">
      <% } %>
      <% if (buKey != null) { %>
        <input type="hidden" name="buKey" value="<%=buKey%>">
      <% } %>
      <% if (sKey != null) { %>
        <input type="hidden" name="sKey" value="<%=sKey%>">
      <% } %>       
      <% if (peKey != null) { %>
        <input type="hidden" name="peKey" value="<%=peKey%>">
      <% } %>
      <% if (poKey != null) { %>
        <input type="hidden" name="poKey" value="<%=poKey%>">
      <% } %>
        <bean:message key="app.itemdictionary.num" />&nbsp;
        <input type="text" name="itemNumFilter" size="4" maxlength="8" value="<bean:write name="itemDictionaryListHeadForm" property="itemNumFilter" />" >
    </th>      
    <th>
      <bean:message key="app.itemdictionary.genericName"/>&nbsp;
      <input type="text" name="genericNameFilter" size="4" maxlength="8" value="<bean:write name="itemDictionaryListHeadForm" property="genericNameFilter" />" >
    </th>      
    <th>
      <bean:message key="app.itemdictionary.name" />&nbsp;
      <input type="text" name="brandNameFilter" size="4" maxlength="8" value="<bean:write name="itemDictionaryListHeadForm" property="brandNameFilter" />" >
      &nbsp;&nbsp;&nbsp;
      <input type="submit" value="<bean:message key="app.runIt" />">
    </th>    
    </form>    
    <th>
      <bean:message key="app.itemdictionary.size" />/<bean:message key="app.itemdictionary.sizeUnit" />
    </th>    
    <th><bean:message key="app.itemdictionary.manufacturer" /></th>
    <% if (ctKey == null) { %>
      <th><bean:message key="app.itemdictionary.itemUnitsSold" /></th>
    <% } %>
    <th><bean:message key="app.itemdictionary.qtyOnHand"/></th>
     <% if ( idKey == null) { %> 
      <th><bean:message key="app.itemdictionary.firstCost"/></th>     
      <th colspan="2" align="right">
        <% if (permAddId.equalsIgnoreCase("true")) { %>
          <button type="button" onClick="window.location='ItemDictionaryEntry.do' "><bean:message key="app.itemdictionary.addTitle" /></button>
        <% } %>
      </th>  
    <% } else { %>
      <th><bean:message key="app.itemdictionary.itemPrice"/></th>         
    <% } %>
  </tr>      
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>  
<logic:notEqual name="itemDictionaryListHeadForm" property="lastRecord" value="0">     
    <!-- iterate over the dictionary items -->     
    <logic:iterate id="itemDictionaryListForm"
                   name="itemDictionaryListHeadForm"
                   property="itemDictionaryListForm"
                   scope="session"
                   type="com.wolmerica.itemdictionary.ItemDictionaryListForm">
<!-- 
  Iterate one row in the permission list for View, Edit, and Delete permission.
  The row permissions are stored in a separate arraylist for flexibility.
 --> 
        <% permOffset = new Integer(i); %>
        <logic:iterate id="permissionListForm"
                       name="itemDictionaryListHeadForm"
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
        <% if (poKey != null) { %>
             <a href="PurchaseOrderItemAddToCart.do?key=<%=oKey%>&idKey=<bean:write name="itemDictionaryListForm" property="key" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="itemNum" /></a>
        <% } else { %>
          <% if (peKey != null) { %>
               <a href="PetAnestheticAdd.do?key=<%=peKey%>&tabId=anestheticTab&idKey=<bean:write name="itemDictionaryListForm" property="key" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="itemNum" /></a>
          <% } else { %>
               <bean:write name="itemDictionaryListForm" property="itemNum" />
          <% } %>
        <% } %>
      <% } else { %>
        <% if (buKey != null) { %>
             <a href="BundleDetailAddToCart.do?key=<%=buKey%>&pbiKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="itemNum" /></a>
        <% } else { %>
          <% if (sKey != null) { %>
               <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="false">
                 <a href="ScheduleGet.do?key=<%=sKey%>"><bean:write name="itemDictionaryListForm" property="itemNum" /></a>
               </logic:equal>
               <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="true">
                 <a href="WorkOrderAddToCart.do?key=<%=sKey%>&pbiKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="itemNum" /></a>
               </logic:equal>
          <% } else { %>
               <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="true">
                 <a href="CustomerInvoiceItemAddToCart.do?key=<%=oKey%>&idKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="itemNum" /></a>
               </logic:equal>
               <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="false">
                 <logic:equal name="itemDictionaryListForm" property="sourceKey" value="">
                   <bean:write name="itemDictionaryListForm" property="itemNum" />
                 </logic:equal>
                 <logic:notEqual name="itemDictionaryListForm" property="sourceKey" value="">
                   <logic:equal name="itemDictionaryListForm" property="sourceTypeKey" value="13">
                     <a href="CustomerInvoiceGet.do?key=<bean:write name="itemDictionaryListForm" property="sourceKey" /> "><bean:write name="itemDictionaryListForm" property="itemNum" /></a>
                   </logic:equal>
                   <logic:equal name="itemDictionaryListForm" property="sourceTypeKey" value="14">
                     <a href="VendorInvoiceGet.do?key=<bean:write name="itemDictionaryListForm" property="sourceKey" /> "><bean:write name="itemDictionaryListForm" property="itemNum" /></a>
                   </logic:equal>
                 </logic:notEqual>
               </logic:equal>
          <% } %>
        <% } %>
      <% } %>
    </td>
    <td>
      <bean:write name="itemDictionaryListForm" property="genericName" />
    </td>
    <td>
      <% if (ctKey == null) { %>
        <% if (poKey != null) { %>
             <a href="PurchaseOrderItemAddToCart.do?key=<%=oKey%>&idKey=<bean:write name="itemDictionaryListForm" property="key" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="brandName" /></a>
        <% } else { %>
          <% if (peKey != null) { %>
               <a href="PetAnestheticAdd.do?key=<%=peKey%>&tabId=anestheticTab&idKey=<bean:write name="itemDictionaryListForm" property="key" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="brandName" /></a>
          <% } else { %>
               <bean:write name="itemDictionaryListForm" property="brandName" />
          <% } %>
        <% } %>
      <% } else { %>
        <% if (buKey != null) { %>
             <a href="BundleDetailAddToCart.do?key=<%=buKey%>&pbiKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="brandName" /></a>
        <% } else { %>
          <% if (sKey != null) { %>
               <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="false">
                 <a href="ScheduleGet.do?key=<%=sKey%>"><bean:write name="itemDictionaryListForm" property="brandName" /></a>
               </logic:equal>
               <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="true">
                 <a href="WorkOrderAddToCart.do?key=<%=sKey%>&pbiKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="brandName" /></a>
               </logic:equal>
          <% } else { %>
               <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="true">
                 <a href="CustomerInvoiceItemAddToCart.do?key=<%=oKey%>&idKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:write name="itemDictionaryListForm" property="brandName" /></a>
               </logic:equal>
               <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="false">
                 <logic:equal name="itemDictionaryListForm" property="sourceKey" value="">
                   <bean:write name="itemDictionaryListForm" property="brandName" />
                 </logic:equal>
                 <logic:notEqual name="itemDictionaryListForm" property="sourceKey" value="">
                   <logic:equal name="itemDictionaryListForm" property="sourceTypeKey" value="13">
                     <a href="CustomerInvoiceGet.do?key=<bean:write name="itemDictionaryListForm" property="sourceKey" /> "><bean:write name="itemDictionaryListForm" property="brandName" /></a>
                   </logic:equal>
                   <logic:equal name="itemDictionaryListForm" property="sourceTypeKey" value="14">
                     <a href="VendorInvoiceGet.do?key=<bean:write name="itemDictionaryListForm" property="sourceKey" /> "><bean:write name="itemDictionaryListForm" property="brandName" /></a>
                   </logic:equal>
                 </logic:notEqual>
               </logic:equal>
          <% } %>
        <% } %>
      <% } %>
    </td>
    <td>
      <bean:write name="itemDictionaryListForm" property="size" />
      <bean:write name="itemDictionaryListForm" property="sizeUnit" />
    </td>
    <td>
      <bean:write name="itemDictionaryListForm" property="manufacturer" />
    </td>
    <% if (ctKey == null) { %>
      <td align="right">
        <bean:write name="itemDictionaryListForm" property="itemUnitsSold" />
      </td>
    <% } %>
    <td align="right">
      <bean:write name="itemDictionaryListForm" property="qtyOnHand" />
      <logic:equal name="itemDictionaryListForm" property="belowThresholdId" value="true">
        <img src="./images/down_red.gif" width="<bean:message key="app.arrow.width" />" height="<bean:message key="app.arrow.height" />" border="0">
      </logic:equal>
      <logic:equal name="itemDictionaryListForm" property="belowThresholdId" value="false">
        <img src="./images/up_green.gif" width="<bean:message key="app.arrow.width" />" height="<bean:message key="app.arrow.height" />" border="0">
      </logic:equal>
    </td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="itemDictionaryListForm" property="firstCost" />
    </td>    
    <% if (idKey == null) { %>
    <td>
      <% if (permViewId.equalsIgnoreCase("true")) { %>	  
        <a href="ItemDictionaryGet.do?key=<bean:write name="itemDictionaryListForm" 
          property="key" /><%=itemFilterURL%> "><bean:message key="app.view" /></a>
      <% } %>
      <% if (permEditId.equalsIgnoreCase("true")) { %>
        <a href="ItemDictionaryGet.do?key=<bean:write name="itemDictionaryListForm" 
          property="key" /><%=itemFilterURL%> "><bean:message key="app.edit" /></a>
      <% } %>
      <% if (permLockAvailableId.equalsIgnoreCase("false")) { %>            
           <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="<bean:message key="app.lock.by" />:&nbsp;<%=permLockedBy%>" >
      <% } %>      
    </td>
    <td align="right">
      <logic:equal name="itemDictionaryListForm" property="allowDeleteId" value="true">    
        <% if (permDeleteId.equalsIgnoreCase("true")) { %>      
          <a href="ItemDictionaryDelete.do?key=<bean:write name="itemDictionaryListForm" property="key" /><%=itemFilterURL%> " onclick="return confirm('<bean:message key="app.itemdictionary.delete.message" />')" ><bean:message key="app.delete" /></a>
        <% } %>
      </logic:equal>
    </td>
    <% } else { %>     
      <td colspan = "2" align="center">
        <% if (ctKey == null) { %>
          <% if (poKey != null) { %>
               <a href="PurchaseOrderItemAddToCart.do?key=<%=oKey%>&idKey=<bean:write name="itemDictionaryListForm" property="key" /><%=itemFilterURL%> "><bean:message key="app.addtocart" /></a>
          <% } else { %>
            <% if (peKey != null) { %>
               <a href="PetAnestheticAdd.do?key=<%=peKey%>&tabId=anestheticTab&idKey=<bean:write name="itemDictionaryListForm" property="key" /><%=itemFilterURL%> "><bean:message key="app.addtopetexam" /></a>
            <% } %>
          <% } %>               
        <% } else { %>
          <% if (buKey != null) { %> 
               <a href="BundleDetailAddToCart.do?key=<%=buKey%>&pbiKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:message key="app.addtocart" /></a>
          <% } else { %>
            <% if (sKey != null) { %>
                 <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="false">
                   <a href="ScheduleGet.do?key=<%=sKey%>"><bean:message key="app.inuse" /></a>
                     <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="" >
                 </logic:equal>
                 <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="true">          
                   <a href="WorkOrderAddToCart.do?key=<%=sKey%>&pbiKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:message key="app.addtocart" /></a>
                 </logic:equal>                   
            <% } else { %>         
                 <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="true">
                   <a href="CustomerInvoiceItemAddToCart.do?key=<%=oKey%>&idKey=<bean:write name="itemDictionaryListForm" property="key" />&availableQty=<bean:write name="itemDictionaryListForm" property="qtyOnHand" /><%=itemFilterURL%> "><bean:message key="app.addtocart" /></a>
                 </logic:equal>
                 <logic:equal name="itemDictionaryListForm" property="sellableItemId" value="false">
                   <logic:equal name="itemDictionaryListForm" property="sourceKey" value="">
                     <bean:message key="app.itemdictionary.lowInventory" />
                   </logic:equal>
                   <logic:notEqual name="itemDictionaryListForm" property="sourceKey" value="">
                     <logic:equal name="itemDictionaryListForm" property="sourceTypeKey" value="13">
                       <a href="CustomerInvoiceGet.do?key=<bean:write name="itemDictionaryListForm" property="sourceKey" /> "><bean:message key="app.inuse" /></a>
                        <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="" >
                     </logic:equal>
                     <logic:equal name="itemDictionaryListForm" property="sourceTypeKey" value="14">
                       <a href="VendorInvoiceGet.do?key=<bean:write name="itemDictionaryListForm" property="sourceKey" /> "><bean:message key="app.inuse" /></a>
                        <img src="./images/lock.gif" width="<bean:message key="app.lock.width" />" height="<bean:message key="app.lock.height" />" border="0" title="" >
                     </logic:equal>
                   </logic:notEqual>            
                 </logic:equal>
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
    <td colspan="2">
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='ItemDictionaryList.do?brandNameFilter=<bean:write name="itemDictionaryListHeadForm" 
        property="brandNameFilter" />&genericNameFilter=<bean:write name="itemDictionaryListHeadForm" 
        property="genericNameFilter" />&idPageNo=<bean:write name="itemDictionaryListHeadForm" 
        property="previousPage" /><% if (ctKey != null) { %>&ctKey=<%=ctKey%><% } %><% if (buKey != null) { %>&buKey=<%=buKey%><% } %><% if (sKey != null) { %>&sKey=<%=sKey%><% } %><% if (idKey != null) { %>&key=<%=oKey%>&idKey=<%=idKey%><% } %><% if (peKey != null) { %>&peKey=<%=peKey%><% } %><% if (poKey != null) { %>&poKey=<%=poKey%><% } %>' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="6">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="itemDictionaryListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="itemDictionaryListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="itemDictionaryListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='ItemDictionaryList.do?brandNameFilter=<bean:write name="itemDictionaryListHeadForm" 
        property="brandNameFilter" />&genericNameFilter=<bean:write name="itemDictionaryListHeadForm" 
        property="genericNameFilter" />&idPageNo=<bean:write name="itemDictionaryListHeadForm" 
        property="nextPage" /><% if (ctKey != null) { %>&ctKey=<%=ctKey%><% } %><% if (buKey != null) { %>&buKey=<%=buKey%><% } %><% if (sKey != null) { %>&sKey=<%=sKey%><% } %><% if (idKey != null) { %>&key=<%=oKey%>&idKey=<%=idKey%><% } %><% if (peKey != null) { %>&peKey=<%=peKey%><% } %><% if (poKey != null) { %>&poKey=<%=poKey%><% } %>' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>

