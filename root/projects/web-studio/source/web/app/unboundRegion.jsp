<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.framework.render.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// render context
	RenderContext context = (RenderContext) request.getAttribute("renderContext");
	
	// get the htmlId
	String htmlId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
	
	// properties
	String regionId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
	String regionScopeId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID);
%>
<div id="<%=htmlId%>" style="border-top: 1px #999 dotted; border-left: 1px #999 dotted; border-right: 1px #999 dotted; border-bottom: 1px #999 dotted; margin: 1px; padding: 8px; color: #999" align="center" valign="middle">
<i><%=regionId%></i>
</div>
