<%@ page import="java.text.*" %>
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
<html>
   <head>
   	<title>Show Pending Invited Users</title>
   </head>
   <body>
      
	<table>
		<tr>
			<td>User ID</td>
			<td>Email</td>
			<td>FirstName</td>
			<td>LastName</td>
			<td>Hash</td>
			<td></td>
		</tr>
<%
	// get services
	InvitationService invitationService = ExtranetHelper.getInvitationService(request);
	
	List invitedUsers = invitationService.list();
	for(int i = 0; i < invitedUsers.size(); i++)
	{
		DatabaseInvitedUser dbUser = (DatabaseInvitedUser) invitedUsers.get(i);
		if(!dbUser.isCompleted())
		{
		
%>
		<tr>
			<td>
				<%=dbUser.getUserId()%>
			</td>
			<td>
				<%=dbUser.getEmail()%>
			</td>
			<td>
				<%=dbUser.getFirstName()%>
			</td>
			<td>
				<%=dbUser.getLastName()%>
			</td>
			<td>
				<%=dbUser.getHash()%>
			</td>
			<td>
				<a href="/extranet/?p=invitation-wizard&hash=<%=dbUser.getHash()%>">Process</a>
			</td>
		</tr>
<%

		}
	}
%>

	</table>
			
   </body>
</html>
