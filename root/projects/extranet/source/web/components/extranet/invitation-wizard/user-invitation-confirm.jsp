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
	UserService userService = ExtranetHelper.getUserService(request);
	SyncService syncService = ExtranetHelper.getSyncService(request);
	
	// get the invitation hash
	String hash = (String) request.getParameter("hash");
	
	// determine the user who has been invited
	DatabaseInvitedUser invitedUser = invitationService.getInvitedUserFromHash(hash);
		
	// properties
	String invitedUserId = request.getParameter("invitedUserId");
	String userId = request.getParameter("userId");
	String password = request.getParameter("password");
	String passwordVerify = request.getParameter("passwordVerify");
	String firstName = request.getParameter("firstName");
	String lastName = request.getParameter("lastName");
	String email = request.getParameter("email");
	String whdUserId = request.getParameter("whdUserId");
	String alfrescoUserId = request.getParameter("alfrescoUserId");
	
	// command processing
	if(userId == null)
	{
		// redirect to user-invitation with a message
		request.setAttribute("user-invitation-message", "The user id '" + userId + "' was empty");
		request.getRequestDispatcher("/components/extranet/invitation-wizard/user-invitation.jsp").include(request, response);
		return;
	}
	else
	{
		// check to see if this user id is available
		boolean available = syncService.isUserIdAvailable(userId);
		if(!available)
		{
			// redirect to user-invitation with a message
			request.setAttribute("user-invitation-message", "The user id '" + userId + "' is already in use");
			request.getRequestDispatcher("/components/extranet/invitation-wizard/user-invitation.jsp").include(request, response);
			return;
		}
		
		// check to see if null password
		if(password == null || password.length() == 0)
		{
			// redirect to user-invitation with a message
			request.setAttribute("user-invitation-message", "An empty password was provided.  Please provide a valid password");
			request.getRequestDispatcher("/components/extranet/invitation-wizard/user-invitation.jsp").include(request, response);
			return;
		}
		if(passwordVerify == null || passwordVerify.length() == 0)
		{
			// redirect to user-invitation with a message
			request.setAttribute("user-invitation-message", "An empty password was provided.  Please provide a valid password");
			request.getRequestDispatcher("/components/extranet/invitation-wizard/user-invitation.jsp").include(request, response);
			return;
		}
		
		// check to see if passwords match
		if(!password.equals(passwordVerify))
		{
			// redirect to user-invitation with a message
			request.setAttribute("user-invitation-message", "The passwords provided did not match.  Please re-enter your password and verify that it is correct.");
			request.getRequestDispatcher("/components/extranet/invitation-wizard/user-invitation.jsp").include(request, response);
			return;		
		}
	}	
%>


      <h5>Let's create your account...</h5>
      <p>
      	Congratulations!  Your account is ready to be created.
      	<br/>
      	To authorize the creation of your account, just click "Create my account" below!
      	<br/>
      	<br/>
      	
   	<form method="POST" action="/extranet/">

		<table>
			<tr>
				<td colspan="2"><b>Account Details</b></td>
			</tr>
			<tr>
				<td>User ID</td>
				<td>
					<%=userId%>
				</td>
			</tr>
			<tr>
				<td>Password</td>
				<td>
					*********
				</td>
			</tr>

			<tr>
				<td colspan="2"><br/></td>
			</tr>
			<tr>
				<td colspan="2"><b>User Details</b></td>
			</tr>
			<tr>
				<td>First Name</td>
				<td>
					<%=firstName%>
				</td>
			</tr>
			<tr>
				<td>Last Name</td>
				<td>
					<%=lastName%>
				</td>
			</tr>
			<tr>
				<td>Email</td>
				<td>
					<%=email%>
				</td>
			</tr>
			<tr>
				<td>Web Helpdesk User</td>
				<td>
					<%=((whdUserId!=null)?whdUserId:"N/A")%>
				</td>
			</tr>
			<tr>
				<td>Alfresco User</td>
				<td>
					<%=((alfrescoUserId!=null)?alfrescoUserId:"N/A")%>
				</td>
			</tr>
		</table>
		
		<input type="hidden" name="invitedUserId" value="<%=invitedUserId%>"/>
		
		<input type="hidden" name="userId" value="<%=userId%>"/>
		<input type="hidden" name="password" value="<%=password%>"/>
		<input type="hidden" name="firstName" value="<%=firstName%>"/>
		<input type="hidden" name="lastName" value="<%=lastName%>"/>
		<input type="hidden" name="email" value="<%=email%>"/>
		<input type="hidden" name="whdUserId" value="<%=whdUserId%>"/>
		<input type="hidden" name="alfrescoUserId" value="<%=alfrescoUserId%>"/>
				
		<input type="hidden" name="p" value="invitation-wizard"/>
		<input type="hidden" name="dispatchTo" value="user-invitation-process.jsp"/>		
		<input type="hidden" name="hash" value="<%=hash%>"/>
		
		<br/>
		
		<input type="submit" value="Create my account"/>
	</form>

      	
      </p>      	
