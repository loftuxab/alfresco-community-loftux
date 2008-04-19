<%@ page import="org.alfresco.web.site.*"%>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" autoFlush="true"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);

	// get the current theme
	String currentThemeId = ThemeUtil.getCurrentThemeId(context);	
%>
<!-- ExtJS Core CSS Libraries -->
<%=RenderUtil.renderLinkImport(context, "/extjs/resources/css/ext-all.css")%>
<%=RenderUtil.renderLinkImport(context, "/themes/builder/css/builder-default.css")%>

<!-- Theme-specific CSS Libraries -->
<%=RenderUtil.renderLinkImport(context, "/themes/extjs/css/xtheme-" + currentThemeId + ".css", "extjs-theme-link")%>
<%=RenderUtil.renderLinkImport(context, "/themes/builder/css/builder-" + currentThemeId + ".css", "builder-theme-link")%>

<!-- ExtJS Javascript Libraries -->
<%=RenderUtil.renderScriptImport(context, "/extjs/adapter/ext/ext-base.js") %>
<%=RenderUtil.renderScriptImport(context, "/extjs/ext-all.js") %>

<!-- Custom Javascript -->
<%=RenderUtil.renderScriptImport(context, "/js/builder/utils/miframe-min.js") %>
<%=RenderUtil.renderScriptImport(context, "/js/builder/utils/json.js") %>
<%=RenderUtil.renderScriptImport(context, "/js/builder/dynamic.js.jsp") %>
<%=RenderUtil.renderScriptImport(context, "/js/builder/incontext.js.jsp") %>

<%=RenderUtil.renderScriptImport(context, "/js/builder/wizard-core.js") %>
<%=RenderUtil.renderScriptImport(context, "/js/builder/wizard-adapter-extjs.js") %>
<%=RenderUtil.renderScriptImport(context, "/js/builder/application.js") %>
<%=RenderUtil.renderScriptImport(context, "/js/builder/builder.js") %>
