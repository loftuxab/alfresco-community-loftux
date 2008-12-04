{
	"status" : "${status}"	
	,	
	"webProjectId" : "${webProjectId}"
	,
	"sandboxId" : "${sandboxId}"
	,
	"storeId" : "${storeId}"

<#if status == 'importing'>
	,
	"taskId" : "${taskId}"
</#if>	

}