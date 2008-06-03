<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.web.site.model.*"%>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);

	String backgroundImageUrl = "/images/logo/AlfrescoFadedBG.png";
	backgroundImageUrl = URLUtil.browser(context, backgroundImageUrl);
	
	String homePageUrl = URLUtil.browser(context, "/");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><alf:pageTitle/></title>
    <alf:head/>
    <style>
	a:link              { color:red; text-decoration:underline; }
    	a:visited           { color:red; text-decoration:underline; }
    	a:hover             { color:blue; text-decoration:underline; }
    	a:active            { color:red; text-decoration:underline; }
    </style>
</head>
<body>
<table width="100%" height="100%" border="0" style="background-image:url('<%=backgroundImageUrl%>'); background-repeat:no-repeat;">
	<tr>
		<td align="center" valign="center" height="100%">
			This content is not associated to a page.
			<br/>
			<br/>
			<a href="javascript:window.history.back()">Go back to previous page</a>
			<br/>
			<br/>
			<a href="<%=homePageUrl%>">Go to the home page</a>
		</td>
	</tr>
	<tr>
		<td>
			<jsp:include page="content-error-viewer.jsp"/>
		</td>
	</tr>
</table>

</body>
</html>

