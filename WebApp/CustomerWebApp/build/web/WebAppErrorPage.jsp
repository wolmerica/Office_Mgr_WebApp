<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>


<!-- Display messages from application. -->
<table>
<logic:messagesPresent message="true">
  <tr>
    <html:messages id="message" message="true">
    <td>
      <bean:message key="messages.header" />    
      <bean:write name="message"/>
      <bean:message key="messages.footer" />          
    </td>
    </html:messages>
  </tr>
</logic:messagesPresent>
</table>


