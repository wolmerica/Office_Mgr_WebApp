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
%>   
 
<logic:equal name="priceSheetListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="priceSheetListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
<!-- Post Daily Price Sheet Page -->
    <logic:equal name="priceSheetListHeadForm" property="mode" value="1">
      <td colspan = "6">          
        <form action="PriceSheetSet.do" name="priceSheetSet" method="post">        
          <input type="hidden" name="mode" value="<bean:write name="priceSheetListHeadForm" property="mode" />">                                
          <input type="submit" value="Set Price Sheet" onclick="return confirm('<bean:message key="app.pricesheet.set.message" />')" >
        </form> 
    </logic:equal>  
    </td>
  </tr>
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <th><bean:message key="app.pricesheet.clientName" /></th>
    <th align="right"><bean:message key="app.pricesheet.invoiceNumber" /></th>
    <th align="right"><bean:message key="app.pricesheet.invoiceTotal" /></th>
    <th align="right"><bean:message key="app.pricesheet.priceSheetKey" /></th>        
    <th>&nbsp;</th>
    <th>&nbsp;</th>
  </tr>     
  <tr>
    <td colspan="7">
      <hr>
    </td>
  </tr>
    
<logic:notEqual name="priceSheetListHeadForm" property="recordCount" value="0"> 
  <!-- iterate over the employees -->       
  <logic:iterate id="priceSheetForm"
                 name="priceSheetListHeadForm"
                 property="priceSheetForm"
                 scope="session"
                 type="com.wolmerica.pricesheet.PriceSheetForm">
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
            <bean:write name="priceSheetForm" property="clientName" />
	  </td>
	  <td align="right">
              <bean:write name="priceSheetForm" property="invoiceNumber" />
	  </td>
	  <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="priceSheetForm" property="invoiceTotal" />
	  </td>
          <logic:equal name="priceSheetListHeadForm" property="mode" value="2">                    
	  <td align="right">
             <bean:write name="priceSheetForm" property="priceSheetKey" />
	  </td>   
          <td align="center">
            &nbsp;
            <a href="#" onclick="popup = putinpopup('PdfPriceSheet.do?presentationType=pdf&key=<bean:write name="priceSheetForm"
              property="priceSheetKey" />'); return false" target="_PDF"><bean:message key="app.pdf" /></a>
            &nbsp;
          </td>
          <td align="center">
            &nbsp;
            <a href="#" onclick="popup = putinpopup('PdfBarCodeSheet.do?presentationType=pdf&key=<bean:write name="priceSheetForm"
              property="priceSheetKey" />'); return false" target="_PDF"><bean:message key="app.barcode" /></a>
            &nbsp;
          </td>            
          </logic:equal>  
	</tr>	
  </logic:iterate> 
</logic:notEqual>
 
  <tr>
    <td colspan="7">
      <hr>
    </td>      
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='PriceSheetList.do?pageNo=<bean:write name="priceSheetListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>        
    </td>
    <td>
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="priceSheetListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="priceSheetListHeadForm" property="recordCount" />      
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />
      <bean:write name="priceSheetListHeadForm" property="grandTotal" />
    </td>
    <td colspan="3" align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='PriceSheetList.do?pageNo=<bean:write name="priceSheetListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>        
    </td>  
  </tr>
</table>
