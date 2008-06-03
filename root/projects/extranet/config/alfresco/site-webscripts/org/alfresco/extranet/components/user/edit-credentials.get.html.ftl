<p align="center">
<br/>
<br/>


View Credentials for ${user.fullName}
<br/>
<br/>

<form action="${url.full}" method="POST">

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
			<td>
				<input name="PARAM_${endpointId}_${propertyKey}" value="${propertyValue}"/>
			</td>
		</tr>
		
	</#list>
	</table>
	
	<hr/>
</#list>

<input type="hidden" name="command" value="update"/>
<input type="hidden" name="successUrl" value='<@link pageType="viewcredentials"/>'/>
<input type="hidden" name="failureUrl" value='<@link pageType="editcredentials"/>'/>
<input type="submit" value="Save" />

</form>



<br/>
<@anchor pageType="editprofile">Edit my profile</@anchor>
<br/>
<@anchor pageType="viewcredentials">View my credentials</@anchor>
<br/>
<br/>
</p>