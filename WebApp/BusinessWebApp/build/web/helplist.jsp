<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
            
<title><bean:message key="app.title" /></title>

<LINK REL=STYLESHEET HREF="css/style_sheet.css">

<script type="text/javascript" src="./js/popup_window.js"></script>

<logic:notPresent name="USER">
  <logic:forward name="login" />
</logic:notPresent>



<style type="text/css">
.collapsible {
          display: none; /* Only important part */
          border: dashed 1px silver;
          padding: 10px;
}
</style>

<script type="text/javascript">
//preload image
var collimg = new Image();
collimg.src = "images/collapse.gif";
var expimg = new Image();
collimg.src = "images/expand.gif";

function ShowHideLayer(boxID) {
  /* Obtain reference for the selected boxID layer and its button */
  var box = document.getElementById("box"+boxID);
  var boxbtn = document.getElementById("btn"+boxID);

  /* If the selected box is currently invisible, show it */
  if(box.style.display == "none" || box.style.display=="") {
    box.style.display = "block";
    boxbtn.src = "images/collapse.gif";
  }
  /* otherwise hide it */
  else {
    box.style.display = "none";
    boxbtn.src = "images/expand.gif";
  }
}
</script>

<%
  boolean openDiv = false;
%>

<h3>
  <img src="./images/Wolmerica_Logo.gif" height="45" width="108" border="0" title="Wolmerica Logo">
  Office Wizard Help
</h3>
<a href="javascript:window.close();">Close Window</a>
<hr>

<logic:notEqual name="helpHeadForm" property="recordCount" value="0"> 
  <logic:iterate id="helpForm"
                 name="helpHeadForm"
                 property="helpForm"
                 scope="session"
                 type="com.wolmerica.help.HelpForm">

    <logic:equal name="helpForm" property="step" value="1">
      <% if (openDiv) { %> 
           </div>
      <%   openDiv = false;
         } %>
      <a href="javascript:;" onclick="ShowHideLayer(<bean:write name="helpForm" property="key" />);"><img src="images/expand.gif" alt="Expand Me" name="btn<bean:write name="helpForm" property="key" />" width="9" height="9" border="0" id="btn<bean:write name="helpForm" property="key" />" /></a>
      &nbsp;<strong><bean:write name="helpForm" property="description" /></strong><br>
      <div id="box<bean:write name="helpForm" property="key" />" class="collapsible">
          
      <% openDiv = true; %>
    </logic:equal>                 
                     
    <logic:equal name="helpForm" property="step" value="2">
      <logic:notEqual name="helpForm" property="videoFileName" value="">
        <a href="<bean:write name="helpForm" 
          property="documentServerURL" />/<bean:message key="app.help.id" />/<bean:write name="helpForm" 
          property="key" />/<bean:write name="helpForm" 
          property="videoFileName" />"><image src="images/video.jpg" border="0" height="20" width="22" title="<bean:message key="app.video" />"></a>
      </logic:notEqual>
      <logic:equal name="helpForm" property="videoFileName" value="">
        <image src="images/blank.jpg" border="0" height="20" width="22">
      </logic:equal>
      &nbsp;&nbsp;
      <logic:notEqual name="helpForm" property="htmlFileName" value="">                  
        <a href="<bean:write name="helpForm" 
          property="documentServerURL" />/<bean:message key="app.help.id" />/<bean:write name="helpForm" 
          property="key" />/<bean:write name="helpForm" 
          property="htmlFileName" />"><bean:write name="helpForm" property="description" /></a>
      </logic:notEqual>
      <logic:equal name="helpForm" property="htmlFileName" value="">
        <bean:write name="helpForm" property="description" />
      </logic:equal>      
      <br>              
    </logic:equal>         
  </logic:iterate>
  <% if (openDiv) { %> 
       </div>
  <% } %>
</logic:notEqual>
