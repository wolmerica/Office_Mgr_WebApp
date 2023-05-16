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
    <td><strong><bean:message key="app.export.label.businessphone2" /></strong></td>
    <td><strong><bean:message key="app.export.label.categories" /></strong></td>
    <td><strong><bean:message key="app.export.label.emailaddress" /></strong></td>
    <td><strong><bean:message key="app.export.label.email2address" /></strong></td>    
    <td><strong><bean:message key="app.export.label.notes" /></strong></td>
    <td><strong><bean:message key="app.export.label.profession" /></strong></td>
    <td><strong><bean:message key="app.export.label.webpage" /></strong></td>
  </tr>
  
  <logic:notEqual name="vendorListHeadForm" property="lastRecord" value="0">      
    <logic:iterate id="vendorListForm"
                   name="vendorListHeadForm"
                   property="vendorListForm"
                   scope="session"
                   type="com.wolmerica.vendor.VendorListForm">                                    
      <tr>
        <td></td>
        <td><bean:write name="vendorListForm" property="name" /></td>
        <td><bean:write name="vendorListForm" property="name" /></td>
        <td><bean:write name="vendorListForm" property="address" /></td>
        <td><bean:write name="vendorListForm" property="address2" /></td>
        <td><bean:write name="vendorListForm" property="city" /></td>
        <td><bean:write name="vendorListForm" property="state" /></td>
        <td><bean:write name="vendorListForm" property="zip" /></td>
        <td><bean:message key="app.export.country" /></td>
        <td><bean:write name="vendorListForm" property="faxNum" /></td>
        <td><bean:write name="vendorListForm" property="phoneNum" /></td>
        <td><bean:write name="vendorListForm" property="phoneExt" /></td>        
        <td></td>
        <td><bean:message key="app.export.business" />;<bean:message key="app.export.suppliers" /></td>
        <td><bean:write name="vendorListForm" property="email" /></td>
        <td><bean:write name="vendorListForm" property="email2" /></td>        
        <td>      
          <bean:message key="app.export.label.contact" />:&nbsp;<bean:write name="vendorListForm" property="contactName" />&nbsp;&nbsp;<bean:message key="app.export.label.account" />:&nbsp;<bean:write name="vendorListForm" property="acctNum" />
          <logic:notEqual name="vendorListForm" property="terms" value="">            
            &nbsp;&nbsp;<bean:message key="app.export.label.terms" />:
            &nbsp;<bean:write name="vendorListForm" property="terms" />
          </logic:notEqual>
        </td>
        <td><bean:message key="app.shortTitle" />&nbsp;<bean:message key="app.export.vendor" /></td>
        <td><bean:write name="vendorListForm" property="webSite" /></td>
      </tr>  
    </logic:iterate> 
  </logic:notEqual>  
</table>