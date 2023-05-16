<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script type="text/javascript"><!--

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

<%
  String currentColor = "ODD";
  int i = -1;      
  String attTitle = "app.petexam.addTitle";            
  String attAction = "/PetExamAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="petExamForm" property="key" value="">
  <%
    attTitle = "app.petexam.editTitle";
    attAction = "/PetExamEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="petExamForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="petExamForm" property="permissionStatus" value="READONLY">
    [<bean:write name="petExamForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <button type="button" onClick="window.location='PetExamListEntry.do?petKey=<bean:write name="petExamForm" property="petKey" />' "><bean:message key="app.petexam.backMessage" /></button>
    </td>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <!-- Display tabs to show and hide pet exam content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff; font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('anestheticTab','anestheticInfo');HideContent('vitalsignsTab','vitalsignsInfo');">
              <bean:message key="app.petexam.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="petExamForm" property="key" value="">
            <td align="center">
              <div id="anestheticTab" style="background:#fff; font:12;">
                <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('anestheticTab','anestheticInfo');HideContent('vitalsignsTab','vitalsignsInfo');">
                  <bean:message key="app.petexam.anestheticTab" />
                </a>
              </div>
            </td>
            <td align="center">
            <div id="vitalsignsTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');HideContent('anestheticTab','anestheticInfo');ShowContent('vitalsignsTab','vitalsignsInfo');">
              <bean:message key="app.petexam.vitalsignsTab" />
              </a>
            </div>
            </td>
          </logic:notEqual>          
        </tr>
      </table>
    </td>
    <td colspan="2" align="right">
      <logic:notEqual name="petExamForm" property="key" value="">        
        <a href="#" onclick="popup = putinpopup('PdfAnesthesiaForm.do?key=<bean:write name="petExamForm" 
                                             property="key" />&petKey=<bean:write name="petExamForm" 
                                             property="petKey" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.view"/>&nbsp;<bean:message key="app.pdf"/></a>
      </logic:notEqual>                                             
    </td>
  </tr>      
</table>

<div id="mainInfo" style="display:none;"> 
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
    <html:form action="<%=attAction%>"> 
    <logic:notEqual name="petExamForm" property="key" value="">
      <html:hidden property="key" /> 
      <input type="hidden" name="sourceTypeKey" value="<bean:message key="app.pet.id"/>" >
    </logic:notEqual>
    <tr> 
      <td><strong><bean:message key="app.petexam.customer" />:</strong></td>
      <td>
        <html:text property="clientName" size="40" maxlength="40" readonly="true" /> 
        <html:errors property="clientName" />
      </td>        
      <td><strong><bean:message key="app.petexam.treatmentDate" />:</strong></td>
      <td>
        <html:text property="treatmentDate" size="10" maxlength="10" readonly="true" />
          <a href="ScheduleGet.do?key=<bean:write name="petExamForm" property="scheduleKey" /> "><img src="./images/prev.gif" width="16" height="16" border="0" title="Click edit treatment date."></a>
        <html:errors property="treatmentDate" />
      </td>       
    </tr>
    <tr>
      <td><strong><bean:message key="app.petexam.petName" />:</strong></td>
      <td>
        <html:text property="petName" size="40" maxlength="40" readonly="true" /> 
        <html:errors property="petName" />
      </td>       
      <td><strong><bean:message key="app.petexam.heartRate" />:</strong></td>
      <td>
        <html:text property="heartRate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" /> 
        <html:errors property="heartRate" />
      </td>    
    </tr>   
    <tr>
      <td><strong><bean:message key="app.petexam.subject" />:</strong></td>      
      <td>       
        <html:text property="subject" size="40" maxlength="30" readonly="true" />
        <html:errors property="subject" />
      </td>            
      <td><strong><bean:message key="app.petexam.resptRate" />:</strong></td>
      <td>
        <html:text property="resptRate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" /> 
        <html:errors property="resptRate" />
      </td>        
    </tr>
    <tr>      
      <td><strong><bean:message key="app.petexam.dvmResourceName" />:</strong></td>
      <td>
        <html:hidden property="dvmResourceKey" />
        <html:hidden property="dvmResourceName" />
        <% if (attDisableEdit) { %>        
          <html:text property="dvmResourceName" size="40" maxlength="40" readonly="true" />
        <% } else { %>
          <span class="ac_holder">
          <input id="dvmResourceNameAC" size="40" maxlength="40" value="<bean:write name="petExamForm" property="dvmResourceName" />" onFocus="javascript:
          var options = {
                script:'ResourceLookUp.do?json=true&',
  		varname:'resourceNameFilter',
                json:true,
                shownoresults:true,
                maxresults:10,
                callback: function (obj) { document.petExamForm.dvmResourceKey.value=obj.id; document.petExamForm.dvmResourceName.value=obj.value; }
		};
		var json=new AutoComplete('dvmResourceNameAC',options);return true;" value="" />
          </span>
        <% } %>               
        <html:errors property="dvmResourceKey" />
      </td>            
      <td><strong><bean:message key="app.petexam.capRefillTime" />:</strong></td>
      <td>
        <html:text property="capRefillTime" size="10" maxlength="10" readonly="<%=attDisableEdit%>" /> 
        <html:errors property="capRefillTime" />
      </td>    
    </tr>
    <tr>
      <td><strong><bean:message key="app.petexam.techResourceName" />:</strong></td>
      <td>
        <html:hidden property="techResourceKey" />
        <html:hidden property="techResourceName" />
        <% if (attDisableEdit) { %>
          <html:text property="techResourceNameAC" size="40" maxlength="40" readonly="true" />        
        <% } else { %>
          <span class="ac_holder">
          <input id="techResourceNameAC" size="40" maxlength="40" value="<bean:write name="petExamForm" property="techResourceName" />" onFocus="javascript:
          var options = {
                script:'ResourceLookUp.do?json=true&',
  		varname:'resourceNameFilter',
                json:true,
                shownoresults:true,
                maxresults:10,
                callback: function (obj) { document.petExamForm.techResourceKey.value=obj.id; document.petExamForm.techResourceName.value=obj.value; }
		};
		var json=new AutoComplete('techResourceNameAC',options);return true;" value="" />
          </span>        
        <% } %>               
        <html:errors property="techResourceKey" />
      </td>      
      <td><strong><bean:message key="app.petexam.temperature" />:</strong></td>
      <td>
        <html:text property="temperature" size="10" maxlength="10" readonly="<%=attDisableEdit%>" /> 
        <html:errors property="temperature" />
      </td>     
    </tr>  
    <tr> 
      <td><strong><bean:message key="app.petexam.generalCondition" />:</strong></td>
      <td>
        <html:text property="generalCondition" size="10" maxlength="10" readonly="<%=attDisableEdit%>" /> 
        <html:errors property="generalCondition" />
      </td>
      <td><strong><bean:message key="app.petexam.bodyWeight" />:</strong></td>
      <td>
        <html:text property="bodyWeight" size="10" maxlength="10" readonly="<%=attDisableEdit%>" /> 
        <html:errors property="bodyWeight" />
      </td>    
    </tr>    
    <tr>   
      <td rowspan="5"><strong><bean:message key="app.petexam.medicalData" />:</strong></td>
      <td rowspan="5">
        <html:textarea property="medicalData" cols="30" rows="5" readonly="<%=attDisableEdit%>" />
        <html:errors property="medicalData" />
      </td>
    </tr>  
    <tr> 
      <td>
        <strong><bean:message key="app.petexam.startHour" />:</strong>
     </td>
      <td>      
        <html:select property="startHour" disabled="<%=attDisableEdit%>"> 
          <html:option value="0">N/A</html:option>          
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
        </html:select>
        <html:select property="startMinute" disabled="<%=attDisableEdit%>">          
          <html:option value="0">:00</html:option>
          <html:option value="5">:05</html:option>
          <html:option value="10">:10</html:option>
          <html:option value="15">:15</html:option>
          <html:option value="20">:20</html:option>
          <html:option value="25">:25</html:option>
          <html:option value="30">:30</html:option>
          <html:option value="35">:35</html:option>
          <html:option value="40">:40</html:option>
          <html:option value="45">:45</html:option>
          <html:option value="50">:50</html:option>
          <html:option value="55">:55</html:option>        
        </html:select>
      </td>    
    </tr>
    <tr> 
      <td>
        <strong><bean:message key="app.petexam.surgeryHour" />:</strong>
      </td>
      <td>    
        <html:select property="surgeryHour" disabled="<%=attDisableEdit%>"> 
          <html:option value="0">N/A</html:option>           
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
        </html:select>
        <html:select property="surgeryMinute" disabled="<%=attDisableEdit%>">  
          <html:option value="0">:00</html:option>
          <html:option value="5">:05</html:option>
          <html:option value="10">:10</html:option>
          <html:option value="15">:15</html:option>
          <html:option value="20">:20</html:option>
          <html:option value="25">:25</html:option>
          <html:option value="30">:30</html:option>
          <html:option value="35">:35</html:option>
          <html:option value="40">:40</html:option>
          <html:option value="45">:45</html:option>
          <html:option value="50">:50</html:option>
          <html:option value="55">:55</html:option>    
        </html:select>
      </td>    
    </tr>
    <tr> 
      <td>
        <strong><bean:message key="app.petexam.endHour" />:</strong>
      </td>
      <td>      
        <html:select property="endHour" disabled="<%=attDisableEdit%>"> 
          <html:option value="0">N/A</html:option>           
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
        </html:select>
        <html:select property="endMinute" disabled="<%=attDisableEdit%>">      
          <html:option value="0">:00</html:option>
          <html:option value="5">:05</html:option>
          <html:option value="10">:10</html:option>
          <html:option value="15">:15</html:option>
          <html:option value="20">:20</html:option>
          <html:option value="25">:25</html:option>
          <html:option value="30">:30</html:option>
          <html:option value="35">:35</html:option>
          <html:option value="40">:40</html:option>
          <html:option value="45">:45</html:option>
          <html:option value="50">:50</html:option>
          <html:option value="55">:55</html:option>    
        </html:select>
      </td>    
    </tr>
    <tr> 
      <td>
        <strong><bean:message key="app.petexam.reflexHour" />:</strong>
      </td>
      <td>
        <html:select property="reflexHour" disabled="<%=attDisableEdit%>">   
          <html:option value="0">N/A</html:option>           
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
        </html:select>
        <html:select property="reflexMinute" disabled="<%=attDisableEdit%>">      
          <html:option value="0">:00</html:option>
          <html:option value="5">:05</html:option>
          <html:option value="10">:10</html:option>
          <html:option value="15">:15</html:option>
          <html:option value="20">:20</html:option>
          <html:option value="25">:25</html:option>
          <html:option value="30">:30</html:option>
          <html:option value="35">:35</html:option>
          <html:option value="40">:40</html:option>
          <html:option value="45">:45</html:option>
          <html:option value="50">:50</html:option>
          <html:option value="55">:55</html:option>    
        </html:select>
      </td>    
    </tr>
    <tr> 
      <td><strong><bean:message key="app.petexam.noteLine1" />:</strong></td>
      <td>
        <html:text property="noteLine1" size="40" maxlength="60" readonly="<%=attDisableEdit%>" /> 
        <html:errors property="noteLine1" />
      </td>
      <td>
        <strong><bean:message key="app.petexam.recoveryHour" />:</strong>
      </td>
      <td>
        <html:select property="recoveryHour" disabled="<%=attDisableEdit%>">  
          <html:option value="0">N/A</html:option>           
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
        </html:select>
        <html:select property="recoveryMinute" disabled="<%=attDisableEdit%>">      
          <html:option value="0">:00</html:option>
          <html:option value="5">:05</html:option>
          <html:option value="10">:10</html:option>
          <html:option value="15">:15</html:option>
          <html:option value="20">:20</html:option>
          <html:option value="25">:25</html:option>
          <html:option value="30">:30</html:option>
          <html:option value="35">:35</html:option>
          <html:option value="40">:40</html:option>
          <html:option value="45">:45</html:option>
          <html:option value="50">:50</html:option>
          <html:option value="55">:55</html:option>    
        </html:select>
      </td>    
    </tr>    
    <tr>
      <td><strong><bean:message key="app.petexam.createUser" />:</strong></td>
      <td><bean:write name="petExamForm" property="createUser" /></td>	          	
      <td><strong><bean:message key="app.petexam.createStamp" />:</strong></td>
      <td><bean:write name="petExamForm" property="createStamp" /></td>	             	    
    </tr>
    <tr>
      <td><strong><bean:message key="app.petexam.updateUser" />:</strong></td>
      <td><bean:write name="petExamForm" property="updateUser" /></td>	               
      <td><strong><bean:message key="app.petexam.updateStamp" />:</strong></td>
      <td><bean:write name="petExamForm" property="updateStamp" /></td>	             
    </tr>	
    <tr>
      <td colspan="8" align="right">
        <html:submit value="Save Exam Values" disabled="<%=attDisableEdit%>" />         
       </html:form> 
      </td>
    </tr>
  </table>
</div>

<script type="text/javascript" language="JavaScript"><!--
  ShowContent('mainTab', 'mainInfo');
//--></script>


<logic:notEqual name="petExamForm" property="key" value="">    
  <div id="anestheticInfo" style="display:none;">
    <%
      i = -1;  
      int resKey = i;
      boolean attDisableEditQty = attDisableEdit;
      boolean attDisableAddItems = attDisableEdit;  
    
      String doseBase = "dose";
      String doseUnitBase = "doseUnit";
      String routeBase = "route";    
      String resourceNameBase = "resourceName";
      String doseError;
      String doseUnitError;
      String routeError; 
      String resourceNameError;
    %>

    <logic:notEqual name="petAnestheticListHeadForm" property="permissionStatus" value="LOCKED">
      <bean:message key="app.readonly"/>
        <logic:notEqual name="petAnestheticListHeadForm" property="permissionStatus" value="READONLY">
          [<bean:write name="petExamForm" property="permissionStatus" />]      
          <% attDisableAddItems = true; %>
        </logic:notEqual>
      </logic:notEqual>
      <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
      <tr>
        <td colspan="10">
          <hr>
        </td>
      </tr>     
      <tr align="left">
        <th><bean:message key="app.petanesthetic.startTime" /></th>          
        <th><bean:message key="app.petanesthetic.applicationType" /></th>      
        <th><bean:message key="app.petanesthetic.brandName" /></th>  
        <th><bean:message key="app.petanesthetic.dose" /></th>
        <th><bean:message key="app.petanesthetic.doseUnit" /></th>
        <th><bean:message key="app.petanesthetic.route" /></th>    
        <th><bean:message key="app.petanesthetic.resourceName" /></th>
        <td align="right" colspan="2">
          <% if (!(attDisableAddItems)) { %>   
            <button type="button" onClick="window.location='ItemDictionaryList.do?peKey=<bean:write name="petExamForm" 
               property="key" />&key=<bean:write name="petExamForm" 
               property="key" />&idKey=<bean:write name="petExamForm" 
               property="key" />' "><bean:message key="app.petexam.addAgent" /></button>
          <% } else { %> 
            <button type="button" disabled><bean:message key="app.petexam.addAgent" /></button>      
          <% } %>        
        </td>        
      </tr>
      <tr>
        <td colspan="10">
          <hr>
        </td>
      </tr> 
      <html:form action="/PetAnestheticEdit"> 
        <input type="hidden" name="key" value="<bean:write name="petExamForm" property="key" />" >       
        <logic:notEqual name="petAnestheticListHeadForm" property="lastRecord" value="0"> 
          <logic:iterate id="petAnestheticForm"
                         name="petAnestheticListHeadForm"
                         property="petAnestheticForm"
                         scope="session"
                         type="com.wolmerica.petanesthetic.PetAnestheticForm">
            <%              
              ++i;
              if ( i % 2 == 0) { currentColor = "ODD"; } 
              else { currentColor = "EVEN"; } 
              doseError = doseBase + i;
              doseUnitError = doseUnitBase + i;
              routeError = routeBase + i;
              resourceNameError = resourceNameBase + i;
            %>
        
            <tr align="left" class="<%=currentColor %>" >
              <td>
                <html:select indexed="true" name="petAnestheticForm" property="startHour" disabled="<%=attDisableEdit%>">  
                  <html:option value="0">N/A</html:option>           
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
                </html:select>
                <html:select indexed="true" name="petAnestheticForm" property="startMinute" disabled="<%=attDisableEdit%>">      
                  <html:option value="0">:00</html:option>
                  <html:option value="5">:05</html:option>
                  <html:option value="10">:10</html:option>
                  <html:option value="15">:15</html:option>
                  <html:option value="20">:20</html:option>
                  <html:option value="25">:25</html:option>
                  <html:option value="30">:30</html:option>
                  <html:option value="35">:35</html:option>
                  <html:option value="40">:40</html:option>
                  <html:option value="45">:45</html:option>
                  <html:option value="50">:50</html:option>
                  <html:option value="55">:55</html:option>    
                </html:select>
              </td>                
              <td>
                <html:select indexed="true" name="petAnestheticForm" property="applicationType" disabled="<%=attDisableEdit%>">  
                  <html:option value="1">Pre</html:option>           
                  <html:option value="2">Ans</html:option>
                  <html:option value="3">Gas</html:option>
                </html:select>          
              </td>
              <td>          
                <bean:write name="petAnestheticForm" property="brandName" />
              </td>
              <td>
                <html:text indexed="true" name="petAnestheticForm" property="dose" size="4" maxlength="6" readonly="<%=attDisableEdit%>" /> 
                <html:errors property="<%=doseError%>" />
              </td>
              <td>
                <html:text indexed="true" name="petAnestheticForm" property="doseUnit" size="2" maxlength="6" readonly="<%=attDisableEdit%>" /> 
                <html:errors property="<%=doseUnitError%>" />                
              </td>
              <td>
                <html:text indexed="true" name="petAnestheticForm" property="route" size="2" maxlength="6" readonly="<%=attDisableEdit%>" /> 
                <html:errors property="<%=routeError%>" />                
              </td>    
              <td>    
                <html:hidden indexed="true" name="petAnestheticForm" property="resourceKey" />
                  <html:text indexed="true" name="petAnestheticForm" property="resourceName" size="10" maxlength="40" readonly="true" />
                  <% 
                     if (!(attDisableEdit)) { 
                       resKey = (i * 8) + 7;
                  %>                    
                    <a href="blank" onclick="popup = putinpopup('ResourcePicker.do?entityKey=<%=resKey%>&entityNameKey=<%=resKey+1%>'); return false" target="_blank"><img src="./images/prev.gif" width="16" height="16" border="0" title="Click to select a technician."></a>
                  <% } %>
                <html:errors property="<%=resourceNameError%>" />                 
              </td>
              <td align="right" colspan="2">           
                <% if (!(attDisableAddItems)) { %>                 
                  <a href="PetAnestheticDelete.do?key=<bean:write name="petExamForm" 
                    property="key" />&tabId=anestheticTab&paKey=<bean:write name="petAnestheticForm" property="key" /> " onclick="return confirm('<bean:message key="app.petanesthetic.delete.message" />')" ><bean:message key="app.delete" /></a>
                <% } %>           
              </td>          
            </tr>        
          </logic:iterate>    
          <tr>  
            <td align="right" colspan="10">
              <input type="hidden" name="tabId" value="anestheticTab" >                
              <html:submit value="Save Agent Detail" disabled="<%=attDisableEdit%>" />
            </td>
          </tr>        
        </logic:notEqual>
      </html:form> 
    </table>
  </div>    
  
<% if (request.getParameter("tabId") != null) { %>
  <% if (request.getParameter("tabId").toString().compareToIgnoreCase("anestheticTab") == 0) { %>
    <script type="text/javascript" language="JavaScript"><!--
      HideContent('mainTab','mainInfo');
      ShowContent('anestheticTab','anestheticInfo');
      HideContent('vitalsignsTab','vitalsignsInfo');
    //--></script>
  <% } %>
<% } %>
  
</logic:notEqual>

<logic:notEqual name="petExamForm" property="key" value="">  
  <div id="vitalsignsInfo" style="display:none;">  
    <%
      i = -1;
      String heartRateBase = "heartRate";
      String resptRateBase = "resptRate";
      String noteLine1Base = "noteLine1";
      
      String heartRateError;
      String resptRateError;
    %>                
    <logic:notEqual name="petVitalSignsListHeadForm" property="permissionStatus" value="LOCKED">
      <bean:message key="app.readonly"/>
        <logic:notEqual name="petVitalSignsListHeadForm" property="permissionStatus" value="READONLY">
          [<bean:write name="petExamForm" property="permissionStatus" />]      
          <% attDisableEdit = true; %>
        </logic:notEqual>
      </logic:notEqual>
      <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
      <tr>
        <td colspan="10">
          <hr>
        </td>
      </tr>     
      <tr align="left">
        <th><bean:message key="app.petvitalsigns.startTime" /></th>      
        <th><bean:message key="app.petvitalsigns.heartRate" /></th>      
        <th><bean:message key="app.petvitalsigns.resptRate" /></th>  
        <th><bean:message key="app.petvitalsigns.noteLine1" /></th>
        <td align="right" colspan="2">
          <% if (!(attDisableEdit)) { %>   
            <button type="button" onClick="window.location='PetVitalSignsAdd.do?peKey=<bean:write name="petExamForm" 
               property="key" />&key=<bean:write name="petExamForm" 
               property="key" />&tabId=vitalsignsTab' "><bean:message key="app.petexam.addVitalSigns" /></button>
          <% } else { %> 
            <button type="button" disabled><bean:message key="app.petexam.addVitalSigns" /></button>      
          <% } %>        
        </td>        
      </tr>
      <tr>
        <td colspan="10">
          <hr>
        </td>
      </tr> 
      <html:form action="/PetVitalSignsEdit"> 
        <input type="hidden" name="key" value="<bean:write name="petExamForm" property="key" />" >       
        <logic:notEqual name="petVitalSignsListHeadForm" property="lastRecord" value="0"> 
          <logic:iterate id="petVitalSignsForm"
                         name="petVitalSignsListHeadForm"
                         property="petVitalSignsForm"
                         scope="session"
                         type="com.wolmerica.petvitalsigns.PetVitalSignsForm">
            <%
              ++i;      
              if ( i % 2 == 0) { currentColor = "ODD"; } 
              else { currentColor = "EVEN"; } 
              heartRateError = heartRateBase + i;
              resptRateError = resptRateBase + i;
            %>
        
            <tr align="left" class="<%=currentColor %>" >
              <td>
                <html:select indexed="true" name="petVitalSignsForm" property="startHour" disabled="<%=attDisableEdit%>">
                  <html:option value="0">N/A</html:option>
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
                </html:select>
                <html:select indexed="true" name="petVitalSignsForm" property="startMinute" disabled="<%=attDisableEdit%>">
                  <html:option value="0">:00</html:option>
                  <html:option value="5">:05</html:option>
                  <html:option value="10">:10</html:option>
                  <html:option value="15">:15</html:option>
                  <html:option value="20">:20</html:option>
                  <html:option value="25">:25</html:option>
                  <html:option value="30">:30</html:option>
                  <html:option value="35">:35</html:option>
                  <html:option value="40">:40</html:option>
                  <html:option value="45">:45</html:option>
                  <html:option value="50">:50</html:option>
                  <html:option value="55">:55</html:option>    
                </html:select>
              </td> 
              <td>
                <html:text indexed="true" name="petVitalSignsForm" property="heartRate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" /> 
                <html:errors property="<%=heartRateError%>" />                
              </td>
              <td>
                <html:text indexed="true" name="petVitalSignsForm" property="resptRate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" /> 
                <html:errors property="<%=resptRateError%>" />                
              </td>              
              <td>
                <html:text indexed="true" name="petVitalSignsForm" property="noteLine1" size="40" maxlength="60" readonly="<%=attDisableEdit%>" /> 
              </td>
              <td align="right" colspan="2">           
                <% if (!(attDisableEdit)) { %>                 
                  <a href="PetVitalSignsDelete.do?key=<bean:write name="petExamForm" 
                    property="key" />&tabId=vitalsignsTab&pvsKey=<bean:write name="petVitalSignsForm" property="key" /> " onclick="return confirm('<bean:message key="app.petvitalsigns.delete.message" />')" ><bean:message key="app.delete" /></a>
                <% } %>           
              </td>          
            </tr>        
          </logic:iterate>    
          <tr>  
            <td align="right" colspan="10">
              <input type="hidden" name="tabId" value="vitalsignsTab" >                
              <html:submit value="Save Vital Signs" disabled="<%=attDisableEdit%>" />
            </td>
          </tr>        
        </logic:notEqual>
      </html:form> 
    </table>               
  </div>
  
<% if (request.getParameter("tabId") != null) { %>
  <% if (request.getParameter("tabId").toString().compareToIgnoreCase("vitalsignsTab") == 0) { %>
    <script type="text/javascript" language="JavaScript"><!--
      HideContent('mainTab','mainInfo');
      HideContent('anestheticTab','anestheticInfo');
      ShowContent('vitalsignsTab', 'vitalsignsInfo');
    //--></script>
  <% } %>
<% } %>
    
</logic:notEqual>