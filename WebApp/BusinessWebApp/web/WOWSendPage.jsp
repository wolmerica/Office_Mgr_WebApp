<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<logic:messagesNotPresent message="true">
<!-- Display the successful confirmation notice. -->
  <h2><bean:message key="app.send.confirmation"/></h2>
  
  <%
    if (request.getAttribute("sendToMap") == null) {
  %>
      <bean:message key="app.missing.attribute"/>
  <%
    } else {
//-----------------------------------------------------------------------------  
// Traverse the HashMap to find out how many messages were sent.
//-----------------------------------------------------------------------------
      java.util.HashMap sendToMap = null;
      java.util.Iterator itr = null;
      java.util.Set sendToKeys = null;
      String sendToNow = "";
      int i = 0;
    
      sendToMap = (java.util.HashMap) request.getAttribute("sendToMap");
      sendToKeys = sendToMap.keySet();
      itr = sendToKeys.iterator();
      while(itr.hasNext()) {
        sendToNow = (String) (itr.next());
        i++;
      }
    
    %>

    <p>
    <bean:message key="app.send.phrase1"/>&nbsp;<strong><%=i%></strong>
    <bean:message key="app.send.phrase2"/>&nbsp;<strong><%=sendToMap.size()%></strong>
    <bean:message key="app.send.phrase3"/>.
    <%
//-----------------------------------------------------------------------------
// Traverse the HashMap to display the message recipients.
//-----------------------------------------------------------------------------
      itr = sendToKeys.iterator();
      while(itr.hasNext()) {
        sendToNow = (String) (itr.next());
     %>
        <br>
        <%=sendToMap.get(sendToNow)%>-<%=sendToNow%>
     <%
      }
    }    
  %>  
  
  </p>
</logic:messagesNotPresent>

<!-- Display the IM error messages from application. -->
<logic:messagesPresent message="true">  
<h2><bean:message key="app.send.error"/></h2>    
  <table>
    <tr>
      <html:messages id="message" message="true">
      <td>
        <bean:message key="messages.header" />    
        <bean:write name="message"/>
        <bean:message key="messages.footer" />          
      </td>
      </html:messages>
    </tr>
  </table>  
</logic:messagesPresent>
