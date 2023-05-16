<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<table>
  <tr>
    <td><strong><bean:message key="app.export.label.firstname" /></strong></td>
    <td><strong><bean:message key="app.export.label.lastname" /></strong></td>
    <td><strong><bean:message key="app.export.label.company" /></strong></td>
    <td><strong><bean:message key="app.export.label.homestreet" /></strong></td>
    <td><strong><bean:message key="app.export.label.homestreet2" /></strong></td>
    <td><strong><bean:message key="app.export.label.homecity" /></strong></td>
    <td><strong><bean:message key="app.export.label.homestate" /></strong></td>
    <td><strong><bean:message key="app.export.label.homepostalcode" /></strong></td>
    <td><strong><bean:message key="app.export.label.homecountry" /></strong></td>
    <td><strong><bean:message key="app.export.label.homefax" /></strong></td>
    <td><strong><bean:message key="app.export.label.homephone" /></strong></td>
    <td><strong><bean:message key="app.export.label.mobilephone" /></strong></td>
    <td><strong><bean:message key="app.export.label.businessphone" /></strong></td>
    <td><strong><bean:message key="app.export.label.businessextension" /></strong></td>
    <td><strong><bean:message key="app.export.label.categories" /></strong></td>
    <td><strong><bean:message key="app.export.label.emailaddress" /></strong></td>
    <td><strong><bean:message key="app.export.label.email2address" /></strong></td>
    <td><strong><bean:message key="app.export.label.notes" /></strong></td>
    <td><strong><bean:message key="app.export.label.profession" /></strong></td>
    <td><strong><bean:message key="app.export.label.webpage" /></strong></td>
  </tr>
  
  <logic:notEqual name="employeeListHeadForm" property="lastRecord" value="0">     
    <logic:iterate id="employeeListForm"
                   name="employeeListHeadForm"
                   property="employeeListForm"
                   scope="session"
                   type="com.wolmerica.employee.EmployeeListForm">                             
      <tr>
        <td><bean:write name="employeeListForm" property="firstName" /></td>
        <td><bean:write name="employeeListForm" property="lastName" /></td>
        <td><bean:write name="employeeListForm" property="employerName" /></td>
        <td><bean:write name="employeeListForm" property="address" /></td>
        <td><bean:write name="employeeListForm" property="address2" /></td>
        <td><bean:write name="employeeListForm" property="city" /></td>
        <td><bean:write name="employeeListForm" property="state" /></td>
        <td><bean:write name="employeeListForm" property="zip" /></td>
        <td><bean:message key="app.export.country" /></td>
        <td></td>
        <td><bean:write name="employeeListForm" property="phone" /></td>
        <td><bean:write name="employeeListForm" property="mobileNum" /></td>
        <td><bean:write name="employeeListForm" property="phoneNum" /></td>
        <td><bean:write name="employeeListForm" property="phoneExt" /></td>        
        <td><bean:message key="app.shortTitle" />&nbsp;<bean:message key="app.export.employee" /></td>
        <td><bean:write name="employeeListForm" property="email" /></td>
        <td><bean:write name="employeeListForm" property="email2" /></td>
        <td><bean:write name="employeeListForm" property="userName" /></td>            
        <td><bean:message key="app.export.employee" /></td>
        <td><bean:write name="employeeListForm" property="yimId" /></td> 
      </tr>  
    </logic:iterate> 
  </logic:notEqual>
</table>