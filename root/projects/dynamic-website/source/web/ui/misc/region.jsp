<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.web.site.model.*"%>
<%@ page import="org.alfresco.web.site.config.*"%>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	RuntimeConfig configuration = (RuntimeConfig) request.getAttribute("region-configuration");

	// properties from configuration	
	String regionId = (String) configuration.get("region-id");
	String regionScopeId = (String) configuration.get("region-scope-id");
	String regionSourceId = (String) configuration.get("region-source-id");
	String componentId = (String) configuration.get("component-id");	
	String componentTypeId = (String) configuration.get("component-type-id");

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
<script language="Javascript">
	configureRegion("<%=regionId%>", "<%=regionScopeId%>", <%=(regionSourceId == null ? "null" : "\"" + regionSourceId + "\"")%>);
</script>
<%
	// component bindings	
	if(componentId != null && !"".equals(componentId))
	{
%>
<script language="Javascript">
	bindComponentToRegion("<%=regionId%>", "<%=componentId%>", "<%=componentTypeId%>");
</script>
<%
	}
%>
