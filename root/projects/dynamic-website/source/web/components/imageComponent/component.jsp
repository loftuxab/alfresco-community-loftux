<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.tools.*" %>
<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// config values
	String imageLocation = (String) context.getRenderContext().get("imageLocation");
	String width = (String) context.getRenderContext().get("width");
	String height = (String) context.getRenderContext().get("height");
	String alt = (String) context.getRenderContext().get("alt");
	
	String renderString = null;
	if(imageLocation != null && !"".equals(imageLocation))
	{
		if(imageLocation.startsWith("/"))
		{
			imageLocation = URLUtil.browser(context, imageLocation);
		}
		renderString = "<img src=\"" + imageLocation + "\" ";
		if(width != null)
			renderString += " width=\"" + width + "\"";
		if(height != null)
			renderString += " height=\"" + height + "\"";
		if(alt != null)
			renderString += " alt=\"" + alt + "\"";
		renderString += "/>";
	}
	
	// default message if no image was specified
	if(renderString == null)
	{
		String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		String unconfiguredImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
		renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Image Component' width='64px' height='64px'/>";
	}
%>
<%=renderString%>
