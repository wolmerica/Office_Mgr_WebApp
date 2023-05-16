<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>   
<%
  String currentColor = "ODD";
  int i = -1;
  String attTitle = "app.purchaseorderitem.editTitle";            
  String attAction = "/PurchaseOrderItemEdit.do";    
  
  String attDisableAutoLoad = "disabled";     
  boolean attDisableSave = false;  
  boolean attDisableEdit = true;
  boolean attDisableCommentEdit = false;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
       attDisableAutoLoad = "";
     }
  }
  String attKey = request.getParameter("key").toString();
  if (attKey == null) {
    attKey = request.getAttribute("key").toString();
  }
  String orderQtyBase = "orderQty";
  String orderQtyError;
  
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
%>

<logic:equal name="purchaseOrderItemHeadForm" property="lastRecord" value="0"> 
  <%
    attDisableSave = true;
  %>
</logic:equal>

<logic:notEqual name="purchaseOrderItemHeadForm" property="permissionStatus" value="LOCKED"> 
<bean:message key="app.readonly"/>
  <logic:notEqual name="purchaseOrderItemHeadForm" property="permissionStatus" value="READONLY">
    [<bean:write name="purchaseOrderItemHeadForm" property="permissionStatus" />]
  </logic:notEqual>
  <%
    attDisableCommentEdit = true;
    attDisableSave = true;
  %>
</logic:notEqual>


<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="PurchaseOrderGet.do" name="orderGet" method="post">                                  
        <input type="hidden" name="key" value="<%=request.getParameter("key")%>">  
        <input type="submit" value="<bean:message key="app.purchaseorderitem.back" />">
      </form>            
    </td>
    <td colspan="7" align="right">
      <button type="button" <%=attDisableAutoLoad%> onClick="window.location='PurchaseOrderItemAutoLoadCart.do?key=<%=request.getParameter("key")%>' "><bean:message key="app.purchaseorderitem.recommendation" /></button>
    </td>    
  </tr>
  <tr>
    <td colspan=2>
      <strong><bean:message key="app.purchaseorderitem.purchaseOrderNumber" />:</strong>
      <bean:write name="purchaseOrderItemHeadForm" property="purchaseOrderNumber" />      
    </td>
    <td colspan=3>
      <strong><bean:message key="app.purchaseorderitem.vendorName" />:</strong>
      <bean:write name="purchaseOrderItemHeadForm" property="vendorName" />
    </td>
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>     
  <tr align="left">
    <th><bean:message key="app.purchaseorderitem.itemNum" /></th>
    <th><bean:message key="app.purchaseorderitem.brandName" /></th>
    <th>
      <bean:message key="app.purchaseorderitem.size" />/<bean:message key="app.purchaseorderitem.sizeUnit" />
    </th> 
    <th><bean:message key="app.purchaseorderitem.rebate" /></th>    
    <th><bean:message key="app.purchaseorderitem.orderQty" /></th>
    <th align="right"><bean:message key="app.purchaseorderitem.firstCost" /></th>
    
    <th align="right"><bean:message key="app.purchaseorderitem.extendTotal" /></th>
    <th colspan align="right">     
      <% if (!(attDisableEdit)) { %>
        <form action="ItemDictionaryList.do" name="orderAdd" method="post">
          <input type="hidden" name="key" value="<%=request.getParameter("key").toString()%>">
          <input type="hidden" name="poKey" value="<%=request.getParameter("key").toString()%>">
          <input type="hidden" name="idKey" value="<%=request.getParameter("key").toString()%>">
        <% if (!(request.getParameter("brandNameFilter") == null)) { %>
          <input type="hidden" name="brandNameFilter" value="<%=request.getParameter("brandNameFilter").toString()%>">
        <% } %>
        <% if (!(request.getParameter("itemNumFilter") == null)) { %>
          <input type="hidden" name="itemNumFilter" value="<%=request.getParameter("itemNumFilter").toString()%>">
        <% } %>
        <% if (!(request.getParameter("genericNameFilter") == null)) { %>
          <input type="hidden" name="genericNameFilter" value="<%=request.getParameter("genericNameFilter").toString()%>">
        <% } %>        
        <% if (!(request.getParameter("idPageNo") == null)) { %>
          <input type="hidden" name="idPageNo" value="<%=request.getParameter("idPageNo").toString()%>">
        <% } %>          
          <input type="submit" value="Add Order Item">
        </form>
      <% } else { %>
        <form action="#" name="orderAdd" method="post">
          <input type="submit" value="Add Order Item" disabled >
        </form>        
      <% } %>           
    </th>    
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
  <html:form action="<%=attAction%>"> 
  <input type="hidden" name="key" value="<%=attKey%>" >         
  
  <!-- Iterate over the purchaseorder items. 
       Note that an additional row is added to accomodate issues 
       with the "Formatter" validation class.  
   -->     
  <logic:iterate id="purchaseOrderItemForm"
                 name="purchaseOrderItemHeadForm"
                 property="purchaseOrderItemForm"
                 scope="session"
                 type="com.wolmerica.purchaseorderitem.PurchaseOrderItemForm">
    <logic:notEqual name="purchaseOrderItemForm" property="brandName" value="">      
      <%
        ++i;      
        if ( i % 2 == 0) { currentColor = "ODD"; }
        else { currentColor = "EVEN"; }
        orderQtyError = orderQtyBase + i;
      %>                              
      <tr align="left" class="<%=currentColor %>" >
        <td>
          <bean:write name="purchaseOrderItemForm" property="itemNum" />
        </td>
        <td>          
          <bean:write name="purchaseOrderItemForm" property="brandName" />
        </td>  
        <td>
          <bean:write name="purchaseOrderItemForm" property="size" />
          <bean:write name="purchaseOrderItemForm" property="sizeUnit" />
        </td>
        <td align="center"> 
        <logic:notEqual name="purchaseOrderItemForm" property="rebateCount" value="0">    
          <a href="RebateList.do?poiKey=<bean:write name="purchaseOrderItemForm" property="key" />&idKey=<bean:write name="purchaseOrderItemForm" property="itemDictionaryKey" />&poKey=<%=request.getParameter("key")%>"><bean:write name="purchaseOrderItemForm" property="rebateCount" /><bean:message key="app.purchaseorderitem.rebateAvailable" /> </a>
          <logic:notEqual name="purchaseOrderItemForm" property="rebateInstanceCount" value="0">    
            &nbsp;
            <a href="RebateInstanceList.do?poiKey=<bean:write name="purchaseOrderItemForm" property="key" />&idKey=<bean:write name="purchaseOrderItemForm" property="itemDictionaryKey" />&poKey=<%=request.getParameter("key")%>"><bean:write name="purchaseOrderItemForm" property="rebateInstanceCount" /><bean:message key="app.purchaseorderitem.rebateOpen" /></a>         
          </logic:notEqual>
        </logic:notEqual>
        </td>                
        <td>   
          <html:text indexed="true" name="purchaseOrderItemForm" property="orderQty" size="1" maxlength="5" readonly="<%=attDisableEdit%>" />
          <html:errors property="<%=orderQtyError%>" />             
        </td>
        <td align="right">         
          <bean:message key="app.localeCurrency" />
          <bean:write name="purchaseOrderItemForm" property="firstCost" />      
        </td>
        <td align="right"> 
          <bean:message key="app.localeCurrency" />
          <bean:write name="purchaseOrderItemForm" property="extendCost" />
        </td>
        <td align="right">
          <% if (!(attDisableEdit)) { %>                
            <a href="PurchaseOrderItemDelete.do?poiKey=<bean:write name="purchaseOrderItemForm" property="key" />&key=<%=request.getParameter("key")%><%=itemFilterURL%> " 
              onclick="return confirm('<bean:message key="app.purchaseorderitem.delete.message" />')" >Delete</a>
          <% } %>                
        </td>
      </tr>
      <tr class="<%=currentColor %>" >
        <td colspan="2"></td>
        <td colspan="6">     
          <strong><bean:message key="app.purchaseorderitem.noteLine1" />:</strong>
          &nbsp;&nbsp;
          <html:text indexed="true" name="purchaseOrderItemForm" property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableCommentEdit%>" />            
        </td>  
      </tr>
    </logic:notEqual>
 </logic:iterate>    
  
  <%
    if (i == -1) { attDisableEdit=true; }
    ++i;      
    if ( i % 2 == 0) { currentColor = "ODD"; } 
    else { currentColor = "EVEN"; }    
  %>
  <tr align="left" class="<%=currentColor %>" >
    <td></td>
    <td>
      <strong>
        <bean:message key="app.purchaseorderitem.itemTotal" />
      </strong>
    </td>       
    <td colspan="4">&nbsp;</td>
    <td align="right">       
      <strong>
        <bean:message key="app.localeCurrency" />
        <bean:write name="purchaseOrderItemHeadForm" property="itemTotal" />      
      </strong>
    </td>
    <td align="right">
      <% if (!(request.getParameter("brandNameFilter") == null)) { %>
        <input type="hidden" name="brandNameFilter" value="<%=request.getParameter("brandNameFilter").toString()%>">
      <% } %>
      <% if (!(request.getParameter("itemNumFilter") == null)) { %>
        <input type="hidden" name="itemNumFilter" value="<%=request.getParameter("itemNumFilter").toString()%>">
      <% } %>
      <% if (!(request.getParameter("genericNameFilter") == null)) { %>
        <input type="hidden" name="genericNameFilter" value="<%=request.getParameter("genericNameFilter").toString()%>">
      <% } %>        
      <% if (!(request.getParameter("idPageNo") == null)) { %>
        <input type="hidden" name="idPageNo" value="<%=request.getParameter("idPageNo").toString()%>">
      <% } %>              
      <html:submit value="Save Item Detail" disabled="<%=attDisableSave%>" />
    </td>
  </tr>    
  </html:form>                   
</table>