<%@ page import="org.alfresco.web.framework.render.*"%>
<%@ page import="org.alfresco.web.site.*"%>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	RenderContext context = RenderUtil.getContext(request);

	String backgroundImageUrl = URLUtil.browser(context, "/images/logo/AlfrescoFadedBG.png");	
	String homePageUrl = URLUtil.browser(context, "/");
	
	Content object = context.getCurrentObject();
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
		
			The content being viewed is not associated to a display page.
			<br/>
			<br/>
			The content type is:<br/>
			<%=object.getTypeId()%>
			<br/>
			<br/>
			<a href="javascript:window.history.back()">Associate a page for this content type!</a>
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

