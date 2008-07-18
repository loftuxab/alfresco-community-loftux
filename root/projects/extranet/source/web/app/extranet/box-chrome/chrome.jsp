<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	String servletPath = request.getContextPath();
	
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);

	// get the component	
	String componentId = (String) context.getRenderContext().get("component-id");
	Component component = context.getModel().getComponent(componentId);
	
	// component properties
	String title = component.getTitle();
	
	// html binding id (TODO: this is too much computation, necessary?)
	String htmlBindingId = (String) context.getRenderContext().get("html-binding-id");
	htmlBindingId = htmlBindingId.replace(".", "-");
%>
<style type="text/css">
<!--
#chrome-extranet-header-<%=htmlBindingId%>,
#chrome-extranet-content-<%=htmlBindingId%> {
	background-color: ffffff;
	border: solid 1px #cccccc;
}

#chrome-extranet-header-<%=htmlBindingId%> {
	color: #014a67;
	font-weight: bold;
	padding: 3px;
	padding-left: 5px;
	background-image: url(<%=servletPath%>/app/extranet/box-chrome/header_bg.gif);
	background-repeat: repeat-x;
	background-position: bottom;
	border-bottom: 0px;
}
-->
</style>

<table width="100%" cellpadding="0" cellspacing="0">
	<tr>
		<td id="chrome-extranet-header-<%=htmlBindingId%>" align="left" valign="top">
			<%=title%>
		</td>
	</tr>
	<tr>
		<td id="chrome-extranet-content-<%=htmlBindingId%>" style="padding: 5px;" align="left" valign="top">
			
			<alf:component-include/>
			
		</td>
	</tr>
</table>
