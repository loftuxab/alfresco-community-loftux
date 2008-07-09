<%@ page import="org.alfresco.connector.*" %>
<%@ page import="org.alfresco.extranet.*" %>
<%@ page import="org.alfresco.extranet.database.*" %>
<%@ page import="org.alfresco.extranet.ldap.*" %>
<%@ page import="org.alfresco.extranet.webhelpdesk.*" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" autoFlush="true"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%
	// get services
	InvitationService invitationService = ExtranetHelper.getInvitationService(request);
	UserService userService = ExtranetHelper.getUserService(request);
	
	// store the invited users
	Map invitedUsersMap = new HashMap();
	
	// store users who were not invited
	Map invitedUsersProblems = new HashMap();
	
	// process invitations
	String[] invitedUsers = request.getParameterValues("invitedUsers");
	for(int i = 0; i < invitedUsers.length; i++)
	{
		String key = invitedUsers[i];

		// get properties
		String userId = request.getParameter(key + "_userId");
		String firstName = request.getParameter(key + "_firstName");
		String lastName = request.getParameter(key + " _lastName");
		String email = request.getParameter(key + "_email");
		String whdUserId = request.getParameter(key + "_whdUserId");
		String alfrescoUserId = request.getParameter(key + "_alfrescoUserId");
		String companyName = request.getParameter(key + "_companyName");
		
		String message = null;
		
		// check whether the user already exists
		if(userService.getUserByEmail(email) == null)
		{
			if(userService.getUser(userId) == null)
			{
				if(invitationService.getInvitedUser(userId) == null)
				{
					// invite the user
					DatabaseInvitedUser invitedUser = invitationService.inviteUser(userId, firstName, lastName, email, whdUserId, alfrescoUserId);
					if(invitedUser != null)
					{
						invitedUsersMap.put(userId, invitedUser);
					}
				}
				else
				{
					invitedUsersProblems.put(userId, "The user id '" + userId + "' has already been invited");
				}
				
			}
			else
			{
				invitedUsersProblems.put(userId, "The user id '" + userId + "' already exists as a user of Network");
			}
		}
		else
		{
			invitedUsersProblems.put(userId, "The email '" + email + "' already exists as a user of Network");
		}
	}
%>
<html>
   <head><title>Bulk Invite Users</title></head>
   <body>
   	
<%
	Iterator it = invitedUsersMap.keySet().iterator();
	if(it.hasNext())
	{
%>
	The following users were successfully invited:
	<br/>
	<br/>
<%
		while(it.hasNext())
		{
			String userId = (String) it.next();
			DatabaseInvitedUser user = (DatabaseInvitedUser) invitedUsersMap.get(userId);

			out.println(userId + " (" + user.getEmail() + ")");
			out.println("<BR/>");
		}
	}
%>

	<br/>
	<br/>

<%
	Iterator it2 = invitedUsersProblems.keySet().iterator();
	if(it2.hasNext())
	{
%>
	The following users could not be invited:
	<br/>
	<br/>
<%
		while(it2.hasNext())
		{
			String userId = (String) it2.next();
			String message = (String) invitedUsersProblems.get(userId);

			out.println(userId + " (" + message + ")");
			out.println("<BR/>");
		}
	}
%>	
	
   </body>
</html>
