{
	"results" : {
	
<#assign first = true>	
<#list sites?keys as id>

	<#assign site = sites[id]>
	
	<#if id != "toJSONString">
	
		<#if first == false>,</#if>
	
		"${id}" : {
			
			"title" : "${site.title}",
			"description" : "${site.description}",
			"previewImageUrl" : "${site.previewImageUrl}",
			"archiveUrl" : "${site.archiveUrl}"
			
		}
		
		<#assign first = false>
	
	</#if>
		
</#list>

	}
}
