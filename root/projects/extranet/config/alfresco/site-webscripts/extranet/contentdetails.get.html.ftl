<table>

<tr>
	<td>
		ID
	</td>
	<td>
		${content.getId()}
	</td>
</tr>
<tr>
	<td>
		Type
	</td>
	<td>
		${content.getTypeId()}
	</td>
</tr>
<tr>
	<td>
		Name
	</td>
	<td>
		${content.getProperty("name")}
	</td>
</tr>
<tr>
	<td>
		Size
	</td>
	<td>
		${content.getProperty("size")}
	</td>
</tr>

</table>

<a href="${content.getProperty("url")}">Download</a>