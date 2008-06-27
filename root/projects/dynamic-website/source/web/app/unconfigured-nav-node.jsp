<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.site.*"%>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%@ taglib uri="/WEB-INF/tlds/adw.tld" prefix="adw" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);

	Page currentPage = context.getPage();
	String pageName = currentPage.getTitle();
	
	String backgroundImageUrl = "/images/logo/AlfrescoFadedBG.png";
	backgroundImageUrl = URLUtil.browser(context, backgroundImageUrl);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><alf:pageTitle/></title>
    <alf:head/>
</head>
<body>
<table width="100%" height="100%" border="0" style="background-image:url('<%=backgroundImageUrl%>'); background-repeat:no-repeat;">
	<tr>
		<td valign="center" align="middle">
			This is the landing page for a navigation node.
			<br/><br/>
			<b><%=pageName%></b>
			<br/><br/>
			<br/>
			It has not yet been configured.
			<br/><br/><br/>
		</td>
	</tr>
</table>
<adw:floatingmenu/>

</body>
</html>

