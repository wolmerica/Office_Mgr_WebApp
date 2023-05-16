<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<%
  String currentColor = "ODD";
  int i = 0;
%>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script type="text/javascript" src="./js/date_picker.js"></script> 

<style type="text/css">
.collapsible {
          display: none; /* Only important part */
          border: dashed 1px silver;
          padding: 10px;
}
</style>

<script type="text/javascript" language="JavaScript"><!--
//preload image
var collimg = new Image();
collimg.src = "./images/collapse.gif";
var expimg = new Image();
collimg.src = "./images/expand.gif";

function ShowHideLayer(boxID) {
  /* Obtain reference for the selected boxID layer and its button */
  var box = document.getElementById("box"+boxID);
  var boxbtn = document.getElementById("btn"+boxID);

  /* If the selected box is currently invisible, show it */
  if(box.style.display == "none" || box.style.display=="") {
    box.style.display = "block";
    boxbtn.src = "./images/collapse.gif";
  }
  /* otherwise hide it */
  else {
    box.style.display = "none";
    boxbtn.src = "./images/expand.gif";
  }
}

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

function defaultField(what,text) {
  if (what.value == '')
     what.value = text;
}

//--></script>

  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td colspan = "10">
        <html:form action="CustomerInvoiceReportList.do">
          <html:hidden property="customerKey" />
          <html:hidden property="clientName" />
          <logic:notEqual name="customerInvoiceReportListHeadForm" property="sourceTypeKey" value="">
            <html:hidden property="sourceTypeKey" />
          </logic:notEqual>
          <logic:notEqual name="customerInvoiceReportListHeadForm" property="sourceKey" value="">
            <html:hidden property="sourceKey" />
          </logic:notEqual>
          <strong><bean:message key="app.customerinvoicereport.customer" />:</strong>
          <span class="ac_holder">
          <input id="clientNameAC" size="40" maxlength="40" value="<bean:write name="customerInvoiceReportListHeadForm" property="clientName" />" onFocus="javascript:
          var options = {
                  script:'CustomerLookUp.do?json=true&',
                  varname:'lastNameFilter',
                  json:true,
                  shownoresults:true,
                  maxresults:10,
                  callback: function (obj) { document.customerInvoiceReportListHeadForm.customerKey.value=obj.id; document.customerInvoiceReportListHeadForm.clientName.value=obj.value; }
                  };
                  var json=new AutoComplete('clientNameAC',options);return true;" value="" />
          </span>
          &nbsp;&nbsp;&nbsp;&nbsp;
          <strong><bean:message key="app.customerinvoicereport.dateRange" />:</strong>
          <html:text property="fromDate" size="10" maxlength="10" />
          <a href="javascript:show_calendar('customerInvoiceReportListHeadForm.fromDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the from date."></a>
          <html:errors property="fromDate" />
          <strong>&nbsp;<bean:message key="app.customerinvoicereport.toDate" />&nbsp;</strong>
          <html:text property="toDate" size="10" maxlength="10" />
          <a href="javascript:show_calendar('customerInvoiceReportListHeadForm.toDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the to date."></a>
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
  <!-- Display tabs to show and hide customer content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff; font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('profitLossTab','profitLossInfo');">
              <bean:message key="app.customerinvoicereport.mainTab" />
              </a>
            </div>
          </td>
          <td align="center">
            <div id="profitLossTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('profitLossTab','profitLossInfo');">
                <bean:message key="app.customerinvoicereport.profitLossTab" />
              </a>
            </div>
          </td>
        </tr>
      </table>
    </td>
  </table>

  <div id="mainInfo" style="display:none;">
    <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
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
        <td colspan="14">
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
            <td colspan="14">
              <bean:message key="app.customerinvoice.noteLine1" />&nbsp;&nbsp;
              <bean:write name="customerInvoiceReportForm" property="noteLine1" />
            </td>
          </tr>
        </logic:notEqual>
        <logic:notEqual name="customerInvoiceReportForm" property="noteLine2" value="">
          <tr align="left" class="ODD">
            <td></td>
            <td colspan="14">
              <bean:message key="app.customerinvoice.noteLine2" />&nbsp;&nbsp;
              <bean:write name="customerInvoiceReportForm" property="noteLine2" />
            </td>
          </tr>
        </logic:notEqual>
        <logic:notEqual name="customerInvoiceReportForm" property="noteLine3" value="">
          <tr align="left" class="ODD">
            <td></td>
            <td colspan="14">
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
                  <bean:message key="app.localeCurrency" />
                  <bean:write name="customerInvoiceReportDetailForm" property="costBasis" />
                </td>
                <td align="right">
                  <bean:write name="customerInvoiceReportDetailForm" property="orderQty" />
                </td>
                <td align="right">
                  <bean:message key="app.localeCurrency" />
                  <bean:write name="customerInvoiceReportDetailForm" property="thePrice" />
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
                <td align="right">

                </td>
              </tr>
              <logic:notEqual name="customerInvoiceReportDetailForm" property="noteLine1" value="">
                <tr align="left" class="EVEN">
                  <td></td>
                  <td colspan="14">
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
        <td colspan="14">
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
  </div>

<% if (request.getParameter("tabId") == null) { %>
  <script type="text/javascript" language="JavaScript"><!--
    ShowContent('mainTab', 'mainInfo');
  //--></script>
<% } %>

  <div id="profitLossInfo" style="display:none;">
    <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
      <tr align="left">
        <th><bean:message key="app.customerinvoicereport.customerInvoiceNumber" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.itemNetAmount" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.itemDiscountAmount" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.salesTaxCost" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.serviceNetAmount" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.serviceDiscountAmount" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.serviceTaxCost" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.packagingCost" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.freightCost" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.miscellaneousCost" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.invoiceTotal" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.netProfitAmount" /></th>
        <th align="right"><bean:message key="app.customerinvoicereport.costBasisTotal" /></th>
      </tr>
      <tr>
        <td colspan="14">
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

<!-- Set values for row shading -->
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
            <bean:write name="customerInvoiceReportForm" property="customerInvoiceNumber" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="itemNetAmount" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="itemDiscountAmount" />
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
            <bean:write name="customerInvoiceReportForm" property="serviceDiscountAmount" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="serviceTaxCost" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="packagingCost" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="freightCost" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="miscellaneousCost" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="invoiceTotal" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="netProfitAmount" />
          </td>
          <td align="right">
            <bean:message key="app.localeCurrency" />
            <bean:write name="customerInvoiceReportForm" property="costBasisTotal" />
          </td>

        </tr>

      </logic:iterate>
    </logic:notEqual>
      <tr>
        <td colspan="14">
          <hr>
        </td>
      </tr>
      <tr>
        <td colspan="1" align="center">
          <strong><bean:message key="app.result.title" /></strong>&nbsp;
          <bean:write name="customerInvoiceReportListHeadForm" property="firstRecord" />
          <strong><bean:message key="app.result.thru" /></strong>
          <bean:write name="customerInvoiceReportListHeadForm" property="recordCount" />
          &nbsp;&nbsp;
          <a href="#top">Top&nbsp;<img border="0" src="./images/arrowtop.gif" align="absmiddle" width="16" height="15"></a>
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="itemNetAmount" />
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="itemDiscountAmount" />
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
          <bean:write name="customerInvoiceReportListHeadForm" property="serviceDiscountAmount" />
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="serviceTaxCost" />
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="packagingCost" />
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="freightCost" />
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="miscellaneousCost" />
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="invoiceTotal" />
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="netProfitAmount" />
        </td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerInvoiceReportListHeadForm" property="costBasisTotal" />
        </td>

      </tr>
    </table>
  </div>