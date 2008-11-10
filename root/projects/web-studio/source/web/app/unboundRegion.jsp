<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.framework.render.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// render context
	RenderContext context = (RenderContext) request.getAttribute("renderContext");
	
	// get the htmlId
	String htmlId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
	
	// properties
	String regionId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
	String regionScopeId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID);
%>
<div id="<%=htmlId%>" style="border-top: 1px black solid; border-left: 1px black solid; border-right: 1px #fafafa solid; border-bottom: 1px #fafafa solid; background-color: #f5f5f5">
Region: <b><%=regionId%></b>
<br/>
Scope: <b><%=regionScopeId%></b>
<br/>
<br/>
This region has nothing in it.
<br/>
Drop a component here to bind it into the page.
<br/>
</div>
