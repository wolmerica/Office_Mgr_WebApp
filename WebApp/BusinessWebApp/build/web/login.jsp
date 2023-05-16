<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<LINK REL=STYLESHEET HREF="css/style_sheet.css">

<table>
  <tr>
    <td>
        <img src="./images/Wolmerica_Logo.gif" border="0" title="Wolmerica Logo">
    </td>
    <td rowspan="2" valign="bottom">
      <img src="./images/Business_Logo.gif" border="0" title="Business Logo">
    </td>
  </tr>
  <tr>
    <td>
      <bean:message key="app.access" />
      &nbsp;
      <bean:message key="app.title" />
      &nbsp;
      <bean:message key="app.for" />
      &nbsp;
    </td>
  </tr>
</table>
				
<h1><bean:message key="app.signin" />:</h1>  	

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

<fieldset>
<legend>Login Form</legend>
			
<html:form action="/Login" focus="userName">
  <table id="wreglgtb" summary="form: login information">
    <tr>
      <th><label for="userName"><bean:message key="app.employee.userName"/>:</label></th>
      <td>
        <html:text  property="userName" size="16" maxlength="16" />
        <html:errors property="userName" />
      </td>
    </tr>
    <tr>
      <th><label for="passwd"><bean:message key="app.employee.password"/>:</label></th>
      <td>
        <html:password property="password" size="16" maxlength="16" />
        <html:errors property="password" />      
      </td>
    </tr>
    <tr>
      <td class="right">
        <html:submit>
	  <bean:message key="button.submit"/>
	</html:submit>
      </td>
      <td class="right">
        <html:reset>
          <bean:message key="button.reset"/>
        </html:reset>
      </td>
    </tr>
  </table>
</html:form>
</fieldset>