<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>



<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
   <tr>
      <td colspan="7"> 
         <hr>
      </td>
   </tr>
</table>
<small>
  <bean:message key="app.copyright" />
</small>

<!-- Popup to present a message to the user. -->
<% if (request.getAttribute("popupMessage") != null) { %>
  <script language="JavaScript" type="text/javascript">
    alert( "<%=request.getAttribute("popupMessage").toString()%>" );
  </script>
<% } %>
