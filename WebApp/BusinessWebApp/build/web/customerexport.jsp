<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<table>
  <tr>
    <td><strong><bean:message key="app.export.label.firstname" /></strong></td>
    <td><strong><bean:message key="app.export.label.lastname" /></strong></td>
    <td><strong><bean:message key="app.export.label.company" /></strong></td>
    <td><strong><bean:message key="app.export.label.businessstreet" /></strong></td>
    <td><strong><bean:message key="app.export.label.businessstreet2" /></strong></td>
    <td><strong><bean:message key="app.export.label.businesscity" /></strong></td>
    <td><strong><bean:message key="app.export.label.businessstate" /></strong></td>
    <td><strong><bean:message key="app.export.label.businesspostalcode" /></strong></td>
    <td><strong><bean:message key="app.export.label.businesscountry" /></strong></td>
    <td><strong><bean:message key="app.export.label.businessfax" /></strong></td>
    <td><strong><bean:message key="app.export.label.businessphone" /></strong></td>
    <td><strong><bean:message key="app.export.label.businessextension" /></strong></td>    
    <td><strong><bean:message key="app.export.label.mobilephone" /></strong></td>
    <td><strong><bean:message key="app.export.label.categories" /></strong></td>
    <td><strong><bean:message key="app.export.label.emailaddress" /></strong></td>
    <td><strong><bean:message key="app.export.label.email2address" /></strong></td>
    <td><strong><bean:message key="app.export.label.notes" /></strong></td>
    <td><strong><bean:message key="app.export.label.profession" /></strong></td>
    <td><strong><bean:message key="app.export.label.webpage" /></strong></td>
  </tr>
  
  <logic:notEqual name="customerListHeadForm" property="lastRecord" value="0"> 
    <logic:iterate id="customerListForm"
                   name="customerListHeadForm"
                   property="customerListForm"
                   scope="session"
                   type="com.wolmerica.customer.CustomerListForm">                               
      <tr>
        <td><bean:write name="customerListForm" property="firstName" /></td>
        <td><bean:write name="customerListForm" property="lastName" /></td>
        <td><bean:write name="customerListForm" property="clientName" /></td>
        <td><bean:write name="customerListForm" property="address" /></td>
        <td><bean:write name="customerListForm" property="address2" /></td>
        <td><bean:write name="customerListForm" property="city" /></td>
        <td><bean:write name="customerListForm" property="state" /></td>
        <td><bean:write name="customerListForm" property="zip" /></td>
        <td><bean:message key="app.export.country" /></td>
        <td><bean:write name="customerListForm" property="faxNum" /></td>
        <td><bean:write name="customerListForm" property="phoneNum" /></td>
        <td><bean:write name="customerListForm" property="phoneExt" /></td>        
        <td><bean:write name="customerListForm" property="mobileNum" /></td>
        <td><bean:message key="app.export.business" />;<bean:message key="app.export.customer" />;<bean:write name="customerListForm" property="lineOfBusiness" /></td>
        <td><bean:write name="customerListForm" property="email" /></td>
        <td><bean:write name="customerListForm" property="email2" /></td>
        <td>
          <bean:message key="app.export.label.contact" />:&nbsp;<bean:write name="customerListForm" property="contactName" />
          <logic:notEqual name="customerListForm" property="referredBy" value="">
            &nbsp;&nbsp;<bean:message key="app.export.label.referredby" />:&nbsp;
            <bean:write name="customerListForm" property="referredBy" />
          </logic:notEqual>            
          <logic:notEqual name="customerListForm" property="noteLine1" value="">
            &nbsp;&nbsp;<bean:message key="app.export.label.note" />:&nbsp;
            <bean:write name="customerListForm" property="noteLine1" />
          </logic:notEqual>
          <logic:notEqual name="customerListForm" property="noteLine2" value="">
            &nbsp;&nbsp;<bean:write name="customerListForm" property="noteLine2" />
          </logic:notEqual>          
        </td>
        <td><bean:write name="customerListForm" property="lineOfBusiness" /></td>
        <td><bean:write name="customerListForm" property="webSite" /></td>
      </tr>  
    </logic:iterate> 
  </logic:notEqual>
</table>