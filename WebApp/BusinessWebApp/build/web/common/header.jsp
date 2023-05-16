<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<%@page  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<title><bean:message key="app.title" /></title>

<META http-equiv=Content-Type content="text/html; charset=utf-8">
<META content="MSHTML 6.00.2900.2912" name=GENERATOR>
<META http-equiv="Refresh" content="600;URL=Logout.do?type=auto">

<LINK REL=STYLESHEET HREF="css/style_sheet.css">

<script src="./js/popup_window.js" type="text/javascript"></script>

<logic:notPresent name="USER">
  <logic:forward name="login" />
</logic:notPresent>

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr bgcolor="<bean:message key="app.banner.bgColor" />"> 
    <td  colspan="7" height="50" width="30%">
      <% if (request.getSession().getAttribute("USER") != null) { %>
        &nbsp;&nbsp;&nbsp;<a href="MenuEntry.do"><strong><font color="<bean:message key="app.banner.fontColor" />"><bean:message key="app.menu.link" /></font></strong></a>
      <% } %>
    </td>
    <td align="center" width="40%">
      <font size=+1 color="<bean:message key="app.banner.fontColor" />">
        <strong><bean:message key="app.title" /></strong>
      </font>
      <% if (request.getSession().getAttribute("INSTANCENAME") != null) { %>
        <br>
        <font color="<bean:message key="app.banner.fontColor" />">
          <strong><%=request.getSession().getAttribute("INSTANCENAME")%></strong>
        </font>
      <% } %>
    </td>
    <td align="right" width="30%">
      <% if (request.getSession().getAttribute("USER") != null) { %>
        <a href="Logout.do?type=user"><strong><font color="<bean:message key="app.banner.fontColor" />"><bean:message key="app.logout.link" /></font></strong></a>&nbsp;&nbsp;&nbsp;&nbsp;
        <br>
        <br>
        <font color="<bean:message key="app.banner.fontColor" />"><bean:message key="app.welcome" />,&nbsp;&nbsp;<strong><%=request.getSession().getAttribute("USER")%></strong></font>&nbsp;&nbsp;&nbsp;&nbsp;
      <% } %>
    </td>
  </tr>
  <tr bgcolor="<bean:message key="app.banner.bgColor" />" valign="center"> 
    <td colspan="7" width="30%">
      <% if (request.getSession().getAttribute("USER").toString().equalsIgnoreCase("Expired")) { %>
        <font color="<bean:message key="app.banner.fontColor" />"><%=request.getSession().getAttribute("LICENSE")%></font>        
      <% } %>    
    </td>
    <td width="40%"> 
      <% if (request.getSession().getAttribute("USER") != null) { %>     
        <a href="EventCalendarList.do"><img src="./images/cal.gif" width="16" height="16" border="0" title="Click to display event calendar."></a>
        &nbsp;&nbsp;
        <a href="PurchaseOrderList.do"><img src="./images/cart.jpg" width="16" height="16" border="0" title="Click to display purchase orders."></a>
        &nbsp;&nbsp;
        <a href="VendorInvoiceList.do"><img src="./images/vndinv.jpg" width="16" height="16" border="0" title="Click to display vendor invoices."></a>
        &nbsp;&nbsp;
        <a href="CustomerInvoiceList.do"><img src="./images/invoice.jpg" width="16" height="16" border="0" title="Click to display customer invoices."></a>        
      <% } %>
    </td>
    <td align="right" width="30%">
      <% if (request.getSession().getAttribute("LICENSE").toString().equalsIgnoreCase("Expired")) { %>
        <font color="<bean:message key="app.banner.fontColor" />"><bean:message key="app.readonly"/></font>        
      <% } %>        
    </td>
  </tr>
  <tr>
    <td>
      <strong>
        <script language="JavaScript" type="text/javascript" >
         document.write(document.title);
      </script>
    </td>
    <td colspan="9" align="right">

<script language="JavaScript" type="text/javascript" >

/*Current date script credit:
JavaScript Kit (www.javascriptkit.com)
Over 200+ free scripts here!
*/

var mydate=new Date()
var year=mydate.getYear()
if (year < 1000)
year+=1900
var day=mydate.getDay()
var month=mydate.getMonth()
var daym=mydate.getDate()
if (daym<10)
daym="0"+daym
var dayarray=new Array("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
var montharray=new Array("January","February","March","April","May","June","July","August","September","October","November","December")
document.write("<small><font color='black' face='Arial'><b>"+dayarray[day]+", "+montharray[month]+" "+daym+", "+year+"</b></font></small>&nbsp;&nbsp;&nbsp;")
</script>
    </td>
  </tr>
</table>
