<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page import="org.alfresco.web.site.config.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	RuntimeConfig configuration = (RuntimeConfig) request.getAttribute("component-configuration");
	
	// config values
	String componentId = (String) configuration.get("component-id");
	String endpointId = (String) configuration.get("endpointId");
	String webscript = (String) configuration.get("webscript");
	String container = (String) configuration.get("container");
	
	// find the endpoint object
	Endpoint[] array = ModelUtil.findEndpoints(context, endpointId);
	if(array == null || array.length == 0)
	{
		out.println("Unable to connect to component provider");
		return;
	}
	
	// find the endpoint
	Endpoint endpoint = (Endpoint) array[0];
	
	// properties of the end point
	String protocol = endpoint.getSetting("protocol");
	String host = endpoint.getSetting("host");
	String port = endpoint.getSetting("port");
	String uri = endpoint.getSetting("uri");
	
	// build the url
	String url = protocol + "://" + host + ":" + port + uri + webscript;
	
	
	
	
	// if we don't have a webscript, just show an icon
	if(webscript == null)
	{
		String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		String unconfiguredImageUrl = URLUtil.toBrowserUrl("/ui/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
		String renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Markup Component'/>";	
		out.println(renderString);
		return;
	}




	// render via div tag
	if(container == null || "div".equalsIgnoreCase(container))
	{
		// render
		String divId = "ws-" + componentId;
%>
		<div id="<%=divId%>"></div>
		<script language="Javascript">
		  renderURL("<%=url%>", "<%=divId%>");
		</script>
<%
	}
	
	
	
	// render via iframe
	if("iframe".equalsIgnoreCase(container))
	{
		// render
		String divId = "ws-" + componentId;
%>
		<div id="<%=divId%>"></div>
		<script language="Javascript">
		  renderIntoIFrame("<%=url%>", "<%=divId%>");
		</script>
<%
	}
%>