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
%>
<div width="100%" id="<%=regionId%>">
<%
	PresentationUtil.renderComponent(context, request, response, componentId);
%>
</div>
