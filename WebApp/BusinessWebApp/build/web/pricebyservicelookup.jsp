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
        if (testIsValidObject(myDoc.customerInvoiceServiceHeadForm))
        {       
          myDoc.customerInvoiceServiceHeadForm.elements[key].value = price;
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
    
  String attTitle = "app.servicedictionary.editTitle";            
  String attAction = "/ServiceDictionaryAdjust";
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
      <html:hidden property="serviceName" />
      <html:hidden property="serviceCategory" />
      <html:hidden property="laborCost" />
      <html:hidden property="durationHours" />
      <html:hidden property="durationMinutes" />      
    <td>
      <strong>Name</strong>
    </td>
    <td colspan=8>
      <bean:write name="serviceDictionaryForm" property="serviceName" />
    <td>
  </tr>
  <tr>
    <td>
      <strong>Category</strong>
    </td>  
    <td colspan=8>
      <bean:write name="serviceDictionaryForm" property="serviceCategory" />
    <td>
  </tr>
  <tr>
    <td>
      <strong>SERVICE</strong>
    </td>
    <td colspan="8">
      <hr>
    </td>
  </tr>  
  <tr>
    <td><strong>Base Cost<strong></td>
    <td><strong>1st MrkUp<strong></td>
    <td><strong>2nd MrkUp<strong></td>
    <td><strong>1st Fee<strong></td>
    <td><strong>2nd Fee<strong></td>    
  </tr>
  <tr align="left" class="<%=currentColor%>" >
    <td>    
      <bean:message key="app.localeCurrency" />		    
      <html:text property="serviceCost" size="4" maxlength="6" readonly="true" />
      <html:errors property="serviceCost" />                                  
    </td>
    <td>
      <html:text property="markUp1Rate" size="2" maxlength="4" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />
      <html:errors property="markUp1Rate" />
    </td>	        
    <td>
      <html:text property="markUp2Rate" size="2" maxlength="4" readonly="<%=attDisableEdit%>" />
      <bean:message key="app.localePercent" />
      <html:errors property="markUp2Rate" />
    </td>	        
    <td>
      <bean:message key="app.localeCurrency" />	    
      <html:text property="fee1Cost" size="4" maxlength="6" readonly="<%=attDisableEdit%>" />
      <html:errors property="fee1Cost" />                                  
    </td>    
    <td>
      <bean:message key="app.localeCurrency" />	    
      <html:text property="fee2Cost" size="4" maxlength="6" readonly="<%=attDisableEdit%>" />
      <html:errors property="fee2Cost" />                                  
    </td>        
    <td>
      <html:submit value="Save" disabled="<%=attDisableEdit%>" />         
      </html:form> 
    </td>
  </tr>
</table>

<!--=========================================================-->
<!-- Customer Attribute By Service edit                      -->
<!--=========================================================-->
<table width="435" border="0" cellspacing="1" cellpadding="0">

  <!-- Set the Base and Error variables for customer attribute -->
  <% i = -1; 
    String markUp1RateBase="markUp1Rate";
    String markUp2RateBase="markUp2Rate";
    String fee1CostBase = "fee1Cost";
    String fee2CostBase="fee2Cost";
    String discountThresholdBase="discountThreshold"; 
    String discountRateBase="discountRate"; 
    String markUp1RateError;
    String markUp2RateError;
    String fee1CostError;
    String fee2CostError;
    String discountThresholdError;           
    String discountRateError;
  %>
  
  <html:form action="/CustomerAttributeByServiceAdjust.do"> 
    <input type="hidden" name="key" value="<bean:write name="serviceDictionaryForm" property="key" />" >
    <html:hidden property="row" value="<%=attRow%>" />           
    <html:hidden property="ctkey" value="<%=attCtKey%>" />
    <html:hidden property="ptkey" value="<%=attPtKey%>" />
  
  <!-- iterate over the customer attribute by service -->
  <logic:iterate id="customerAttributeByServiceForm"
                 name="customerAttributeByServiceHeadForm"
                 property="customerAttributeByServiceForm"
                 scope="session"
                 type="com.wolmerica.customerattributebyservice.CustomerAttributeByServiceForm">
  <%
    ++i;      
    markUp1RateError = markUp1RateBase + i;
    markUp2RateError = markUp2RateBase + i;
    fee1CostError = fee1CostBase + i;
    fee2CostError = fee2CostBase + i;
    discountThresholdError = discountThresholdBase + i;
    discountRateError = discountRateBase + i;
  %>   
  <tr>
    <td>    
      <strong>
        <bean:write name="customerAttributeByServiceForm" property="customerTypeName" />
      </strong>
    </td>  
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr align="left">
    <td><strong><bean:message key="app.customerattributebyservice.markUp1Rate" /></strong></td>
    <td><strong><bean:message key="app.customerattributebyservice.markUp2Rate" /></strong></td>
    <td><strong><bean:message key="app.customerattributebyservice.fee1Cost" /><strong></td>
    <td><strong><bean:message key="app.customerattributebyservice.fee2Cost" /></strong></td>
    <td><strong><bean:message key="app.customerattributebyservice.discountThreshold" /></strong></td>
    <td><strong><bean:message key="app.customerattributebyservice.discountRate" /></strong></td>
  </tr>    
  <tr align="left" class="<%=currentColor%>" >
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
  </logic:iterate>  
    <td>
      <html:submit value="Save" disabled="<%=attDisableEdit%>" />         
    </td>
  </tr>         
  </html:form>    
</table>

<!--=========================================================-->
<!-- Price By Service heading                                -->
<!--=========================================================-->
<table width="435" border="0" cellspacing="1" cellpadding="0">
    <tr>
      <td colspan="20">
        <hr>
      </td>
    </tr>
    <tr>    
      <!-- iterate over the price attribute by service -->     
      <logic:iterate id="priceByServiceForm"
                     name="priceByServiceHeadForm"
                     property="priceByServiceForm"
                     scope="session"
                     type="com.wolmerica.pricebyservice.PriceByServiceForm">
      <td>        
        <strong>
          PRICE BY <bean:write name="priceByServiceForm" property="priceTypeName" />
        </strong>
      </td>
      </logic:iterate>   
      <td colspan="8">
        <hr>
      </td>
    </tr>
<!--=========================================================-->
<!-- Price By Service details                                -->
<!--=========================================================-->
    <% i = -1; 
      String overRidePriceBase = "overRidePrice";
      String overRidePriceError; 
    %>   
    <tr>
      <html:form action="/PriceByServiceEdit.do">  
      <input type="hidden" name="key" value="<bean:write name="serviceDictionaryForm" property="key" />" >
      <html:hidden property="row" value="<%=attRow%>" />           
      <html:hidden property="ctkey" value="<%=attCtKey%>" />
      <html:hidden property="ptkey" value="<%=attPtKey%>" />

      <%
        ++i;      
        overRidePriceError = overRidePriceBase + i;
      %>   
    <tr align="left">
      <logic:iterate id="priceByServiceForm"
                     name="priceByServiceHeadForm"
                     property="priceByServiceForm"
                     scope="session"
                     type="com.wolmerica.pricebyservice.PriceByServiceForm">          
    <tr align="left">
      <td>
        <input type="radio" name="price" value="<bean:write name="priceByServiceForm" property="computedPrice" />" checked onclick="set_parent_value(<%=id%>, '<bean:write name="priceByServiceForm" property="computedPrice" />')">
        <strong>Current</strong>
      </td>
      <td>
        <input type="radio" name="price" value="<bean:write name="priceByServiceForm" property="computedPrice" />" onclick="set_parent_value(<%=id%>, '<bean:write name="priceByServiceForm" property="previousPrice" />')">
        <strong>Previous</strong>
      </td>
      <td>
        <input type="radio" name="price" value="<bean:write name="priceByServiceForm" property="overRidePrice" />" onclick="set_parent_value(<%=id%>, '<bean:write name="priceByServiceForm" property="overRidePrice" />')">
        <strong>Over-Ride</strong>
      </td>
    </tr>          
    <tr align="left" class="<%=currentColor%>" >
      <td>
         <bean:message key="app.localeCurrency" />
         <html:text name="priceByServiceForm" indexed="true" property="computedPrice" size="4" readonly="true" />      
      </td>
      <td>
         <bean:message key="app.localeCurrency" />
         <html:text name="priceByServiceForm" indexed="true" property="previousPrice" size="4" readonly="true" />      
      </td>          
      <td>
         <bean:message key="app.localeCurrency" />
         <html:text name="priceByServiceForm" indexed="true" property="overRidePrice" size="4" readonly="<%=attDisableEdit%>" />
         <html:errors property="<%=overRidePriceError%>" />             
      </td> 
      </logic:iterate>         
      <td>
        <html:submit value="Save" disabled="<%=attDisableEdit%>" />  
      </td>
    </tr>
    </html:form>    
  </table> 
