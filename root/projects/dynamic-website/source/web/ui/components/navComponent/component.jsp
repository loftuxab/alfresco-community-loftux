<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page import="org.alfresco.web.site.config.*" %>
<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%@ taglib uri="/WEB-INF/tlds/adw.tld" prefix="adw" %>
<%@ page isELIgnored="false" %>
<alf:require script="/yui/build/yahoo-dom-event/yahoo-dom-event.js"/>
<alf:require script="/yui/build/container/container-min.js"/>
<alf:require script="/yui/build/menu/menu.js"/>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	RuntimeConfig configuration = (RuntimeConfig) request.getAttribute("component-configuration");
	
	
	String orientation = (String) configuration.get("orientation");
	if(orientation == null || "".equals(orientation))
	{
		String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		String unconfiguredImageUrl = URLUtil.toBrowserUrl("/ui/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
		String renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Nav Component'/>";	
		out.println(renderString);
		return;	
	}
	
	// determine the renderer to use
	String renderer = (String) configuration.get("renderer");
	if(renderer == null)
	{
		renderer = "renderers/horizontalNav1/horizontalNav1.jsp";
		if("vertical".equalsIgnoreCase(orientation))
			renderer = "renderers/verticalNav1/verticalNav1.jsp";
	}
	
	// dispatch
	if(renderer.endsWith(".jsp"))
	{
		RequestUtil.include(request, response, renderer);
	}
	else if(renderer.endsWith(".xsl"))
	{
		// TODO: add support for XSL renderers
	}
	else if(renderer.endsWith(".ftl"))
	{
		// TODO: add support for FTL renderers
	}
%>
