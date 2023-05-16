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
//--></script>

<%
  String currentColor = "ODD";
  int i = -1;      
  String attTitle = "app.pet.addTitle";            
  String attAction = "/PetAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }   
  boolean attBreedLookup = false;
%> 

<logic:notEqual name="petForm" property="key" value="">
  <%
    attTitle = "app.pet.editTitle";
    attAction = "/PetEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="petForm" property="speciesKey" value="">
  <%
     attBreedLookup = true;
  %>
</logic:notEqual>

<logic:notEqual name="petForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="petForm" property="permissionStatus" value="READONLY">
    [<bean:write name="petForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="PetList.do" name="petList" method="post">                                  
        <input type="hidden" name="clientNameFilter" value="<bean:write name="petForm" property="clientNameFilter" />" >
        <input type="hidden" name="petNameFilter" value="<bean:write name="petForm" property="petNameFilter" />" >
        <input type="hidden" name="pageNo" value="<bean:write name="petForm" property="currentPage" />" >          
        <input type="submit" value="<bean:message key="app.pet.backMessage" />">
      </form> 
    </td>
    <logic:notEqual name="petForm" property="key" value="">        
    <td colspan="6" align="right">
      <a href="AttachmentList.do?sourceTypeKey=<bean:message key="app.pet.id"/>&sourceKey=<bean:write name="petForm"
                property="key" />&sourceName=<bean:write name="petForm"
                property="clientName" />/<bean:write name="petForm"
                property="petName" />" >[<bean:write name="petForm" property="attachmentCount" />]<img src="./images/attachment.gif" width="18" height="18" border="0" title="Click to view attachments."></a>
    </td>
    </logic:notEqual>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <!-- Display tabs to show and hide pet content. -->
  <tr>
    <td colspan="2">
      <table border="1" cellspacing="3" cellpadding="3">
        <tr>
          <td align="center">
            <div id="mainTab" style="background:#fff; font:12;">
              <a href="javascript:ShowContent('mainTab','mainInfo');HideContent('formTab','formInfo');HideContent('scheduleTab','scheduleInfo');">
              <bean:message key="app.pet.mainTab" />
              </a>
            </div>
          </td>
          <logic:notEqual name="petForm" property="key" value="">
            <td align="center">
            <div id="formTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');ShowContent('formTab','formInfo');HideContent('scheduleTab','scheduleInfo');">
              <bean:message key="app.pet.formTab" />
              </a>
            </div>
            </td>              
            <td align="center">
            <div id="scheduleTab" style="background:#fff; font:12;">
              <a href="javascript:HideContent('mainTab','mainInfo');HideContent('formTab','formInfo');ShowContent('scheduleTab','scheduleInfo');">
              <bean:message key="app.pet.scheduleTab" />
              </a>
            </div>
            </td>
          </logic:notEqual>          
        </tr>
      </table>
    </td> 
    <logic:notEqual name="petForm" property="key" value="">
      <td align="right" colspan="6">
        <button type="button" onClick="window.location='CustomerInvoiceReportEntry.do?customerKey=<bean:write name="petForm"
          property="customerKey" />&sourceTypeKey=<bean:message key="app.pet.id"/>&sourceKey=<bean:write name="petForm"
          property="key" />' "><bean:message key="app.pet.viewActivity" /></button>
      </td>
    </logic:notEqual>
  </tr>
</table>

<div id="mainInfo" style="display:none;">  
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <html:form action="<%=attAction%>"> 
  <logic:notEqual name="petForm" property="key" value="">
    <html:hidden property="key" /> 
    <input type="hidden" name="sourceTypeKey" value="<bean:message key="app.pet.id"/>" >
  </logic:notEqual>
  <tr>
    <td><strong><bean:message key="app.pet.petName" />:</strong></td>
    <td>     
      <html:text property="petName" size="40" maxlength="30" readonly="<%=attKeyEdit%>" />
      <html:errors property="petName" />
    </td>          
    <td><strong><bean:message key="app.pet.speciesName" />:</strong></td>
    <td>
      <html:hidden property="speciesKey" />
      <html:hidden property="speciesName" />
      <span class="ac_holder">
      <input id="speciesNameAC" size="30" maxlength="30" value="<bean:write name="petForm" property="speciesName" />" onFocus="javascript:
      var options = {
		script:'SpeciesLookUp.do?json=true&',
		varname:'speciesNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.petForm.speciesKey.value=obj.id; document.petForm.speciesName.value=obj.value; }
		};
		var json=new AutoComplete('speciesNameAC',options);return true;" value="" />
      </span>
      <html:errors property="speciesKey" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.pet.clientName" />:</strong></td>
    <td>
      <html:hidden property="customerKey" />
      <html:hidden property="clientName" />
      <span class="ac_holder">
      <input id="clientNameAC" size="40" maxlength="30" value="<bean:write name="petForm" property="clientName" />" onFocus="javascript:
      var options = {
		script:'CustomerLookUp.do?json=true&',
		varname:'lastNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.petForm.customerKey.value=obj.id; document.petForm.clientName.value=obj.value; }
		};
		var json=new AutoComplete('clientNameAC',options);return true;" value="" />
      </span>
      <html:errors property="customerKey" />
    </td>          
    <td><strong><bean:message key="app.pet.breedName" />:</strong></td>
    <td>
      <% if (!(attBreedLookup) || attDisableEdit) { %>
        <html:text property="breedName" size="30" maxlength="30" readonly="true" />
      <% } else { %>
        <html:hidden property="breedKey" />
        <html:hidden property="breedName" />
        <span class="ac_holder">
        <input id="breedNameAC" size="30" maxlength="30" value="<bean:write name="petForm" property="breedName" />" onFocus="javascript:
        var options = {
	  	script:'BreedLookUp.do?json=true&speciesKey=<bean:write name="petForm" property="speciesKey" />&',
		varname:'breedNameFilter',
		json:true,
		shownoresults:true,
		maxresults:10,
		callback: function (obj) { document.petForm.breedKey.value=obj.id; document.petForm.breedName.value=obj.value; }
		};
		var json=new AutoComplete('breedNameAC',options);return true;" value="" />
        </span>
      <% } %>
      <html:errors property="breedKey" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.pet.dvmResourceName" />:</strong></td>
    <td>
      <html:hidden property="dvmResourceKey" />
      <html:hidden property="dvmResourceName" />      
      <% if (attDisableEdit) { %>
        <html:text property="dvmResourceNameAC" size="40" maxlength="40" readonly="true" />
      <% } else { %>
        <span class="ac_holder">
          <input id="dvmResourceNameAC" size="40" maxlength="40" value="<bean:write name="petForm" property="dvmResourceName" />" onFocus="javascript:
          var options = {
            script:'ResourceLookUp.do?json=true&',
            varname:'resourceNameFilter',
            json:true,
            shownoresults:true,
            maxresults:10,
            callback: function (obj) { document.petForm.dvmResourceKey.value=obj.id; document.petForm.dvmResourceName.value=obj.value; }
          };
          var json=new AutoComplete('dvmResourceNameAC',options);return true;" value="" />
        </span>
      <% } %>
      <html:errors property="dvmResourceKey" />
    </td>
    <td>
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.pet.petSexId" />:</strong></td>
    <td>
      <html:radio property="petSexId" value="0" disabled="<%=attDisableEdit%>"/>Male
      <html:radio property="petSexId" value="1" disabled="<%=attDisableEdit%>"/>Female
      <html:errors property="petSexId" />
    </td>          
    <td><strong><bean:message key="app.pet.neuteredId" />:</strong></td>
    <td>
      <html:radio property="neuteredId" value="0" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="neuteredId" value="1" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="neuteredId" />
    </td>          
  </tr>
  <tr>
    <td><strong><bean:message key="app.pet.birthDate" />:</strong></td>
    <td>
      <html:text property="birthDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>  
        <a href="javascript:show_calendar('petForm.birthDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the pet birth date."></a>
      <% } %>
      <html:errors property="birthDate" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <strong><bean:message key="app.pet.age" />:</strong>
      <html:text property="petAge" size="2" maxlength="3" readonly="true" />
    </td>          
    <td><strong><bean:message key="app.pet.neuteredDate" />:</strong></td>
    <td>
      <html:text property="neuteredDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>            
        <a href="javascript:show_calendar('petForm.neuteredDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the pet neutered date."></a>
      <% } %>
      <html:errors property="neuteredDate" />
    </td>          
  </tr>
  <tr>
    <td><strong><bean:message key="app.pet.petWeight" />:</strong></td>
    <td>
      <html:text property="petWeight" size="5" maxlength="7"/> 
      <bean:message key="app.localeWeight" />
      <html:errors property="petWeight" />
    </td>
    <td><strong><bean:message key="app.pet.petColor" />:</strong></td>
    <td>
      <html:text property="petColor" size="25" maxlength="20"/> 
      <html:errors property="petColor" />
    </td>          
  </tr>      
  <tr>
    <td><strong><bean:message key="app.pet.identificationTagNumber" />:</strong></td>
    <td>
      <html:text property="identificationTagNumber" size="25" maxlength="20"/> 
      <html:errors property="identificationTagNumber" />
    </td>          
    <td><strong><bean:message key="app.pet.rabiesTagNumber" />:</strong></td>
    <td>
      <html:text property="rabiesTagNumber" size="25" maxlength="20" /> 
      <html:errors property="rabiesTagNumber" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.pet.disposition" />:</strong></td>
    <td colspan="2">
      <html:text property="disposition" size="25" maxlength="30" /> 
      <html:errors property="disposition" />
    </td>
    <td rowspan="3">
      <logic:notEqual name="petForm" property="key" value="">
        <div style="width:<bean:message key="app.pet.photo.width" />; height:<bean:message key="app.pet.photo.height" />;  border-width: 1px; border-style: solid; border-color: black; ">
          <logic:notEqual name="petForm" property="photoFileName" value="">          
            <img src="<bean:write name="petForm" 
              property="documentServerURL" />/<bean:message key="app.pet.id" />/<bean:write name="petForm" 
            property="key" />/<bean:write name="petForm" 
            property="photoFileName" />" width="<bean:message key="app.pet.photo.width" />" height="<bean:message key="app.pet.photo.height" />" >
          </logic:notEqual>
          <logic:equal name="petForm" property="photoFileName" value="">
            <img src="images/NoImage.jpg" width="<bean:message key="app.pet.photo.width" />" height="<bean:message key="app.pet.photo.height" />" >
          </logic:equal>
        </div>
      </logic:notEqual>
    </td>    
  </tr>
  <tr>  
    <td><strong><bean:message key="app.pet.lastCheckDate" />:</strong></td>
    <td>
      <html:text property="lastCheckDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>            
        <a href="javascript:show_calendar('petForm.lastCheckDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click to select the annual check date."></a>
      <% } %>
      <html:errors property="lastCheckDate" />
    </td> 
  </tr>  
  <tr>
    <td><strong><bean:message key="app.pet.petMemo" />:</strong></td>
    <td colspan="2">
      <html:text property="petMemo" size="60" maxlength="60" /> 
      <html:errors property="petMemo" />
    </td>   
  </tr>  
  <tr>
    <td><strong><bean:message key="app.pet.activeId" />:</strong></td>
    <td>
      <html:radio property="activeId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="activeId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="activeId" />                   
    </td>   
  </tr>  
  <tr>
    <td><strong><bean:message key="app.pet.createUser" />:</strong></td>
    <td><bean:write name="petForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.pet.createStamp" />:</strong></td>
    <td><bean:write name="petForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.pet.updateUser" />:</strong></td>
    <td><bean:write name="petForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.pet.updateStamp" />:</strong></td>
    <td><bean:write name="petForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="8" align="right">
      <input type="hidden" name="clientNameFilter" value="<bean:write name="petForm" property="clientNameFilter" />" >
      <input type="hidden" name="petNameFilter" value="<bean:write name="petForm" property="petNameFilter" />" >
      <input type="hidden" name="pageNo" value="<bean:write name="petForm" property="currentPage" />" >        
      <html:submit value="Save Pet Values" disabled="<%=attDisableEdit%>" />         
     </html:form> 
    </td>
  </tr>
</table>
</div>

<script type="text/javascript" language="JavaScript"><!--
  ShowContent('mainTab', 'mainInfo');
//--></script>

<logic:notEqual name="petForm" property="key" value="">  
<!-- ================================================================= -->
<!-- Graphical display of the pet schedule                             -->
<!-- ================================================================= -->
<div id="formInfo" style="display:none;"> 
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td width="12%">
        <strong><bean:message key="app.pet.petName" />:</strong>
      </td>
      <td>
        <bean:write name="petForm" property="petName" />
      </td>
    </tr>
    <tr>
      <td>
        <strong><bean:message key="app.pet.speciesName" />:</strong>
      </td>
      <td>
        <bean:write name="petForm" property="speciesName" />
      </td>
    </tr>    
    <tr>
      <td>
        <strong><bean:message key="app.pet.clientName" />:</strong>
      </td>
      <td>
        <bean:write name="petForm" property="clientName" />
      </td>
    </tr>
    <tr>
      <td colspan="7">
        <hr>
      </td>
    </tr>
  </table>
  <ul> 
    <li>
      <a href="#" onclick="popup = putinpopup('PdfNewPetInfo.do?key=<bean:write name="petForm" 
        property="key" />&petName=<bean:write name="petForm" 
        property="petName" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.newPetInfoTitle"/></a>
    </li>
    <logic:equal name="petForm" property="speciesName" value="Canine">
      <li>
        <a href="#" onclick="popup = putinpopup('PdfPuppyProofInfo.do?key=<bean:write name="petForm" 
          property="key" />&petName=<bean:write name="petForm" 
          property="petName" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.puppyProofInfoTitle"/></a>
      </li>
      <li>
        <a href="#" onclick="popup = putinpopup('PdfCanineVacInfo.do?key=<bean:write name="petForm" 
          property="key" />&petName=<bean:write name="petForm" 
          property="petName" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.canineVacInfoTitle"/></a>
      </li>
    </logic:equal>
    <logic:equal name="petForm" property="speciesName" value="Feline">    
      <li>
        <a href="#" onclick="popup = putinpopup('PdfFelineVacInfo.do?key=<bean:write name="petForm" 
          property="key" />&petName=<bean:write name="petForm" 
          property="petName" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.felineVacInfoTitle"/></a>
      </li>
    </logic:equal>
    <logic:equal name="petForm" property="petSexId" value="0">      
      <li>
        <a href="#" onclick="popup = putinpopup('PdfNeuterFacts.do?key=<bean:write name="petForm" 
          property="key" />&petName=<bean:write name="petForm" 
          property="petName" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.neuterFactsTitle"/></a>
      </li>
    </logic:equal>   
    <logic:equal name="petForm" property="petSexId" value="1">
      <li>
        <a href="#" onclick="popup = putinpopup('PdfSpayFacts.do?key=<bean:write name="petForm" 
          property="key" />&petName=<bean:write name="petForm" 
          property="petName" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.spayFactsTitle"/></a>
      </li>
    </logic:equal>
    <li>
      <a href="PetBoardListEntry.do?petKey=<bean:write name="petForm"
        property="key" /> "><bean:message key="app.pet.viewBoardActivity"/></a>
    </li>
    <li>
      <a href="PetExamListEntry.do?petKey=<bean:write name="petForm"
        property="key" /> "><bean:message key="app.pet.viewExamActivity"/></a>
    </li>    
    <li>
      <a href="PetVacListEntry.do?petKey=<bean:write name="petForm"
        property="key" /> "><bean:message key="app.pet.viewVacActivity"/></a>
    </li>
    <li>
      <a href="VendorResultEntry.do?sourceTypeKey=<bean:message key="app.pet.id"/>&sourceKey=<bean:write name="petForm"
        property="key" /> "><bean:message key="app.pet.viewLabActivity"/></a>
    </li>
    <logic:equal name="petForm" property="neuteredId" value="1">      
      <li>
        <a href="#" onclick="popup = putinpopup('PdfNeuterForm.do?key=<bean:write name="petForm" 
          property="key" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.neuterTitle"/></a>
      </li>
    </logic:equal>
    <li>
      <a href="#" onclick="popup = putinpopup('PdfSurgeryDischargeForm.do?key=<bean:write name="petForm" 
        property="key" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.surgeryDischargeTitle"/></a>
    </li>    
    <li>
      <a href="#" onclick="popup = putinpopup('PdfEuthanasiaForm.do?key=<bean:write name="petForm" 
        property="key" />&presentationType=pdf'); return false" target="_PDF"><bean:message key="app.pet.euthanasiaTitle"/></a>
    </li>
  </ul>
</div>

<!-- ================================================================= -->
<!-- Display of the pet schedule                                       -->
<!-- ================================================================= -->
<div id="scheduleInfo" style="display:none;"> 
  <div style="width:575px; border-width: 0px; border-style: solid; border-color: black; ">    
  <br>
  <strong><bean:message key="app.pet.petName" />:</strong>
  &nbsp;&nbsp;
  <bean:write name="petForm" property="petName" />
  <br>      
  <strong><bean:message key="app.pet.speciesName" />:</strong>
  &nbsp;&nbsp;
  <bean:write name="petForm" property="speciesName" />
  <br>  
  <strong><bean:message key="app.customer.clientName" />:</strong>
  &nbsp;&nbsp;
  <bean:write name="petForm" property="clientName" />

  <br>  
  <logic:equal name="resourceInstanceListHeadForm" property="lastRecord" value="0">
    <br>
    <bean:message key="app.schedule.noResults" />
  </logic:equal>
  
  <logic:notEqual name="resourceInstanceListHeadForm" property="lastRecord" value="0"> 
       
    <jsp:useBean id="workOrderForm" scope="session" 
      class="com.wolmerica.workorder.WorkOrderForm" />

   <%
       String eventTOD = "";     
       String startDate = "";
       int startHour = 8;
       int startMinute = 0;
       String startTime = "";
       String endDate = "";
       int endHour = 10;
       int endMinute = 0;
       String endTime = "";
  
       String scheduleKey = "";
       String service = "";
       String subject = "";
       String location = "";
       
       String pStartDate = "";
       String pStartTime = "";
       String pEndDate = "";
       String pEndTime = "";
       String pSubject = "";
       String pLocation = "";
       
       int x = 0;
       int eventCnt = 0;
       int y = 0;     
    %>
    
    <logic:iterate id="resourceInstanceForm"
                   name="resourceInstanceListHeadForm"
                   property="resourceInstanceForm"
                   scope="session"
                   type="com.wolmerica.resourceinstance.ResourceInstanceForm">  

      <% if (x == 0) { %>
        <br>
      <% } else { %>
        <hr>
      <% } 
        x++; %>
      &nbsp;
      <a href="javascript:;" onclick="ShowHideLayer('<%=x%>');"><img src="images/expand.gif" alt="Expand Me" name="btn<%=x%>" width="9" height="9" border="0" id="btn<%=x%>" /></a>
      &nbsp;<strong><bean:write name="resourceInstanceForm" property="startDate" /></strong><br>
      <div id="box<%=x%>" class="collapsible">
            
        <logic:notEqual name="resourceInstanceForm" property="eventCount" value="0"> 
      
        <% 
           pStartDate = "";
           pStartTime = "";
           pEndDate = "";
           pEndTime = "";
           pSubject = "";
           pLocation = "";
        
           eventCnt = new Integer(resourceInstanceForm.getEventCount()).intValue();          
           y = 0;
           while( y < eventCnt) { 
//--------------------------------------------------------------------------------
// Cast the array value into a workorder form to be evaluated for display purposes.
//--------------------------------------------------------------------------------
             workOrderForm = (com.wolmerica.workorder.WorkOrderForm) resourceInstanceForm.getWorkOrderForm().get(y);
             eventTOD = "AM";                       
             startDate = workOrderForm.getStartDate();
             startHour = new Integer(workOrderForm.getStartHour()).intValue();
             startMinute = new Integer(workOrderForm.getStartMinute()).intValue();
             if (startHour > 11)
               eventTOD = "PM";
             if (startHour > 12)
               startHour = startHour - 12;
             startTime = startHour + ":";
             if (startMinute < 10)
               startTime = startTime + "0";
             startTime = startTime + startMinute + eventTOD;
             
             eventTOD = "AM";                 
             endDate = workOrderForm.getEndDate();
             endHour = new Integer(workOrderForm.getEndHour()).intValue();
             endMinute = new Integer(workOrderForm.getEndMinute()).intValue();
             if (endHour > 11)
               eventTOD = "PM";
             if (endHour > 12)
               endHour = endHour - 12;
             endTime = endHour + ":";
             if (endMinute < 10)
               endTime = endTime + "0";
             endTime = endTime + endMinute + eventTOD;

             scheduleKey = workOrderForm.getScheduleKey();
             service = workOrderForm.getSourceName();             
             subject = workOrderForm.getSubject();
             location = "Customer Site";
             if (workOrderForm.getLocationId().equalsIgnoreCase("true")) {
               location = "In Office";
             } 
        %>
               
          <% if (!((pStartDate.equalsIgnoreCase(startDate)) &&
                (pStartTime.equalsIgnoreCase(startTime)) &&
                (pSubject.equalsIgnoreCase(subject)) &&
                (pLocation.equalsIgnoreCase(location)))) { %>
        
            <% if (y > 0) { %>
                 <hr>
            <% } %>
                                
               <font size="-1">
                 <strong>Start Date:</strong><%=startDate%>
                 &nbsp;<strong>Time:</strong>&nbsp;<%=startTime%>
                 <br>
                 <strong>End</strong>
                 <% if (!(startDate.equalsIgnoreCase(endDate))) { %>
                   &nbsp;<strong>Date:</strong><%=endDate%>
                 <% } %>
                 &nbsp;<strong>Time:</strong>&nbsp;<%=endTime%>
                 <br>
                 <strong>Location:</strong>&nbsp;<%=location%>
                 <br>               
                 <strong>Subject:</strong>&nbsp;<a href="ScheduleGet.do?key=<%=scheduleKey%> "><%=subject%></a>
                 <br>
                 <strong>Service:</strong>&nbsp;                 
               </font>  
          <% } else { %>
               ;&nbsp;
          <% } %>
             <font size="-1">          
               <%=service%>
             </font>
        <%
             pStartDate = startDate;
             pStartTime = startTime;
             pEndDate = endDate;
             pEndTime = endTime;
             pSubject = subject;
             pLocation = location;
                          
             y++;        
           }
        %>   
        </logic:notEqual>   
      </div>
    </logic:iterate> 
  </logic:notEqual>
  </div>
</div>
</logic:notEqual> 
