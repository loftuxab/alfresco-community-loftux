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
	
	// get the invitation hash
	String hash = (String) request.getParameter("hash");
	
	// determine the user who has been invited
	DatabaseInvitedUser invitedUser = invitationService.getInvitedUserFromHash(hash);
	
	// message
	String message = (String) request.getAttribute("user-invitation-message");	
%>
<h5>Welcome to Alfresco Network!</h5>
<p>
You have been invited to be a part of the Alfresco Enterprise Network community.

<br/>
<br/>
Please verify the following invitation data.
<br/>
If you need to make any corrections, please do so.
<br/>
<br/>
When you are done, click "Check" below to go to the next step
<br/>
<br/>
<%
	if(message != null)
	{
		out.println("<font color='red'><b>");
		out.println(message);
		out.println("</b></font>");
	}
%>      		

<form method="POST" action="/extranet/">

	<table>
		<tr>
			<td colspan="2"><b>Account Details</b></td>
		</tr>
		<tr>
			<td>User ID</td>
			<td>
				<input name="userId" type="text" value="<%=invitedUser.getUserId()%>"/>
			</td>
		</tr>
		<tr>
			<td>Password</td>
			<td>
				<input name="password" type="password" value=""/>
			</td>
		</tr>
		<tr>
			<td>Password (verify)</td>
			<td>
				<input name="passwordVerify" type="password" value=""/>
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
				<input name="firstName" type="text" value="<%=invitedUser.getFirstName()%>"/>
			</td>
		</tr>
		<tr>
			<td>Last Name</td>
			<td>
				<input name="lastName" type="text" value="<%=invitedUser.getLastName()%>"/>
			</td>
		</tr>
		<tr>
			<td>Email</td>
			<td>
				<input name="email" type="text" value="<%=invitedUser.getEmail()%>"/>
			</td>
		</tr>
		<tr>
			<td><i>Web Helpdesk User</i></td>
			<td>
				<input name="whdUserId" type="text" value="<%=invitedUser.getWebHelpdeskUserId()%>" disabled />
			</td>
		</tr>
		<tr>
			<td><i>Partners User</i></td>
			<td>
				<input name="alfrescoUserId" type="text" value="<%=invitedUser.getAlfrescoUserId()%>" disabled />
			</td>
		</tr>
	</table>
	
	<input name="invitedUserId" type="hidden" value="<%=invitedUser.getUserId()%>"/>

	<input type="hidden" name="p" value="invitation-wizard"/>
	<input type="hidden" name="dispatchTo" value="user-invitation-confirm.jsp"/>
	<input type="hidden" name="hash" value="<%=hash%>"/>
	<br/>
	<input type="submit" value="Check and Verify Settings"/>
</form>


</p>   
