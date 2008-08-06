<%@ page import="org.alfresco.connector.*" %>
<%@ page import="org.alfresco.extranet.*" %>
<%@ page import="org.alfresco.extranet.database.*" %>
<%@ page import="org.alfresco.extranet.ldap.*" %>
<%@ page import="org.alfresco.extranet.webhelpdesk.*" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.jdbc.support.rowset.SqlRowSet" %>
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
	WebHelpdeskService whdService = ExtranetHelper.getWebHelpdeskService(request);
	UserService userService = ExtranetHelper.getUserService(request);
%>
<%
	Map emailUserMap = new HashMap();
	Map usernameUserMap = new HashMap();
	
	// query for all existing users
	List dbUsers = userService.list();
	for(int z = 0; z < dbUsers.size(); z++)
	{
		DatabaseUser dbUser = (DatabaseUser) dbUsers.get(z);
		
		String username = dbUser.getUserId();
		String email = dbUser.getEmail();
		
		emailUserMap.put(email, dbUser);
		usernameUserMap.put(username, dbUser);
	}
%>
<html>
   <head><title>Invite ACT Users</title></head>
   <body>
   
   	<form method="POST" action="/extranet/">
   		<input type="hidden" name="p" value="admin-tools"/>
   		<input type="hidden" name="dispatchTo" value="admin-act-invite-users-process"/>
   	

		<input type="submit" value="Invite"/>
		
		<table>
			<tr>
				<td></td>
				<td nowrap>DBID</td>
				<td nowrap>User Name</td>
				<td nowrap>First Name</td>
				<td nowrap>Last Name</td>
				<td nowrap>Email</td>
				<td nowrap>Web Helpdesk User ID</td>
				<td nowrap>Company</td>
			</tr>
<%
	// query for customers
	String sql = "SELECT CLIENT.CLIENT_ID, CLIENT.USER_NAME, CLIENT.FIRST_NAME, CLIENT.LAST_NAME, CLIENT.EMAIL, LOCATION.LOCATION_NAME from CLIENT, LOCATION where (CLIENT.DELETED = 0 OR CLIENT.DELETED IS NULL) AND LOCATION.LOCATION_ID = CLIENT.LOCATION_ID order by CLIENT.CLIENT_ID";

	// run the query
	int count = 0;
	SqlRowSet rowSet = whdService.query(sql);
	while(rowSet.next())
	{
		int clientId = (int) rowSet.getInt("CLIENT_ID");
		String whdUserId = (String) rowSet.getString("USER_NAME");
		String firstName = (String) rowSet.getString("FIRST_NAME");
		String lastName = (String) rowSet.getString("LAST_NAME");
		String email = (String) rowSet.getString("EMAIL");
		String companyName = (String) rowSet.getString("LOCATION_NAME");
		
		// assumptions on subscription start/end
		String subscriptionStart = "07/01/2008";
		String subscriptionEnd = "07/01/2009";
		
		// assumption on invitation type
		String invitationType = "enterprise";
		
		// proposed user name
		String userName = firstName.substring(0,1).toLowerCase().replace(" ", "") + lastName.toLowerCase().replace(" ", "");
		
		// do they already exist
		boolean usernameExists = (usernameUserMap.get(userName) != null);
		boolean emailExists = (emailUserMap.get(email) != null);
		
		if(!usernameExists && !emailExists)
		{		
%>		
			<tr>
				<td>
					<input type="checkbox" name="invitedUsers" value="<%=userName%>"/>
				</td>
				<td><%=clientId%></td>
				<td><%=userName%></td>
				<td><%=firstName%></td>
				<td><%=lastName%></td>
				<td><%=email%></td>
				<td><%=whdUserId%></td>
				<td><%=companyName%></td>
			</tr>
			
			<input type="hidden" name="<%=userName%>_userName" value="<%=userName%>"/>
			<input type="hidden" name="<%=userName%>_firstName" value="<%=firstName%>"/>
			<input type="hidden" name="<%=userName%>_lastName" value="<%=lastName%>"/>
			<input type="hidden" name="<%=userName%>_email" value="<%=email%>"/>
			<input type="hidden" name="<%=userName%>_whdUserId" value="<%=whdUserId%>"/>
			<input type="hidden" name="<%=userName%>_companyName" value="<%=companyName%>"/>

			<!-- hidden fields -->		
			<input type="hidden" name="<%=userName%>_alfrescoUserId" value="<%=userName%>"/>
			<input type="hidden" name="<%=userName%>_invitationType" value="<%=invitationType%>"/>
			<input type="hidden" name="<%=userName%>_subscriptionStart" value="<%=subscriptionStart%>"/>
			<input type="hidden" name="<%=userName%>_subscriptionEnd" value="<%=subscriptionEnd%>"/>
			
<%
		}
		else
		{
%>
<tr>
				<td></td>
				<td><font color="gray"><%=clientId%></font></td>
				<td><font color="gray"><%=userName%></font></td>
				<td><font color="gray"><%=firstName%></font></td>
				<td><font color="gray"><%=lastName%></font></td>
				<td><font color="gray"><%=email%></font></td>
				<td><font color="gray"><%=whdUserId%></font></td>
				<td><font color="gray"><%=companyName%></font></td>
			</tr>
<%
		}
		
		count++;
	}
%>

		</table>		
		
		<%=count%> Total Rows
	</form>
   </body>
</html>
