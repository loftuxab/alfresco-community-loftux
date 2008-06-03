<p align="center">
<br/>
<br/>


View Credentials for ${user.fullName}
<br/>
<br/>

<#list vault.properties?keys as endpointId>

	<table border="1">
		<tr>
			<td colspan="2">
				<b>${endpointId}</b>
			</td>
		</tr>
	<#assign credentials = vault.properties[endpointId]>
	<#list credentials.properties?keys as propertyKey>

		<#assign propertyValue = credentials.properties[propertyKey]>
		
		<tr>
			<td>${propertyKey}</td>
			<td>${propertyValue}</td>
		</tr>
		
	</#list>
	</table>
	
	<hr/>
</#list>



<br/>
<@anchor pageType="editprofile">Edit my profile</@anchor>
<br/>
<@anchor pageType="editcredentials">Edit my credentials</@anchor>
<br/>
<br/>
</p>