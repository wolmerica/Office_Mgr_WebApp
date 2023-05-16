<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<link rel="stylesheet" href="css/autocomplete.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="css/autosuggest_inquisitor.css" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/autocomplete.js"></script>
<script type="text/javascript" src="js/bsn.AutoSuggest_2.1.3_comp.js" charset="utf-8"></script>

<script src="./js/date_picker.js" type="text/javascript"></script>

<%
  String attTitle = "app.promotion.addTitle";            
  String attAction = "/PromotionAdd";
  String attReplicateAction = "";
  boolean attKeyEdit = false;
  
  boolean attDisableEdit = true;
  if (!(request.getAttribute("disableEdit") == null)) {        
     if (request.getAttribute("disableEdit").toString().compareToIgnoreCase("false") == 0) {
       attDisableEdit = false;
     }
  }
%> 

<logic:notEqual name="promotionForm" property="key" value="">
  <%
    attTitle = "app.promotion.editTitle";
    attReplicateAction = attAction;    
    attAction = "/PromotionEdit";
    
    attKeyEdit = true;
  %>
          
</logic:notEqual>

<logic:notEqual name="promotionForm" property="permissionStatus" value="LOCKED">
  <% attDisableEdit = true; %>
<bean:message key="app.readonly"/>
  <logic:notEqual name="promotionForm" property="permissionStatus" value="READONLY">
    [<bean:write name="promotionForm" property="permissionStatus" />]
  </logic:notEqual>
</logic:notEqual>
  
<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr>
    <td colspan="2">
      <form action="PromotionList.do" name="promotionList" method="post">
        <% if (request.getParameter("promoNameFilter") != null) { %>
          <input type="hidden" name="promoNameFilter" value="<%=request.getParameter("promoNameFilter")%>">
        <% } %>
        <% if (request.getParameter("promoCategoryFilter") != null) { %>
          <input type="hidden" name="promoCategoryFilter" value="<%=request.getParameter("promoCategoryFilter")%>">
        <% } %>        
        <% if (request.getParameter("promoFromDate") != null) { %>
          <input type="hidden" name="promoFromDate" value="<%=request.getParameter("promoFromDate")%>">
        <% } %>        
        <% if (request.getParameter("promoToDate") != null) { %>
          <input type="hidden" name="promoToDate" value="<%=request.getParameter("promoToDate")%>">
        <% } %>                
        <% if (request.getParameter("pageNo") != null) { %>
          <input type="hidden" name="pageNo" value="<%=request.getParameter("pageNo")%>">
        <% } %>
        <input type="submit" value="<bean:message key="app.promotion.backMessage" />">
      </form> 
    </td>  
  </tr>
  <tr>
    <td colspan="8">
      <hr>
    </td>
  </tr>
<html:form action="<%=attAction%>"> 
<logic:notEqual name="promotionForm" property="key" value="">
  <html:hidden property="key" />  
</logic:notEqual>
  <% if (request.getParameter("promoNameFilter") != null) { %>
    <input type="hidden" name="promoNameFilter" value="<%=request.getParameter("promoNameFilter")%>">
  <% } %>
  <% if (request.getParameter("promoCategoryFilter") != null) { %>
    <input type="hidden" name="promoCategoryFilter" value="<%=request.getParameter("promoCategoryFilter")%>">
  <% } %>   
  <% if (request.getParameter("promoFromDate") != null) { %>
    <input type="hidden" name="promoFromDate" value="<%=request.getParameter("promoFromDate")%>">
  <% } %>   
  <% if (request.getParameter("promoToDate") != null) { %>
    <input type="hidden" name="promoToDate" value="<%=request.getParameter("promoToDate")%>">
  <% } %>     
  <% if (request.getParameter("pageNo") != null) { %>
    <input type="hidden" name="pageNo" value="<%=request.getParameter("pageNo")%>">
  <% } %>
  <tr>
    <td><strong><bean:message key="app.promotion.name" />:</strong></td>
    <td>     
      <html:text property="name" size="30" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="name" />
    </td>
    <td><strong><bean:message key="app.promotion.category" />:</strong></td>
    <td>     
      <html:text property="category" size="30" maxlength="30" readonly="<%=attDisableEdit%>" /> 
      <html:errors property="category" />
    </td>                    
  </tr>
  <tr>
    <td><strong><bean:message key="app.promotion.startDate" /></strong></td>
    <td>
      <html:text property="startDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>            
        <a href="javascript:show_calendar('promotionForm.startDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the promotion start date."></a>
      <% } %>        
      <html:errors property="startDate" />
    </td>
    <td><strong><bean:message key="app.promotion.allItemsId" /></strong></td>
    <td>
      <html:radio property="allItemsId" value="false" disabled="<%=attDisableEdit%>" />No
      <html:radio property="allItemsId" value="true" disabled="<%=attDisableEdit%>" />Yes
    </td>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.promotion.endDate" /></strong></td>
    <td>
      <html:text property="endDate" size="10" maxlength="10" readonly="<%=attDisableEdit%>" />
      <% if (!(attDisableEdit)) { %>            
        <a href="javascript:show_calendar('promotionForm.endDate');"><img src="./images/cal.gif" width="16" height="16" border="0" alt="Click here to select the promotion end date."></a>
      <% } %>                
      <html:errors property="endDate" />
    </td>
    <td><strong><bean:message key="app.promotion.allServicesId" /></strong></td>    
    <td>
      <html:radio property="allServicesId" value="false" disabled="<%=attDisableEdit%>" />No
      <html:radio property="allServicesId" value="true" disabled="<%=attDisableEdit%>" />Yes
    </td>      
  </tr>
  <tr>
    <td><strong><bean:message key="app.promotion.discountRate" /></strong></td>
    <td>
      <html:text property="discountRate" size="2" readonly="<%=attDisableEdit%>" />        
      <bean:message key="app.localePercent" />
      <html:errors property="discountRate" />
    </td>
  </tr>      
  <tr>
    <td><strong><bean:message key="app.promotion.noteLine1" />:</strong></td>
    <td colspan="4">   
      <html:text property="noteLine1" size="60" maxlength="60" readonly="<%=attDisableEdit%>" />
      <html:errors property="noteLine1" />
    </td>   
  </tr>
  <tr>
    <td><strong><bean:message key="app.promotion.createUser" />:</strong></td>
    <td><bean:write name="promotionForm" property="createUser" /></td>	          	
    <td><strong><bean:message key="app.promotion.createStamp" />:</strong></td>
    <td><bean:write name="promotionForm" property="createStamp" /></td>	             	    
  </tr>
  <tr>
    <td><strong><bean:message key="app.promotion.updateUser" />:</strong></td>
    <td><bean:write name="promotionForm" property="updateUser" /></td>	               
    <td><strong><bean:message key="app.promotion.updateStamp" />:</strong></td>
    <td><bean:write name="promotionForm" property="updateStamp" /></td>	             
  </tr>	
  <tr>
    <td colspan="8" align="right">
      <html:submit value="Save Promotion Values" disabled="<%=attDisableEdit%>" />         
     </html:form> 
    </td>
  </tr>
</table>

<!-- ================================================================= -->
<!-- List the promotion detail                                         -->
<!-- ================================================================= -->
<logic:notEqual name="promotionForm" property="key" value="">
  <%
    String currentColor = "ODD";
    int i = -1;
    String discountRateBase = "discountRate";
    String discountRateError;
  %>
  <table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
    <tr>
      <td colspan="10">
        <hr>
      </td>
    </tr>
    <tr align="left">
      <th><bean:message key="app.promotiondetail.bundlename" /></th>
      <th><bean:message key="app.promotiondetail.bundlecategory" /></th>
      <th><bean:message key="app.promotiondetail.combodiscountid" /></th>
      <th><bean:message key="app.promotiondetail.discountRate" /></th>
      <td></td>
      <td align="right">
        <% if (!(attDisableEdit)) { %>
          <button type="button" onClick="window.location='BundleList.do?promoKey=<bean:write name="promotionForm"
             property="key" />&key=<bean:write name="promotionForm"
             property="key" />' "><bean:message key="app.workorder.addBundle" /></button>
        <% } else { %>
          <button type="button" disabled><bean:message key="app.workorder.addBundle" /></button>
        <% } %>
      </td>
    </tr>
    <tr>
      <td colspan="10">
        <hr>
      </td>
    </tr>
    <html:form action="/PromotionDetailEdit">
    <input type="hidden" name="key" value="<bean:write name="promotionForm" property="key" />" >
    <logic:notEqual name="promotionDetailListHeadForm" property="recordCount" value="0">
      <logic:iterate id="promotionDetailForm"
                     name="promotionDetailListHeadForm"
                     property="promotionDetailForm"
                     scope="session"
                     type="com.wolmerica.promotiondetail.PromotionDetailForm">
      <%
        ++i;
          if ( i % 2 == 0) { currentColor = "ODD"; }
          else { currentColor = "EVEN"; }
          discountRateError = discountRateBase + i;
      %>
      <tr>
        <td>
          <a href="BundleGet.do?key=<bean:write name="promotionDetailForm"
            property="bundleKey" /> "><bean:write name="promotionDetailForm" property="bundleName" /></a>
        </td>
        <td>
          <bean:write name="promotionDetailForm" property="bundleCategory" />
        </td>
        <td>
          <html:radio name="promotionDetailForm" indexed="true" property="comboDiscountId" value="false" disabled="<%=attDisableEdit%>" />No
          <html:radio name="promotionDetailForm" indexed="true" property="comboDiscountId" value="true" disabled="<%=attDisableEdit%>" />Yes
        </td>
        <td>
          <html:text name="promotionDetailForm" indexed="true" property="discountRate" size="2" readonly="<%=attDisableEdit%>" />
          <bean:message key="app.localePercent" />
          <html:errors property="<%=discountRateError%>" />
        </td>
        <td colspan="3" align="right">
          <a href="PromotionDetailDelete.do?key=<bean:write name="promotionForm"
                  property="key" />&promoDetailKey=<bean:write name="promotionDetailForm"
                  property="key" />"  onclick="return confirm('<bean:message key="app.promotiondetail.delete.message" />')" ><bean:message key="app.delete" /></a>

      </tr>
      </logic:iterate>
      <tr>
        <td align="right" colspan="10">
          <html:submit value="Save Promotion Detail" disabled="<%=attDisableEdit%>" />
        </td>
      </tr>
    </logic:notEqual>
    </html:form>
  </table>
</logic:notEqual>