<%@ page import="org.alfresco.web.site.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" autoFlush="true"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	RequestContext context = RequestUtil.getRequestContext(request);
	User user = context.getUser();
%>	
<html>
   <head><title>Welcome, <%=user.getId()%></title></head>
   <body>
      <h2>Welcome to the Network Project.</h2>
      <p>For a list of services available <a href="service/index">click here.</a></p>
   </body>
</html>