<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script> 

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan = "10">
      <html:form action="VendorInvoiceReportList.do">
        <html:hidden property="vendorKey" />
        <html:hidden property="vendorName" />
        <strong><bean:message key="app.vendorinvoicereport.vendor" />:</strong>
        <span class="ac_holder">
        <input id="vendorNameAC" size="30" maxlength="30" value="<bean:write name="vendorInvoiceReportListHeadForm" property="vendorName" />" onFocus="javascript:
        var options = {
		script:'VendorLookUp.do?json=true&',
		varname:'nameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.vendorInvoiceReportListHeadForm.vendorKey.value=obj.id; document.vendorInvoiceReportListHeadForm.vendorName.value=obj.value; }
		};
		var json=new AutoComplete('vendorNameAC',options);return true;" value="" />
        </span>      
        &nbsp;&nbsp;&nbsp;&nbsp;
        <strong><bean:message key="app.vendorinvoicereport.dateRange" />:</strong>
        <html:text property="fromDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('vendorInvoiceReportListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
        <html:errors property="fromDate" />
        <strong>&nbsp;<bean:message key="app.vendorinvoicereport.toDate" />&nbsp;</strong>
        <html:text property="toDate" size="10" maxlength="10" />
        <a href="javascript:show_calendar('vendorInvoiceReportListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
        <html:errors property="toDate" />
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="submit" value="<bean:message key="app.runIt" />">
      </html:form>
    </td>
  </tr>  
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>  
  <tr align="left">
    <th align="right"><bean:message key="app.vendorinvoicereport.invoiceDate" /></th>    
    <th><bean:message key="app.vendorinvoicereport.vendorName" /></th>
    <th><bean:message key="app.vendorinvoicereport.invoiceNumber" /></th>
    <th align="right"><bean:message key="app.vendorinvoicereport.subTotal" /></th>
    <th align="right"><bean:message key="app.vendorinvoicereport.salesTaxCost" /></th>    
    <th align="right"><bean:message key="app.vendorinvoicereport.handlingCost" /></th>
    <th align="right"><bean:message key="app.vendorinvoicereport.invoiceTotal" /></th>
  </tr>     
  <tr>
    <td colspan="10">
      <hr>
    </td>
  </tr>
<logic:notEqual name="vendorInvoiceReportListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the vendor invoices -->      
  <logic:iterate id="vendorInvoiceReportForm"
                 name="vendorInvoiceReportListHeadForm"
                 property="vendorInvoiceReportForm"
                 scope="session"
                 type="com.wolmerica.vendorinvoicereport.VendorInvoiceReportForm">
            
    <tr align="left" class="ODD">
      <td align="right">
        <bean:write name="vendorInvoiceReportForm" property="invoiceDate" />
      </td>
      <td>
        <bean:write name="vendorInvoiceReportForm" property="vendorName" />
      </td>      
      <td>
        <bean:write name="vendorInvoiceReportForm" property="invoiceNumber" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="vendorInvoiceReportForm" property="subTotal" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="vendorInvoiceReportForm" property="salesTaxCost" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="vendorInvoiceReportForm" property="handlingCost" />
      </td>
      <td align="right">
        <bean:message key="app.localeCurrency" />      
        <bean:write name="vendorInvoiceReportForm" property="invoiceTotal" />
      </td>
    </tr>  
    <logic:notEqual name="vendorInvoiceReportForm" property="noteLine1" value="">     
      <tr align="left" class="ODD">
        <td></td>
        <td colspan="9">
          <bean:message key="app.vendorinvoice.noteLine1" />&nbsp;&nbsp;
          <bean:write name="vendorInvoiceReportForm" property="noteLine1" />
        </td>
      </tr>      
    </logic:notEqual>
    
    <logic:notEqual name="vendorInvoiceReportDetailListHeadForm" property="lastRecord" value="0"> 
      <logic:iterate id="vendorInvoiceReportDetailForm"
                 name="vendorInvoiceReportDetailListHeadForm"
                 property="vendorInvoiceReportDetailForm"
                 scope="session"
                 type="com.wolmerica.vendorinvoicereportdetail.VendorInvoiceReportDetailForm">  
        <% if (vendorInvoiceReportForm.getVendorInvoiceKey().compareToIgnoreCase(vendorInvoiceReportDetailForm.getVendorInvoiceKey()) == 0) { %>
          <tr align="left" class="EVEN">              
            <td align="right">
              <logic:notEqual name="vendorInvoiceReportDetailForm" property="lineDetailNumber" value="">
                <bean:message key="app.vendorinvoicereport.item" />
              </logic:notEqual>  
              <logic:equal name="vendorInvoiceReportDetailForm" property="lineDetailNumber" value="">
                <bean:message key="app.vendorinvoicereport.service" />
              </logic:equal>                
            </td>
            <td>
              <bean:write name="vendorInvoiceReportDetailForm" property="lineDetailName" />       
            </td>              
            <td>
              <bean:write name="vendorInvoiceReportDetailForm" property="lineDetailSize" />
              <bean:write name="vendorInvoiceReportDetailForm" property="lineDetailUnit" />
            </td>         
            <td align="right">
              <bean:write name="vendorInvoiceReportDetailForm" property="orderQty" />
            </td>
            <td align="right">
              <bean:message key="app.localeCurrency" />      
              <bean:write name="vendorInvoiceReportDetailForm" property="thePrice" />
            </td>            
            <td align="right">
              <bean:message key="app.localeCurrency" />
              <bean:write name="vendorInvoiceReportDetailForm" property="theDiscount" />
            </td>
            <td align="right">
              <bean:message key="app.localeCurrency" />      
              <bean:write name="vendorInvoiceReportDetailForm" property="extendPrice" />
            </td>      
          </tr>
          <logic:notEqual name="vendorInvoiceReportDetailForm" property="noteLine1" value="">     
            <tr align="left" class="EVEN">
              <td></td>
              <td colspan="9">
                <bean:message key="app.vendorinvoiceitem.noteLine1" />&nbsp;&nbsp;                  
                <bean:write name="vendorInvoiceReportDetailForm" property="noteLine1" />            
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
      <bean:write name="vendorInvoiceReportListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="vendorInvoiceReportListHeadForm" property="recordCount" />      
    </td>
    <td>
      <a href="#top">Top&nbsp;<img border="0" src="./images/arrowtop.gif" align="absmiddle" width="16" height="15"></a>
    </td>    
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="vendorInvoiceReportListHeadForm" property="subTotal" />
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="vendorInvoiceReportListHeadForm" property="salesTaxCost" />
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="vendorInvoiceReportListHeadForm" property="handlingCost" />
    </td>
    <td align="right">
      <bean:message key="app.localeCurrency" />      
      <bean:write name="vendorInvoiceReportListHeadForm" property="invoiceTotal" />
    </td>      
  </tr>
</table>