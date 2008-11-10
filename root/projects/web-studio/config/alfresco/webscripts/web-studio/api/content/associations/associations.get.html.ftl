{
	"content-associations": [

<#assign first = true>
<#list associations as association>

	<#if !first>,</#if>
	
	{
		'id' : '${association.id}'
		
		<#if association.title?exists>
		,
		'title': '${association.title}'
		</#if>

		<#if association.description?exists>
		,
		'description': '${association.description}'
		</#if>
		
		<#if association.properties["source-id"]?exists>
		,
		'source-id': '${association.properties["source-id"]}'
		</#if>

		<#if association.properties["dest-id"]?exists>
		,
		'dest-id': '${association.properties["dest-id"]}'
		</#if>

		<#if association.properties["assoc-type"]?exists>
		,
		'assoc-type': '${association.properties["assoc-type"]}'
		</#if>

		<#if association.properties["format-id"]?exists>
		,
		'format-id': '${association.properties["format-id"]}'
		</#if>
	}	
	
	<#assign first = false>

</#list>

	]
}