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
<font size="4">User Profile</font>
<br/>
<br/>
<table class="userprofile">
	<tr>
		<td>*</td>
		<td class="userprofile-label">User Name</td>
		<td>${user.id}</td>
	</tr>
	<tr>
		<td>*</td>
		<td class="userprofile-label">Email</td>
		<td>${user.email}</td>
	</tr>
	<tr>
		<td>*</td>
		<td class="userprofile-label">First Name</td>
		<td>${user.firstName}</td>
	</tr>
	<tr>
		<td>*</td>
		<td class="userprofile-label">Last Name</td>
		<td>${user.lastName}</td>
	</tr>
</table>

