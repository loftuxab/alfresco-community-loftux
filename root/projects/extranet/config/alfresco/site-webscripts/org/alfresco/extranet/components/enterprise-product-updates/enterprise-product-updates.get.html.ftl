<table width="100%">
	<tr>
		<td></td>
		<td>Title</td>
		<td>Version</td>
		<td>Components</td>
	</tr>
	
<#assign count = 0>
<#list objects as object>

	<#assign bgClass = "tableRowA">
	<#if count % 2 == 0>
		<#assign bgClass = "tableRowB">
	</#if>
	<tr class="${bgClass}">
		<td>
			<img src="${url.context}/images/extranet/info_16.gif"/>
		</td>
		<td>
			<a href="${object.url}" target="_blank">
			${object.title}
			</a>
		</td>
		<td>${object.category}</td>
		<td>${object.components}</td>
	</tr>	
	
	<#assign count = count + 1>

</#list>

</table>