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

    // select the entity type
    String entityType = request.getParameter(Constants.ADMIN_TOOLS_ENTITY_TYPE);


    // properties
    String entityTitle = entityType;
    String[] propertyNames = ExtranetHelper.getEntityPropertyNames(entityType);

    String result = admin.addEntity(entityType, entityTitle, propertyNames);
    if( result != null )
    {
        out.println( result);
        return;
    }
%>
<html>
   <head><title>Add <%=entityTitle%></title></head>
   <body>
   	<form method="POST" action="/extranet/">
   		<input type="hidden" name="p" value="<%=Constants.ADMIN_TOOLS%>"/>
   		<input type="hidden" name="<%=Constants.ADMIN_TOOLS_DISPATCH_TO%>" value="<%=Constants.ADMIN_TOOLS_DISPATCH_TO_ENTITY_ADD%>"/>
   		<input type="hidden" name="<%=Constants.ADMIN_TOOLS_ENTITY_TYPE%>" value="<%=entityType%>"/>

		<table>
<%
	for(int i = 0; i < propertyNames.length; i++)
	{
%>
			<tr>
				<td><%=propertyNames[i]%></td>
				<td>
					<input name="<%=propertyNames[i]%>"/>
				</td>
			</tr>
<%
	}
%>
		</table>

		<input type="submit" value="save" name="<%=Constants.ADMIN_TOOLS_COMMAND%>" />
		<input type="button" value="cancel" onclick="window.location.href='?p=admin-tools&dispatchTo=admin-entities';" />
	</form>

   </body>
</html>
