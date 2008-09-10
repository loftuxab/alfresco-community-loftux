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
    System.out.println("blah");
    // safety check
	AdminUtil admin = new AdminUtil(request);
    if( !admin.isAuthorizedAdmin())
	{
		out.println(admin.getAccessDeniedMessage());
		return;
	}

	// get services
	InvitationService invitationService = ExtranetHelper.getInvitationService(request);
	UserService userService = ExtranetHelper.getUserService(request);

	// get the invitation hash
	String hash = (String) request.getParameter("hash");

	// determine the user who has been invited
	DatabaseInvitedUser invitedUser = invitationService.getInvitedUserFromHash(hash);

	// message
	String message = (String) request.getAttribute("user-invitation-message");
%>
<%
	// check to see if they were already processed
	if(invitedUser.isCompleted())
	{
	    // they already completed, so escape out
%>
		This user has already processed their invitation.
<%
		return;
	}

    // send the email if we're supposed to
    if( request.getParameter(Constants.ADMIN_TOOLS_COMMAND) != null &&
    request.getParameter(Constants.ADMIN_TOOLS_COMMAND).equals(Constants.ADMIN_TOOLS_COMMAND_INVITE_USER) )
    {
        invitationService.sendEmail( invitedUser );
%>
            email sent to <%=invitedUser.getFirstName()%> <%=invitedUser.getLastName()%> at <%=invitedUser.getEmail()%>
<%
        return;
    }

	if(message != null)
	{
%>
        <font color="red"><b><%=message%></b></font>
<%
	}
%>
Verify the user's information before sending the invite:


<form method="POST" action="/extranet/">

	<table>
		<tr>
			<td colspan="2"><b>Account Details</b></td>
		</tr>
		<tr>
			<td>User ID</td>
			<td><%=invitedUser.getUserId()%></td>
		</tr>
		<tr>
			<td colspan="2"><br/></td>
		</tr>
		<tr>
			<td colspan="2"><b>User Details</b></td>
		</tr>
		<tr>
			<td>First Name</td>
			<td><%=invitedUser.getFirstName()%>
			</td>
		</tr>
		<tr>
			<td>Last Name</td>
			<td><%=invitedUser.getLastName()%>
			</td>
		</tr>
		<tr>
			<td>Email</td>
			<td><%=invitedUser.getEmail()%>
			</td>
		</tr>
		<tr>
			<td><i>Web Helpdesk User</i></td>
			<td><%=(invitedUser.getWebHelpdeskUserId() != null ? invitedUser.getWebHelpdeskUserId() : "")%>
			</td>
		</tr>
		<tr>
			<td><i>Alfresco User</i></td>
			<td><%=(invitedUser.getAlfrescoUserId() != null ? invitedUser.getAlfrescoUserId() : "")%>
			</td>
		</tr>
        <tr>
            <td><i>Subscription</i></td>
            <td><%=invitedUser.getSubscriptionStart()%> to <%=invitedUser.getSubscriptionEnd()%></td>
        </tr>
	</table>

	<input name="<%=Constants.ADMIN_TOOLS_INVITED_USER_ID%>" type="hidden" value="<%=invitedUser.getUserId()%>"/>

	<!--  Sync Properties  -->
	<input type="hidden" name="<%=Constants.ADMIN_TOOLS_WHD_USER_ID%>" value="<%=(invitedUser.getWebHelpdeskUserId() != null ? invitedUser.getWebHelpdeskUserId() : "")%>" />
	<input type="hidden" name="<%=Constants.ADMIN_TOOLS_ALFRESCO_USER_ID%>" value="<%=(invitedUser.getAlfrescoUserId() != null ? invitedUser.getAlfrescoUserId() : "")%>" />

	<!--  Dispatch Properties  -->
	<input type="hidden" name="<%=Constants.ADMIN_TOOLS_P%>" value="<%=Constants.ADMIN_TOOLS%>"/>
	<input type="hidden" name="<%=Constants.ADMIN_TOOLS_DISPATCH_TO%>" value="<%=Constants.ADMIN_TOOLS_DISPATCH_TO_EMAIL_INVITE%>"/>
	<input type="hidden" name="<%=Constants.ADMIN_TOOLS_HASH%>" value="<%=hash%>"/>
    <input type="hidden" name="<%=Constants.ADMIN_TOOLS_COMMAND%>" value="<%=Constants.ADMIN_TOOLS_COMMAND_INVITE_USER%>"/>
	<br/>
	<input type="submit" value="Invite"/>
</form>


</p>
