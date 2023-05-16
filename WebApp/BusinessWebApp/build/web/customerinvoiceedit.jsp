<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script  type="text/javascript" src="./js/date_picker.js"></script>

<%
  String attTitle = "app.customerinvoice.addTitle";            
  String attAction = "/CustomerInvoiceAdd";
  String attributeToAction = "/PetLookUp.do";
  String attDisableCreditButton = "";   
  String attDisablePostButton = "disabled";
  String attVILimit = "";
  
  boolean attKeyEdit = false;
  boolean attDisableEdit = true;
  if (request.getAttribute("disableEdit") != null) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
       attDisablePostButton = "";
     }
  }
  boolean attDisableCustomerLookup = attDisableEdit;
  boolean attDisableClinicEdit = attDisableEdit;
  boolean attDisableShipEdit = attDisableEdit;  
  boolean attDisableDropListLookup = attDisableEdit;
  boolean attNonInventorySales = false;
  boolean attInventorySales = false;  
  boolean attServiceSales = false;   
  boolean attSalesScenarios = false;
  boolean attOfficeScenarios = false;
  boolean attSaveButton = attDisableEdit;
  boolean attAttributeToLookup = false;
%>
 
<logic:notEqual name="customerInvoiceForm" property="key" value="">
  <%
    attTitle = "app.customerinvoice.editTitle";
    attAction = "/CustomerInvoiceEdit";
    attKeyEdit = true;    
  %>
</logic:notEqual>   
   
<logic:equal name="customerInvoiceForm" property="key" value="">
  <logic:equal name="customerInvoiceForm" property="scenarioKey" value="3">
    <% 
       attAction = "/CustomerInvoiceCreditAdd"; 
       attSaveButton = false;        
    %>
  </logic:equal>
</logic:equal>

<logic:equal name="customerInvoiceForm" property="scenarioKey" value="-1">
  <%
     attDisableClinicEdit = true;
     attDisableShipEdit = true;
    %>   
</logic:equal>                                     
<logic:equal name="customerInvoiceForm" property="scenarioKey" value="0">
  <%
     attDisableCustomerLookup = true;
     attDisableShipEdit = true;
     attNonInventorySales = true;
     attServiceSales = true;
     attSalesScenarios = true;
     attAttributeToLookup = true;
  %>     
</logic:equal>
<logic:equal name="customerInvoiceForm" property="scenarioKey" value="1">
  <% 
     attDisableCustomerLookup = true;
     attNonInventorySales = true;
     attServiceSales = true;
     attSalesScenarios = true; 
     attAttributeToLookup = true;
  %>
</logic:equal> 
<logic:equal name="customerInvoiceForm" property="scenarioKey" value="2">              
  <% 
     attInventorySales = true;
     attServiceSales = true;
     attSalesScenarios = true;
     attAttributeToLookup = true;
  %>
</logic:equal> 
<logic:equal name="customerInvoiceForm" property="scenarioKey" value="3">
  <%
     attServiceSales = true;
     attDisableCustomerLookup = true;
     attDisableDropListLookup = true;
  %>
</logic:equal>
<logic:equal name="customerInvoiceForm" property="scenarioKey" value="4">
  <%
     attDisableCustomerLookup = true;     
     attDisableClinicEdit = true; 
     attDisableShipEdit = true; 
  %>
</logic:equal>                                     
<logic:equal name="customerInvoiceForm" property="scenarioKey" value="5">
  <% 
     attDisableCustomerLookup = true;
     attInventorySales = true;
     attSalesScenarios = true; 
     attOfficeScenarios = true;     
  %>
</logic:equal>              
<logic:equal name="customerInvoiceForm" property="scenarioKey" value="6">
  <% 
     attDisableCustomerLookup = true;     
     attInventorySales = true;
     attSalesScenarios = true; 
     attOfficeScenarios = true;     
  %>
</logic:equal>  
  
<logic:notEqual name="customerInvoiceForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="customerInvoiceForm" property="permissionStatus" value="READONLY">
    [<bean:write name="customerInvoiceForm" property="permissionStatus" />]
  </logic:notEqual>
  <%
    attDisableCreditButton = "disabled";   
  %>
</logic:notEqual>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2" align="left">
      <logic:notEqual name="customerInvoiceForm" property="vendorInvoiceKey" value="">
        <jsp:useBean id="customerInvoiceForm" scope="session" 
          class="com.wolmerica.customerinvoice.CustomerInvoiceForm" />      
        <% attVILimit = "?key=" + customerInvoiceForm.getVendorInvoiceKey().toString(); %>
      </logic:notEqual>         
      <button type="button" onClick="window.location='CustomerInvoiceList.do<%=attVILimit%>' "><bean:message key="app.customerinvoice.backMessage" /></button>
    </td>
    <logic:equal name="customerInvoiceForm" property="key" value="">
      <td colspan="2" align="right">         
        <strong><bean:message key="app.customerinvoice.clickSave" /></strong>            
      </td>
    </logic:equal>
    <logic:notEqual name="customerInvoiceForm" property="key" value="">       
      <td colspan="2" align="right">  
        <table cellpadding="10">
          <tr>
          <% if (attServiceSales) { %>    
            <td>    
              <button type="button" onClick="window.location='CustomerInvoiceServiceGet.do?key=<bean:write name="customerInvoiceForm" 
                property="key" />' "><bean:message key="app.customerinvoice.serviceDetail" /></button>              
            </td>          
          <% } %>
            <td align="right">
              <button type="button" onClick="window.location='CustomerInvoiceItemGet.do?key=<bean:write name="customerInvoiceForm" 
                property="key" />' "><bean:message key="app.customerinvoice.itemDetail" /></button>              
            </td>
          </tr>
        </table>
      </td>        
    </logic:notEqual>       
  </tr>
  <tr>
    <td colspan="6">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
  <logic:notEqual name="customerInvoiceForm" property="key" value="">
    <html:hidden property="key" />
  </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.customerInvoiceNumber" />:</strong></td>
    <td>
      <html:text property="customerInvoiceNumber" size="15" readonly="<%=attKeyEdit%>" />
      <html:errors property="customerInvoiceNumber" />            
    </td>
    <td><strong><bean:message key="app.customerinvoice.clientName" />:</strong></td>
    <td>
      <html:hidden property="customerKey" />
      <html:hidden property="clientName" />
      <% if (attDisableEdit) { %>      
        <html:text property="clientName" size="35" maxlength="35" readonly="true" />
      <% } else { %>
        <span class="ac_holder">
        <input id="clientNameAC" size="35" maxlength="35" value="<bean:write name="customerInvoiceForm" property="clientName" />" onFocus="javascript:
        var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.customerInvoiceForm.customerKey.value=obj.id; document.customerInvoiceForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
        </span>
        <html:errors property="customerKey" />
      <% } %>        
    </td> 
  </tr>
  <tr>
    <td><strong><bean:message key="app.customer.customerTypeKey" />:</strong></td>
    <td>
      <html:select property="customerTypeKey" disabled="<%=attDisableDropListLookup%>">
      <!-- iterate over the customer type -->       
      <logic:iterate id="customerTypeForm"
                     name="customerInvoiceForm"
                     property="customerTypeForm"
                     scope="session"
                     type="com.wolmerica.customertype.CustomerTypeForm">   
          <html:option value="<%=customerTypeForm.getKey()%>" > <%=customerTypeForm.getName()%> </html:option>
      </logic:iterate>
      </html:select>
      <html:errors property="customerTypeKey" />
    </td>  
<!-- Present the office use and office loss scenarios for office related invoices.  -->
  <% if (attOfficeScenarios) { %>
    <td>         
      <strong><bean:message key="app.customerinvoice.officeScenario" />:</strong>
    </td>
    <td>
      <html:select property="scenarioKey" disabled="<%=attDisableDropListLookup%>">
      <html:option value="5" ><bean:message key="app.customerinvoice.officeUse" /> </html:option>
      <html:option value="6" ><bean:message key="app.customerinvoice.officeLoss" /> </html:option>              
      </html:select>          
      <html:errors property="scenarioKey" />
    </td>
  <% } else { %>
    <html:hidden property="scenarioKey" />      
    <td>         
      <html:hidden property="sourceTypeKey" />    
      <html:hidden property="sourceKey" />
      <html:hidden property="attributeToName" />
      <strong>
        <bean:message key="app.customerinvoice.attributeToName" />&nbsp;
        <bean:write name="customerInvoiceForm" property="attributeToEntity" />
      </strong>
    </td>
    <td>
      <% if (!(attAttributeToLookup) || attDisableEdit) { %>      
        <html:text property="attributeToName" size="35" maxlength="35" readonly="true" />
      <% } else { %>
        <logic:equal name="customerInvoiceForm" property="attributeToEntity" value="Pet">
          <span class="ac_holder">
          <input id="attributeToNameAC" size="35" maxlength="35" value="<bean:write name="customerInvoiceForm" property="attributeToName" />" onFocus="javascript:
          var options = {
		script:'PetLookUp.do?json=true&customerKeyFilter=<bean:write name="customerInvoiceForm" property="customerKey" />&',
		varname:'petNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.customerInvoiceForm.sourceTypeKey.value=4; document.customerInvoiceForm.sourceKey.value=obj.id; document.customerInvoiceForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
          </span>
        </logic:equal>        
        <logic:equal name="customerInvoiceForm" property="attributeToEntity" value="System">
          <span class="ac_holder">
          <input id="attributeToNameAC" size="35" maxlength="35" value="<bean:write name="customerInvoiceForm" property="attributeToName" />" onFocus="javascript:
          var options = {
		script:'SystemLookUp.do?json=true&customerKeyFilter=<bean:write name="customerInvoiceForm" property="customerKey" />&',
		varname:'makeModelFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.customerInvoiceForm.sourceTypeKey.value=24; document.customerInvoiceForm.sourceKey.value=obj.id; document.customerInvoiceForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
          </span>
        </logic:equal>
        <logic:equal name="customerInvoiceForm" property="attributeToEntity" value="Vehicle">
          <span class="ac_holder">
          <input id="attributeToNameAC" size="35" maxlength="35" value="<bean:write name="customerInvoiceForm" property="attributeToName" />" onFocus="javascript:
          var options = {
		script:'VehicleLookUp.do?json=true&customerKeyFilter=<bean:write name="customerInvoiceForm" property="customerKey" />&',
		varname:'makeFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.customerInvoiceForm.sourceTypeKey.value=31; document.customerInvoiceForm.sourceKey.value=obj.id; document.customerInvoiceForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
          </span>              
        </logic:equal>
      <% } %>              
      <html:errors property="attributeToName" />      
    </td>
  <% } %>
  </tr>
<!-- A non-negative vendor invoice key indicates that the vendor name can be displayed.  -->   
<logic:notEqual name="customerInvoiceForm" property="vendorInvoiceKey" value="-1"> 
  <tr>
    <td>         
      <html:hidden property="vendorInvoiceKey" />
      <strong><bean:message key="app.customerinvoice.vendorName" />:</strong>
    </td>
    <td>
      <html:text property="vendorName" size="32" readonly="true" />
      <html:errors property="vendorName" />
    </td>
  </tr>
</logic:notEqual>  
  <tr>
    <td><strong><bean:message key="app.customerinvoice.itemDiscountAmount" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="itemDiscountAmount" size="7" readonly="true" />
      <html:errors property="itemDiscountAmount" />
    </td>       
    <td>
      <strong><bean:message key="app.customerinvoice.itemNetAmount" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="itemNetAmount" size="7" readonly="true" />
      <html:errors property="itemNetAmount" />   
    </td>            
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.serviceDiscountAmount" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="serviceDiscountAmount" size="7" readonly="true" />
      <html:errors property="serviceDiscountAmount" />
    </td>       
    <td>
      <strong><bean:message key="app.customerinvoice.serviceNetAmount" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="serviceNetAmount" size="7" readonly="true" />
      <html:errors property="serviceNetAmount" />   
    </td>            
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.netProfitAmount" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="netProfitAmount" size="7" readonly="true" />
      <html:errors property="netProfitAmount" />
    </td>
    <td>
      <strong><bean:message key="app.customerinvoice.subTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="subTotal" size="7" readonly="true" />
      <html:errors property="subTotal" />   
    </td>           
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.salesTaxKey" />:</strong></td>
    <td>
      <html:select property="salesTaxKey" disabled="<%=attDisableDropListLookup%>">
      <!-- iterate over the sales tax keys -->       
      <logic:iterate id="taxMarkUpForm"
                     name="customerInvoiceForm"
                     property="taxMarkUpForm"
                     scope="session"
                     type="com.wolmerica.taxmarkup.TaxMarkUpForm">      
        <% if (taxMarkUpForm.getName().contains("SALES")) { %>
          <html:option value="<%=taxMarkUpForm.getKey()%>" > <%=taxMarkUpForm.getDescription()%> </html:option>
        <% } %>
      </logic:iterate>
      </html:select>
      <html:errors property="salesTaxKey" />
    </td>
    <td><strong><bean:message key="app.customerinvoice.salesTaxCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="salesTaxCost" size="7" readonly="true" />
      <html:errors property="salesTaxCost" />
    </td>  
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.taxableTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="taxableTotal" size="7" readonly="true" />
      <html:errors property="taxableTotal" />
    </td> 
    
    <logic:notEqual name="customerInvoiceForm" property="serviceTaxRate" value="0.00">
        <td><strong><bean:message key="app.customerinvoice.serviceTaxCost" />:</strong></td>
        <td>
          <bean:message key="app.localeCurrency" />
          <html:text property="serviceTaxCost" size="7" readonly="true" />
          <html:errors property="serviceTaxCost" />
        </td>  
      </tr>  
      <tr>
        <td><strong><bean:message key="app.customerinvoice.serviceTaxableTotal" />:</strong></td>
        <td>
          <bean:message key="app.localeCurrency" />
          <html:text property="serviceTaxableTotal" size="7" readonly="true" />
          <html:errors property="serviceTaxableTotal" />
        </td>
    </logic:notEqual>    
    
    <td><strong><bean:message key="app.customerinvoice.packagingCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="packagingCost" size="7" readonly="<%=attDisableShipEdit%>" />
      <html:errors property="packagingCost" />            
    </td>          
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.debitAdjustment" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />          
      <html:text property="debitAdjustment" size="7" readonly="<%=attDisableClinicEdit%>" />
      <html:errors property="debitAdjustment" />            
    </td>                                   
    <td><strong><bean:message key="app.customerinvoice.freightCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="freightCost" size="7" readonly="<%=attDisableShipEdit%>" />
      <html:errors property="freightCost" />            
    </td> 
  </tr>	
  <tr>
    <td><strong><bean:message key="app.customerinvoice.creditAdjustment" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />          
      <html:text property="creditAdjustment" size="7" readonly="<%=attDisableClinicEdit%>" />
      <html:errors property="creditAdjustment" />            
    </td>                   
    <td><strong><bean:message key="app.customerinvoice.miscellaneousCost" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="miscellaneousCost" size="7" readonly="<%=attDisableShipEdit%>" />
      <html:errors property="miscellaneousCost" />            
    </td>
  </tr>
  <tr>
    <td></td>
    <td></td>                
    <td><strong><bean:message key="app.customerinvoice.invoiceTotal" />:</strong></td>
    <td>
      <bean:message key="app.localeCurrency" />
      <html:text property="invoiceTotal" size="7" readonly="true" />
      <html:errors property="invoiceTotal" />            
      &nbsp;&nbsp;&nbsp;
      [<bean:write name="customerInvoiceForm" property="logisticsCount" />]
      <a href="" onClick="popup = logisticspopup('LogisticsGet.do?sourceTypeKey=<bean:message key="app.customerinvoice.id" />&sourceKey=<bean:write name="customerInvoiceForm" 
        property="key" />&sourceName=<bean:write name="customerInvoiceForm"
        property="customerInvoiceNumber" />&disableEdit=<%=attDisableEdit%>', 'PopupPage'); return false" target="_blank"><bean:message key="app.customerinvoice.shippingInfo" /></a>           
    </td>  
  </tr>		
  <tr>
    <td><strong><bean:message key="app.customerinvoice.noteLine1" />:</strong></td>
    <td colspan="4">   
      <html:text property="noteLine1" size="80" maxlength="80" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine1" />
    </td> 
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.noteLine2" />:</strong></td>
    <td colspan="4">   
      <html:text property="noteLine2" size="80" maxlength="80" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine2" />            
    </td> 
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.noteLine3" />:</strong></td>
    <td colspan="4">   
      <html:text property="noteLine3" size="80" maxlength="80" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine3" />            
    </td> 
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.createUser" />:</strong></td>
    <td><bean:write name="customerInvoiceForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.customerinvoice.createStamp" />:</strong></td>
    <td>
      <bean:write name="customerInvoiceForm" property="createStamp" />
      <html:errors property="createStamp" />            
    </td>	     
  </tr>
  <tr>
    <td><strong><bean:message key="app.customerinvoice.updateUser" />:</strong></td>
    <td><bean:write name="customerInvoiceForm" property="updateUser" /></td>	     
    <td><strong><bean:message key="app.customerinvoice.updateStamp" />:</strong></td>
    <td>
      <bean:write name="customerInvoiceForm" property="updateStamp" />
      <html:errors property="updateStamp" />            
    </td>	     
  </tr>	
  <tr>
    <td colspan="4" align="right">
    <html:submit value="Save Customer Invoice" disabled="<%=attSaveButton%>" />         
    </html:form> 
    </td>          
  </tr>
  <tr>
  <logic:notEqual name="customerInvoiceForm" property="key" value="">                  
    <logic:equal name="customerInvoiceForm" property="activeId" value="true">
    <% if (attNonInventorySales) { %>
    <td colspan="2" align="left"> 
      <form action="PostNewSaleToLedger.do" name="postNewSaleToLedger" method="post">                                  
        <input type="hidden" name="key" value="<bean:write name="customerInvoiceForm" property="key" />">
        <input type="hidden" name="ciState" value="false">              
        <input type="submit" value="<bean:message key="app.customerinvoice.complete" />" <%=attDisablePostButton%> onclick="return confirm('<bean:message key="app.customerinvoice.complete.message" />')" >
      </form>
    </td>
    <% } %>
    <% if (attInventorySales) { %>            
      <logic:notEqual name="customerInvoiceForm" property="invoiceTotal" value="0.00">
        <logic:equal name="customerInvoiceForm" property="adjustmentId" value="false">          
          <td colspan="2" align="left"> 
            <form action="StockItemDistribution.do" name="stockItemDistribution" method="post">                                  
              <input type="hidden" name="key" value="<bean:write name="customerInvoiceForm" property="key" />">
              <input type="hidden" name="ciState" value="false">              
              <input type="submit" value="<bean:message key="app.customerinvoice.complete" />" <%=attDisablePostButton%> onclick="return confirm('<bean:message key="app.customerinvoice.complete.message" />')" >
            </form>
          </td>
        </logic:equal>
      </logic:notEqual>
    <% } %>
      <logic:equal name="customerInvoiceForm" property="scenarioKey" value="4"> 
        <logic:notEqual name="customerInvoiceForm" property="itemCount" value="0">               
          <td colspan="2" align="left"> 
            <form action="StockItemAcquisition.do" name="stockItemAcquisition" method="post">                                  
              <input type="hidden" name="key" value="<bean:write name="customerInvoiceForm" property="key" />">
              <input type="hidden" name="ciState" value="false">              
              <input type="submit" value="<bean:message key="app.customerinvoice.inventory" />" <%=attDisablePostButton%> onclick="return confirm('<bean:message key="app.customerinvoice.inventory.message" />')" >
            </form>
          </td>
        </logic:notEqual>      
      </logic:equal>
      <logic:equal name="customerInvoiceForm" property="scenarioKey" value="3">
        <td colspan="2" align="left"> 
          <form action="PostCreditToLedger.do" name="stockItemAcquisition" method="post">                                  
            <input type="hidden" name="key" value="<bean:write name="customerInvoiceForm" property="key" />">
            <input type="hidden" name="ciState" value="false">              
            <input type="submit" value="<bean:message key="app.customerinvoice.postcredit" />" <%=attDisablePostButton%> onclick="return confirm('<bean:message key="app.customerinvoice.inventory.message" />')" >
          </form>
        </td>
      </logic:equal>
      <logic:equal name="customerInvoiceForm" property="adjustmentId" value="true">
        <td colspan="2" align="left"> 
          <form action="PostNewSaleToLedger.do" name="postNewSalesToLedger" method="post">                                  
            <input type="hidden" name="key" value="<bean:write name="customerInvoiceForm" property="key" />">
            <input type="hidden" name="ciState" value="false">              
            <input type="submit" value="<bean:message key="app.customerinvoice.postAdjustment" />" <%=attDisablePostButton%> onclick="return confirm('<bean:message key="app.customerinvoice.inventory.message" />')" >
          </form>
        </td>      
      </logic:equal>
    </logic:equal>
    <logic:equal name="customerInvoiceForm" property="allowCreditId" value="true">
      <% if (attSalesScenarios) { %>
      <td colspan="2" align="left"> 
        <form action="CustomerInvoiceCreditEntry.do" name="customerInvoiceCreditEntry" method="post">                                  
          <input type="hidden" name="key" value="<bean:write name="customerInvoiceForm" property="genesisKey" />">
          <input type="hidden" name="creditKey" value="<bean:write name="customerInvoiceForm" property="genesisKey" />">
          <input type="submit" value="<bean:message key="app.customerinvoice.credit" />" <%=attDisableCreditButton%> onclick="return confirm('<bean:message key="app.customerinvoice.credit.message" />')" >
        </form>         
      </td>
      <% } %>
    </logic:equal>
    <logic:equal name="customerInvoiceForm" property="allowAdjustmentId" value="true">
      <td colspan="2" align="left"> 
        <form action="CustomerInvoiceAdjustment.do" name="customerInvoiceAdjustment" method="post">
          <input type="hidden" name="key" value="<bean:write name="customerInvoiceForm" property="genesisKey" />">
          <input type="submit" value="<bean:message key="app.customerinvoice.allowAdjustment" />" <%=attDisableCreditButton%> onclick="return confirm('<bean:message key="app.customerinvoice.adjustment.message" />')" >
        </form>         
      </td>    
    </logic:equal>    
  </logic:notEqual>          
  </tr>
</table>