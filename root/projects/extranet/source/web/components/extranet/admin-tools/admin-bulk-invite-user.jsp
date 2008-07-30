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
	
	// load the file
	File f = new File("/customers.txt");
	FileInputStream in = new FileInputStream(f);	
	String text = org.alfresco.tools.DataUtil.toString(in);
	
	// data containers
	List headers = new ArrayList();
	List rows = new ArrayList();
	
	// read the file contents	
	int rowCount = 0;
	int x = 0;
	int y = -1;
	do
	{
		y = text.indexOf("\r", x);
		if(y > -1)
		{
			String row = text.substring(x, y);
			
			Map elements = null;
			
			int columnCount = 0;
			
			StringTokenizer tokenizer = new StringTokenizer(row, "\t");
			while(tokenizer.hasMoreTokens())
			{
				String value = (String) tokenizer.nextElement();
				
				if(rowCount == 0)
				{
					headers.add(value);
				}
				else
				{
					if(elements == null)
					{
						elements = new HashMap();
						rows.add(elements);
					}
					
					String header = (String) headers.get(columnCount);					
					elements.put(header, value);
					
				}
				
				columnCount++;
			}
			
			x = y + 1;
			rowCount++;
		}
	}
	while(y > -1);	
%>
<html>
   <head><title>Bulk Invite Users</title></head>
   <body>
   
   	<form method="POST" action="/extranet/">
   		<input type="hidden" name="p" value="admin-tools"/>
   		<input type="hidden" name="dispatchTo" value="admin-invite-user"/>
   	

		<input type="submit" value="Invite"/>
		<br/>
		
		<table>
			<tr>
				<td></td>
				<td nowrap>User ID</td>
				<td nowrap>First Name</td>
				<td nowrap>Last Name</td>
				<td nowrap>Email</td>
				<td nowrap>Web Helpdesk User ID</td>
				<td nowrap>Alfresco User ID</td>
				<td nowrap>Company</td>
			</tr>
<%
for(int i = 0; i < rows.size(); i++)
{
	Map row = (Map) rows.get(i);
	
	String userId = (String) row.get("USERID");
	String firstName = (String) row.get("FIRSTNAME");
	String lastName = (String) row.get("LASTNAME");
	String email = (String) row.get("EMAIL");
	String whdUserId = (String) row.get("WHDUSERNAME");
	String alfrescoUserId = (String) row.get("ALFRESCOUSERNAME");
	String companyName = (String) row.get("COMPANYNAME");
	
	// does this user already exist?
	DatabaseInvitedUser databaseInvitedUser = invitationService.getInvitedUserByEmail(email);
	if(databaseInvitedUser == null)
	{
%>
			<tr>
				<td>
					<input type="checkbox" name="invitedUsers" value="<%=userId%>"/>
				</td>
				<td><%=userId%></td>
				<td><%=firstName%></td>
				<td><%=lastName%></td>
				<td><%=email%></td>
				<td><%=whdUserId%></td>
				<td><%=alfrescoUserId%></td>
				<td><%=companyName%></td>
			</tr>
			
			<input type="hidden" name="<%=userId%>_userId" value="<%=userId%>"/>
			<input type="hidden" name="<%=userId%>_firstName" value="<%=firstName%>"/>
			<input type="hidden" name="<%=userId%>_lastName" value="<%=lastName%>"/>
			<input type="hidden" name="<%=userId%>_email" value="<%=email%>"/>
			<input type="hidden" name="<%=userId%>_whdUserId" value="<%=whdUserId%>"/>
			<input type="hidden" name="<%=userId%>_alfrescoUserId" value="<%=alfrescoUserId%>"/>
			<input type="hidden" name="<%=userId%>_companyName" value="<%=companyName%>"/>
			
<%
	}
	else
	{
%>
<tr>
				<td></td>
				<td><font color="gray"><%=userId%></font></td>
				<td><font color="gray"><%=firstName%></font></td>
				<td><font color="gray"><%=lastName%></font></td>
				<td><font color="gray"><%=email%></font></td>
				<td><font color="gray"><%=whdUserId%></font></td>
				<td><font color="gray"><%=alfrescoUserId%></font></td>
				<td><font color="gray"><%=companyName%></font></td>
			</tr>
<%
	}
}
%>
		</table>
		
	</form>
			
   </body>
</html>
