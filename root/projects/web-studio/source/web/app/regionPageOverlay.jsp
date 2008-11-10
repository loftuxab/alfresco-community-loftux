<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.framework.render.*"%>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	RenderContext context = RenderUtil.getContext(request);
	
	// incoming
	String regionId = (String) request.getParameter("regionId");
	String regionScopeId = (String) request.getParameter("regionScopeId");
	String regionSourceId = (String) request.getParameter("regionSourceId");
	String componentId = (String) request.getParameter("componentId");
	String componentTypeId = (String) request.getParameter("componentTypeId");
		
	// load component info (if we have it)
	Component component = null;
	ComponentType componentType = null;
	if(componentId != null)
	{
		component = (Component) context.getModel().getComponent(componentId);
		componentType = (ComponentType) component.getComponentType(context);
	}

	// resources
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio");
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	
	String infoImageUrl = overlayPath + "/images/icons/info_icon.gif";	
	String componentImageUrl = overlayPath + "/images/icons/component.gif";
	String componentImageLargeUrl = overlayPath + "/images/icons/component_large.gif";	
	String missingComponentImageLargeUrl = overlayPath + "/images/icons/unconfigured_region_large.gif";
	
	String siteScopeImageUrl = overlayPath + "/images/icons/region_scope_site_large.gif";
	String templateScopeImageUrl = overlayPath + "/images/icons/region_scope_template_large.gif";
	String pageScopeImageUrl = overlayPath + "/images/icons/region_scope_page_large.gif";
	
	String _regionScopeId = regionScopeId.substring(0,1).toUpperCase() + regionScopeId.substring(1, regionScopeId.length());
	
	String scopeImageUrl = siteScopeImageUrl;
	if("template".equals(regionScopeId))
	{
		scopeImageUrl = templateScopeImageUrl;
	}
	if("page".equals(regionScopeId))
	{
		scopeImageUrl = pageScopeImageUrl;
	}
		
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



