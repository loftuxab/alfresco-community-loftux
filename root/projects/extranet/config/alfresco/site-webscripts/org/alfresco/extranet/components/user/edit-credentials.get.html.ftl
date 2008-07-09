<#assign endpointImage = "/images/extranet/user_credentials_32.gif">

<#assign cleartextUsername = "">
<#assign cleartextPassword = "">

<#if vault.properties[endpointId]?exists>
	<#assign credentials = vault.properties[endpointId]>
	<#assign cleartextUsername = credentials.properties["cleartextUsername"]>
	<#assign cleartextPassword = credentials.properties["cleartextPassword"]>	
</#if>

<p align="center">
<br/>
<br/>


Editing Credentials '${endpointId}' for '${user.fullName}'
<br/>
<br/>

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



<br/>
<@anchor pageType="editprofile">Edit my profile</@anchor>
<br/>
<@anchor pageType="viewcredentials">View my credentials</@anchor>
<br/>
<br/>
</p>