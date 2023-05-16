<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>


<style type="text/css">
.menu1{
background-image: url(images/menudiv1bg.gif);
margin-left: 25px;
padding-left: 20px;
padding-top: 2px;
padding-bottom: 2px;
display: block;
text-decoration: none;
color: <bean:message key="app.menu.fontColor" />;
height: 20px;
border-style: groove;
}
.submenu{
background-image: url(images/submenu.gif);
display: block;
height: 19px;
margin-left: 38px;
padding-top: 2px;
padding-left: 15px;
color: <bean:message key="app.submenu.fontColor" />;
border-style: groove;
}
.hide{
display: none;
}

.show{
display: block;
}
</style>

<script language="JavaScript" type="text/JavaScript">
<!--
menu_status = new Array();
function showHide(theid){
    if (document.getElementById) {
    var switch_id = document.getElementById(theid);
        if(menu_status[theid] != 'show') {
           switch_id.className = 'show';
           menu_status[theid] = 'show';
        }else{
           switch_id.className = 'hide';
           menu_status[theid] = 'hide';
        }
    }
}
//-->
</script>


<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td valign="top" align="left">
Click a menu heading to expand the options:
<div style="width:240px;">
<a href="#" class="menu1" onclick="showHide('mymenu1')">Support</a>
    <div id="mymenu1" class="hide">
        <a href="#" class="submenu" onclick="popup = helppopup('HelpList.do'); return false" target="_HELP">Help Contents</a>
        <a href="LogGet.do" class="submenu">Application Logs</a>        
        <a href="AttachmentVerify.do" class="submenu">Audit Attachments</a>
        <a href="UserStateList.do" class="submenu">Who's Logged On?</a>        
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu2')">Scheduling</a>
    <div id="mymenu2" class="hide">
        <a href="ScheduleListEntry.do" class="submenu">Schedule</a>
        <a href="WorkOrderListEntry.do" class="submenu">Work Order</a>
        <a href="ResourceList.do" class="submenu">Resource</a>
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu3')">Business Drivers</a>
    <div id="mymenu3" class="hide">
        <a href="EmployeeList.do" class="submenu">Employee</a>        
        <a href="ProspectList.do" class="submenu">Prospect</a>
        <a href="CustomerList.do?activeId=true" class="submenu">Customer</a>
        <a href="VendorList.do" class="submenu">Vendor</a>        
        <a href="ItemDictionaryList.do" class="submenu">Item Dictionary</a>
        <a href="ServiceDictionaryList.do" class="submenu">Service Dictionary</a>
        <a href="BundleList.do" class="submenu">Goods & Service Bundle</a>
        <a href="PromotionList.do" class="submenu">Promotion</a>
        <a href="SpeciesList.do" class="submenu">Species</a>
        <a href="BreedList.do" class="submenu">Breed</a>
        <a href="PetList.do" class="submenu">Pet</a>
        <a href="PetVacListEntry.do" class="submenu">Pet Vaccination</a>
        <a href="SystemList.do" class="submenu">System</a>
        <a href="VehicleList.do" class="submenu">Vehicle</a>
        <a href="RebateList.do" class="submenu">Rebate</a>        
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu4')">Pricing</a>
    <div id="mymenu4" class="hide">
        <a href="CustomerTypeList.do" class="submenu">Customer Type</a>
        <a href="PriceTypeList.do" class="submenu">Price Type</a>
        <a href="TaxMarkUpList.do" class="submenu">Tax & Mark-Up</a>
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu5')">Transactions</a>
    <div id="mymenu5" class="hide">
        <a href="PurchaseOrderList.do" class="submenu">Purchase Order</a>
        <a href="RebateInstanceList.do" class="submenu">Rebate Submission</a>
        <a href="VendorResultEntry.do" class="submenu">Vendor Result</a>
        <a href="VendorInvoiceList.do" class="submenu">Vendor Invoice</a>
        <a href="CustomerInvoiceList.do" class="submenu">Customer Invoice</a>
        <a href="VendorInvoiceReportEntry.do" class="submenu">Vendor Invoice Report</a>
        <a href="CustomerInvoiceReportEntry.do" class="submenu">Customer Invoice Report</a>
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu6')">Price Sheet</a>
    <div id="mymenu6" class="hide">
        <a href="PriceSheetList.do?mode=1" class="submenu">Assign To Price Sheet</a>
        <a href="PriceSheetList.do?mode=2" class="submenu">Price Sheet List</a>
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu7')">Credit Ledger</a>
    <div id="mymenu7" class="hide">
        <a href="LedgerListEntry.do?mode=1" class="submenu">Daily</a>
        <a href="LedgerListEntry.do?mode=2" class="submenu">Monthly</a>
        <a href="LedgerListEntry.do?mode=3" class="submenu">Archive</a>
        <a href="LedgerListEntry.do?mode=4" class="submenu">Invoice By Customer & Date</a>
        <a href="LedgerListEntry.do?mode=6" class="submenu">Slip By Customer & Date</a>
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu8')">Bookkeeping</a>
    <div id="mymenu8" class="hide">
        <a href="CustomerAccountingListEntry.do" class="submenu">Customer Accounting</a>
        <a href="VendorInvoiceDueEntry.do" class="submenu">Vendor Invoice Date</a>
        <a href="VendorAccountingListEntry.do" class="submenu">Vendor Accounting</a>
        <a href="ExpenseListEntry.do" class="submenu">Track Expenses</a>
        <a href="ExpenseSummaryListEntry.do" class="submenu">Expense Summary</a>
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu9')">Item Reports</a>
    <div id="mymenu9" class="hide">
        <a href="ItemPurchaseDetailEntry.do" class="submenu">Item Purchase Detail</a>
        <a href="ItemPurchaseSummaryEntry.do" class="submenu">Item Purchase Summary</a>
        <a href="ItemSaleDetailEntry.do" class="submenu">Item Sale Detail</a>
        <a href="ItemSaleSummaryEntry.do" class="submenu">Item Sale Summary</a>
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu10')">Service Reports</a>
    <div id="mymenu10" class="hide">
        <a href="ServiceLaborDetailEntry.do" class="submenu">Service Labor Detail</a>
        <a href="ServiceLaborSummaryEntry.do" class="submenu">Service Labor Summary</a>
        <a href="ServiceSaleDetailEntry.do" class="submenu">Service Sale Detail</a>
        <a href="ServiceSaleSummaryEntry.do" class="submenu">Service Sale Summary</a>
    </div>
<a href="#" class="menu1" onclick="showHide('mymenu11')">PDF Reports</a>
    <div id="mymenu11" class="hide">
        <a href="#" class="submenu" onclick="popup = putinpopup('PdfItemBlueBook.do?presentationType=pdf'); return false" target="_PDF">Item Blue Book</a>
        <a href="#" class="submenu" onclick="popup = putinpopup('PdfServiceBlueBook.do?presentationType=pdf'); return false" target="_PDF">Service Blue Book</a>
        <a href="#" class="submenu" onclick="popup = putinpopup('PdfItemInventory.do?presentationType=pdf'); return false" target="_PDF">Inventory List</a>
    </div>
</div>
    </td>
    <td valign="top" align="left">
       
      <br>        
<!-- Upcoming events to take note of --> 
<%
  String currentColor = "ODD";
  int i = 0;
  int startHour = 0;
  int startMinute = 0;
  String timeOfDay = "AM";
  boolean attAppointment = false;  
  boolean attShowInfoIcon = false;
%> 

      <table width="300" border="1" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
        <tr>
          <th><bean:message key="app.schedule.startDate" /></th>
          <th align="right"><bean:message key="app.schedule.startTime" /></th>
          <th><bean:message key="app.schedule.subject" /></th>
        </tr>
<logic:notEqual name="scheduleListHeadForm" property="lastRecord" value="0"> 
  <logic:iterate id="scheduleForm"
                 name="scheduleListHeadForm"
                 property="scheduleForm"
                 scope="session"
                 type="com.wolmerica.schedule.ScheduleForm">                 
        <%     
          if ( i % 2 == 0)
             currentColor = "ODD";
          else
             currentColor = "EVEN";
          i++;
        %>   

        <% attAppointment = false;
           attShowInfoIcon = false; %>        
        <logic:equal name="scheduleForm" property="eventTypeKey" value="0">
          <% attAppointment = true; %>    
        </logic:equal>  
        <logic:notEqual name="scheduleForm" property="examKey" value="0">
          <% attShowInfoIcon = true; %>
        </logic:notEqual>
        <logic:notEqual name="scheduleForm" property="category1Key" value="0">
          <% attShowInfoIcon = true; %>
        </logic:notEqual>
        <logic:notEqual name="scheduleForm" property="category2Key" value="0">
          <% attShowInfoIcon = true; %>
        </logic:notEqual>
        
	<tr align="left" class="<%=currentColor %>">
	  <td>
            <bean:write name="scheduleForm" property="startDate" />
	  </td>            
	  <td align="right">
            <logic:equal name="scheduleForm" property="eventTypeKey" value="1">
              <% if (!((scheduleForm.getStartHour().equalsIgnoreCase(scheduleForm.getEndHour())) &&
                (scheduleForm.getStartMinute().equalsIgnoreCase(scheduleForm.getEndMinute())))) { %>
                <bean:message key="app.calendar.closed" />&nbsp;
              <% } %>
            </logic:equal>
              
            <% startHour = new Byte(scheduleForm.getStartHour()).intValue();
               startMinute = new Byte(scheduleForm.getStartMinute()).intValue();
               timeOfDay = "am";               
               if (startHour > 11)  
                 timeOfDay = "pm";            
               if (startHour > 12)
                 startHour = startHour - 12;
            %>
            <% if (startMinute == 0) { %>
              <%=startHour%>&nbsp;<%=timeOfDay%>
            <% } else { %>
              <%=startHour%>:<bean:write name="scheduleForm" property="startMinute" />&nbsp;<%=timeOfDay%>
            <% } %>  
	  </td>	  
	  <td>
            <logic:equal name="scheduleForm" property="eventTypeKey" value="1"> 
              <img src="./images/holiday.jpg" height="15" width="15" border="0" title="<bean:message key="app.schedule.holiday" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="eventTypeKey" value="2"> 
              <img src="./images/vacation.jpg" height="15" width="15" border="0" title="<bean:message key="app.schedule.vacation" />" >                  
            </logic:equal>            
            <logic:equal name="scheduleForm" property="eventTypeKey" value="3"> 
              <img src="./images/birthday.gif" height="15" width="15" border="0" title="<bean:message key="app.schedule.birthday" />" >                  
            </logic:equal>
            <logic:equal name="scheduleForm" property="eventTypeKey" value="4"> 
             <img src="./images/anniversary.jpg" height="18" width="18" border="0" title="<bean:message key="app.schedule.anniversary" />" >                  
            </logic:equal>                  
            <logic:equal name="scheduleForm" property="eventTypeKey" value="5">             
              <img src="./images/reminder.gif" height="15" width="15" border="0" title="<bean:message key="app.schedule.reminder" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="category1Id" value="true"> 
              <a href="#" onclick="popup = putinpopup('PdfBoardingForm.do?key=<bean:write name="scheduleForm" 
	        property="key" />&presentationType=pdf'); return false" target="_PDF"> <img src="./images/boarding.gif" width="20" height="20" border="0" title="<bean:message key="app.schedule.boarding" />" ></a>
            </logic:equal>
            <logic:equal name="scheduleForm" property="category2Id" value="true"> 
              <img src="./images/needle.gif" width="20" height="20" border="0" title="<bean:message key="app.schedule.vaccination" />" >
            </logic:equal>            
            <logic:equal name="scheduleForm" property="category3Id" value="true"> 
              <img src="./images/surgery.gif" width="18" height="18" border="0" title="<bean:message key="app.schedule.surgery" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="category4Id" value="true"> 
              <img src="./images/grooming.gif" width="20" height="20" border="0" title="<bean:message key="app.schedule.grooming" />" >
            </logic:equal>
            <logic:equal name="scheduleForm" property="locationId" value="false"> 
              <img src="./images/truck.jpg" width="16" height="16" border="0" title="<bean:message key="app.schedule.offSite" />" >
            </logic:equal>
            <% if (attAppointment) { %>
              <logic:equal name="scheduleForm" property="statusKey" value="0">
                <logic:equal name="scheduleForm" property="reminderPrefKey" value="0">
                  <img src="./images/bluephone.gif" height="18" width="18" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                </logic:equal>
                <logic:equal name="scheduleForm" property="reminderPrefKey" value="1">
                  <img src="./images/mobilephone.gif" height="18" width="18" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                </logic:equal>
                <logic:equal name="scheduleForm" property="reminderPrefKey" value="2">
                  <img src="./images/email.gif" height="18" width="18" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                </logic:equal>
                <logic:equal name="scheduleForm" property="reminderPrefKey" value="3">
                  <img src="./images/sms.gif" height="18" width="18" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                </logic:equal>
              </logic:equal>            
            <% } else { %>
              <logic:equal name="scheduleForm" property="eventTypeKey" value="5"> 
                <logic:equal name="scheduleForm" property="statusKey" value="0">
                  <logic:equal name="scheduleForm" property="reminderPrefKey" value="0">
                    <img src="./images/bluephone.gif" height="20" width="20" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="reminderPrefKey" value="1">
                    <img src="./images/mobilephone.gif" height="20" width="20" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                  </logic:equal>
                  <logic:equal name="scheduleForm" property="reminderPrefKey" value="2">
                    <img src="./images/email.gif" height="20" width="20" border="0" title="<bean:message key="app.schedule.newAppointment" />" >
                  </logic:equal>                 
                </logic:equal>
              </logic:equal>            
            <% } %>            
            <logic:equal name="scheduleForm" property="statusKey" value="4">
              <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerCancel" />">
            </logic:equal>
            <logic:equal name="scheduleForm" property="statusKey" value="5">
              <img src="./images/reschedule.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.customerReschedule" />">
            </logic:equal>
            <logic:equal name="scheduleForm" property="statusKey" value="6">
              <img src="./images/cancel.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.officeCancel" />">
            </logic:equal>
            <logic:equal name="scheduleForm" property="statusKey" value="7">
              <img src="./images/reschedule.gif" height="14" width="14" border="0" title="<bean:message key="app.schedule.officeReschedule" />">
            </logic:equal>
            <logic:equal name="scheduleForm" property="releaseId" value="true">
              <logic:notEqual name="scheduleForm" property="sourceTypeKey" value="-1">
                <a href="#" onclick="popup = putinpopup('PdfReleaseForm.do?customerKey=<bean:write name="scheduleForm" 
                  property="customerKey" />&sourceTypeKey=<bean:write name="scheduleForm" 
                  property="sourceTypeKey" />&sourceKey=<bean:write name="scheduleForm"
                  property="sourceKey" />&scheduleKey=<bean:write name="scheduleForm" 
                  property="key" />&presentationType=pdf'); return false" target="_PDF"><img src="./images/pencil.gif" height="16" width="16" border="0" title="<bean:message key="app.schedule.releaseRequired" />"></a>
              </logic:notEqual>                  
            </logic:equal>
            <% if (attShowInfoIcon) { %>
              <img src="./images/info.gif" height="16" width="16" border="0" title="<bean:message key="app.schedule.examInfo" />">
            <% } %>           
            <logic:notEqual name="scheduleForm" property="customerInvoiceKey" value="">
              <img src="./images/invoice.jpg" height="20" width="20" border="0" title="<bean:message key="app.schedule.invoicedAppointment" />">
            </logic:notEqual>
            <a href="ScheduleGet.do?key=<bean:write name="scheduleForm" 
              property="key" />"><bean:write name="scheduleForm" property="subject" /></a>
            <logic:equal name="scheduleForm" property="eventTypeKey" value="0">
              <br>
              <bean:write name="scheduleForm" property="clientName" />
            </logic:equal>              
            <logic:equal name="scheduleForm" property="eventTypeKey" value="5">
              <br>
              <bean:write name="scheduleForm" property="clientName" />
            </logic:equal>              
	  </td>          
	</tr>	
  </logic:iterate> 
</logic:notEqual>
        <% if (i == 0) { %>
          <tr>
            <td colspan="3">
              <bean:message key="app.menu.noUpcomingEvents" />
            </td>
          </tr>
        <% } %>
      </table>    
    </td>    
    <td valign="top" align="left">
      <br>
<!-- Outstanding vendor bills -->    
<% i = 0; %>
      <table width="200" border="1" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
        <tr>
          <th><bean:message key="app.vendorinvoice.vendorName" /></th>
          <th align="right"><bean:message key="app.vendor.accountBalanceAmount" /></th>              
        </tr>
<logic:notEqual name="vendorListHeadForm" property="lastRecord" value="0">      
  <logic:iterate id="vendorListForm"
                 name="vendorListHeadForm"
                 property="vendorListForm"
                 scope="session"
                 type="com.wolmerica.vendor.VendorListForm">
    <logic:notEqual name="vendorListForm" property="accountBalanceAmount" value="0.00">
      <%
        if ( i % 2 == 0)
           currentColor = "ODD"; 
        else
           currentColor = "EVEN";
        i++;
      %>  
      <tr align="left" class="<%=currentColor %>">
	<td>
          <a href="VendorGet.do?tabId=accountTab&key=<bean:write name="vendorListForm"
            property="key" />"><bean:write name="vendorListForm" property="name" /></a>
	</td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="vendorListForm" property="accountBalanceAmount" />          
        </td>          
      </tr> 
    </logic:notEqual>         
  </logic:iterate> 
</logic:notEqual>  
        <% if (i == 0) { %>
          <tr>
            <td colspan="2">
              <bean:message key="app.menu.noVendorBalances" />
            </td>
          </tr>
        <% } %>
      </table>            
      <br>  
<!-- Outstanding customer payments  -->   
<% i = 0; %>
      <table width="200" border="1" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
        <tr>
          <th><bean:message key="app.customer.clientName" /></th>
          <th align="right"><bean:message key="app.customer.accountBalanceAmount" /></th>  
        </tr>
<logic:notEqual name="customerListHeadForm" property="lastRecord" value="0"> 
  <logic:iterate id="customerListForm"
                 name="customerListHeadForm"
                 property="customerListForm"
                 scope="session"
                 type="com.wolmerica.customer.CustomerListForm">
    <logic:notEqual name="customerListForm" property="accountBalanceAmount" value="0.00">
      <%
        if ( i % 2 == 0)
          currentColor = "ODD";
        else
          currentColor = "EVEN";
        i++;
      %>
      <tr align="left" class="<%=currentColor %>">
        <td>
          <a href="CustomerGet.do?tabId=accountTab&key=<bean:write name="customerListForm"
            property="key" />"><bean:write name="customerListForm" property="clientName" /></a>
	</td>
        <td align="right">
          <bean:message key="app.localeCurrency" />
          <bean:write name="customerListForm" property="accountBalanceAmount" />
        </td>          
      </tr>	
    </logic:notEqual>        
  </logic:iterate> 
</logic:notEqual>
        <% if (i == 0) { %>
          <tr>
            <td colspan="2">
              <bean:message key="app.menu.noCustomerBalances" />
            </td>
          </tr>
        <% } %>
      </table>
    </td>
  </tr>
</table>
