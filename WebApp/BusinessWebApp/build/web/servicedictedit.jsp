<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>
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
  String attTitle = "app.servicedictionary.addTitle";            
  String attAction = "/ServiceDictionaryAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="serviceDictionaryForm" property="key" value="">
  <%
    attTitle = "app.servicedictionary.editTitle";
    attAction = "/ServiceDictionaryEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="serviceDictionaryForm" property="permissionStatus" value="LOCKED">
<% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="serviceDictionaryForm" property="permissionStatus" value="READONLY">
    [<bean:write name="serviceDictionaryForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="ServiceDictionaryList.do" name="serviceDictionaryList" method="post"> 
        <input type="hidden" name="serviceNameFilter" value="<bean:write name="serviceDictionaryForm" property="serviceNameFilter" />" >
        <input type="hidden" name="serviceNumFilter" value="<bean:write name="serviceDictionaryForm" property="serviceNumFilter" />" >
        <input type="hidden" name="categoryNameFilter" value="<bean:write name="serviceDictionaryForm" property="categoryNameFilter" />" >
        <input type="hidden" name="sdPageNo" value="<bean:write name="serviceDictionaryForm" property="currentPage" />" >      
        <input type="submit" value="<bean:message key="app.servicedictionary.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="serviceDictionaryForm" property="key" value="">
      <td colspan="4" align="right">
        <form action="ServiceDictionaryEntry.do" name="serviceDictionaryEntry" method="post" >
          <input type="hidden" name="key" value="<bean:write name="serviceDictionaryForm" property="key" />" >
          <input type="submit" value="<bean:message key="app.servicedictionary.replicateMessage" />" >
        </form>       
      </td>    
      <td colspan="2" align="right">
        <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.servicedictionary.id"/>&sourceKey=<bean:write name="serviceDictionaryForm"
                property="key" />&sourceName=<bean:write name="serviceDictionaryForm"
                property="serviceName" />" >[<bean:write name="serviceDictionaryForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."></a>
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
              <bean:message key="app.servicedictionary.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="serviceDictionaryForm" property="key" value="">          
          <td align="center">
            <div id="custTypeTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('custTypeTab','custTypeInfo');HideContent('priceTypeTab','priceTypeInfo');">
              <bean:message key="app.servicedictionary.custTypeTab" />
              </a>
            </div>
          </td>
          <td align="center">
            <div id="priceTypeTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');HideContent('custTypeTab','custTypeInfo');ShowContent('priceTypeTab','priceTypeInfo');">
              <bean:message key="app.servicedictionary.priceTypeTab" />
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
  <logic:notEqual name="serviceDictionaryForm" property="key" value="">
    <html:hidden property="key" />  
  </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.servicedictionary.customerTypeKey" />:</strong></td>
    <td>
      <html:select property="customerTypeKey" disabled="<%=attDisableEdit%>">
      <!-- iterate over the customer type -->       
      <logic:iterate id="customerTypeForm"
                 name="serviceDictionaryForm"
                 property="customerTypeForm"
                 scope="session"
                 type="com.wolmerica.customertype.CustomerTypeForm">         
        <html:option value="<%=customerTypeForm.getKey()%>" > <%=customerTypeForm.getName()%> </html:option>
      </logic:iterate>
      </html:select>
      <html:errors property="customerTypeKey" />                   
    </td>   
    <td><strong><bean:message key="app.servicedictionary.serviceNum" />:</strong></td>
    <td>     
      <html:text property="serviceNum" size="20" maxlength="20" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="serviceNum" />
    </td>                    
  </tr>      
  <tr>
    <td><strong><bean:message key="app.servicedictionary.serviceName" />:</strong></td>
    <td>     
      <html:text property="serviceName" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="serviceName" />
    </td>
    <td><strong><bean:message key="app.servicedictionary.profileNum" />:</strong></td>
    <td>
      <html:text property="profileNum" size="20" maxlength="20" readonly="<%=attDisableEdit%>" />
      <html:errors property="profileNum" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.servicedictionary.serviceCategory" />:</strong></td>
    <td>     
      <html:text property="serviceCategory" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="serviceCategory" />
    </td>
    <td><strong><bean:message key="app.servicedictionary.other" />:</strong></td>
    <td>
      <html:text property="other" size="45" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="other" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.servicedictionary.serviceDescription" />:</strong></td>
    <td colspan="4">
      <html:textarea property="serviceDescription" cols="70" rows="4" readonly="<%=attDisableEdit%>" />        
      <html:errors property="serviceDescription" />
    </td>  
  </tr> 
  <tr>
    <td><strong><bean:message key="app.servicedictionary.vendorKey" />:</strong></td>
    <td>
      <html:hidden property="vendorKey" />
      <html:hidden property="vendorName" />
      <span class="ac_holder">
      <input id="vendorNameAC" size="40" maxlength="40" value="<bean:write name="serviceDictionaryForm" property="vendorName" />" onFocus="javascript:
      var options = {
		script:'VendorLookUp.do?json=true&',
		varname:'nameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.serviceDictionaryForm.vendorKey.value=obj.id; document.serviceDictionaryForm.vendorName.value=obj.value; }
		};
		var json=new AutoComplete('vendorNameAC',options);return true;" value="" />
      </span>      
      <html:errors property="vendorKey" />
    </td>  
    <td><strong><bean:message key="app.servicedictionary.vendorSpecificId" />:</strong></td>
    <td>
      <html:radio property="vendorSpecificId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="vendorSpecificId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="vendorSpecificId" />
    </td>
  </tr>  
  <tr>
    <td>
      <strong><bean:message key="app.servicedictionary.duration" />
      </strong>
    </td>
    <td>
      <html:select property="durationHours" disabled="<%=attDisableEdit%>">
        <html:option value="0" > 0 hours</html:option>
        <html:option value="1"> 1 hour</html:option>
        <html:option value="2"> 2 hours</html:option>
        <html:option value="3"> 3 hours</html:option>
        <html:option value="4"> 4 hours</html:option>
        <html:option value="5"> 5 hours</html:option>
        <html:option value="6"> 6 hours</html:option>
        <html:option value="7"> 7 hours</html:option>
        <html:option value="8"> 8 hours</html:option>        
      </html:select>
      <html:errors property="durationHours" />    
      &nbsp;&nbsp;
      <html:select property="durationMinutes" disabled="<%=attDisableEdit%>">
        <html:option value="0">0 minutes</html:option>
        <html:option value="15">15 minutes</html:option>
        <html:option value="30">30 minutes</html:option>
        <html:option value="45">45 minutes</html:option>
      </html:select>
      <html:errors property="durationMinutes" />
    </td>
    <td><strong><bean:message key="app.servicedictionary.releaseId" />:</strong></td>
    <td>
      <html:radio property="releaseId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="releaseId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="releaseId" />                   
    </td>   
  </tr>
  <tr>
    <td><strong><bean:message key="app.servicedictionary.priceTypeKey" />:</strong></td>
    <td>
      <html:select property="priceTypeKey" disabled="<%=attDisableEdit%>">
      <!-- iterate over the price type -->
      <logic:iterate id="priceTypeForm"
                 name="serviceDictionaryForm"
                 property="priceTypeForm"
                 scope="session"
                 type="com.wolmerica.pricetype.PriceTypeForm">
        <html:option value="<%=priceTypeForm.getKey()%>" > <%=priceTypeForm.getName()%> </html:option>
      </logic:iterate>
      </html:select>
      <html:errors property="priceTypeKey" />
    </td>
    <td><strong><bean:message key="app.servicedictionary.billableId" />:</strong></td>
    <td>
      <html:radio property="billableId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="billableId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="billabeId" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.servicedictionary.laborCost" />:</strong></td>
    <td>
      <html:text property="laborCost" size="4" maxlength="8" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="laborCost" />            
    </td>         
    <td><strong><bean:message key="app.servicedictionary.serviceCost" />:</strong></td>
    <td>
      <html:text property="serviceCost" size="4" maxlength="8" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="serviceCost" />            
    </td>         
  </tr>    
  <tr>
    <td><strong><bean:message key="app.servicedictionary.markUp1Rate" />:</strong></td>
    <td>
      <html:text property="markUp1Rate" size="4" maxlength="8" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="markUp1Rate" />            
    </td>         
    <td><strong><bean:message key="app.servicedictionary.markUp2Rate" />:</strong></td>
    <td>
      <html:text property="markUp2Rate" size="4" maxlength="8" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="markUp2Rate" />            
    </td>         
  </tr>
  <tr>
    <td><strong><bean:message key="app.servicedictionary.fee1Cost" />:</strong></td>
    <td>
      <html:text property="fee1Cost" size="4" maxlength="8" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="fee1Cost" />            
    </td>         
    <td><strong><bean:message key="app.servicedictionary.fee2Cost" />:</strong></td>
    <td>
      <html:text property="fee2Cost" size="4" maxlength="8" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="fee2Cost" />            
    </td>         
  </tr>
  <tr>
    <td><strong><bean:message key="app.servicedictionary.createUser" />:</strong></td>
    <td><bean:write name="serviceDictionaryForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.servicedictionary.createStamp" />:</strong></td>
    <td><bean:write name="serviceDictionaryForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.servicedictionary.updateUser" />:</strong></td>
    <td><bean:write name="serviceDictionaryForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.servicedictionary.updateStamp" />:</strong></td>
    <td><bean:write name="serviceDictionaryForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="3">
    </td>
    <td align="right">
      <input type="hidden" name="serviceNameFilter" value="<bean:write name="serviceDictionaryForm" property="serviceNameFilter" />" >
      <input type="hidden" name="serviceNumFilter" value="<bean:write name="serviceDictionaryForm" property="serviceNumFilter" />" >
      <input type="hidden" name="categoryNameFilter" value="<bean:write name="serviceDictionaryForm" property="categoryNameFilter" />" >
      <input type="hidden" name="sdPageNo" value="<bean:write name="serviceDictionaryForm" property="currentPage" />" >
      <html:submit value="Save Service Values" disabled="<%=attDisableEdit%>" />         
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

<logic:notEqual name="serviceDictionaryForm" property="key" value="">    
<div id="custTypeInfo" style="display:none;">
<!--=========================================================-->
<!-- Customer Attribute By Service for editing only          -->
<!--=========================================================-->
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td colspan="3">
        <strong><bean:message key="app.servicedictionary.serviceName" />:</strong>
        &nbsp;&nbsp;
        <bean:write name="serviceDictionaryForm" property="serviceName" />
      </td>
    </tr>
    <tr>
      <td colspan="7">
        <hr>
      </td>
    </tr>
    <tr align="left">
      <th><bean:message key="app.customerattributebyservice.customerTypeName" /></th>
      <th><bean:message key="app.customerattributebyservice.markUp1Rate" /></th>                    
      <th><bean:message key="app.customerattributebyservice.markUp2Rate" /></th>
      <th><bean:message key="app.customerattributebyservice.fee1Cost" /></th>
      <th><bean:message key="app.customerattributebyservice.fee2Cost" /></th>
      <th><bean:message key="app.customerattributebyservice.discountThreshold" /></th>      
      <th><bean:message key="app.customerattributebyservice.discountRate" /></th>
    </tr>
    <tr>
      <td colspan="7">
        <hr>
      </td>
    </tr>
  <!-- Set the Base and Error variables for customer attribute -->
    <% i = -1; 
      String fee1CostBase="fee1Cost";
      String fee2CostBase="fee2Cost";
      String markUp1RateBase="markUp1Rate";
      String markUp2RateBase="markUp2Rate";
      String discountThresholdBase="discountThreshold"; 
      String discountRateBase="discountRate"; 
      String fee1CostError;
      String fee2CostError;
      String markUp1RateError;
      String markUp2RateError;
      String discountThresholdError; 
      String discountRateError;  
      String customerTypeKey = "";
    %>
    <html:form action="/CustomerAttributeByServiceEdit.do"> 
    <input type="hidden" name="key" value="<bean:write name="serviceDictionaryForm" property="key" />" >
  <!-- iterate over the customer attribute by service -->     
    <logic:iterate id="customerAttributeByServiceForm"
                   name="customerAttributeByServiceHeadForm"
                   property="customerAttributeByServiceForm"
                   scope="session"
                   type="com.wolmerica.customerattributebyservice.CustomerAttributeByServiceForm">
    <%
      ++i;      
      if ( i % 2 == 0) { currentColor = "ODD"; }
      else { currentColor = "EVEN"; }
      fee1CostError = fee1CostBase + i;
      fee2CostError = fee2CostBase + i;
      markUp1RateError = markUp1RateBase + i;
      markUp2RateError = markUp2RateBase + i;
      discountThresholdError = discountThresholdBase + i;
      discountRateError = discountRateBase + i;
      customerTypeKey = customerAttributeByServiceForm.getCustomerTypeKey();
    %>   
    <tr align="left" class="<%=currentColor %>" >
      <td>    
        <strong>
          <bean:write name="customerAttributeByServiceForm" property="customerTypeName" />
        </strong>
      </td>  
      <td>
        <html:text indexed="true" name="customerAttributeByServiceForm" property="markUp1Rate" size="2" readonly="<%=attDisableEdit%>" />    
        <bean:message key="app.localePercent" />
        <html:errors property="<%=markUp1RateError%>" /> 
      </td>
      <td>
        <html:text indexed="true" name="customerAttributeByServiceForm" property="markUp2Rate" size="2" readonly="<%=attDisableEdit%>" />    
        <bean:message key="app.localePercent" />
        <html:errors property="<%=markUp2RateError%>" /> 
      </td>
      <td>
        <bean:message key="app.localeCurrency" />    
        <html:text indexed="true" name="customerAttributeByServiceForm" property="fee1Cost" size="2" readonly="<%=attDisableEdit%>" />
        <html:errors property="<%=fee1CostError%>" />         
      </td>
      <td>
        <bean:message key="app.localeCurrency" />    
        <html:text indexed="true" name="customerAttributeByServiceForm" property="fee2Cost" size="2" readonly="<%=attDisableEdit%>" />
        <html:errors property="<%=fee2CostError%>" /> 
      </td>      
      <td>
        <html:text indexed="true" name="customerAttributeByServiceForm" property="discountThreshold" size="2" readonly="<%=attDisableEdit%>" />
        <html:errors property="<%=discountThresholdError%>" /> 
      </td>
      <td>
        <html:text indexed="true" name="customerAttributeByServiceForm" property="discountRate" size="2" readonly="<%=attDisableEdit%>" />
        <bean:message key="app.localePercent" />
        <html:errors property="<%=discountRateError%>" /> 
      </td>
    </tr>   
    </logic:iterate>  
    <tr>
      <td colspan="8" align="right">
        <input type="hidden" name="serviceNameFilter" value="<bean:write name="serviceDictionaryForm" property="serviceNameFilter" />" >
        <input type="hidden" name="categoryNameFilter" value="<bean:write name="serviceDictionaryForm" property="categoryNameFilter" />" >
        <input type="hidden" name="sdPageNo" value="<bean:write name="serviceDictionaryForm" property="currentPage" />" >
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
<!-- Price Attribute By Service headings                     -->
<!--=========================================================-->  
<div id="priceTypeInfo" style="display:none;">
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td colspan="3">
        <strong><bean:message key="app.servicedictionary.serviceName" />:</strong>
        &nbsp;&nbsp;
        <bean:write name="serviceDictionaryForm" property="serviceName" />
      </td>
    </tr>
    <tr>
      <td colspan="20">
        <hr>
      </td>
    </tr>
    <tr align="left">
      <th align="left"><bean:message key="app.customerattributebyservice.customerTypeName" /></th>

      <!-- iterate over the price attribute by service -->     
      <logic:iterate id="priceByServiceForm"
                     name="priceByServiceHeadForm"
                     property="priceByServiceForm"
                     scope="session"
                     type="com.wolmerica.pricebyservice.PriceByServiceForm">
        <logic:equal name="priceByServiceForm" property="customerTypeKey" value="<%=customerTypeKey%>">            
          <td>
            <strong>
              <bean:write name="priceByServiceForm" property="priceTypeName" />
              <br><bean:message key="app.pricebyservice.computedPrice" />
            </strong>
          </td>
          <td>
            <strong>
              <bean:write name="priceByServiceForm" property="priceTypeName" />
              <br><bean:message key="app.pricebyservice.overRidePrice" />
            </strong>
          </td>
        </logic:equal>
      </logic:iterate> 
    </tr>
  
<!--=========================================================-->
<!-- Price By Service price values                           -->
<!--=========================================================--> 
    <% i = -1; 
      String computedPriceBase = "computedPrice";
      String overRidePriceBase = "overRidePrice";
      String computedPriceError;
      String overRidePriceError;    
    %>   

      <html:form action="/PriceByServiceEdit.do">  
      <input type="hidden" name="key" value="<bean:write name="serviceDictionaryForm" property="key" />" >

      <!-- iterate over the customer attribute by service -->     
      <logic:iterate id="customerAttributeByServiceForm"
                     name="customerAttributeByServiceHeadForm"
                     property="customerAttributeByServiceForm"
                     scope="session"
                     type="com.wolmerica.customerattributebyservice.CustomerAttributeByServiceForm">
      <%
        ++i;      
        if ( i % 2 == 0) { currentColor = "ODD"; }
        else { currentColor = "EVEN"; }
        computedPriceError = computedPriceBase + i;        
        overRidePriceError = overRidePriceBase + i;
        customerTypeKey = customerAttributeByServiceForm.getCustomerTypeKey();
      %>   
    <tr align="left" class="<%=currentColor %>">
      <td>
        <strong>
          <bean:write name="customerAttributeByServiceForm" property="customerTypeName" />
        </strong>
      </td>   
      <logic:iterate id="priceByServiceForm"
                     name="priceByServiceHeadForm"
                     property="priceByServiceForm"
                     scope="session"
                     type="com.wolmerica.pricebyservice.PriceByServiceForm">
        <logic:equal name="priceByServiceForm" property="customerTypeKey" value="<%=customerTypeKey%>">   
          <td>
            <bean:message key="app.localeCurrency" />
            <bean:write name="priceByServiceForm" property="computedPrice" />
          </td>
          <td>
            <bean:message key="app.localeCurrency" />
<!--
            <bean:write name="priceByServiceForm" property="overRidePrice" />
-->            
            <html:text name="priceByServiceForm" indexed="true" property="overRidePrice" size="4" readonly="<%=attDisableEdit%>" />      
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
        <bean:message key="app.pricebyservice.notFound" />&nbsp;
        <bean:write name="serviceDictionaryForm" property="key" />
      </td>                 
    </tr>
    <% } else { %>
    <tr>
      <td colspan="20" align="right">
        <input type="hidden" name="serviceNameFilter" value="<bean:write name="serviceDictionaryForm" property="serviceNameFilter" />" >
        <input type="hidden" name="serviceNumFilter" value="<bean:write name="serviceDictionaryForm" property="serviceNumFilter" />" >
        <input type="hidden" name="categoryNameFilter" value="<bean:write name="serviceDictionaryForm" property="categoryNameFilter" />" >
        <input type="hidden" name="sdPageNo" value="<bean:write name="serviceDictionaryForm" property="currentPage" />" >
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
