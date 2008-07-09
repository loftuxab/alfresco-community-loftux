<p align="center">
<br/>
<br/>


View Credentials for ${user.fullName}
<br/>
<br/>

<#list endpointIds as endpointId>

	<#assign endpointPersistent = endpointPersistence[endpointId]>
	<#if endpointPersistent == true>
		<@renderEndpoint endpointId="${endpointId}"/>
		<hr/>
	</#if>
</#list>

<#list endpointIds as endpointId>

	<#assign endpointPersistent = endpointPersistence[endpointId]>
	<#if endpointPersistent == false>
		<@renderEndpoint endpointId="${endpointId}"/>
		<hr/>
	</#if>
</#list>

<br/>
<@anchor pageType="editprofile">Edit my profile</@anchor>
<br/>
<@anchor pageType="editcredentials">Edit my credentials</@anchor>
<br/>
<br/>
</p>


<#macro renderEndpoint endpointId>
	
	<#assign endpointName = endpointNames[endpointId]>
	<#assign endpointDescription = endpointDescriptions[endpointId]>	
	<#assign endpointPersistent = endpointPersistence[endpointId]>	
	<#assign endpointImage = "/images/extranet/system_credentials_32.gif">
	<#if endpointPersistent == true>
		<#assign endpointImage = "/images/extranet/user_credentials_32.gif">
	</#if>
	<#assign editCredentialsImage = "/images/extranet/edit_16.gif">
	<#assign editCredentialsUrl = "?f=default&pt=editcredentials&endpointId=" + endpointId>

	<table border="0">
		<tr>
			<td><img src="${url.context}${endpointImage}"/></td>
			<td colspan="2">
				<b>${endpointName}</b>				
				<#if endpointPersistent == true>
					<a href="${url.context}${editCredentialsUrl}">
					<img src="${url.context}${editCredentialsImage}"/>
					</a>
				</#if>
				<br/>				
				<i>${endpointDescription}</i>
			</td>
		</tr>
		
		<#if endpointPersistent == false>
		<tr>
			<td></td>
			<td colspan="2">
				These credentials are managed by the application
			</td>
		</tr>		
		</#if>
	
		<#if endpointPersistent == true>
		
			<#if vault.properties[endpointId]?exists>
			
				<#assign credentials = vault.properties[endpointId]>
				<#assign username = credentials.properties["cleartextUsername"]>
				<#assign password = credentials.properties["cleartextPassword"]>
		<tr>
			<td></td>
			<td>User ID</td>
			<td>${username}</td>
		</tr>	
		<tr>
			<td></td>
			<td>Password</td>
			<td>*********</td>
		</tr>
		
			<#else>

		<tr>
			<td></td>
			<td colspan="2">None defined</td>
		</tr>	
			
			</#if>
		
		</#if>	
	</table>
</#macro>



