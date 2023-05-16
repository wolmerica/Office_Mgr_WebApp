<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
  
<%
  String attAction = "";
  String attObject = "";
  String currentColor = "ODD";
  int i = 0;
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
%>   
 
<logic:equal name="itemOriginListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="itemOriginListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="ItemDictionaryGet.do" name="itemGet" method="post">                                  
        <input type="hidden" name="key" value="<bean:write name="itemOriginListHeadForm" property="itemDictionaryKey" />">
        <input type="submit" value="<bean:message key="app.itemorigin.backMessage" />">
      </form>            
    </td>
  </tr>
  <tr>
    <td>
      <strong><bean:message key="app.itemorigin.mode" />:</strong>
      <logic:equal name="itemOriginListHeadForm" property="originMode" value="1">
        <% attAction = "VendorInvoiceGet.do";
           attObject = "Vendor Invoice"; %>
        <bean:message key="app.itemorigin.inventory" />   
      </logic:equal>
      <logic:equal name="itemOriginListHeadForm" property="originMode" value="2">
        <% attAction = "PurchaseOrderGet.do";
           attObject = "Purchase Order"; %>
        <bean:message key="app.itemorigin.ordered" />   
      </logic:equal>
      <logic:equal name="itemOriginListHeadForm" property="originMode" value="3">
        <% attAction = "ScheduleGet.do"; 
           attObject = "Event Subject"; %>
        <bean:message key="app.itemorigin.forecast" />   
      </logic:equal>      
    </td>          
    <td colspan=2>
      <strong><bean:message key="app.itemorigin.brandName" />:</strong>
      <bean:write name="itemOriginListHeadForm" property="brandName" />      
    </td>
    <td>
      <strong>
        <bean:message key="app.itemorigin.size" />/
        <bean:message key="app.itemorigin.sizeUnit" />
      </strong>    
      <bean:write name="itemOriginListHeadForm" property="size" />
      <bean:write name="itemOriginListHeadForm" property="sizeUnit" />
    </td>           
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.itemorigin.transactionDate" /></th>
    <th><%=attObject%></th>
    <th><bean:message key="app.itemorigin.vendorName" /></th>    
    <th><bean:message key="app.itemorigin.clientName" /></th>
    <th><bean:message key="app.itemorigin.expirationDate" /></th>    
    <th align="right"><bean:message key="app.itemorigin.quantity" /></th>    
  </tr>    
  <tr>
    <td colspan="8">
      <hr>
    </td>
  <tr>  
<logic:notEqual name="itemOriginListHeadForm" property="lastRecord" value="0"> 
  <logic:iterate id="itemOriginForm"
                 name="itemOriginListHeadForm"
                 property="itemOriginForm"
                 scope="session"
                 type="com.wolmerica.itemorigin.ItemOriginForm">
        
<!-- Set values for row shading -->                  
        <%
          if ( i % 2 == 0)
            currentColor = "ODD";        
          else 
            currentColor = "EVEN";          
          i++;
        %>                
	<tr align="left" class="<%=currentColor %>">
	  <td>
            <bean:write name="itemOriginForm" property="transactionDate" />
	  </td>
	  <td>
            <a href="<%=attAction%>?key=<bean:write name="itemOriginForm" 
              property="transactionKey" />"><bean:write name="itemOriginForm" property="transactionName" /></a>                                
	  </td>
	  <td>
            <bean:write name="itemOriginForm" property="vendorName" />
	  </td>
	  <td>
            <bean:write name="itemOriginForm" property="clientName" />
	  </td>
	  <td>
            <bean:write name="itemOriginForm" property="expirationDate" />
	  </td>
	  <td align="right">
            <bean:write name="itemOriginForm" property="quantity" />
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
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='ItemOriginList.do?key=<bean:write name="itemOriginListHeadForm" 
        property="itemDictionaryKey" />&mode=<bean:write name="itemOriginListHeadForm" 
        property="originMode" />&pageNo=<bean:write name="itemOriginListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>
    </td>
    <td colspan="3" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="itemOriginListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="itemOriginListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="itemOriginListHeadForm" property="recordCount" />      
    </td>
    <td colspan="2" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='ItemOriginList.do?key=<bean:write name="itemOriginListHeadForm" 
        property="itemDictionaryKey" />&mode=<bean:write name="itemOriginListHeadForm" 
        property="originMode" />&pageNo=<bean:write name="itemOriginListHeadForm"
        property="nextPage" />' "><bean:message key="app.next" /></button>
    </td>  
  </tr>
</table>