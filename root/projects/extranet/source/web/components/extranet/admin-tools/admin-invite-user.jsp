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
	AdminUtil admin = new AdminUtil(request);
    if( !admin.isAuthorizedAdmin())
	{
        //TODO: replace with redirect and error message
		out.println("Access denied");
		return;
	}


    String invitationResult = admin.inviteUser();
    out.println( invitationResult );
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
   		<input type="hidden" name="p" value="<%=Constants.ADMIN_TOOLS%>"/>
   		<input type="hidden" name="<%=Constants.ADMIN_TOOLS_DISPATCH_TO%>"
                  value="<%=Constants.ADMIN_TOOLS_DISPATCH_TO_INVITE_USER%>"/>

		<table>
			<tr>
				<td>User ID</td>
				<td>
					<input name="<%=Constants.ADMIN_TOOLS_USER_ID%>" type="text"/>
				</td>
			</tr>
			<tr>
				<td>First Name</td>
				<td>
					<input name="<%=Constants.ADMIN_TOOLS_FIRST_NAME%>" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Last Name</td>
				<td>
					<input name="<%=Constants.ADMIN_TOOLS_LAST_NAME%>" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Email</td>
				<td>
					<input name="<%=Constants.ADMIN_TOOLS_EMAIL%>" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Web Helpdesk User ID</td>
				<td>
					<input name="<%=Constants.ADMIN_TOOLS_WHD_USER_ID%>" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Alfresco User ID</td>
				<td>
					<input name="<%=Constants.ADMIN_TOOLS_ALFRESCO_USER_ID%>" type="text"/>
				</td>
			</tr>
			<tr>
				<td>Invitation Type</td>
				<td>
					<select name="<%=Constants.ADMIN_TOOLS_INVITATION_TYPE%>">
						<option value="<%=Constants.ADMIN_TOOLS_INVITATION_ENTERPRISE%>">Supported Enterprise Customer</option>
						<option value="<%=Constants.ADMIN_TOOLS_INVITATION_EMPLOYEE%>">Alfresco Employee</option>
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
					<input type="text" name="<%=Constants.ADMIN_TOOLS_SUBSCRIPTION_START%>" id="<%=Constants.ADMIN_TOOLS_SUBSCRIPTION_START%>" value=""/>
					<img src="/extranet/components/extranet/datepicker/calendaricon.gif" height="17" width="17" border="0" onClick="popUpCalendar(this, document.getElementById('<%=Constants.ADMIN_TOOLS_SUBSCRIPTION_START%>'), 'm/dd/yyyy', 0, 0)"/>
					&nbsp;(m/dd/yyyy)
				</td>
			</tr>
			<tr>
				<td>Subscription End</td>
				<td>
					<input type="text" name="<%=Constants.ADMIN_TOOLS_SUBSCRIPTION_END%>" id="<%=Constants.ADMIN_TOOLS_SUBSCRIPTION_END%>" value=""/>
					<img src="/extranet/components/extranet/datepicker/calendaricon.gif" height="17" width="17" border="0" onClick="popUpCalendar(this, document.getElementById('<%=Constants.ADMIN_TOOLS_SUBSCRIPTION_END%>'), 'm/dd/yyyy', 0, 0)"/>
					&nbsp;(m/dd/yyyy)
				</td>
			</tr>
		</table>

		<input type="hidden" name="<%=Constants.ADMIN_TOOLS_COMMAND%>" value="<%=Constants.ADMIN_TOOLS_COMMAND_INVITE_USER%>"/>
		<br/>
		<input type="submit" value="Invite"/>
	</form>

   </body>
</html>
