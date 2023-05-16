<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
  <tr>
<table>
    
<h2><bean:message key="app.attachment.confirmation"/></h2>

<p>
    <bean:message key="app.attachment.phrase1"/>&nbsp;<strong><%=request.getAttribute("attachmentCnt")%></strong>&nbsp;<bean:message key="app.attachment.phrase2"/>;
</p>
