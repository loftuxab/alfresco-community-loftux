<%@ page import="org.alfresco.web.site.*" %>
<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%@ taglib uri="/WEB-INF/tlds/adw.tld" prefix="adw" %>
<%
	RequestContext context = RequestUtil.getRequestContext(request);
	String bgImageUrl = org.alfresco.web.site.URLUtil.browser(context, "/images/logo/AlfrescoFadedBG.png");
	String logoImageUrl = org.alfresco.web.site.URLUtil.browser(context, "/images/logo/AlfrescoLogo200.png");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><alf:pageTitle/></title>
    <alf:head/>
</head>
<body onLoad="showWelcomeWindow()">
<table width="100%" height="100%" border="0" style="background-image:url('<%=bgImageUrl%>'); background-repeat:no-repeat;">
	<tr>
		<td valign="center" align="middle">
			<img src="<%=logoImageUrl%>"/>
			<br/>
			Welcome to Alfresco Dynamic Website
			<br/>
			<br/>
			<div onClick="showWelcomeWindow();">
			<b>Click here to Get Started!</b>
			</div>
			<br/>
			<br/>
			<br/>

		</td>
	</tr>
</table>

</body>
</html>