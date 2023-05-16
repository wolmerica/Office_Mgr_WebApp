<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>


<!-- Display messages from application. -->
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
<logic:messagesPresent message="true">
  <html:messages id="message" message="true">    
    <tr>
      <td>
        <bean:message key="messages.header" />    
        <bean:write name="message"/>
        <bean:message key="messages.footer" />          
      </td>
    </tr>
  </html:messages>    
</logic:messagesPresent>
</table>


