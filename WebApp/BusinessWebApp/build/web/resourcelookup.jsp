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

  function set_parent_value(key, resourcename) {
    if (testIsValidObject(opener))
    {
      var myDoc = opener.document;   
      if (testIsValidObject(myDoc))
      {
        if (testIsValidObject(myDoc.petAnestheticListHeadForm))
        {           
           <% if (request.getParameter("entityKey") != null) { %>        
             myDoc.petAnestheticListHeadForm.elements[<%=request.getParameter("entityKey")%>].value = key;
             myDoc.petAnestheticListHeadForm.elements[<%=request.getParameter("entityNameKey")%>].value = resourcename;
           <% } %>             
        }
      }
      opener.focus();
    }
    window.close();
  }
  
</SCRIPT>
  
<%
  String currentColor = "ODD";
  int i = 0;
  String attDisablePrevButton = "";   
  String attDisableNextButton = "";
%>   
 
<logic:equal name="resourceListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="resourceListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>      
      
<table width="290" border="0" cellspacing="1" cellpadding="1">
  <tr align="left" class="EVEN">
    <th colspan="2">
      &nbsp;&nbsp;&nbsp;      
      <bean:message key="app.resource.resourceName" />
    </th>
    <th><bean:message key="app.resource.description" /></th>
  <tr>      
<logic:notEqual name="resourceListHeadForm" property="lastRecord" value="0"> 
  <logic:iterate id="resourceForm"
                 name="resourceListHeadForm"
                 property="resourceForm"
                 scope="session"
                 type="com.wolmerica.resource.ResourceForm">                     
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
	  <td colspan="2">          
	    <a href="javascript:set_parent_value(<bean:write name="resourceForm" property="key" />, '<bean:write name="resourceForm" property="resourceName" />')"><img src="./images/next.gif" width="16" height="16" border="0" title="Click to select resource."></a>
            &nbsp;
            <bean:write name="resourceForm" property="resourceName" />
	  </td>            
	  <td>
            <bean:write name="resourceForm" property="description" />
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
  <tr>
    <td colspan="3">
      <hr>
    </td>
  </tr>
  <tr>
    <td>
      <button type="button" <%=attDisablePrevButton%> onClick="window.location='ResourceLookUp.do?pageNo=<bean:write name="resourceListHeadForm" 
        property="previousPage" />' "><bean:message key="app.previous" /></button>      
    </td>
    <td>
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="resourceListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="resourceListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="resourceListHeadForm" property="recordCount" />      
    </td>    
    <td align="right">
      <button type="button" <%=attDisableNextButton%> onClick="window.location='ResourceLookUp.do?pageNo=<bean:write name="resourceListHeadForm" 
        property="nextPage" />' "><bean:message key="app.next" /></button>      
    </td>  
  </tr>
</table>