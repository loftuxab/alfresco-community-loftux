<#assign first=true>
{
	"results" :
	[
	<#list objects as object>
		<#if first==false>,</#if>
		{
			"title": "${object.title}",
			"key": "${object.key}",
			"url": "${object.url}",
			"category": "${object.category}",
			"components": "${object.components}",
			"overview": "${object.overview}",
			"details": "${object.details}"			
		}
		<#assign first=false>
	</#list>
	]
}

