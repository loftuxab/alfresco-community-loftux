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
<font size="4">User Credentials</font>
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
	
	<#assign tdDisplayClass = "">
	<#if endpointPersistent == false>
		<#assign tdDisplayClass = "class='userprofile-grayed'">
	</#if>

	<table border="0" class="userprofile">
		<tr>
			<td><img src="${url.context}${endpointImage}"/></td>
			<td colspan="2" ${tdDisplayClass} >
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
			<td colspan="2" ${tdDisplayClass}>
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
			<td colspan="2" ${tdDisplayClass}>None defined</td>
		</tr>	
			
			</#if>
		
		</#if>	
	</table>
</#macro>



