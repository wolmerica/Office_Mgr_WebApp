<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<LINK REL=STYLESHEET HREF="css/style_sheet.css">

<SCRIPT LANGUAGE=JavaScript>
  window.focus();
    
  function testIsValidObject(objToTest) {

     if (objToTest == null || objToTest == undefined) {
        return false;
     }
     return true;
  }

  function set_parent_value(key, price) {
    if (testIsValidObject(opener))
      {
      var myDoc = opener.document;
      if (testIsValidObject(myDoc))
      {    
        if (testIsValidObject(myDoc.customerInvoiceItemHeadForm))
        {       
          myDoc.customerInvoiceItemHeadForm.elements[key].value = price;
        }
      }
      opener.focus();
    }
    window.close();
  }

  function showElements(theForm) {
    str = "Form Elements of form " + theForm.name + ": \n "
    for (i = 0; i < theForm.length; i++)
       str += theForm.elements[i].name + "\n"
    alert(str);
  }
  
</SCRIPT>

<%
  String currentColor = "EVEN";
  int i = -1;
    
  String attTitle = "app.itemdictionary.editTitle";            
  String attAction = "/ItemDictionaryAdjust";
  String ptName = ""; 
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }


  String attRow = request.getParameter("row").toString();
  String attCtKey = request.getParameter("ctkey").toString();
  String attPtKey = request.getParameter("ptkey").toString();
  
  int id = new Integer(attRow);
  int rlen = 7;
  int relmt = 4;
  id = (id * rlen) + relmt;
%> 

<table width="435" border="0" cellspacing="1" cellpadding="0">
  <tr>
    <html:form action="<%=attAction%>"> 
      <html:hidden property="key" />
      <html:hidden property="row" value="<%=attRow%>" />           
      <html:hidden property="ctkey" value="<%=attCtKey%>" />
      <html:hidden property="ptkey" value="<%=attPtKey%>" />    
      <html:hidden property="brandName" />
      <html:hidden property="size" />
      <html:hidden property="sizeUnit" />
      <html:hidden property="qtyOnHand" />
      <html:hidden property="genericName" />
      <html:hidden property="dose" />
      <html:hidden property="doseUnit" />
      <html:hidden property="orderThreshold" />
      <html:hidden property="itemName" /> 
      <html:hidden property="other" />
      <html:hidden property="itemNum" />
      <html:hidden property="manufacturer" />
      <html:hidden property="itemMemo" />
      <html:hidden property="reportId" />
      <html:hidden property="percentUse" />
      <html:hidden property="muVendor" />  
      <html:hidden property="unitCost" />    
      <html:hidden property="prevUnitCost" />     
    <td>
      <strong>Name</strong>
    </td>
    <td colspan=8>
      <bean:write name="itemDictionaryForm" property="brandName" />
    <td>
  </tr>
  <tr>
    <td>
      <strong>Size</strong>
    </td>  
    <td colspan=8>
      <bean:write name="itemDictionaryForm" property="size" />
    <td>
  </tr>
  <tr>
    <td>
      <strong>ITEM</strong>
    </td>
    <td colspan="8">
      <hr>
    </td>
  </tr>  
  <tr>
    <td><strong>Curr Cost<strong></td>
    <td><strong>Prev Cost<strong></td>    
    <td><strong>MarkUp<strong></td>    
    <td><strong>Carry Fact<strong></td>
    <td><strong>Label<strong></td>    
  </tr>
  <tr align="left" class="<%=currentColor%>" >
    <td>    
      <bean:message key="app.localeCurrency" />		    
      <html:text property="firstCost" size="4" maxlength="6" readonly="true" />
      <html:errors property="firstCost" />                                  
    </td>
    <td>
      <bean:message key="app.localeCurrency" />	    
      <html:text property="prevFirstCost" size="4" maxlength="6" readonly="true" />
      <html:errors property="prevFirstCost" />                                  
    </td>
    <td>
      <html:text property="muAdditional" size="2" maxlength="4" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />	      
      <html:errors property="muAdditional" />                                  
    </td>	        
    <td>	    
      <html:text property="carryFactor" size="2" maxlength="4" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />
      <html:errors property="carryFactor" />                                  
    </td>
    <td>
      <bean:message key="app.localeCurrency" />	    
      <html:text property="labelCost" size="4" maxlength="6" readonly="<%=attDisableEdit%>" />
      <html:errors property="labelCost" />                                  
    </td>    
    <td>
      <html:submit value="Save" disabled="<%=attDisableEdit%>" />         
      </html:form> 
    </td>
  </tr>
</table>

<!--=========================================================-->
<!-- Customer Attribute By Item edit                         -->
<!--=========================================================-->
<table width="435" border="0" cellspacing="1" cellpadding="0">

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
  
  <html:form action="/CustomerAttributeByItemAdjust.do"> 
    <input type="hidden" name="key" value="<bean:write name="itemDictionaryForm" property="key" />" >
    <html:hidden property="row" value="<%=attRow%>" />           
    <html:hidden property="ctkey" value="<%=attCtKey%>" />
    <html:hidden property="ptkey" value="<%=attPtKey%>" />
  
  <!-- iterate over the customer attribute by item -->     
  <logic:iterate id="customerAttributeByItemForm"
                 name="customerAttributeByItemHeadForm"
                 property="customerAttributeByItemForm"
                 scope="session"
                 type="com.wolmerica.customerattributebyitem.CustomerAttributeByItemForm">
  <%
    ++i;      
    labelCostError = labelCostBase + i;
    adminCostError = adminCostBase + i;
    markUpRateError = markUpRateBase + i;
    additionalMarkUpRateError = additionalMarkUpRateBase + i;
    discountThresholdError = discountThresholdBase + i;
    discountRateError = discountRateBase + i;
  %>   
  <tr>
    <td>    
      <strong>
        <bean:write name="customerAttributeByItemForm" property="customerTypeName" />
      </strong>
    </td>  
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <td><strong><bean:message key="app.customerattributebyitem.markUpRate" /></strong></td>
    <td><strong><bean:message key="app.customerattributebyitem.additionalMarkUpRate" /></strong></td>
    <td><strong><bean:message key="app.customerattributebyitem.labelCost" /><strong></td>
    <td><strong><bean:message key="app.customerattributebyitem.adminCost" /></strong></td>
    <td><strong><bean:message key="app.customerattributebyitem.discountThreshold" /></strong></td>
    <td><strong><bean:message key="app.customerattributebyitem.discountRate" /></strong></td>
  </tr>    
  <tr align="left" class="<%=currentColor%>" >
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
  </logic:iterate>  
    <td>
      <html:submit value="Save" disabled="<%=attDisableEdit%>" />         
    </td>
  </tr>         
  </html:form>    
</table>

<!--=========================================================-->
<!-- Price Attribute By Item                                 -->
<!--=========================================================-->
<table width="435" border="0" cellspacing="1" cellpadding="0">
    <tr>
      <td colspan="20">
        <hr>
      </td>
    </tr>
    <tr>    
      <!-- iterate over the price attribute by item -->     
      <logic:iterate id="priceAttributeByItemForm"
                     name="priceAttributeByItemHeadForm"
                     property="priceAttributeByItemForm"
                     scope="session"
                     type="com.wolmerica.priceattributebyitem.PriceAttributeByItemForm">
      <td>        
        <strong>
          <bean:write name="priceAttributeByItemForm" property="priceTypeName" />
        </strong>
      </td>
      </logic:iterate>   
      <td colspan="8">
        <hr>
      </td>
    </tr>
<!--=========================================================-->
<!-- Price By Item                                           -->
<!--=========================================================-->
    <% i = -1; 
      String overRidePriceBase = "overRidePrice";
      String overRidePriceError; 
    %>   
    <tr>
      <html:form action="/PriceByItemEdit.do">  
      <input type="hidden" name="key" value="<bean:write name="itemDictionaryForm" property="key" />" >
      <html:hidden property="row" value="<%=attRow%>" />           
      <html:hidden property="ctkey" value="<%=attCtKey%>" />
      <html:hidden property="ptkey" value="<%=attPtKey%>" />

      <%
        ++i;      
        overRidePriceError = overRidePriceBase + i;
      %>   
    <tr align="left">
      <logic:iterate id="priceByItemForm"
                     name="priceByItemHeadForm"
                     property="priceByItemForm"
                     scope="session"
                     type="com.wolmerica.pricebyitem.PriceByItemForm">          
    <tr align="left">
      <td>
        <input type="radio" name="price" value="<bean:write name="priceByItemForm" property="computedPrice" />" checked onclick="set_parent_value(<%=id%>, '<bean:write name="priceByItemForm" property="computedPrice" />')">
        <strong>Current</strong>
      </td>
      <td>
        <input type="radio" name="price" value="<bean:write name="priceByItemForm" property="computedPrice" />" onclick="set_parent_value(<%=id%>, '<bean:write name="priceByItemForm" property="previousPrice" />')">
        <strong>Previous</strong>
      </td>
      <td>
        <input type="radio" name="price" value="<bean:write name="priceByItemForm" property="overRidePrice" />" onclick="set_parent_value(<%=id%>, '<bean:write name="priceByItemForm" property="overRidePrice" />')">
        <strong>Over-Ride</strong>
      </td>
    </tr>          
    <tr align="left" class="<%=currentColor%>" >
      <td>
         <bean:message key="app.localeCurrency" />
         <html:text name="priceByItemForm" indexed="true" property="computedPrice" size="4" readonly="true" />      
      </td>
      <td>
         <bean:message key="app.localeCurrency" />
         <html:text name="priceByItemForm" indexed="true" property="previousPrice" size="4" readonly="true" />      
      </td>          
      <td>
         <bean:message key="app.localeCurrency" />
         <html:text name="priceByItemForm" indexed="true" property="overRidePrice" size="4" readonly="<%=attDisableEdit%>" />
         <html:errors property="<%=overRidePriceError%>" />             
      </td> 
    </logic:iterate>         
      <td>
        <html:submit value="Save" disabled="<%=attDisableEdit%>" />  
      </td>
    </tr>
    </html:form>    
  </table> 
