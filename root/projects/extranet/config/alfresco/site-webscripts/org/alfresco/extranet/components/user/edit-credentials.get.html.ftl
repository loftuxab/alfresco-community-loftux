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
td.userprofile-grayed
{
	padding: 4px 4px 4px 4px;
	background-color: white;
	-moz-border-radius: 0px 0px 0px 0px;
	color: lightgray;
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


<br/>
<br/>
<font size="4">Edit User Credentials</font>
<br/>
<br/>












<#assign endpointImage = "/images/extranet/user_credentials_32.gif">

<#assign cleartextUsername = "">
<#assign cleartextPassword = "">

<#if vault.properties[endpointId]?exists>
	<#assign credentials = vault.properties[endpointId]>
	<#assign cleartextUsername = credentials.properties["cleartextUsername"]>
	<#assign cleartextPassword = credentials.properties["cleartextPassword"]>	
</#if>

<form action="${url.full}" method="POST">

	<table border="0">
		<tr>
			<td><img src="${url.context}${endpointImage}"/></td>
			<td colspan="2">
				<b>${endpointName}</b>
				<br/>
				<i>${endpointDescription}</i>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>User ID</td>
			<td>
				<input name="PARAM_${endpointId}_cleartextUsername" value="${cleartextUsername}"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>Password</td>
			<td>
				<input name="PARAM_${endpointId}_cleartextPassword" type="password" value="${cleartextPassword}"/>
			</td>
		</tr>
	</table>
	
	<hr/>

<input type="hidden" name="endpointId" value="${endpointId}"/>
<input type="hidden" name="command" value="update"/>
<input type="hidden" name="successUrl" value='${url.context}<@link pageType="viewcredentials"/>'/>
<input type="hidden" name="failureUrl" value='${url.context}<@link pageType="editcredentials"/>&endpointId=${endpointId}'/>
<input type="submit" value="Save" />

</form>
