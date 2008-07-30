<%@ page import="java.util.*" %>
<%@ page import="org.alfresco.connector.*" %>
<%@ page import="org.alfresco.extranet.*" %>
<%@ page import="org.alfresco.extranet.database.*" %>
<%@ page import="org.alfresco.extranet.ldap.*" %>
<%@ page import="org.alfresco.extranet.webhelpdesk.*" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" autoFlush="true"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	// safety check
	org.alfresco.connector.User user = org.alfresco.web.site.RequestUtil.getRequestContext(request).getUser();
	if(user == null || !user.isAdmin())
	{
		out.println("Access denied");
		return;
	}
%>
<%
	// get the selected object
	String entityId = request.getParameter("selectedId");
		
	// select the entity type
	String entityType = request.getParameter("entity_type");
	
	// get the appropriate entity service
	EntityService entityService = ExtranetHelper.getEntityService(request, entityType);
	
	// get the entity
	Entity entity = entityService.get(entityId);
	
	// remove the entity
	entityService.remove(entity);
%>
<form method="POST" action="/extranet/">

	<input type="button" value="finished" onclick="window.location.href='?p=admin-tools&dispatchTo=admin-entities';" />

</form>
