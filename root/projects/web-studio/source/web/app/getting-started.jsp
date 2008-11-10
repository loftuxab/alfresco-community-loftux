<%@ page import="org.alfresco.web.framework.render.*" %>
<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.studio.*" %>
<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	RenderContext context = RenderUtil.getContext(request);
	String bgImageUrl = URLUtil.browser(context, "/images/logo/alfresco_webstudio_fadedbg.png");
	String logoImageUrl = URLUtil.browser(context, "/images/logo/AlfrescoLogo200.png");
	
	String webProjectId = WebStudioUtil.getCurrentWebProject(request);
	String sandboxId = WebStudioUtil.getCurrentSandbox(request);
	String storeId = WebStudioUtil.getCurrentStore(request);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Expires" content="Sat, 1 Jan 2000 08:00:00 GMT" />
    <title><alf:pageTitle/></title>
    <alf:head/>
</head>
<body class="studio">
<table width="100%" height="100%" border="0" style="background-image:url('<%=bgImageUrl%>'); background-repeat:no-repeat;">
	<tr>
		<td valign="center" align="middle">
			
			<div onClick="javascript:WebStudio.app.initOverlays();">
			 
			<img src="<%=logoImageUrl%>"/>
			<br/>
			Welcome to Alfresco Web Studio

			</div>
			
		</td>
	</tr>
</table>

</body>
</html>