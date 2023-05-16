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
    <td><strong><bean:message key="app.export.label.businessphone2" /></strong></td>
    <td><strong><bean:message key="app.export.label.categories" /></strong></td>
    <td><strong><bean:message key="app.export.label.emailaddress" /></strong></td>
    <td><strong><bean:message key="app.export.label.notes" /></strong></td>
    <td><strong><bean:message key="app.export.label.profession" /></strong></td>
    <td><strong><bean:message key="app.export.label.webpage" /></strong></td>
  </tr>
  
  <logic:notEqual name="prospectListHeadForm" property="lastRecord" value="0"> 
    <logic:iterate id="prospectForm"
                   name="prospectListHeadForm"
                   property="prospectForm"
                   scope="session"
                   type="com.wolmerica.prospect.ProspectForm">                                  
      <tr>
        <td></td>
        <td><bean:write name="prospectForm" property="name" /></td>
        <td><bean:write name="prospectForm" property="name" /></td>
        <td><bean:write name="prospectForm" property="address" /></td>
        <td><bean:write name="prospectForm" property="address2" /></td>
        <td><bean:write name="prospectForm" property="city" /></td>
        <td><bean:write name="prospectForm" property="state" /></td>
        <td><bean:write name="prospectForm" property="zip" /></td>
        <td><bean:message key="app.export.country" /></td>
        <td><bean:write name="prospectForm" property="faxNum" /></td>
        <td><bean:write name="prospectForm" property="phoneNum" /></td>
        <td><bean:write name="prospectForm" property="mobileNum" /></td>
        <td><bean:message key="app.export.business" />;<bean:message key="app.export.prospect" />;<bean:write name="prospectForm" property="lineOfBusiness" /></td>
        <td><bean:write name="prospectForm" property="email" /></td>
        <td>
          <bean:message key="app.export.label.contact" />:&nbsp;<bean:write name="prospectForm" property="contactName" />
          <logic:notEqual name="prospectForm" property="referredBy" value="">
            &nbsp;&nbsp;<bean:message key="app.export.label.referredby" />:&nbsp;
            <bean:write name="prospectForm" property="referredBy" />
          </logic:notEqual>            
          <logic:notEqual name="prospectForm" property="noteLine1" value="">
            &nbsp;&nbsp;<bean:message key="app.export.label.note" />:&nbsp;
            <bean:write name="prospectForm" property="noteLine1" />
          </logic:notEqual> 
        <td><bean:write name="prospectForm" property="lineOfBusiness" /></td>
        <td><bean:write name="prospectForm" property="webSite" /></td>
      </tr>  
    </logic:iterate> 
  </logic:notEqual>
</table