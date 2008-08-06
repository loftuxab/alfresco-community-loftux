<%@ page import="java.text.*" %>
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
	// safety check
	org.alfresco.connector.User user = org.alfresco.web.site.RequestUtil.getRequestContext(request).getUser();
	if(user == null || !user.isAdmin())
	{
		out.println("Access denied");
		return;
	}
%>
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
		String userName = request.getParameter(key + "_userName");
		String firstName = request.getParameter(key + "_firstName");
		String lastName = request.getParameter(key + "_lastName");
		String email = request.getParameter(key + "_email");
		String whdUserId = request.getParameter(key + "_whdUserId");
		String companyName = request.getParameter(key + "_companyName");
		String alfrescoUserId = request.getParameter(key + "_alfrescoUserId");
		
		// additional data
		String invitationType = request.getParameter(key + "_invitationType");
		String subscriptionStart = request.getParameter(key + "_subscriptionStart");
		String subscriptionEnd = request.getParameter(key + "_subscriptionEnd");
		
		String message = null;
		
		// check whether the user already exists
		if(userService.getUserByEmail(email) == null)
		{
			if(userService.getUser(userName) == null)
			{
				if(invitationService.getInvitedUser(userName) == null)
				{
					// build date objects
					Date subscriptionStartDate = null;
					if(subscriptionStart != null)
					{
						subscriptionStartDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(subscriptionStart);
					}
					Date subscriptionEndDate = null;
					if(subscriptionEnd != null)
					{
						subscriptionEndDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(subscriptionEnd);
					}
				
					// invite the user
					DatabaseInvitedUser invitedUser = invitationService.inviteUser(userName, firstName, lastName, email, whdUserId, alfrescoUserId, invitationType, subscriptionStartDate, subscriptionEndDate);
					if(invitedUser != null)
					{
						invitedUsersMap.put(userName, invitedUser);
					}
				}
				else
				{
					invitedUsersProblems.put(userName, "The user name '" + userName + "' has already been invited");
				}
				
			}
			else
			{
				invitedUsersProblems.put(userName, "The user name '" + userName + "' already exists as a user of Network");
			}
		}
		else
		{
			invitedUsersProblems.put(userName, "The email '" + email + "' already exists as a user of Network");
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
			DatabaseInvitedUser dbUser = (DatabaseInvitedUser) invitedUsersMap.get(userId);

			out.println(userId + " (" + dbUser.getEmail() + ")");
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
