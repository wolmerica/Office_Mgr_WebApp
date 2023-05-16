<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script> 

<%  
  boolean attDisableCustomerLookUp = true;
  if (request.getSession().getAttribute("MULTIACCT") != null) {
     if (request.getSession().getAttribute("MULTIACCT").toString().compareToIgnoreCase("true") == 0) {
       attDisableCustomerLookUp = false;
     }
  }  
%>   

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan = "10">
      <form action="CustomerInvoiceReportList.do" name="customerInvoiceReportListHeadForm" method="post">
        <input type="hidden" name="customerKey" value="<bean:write name="customerInvoiceReportListHeadForm" property="customerKey" />">
        <logic:notEqual name="customerInvoiceReportListHeadForm" property="sourceTypeKey" value="">
          <input type="hidden" name="sourceTypeKey" value="<bean:write name="customerInvoiceReportListHeadForm" property="sourceTypeKey" />">
        </logic:notEqual>
        <logic:notEqual name="customerInvoiceReportListHeadForm" property="sourceKey" value="">
          <input type="hidden" name="sourceKey" value="<bean:write name="customerInvoiceReportListHeadForm" property="sourceKey" />">        
        </logic:notEqual>
        <strong><bean:message key="app.customerinvoicereport.customer" />:</strong>
        <input type="text" name="clientName" value="<bean:write name="customerInvoiceReportListHeadForm" property="clientName" />" size="40" maxlength="40" readonly="true">
        <% if (!(attDisableCustomerLookUp)) { %>
          <a href="blank" onclick="popup = putinpopup('CustomerLookUp.do'); return false" target="_blank"><img src="./images/prev.gif" width="16" height="16" border="0" title="Click here to pick a customer."></a>
        <% } %>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.customerinvoicereport.dateRange" />:</strong>
        <input type="text" name="fromDate" value="<bean:write name="customerInvoiceReportListHeadForm" property="fromDate" />" size="10" maxlength="10" readonly="true" >
        <a href="javascript:show_calendar('customerInvoiceReportListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <strong>&nbsp;<bean:message key="app.customerinvoicereport.toDate" />&nbsp;</strong>
        <input type="text" name="toDate" value="<bean:write name="customerInvoiceReportListHeadForm" property="toDate" />" size="10" maxlength="10" readonly="true" >
        <a href="javascript:show_calendar('customerInvoiceReportListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </form>                
    </td>
  </tr>  
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>  
  <tr align="left">
    <th align="right"><bean:message key="app.customerinvoicereport.dateOfService" /></th>    
    <th><bean:message key="app.customerinvoicereport.clientName" /></th>
    <th><bean:message key="app.customerinvoicereport.customerInvoiceNumber" /></th>
    <th align="right"><bean:message key="app.customerinvoicereport.itemNetAmount" /></th>
    <th align="right"><bean:message key="app.customerinvoicereport.salesTaxCost" /></th>    
    <th align="right"><bean:message key="app.customerinvoicereport.serviceNetAmount" /></th>
    <th align="right"><bean:message key="app.customerinvoicereport.serviceTaxCost" /></th>
    <th align="right"><bean:message key="app.customerinvoicereport.handlingCost" /></th>
    <th align="right"><bean:message key="app.customerinvoicereport.invoiceTotal" /></th>
  </tr>     
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
<logic:notEqual name="customerInvoiceReportListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the customer invoices -->      
  <logic:iterate id="customerInvoiceReportForm"
                 name="customerInvoiceReportListHeadForm"
                 property="customerInvoiceReportForm"
                 scope="session"
                 type="com.wolmerica.customerinvoicereport.CustomerInvoiceReportForm">
            
    <tr align="left" class="ODD">
      <td align="right">
        <bean:write name="customerInvoiceReportForm" property="dateOfService" />
      </td>
      <td>
        <bean:write name="customerInvoiceReportForm" property="clientName" />
        <logic:notEqual name="customerInvoiceReportForm" property="attributeToName" value="">        
          <i>&nbsp;<bean:message key="app.customerinvoicereport.regarding" /><bean:write name="customerInvoiceReportForm" property="attributeToName" /></i>
        </logic:notEqual>
      </td>      
      <td>
        <bean:write name="customerInvoiceReportForm" property="customerInvoiceNumber" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="customerInvoiceReportForm" property="itemNetAmount" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="customerInvoiceReportForm" property="salesTaxCost" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="customerInvoiceReportForm" property="serviceNetAmount" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="customerInvoiceReportForm" property="serviceTaxCost" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="customerInvoiceReportForm" property="handlingCost" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="customerInvoiceReportForm" property="invoiceTotal" />
      </td>
    </tr>  
    <logic:notEqual name="customerInvoiceReportForm" property="noteLine1" value="">     
      <tr align="left" class="ODD">
        <td></td>
        <td colspan="9">
          <bean:message key="app.customerinvoice.noteLine1" />&nbsp;&nbsp;
          <bean:write name="customerInvoiceReportForm" property="noteLine1" />
        </td>
      </tr>      
    </logic:notEqual>
    <logic:notEqual name="customerInvoiceReportForm" property="noteLine2" value="">
      <tr align="left" class="ODD">
        <td></td>
        <td colspan="9">
          <bean:message key="app.customerinvoice.noteLine2" />&nbsp;&nbsp;
          <bean:write name="customerInvoiceReportForm" property="noteLine2" />
        </td>
      </tr>
    </logic:notEqual>    
    <logic:notEqual name="customerInvoiceReportForm" property="noteLine3" value="">
      <tr align="left" class="ODD">
        <td></td>
        <td colspan="9">
          <bean:message key="app.customerinvoice.noteLine3" />&nbsp;&nbsp;
          <bean:write name="customerInvoiceReportForm" property="noteLine3" />
        </td>
      </tr>
    </logic:notEqual>  
    
    <logic:notEqual name="customerInvoiceReportDetailListHeadForm" property="lastRecord" value="0"> 
      <logic:iterate id="customerInvoiceReportDetailForm"
                 name="customerInvoiceReportDetailListHeadForm"
                 property="customerInvoiceReportDetailForm"
                 scope="session"
                 type="com.wolmerica.customerinvoicereportdetail.CustomerInvoiceReportDetailForm">  
        <% if (customerInvoiceReportForm.getCustomerInvoiceKey().compareToIgnoreCase(customerInvoiceReportDetailForm.getCustomerInvoiceKey()) == 0) { %>
          <tr align="left" class="EVEN">              
            <td align="right">
              <logic:notEqual name="customerInvoiceReportDetailForm" property="lineDetailNumber" value="">
                <bean:message key="app.customerinvoicereport.item" />
              </logic:notEqual>  
              <logic:equal name="customerInvoiceReportDetailForm" property="lineDetailNumber" value="">
                <bean:message key="app.customerinvoicereport.service" />
              </logic:equal>                
            </td>
            <td>
              <bean:write name="customerInvoiceReportDetailForm" property="lineDetailName" />       
            </td>              
            <td>
                <logic:notEqual name="customerInvoiceReportDetailForm" property="lineDetailNumber" value="">
                    <bean:write name="customerInvoiceReportDetailForm" property="lineDetailSize" />
                    <bean:write name="customerInvoiceReportDetailForm" property="lineDetailUnit" />
                </logic:notEqual>
                <logic:equal name="customerInvoiceReportDetailForm" property="lineDetailNumber" value="">
                    <bean:write name="customerInvoiceReportDetailForm" property="lineDetailUnit" />
                </logic:equal>                
            </td>                  
            <td align="right">
              <bean:write name="customerInvoiceReportDetailForm" property="orderQty" />
            </td>                  
            <td align="right">
              <bean:message key="app.localeCurrency" />      
              <bean:write name="customerInvoiceReportDetailForm" property="thePrice" />
            </td>
            <td align="right">
            </td>            
            <td align="right">
              <bean:message key="app.localeCurrency" />      
              <bean:write name="customerInvoiceReportDetailForm" property="discountAmount" />
            </td>
            <td align="right">
            </td>
            <td align="right">
              <bean:message key="app.localeCurrency" />      
              <bean:write name="customerInvoiceReportDetailForm" property="extendPrice" />
            </td>      
          </tr>
          <logic:notEqual name="customerInvoiceReportDetailForm" property="noteLine1" value="">     
            <tr align="left" class="EVEN">
              <td></td>
              <td colspan="9">
                <bean:message key="app.customerinvoiceitem.noteLine1" />&nbsp;&nbsp;                  
                <bean:write name="customerInvoiceReportDetailForm" property="noteLine1" />            
              </td>
            </tr>      
          </logic:notEqual>          
        <% } %>
      </logic:iterate> 
    </logic:notEqual>                      
    
  </logic:iterate> 
</logic:notEqual> 
  <tr>
    <td colspan="10">
      <hr>
    </td>      
  </tr>
  <tr>
    <td colspan="2" align="center">
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="customerInvoiceReportListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="customerInvoiceReportListHeadForm" property="recordCount" />      
    </td>
    <td>
      <a href="#top">Top&nbsp;<img border="0" src="./images/arrowtop.gif" align="absmiddle" width="16" height="15"></a>
    </td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="customerInvoiceReportListHeadForm" property="itemNetAmount" />
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="customerInvoiceReportListHeadForm" property="salesTaxCost" />
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="customerInvoiceReportListHeadForm" property="serviceNetAmount" />
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="customerInvoiceReportListHeadForm" property="serviceTaxCost" />
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="customerInvoiceReportListHeadForm" property="handlingCost" />
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="customerInvoiceReportListHeadForm" property="invoiceTotal" />
    </td>      
  </tr>
</table>