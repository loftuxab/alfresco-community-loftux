<%@ page import="org.alfresco.web.site.RequestContext" %>
<%@ page import="org.alfresco.web.site.RequestUtil" %>
<%@ page import="org.alfresco.web.studio.*" %>
<%@ page import="org.alfresco.web.framework.model.*" %>
<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	RequestContext context = RequestUtil.getRequestContext(request);
	String bgImageUrl = org.alfresco.web.site.URLUtil.browser(context, "/images/logo/alfresco_webstudio_fadedbg.png");
	String logoImageUrl = org.alfresco.web.site.URLUtil.browser(context, "/images/logo/AlfrescoLogo200.png");
	
	Page currentPage = context.getPage();
	String pageName = currentPage.getTitle();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><alf:pageTitle/></title>
    <alf:head/>
</head>
<body class="studio">
<table width="100%" height="100%" border="0" style="background-image:url('<%=bgImageUrl%>'); background-repeat:no-repeat;">
	<tr>
		<td valign="center" align="middle">
			
			<div>
			
			This is an unconfigured page.
			
			<br/><br/>
			<b><%=pageName%></b>
			<br/><br/>
			
			It does not have a presentation template.
			<br/>
			<br/>
			<a href="javascript:WebStudio.app.onMenuItemClick('webdesigner', 'page-template-associations-view');">
			Associate a Presentation Template
			</a>
						 
			</div>
			
		</td>
	</tr>
</table>

</body>
</html>