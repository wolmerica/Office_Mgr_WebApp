<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>   

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script type="text/javascript" language="JavaScript"><!--

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
//--></script>

<%
  String currentColor = "ODD";
  int i = -1;
  String attTitle = "app.itemdictionary.addTitle";            
  String attAction = "/ItemDictionaryAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="itemDictionaryForm" property="key" value="">    
  <%
    attTitle = "app.itemdictionary.editTitle";
    attAction = "/ItemDictionaryEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="itemDictionaryForm" property="permissionStatus" value="LOCKED">
  <% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="itemDictionaryForm" property="permissionStatus" value="READONLY">
    [<bean:write name="itemDictionaryForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
    
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="ItemDictionaryList.do" name="itemDictionaryList" method="post">                                  
        <input type="hidden" name="brandNameFilter" value="<bean:write name="itemDictionaryForm" property="brandNameFilter" />" >
        <input type="hidden" name="itemNumFilter" value="<bean:write name="itemDictionaryForm" property="itemNumFilter" />" >
        <input type="hidden" name="genericNameFilter" value="<bean:write name="itemDictionaryForm" property="genericNameFilter" />" >
        <input type="hidden" name="idPageNo" value="<bean:write name="itemDictionaryForm" property="currentPage" />" >      
        <input type="submit" value="<bean:message key="app.itemdictionary.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="itemDictionaryForm" property="key" value="">
    <td colspan="3" align="right">
      <button type="button" onClick="window.location='ItemDictionaryEntry.do?key=<bean:write name="itemDictionaryForm" property="key" />' "><bean:message key="app.itemdictionary.replicateMessage" /></button>
    </td>      
    <td colspan="1" align="right">
      <button type="button" onClick="window.location='RebateList.do?idKey=<bean:write name="itemDictionaryForm" property="key" />&idBrandName=<bean:write name="itemDictionaryForm" 
        property="brandName" />&idSize=<bean:write name="itemDictionaryForm" 
        property="size" />&idSizeUnit=<bean:write name="itemDictionaryForm" 
        property="sizeUnit" />' "><bean:message key="app.itemdictionary.rebateMessage" /></button>
    </td>    
    <td colspan="2" align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.itemdictionary.id"/>&sourceKey=<bean:write name="itemDictionaryForm"
                property="key" />&sourceName=<bean:write name="itemDictionaryForm"
                property="brandName" />" >[<bean:write name="itemDictionaryForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."></a>
    </td>    
    </logic:notEqual>    
  </tr>  
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <!-- Display tabs to show and hide customer content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff; font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('custTypeTab','custTypeInfo');HideContent('priceTypeTab','priceTypeInfo');">
              <bean:message key="app.itemdictionary.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="itemDictionaryForm" property="key" value="">          
          <td align="center">
            <div id="custTypeTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('custTypeTab','custTypeInfo');HideContent('priceTypeTab','priceTypeInfo');">
              <bean:message key="app.itemdictionary.custTypeTab" />
              </a>
            </div>
          </td>
          <td align="center">
            <div id="priceTypeTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');HideContent('custTypeTab','custTypeInfo');ShowContent('priceTypeTab','priceTypeInfo');">
              <bean:message key="app.itemdictionary.priceTypeTab" />
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
    <logic:notEqual name="itemDictionaryForm" property="key" value="">           
      <html:hidden property="key" />
  </logic:notEqual>           
  <tr>
    <td><strong><bean:message key="app.itemdictionary.brandName" />:</strong></td>
    <td colspan="3">
    <html:text property="brandName" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
    <html:errors property="brandName" />            
    </td>
    <td><strong><bean:message key="app.itemdictionary.size" />:</strong>
      <html:text property="size" size="4" maxlength="6" readonly="<%=attKeyEdit%>" />
      <html:errors property="size" />             
    </td>
    <td><strong><bean:message key="app.itemdictionary.sizeUnit" />:</strong>
      <html:text property="sizeUnit" size="6" maxlength="8" readonly="<%=attDisableEdit%>" />
      <html:errors property="sizeUnit" />
    </td>
    <td align="right"><strong><bean:message key="app.itemdictionary.thresholdQty" />:</strong></td>
    <td>
      <html:text property="orderThreshold" size="4" maxlength="5" readonly="<%=attDisableEdit%>" />
      <html:errors property="orderThreshold" />            
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.itemdictionary.genericName" />:</strong></td>
    <td colspan="3">
      <html:text property="genericName" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="genericName" />
    </td>
    <td><strong><bean:message key="app.itemdictionary.dose" />:</strong>
      <html:text property="dose" size="4" maxlength="6" readonly="<%=attDisableEdit%>" /></td>
      <html:errors property="dose" />            
    <td><strong><bean:message key="app.itemdictionary.doseUnit" />:</strong>
      <html:text property="doseUnit" size="6" maxlength="8" readonly="<%=attDisableEdit%>" />
      <html:errors property="doseUnit" />            
    </td>
    <td align="right">
      <strong>    
        <logic:notEqual name="itemDictionaryForm" property="qtyOnHand" value="0.00">
          <a href="ItemOriginList.do?key=<bean:write name="itemDictionaryForm"
              property="key" />&mode=1"><bean:message key="app.itemdictionary.inventoryQty" />:</a>        
        </logic:notEqual>
        <logic:equal name="itemDictionaryForm" property="qtyOnHand" value="0.00">
          <bean:message key="app.itemdictionary.inventoryQty" />:
        </logic:equal>    
      </strong>
    </td>
    <td>
      <html:text property="qtyOnHand" size="4" maxlength="5" readonly="true" />
      <html:errors property="qtyOnHand" />
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.itemdictionary.itemName" />:</strong></td>
    <td colspan="3">
      <html:text property="itemName" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="itemName" />            
    </td>	
    <td colspan="2" align="center">
      <strong><bean:message key="app.itemdictionary.manufacturer" />:</strong>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <html:text property="manufacturer" size="12" maxlength="15" readonly="<%=attDisableEdit%>" />
      <html:errors property="manufacturer" />            
    </td>
    <td align="right">
      <strong>    
        <logic:notEqual name="itemDictionaryForm" property="orderedQty" value="0.00">
          <a href="ItemOriginList.do?key=<bean:write name="itemDictionaryForm"
              property="key" />&mode=2"><bean:message key="app.itemdictionary.orderedQty" />:</a>        
        </logic:notEqual>
        <logic:equal name="itemDictionaryForm" property="orderedQty" value="0.00">
          <bean:message key="app.itemdictionary.orderedQty" />:
        </logic:equal>    
      </strong>
    </td>
    <td>
      <html:text property="orderedQty" size="4" maxlength="5" readonly="true" />
      <html:errors property="orderedQty" />
    </td>          
  </tr>
  <tr>
    <td><strong><bean:message key="app.itemdictionary.other" />:</strong></td>
    <td colspan="3">
      <html:text property="other" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="other" />           
    </td>
    <td colspan="2" align="right">
      <strong><bean:message key="app.itemdictionary.itemNum" />:</strong>
      <html:text property="itemNum" size="15" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="itemNum" />            
    </td>
    <td align="right">
      <strong>    
        <logic:notEqual name="itemDictionaryForm" property="forecastQty" value="0.00">
          <a href="ItemOriginList.do?key=<bean:write name="itemDictionaryForm"
              property="key" />&mode=3"><bean:message key="app.itemdictionary.forecastQty" />:</a>        
        </logic:notEqual>
        <logic:equal name="itemDictionaryForm" property="forecastQty" value="0.00">
          <bean:message key="app.itemdictionary.forecastQty" />:
        </logic:equal>    
      </strong>
    </td>
    <td>
      <html:text property="forecastQty" size="4" maxlength="5" readonly="true" />
      <html:errors property="forecastQty" />
    </td>         
  </tr>
  <tr>
    <td><strong><bean:message key="app.itemdictionary.itemMemo" />:</strong></td>
    <td colspan="3">
      <html:text property="itemMemo" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="itemMemo" />                       
    </td>
    <td colspan="2" align="right">
      <strong><bean:message key="app.itemdictionary.profileNum" />:</strong>
      <html:text property="profileNum" size="15" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="profileNum" />
    </td>
    <td align="right"><strong><bean:message key="app.itemdictionary.percentUse" />:</strong></td>
    <td>
      <html:text property="percentUse" size="2" maxlength="4" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />
      <html:errors property="percentUse" />                       
    </td>     	    
  </tr>	
  <tr>
    <td><strong><bean:message key="app.itemdictionary.vendorKey" />:</strong></td>
    <td colspan="3">
      <html:hidden property="vendorKey" />
      <html:hidden property="vendorName" />
      <span class="ac_holder">
      <input id="vendorNameAC" size="35" maxlength="35" value="<bean:write name="itemDictionaryForm" property="vendorName" />" onFocus="javascript:
      var options = {
		script:'VendorLookUp.do?json=true&',
		varname:'nameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.itemDictionaryForm.vendorKey.value=obj.id; document.itemDictionaryForm.vendorName.value=obj.value;}
		};
		var json=new AutoComplete('vendorNameAC',options);return true;" value="" />
      </span>      
      <html:errors property="vendorKey" />
    </td>
    <td><strong><bean:message key="app.itemdictionary.vendorSpecificId" />:</strong></td>
    <td>
      <html:radio property="vendorSpecificId" value="0" disabled="<%=attDisableEdit%>" />No
      <html:radio property="vendorSpecificId" value="1" disabled="<%=attDisableEdit%>" />Yes
      <html:errors property="vendorSpecificId" />
    </td>
    <td colspan="2" align="center">
      <strong><bean:message key="app.itemdictionary.customerTypeKey" />:</strong>
      <html:select property="customerTypeKey" disabled="<%=attDisableEdit%>">
      <!-- iterate over the customer type -->
      <logic:iterate id="customerTypeForm"
                 name="itemDictionaryForm"
                 property="customerTypeForm"
                 scope="session"
                 type="com.wolmerica.customertype.CustomerTypeForm">
        <html:option value="<%=customerTypeForm.getKey()%>" > <%=customerTypeForm.getName()%> </html:option>
      </logic:iterate>
      </html:select>
      <html:errors property="customerTypeKey" />
    </td>
    <td colspan="2"></td>
  </tr>
  <tr>  
    <td><strong><bean:message key="app.itemdictionary.firstCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="firstCost" size="4" maxlength="8" readonly="<%=attDisableEdit%>" />
      <html:errors property="firstCost" />                                  
    </td>
    <td align="right"><strong><bean:message key="app.itemdictionary.prevFirstCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />	    
      <bean:write name="itemDictionaryForm" property="prevFirstCost" />
      <html:errors property="prevFirstCost" />                                  
    </td>
    <td align="right"><strong><bean:message key="app.itemdictionary.muVendor" />:</strong></td>
    <td>
      <html:text property="muVendor" size="2" maxlength="4" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />
      <html:errors property="muVendor" />                                  
    </td>
    <td align="right"><strong><bean:message key="app.itemdictionary.carryFactor" />:</strong></td>
    <td>	    
      <html:text property="carryFactor" size="2" maxlength="4" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />
      <html:errors property="carryFactor" />                                  
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.itemdictionary.unitCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />		    
      <html:text property="unitCost" size="4" maxlength="8" readonly="<%=attDisableEdit%>" />
      <html:errors property="unitCost" />                                  
    </td>
    <td align="right"><strong><bean:message key="app.itemdictionary.prevUnitCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />		    
      <bean:write name="itemDictionaryForm" property="prevUnitCost" />
      <html:errors property="prevUnitCost" />                                  
    </td>
    <td align="right"><strong><bean:message key="app.itemdictionary.muAdditional" />:</strong></td>
    <td>
      <html:text property="muAdditional" size="2" maxlength="4" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />	      
      <html:errors property="muAdditional" />                                  
    </td>	        
    <td align="right"><strong><bean:message key="app.itemdictionary.labelCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />	    
      <html:text property="labelCost" size="4" maxlength="6" readonly="<%=attDisableEdit%>" />
      <html:errors property="labelCost" />                                  
    </td>
  </tr>	
  <tr>
    <td><strong><bean:message key="app.itemdictionary.licenseKeyId" />:</strong></td>
    <td>
      <html:radio property="licenseKeyId" value="0" disabled="<%=attDisableEdit%>" />No
      <html:radio property="licenseKeyId" value="1" disabled="<%=attDisableEdit%>" />Yes
      <html:errors property="licenseKeyId" />
    </td>
    <td align="right"><strong><bean:message key="app.itemdictionary.reportId" />:</strong></td>
    <td>
      <html:radio property="reportId" value="0" disabled="<%=attDisableEdit%>" />No
      <html:radio property="reportId" value="1" disabled="<%=attDisableEdit%>" />Yes
      <html:errors property="reportId" />                       
    </td>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.itemdictionary.createUser" />:</strong></td>
    <td colspan="3"><bean:write name="itemDictionaryForm" property="createUser" /></td>	     
    <td><strong><bean:message key="app.itemdictionary.createStamp" />:</strong></td>
    <td colspan="2"><bean:write name="itemDictionaryForm" property="createStamp" /></td>	     
  </tr>		
  <tr>
    <td><strong><bean:message key="app.itemdictionary.updateUser" />:</strong></td>
    <td colspan="3"><bean:write name="itemDictionaryForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.itemdictionary.updateStamp" />:</strong></td>
    <td colspan="2"><bean:write name="itemDictionaryForm" property="updateStamp" /></td>	     
  </tr>	
  <tr>
    <td colspan="8" align="right">
      <input type="hidden" name="brandNameFilter" value="<bean:write name="itemDictionaryForm" property="brandNameFilter" />" >
      <input type="hidden" name="itemNumFilter" value="<bean:write name="itemDictionaryForm" property="itemNumFilter" />" >
      <input type="hidden" name="genericNameFilter" value="<bean:write name="itemDictionaryForm" property="genericNameFilter" />" >
      <input type="hidden" name="idPageNo" value="<bean:write name="itemDictionaryForm" property="currentPage" />" >         
      <html:submit value="Save Item Specific Values" disabled="<%=attDisableEdit%>" />         
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

<logic:notEqual name="itemDictionaryForm" property="key" value="">    
<div id="custTypeInfo" style="display:none;">
<!--=========================================================-->
<!-- Customer Attribute By Item for editing only             -->
<!--=========================================================-->
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td colspan="3">
        <strong><bean:message key="app.itemdictionary.brandName" />:</strong>
        &nbsp;&nbsp;
        <bean:write name="itemDictionaryForm" property="brandName" />
      </td>
    </tr>
    <tr>
      <td colspan="7">
        <hr>
      </td>
    </tr>    
    <tr align="left">
      <th><bean:message key="app.customerattributebyitem.customerTypeName" /></th>
      <th><bean:message key="app.customerattributebyitem.markUpRate" /></th>                    
      <th><bean:message key="app.customerattributebyitem.additionalMarkUpRate" /></th>
      <th><bean:message key="app.customerattributebyitem.labelCost" /></th>
      <th><bean:message key="app.customerattributebyitem.adminCost" /></th>
      <th><bean:message key="app.customerattributebyitem.discountThreshold" /></th>      
      <th><bean:message key="app.customerattributebyitem.discountRate" /></th>
    </tr>
    <tr>
      <td colspan="7">
        <hr>
      </td>
    </tr>
  <!-- Set the Base and Error variables for customer attribute -->
    <% i = -1; 
      String labelCostBase = "labelCost";
      String adminCostBase="adminCost";
      String markUpRateBase="markUpRate";
      String additionalMarkUpRateBase="additionalMarkUpRate";
      String discountThresholdBase="discountThreshold"; 
      String discountRateBase="discountRate"; 
      String labelCostError;
      String adminCostError;
      String markUpRateError;
      String additionalMarkUpRateError;
      String discountThresholdError; 
      String discountRateError;       
    %>
    <html:form action="/CustomerAttributeByItemEdit.do"> 
    <input type="hidden" name="key" value="<bean:write name="itemDictionaryForm" property="key" />" >     
  <!-- iterate over the customer attribute by item -->     
    <logic:iterate id="customerAttributeByItemForm"
                   name="customerAttributeByItemHeadForm"
                   property="customerAttributeByItemForm"
                   scope="session"
                   type="com.wolmerica.customerattributebyitem.CustomerAttributeByItemForm">
    <%
      ++i;      
      if ( i % 2 == 0) { currentColor = "ODD"; }
      else { currentColor = "EVEN"; }
      labelCostError = labelCostBase + i;
      adminCostError = adminCostBase + i;
      markUpRateError = markUpRateBase + i;
      additionalMarkUpRateError = additionalMarkUpRateBase + i;
      discountThresholdError = discountThresholdBase + i;
      discountRateError = discountRateBase + i;
    %>   
    <tr align="left" class="<%=currentColor %>" >
      <td>    
        <strong>
          <bean:write name="customerAttributeByItemForm" property="customerTypeName" />
        </strong>
      </td>  
      <td>
        <html:text indexed="true" name="customerAttributeByItemForm" property="markUpRate" size="2" readonly="<%=attDisableEdit%>" />    
        <bean:message key="app.localePercent" />
        <html:errors property="<%=markUpRateError%>" /> 
      </td>
      <td>
        <html:text indexed="true" name="customerAttributeByItemForm" property="additionalMarkUpRate" size="2" readonly="<%=attDisableEdit%>" />    
        <bean:message key="app.localePercent" />
        <html:errors property="<%=additionalMarkUpRateError%>" /> 
      </td>
      <td>
        <bean:message key="app.localeCurrency" />    
        <html:text indexed="true" name="customerAttributeByItemForm" property="labelCost" size="2" readonly="<%=attDisableEdit%>" />
        <html:errors property="<%=labelCostError%>" />         
      </td>
      <td>
        <bean:message key="app.localeCurrency" />    
        <html:text indexed="true" name="customerAttributeByItemForm" property="adminCost" size="2" readonly="<%=attDisableEdit%>" />
        <html:errors property="<%=adminCostError%>" /> 
      </td>      
      <td>
        <html:text indexed="true" name="customerAttributeByItemForm" property="discountThreshold" size="2" readonly="<%=attDisableEdit%>" />
        <html:errors property="<%=discountThresholdError%>" /> 
      </td>
      <td>
        <html:text indexed="true" name="customerAttributeByItemForm" property="discountRate" size="2" readonly="<%=attDisableEdit%>" />
        <bean:message key="app.localePercent" />
        <html:errors property="<%=discountRateError%>" /> 
      </td>
    </tr>   
    </logic:iterate>  
    <tr>
      <td colspan="8" align="right">
        <input type="hidden" name="brandNameFilter" value="<bean:write name="itemDictionaryForm" property="brandNameFilter" />" >
        <input type="hidden" name="itemNumFilter" value="<bean:write name="itemDictionaryForm" property="itemNumFilter" />" >
        <input type="hidden" name="genericNameFilter" value="<bean:write name="itemDictionaryForm" property="genericNameFilter" />" >
        <input type="hidden" name="idPageNo" value="<bean:write name="itemDictionaryForm" property="currentPage" />" >      
        <input type="hidden" name="tabId" value="custTypeTab" >        
        <html:submit value="Save Customer Specific Values" disabled="<%=attDisableEdit%>" />         
      </td>
    </tr>         
    </html:form>    
  </table>
</div>

<% if (request.getParameter("tabId") != null) { %>
  <% if (request.getParameter("tabId").toString().compareToIgnoreCase("custTypeTab") == 0) { %>
    <script type="text/javascript" language="JavaScript"><!--
      ShowContent('custTypeTab', 'custTypeInfo');
    //--></script>
  <% } %>
<% } %>

<!--=========================================================-->
<!-- Price Attribute By Item                                 -->
<!--=========================================================-->
<div id="priceTypeInfo" style="display:none;">
    <% i = -1; 
      String sizeBase = "size";
      String sizeError;
    %>   
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td colspan="3">
        <strong><bean:message key="app.itemdictionary.brandName" />:</strong>
        &nbsp;&nbsp;
        <bean:write name="itemDictionaryForm" property="brandName" />
      </td>
    </tr>
    <tr>
      <td colspan="20">
        <hr>
      </td>
    </tr>  
    <tr align="left">
      <th align="left"><bean:message key="app.customerattributebyitem.customerTypeName" /></th>

      <html:form action="/PriceAttributeByItemEdit.do">  
      <input type="hidden" name="key" value="<bean:write name="itemDictionaryForm" property="key" />" >    
      <!-- iterate over the price attribute by item -->     
      <logic:iterate id="priceAttributeByItemForm"
                     name="priceAttributeByItemHeadForm"
                     property="priceAttributeByItemForm"
                     scope="session"
                     type="com.wolmerica.priceattributebyitem.PriceAttributeByItemForm">
      <%
        ++i;      
        sizeError = sizeBase + i;
      %>                       
        <td>
          <strong>
            <bean:write name="priceAttributeByItemForm" property="priceTypeName" />
            <br><bean:message key="app.pricebyitem.computedPrice" />
          </strong>
        </td>
        <td>
          <logic:equal name="priceAttributeByItemForm" property="fullSize" value="true">
            <strong>          
              <bean:message key="app.priceattributebyitem.size" />:
              <bean:write name="priceAttributeByItemForm" property="size" />
            </strong>
          </logic:equal>         
          <logic:equal name="priceAttributeByItemForm" property="fullSize" value="false">
            <strong>              
              <bean:message key="app.priceattributebyitem.size" />:
              <html:text name="priceAttributeByItemForm" indexed="true" property="size" size="2" readonly="<%=attDisableEdit%>" />      
            </strong>
              <html:errors property="<%=sizeError%>" />             
          </logic:equal>         
          <strong>
            <br><bean:message key="app.pricebyitem.overRidePrice" />
          </strong>
        </td>
      </logic:iterate> 
    </tr>
    <tr>
      <td colspan="7">
        <hr>
      </td>
      <td colspan="2" align="right">
        <input type="hidden" name="brandNameFilter" value="<bean:write name="itemDictionaryForm" property="brandNameFilter" />" >
        <input type="hidden" name="itemNumFilter" value="<bean:write name="itemDictionaryForm" property="itemNumFilter" />" >
        <input type="hidden" name="genericNameFilter" value="<bean:write name="itemDictionaryForm" property="genericNameFilter" />" >
        <input type="hidden" name="idPageNo" value="<bean:write name="itemDictionaryForm" property="currentPage" />" >
        <input type="hidden" name="tabId" value="priceTypeTab" >        
        <html:submit value="Save Size Values" disabled="<%=attDisableEdit%>" />  
      </td>
      </html:form> 
    </tr>
<!--=========================================================-->
<!-- Price By Item                                           -->
<!--=========================================================--> 
    <% i = -1; 
      String computedPriceBase = "computedPrice";
      String overRidePriceBase = "overRidePrice";
      String computedPriceError;
      String overRidePriceError;
      String customerTypeKey = "";    
    %>   
    <tr>
      <html:form action="/PriceByItemEdit.do">  
      <input type="hidden" name="key" value="<bean:write name="itemDictionaryForm" property="key" />" >    

      <!-- iterate over the customer attribute by item -->     
      <logic:iterate id="customerAttributeByItemForm"
                     name="customerAttributeByItemHeadForm"
                     property="customerAttributeByItemForm"
                     scope="session"
                     type="com.wolmerica.customerattributebyitem.CustomerAttributeByItemForm">
      <%
        ++i;      
        if ( i % 2 == 0) { currentColor = "ODD"; }
        else { currentColor = "EVEN"; }
        computedPriceError = computedPriceBase + i;        
        overRidePriceError = overRidePriceBase + i;
        customerTypeKey = customerAttributeByItemForm.getCustomerTypeKey();
      %>   
    <tr align="left" class="<%=currentColor %>">
      <td>
        <strong>
          <bean:write name="customerAttributeByItemForm" property="customerTypeName" />
        </strong>
      </td>   
      <logic:iterate id="priceByItemForm"
                     name="priceByItemHeadForm"
                     property="priceByItemForm"
                     scope="session"
                     type="com.wolmerica.pricebyitem.PriceByItemForm">
        <logic:equal name="priceByItemForm" property="customerTypeKey" value="<%=customerTypeKey%>">   
          <td>
            <bean:message key="app.localeCurrency" />
            <bean:write name="priceByItemForm" property="computedPrice" />
          </td>
          <td>
            <bean:message key="app.localeCurrency" />
<!--
            <bean:write name="priceByItemForm" property="overRidePrice" />
-->            
            <html:text name="priceByItemForm" indexed="true" property="overRidePrice" size="4" readonly="<%=attDisableEdit%>" />      
            <html:errors property="<%=overRidePriceError%>" />             
          </td> 
        </logic:equal>
      </logic:iterate> 
    </tr>        
    </logic:iterate>    
    <%
      if (i == -1) {
    %>
    <tr>
      <td>
        <bean:message key="app.pricebyitem.notFound" />&nbsp;
        <bean:write name="priceattributebyitem" property="itemDictionaryKey" />
      </td>                 
    </tr>
    <% } else { %>
    <tr>
      <td colspan="20" align="right">
        <input type="hidden" name="brandNameFilter" value="<bean:write name="itemDictionaryForm" property="brandNameFilter" />" >
        <input type="hidden" name="itemNumFilter" value="<bean:write name="itemDictionaryForm" property="itemNumFilter" />" >
        <input type="hidden" name="genericNameFilter" value="<bean:write name="itemDictionaryForm" property="genericNameFilter" />" >
        <input type="hidden" name="idPageNo" value="<bean:write name="itemDictionaryForm" property="currentPage" />" >
        <input type="hidden" name="tabId" value="priceTypeTab" >
        <html:submit value="Save Over-Ride Prices" disabled="<%=attDisableEdit%>" />  
      </td>
    </tr>
    <% } %>
    </html:form>    
  </table>
</div>  

<% if (request.getParameter("tabId") != null) { %>
  <% if (request.getParameter("tabId").toString().compareToIgnoreCase("priceTypeTab") == 0) { %>
    <script type="text/javascript" language="JavaScript"><!--
      ShowContent('priceTypeTab', 'priceTypeInfo');
    //--></script>
  <% } %>
<% } %>

</logic:notEqual>

