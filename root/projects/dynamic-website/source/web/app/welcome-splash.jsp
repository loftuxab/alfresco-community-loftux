<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.site.*" %>
<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%
	RequestContext context = RequestUtil.getRequestContext(request);
	String logoImageUrl = org.alfresco.web.site.URLUtil.browser(context, "/images/logo/AlfrescoLogo200.png");
%>
<html>
<table width="100%" height="100%" border="1" bgcolor="#FFFFFF">
<tr>
	<td valign="middle" align="center">
		<img src="<%=logoImageUrl%>"/>
		
		<br/>
		<br/>
		
		<font size="2">
		<b>Alfresco Dynamic Website</b>
		is a community-supported and GPL-licensed web application framework that implements and improves upon designs offered by competing, closed-source content management vendors.  The purpose of this project is to distribute a freely-available implementation of this technology so that innovation may continue and all may benefit from its availability.
		</font>
	</td>
</tr>
</table>
</html>