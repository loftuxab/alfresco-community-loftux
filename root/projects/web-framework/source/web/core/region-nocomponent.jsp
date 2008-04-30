<%@ page import="org.alfresco.web.site.*"%>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration and regionId
	String regionId = (String) context.getRenderData().get("region-id");

	// unconfigured component image
	String unconfiguredImageUrl = URLUtil.browser(context, "/images/core/unconfigured_region_large.gif");
%>
<div width="100%" id="<%=regionId%>">
	<img src="<%=unconfiguredImageUrl%>" border="0" width="64px" height="64px"/>
</div>
