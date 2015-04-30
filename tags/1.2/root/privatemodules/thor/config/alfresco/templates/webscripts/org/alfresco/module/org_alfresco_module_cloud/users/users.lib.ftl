<#macro personJSONinner person>
<#local p=person.properties>
<#escape x as jsonUtils.encodeJSONString(x)>
	"userName": "${p.userName}",
	"enabled": ${people.isAccountEnabled(person)?string("true","false")},
	<#if person.assocs["cm:avatar"]??>
	"avatar": "${"api/node/" + person.assocs["cm:avatar"][0].nodeRef?string?replace('://','/') + "/content/thumbnails/avatar"}",
	</#if>
	"firstName": <#if p.firstName??>"${p.firstName}"<#else>null</#if>,
	"lastName": <#if p.lastName??>"${p.lastName}"<#else>null</#if>,
	"jobtitle": <#if p.jobtitle??>"${p.jobtitle}"<#else>null</#if>,
	"organization": <#if p.organization??>"${p.organization}"<#else>null</#if>,
    "isExternal": <#if person.hasAspect("cloud:personExternal")>true<#else>false</#if>,
    "isNetworkAdmin": <#if person.hasAspect("cloud:networkAdmin")>true<#else>false</#if>
</#escape>
</#macro>

<#macro personJSON person>
{
<@personJSONinner person=person/>
}
</#macro>