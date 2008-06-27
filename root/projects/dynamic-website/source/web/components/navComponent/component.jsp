<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.tools.*" %>
<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%@ taglib uri="/WEB-INF/tlds/adw.tld" prefix="adw" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	String orientation = (String) context.getRenderContext().get("orientation");
	if(orientation == null || "".equals(orientation))
	{
		String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		String unconfiguredImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
		String renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Nav Component'/>";	
		out.println(renderString);
		return;	
	}
	
	// determine the renderer to use
	String renderer = (String) context.getRenderContext().get("renderer");
	if(renderer == null)
	{
		renderer = "/components/navComponent/renderers/horizontalNav1/horizontalNav1.jsp";
		if("vertical".equalsIgnoreCase(orientation))
			renderer = "/components/navComponent/renderers/verticalNav1/verticalNav1.jsp";
	}
	
	// dispatch
	if(renderer.endsWith(".jsp"))
	{
		RequestUtil.include(getServletContext(), request, response, renderer);
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
