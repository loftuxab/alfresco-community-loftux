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
	
	// properties from configuration	
	String regionId = (String) context.getRenderContext().get("region-id");
	String regionScopeId = (String) context.getRenderContext().get("region-scope-id");
	String regionSourceId = (String) context.getRenderContext().get("region-source-id");

	String currentThemeId = ThemeUtil.getCurrentThemeId(context);
	String unconfiguredImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/unconfigured_region_large.gif");
%>
<div width="100%" id="<%=regionId%>">
	<img src="<%=unconfiguredImageUrl%>" border="0" width="64px" height="64px"/>
</div>
<script language="Javascript">
	configureRegion("<%=regionId%>", "<%=regionScopeId%>", <%=(regionSourceId == null ? "null" : "\"" + regionSourceId + "\"")%>);
</script>
