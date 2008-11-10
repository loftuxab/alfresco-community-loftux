<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.framework.render.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// render context
	RenderContext context = (RenderContext) request.getAttribute("renderContext");
	
	// get the htmlId
	String htmlId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);

	// unconfigured component image
	String unconfiguredImageUrl = URLUtil.browser(context, "/images/core/unconfigured_region_large.gif");
%>
<div id="<%=htmlId%>">
	<img src="<%=unconfiguredImageUrl%>" border="0" width="64px" height="64px"/>
</div>
