<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

    
<%
  String attTitle = "app.bundle.addTitle";            
  String attAction = "/BundleAdd";
  String attReplicateAction = "";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="bundleForm" property="key" value="">
  <%
    attTitle = "app.bundle.editTitle";
    attReplicateAction = attAction;    
    attAction = "/BundleEdit";
    
    attKeyEdit = true;
  %>
          
</logic:notEqual>

<logic:notEqual name="bundleForm" property="permissionStatus" value="LOCKED">
  <% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="bundleForm" property="permissionStatus" value="READONLY">
    [<bean:write name="bundleForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="BundleList.do" name="bundleList" method="post">
        <% if (request.getParameter("bundleNameFilter") != null) { %>
          <input type="hidden" name="bundleNameFilter" value="<%=request.getParameter("bundleNameFilter")%>">
        <% } %>
        <% if (request.getParameter("bundleCategoryFilter") != null) { %>
          <input type="hidden" name="bundleCategoryFilter" value="<%=request.getParameter("bundleCategoryFilter")%>">
        <% } %>        
        <% if (request.getParameter("pageNo") != null) { %>
          <input type="hidden" name="pageNo" value="<%=request.getParameter("pageNo")%>">
        <% } %>
        <input type="submit" value="<bean:message key="app.bundle.backMessage" />">
      </form> 
    </td>  
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
<logic:notEqual name="bundleForm" property="key" value="">
  <html:hidden property="key" />  
</logic:notEqual>
  <% if (request.getParameter("bundleNameFilter") != null) { %>
    <input type="hidden" name="bundleNameFilter" value="<%=request.getParameter("bundleNameFilter")%>">
  <% } %>
  <% if (request.getParameter("bundleCategoryFilter") != null) { %>
    <input type="hidden" name="bundleCategoryFilter" value="<%=request.getParameter("bundleCategoryFilter")%>">
  <% } %>   
  <% if (request.getParameter("pageNo") != null) { %>
    <input type="hidden" name="pageNo" value="<%=request.getParameter("pageNo")%>">
  <% } %>
  <tr>
    <td><strong><bean:message key="app.bundle.name" />:</strong></td>
    <td>     
      <html:text property="name" size="30" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="name" />
    </td>
    <td><strong><bean:message key="app.bundle.category" />:</strong></td>
    <td>     
      <html:text property="category" size="30" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="category" />
    </td>                    
  </tr>
  <tr>
    <td><strong><bean:message key="app.bundle.durationTime" /></strong></td>
    <td>
      <html:select property="durationHour" disabled="<%=attDisableEdit%>">
        <html:option value="0" >0 hours</html:option>
        <html:option value="1">1 hour</html:option>
        <html:option value="2">2 hours</html:option>
        <html:option value="3">3 hours</html:option>
        <html:option value="4">4 hours</html:option>
        <html:option value="5">5 hours</html:option>
        <html:option value="6">6 hours</html:option>
        <html:option value="7">7 hours</html:option>
        <html:option value="8">8 hours</html:option>
        <html:option value="9">9 hours</html:option>
        <html:option value="10">10 hours</html:option>
        <html:option value="11">11 hours</html:option>
        <html:option value="12">12 hours</html:option>
      </html:select>
      <html:errors property="durationHour" />    
      &nbsp;&nbsp;
      <html:select property="durationMinute" disabled="<%=attDisableEdit%>">
        <html:option value="0">0 minutes</html:option>
        <html:option value="15">15 minutes</html:option>
        <html:option value="30">30 minutes</html:option>
        <html:option value="45">45 minutes</html:option>
      </html:select>    
      <html:errors property="durationMinute" />
    </td> 
    <td><strong><bean:message key="app.bundle.customerTypeKey" />:</strong></td>
    <td>
      <html:select property="customerTypeKey" disabled="<%=attDisableEdit%>">
      <!-- iterate over the customer type -->       
      <logic:iterate id="customerTypeForm"
                 name="bundleForm"
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
    <td><strong><bean:message key="app.bundle.noteLine1" />:</strong></td>
    <td colspan="4">   
      <html:text property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine1" />
    </td>   
  </tr>
  <tr>
    <td><strong><bean:message key="app.bundle.createUser" />:</strong></td>
    <td><bean:write name="bundleForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.bundle.createStamp" />:</strong></td>
    <td><bean:write name="bundleForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.bundle.updateUser" />:</strong></td>
    <td><bean:write name="bundleForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.bundle.updateStamp" />:</strong></td>
    <td><bean:write name="bundleForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="8" align="right">
      <html:submit value="Save Bundle Values" disabled="<%=attDisableEdit%>" />         
     </html:form> 
    </td>
  </tr>
</table>

<logic:notEqual name="bundleForm" property="key" value="">


<%
  String currentColor = "ODD";
  int i = -1;     
  
  boolean attDisableAddItems = attDisableEdit;
  boolean attDisableEditQty = attDisableEdit;
  boolean attDisableAddServices = attDisableEdit;  
  
  String orderQtyBase = "orderQty";
  String orderQtyError; 
%>
<br>
<strong>
  <bean:message key="app.bundledetail.heading" />
</strong>
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>     
  <tr align="left">
    <th><bean:message key="app.bundledetail.type" /></th>      
    <th><bean:message key="app.bundledetail.category" /></th>
    <th><bean:message key="app.bundledetail.name" /></th>
    <th>
      <bean:message key="app.bundledetail.size" />/
      <bean:message key="app.bundledetail.sizeUnit" />
    </th>
    <th align="center"><bean:message key="app.bundledetail.releaseId" /></th>
    <th><bean:message key="app.bundledetail.orderQty" /></th>
    <td> 
      <% if (!(attDisableAddServices)) { %>   
        <button type="button" onClick="window.location='ServiceDictionaryList.do?ctKey=<bean:write name="bundleForm" 
           property="customerTypeKey" />&buKey=<bean:write name="bundleForm" 
           property="key" />&key=<bean:write name="bundleForm" 
           property="key" />' "><bean:message key="app.servicedictionary.addTitle" /></button>
      <% } else { %> 
        <button type="button" disabled><bean:message key="app.servicedictionary.addTitle" /></button>      
      <% } %>         
    </td>
    <td align="right">
      <% if (!(attDisableAddServices)) { %>   
        <button type="button" onClick="window.location='ItemDictionaryList.do?ctKey=<bean:write name="bundleForm" 
           property="customerTypeKey" />&buKey=<bean:write name="bundleForm" 
           property="key" />&key=<bean:write name="bundleForm" 
           property="key" />&idKey=<bean:write name="bundleForm" 
           property="key" />' "><bean:message key="app.itemdictionary.addTitle" /></button>
      <% } else { %> 
        <button type="button" disabled><bean:message key="app.itemdictionary.addTitle" /></button>      
      <% } %>        
    </td>
  </tr>
    <tr>
      <td colspan="10">
        <hr>
      </td>
    <tr>
    <html:form action="/BundleDetailEdit"> 
      <input type="hidden" name="key" value="<bean:write name="bundleForm" property="key" />" >       
  <logic:notEqual name="bundleDetailListHeadForm" property="lastRecord" value="0"> 
    <logic:iterate id="bundleDetailForm"
                   name="bundleDetailListHeadForm"
                   property="bundleDetailForm"
                   scope="session"
                   type="com.wolmerica.bundledetail.BundleDetailForm">
      <%
        ++i;      
        if ( i % 2 == 0) { currentColor = "ODD"; } 
        else { currentColor = "EVEN"; }    
        orderQtyError = orderQtyBase + i; 
      %>
       
      <tr align="left" class="<%=currentColor %>" >
        <td>
          <logic:equal name="bundleDetailForm" property="sourceTypeKey" value="3">
            <bean:message key="app.bundledetail.item" />              
          </logic:equal>
          <logic:equal name="bundleDetailForm" property="sourceTypeKey" value="6">
            <bean:message key="app.bundledetail.service" />
          </logic:equal>          
        </td>    
        <td>          
          <bean:write name="bundleDetailForm" property="categoryName" />
        </td>        
        <td>          
          <bean:write name="bundleDetailForm" property="sourceName" />
        </td>       
        <td>
          <bean:write name="bundleDetailForm" property="size" />
          <bean:write name="bundleDetailForm" property="sizeUnit" />
        </td>
        <td align="center">
          <html:checkbox name="bundleDetailForm" property="releaseId" disabled="true" />
        </td>        
        <td>   
          <html:text indexed="true" name="bundleDetailForm" property="orderQty" size="2" maxlength="3" readonly="<%=attDisableEditQty%>" />
          <html:errors property="<%=orderQtyError%>" />
        </td>
        <td align="right">        
          <bean:message key="app.localeCurrency" />  
          <bean:write name="bundleDetailForm" property="computedPrice" />
        </td>
        <td align="right">
        <% if (!(attDisableAddItems)) { %>                 
          <a href="BundleDetailDelete.do?key=<bean:write name="bundleDetailForm" 
            property="bundleKey" />&budKey=<bean:write name="bundleDetailForm" property="key" /> " onclick="return confirm('<bean:message key="app.bundledetail.delete.message" />')" ><bean:message key="app.delete" /></a>
        <% } %>           
        </td>
      </tr>     
    </logic:iterate>    
    <tr>  
      <td align="right" colspan="10">
        <html:submit value="Save Bundle Detail" disabled="<%=attDisableEdit%>" />
      </td>
    </tr>        
  </logic:notEqual>
  </html:form> 
</table>
</logic:notEqual>