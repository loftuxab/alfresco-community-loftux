<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);

	// get the component	
	String componentId = (String) context.getRenderContext().get("component-id");
	Component component = context.getModel().getComponent(componentId);
	
	// component properties
	String title = component.getTitle();
%>
<h2><%=title%></h2>
<alf:component-include/>
