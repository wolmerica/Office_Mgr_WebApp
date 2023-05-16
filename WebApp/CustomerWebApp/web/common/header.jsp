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

<!--[if IE]>
  <style type="text/css">
    body {behavior: url(csshover.htc);}
    ul a, ul, li {height: 1%;} /* Holly hack fix for IE bugs */
    li {
    margin-left: -16px;
    mar\gin-left: 0;
    }
  </style>
  <noscript>
    <style type="text/css">
      ul ul {position: static;}
    </style>
  </noscript>
<![endif]-->

<script src="./js/popup_window.js" language="JavaScript" type="text/javascript"></script>

<logic:notPresent name="ACCTKEY">
  <logic:forward name="login" />
</logic:notPresent>

<!-- Dark Blue #0000A0 -->

<table width="<bean:message key="app.table.width"/>" border="<bean:message key="app.table.border"/>" cellspacing="<bean:message key="app.table.cellspacing"/>" cellpadding="<bean:message key="app.table.cellpadding"/>" >
  <tr bgcolor="<bean:message key="app.banner.bgColor"/>"> 
    <td  colspan="7" height="50" width="30%">
      <% if (request.getSession().getAttribute("ACCTNAME") != null) { %>
        &nbsp;&nbsp;&nbsp;<a href="MenuEntry.do"><strong><font color="<bean:message key="app.banner.fontColor" />"><bean:message key="app.menu.link" /></font></strong></a>
      <% } %>
    </td>
    <td align="center" width="40%">    
      <font size=+1 color="<bean:message key="app.banner.fontColor" />">
        <strong><bean:message key="app.title" /></strong>
       </font>
       <br>
       <img src="./images/Business_Logo.gif" border="0" title="Business Logo">  
    </td>
    <td align="right" width="30%">
      <% if (request.getSession().getAttribute("ACCTNAME") != null) { %>        
        <a href="Logout.do?type=user"><strong><font color="<bean:message key="app.banner.fontColor" />"><bean:message key="app.logout.link" /></font></strong></a>&nbsp;&nbsp;&nbsp;&nbsp;
        <br>
        <br>
        <font color="<bean:message key="app.banner.fontColor" />"><bean:message key="app.welcome" />,&nbsp;&nbsp;<strong><%=request.getSession().getAttribute("ACCTNAME")%></strong></font>&nbsp;&nbsp;&nbsp;&nbsp;
      <% } %>        
    </td>
  </tr>
  <tr>
    <td>
      <strong>
        <script language="JavaScript" type="text/javascript" >
          document.write(document.title);
        </script>
      </strong>
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
