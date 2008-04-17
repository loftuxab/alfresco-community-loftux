<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.web.site.config.*"%>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	RuntimeConfig configuration = (RuntimeConfig) request.getAttribute("region-configuration");

	// properties from configuration	
	String regionId = (String) configuration.get("region-id");
	String componentId = (String) configuration.get("component-id");	

	String currentThemeId = ThemeUtil.getCurrentThemeId(context);
	String unconfiguredImageUrl = URLUtil.browser(context, "/ui/themes/builder/images/" + currentThemeId + "/icons/unconfigured_region_large.gif");
%>
<div width="100%" id="<%=regionId%>">
<%
	if(componentId != null)
	{
		PresentationUtil.renderComponent(context, request, response, componentId);
	}
	else
	{
%>
	<img src="<%=unconfiguredImageUrl%>" border="0" width="64px" height="64px"/>
<%
	}
%>
</div>
