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
	AdminUtil admin = new AdminUtil(request);
    if( !admin.isAuthorizedAdmin())
	{
        //TODO: replace with redirect and error message
		out.println("Access denied");
		return;
	}
%>
<%
	// get the selected object
	String entityId = request.getParameter(Constants.ADMIN_TOOLS_SELECTED_ID);

	// select the entity type
	String entityType = request.getParameter(Constants.ADMIN_TOOLS_ENTITY_TYPE);

	// get the appropriate entity service
	EntityService entityService = ExtranetHelper.getEntityService(request, entityType);

	// load the entity
	Entity entity = entityService.get(entityId);
	String entityTitle = entityType;
	String[] propertyNames = ExtranetHelper.getEntityPropertyNames(entityType);

	// command processing
	String command = request.getParameter(Constants.ADMIN_TOOLS_COMMAND);
	if(Constants.ADMIN_TOOLS_COMMAND_SAVE.equals(command))
	{
		// store properties onto entity
		for(int i = 0; i < propertyNames.length; i++)
		{
			String value = request.getParameter(propertyNames[i]);
			if(value != null)
			{
				entity.setProperty(propertyNames[i], value);
			}
		}

		// update
		entityService.update(entity);

		out.println(entityTitle + " updated!");
		out.println("<br/>");
		out.println("<a href='?p=admin-tools&dispatchTo=admin-entities'>Entities</a>");

		return;
	}
%>
<html>
   <head><title>Add <%=entityTitle%></title></head>
   <body>
   	<form method="POST" action="/extranet/">
   		<input type="hidden" name="<%=Constants.ADMIN_TOOLS_P%>" value="<%=Constants.ADMIN_TOOLS%>"/>
   		<input type="hidden" name="<%=Constants.ADMIN_TOOLS_DISPATCH_TO%>" value="<%=Constants.ADMIN_TOOLS_DISPATCH_TO_ENTITY_EDIT%>"/>
   		<input type="hidden" name="<%=Constants.ADMIN_TOOLS_ENTITY_TYPE%>" value="<%=entityType%>"/>
   		<input type="hidden" name="<%=Constants.ADMIN_TOOLS_SELECTED_ID%>" value="<%=entityId%>"/>

		<table>
<%
	for(int i = 0; i < propertyNames.length; i++)
	{
%>
			<tr>
				<td><%=propertyNames[i]%></td>
				<td>
					<input name="<%=propertyNames[i]%>" value="<%=(entity.getProperty(propertyNames[i]) != null ? entity.getProperty(propertyNames[i]) : "")%>" />
				</td>
			</tr>
<%
	}
%>
		</table>

		<input type="submit" value="<%=Constants.ADMIN_TOOLS_COMMAND_SAVE%>" name="<%=Constants.ADMIN_TOOLS_COMMAND%>" />
		<input type="button" value="cancel" onclick="window.location.href='?p=admin-tools&dispatchTo=admin-entities';" />
	</form>

   </body>
</html>
