<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// region stuff
	String regionId = (String) request.getParameter("regionId");	
	String regionScopeId = (String) request.getParameter("regionScopeId");
	String regionSourceId = (String) request.getParameter("regionSourceId");
	
	// load component stuff if we have it
	String componentId = (String) request.getParameter("componentId");
	
	Component component = null;
	ComponentType componentType = null;
	if(componentId != null)
	{
		component = (Component) context.getModel().getComponent(componentId);
		componentType = (ComponentType) component.getComponentType(context);
	}

	String currentThemeId = ThemeUtil.getCurrentThemeId(context);
	String infoImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/info_icon.gif");
	
	String componentImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/component.gif");
	String componentImageLargeUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/component_large.gif");
	
	String missingComponentImageLargeUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/unconfigured_region_large.gif");
	
	String siteScopeImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/website_large.gif");
	String templateScopeImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/template_large.gif");
	String pageScopeImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/page_large.gif");
	
	String _regionScopeId = regionScopeId.substring(0,1).toUpperCase() + regionScopeId.substring(1, regionScopeId.length());
	
	String scopeImageUrl = siteScopeImageUrl;
	if("template".equals(regionScopeId))
		scopeImageUrl = templateScopeImageUrl;
	if("page".equals(regionScopeId))
		scopeImageUrl = pageScopeImageUrl;		
		
%>
	<div width="100%" style="margin: 3px">
		<p valign="top">
			<img align="left" src="<%=scopeImageUrl%>"/>
			<font size="2"><b><%=regionId%></b></font>
			<br/>
			<font size="1"><%=_regionScopeId%> scope region</font>
		</p>
		<br/>
		<br/>
<%
		if(component != null)
		{
%>
		<p valign="top">
			<img align="left" src="<%=componentImageLargeUrl%>"/>
			<font size="2"><b><%=component.getTitle()%></b></font>
			<br/>
			<font size="1"><%=componentType.getTitle()%></font>
		</p>
		<br/>	
<%
		}
		else
		{
%>
		<p valign="top">
			<img align="left" src="<%=missingComponentImageLargeUrl%>" />
			<font size="2">Nothing is currently configured for this region</font>
		</p>
<%		
		}
%>
	</div>



