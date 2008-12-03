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
<div id="<%=htmlId%>" style="border-top: 2px #aaaaaa dotted; border-left: 2px #aaaaaa dotted; border-right: 2px #aaaaaa dotted; border-bottom: 2px #aaaaaa dotted; padding: 8px; margin: 2px; color: #aaaaaa" align="center" valign="middle">
<i><%=regionId%></i>
</div>
