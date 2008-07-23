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
		
		String subscriptionStart = request.getParameter("subscriptionStart");
		String subscriptionEnd = request.getParameter("subscriptionEnd");
		
		// we only handle enterprise invitation types for the moment
		String invitationType = request.getParameter("invitationType");
		if("enterprise".equals(invitationType))
		{
			// build date objects
			Date subscriptionStartDate = null;
			if(subscriptionStart != null)
			{
				subscriptionStartDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT).parse(subscriptionStart);
			}
			Date subscriptionEndDate = null;
			if(subscriptionEnd != null)
			{
				subscriptionEndDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT).parse(subscriptionEnd);
			}
			
			// invite the user
			DatabaseInvitedUser invitedUser = invitationService.inviteUser(userId, firstName, lastName, email, whdUserId, alfrescoUserId, "enterprise", subscriptionStartDate, subscriptionEndDate);
			if(invitedUser != null)
			{
				out.println("Invitation was created!");
				return;
			}
		}
	}
%>
<html>
   <head>
   	<title>Invite a User</title>
   </head>
   <body>
   
   
<SCRIPT LANGUAGE="javascript">
{
self.name="BODY"
}
function calpopup(Ink){
window.open(Ink,"calendar","height=250,width=250,scrollbars=no")
}
</SCRIPT>
<script language='javascript' src='/extranet/components/extranet/datepicker/popcalendar.js'></script>

   
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
						<option value="enterprise">Supported Enterprise Customer</option>
						<!--
							<option value="enterprise_trial">Enterprise Trial</option>
							<option value="community">Community</option>
						-->
					</select>
				</td>
			</tr>
			<tr>
				<td>Subscription Start</td>
				<td>
					<input type="text" name="subscriptionStart" id="subscriptionStart" value=""/>
					<img src="/extranet/components/extranet/datepicker/calendaricon.gif" height="17" width="17" border="0" onClick="popUpCalendar(this, document.getElementById('subscriptionStart'), 'm/dd/yyyy', 0, 0)"/>
				</td>
			</tr>
			<tr>
				<td>Subscription End</td>
				<td>
					<input type="text" name="subscriptionEnd" id="subscriptionEnd" value=""/>
					<img src="/extranet/components/extranet/datepicker/calendaricon.gif" height="17" width="17" border="0" onClick="popUpCalendar(this, document.getElementById('subscriptionEnd'), 'm/dd/yyyy', 0, 0)"/>
				</td>
			</tr>
		</table>
		
		<input type="hidden" name="command" value="inviteUser"/>
		<br/>
		<input type="submit" value="Invite"/>
	</form>
			
   </body>
</html>
