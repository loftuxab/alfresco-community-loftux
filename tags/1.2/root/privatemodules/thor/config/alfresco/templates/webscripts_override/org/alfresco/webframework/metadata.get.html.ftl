{
	"code": "${code}"

<#if object??>
	,
	"data": {
		<@serialize object=object includeChildren=includeChildren includeContent=includeContent/>
	}
</#if>
}

<#macro serialize object includeChildren includeContent>
<#escape x as jsonUtils.encodeJSONString(x)>
	"isContainer": ${object.isContainer?string}
	,
	"isDocument": ${object.isDocument?string}
	,
	"url": "${object.url}"
	,
	"downloadUrl": "${object.downloadUrl}"
<#if object.mimetype??>
	,
	"mimetype": "${object.mimetype}"
</#if>
	,
	"size": "${object.size}"
	,
	"displayPath": "${object.displayPath}"
	,
	"qnamePath": "${object.qnamePath}"
	,
	"icon16": "${object.icon16}"
	,
	"icon32": "${object.icon32}"
	,
	"isLocked": ${object.isLocked?string}
	,
	"id": "${object.id}"
	,
	"nodeRef": "${object.nodeRef}"
	,
	"name": "${object.name}"
	,
	"type": "${object.type}"
	,
	"isCategory": ${object.isCategory?string}
<#if object.properties??>
	<#if isUser && fullProfile>
	,
	"properties":
	{
		<@serializeHash hash=object.properties/>
	}
	<#else>
	,
	"properties":
	{
		<#local p=object.properties>
		"{http:\/\/www.alfresco.org\/model\/content\/1.0}userName" : "${p.userName}"
		<#if p.firstName??>, "{http:\/\/www.alfresco.org\/model\/content\/1.0}firstName" : "${p.firstName}"</#if>
		<#if p.lastName??>, "{http:\/\/www.alfresco.org\/model\/content\/1.0}lastName" : "${p.lastName}"</#if>
		<#if p.organization??>, "{http:\/\/www.alfresco.org\/model\/content\/1.0}organization" : "${p.organization}"</#if>
		<#if p.jobtitle??>, "{http:\/\/www.alfresco.org\/model\/content\/1.0}jobtitle" : "${p.jobtitle}"</#if>
	}
	</#if>
</#if>
<#if includeChildren && object.children??>
	,
	"children":
	[
		<#assign first = true>
		<#list object.children as child>
		<#if first == false>
		,
		</#if>
		{
			<@serialize object=child includeChildren=false includeContent=includeContent/>
		}
		<#assign first = false>
		</#list>
	]
<#else>
	,
	"children": []
</#if>

<#if isUser && object.associations["cm:avatar"]??>
	,
	"associations":
	{
		"{http://www.alfresco.org/model/content/1.0}avatar": ["${object.associations["cm:avatar"][0].nodeRef}"]
	}
</#if>
<#if isUser>
	,
	"homeTenant": <#if homeTenant??>"${homeTenant}"<#else>null</#if>
	<#if capabilities??>
	,
	"capabilities":
	{
		<@serializeHash hash=capabilities/>
	}
	</#if>
</#if>
<#if isUser && fullProfile>
	<#if immutableProperties??>
	,
	"immutableProperties":
	{
		<@serializeHash hash=immutableProperties/>
	} 
	</#if>
	<#if defaultTenant??>
	,
	"defaultTenant": "${defaultTenant}"
	</#if>
	<#if secondaryTenants??>
	,
	"secondaryTenants":
	[
		<#list secondaryTenants as tenant>
		"${tenant}"<#if tenant_has_next>,</#if>
		</#list>
	]
	</#if>
	,
	"isExternal": <#if object.hasAspect("cloud:personExternal")>true<#else>false</#if>,
	"isNetworkAdmin": <#if object.hasAspect("cloud:networkAdmin")>true<#else>false</#if>
	<#if accountType??>
	,
	"accountTypeId": ${accountType.id?c},
	"accountClassName": "${accountType.accountClass.name!""}",
	"accountClassDisplayName": "${accountType.accountClass.displayName!""}"
	</#if>
</#if>
</#escape>
</#macro>

<#macro serializeSequence sequence>
[
<#local first = true>
<#list sequence as e>
	<#if !first>,<#else><#local first = false></#if>
	<#if isUser && object.isTemplateContent(e)>"${e.content}"
	<#elseif object.isTemplateNodeRef(e)>"${e.nodeRef}"
	<#elseif e?is_date>"${xmldate(e)}"
	<#elseif e?is_boolean>${e?string}
	<#elseif e?is_number>${e?c}
	<#else>"${e}"
	</#if>
</#list>
]
</#macro>

<#macro serializeHash hash>
<#escape x as jsonUtils.encodeJSONString(x)>
<#local first = true>
<#list hash?keys as key>
	<#if hash[key]??>
		<#local val = hash[key]>
		<#if !first>,<#else><#local first = false></#if>"${key}":
		<#if isUser && object.isTemplateContent(val)>"${val.content}"
		<#elseif object.isTemplateNodeRef(val)>"${val.nodeRef}"
		<#elseif val?is_date>"${xmldate(val)}"
		<#elseif val?is_boolean>${val?string}
		<#elseif val?is_number>${val?c}
		<#elseif val?is_sequence><@serializeSequence sequence=val/>
		<#else>"${val}"
		</#if>
	</#if>
</#list>
</#escape>
</#macro>