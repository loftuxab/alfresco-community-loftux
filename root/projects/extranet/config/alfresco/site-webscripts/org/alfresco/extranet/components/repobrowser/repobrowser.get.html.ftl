<table>

<#list items as item>
<tr>
	<td>
		<@anchor page="${page.id}" object="${item.nodeRef}">
			<img src="http://localhost:8080/alfresco${item.icon32}" border="0"/>
		</@anchor>
	</td>
	<td>
		<@anchor page="${page.id}" object="${item.nodeRef}">
			${item.name}
		</@anchor>
		<br/>
		${item.description}
	</td>
	<td>
		<@anchor page="page.content.details" object="${item.nodeRef}">
			Details
		</@anchor>
	</td>
</tr>
</#list>

</table>