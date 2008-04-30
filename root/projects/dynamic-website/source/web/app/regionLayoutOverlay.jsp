<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.web.site.model.*"%>
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
	String layoutId = (String) request.getParameter("layoutId");
	
	String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		
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
	</div>



