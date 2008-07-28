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
		DatabaseInvitedUser user = (DatabaseInvitedUser) invitedUsers.get(i);
		if(!user.isCompleted())
		{
		
%>
		<tr>
			<td>
				<%=user.getUserId()%>
			</td>
			<td>
				<%=user.getEmail()%>
			</td>
			<td>
				<%=user.getFirstName()%>
			</td>
			<td>
				<%=user.getLastName()%>
			</td>
			<td>
				<%=user.getHash()%>
			</td>
			<td>
				<a href="/extranet/?p=invitation-wizard&hash=<%=user.getHash()%>">Process</a>
			</td>
		</tr>
<%

		}
	}
%>

	</table>
			
   </body>
</html>
