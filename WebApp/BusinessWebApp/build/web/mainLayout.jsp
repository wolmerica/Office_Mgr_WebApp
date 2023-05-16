<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<html>
<head>
  <title>
    <tiles:getAsString name="title"/>
  </title>
  <tiles:insert attribute="header"/>
</head>  
<body>
  <tiles:insert attribute="body"/>  
  <tiles:insert attribute="footer"/>
</body>
</html>