{
	"results" : {

<#assign first = true>
<#list results as result>
	<#if first == false>,</#if>
	
	"${result.id}" : {
	
	<#assign pFirst = true>
	<#list result.properties?keys as pKey>
		<#if pFirst == false>,</#if>
		
		<#assign r = result.properties[pKey]>
		
		"${pKey}" : "${result.properties[pKey]?replace("\"", "'")}"
		
		<#assign pFirst = false>
	</#list>
	
	}
	
	<#assign first = false>
</#list>

	}
}