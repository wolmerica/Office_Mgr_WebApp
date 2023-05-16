<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script type="text/javascript" src="js/overlib_mini.js"><!-- overLIB (c) Erik Bosrup --></script>
<script type="text/javascript" src="js/date_picker.js"></script>

<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>  

<%
  String currentColor = "ODD";
  int i = -1;
  String attTitle = "app.schedule.addTitle";
  String attAction = "/ScheduleAdd";
  boolean attKeyEdit = false;
  boolean attStatusEdit = false;
  boolean attWorkOrderEdit = false;
  boolean attThirdPartyServiceOnly = true;
  boolean attInsufficientItemQuantity = false;
  String attDisableInvoice = "";
  
  boolean attDisableEdit = true;
  if (request.getAttribute("disableEdit") != null) {
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }
  String orgsKey = "";
  if (request.getAttribute("orgsKey") != null)
    orgsKey = request.getAttribute("orgsKey").toString();
  else if (request.getParameter("copyWO") != null)
    orgsKey = request.getParameter("copyWO").toString();

  String urlParams = "?id=wow";
  String fromDate = ""; 
%> 

<br>
<logic:notEqual name="scheduleForm" property="key" value="">
  <%
    attTitle = "app.schedule.editTitle";
    attAction = "/ScheduleEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:equal name="scheduleForm" property="eventTypeKey" value="0">
  <% attStatusEdit = true; 
     attWorkOrderEdit = true; %>    
</logic:equal>
<logic:equal name="scheduleForm" property="eventTypeKey" value="1">
  <% attWorkOrderEdit = true; %>
</logic:equal>
<logic:equal name="scheduleForm" property="eventTypeKey" value="2">
  <% attWorkOrderEdit = true; %>
</logic:equal>
<logic:equal name="scheduleForm" property="eventTypeKey" value="5">
  <% attStatusEdit = true; %>    
</logic:equal>


<logic:notEqual name="scheduleForm" property="permissionStatus" value="LOCKED">
<% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="scheduleForm" property="permissionStatus" value="READONLY">
    [<bean:write name="scheduleForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>

<%
  if (request.getAttribute("key") != null)
    urlParams = urlParams + "&key=" + request.getAttribute("key").toString();
  else if (request.getParameter("key") != null)
    urlParams = urlParams + "&key=" + request.getParameter("key").toString();
  if (request.getParameter("customerKeyFilter") != null)
    urlParams = urlParams + "&customerKeyFilter=" + request.getParameter("customerKeyFilter").toString();
  if (request.getParameter("fromDate") != null) 
    fromDate = request.getParameter("fromDate").toString();
  else if (request.getParameter("startDate") != null)
    fromDate = request.getParameter("startDate").toString();
  if (!(fromDate.equalsIgnoreCase("")))
    urlParams = urlParams + "&fromDate=" + fromDate;      
  if (request.getParameter("toDate") != null) 
    urlParams = urlParams + "&toDate=" + request.getParameter("toDate").toString();
  if (request.getParameter("pageNo") != null)
    urlParams = urlParams + "&pageNo=" + request.getParameter("pageNo").toString();
%>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="4"> 
      <button onClick="window.location='ScheduleList.do<%=urlParams%>' "><bean:message key="app.schedule.backMessage" /></button>
      &nbsp;&nbsp;
      <button onClick="window.location='EventCalendarList.do<%=urlParams%>&mode=3' "><bean:message key="app.schedule.byMonth" /></button>
        <logic:notEqual name="scheduleForm" property="key" value="">          
            &nbsp;&nbsp;
            <button onClick="window.location='EventCalendarList.do?mode=2&fromDate=<bean:write name="scheduleForm" property="startDate" />' "><bean:message key="app.schedule.byWeek" /></button>
            &nbsp;&nbsp;
            <button onClick="window.location='EventCalendarList.do?mode=1&fromDate=<bean:write name="scheduleForm" property="startDate" />' "><bean:message key="app.schedule.byDay" /></button>
        </logic:notEqual>
    </td>
    <td colspan="2" align="right">  
      <logic:equal name="scheduleForm" property="category1Id" value="true"> 
        <a href="#" onclick="popup = putinpopup('PdfBoardingForm.do?key=<bean:write name="scheduleForm" 
          property="key" />&presentationType=pdf'); return false" target="_PDF">  <img src="./images/boarding.gif" width="20" height="20" border="0" title="<bean:message key="app.schedule.boarding" />" ></a>	
          &nbsp;&nbsp;&nbsp;
      </logic:equal> 
      <logic:equal name="scheduleForm" property="releaseId" value="true">
        <logic:notEqual name="scheduleForm" property="sourceKey" value="-1">
           <a href="#" onclick="popup = putinpopup('PdfReleaseForm.do?customerKey=<bean:write name="scheduleForm" 
             property="customerKey" />&sourceTypeKey=<bean:write name="scheduleForm" 
             property="sourceTypeKey" />&sourceKey=<bean:write name="scheduleForm" 
             property="sourceKey" />&scheduleKey=<bean:write name="scheduleForm" 
             property="key" />&presentationType=pdf'); return false" target="_PDF"><img src="./images/pencil.gif" height="16" width="16" border="0" title="<bean:message key="app.schedule.releaseRequired" />"></a>
        </logic:notEqual>
      </logic:equal>    
    </td>    
    <logic:notEqual name="scheduleForm" property="key" value="">
    <td colspan="2" align="right">
      <% if (orgsKey.equalsIgnoreCase("")) { %>
        <button onClick="window.location='ScheduleEntry.do<%=urlParams%>' "><bean:message key="app.schedule.replicateMessage" /></button>      
      <% } else { %>        
        <button onClick="window.location='WorkOrderAddToCart.do<%=urlParams%>&orgsKey=<%=orgsKey%>' "><bean:message key="app.schedule.copyWorkOrderMessage" /></button>
      <% } %>
    </td>
    </logic:notEqual>     
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
</table>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >     
  <html:form action="<%=attAction%>"> 
  <logic:notEqual name="scheduleForm" property="key" value="">
    <html:hidden property="key" />  
  </logic:notEqual>
  <% if (request.getAttribute("orgsKey") != null) { %>
    <input type="hidden" name="copyWO" value="<%=request.getAttribute("orgsKey")%>">
  <% } %>  
  <% if (request.getParameter("customerKeyFilter") != null) { %>
    <input type="hidden" name="customerKeyFilter" value="<%=request.getParameter("customerKeyFilter")%>">
  <% } %>
  <% if (!(fromDate.equalsIgnoreCase(""))) { %>
    <input type="hidden" name="fromDate" value="<%=fromDate%>">
  <% } %>
  <% if (request.getParameter("toDate") != null) { %>
    <input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>">
  <% } %>
  <% if (request.getParameter("pageNo") != null) { %>
    <input type="hidden" name="pageNo" value="<%=request.getParameter("pageNo")%>">
  <% } %>
  <% if (request.getParameter("pvKey") != null) { %>
    <input type="hidden" name="pvKey" value="<%=request.getParameter("pvKey")%>">
  <% } %>  
  <% if (request.getParameter("pbKey") != null) { %>
    <input type="hidden" name="pbKey" value="<%=request.getParameter("pbKey")%>">
  <% } %>    
  <tr>
    <td><strong><bean:message key="app.schedule.eventTypeKey" />:</strong></td>
    <td>   
      <html:select property="eventTypeKey" disabled="<%=attDisableEdit%>">
        <html:option value="0">Appointment</html:option>
        <html:option value="5">Reminder</html:option>        
        <html:option value="4">Anniversary</html:option>
        <html:option value="3">Birthday</html:option>
        <html:option value="1">Holiday</html:option> 
        <html:option value="2">Vacation</html:option>
      </html:select>    
      <html:errors property="eventTypeKey" />
    </td>
    <td><strong><bean:message key="app.schedule.locationId" />:</strong></td>
    <td>   
      <html:select property="locationId" disabled="<%=attDisableEdit%>">
        <html:option value="true">On-Site</html:option>
        <html:option value="false">Off-Site</html:option>        
      </html:select>    
      <html:errors property="locationId" />
    </td>    
    <logic:notEqual name="scheduleForm" property="key" value="">
      <% if (attStatusEdit) { %>    
        <td><strong><bean:message key="app.schedule.statusKey" />:</strong></td>
        <td>     
          <html:select property="statusKey" disabled="<%=attDisableEdit%>">
            <html:option value="0">Notify Customer</html:option>
            <html:option value="2">Pending</html:option>
            <html:option value="3">In Progress</html:option>
            <html:option value="4">Customer Cancel</html:option>
            <html:option value="5">Customer Reschedule</html:option>        
            <html:option value="6">Office Cancel</html:option>
            <html:option value="7">Office Reschedule</html:option>
            <html:option value="8">Complete</html:option>
          </html:select>    
          <html:errors property="statusKey" />
        </td>
      <% } %>    
    </logic:notEqual>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.schedule.subject" />:</strong></td>
    <td colspan="3">
      <html:text property="subject" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />              
      <html:errors property="subject" />
    </td>   
    <td><strong><bean:message key="app.schedule.startDate" /></strong></td>
    <td>
      <html:text property="startDate" size="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>            
        <a href="javascript:show_calendar('scheduleForm.startDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the invoice due date."></a>
      <% } %>
      <html:errors property="startDate" />             
    </td>    
  </tr>
  <tr>
    <td>
      <logic:notEqual name="scheduleForm" property="key" value="">
        <strong><a href="CustomerGet.do?key=<bean:write name="scheduleForm"
          property="customerKey" />"><bean:message key="app.schedule.clientName" /></a>:</strong>
      </logic:notEqual>
      <logic:equal name="scheduleForm" property="key" value="">  
        <strong><bean:message key="app.schedule.clientName" />:</strong>
      </logic:equal>
    </td>
    <td colspan="3">
      <html:hidden property="customerKey" />
      <html:hidden property="clientName" />
      <span class="ac_holder">
      <input id="clientNameAC" size="40" maxlength="40" value="<bean:write name="scheduleForm" property="clientName" />" onFocus="javascript:
      var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.scheduleForm.customerKey.value=obj.id; document.scheduleForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
      </span>
      <html:errors property="customerKey" />
    </td> 
    <td><strong><bean:message key="app.schedule.startTime" /></strong></td>
    <td>
      <html:select property="startHour" disabled="<%=attDisableEdit%>">      
        <html:option value="8">8 am</html:option>
        <html:option value="9">9 am</html:option>
        <html:option value="10">10 am</html:option>
        <html:option value="11">11 am</html:option>
        <html:option value="12">12 pm</html:option>
        <html:option value="13">1 pm</html:option>
        <html:option value="14">2 pm</html:option>
        <html:option value="15">3 pm</html:option>
        <html:option value="16">4 pm</html:option>
        <html:option value="17">5 pm</html:option>
        <html:option value="18">6 pm</html:option>
        <html:option value="19">7 pm</html:option>
        <html:option value="20">8 pm</html:option>
        <html:option value="21">9 pm</html:option>        
      </html:select>
      &nbsp;&nbsp;      
      <html:select property="startMinute" disabled="<%=attDisableEdit%>">      
        <html:option value="0">:00</html:option>
        <html:option value="15">:15</html:option>
        <html:option value="30">:30</html:option>
        <html:option value="45">:45</html:option>
      </html:select>
    </td>
  </tr>
  <tr>
    <td>
      <html:hidden property="sourceTypeKey" />
      <html:hidden property="sourceKey" />
      <html:hidden property="attributeToName" />
      <logic:notEqual name="scheduleForm" property="key" value="">
        <strong><bean:message key="app.schedule.attributeToName" />&nbsp;</strong>
        <logic:equal name="scheduleForm" property="attributeToEntity" value="Pet">
          <logic:notEqual name="scheduleForm" property="sourceTypeKey" value="-1">
            <strong><a href="PetGet.do?key=<bean:write name="scheduleForm"
              property="sourceKey" />&sourceTypeKey=<bean:write name="scheduleForm"
              property="sourceTypeKey" />"><bean:message key="app.employee.featurePet" /></a>:</strong>
          </logic:notEqual>
          <logic:equal name="scheduleForm" property="sourceTypeKey" value="-1">
            <strong><bean:message key="app.employee.featurePet" />:</strong>
          </logic:equal>        
        </logic:equal>        
        <logic:equal name="scheduleForm" property="attributeToEntity" value="System">
          <logic:notEqual name="scheduleForm" property="sourceTypeKey" value="-1">
            <strong><a href="SystemGet.do?key=<bean:write name="scheduleForm"
              property="sourceKey" />&sourceTypeKey=<bean:write name="scheduleForm"
              property="sourceTypeKey" />"><bean:message key="app.employee.featureSystem" /></a>:</strong>
          </logic:notEqual>
          <logic:equal name="scheduleForm" property="sourceTypeKey" value="-1">
            <strong><bean:message key="app.employee.featureSystem" />:</strong>
          </logic:equal>
        </logic:equal>
        <logic:equal name="scheduleForm" property="attributeToEntity" value="Vehicle">
          <logic:notEqual name="scheduleForm" property="sourceTypeKey" value="-1">
            <strong><a href="VehicleGet.do?key=<bean:write name="scheduleForm"
              property="sourceKey" />&sourceTypeKey=<bean:write name="scheduleForm"
              property="sourceTypeKey" />"><bean:message key="app.employee.featureVehicle" /></a>:</strong>
          </logic:notEqual>
          <logic:equal name="scheduleForm" property="sourceTypeKey" value="-1">
            <strong><bean:message key="app.employee.featureVehicle" />:</strong>
          </logic:equal>        
        </logic:equal>
      </logic:notEqual>        
    </td>
    <td colspan="3">
      <logic:notEqual name="scheduleForm" property="key" value="">    
        <% if (attDisableEdit) { %>      
          <html:text property="attributeToName" size="40" maxlength="40" readonly="true" />
        <% } else { %>
          <logic:equal name="scheduleForm" property="attributeToEntity" value="Pet">
            <span class="ac_holder">
            <input id="attributeToNameAC" size="40" maxlength="40" value="<bean:write name="scheduleForm" property="attributeToName" />" onFocus="javascript:
            var options = {
		script:'PetLookUp.do?json=true&customerKeyFilter=<bean:write name="scheduleForm" property="customerKey" />&',
		varname:'petNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.scheduleForm.sourceTypeKey.value=4; document.scheduleForm.sourceKey.value=obj.id; document.scheduleForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
            </span>
          </logic:equal>        
          <logic:equal name="scheduleForm" property="attributeToEntity" value="System">
            <span class="ac_holder">
            <input id="attributeToNameAC" size="40" maxlength="40" value="<bean:write name="scheduleForm" property="attributeToName" />" onFocus="javascript:
            var options = {
		script:'SystemLookUp.do?json=true&customerKeyFilter=<bean:write name="scheduleForm" property="customerKey" />&',
		varname:'makeModelFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.scheduleForm.sourceTypeKey.value=24; document.scheduleForm.sourceKey.value=obj.id; document.scheduleForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
            </span>
          </logic:equal>
          <logic:equal name="scheduleForm" property="attributeToEntity" value="Vehicle">
            <span class="ac_holder">
            <input id="attributeToNameAC" size="40" maxlength="40" value="<bean:write name="scheduleForm" property="attributeToName" />" onFocus="javascript:
            var options = {
		script:'VehicleLookUp.do?json=true&customerKeyFilter=<bean:write name="scheduleForm" property="customerKey" />&',
		varname:'makeFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.scheduleForm.sourceTypeKey.value=31; document.scheduleForm.sourceKey.value=obj.id; document.scheduleForm.attributeToName.value=obj.value; }
		};
		var json=new AutoComplete('attributeToNameAC',options);return true;" value="" />
            </span>              
          </logic:equal>
        <% } %>      
        <html:errors property="attributeToName" />
      </logic:notEqual>
    </td>    
    <td><strong><bean:message key="app.schedule.endDate" /></strong></td>
    <td>
      <html:text property="endDate" size="10" readonly="true" />
      <html:errors property="endDate" />             
    </td>        
  </tr>  
  <tr>
    <td><strong><bean:message key="app.schedule.noteLine1" />:</strong></td>
    <td colspan="4">
      <html:text property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableEdit%>" />              
      <html:errors property="noteLine1" />
    </td>   
    <td>
      <html:select property="endHour" disabled="true">      
        <html:option value="8">8 am</html:option>
        <html:option value="9">9 am</html:option>
        <html:option value="10">10 am</html:option>
        <html:option value="11">11 am</html:option>
        <html:option value="12">12 pm</html:option>
        <html:option value="13">1 pm</html:option>
        <html:option value="14">2 pm</html:option>
        <html:option value="15">3 pm</html:option>
        <html:option value="16">4 pm</html:option>
        <html:option value="17">5 pm</html:option>
        <html:option value="18">6 pm</html:option>
        <html:option value="19">7 pm</html:option>
        <html:option value="20">8 pm</html:option>
        <html:option value="21">9 pm</html:option>        
      </html:select>
      &nbsp;&nbsp;      
      <html:select property="endMinute" disabled="true">      
        <html:option value="0">:00</html:option>
        <html:option value="15">:15</html:option>
        <html:option value="30">:30</html:option>
        <html:option value="45">:45</html:option>
      </html:select>
    </td>    
  </tr>  
  <logic:notEqual name="scheduleForm" property="key" value="">
    <logic:equal name="scheduleForm" property="locationId" value="false">      
      <tr>
        <td><strong><bean:message key="app.schedule.addressId" />:</strong></td>
        <td colspan="3">
          <html:radio property="addressId" value="false" disabled="<%=attDisableEdit%>"/>Default
          <html:radio property="addressId" value="true" disabled="<%=attDisableEdit%>"/>Other
          <html:errors property="addressId" />                   
        </td>   
      </tr>  
      <tr>
        <td><strong><bean:message key="app.schedule.address" />:</strong></td>
        <td colspan="3">
          <html:text property="address" size="40" maxlength="30" readonly="<%=attDisableEdit%>" />
          <html:errors property="address" />                   
        </td>
      </tr>
      <tr>
        <td><strong><bean:message key="app.schedule.city" />:</strong></td>
        <td colspan="3">
          <html:text property="city" size="40" maxlength="30" readonly="<%=attDisableEdit%>" />
          <html:errors property="city" />                   
        </td>
        <td><strong><bean:message key="app.schedule.state" />:</strong></td>
        <td colspan="3">
          <html:text property="state" size="2" maxlength="2" readonly="<%=attDisableEdit%>" />
          <html:errors property="state" />
          <logic:notEqual name="scheduleForm" property="address" value="">
            <logic:notEqual name="scheduleForm" property="city" value="">
              <logic:notEqual name="scheduleForm" property="state" value="">              
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="blank" onclick="popup = putinpopup('<bean:message key="app.showMap.url" /><bean:write name="scheduleForm" 
                  property="address" /><bean:message key="app.showMap.ctz" /><bean:write name="scheduleForm" 
                  property="city" /><bean:message key="app.showMap.zip" /><bean:write name="scheduleForm" 
                  property="state" />'); return false" target="_blank"><bean:message key="app.map" /></a>
              </logic:notEqual>
            </logic:notEqual>
          </logic:notEqual>            
        </td>
      </tr>
    </logic:equal>      
  </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.schedule.createUser" />:</strong></td>
    <td colspan="3"><bean:write name="scheduleForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.schedule.createStamp" />:</strong></td>
    <td><bean:write name="scheduleForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.schedule.updateUser" />:</strong></td>
    <td colspan="3"><bean:write name="scheduleForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.schedule.updateStamp" />:</strong></td>
    <td><bean:write name="scheduleForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="6" align="right">
      <html:submit value="Save Event Values" disabled="<%=attDisableEdit%>" />         
      </html:form> 
    </td>
  </tr>
</table>

<!-- ================================================================= -->
<!-- List the work order detail for particular appointment(s).         -->
<!-- ================================================================= -->
<logic:notEqual name="scheduleForm" property="key" value="">
  <% if (attWorkOrderEdit) { %>
  
    <%
      currentColor = "ODD";
      i = -1;     
  
      int fldKey = 0;
      String orderQtyBase = "orderQty";
      String estimatedPriceBase = "estimatedPrice";
      String startDateBase = "startDate";
      String endDateBase = "endDate";
      String orderQtyError;
      String estimatedPriceError;
      String startDateError;
      String endDateError;
    %>
    <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >    
      <tr>
        <td colspan="10">
          <hr>
        </td>
      </tr>        
      <tr align="left">
        <th><bean:message key="app.workorder.type" /></th> 
        <th><bean:message key="app.workorder.number" /></th>
        <th><bean:message key="app.workorder.name" /></th>
        <th>
          <bean:message key="app.workorder.size" />/
          <bean:message key="app.workorder.sizeUnit" />
        </th>
        <th align="center"><bean:message key="app.workorder.releaseId" /></th>
        <th><bean:message key="app.workorder.orderQty" /></th>
        <td> 
          <% if (!(attDisableEdit)) { %>   
            <button type="button" onClick="window.location='BundleList.do?ctKey=<bean:write name="scheduleForm" 
               property="customerTypeKey" />&sKey=<bean:write name="scheduleForm" 
               property="key" />&key=<bean:write name="scheduleForm" 
               property="key" />' "><bean:message key="app.workorder.addBundle" /></button>
          <% } else { %> 
            <button type="button" disabled><bean:message key="app.workorder.addBundle" /></button>      
          <% } %>
        </td>
        <td>           
          <% if (!(attDisableEdit)) { %>   
            <button type="button" onClick="window.location='ServiceDictionaryList.do?ctKey=<bean:write name="scheduleForm" 
               property="customerTypeKey" />&sKey=<bean:write name="scheduleForm" 
               property="key" />&key=<bean:write name="scheduleForm" 
               property="key" />' "><bean:message key="app.workorder.addService" /></button>
          <% } else { %> 
            <button type="button" disabled><bean:message key="app.workorder.addService" /></button>      
          <% } %>
        </td>
        <td align="right">
          <% if (!(attDisableEdit)) { %>   
            <button type="button" onClick="window.location='ItemDictionaryList.do?ctKey=<bean:write name="scheduleForm" 
               property="customerTypeKey" />&sKey=<bean:write name="scheduleForm" 
               property="key" />&key=<bean:write name="scheduleForm" 
               property="key" />&idKey=<bean:write name="scheduleForm" 
               property="key" />' "><bean:message key="app.workorder.addItem" /></button>
          <% } else { %> 
            <button type="button" disabled><bean:message key="app.workorder.addItem" /></button>      
          <% } %>        
        </td>
      </tr>
      <tr>
        <td colspan="10">
          <hr>
        </td>
      </tr>
      <html:form action="/WorkOrderEdit"> 
      <input type="hidden" name="key" value="<bean:write name="scheduleForm" property="key" />" >    
      <logic:notEqual name="workOrderListHeadForm" property="recordCount" value="0"> 
        <logic:iterate id="workOrderForm"
                       name="workOrderListHeadForm"
                       property="workOrderForm"
                       scope="session"
                       type="com.wolmerica.workorder.WorkOrderForm">                             
        <%
          ++i;      
          if ( i % 2 == 0) { currentColor = "ODD"; } 
          else { currentColor = "EVEN"; }    
          orderQtyError = orderQtyBase + i;
          estimatedPriceError = estimatedPriceBase + i;
          startDateError = startDateBase + i;
          endDateError = endDateBase + i;          
        %>
      
          <tr align="left" class="<%=currentColor %>" >
            <td>
              <logic:equal name="workOrderForm" property="sourceTypeKey" value="3">
                <bean:message key="app.workorder.item" />              
              </logic:equal>
              <logic:equal name="workOrderForm" property="sourceTypeKey" value="6">
                <bean:message key="app.workorder.service" />
              </logic:equal>          
            </td>
            <td>          
              <bean:write name="workOrderForm" property="sourceNum" />
            </td>             
            <td>          
              <bean:write name="workOrderForm" property="sourceName" />
            </td>       
            <td>
              <bean:write name="workOrderForm" property="size" />
              <bean:write name="workOrderForm" property="sizeUnit" />
            </td>
            <td align="center">
              <% fldKey++; %>                
              <html:checkbox name="workOrderForm" property="releaseId" disabled="true" />
            </td>        
            <td>   
              <logic:equal name="workOrderForm" property="sourceTypeKey" value="6">
                <logic:equal name="workOrderForm" property="thirdPartyId" value="false">
                  <% attThirdPartyServiceOnly = false; %>
                </logic:equal>
              </logic:equal>

              <logic:equal name="workOrderForm" property="sourceTypeKey" value="3"> 
                <%
                   attThirdPartyServiceOnly = false;
                   if ((new Integer(workOrderForm.getOrderQty())) > (new Integer(workOrderForm.getAvailableQty()))){
                     attInsufficientItemQuantity = true;
                   } 
                 %>
              
                <bean:write name="workOrderForm" property="availableQty" />
                &nbsp;
              </logic:equal>
              <% fldKey++; %>              
              <html:text indexed="true" name="workOrderForm" property="orderQty" size="2" maxlength="3" readonly="<%=attDisableEdit%>" />
              <html:errors property="<%=orderQtyError%>" />
            </td>            
            <td align="right">        
              <bean:message key="app.localeCurrency" />
              <% fldKey++; %>
              <html:text indexed="true" name="workOrderForm" property="estimatedPrice" size="3" maxlength="8" readonly="<%=attDisableEdit%>" />
              <html:errors property="<%=estimatedPriceError%>" />
            </td>
            <td align="right">        
              <bean:message key="app.localeCurrency" />  
              <bean:write name="workOrderForm" property="extendedPrice" />
            </td>            
            <td align="right">           
              <logic:equal name="workOrderForm" property="allowDeleteId" value="true">
                <% if (!(attDisableEdit)) { %>                 
                  <a href="WorkOrderDelete.do?key=<bean:write name="workOrderForm" 
                    property="scheduleKey" />&woKey=<bean:write name="workOrderForm" property="key" /> " onclick="return confirm('<bean:message key="app.workorder.delete.message" />')" ><bean:message key="app.delete" /></a>
                <% } %>
              </logic:equal>
            </td>
          </tr>
          
        <logic:equal name="workOrderForm" property="sourceTypeKey" value="3">          
          <% fldKey= fldKey + 2; %>        
        </logic:equal>
        
        <logic:equal name="workOrderForm" property="sourceTypeKey" value="6">

            <tr align="left" class="<%=currentColor %>" >
              <td colspan="3">
                <% fldKey++; %>
                <b><bean:message key="app.workorder.startDate" /></b>
                <html:text indexed="true" name="workOrderForm" property="startDate" size="8" readonly="<%=attDisableEdit%>" />
                <% if (!(attDisableEdit)) { %>
                  <a href="javascript:show_calendar('workOrderListHeadForm.elements[<%=fldKey%>]');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the invoice due date."></a>
                <% } %>
                <html:errors property="<%=startDateError%>" />
                &nbsp;&nbsp;
                <% fldKey++; %>
                <b><bean:message key="app.workorder.startTime" /></b>
                <html:select indexed="true" name="workOrderForm" property="startHour" disabled="<%=attDisableEdit%>">      
                  <html:option value="8">8 am</html:option>
                  <html:option value="9">9 am</html:option>
                  <html:option value="10">10 am</html:option>
                  <html:option value="11">11 am</html:option>
                  <html:option value="12">12 pm</html:option>
                  <html:option value="13">1 pm</html:option>
                  <html:option value="14">2 pm</html:option>
                  <html:option value="15">3 pm</html:option>
                  <html:option value="16">4 pm</html:option>
                  <html:option value="17">5 pm</html:option>
                  <html:option value="18">6 pm</html:option>
                  <html:option value="19">7 pm</html:option>
                  <html:option value="20">8 pm</html:option>
                  <html:option value="21">9 pm</html:option>
                </html:select>
                &nbsp;&nbsp;   
                <% fldKey++; %>
                <html:select indexed="true" name="workOrderForm" property="startMinute" disabled="<%=attDisableEdit%>">      
                  <html:option value="0">:00</html:option>
                  <html:option value="15">:15</html:option>
                  <html:option value="30">:30</html:option>
                  <html:option value="45">:45</html:option>
                </html:select>
              </td>  
              <td colspan="5">                           
                <% fldKey++; %>              
                <b><bean:message key="app.workorder.endDate" /></b>
                <html:text indexed="true" name="workOrderForm" property="endDate" size="8" readonly="<%=attDisableEdit%>" />
                <% if (!(attDisableEdit)) { %>
                     <a href="javascript:show_calendar('workOrderListHeadForm.elements[<%=fldKey%>]');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the invoice due date."></a>
                <% } %>
                <html:errors property="<%=endDateError%>" />                
                &nbsp;&nbsp;
                <% fldKey++; %>                
                <b><bean:message key="app.workorder.endTime" /></b>
                <html:select indexed="true" name="workOrderForm" property="endHour" disabled="<%=attDisableEdit%>">      
                  <html:option value="8">8 am</html:option>
                  <html:option value="9">9 am</html:option>
                  <html:option value="10">10 am</html:option>
                  <html:option value="11">11 am</html:option>
                  <html:option value="12">12 pm</html:option>
                  <html:option value="13">1 pm</html:option>
                  <html:option value="14">2 pm</html:option>
                  <html:option value="15">3 pm</html:option>
                  <html:option value="16">4 pm</html:option>
                  <html:option value="17">5 pm</html:option>
                  <html:option value="18">6 pm</html:option>
                  <html:option value="19">7 pm</html:option>
                  <html:option value="20">8 pm</html:option>
                  <html:option value="21">9 pm</html:option>
                </html:select>
                &nbsp;&nbsp;      
                <% fldKey++; %>                
                <html:select indexed="true" name="workOrderForm" property="endMinute" disabled="<%=attDisableEdit%>">      
                  <html:option value="0">:00</html:option>
                  <html:option value="15">:15</html:option>
                  <html:option value="30">:30</html:option>
                  <html:option value="45">:45</html:option>
                </html:select>
              </td> 
              <td>
              <% if (!(attDisableEdit)) { %>
                <img src="./images/resource.gif" height="16" width="16" border="0" onclick="popup = putinpopup('ResourceAllocation.do?key=<bean:write name="workOrderForm" 
                property="key" />&serviceName=<bean:write name="workOrderForm" 
                property="sourceName" />&startDate=<bean:write name="workOrderForm" 
                property="startDate" />&startHour=<bean:write name="workOrderForm" 
                property="startHour" />&startMinute=<bean:write name="workOrderForm" 
                property="startMinute" />&endDate=<bean:write name="workOrderForm" 
                property="endDate" />&endHour=<bean:write name="workOrderForm" 
                property="endHour" />&endMinute=<bean:write name="workOrderForm" 
                property="endMinute" />'); return false" title="<bean:message key="app.workorder.resource" /> ">
              <% } %>                
              </td>
            </tr>  
          </logic:equal>
          
          <logic:equal name="workOrderForm" property="sourceTypeKey" value="3">
            <input type="hidden" name="startDate" value="<bean:write name="workOrderForm" property="startDate" />" >
            <input type="hidden" name="endDate" value="<bean:write name="workOrderForm" property="endDate" />" >
          </logic:equal>
          
        </logic:iterate>    
        <tr> 
          <td align="right" colspan="10">
            <html:submit value="Save Work Order" disabled="<%=attDisableEdit%>" />
          </td>
        </tr>   
      </logic:notEqual>
      </html:form> 
    </table>      
  <% } %>    
</logic:notEqual>

<!--
<script type="text/javascript">
  for (i=0; i<document.workOrderListHeadForm.elements.length; i++) {
    alert('i='+i+' value=' + document.workOrderListHeadForm.elements[i].value);
  }      
</script>    
-->

<logic:notEqual name="scheduleForm" property="key" value="">  
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >    
    <logic:equal name="scheduleForm" property="eventTypeKey" value="0">
      <tr>
        <td colspan="10">
          <hr>
        </td>
      </tr>
    </logic:equal>
    <tr>
<!-- PET OBJECTS -->
        <logic:equal name="scheduleForm" property="attributeToEntity" value="Pet">
          <logic:notEqual name="scheduleForm" property="attributeToName" value="">
<!-- PET EXAM -->
            <td>
            <logic:equal name="scheduleForm" property="examKey" value="0">
              <button onClick="window.location='PetExamEntry.do?scheduleKey=<bean:write name="scheduleForm" 
                property="key" />&subject=<bean:write name="scheduleForm"
                property="subject" />&examDate=<bean:write name="scheduleForm"
                property="startDate" />&releaseId=<bean:write name="scheduleForm"
                property="releaseId" />&petKey=<bean:write name="scheduleForm"
                property="sourceKey" />&petName=<bean:write name="scheduleForm" 
                property="attributeToName" />&clientName=<bean:write name="scheduleForm" 
                property="clientName" />' "><bean:message key="app.schedule.addPetExam" /></button>
            </logic:equal>

            <logic:notEqual name="scheduleForm" property="examKey" value="0">
              <button onClick="window.location='PetExamGet.do?key=<bean:write name="scheduleForm" property="examKey" />' "><bean:message key="app.schedule.viewPetExam" /></button>
            </logic:notEqual>
            </td>
<!-- PET BOARDING -->
            <logic:equal name="scheduleForm" property="category1Id" value="true"> 
              <logic:equal name="scheduleForm" property="firstDayId" value="true">
                <td>                  
                <logic:equal name="scheduleForm" property="category1Key" value="0">
                  <button onClick="window.location='PetBoardEntry.do?scheduleKey=<bean:write name="scheduleForm" 
                    property="key" />&checkInDate=<bean:write name="scheduleForm"
                    property="startDate" />&checkInHour=<bean:write name="scheduleForm"
                    property="startHour" />&checkInMinute=<bean:write name="scheduleForm"
                    property="startMinute" />&petKey=<bean:write name="scheduleForm" 
                    property="sourceKey" />&petName=<bean:write name="scheduleForm" 
                    property="attributeToName" />&clientName=<bean:write name="scheduleForm" 
                    property="clientName" />' "><bean:message key="app.schedule.addPetBoard" /></button>
                </logic:equal>
              
                <logic:notEqual name="scheduleForm" property="category1Key" value="0">
                  <button onClick="window.location='PetBoardGet.do?key=<bean:write name="scheduleForm" property="category1Key" />' "><bean:message key="app.schedule.viewPetBoard" /></button>
                </logic:notEqual>
                </td>                
              </logic:equal>
            </logic:equal>
            
<!-- PET VACCINATION -->
            <logic:equal name="scheduleForm" property="category2Id" value="true"> 
              <td>
              <logic:equal name="scheduleForm" property="category2Key" value="0">
                <button onClick="window.location='PetVacEntry.do?scheduleKey=<bean:write name="scheduleForm" 
                  property="key" />&vacDate=<bean:write name="scheduleForm"
                  property="startDate" />&petName=<bean:write name="scheduleForm" 
                  property="attributeToName" />&clientName=<bean:write name="scheduleForm" 
                  property="clientName" />' "><bean:message key="app.schedule.addPetVac" /></button>
              </logic:equal>
              
              <logic:notEqual name="scheduleForm" property="category2Key" value="0">
                <button onClick="window.location='PetVacGet.do?key=<bean:write name="scheduleForm" property="category2Key" />' "><bean:message key="app.schedule.viewPetVac" /></button>
              </logic:notEqual>              
              </td>              
            </logic:equal>   
          </logic:notEqual>            
        </logic:equal>

<!-- PURCHASE ORDER -->
        <logic:equal name="scheduleForm" property="thirdPartyId" value="true">
          <logic:equal name="scheduleForm" property="thirdPartyOrderId" value="false">
            <td>
              <button onClick="window.location='WorkOrderInitiatePurchaseOrder.do?key=<bean:write name="scheduleForm" property="key" /> ' "><bean:message key="app.schedule.addPurchaseOrder" /></button>                
            </td>
          </logic:equal>            
          <logic:equal name="scheduleForm" property="thirdPartyOrderId" value="true">
            <td>
              <button onClick="window.location='PurchaseOrderList.do?scheduleKeyFilter=<bean:write name="scheduleForm" property="key" /> ' "><bean:message key="app.schedule.viewPurchaseOrder" /></button>
            </td>
          </logic:equal>            
        </logic:equal>
            
<!-- CUSTOMER INVOICE -->
        <logic:equal name="scheduleForm" property="eventTypeKey" value="0">
          <logic:equal name="scheduleForm" property="customerInvoiceKey" value="">
            <td>
            <logic:equal name="workOrderListHeadForm" property="recordCount" value="0">
              <button onClick="window.location='CustomerInvoiceEntry.do?key=-1' "><bean:message key="app.schedule.addInvoice" /></button>
            </logic:equal>  
            <logic:notEqual name="workOrderListHeadForm" property="recordCount" value="0">
              <% if (attThirdPartyServiceOnly || attInsufficientItemQuantity) {
                   attDisableInvoice = "disabled";
                 }
              %>
              <button <%=attDisableInvoice%> onClick="window.location='WorkOrderInvoiceEntry.do?key=-1&sKey=<bean:write name="scheduleForm" property="key" /> ' "><bean:message key="app.schedule.addInvoice" /></button>
            </logic:notEqual>
            </td>            
          </logic:equal>
          
          <logic:notEqual name="scheduleForm" property="customerInvoiceKey" value="">
            <td>                            
              <button onClick="window.location='CustomerInvoiceGet.do?key=<bean:write name="scheduleForm" property="customerInvoiceKey" />' "><bean:message key="app.schedule.viewInvoice" /></button>
            </td>            
            <logic:equal name="scheduleForm" property="rewindId" value="true">
              <td>
                <% if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("false") != 0) { %>
                  <a href="CustomerInvoiceRewind.do?ciKey=<bean:write name="scheduleForm" property="customerInvoiceKey" />&key=<bean:write name="scheduleForm" property="key" />"  onclick="return confirm('<bean:message key="app.customerinvoice.rewind.message" />')" > <bean:message key="app.schedule.rewindInvoice" /></a>
                <% } %>
              </td>
            </logic:equal>
          </logic:notEqual>                   
        </logic:equal>

    <logic:notEqual name="workOrderListHeadForm" property="recordCount" value="0">
      <td colspan = "8" align="right">
        <a href="#" class="submenu" onclick="popup = putinpopup('PdfWorkOrderEstimate.do?key=<bean:write name="scheduleForm"
          property="key" />&presentationType=pdf'); return false" target="_PDF">Estimate Form</a>
      </td>
    </logic:notEqual>
    </tr>   
  </table>
</logic:notEqual>
