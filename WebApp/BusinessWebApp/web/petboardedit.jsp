<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<script src="./js/date_picker.js" language="JavaScript" type="text/javascript"></script>
<script src="./js/overlib_mini.js" language="JavaScript" type="text/javascript" ><!-- overLIB (c) Erik Bosrup --></script>

<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>  

<%
  String currentColor = "ODD";
  int i = -1;      
  String attTitle = "app.petboard.addTitle";            
  String attAction = "/PetBoardAdd";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }     
%> 

<logic:notEqual name="petBoardForm" property="key" value="">
  <%
    attTitle = "app.petboard.editTitle";
    attAction = "/PetBoardEdit";
    attKeyEdit = true;
  %>
</logic:notEqual>

<logic:notEqual name="petBoardForm" property="permissionStatus" value="LOCKED">
<bean:message key="app.readonly"/>
  <logic:notEqual name="petBoardForm" property="permissionStatus" value="READONLY">
    [<bean:write name="petBoardForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <button type="button" onClick="window.location='PetBoardListEntry.do?petKey=<bean:write name="petBoardForm" property="petKey" />' "><bean:message key="app.petboard.backMessage" /></button>
    </td>
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
</table>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >  
  <html:form action="<%=attAction%>"> 
  <logic:notEqual name="petBoardForm" property="key" value="">
    <html:hidden property="key" /> 
    <input type="hidden" name="sourceTypeKey" value="<bean:message key="app.pet.id"/>" >
  </logic:notEqual>
  <tr> 
    <td><strong><bean:message key="app.petboard.customer" />:</strong></td>
    <td>
      <html:text property="clientName" size="40" maxlength="40" readonly="true" /> 
      <html:errors property="clientName" />
    </td>        
    <td><strong><bean:message key="app.petboard.checkInDate" />:</strong></td>    
    <td>      
      <logic:notEqual name="petBoardForm" property="scheduleKey" value="">        
        <html:text property="checkInDate" size="10" maxlength="10" readonly="true" />
        <a href="ScheduleGet.do?key=<bean:write name="petBoardForm" property="scheduleKey" /> "><img src="./images/prev.gif" width="16" height="16" border="0" title="Click to edit boarding event."></a>
        <html:errors property="checkInDate" />
      </logic:notEqual>
    </td>    
  </tr>
  <tr>
    <td><strong><bean:message key="app.petboard.petName" />:</strong></td>
    <td>
      <html:text property="petName" size="40" maxlength="40" readonly="true" /> 
      <html:errors property="petName" />
    </td>      
    <td><strong><bean:message key="app.petboard.checkInTime" />:</strong></td>        
    <td>
      <html:select property="checkInHour" disabled="true">
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
      <html:select property="checkInMinute" disabled="true">
        <html:option value="0">:00</html:option>
        <html:option value="15">:15</html:option>
        <html:option value="30">:30</html:option>
        <html:option value="45">:45</html:option>
      </html:select>        
    </td>    
  </tr> 
  <tr>
    <td><strong><bean:message key="app.petboard.boardReason" />:</strong></td>
    <td>
      <html:text property="boardReason" size="40" maxlength="40" readonly="<%=attDisableEdit%>" />
      <html:errors property="boardReason" />
    </td>      
    <td><strong><bean:message key="app.petboard.checkOutDate" />:</strong></td>    
    <td>      
      <html:text property="checkOutDate" size="10" maxlength="10" readonly="true" />
      <html:errors property="checkOutDate" />
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.petboard.checkOutTo" />:</strong></td>
    <td>
      <html:text property="checkOutTo" size="40" maxlength="40" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="checkOutTo" />
    </td>      
    <td><strong><bean:message key="app.petboard.checkOutTime" />:</strong></td>        
    <td>
      <html:select property="checkOutHour" disabled="true">
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
      </html:select>
      <html:select property="checkOutMinute" disabled="true">
        <html:option value="0">:00</html:option>
        <html:option value="15">:15</html:option>
        <html:option value="30">:30</html:option>
        <html:option value="45">:45</html:option>
      </html:select>        
    </td>
  </tr>
  <tr>
    <td><strong><bean:message key="app.petboard.emergencyName" />:</strong></td>
    <td>
      <html:text property="emergencyName" size="40" maxlength="40" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="emergencyName" />
    </td>
    <td><strong><bean:message key="app.petboard.emergencyPhone" />:</strong></td>
    <td>
      <html:text property="emergencyPhone" size="14" maxlength="14" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="emergencyPhone" />
    </td>    
  </tr>      
  <tr>
    <td rowspan="4"><strong><bean:message key="app.petboard.boardInstruction" />:</strong></td>
    <td rowspan="4">
      <html:textarea property="boardInstruction" cols="30" rows="4" readonly="<%=attDisableEdit%>" />
      <html:errors property="boardInstruction" />
    </td>
    <td><strong><bean:message key="app.petboard.specialDietId" />:</strong></td>
    <td>
      <html:radio property="specialDietId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="specialDietId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="specialDietId" />
    </td>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.petboard.medicationId" />:</strong></td>
    <td>
      <html:radio property="medicationId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="medicationId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="medicationId" />
    </td>      
  </tr>
  <tr>  
    <td><strong><bean:message key="app.petboard.vaccinationId" />:</strong></td>
    <td>
      <html:radio property="vaccinationId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="vaccinationId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="vaccinationId" />
    </td>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.petboard.serviceId" />:</strong></td>
    <td>
      <html:radio property="serviceId" value="false" disabled="<%=attDisableEdit%>"/>No
      <html:radio property="serviceId" value="true" disabled="<%=attDisableEdit%>"/>Yes
      <html:errors property="serviceId" />
    </td>      
  </tr>  
  <tr>
    <td><strong><bean:message key="app.petboard.createUser" />:</strong></td>
    <td><bean:write name="petBoardForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.petboard.createStamp" />:</strong></td>
    <td><bean:write name="petBoardForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.petboard.updateUser" />:</strong></td>
    <td><bean:write name="petBoardForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.petboard.updateStamp" />:</strong></td>
    <td><bean:write name="petBoardForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="8" align="right">
      <html:submit value="Save Boarding Values" disabled="<%=attDisableEdit%>" />         
     </html:form> 
    </td>
  </tr>
</table> 
