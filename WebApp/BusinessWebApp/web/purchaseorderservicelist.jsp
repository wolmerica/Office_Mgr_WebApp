<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>   
<%
  String currentColor = "ODD";
  int i = -1;
  String attTitle = "app.purchaseorderservice.editTitle";            
  String attAction = "/PurchaseOrderServiceEdit.do";    
  
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
  
  String serviceFilterURL = "";
  if (request.getParameter("serviceNameFilter") != null) 
    if (request.getParameter("serviceNameFilter") != "")
      serviceFilterURL = serviceFilterURL + "&serviceNameFilter=" + request.getParameter("serviceNameFilter");
  if (request.getParameter("serviceNumFilter") != null)
    if (request.getParameter("serviceNumFilter") != "")
      serviceFilterURL = serviceFilterURL + "&serviceNumFilter=" + request.getParameter("serviceNumFilter");
  if (request.getParameter("categoryNameFilter") != null)
    if (request.getParameter("categoryNameFilter") != "")             
      serviceFilterURL = serviceFilterURL + "&categoryNameFilter=" + request.getParameter("categoryNameFilter");
  if (request.getParameter("sdPageNo") != null)
    serviceFilterURL = serviceFilterURL + "&sdPageNo=" + request.getParameter("sdPageNo");         
%>

<logic:equal name="purchaseOrderServiceHeadForm" property="lastRecord" value="0"> 
  <%
    attDisableSave = true;
  %>
</logic:equal>

<logic:notEqual name="purchaseOrderServiceHeadForm" property="permissionStatus" value="LOCKED"> 
<bean:message key="app.readonly"/>
  <logic:notEqual name="purchaseOrderServiceHeadForm" property="permissionStatus" value="READONLY">
    [<bean:write name="purchaseOrderServiceHeadForm" property="permissionStatus" />]
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
        <input type="submit" value="<bean:message key="app.purchaseorderservice.back" />">
      </form>            
    </td>
    <td colspan="7" align="right">
    </td>    
  </tr>
  <tr>
    <td colspan=2>
      <strong><bean:message key="app.purchaseorderservice.purchaseOrderNumber" />:</strong>
      <bean:write name="purchaseOrderServiceHeadForm" property="purchaseOrderNumber" />      
    </td>
    <td colspan=3>
      <strong><bean:message key="app.purchaseorderservice.vendorName" />:</strong>
      <bean:write name="purchaseOrderServiceHeadForm" property="vendorName" />
    </td>
  </tr>
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>     
  <tr align="left">
    <th><bean:message key="app.purchaseorderservice.serviceNum" /></th>
    <th><bean:message key="app.purchaseorderservice.serviceName" /></th>
    <th><bean:message key="app.purchaseorderservice.billBy" /></th>  
    <th><bean:message key="app.purchaseorderservice.orderQty" /></th>
    <th align="right"><bean:message key="app.purchaseorderservice.laborCost" /></th>
    <th align="right"><bean:message key="app.purchaseorderservice.extendTotal" /></th>
    <th colspan align="right">     
      <% if (!(attDisableEdit)) { %>
        <form action="ServiceDictionaryList.do" name="orderAdd" method="post">
          <input type="hidden" name="key" value="<%=request.getParameter("key").toString()%>">
          <input type="hidden" name="poKey" value="<%=request.getParameter("key").toString()%>">
          <input type="hidden" name="ctKey" value="1" >          
        <% if (!(request.getParameter("serviceNameFilter") == null)) { %>
          <input type="hidden" name="serviceNameFilter" value="<%=request.getParameter("serviceNameFilter").toString()%>">
        <% } %>
        <% if (!(request.getParameter("serviceNumFilter") == null)) { %>
          <input type="hidden" name="serviceNumFilter" value="<%=request.getParameter("serviceNumFilter").toString()%>">
        <% } %>
        <% if (!(request.getParameter("categoryNameFilter") == null)) { %>
          <input type="hidden" name="categoryNameFilter" value="<%=request.getParameter("categoryNameFilter").toString()%>">
        <% } %>
        <% if (!(request.getParameter("sdPageNo") == null)) { %>
          <input type="hidden" name="sdPageNo" value="<%=request.getParameter("sdPageNo").toString()%>">
        <% } %>          
          <input type="submit" value="Add Service">
        </form>
      <% } else { %>
        <form action="#" name="orderAdd" method="post">
          <input type="submit" value="Add Service" disabled >
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
  
  <!-- Iterate over the purchaseorder services. 
       Note that an additional row is added to accomodate issues 
       with the "Formatter" validation class.  
   -->     
  <logic:iterate id="purchaseOrderServiceForm"
                 name="purchaseOrderServiceHeadForm"
                 property="purchaseOrderServiceForm"
                 scope="session"
                 type="com.wolmerica.purchaseorderservice.PurchaseOrderServiceForm">
    <logic:notEqual name="purchaseOrderServiceForm" property="serviceName" value="">      
      <%
        ++i;      
        if ( i % 2 == 0) { currentColor = "ODD"; }
        else { currentColor = "EVEN"; }
        orderQtyError = orderQtyBase + i;
      %>                              
      <tr align="left" class="<%=currentColor %>" >
        <td>
          <bean:write name="purchaseOrderServiceForm" property="serviceNum" />
        </td>
        <td>          
          <bean:write name="purchaseOrderServiceForm" property="serviceName" />
        </td>  
        <td>
          <bean:write name="purchaseOrderServiceForm" property="priceTypeName" />
        </td>
        <td>   
          <html:text indexed="true" name="purchaseOrderServiceForm" property="orderQty" size="1" maxlength="5" readonly="<%=attDisableEdit%>" />
          <html:errors property="<%=orderQtyError%>" />             
        </td>
        <td align="right">         
          <bean:message key="app.localeCurrency" />
          <bean:write name="purchaseOrderServiceForm" property="laborCost" />      
        </td>
        <td align="right"> 
          <bean:message key="app.localeCurrency" />
          <bean:write name="purchaseOrderServiceForm" property="extendCost" />
        </td>
        <td align="right">
          <% if (!(attDisableEdit)) { %>                
            <a href="PurchaseOrderServiceDelete.do?posKey=<bean:write name="purchaseOrderServiceForm" property="key" />&key=<%=request.getParameter("key")%><%=serviceFilterURL%> " 
              onclick="return confirm('<bean:message key="app.purchaseorderservice.delete.message" />')" >Delete</a>
          <% } %>                
        </td>
      </tr>
      <tr class="<%=currentColor %>" >
        <td colspan="2"></td>
        <td colspan="6">     
          <strong><bean:message key="app.purchaseorderservice.noteLine1" />:</strong>
          &nbsp;&nbsp;
          <html:text indexed="true" name="purchaseOrderServiceForm" property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableCommentEdit%>" />            
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
        <bean:message key="app.purchaseorderservice.serviceTotal" />
      </strong>
    </td>       
    <td colspan="3">&nbsp;</td>
    <td align="right">       
      <strong>
        <bean:message key="app.localeCurrency" />
        <bean:write name="purchaseOrderServiceHeadForm" property="serviceTotal" />      
      </strong>
    </td>
    <td align="right">
      <% if (!(request.getParameter("serviceNameFilter") == null)) { %>
        <input type="hidden" name="serviceNameFilter" value="<%=request.getParameter("serviceNameFilter").toString()%>">
      <% } %>
      <% if (!(request.getParameter("serviceNumFilter") == null)) { %>
        <input type="hidden" name="serviceNumFilter" value="<%=request.getParameter("serviceNumFilter").toString()%>">
      <% } %>
      <% if (!(request.getParameter("categoryNameFilter") == null)) { %>
        <input type="hidden" name="categoryNameFilter" value="<%=request.getParameter("categoryNameFilter").toString()%>">
      <% } %>        
      <% if (!(request.getParameter("sdPageNo") == null)) { %>
        <input type="hidden" name="sdPageNo" value="<%=request.getParameter("sdPageNo").toString()%>">
      <% } %>              
      <html:submit value="Save Service Detail" disabled="<%=attDisableSave%>" />
    </td>
  </tr>    
  </html:form>                   
</table>