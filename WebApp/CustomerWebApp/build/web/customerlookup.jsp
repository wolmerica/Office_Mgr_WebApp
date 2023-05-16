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

  function set_parent_value(key, acctname) {
    if (testIsValidObject(opener))
      {
      var myDoc = opener.document;   
      if (testIsValidObject(myDoc))
      {   
        if (testIsValidObject(myDoc.customerAccountingListHeadForm))
        {
           myDoc.customerAccountingListHeadForm.customerKeyFilter.value = key;
           myDoc.customerAccountingListHeadForm.acctName.value = acctname;
        }         
        if (testIsValidObject(myDoc.itemSaleListHeadForm))
        {
           myDoc.itemSaleListHeadForm.customerKeyFilter.value = key;
           myDoc.itemSaleListHeadForm.acctName.value = acctname;
        }        
        if (testIsValidObject(myDoc.serviceSaleListHeadForm))
        {
           myDoc.serviceSaleListHeadForm.customerKeyFilter.value = key;
           myDoc.serviceSaleListHeadForm.acctName.value = acctname;
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
 
<logic:equal name="customerListHeadForm" property="previousPage" value="0">
   <% attDisablePrevButton = "disabled"; %>
</logic:equal>

<logic:equal name="customerListHeadForm" property="nextPage" value="0">
   <% attDisableNextButton = "disabled"; %>
</logic:equal>      
      
<table width="290" border="0" cellspacing="1" cellpadding="1">
  <tr align="left" class="EVEN">
    <th>&nbsp;</th>
    <th><bean:message key="app.customer.acctNum" /></th>
    <th colspan="2"><bean:message key="app.customer.acctName" /></th>
  <tr>  
<logic:notEqual name="customerListHeadForm" property="lastRecord" value="0"> 
  <!-- iterate over the customers -->       
  <logic:iterate id="customerListForm"
                 name="customerListHeadForm"
                 property="customerListForm"
                 scope="session"
                 type="com.wolmerica.customer.CustomerListForm">
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
	    <a href="javascript:set_parent_value(<bean:write name="customerListForm" property="key" />, '<bean:write name="customerListForm" property="acctName" />')"><img src="./images/next.gif" width="16" height="16" border="0" title="Click to select an account.">        
	  </td>
	  <td> 	  
            <bean:write name="customerListForm" property="acctNum" />
	  </td>
	  <td colspan="2">
            <bean:write name="customerListForm" property="acctName" />          
	  </td>
	</tr>	
  </logic:iterate> 
</logic:notEqual>
  <tr>
    <td>
    </td>
    <td>
      <form action="CustomerLookUp.do" name="customerFilter" method="post">  
        <input type="hidden" name=pageNo value="<bean:write name="customerListHeadForm" property="previousPage" />" >       
        <input type="submit" value="Previous" <%=attDisablePrevButton%> >
      </form>
    </td>
    <td>
      <strong><bean:message key="app.result.title" /></strong>&nbsp;
      <bean:write name="customerListHeadForm" property="firstRecord" />
      <strong><bean:message key="app.result.thru" /></strong>
      <bean:write name="customerListHeadForm" property="lastRecord" />
      &nbsp;<strong><bean:message key="app.result.of" /></strong>&nbsp;
      <bean:write name="customerListHeadForm" property="recordCount" />      
    </td>    
    
    <td align="right">
      <form action="CustomerLookUp.do" name="customerFilter" method="post">  
        <input type="hidden" name=pageNo value="<bean:write name="customerListHeadForm" property="nextPage" />" >
        <input type="submit" value="Next" <%=attDisableNextButton%> >
      </form>
    </td>  
  </tr>
</table>