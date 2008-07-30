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
	// select the entity type
	String entityType = request.getParameter("entity_type");
	if(entityType == null)
	{
		entityType = AbstractUser.ENTITY_TYPE;
	}
	
	// get the appropriate entity service
	EntityService entityService = ExtranetHelper.getEntityService(request, entityType);
	
	// properties
	String[] propertyNames = ExtranetHelper.getEntityPropertyNames(entityType);
	String entityTitle = entityType;
		
	// get a list of entities
	List entityList = entityService.list();
%>
<html>
   <head><title><%=entityTitle%>s</title></head>
   <body>
   
   	<form method="POST" action="/extranet/">
   		<input type="hidden" name="p" value="admin-tools"/>
   		<input type="hidden" id="dispatchTo" name="dispatchTo" value="admin-entities"/>
   		
   		<select name="entity_type" onchange="document.forms[0].submit()">
   			<option <%=(AbstractUser.ENTITY_TYPE.equals(entityType) ? " selected " : "")%> value="<%=AbstractUser.ENTITY_TYPE%>">User</option>
   			<option <%=(AbstractGroup.ENTITY_TYPE.equals(entityType) ? " selected " : "")%> value="<%=AbstractGroup.ENTITY_TYPE%>">Group</option>
   			<option <%=(AbstractCompany.ENTITY_TYPE.equals(entityType) ? " selected " : "")%> value="<%=AbstractCompany.ENTITY_TYPE%>">Company</option>
   		</select>

		<table>
			<tr>
				<td></td>
<%
	for(int i = 0; i < propertyNames.length; i++)
	{
%>
				<td><%=propertyNames[i]%></td>
<%
	}
%>
			</tr>
		
<%
	for(int i = 0; i < entityList.size(); i++)
	{
		Entity entity = (AbstractEntity) entityList.get(i);
%>
			<tr>
				<td><input name="selectedId" type="radio" value="<%=entity.getEntityId()%>"/></td>
<%
		for(int j = 0; j < propertyNames.length; j++)
		{
%>
				<td><%=entity.getProperty(propertyNames[j])%></td>
<%
		}
%>
			</tr>
<%
	}
%>	
		</table>
		
		<input type="button" value="add_entity" onclick="document.getElementById('dispatchTo').value='admin-entities-add'; document.forms[0].submit()" />
		<input type="button" value="edit_entity"  onclick="document.getElementById('dispatchTo').value='admin-entities-edit'; document.forms[0].submit()" />
		<input type="button" value="remove_entity"  onclick="document.getElementById('dispatchTo').value='admin-entities-remove'; document.forms[0].submit()" />
	</form>
	
   </body>
</html>
