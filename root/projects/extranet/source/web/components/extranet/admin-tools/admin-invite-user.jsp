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
	// get services
	InvitationService invitationService = ExtranetHelper.getInvitationService(request);
	
	// command processor
	String command = request.getParameter("command");
	if("inviteUser".equals(command))
	{
		String userId = request.getParameter("userId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String whdUserId = request.getParameter("whdUserId");
		String alfrescoUserId = request.getParameter("alfrescoUserId");
		
		// we only handle enterprise invitation types for the moment
		String invitationType = request.getParameter("invitationType");
		if("enterprise".equals(invitationType))
		{
			// invite the user
			DatabaseInvitedUser invitedUser = invitationService.inviteUser(userId, firstName, lastName, email, whdUserId, alfrescoUserId, "enterprise");
			if(invitedUser != null)
			{
				out.println("Invitation was created!");
				return;
			}
		}
	}
%>
<html>
   <head><title>Invite a User</title></head>
   <body>
   
   	<form method="POST" action="/extranet/">
   		<input type="hidden" name="p" value="admin-tools"/>
   		<input type="hidden" name="dispatchTo" value="admin-invite-user"/>

		<table>
			<tr>
				<td>User ID</td>
				<td>
					<input name="userId" type="text"/>
				</td>
			</tr>
			<tr>
				<td>First Name</td>
				<td>
					<input name="firstName" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Last Name</td>
				<td>
					<input name="lastName" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Email</td>
				<td>
					<input name="email" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Web Helpdesk User ID</td>
				<td>
					<input name="whdUserId" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Alfresco User ID</td>
				<td>
					<input name="alfrescoUserId" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Invitation Type</td>
				<td>
					<select name="invitationType">
						<option value="enterprise_trial">Enterprise Trial</option>
						<option value="enterprise">Supported Enterprise Customer</option>
						<option value="community">Community</option>
					</select>
				</td>
			</tr>
		</table>
		
		<input type="hidden" name="command" value="inviteUser"/>
		<br/>
		<input type="submit" value="Invite"/>
	</form>
			
   </body>
</html>
