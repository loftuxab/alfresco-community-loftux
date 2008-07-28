<style type="text/css">
<!--
table.userprofile
{
	background-color: white;
}
table.userprofile th {
	padding: 1px 1px 1px 1px;
	background-color: white;
	-moz-border-radius: 0px 0px 0px 0px;
}
table.userprofile td {
	padding: 4px 4px 4px 4px;
	background-color: white;
	-moz-border-radius: 0px 0px 0px 0px;
}
td.userprofile-label
{
	font: bold 12px arial;
}
-->
</style>

<table>
	<tr>
		<td>
			<img src="${url.context}/images/extranet/user_32.gif"/>
		</td>
		<td>
			<font size="5">${user.fullName}</font>
			<br/>
		</td>
	</tr>
</table>
<@anchor pageType="viewprofile">View my profile</@anchor>
|
<@anchor pageType="editprofile">Edit my profile</@anchor>
|
<@anchor pageType="viewcredentials">View/Edit my credentials</@anchor>
|
<@anchor pageType="changepassword">Change my password</@anchor>


<br/>
<br/>
<font size="4">Change my password</font>
<br/>
<br/>

<#if message?exists>
	<font color="red"><b>${message}</b></font>
	<br/>
	<br/>
</#if>

<form action="${url.full}" method="POST">

<table class="userprofile">
	<tr>
		<td>*</td>
		<td class="userprofile-label">Current Password</td>
		<td>
			<input name="originalPassword" type="password" value="" />
		</td>
	</tr>
	<tr>
		<td>*</td>
		<td class="userprofile-label">New Password</td>
		<td>
			<input name="newPassword" type="password" value="" />
		</td>
	</tr>
	<tr>
		<td>*</td>
		<td class="userprofile-label">New Password (Verify)</td>
		<td>
			<input name="newPasswordVerify" type="password" value="" />
		</td>
	</tr>
</table>

<br/>
<input name="username" value="${user.id}" type="hidden"/>
<input type="hidden" name="originalUsername" value="${user.id}"/>
<input type="submit" value="Change my Password"/>

</form>