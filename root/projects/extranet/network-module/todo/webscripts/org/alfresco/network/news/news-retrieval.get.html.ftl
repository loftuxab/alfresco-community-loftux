<#assign first=true>
{
	"results" :
	[
	<#list objects as object>
		<#if first==false>,</#if>
		{
			"title": "${object.title}",
			"nodeRef": "${object.nodeRef}",
			"id": "${object.id}"

<#if object.author?exists>			
			,"author": "${object.author}"
</#if>
<#if object.creator?exists>			
			,"creator": "${object.creator}"
</#if>
<#if object.created?exists>			
			,"created": "${object.created?datetime}"
</#if>
<#if object.modifier?exists>			
			,"modifier": "${object.modifier}"
</#if>
<#if object.modified?exists>			
			,"modified": "${object.modified?datetime}"
</#if>						
<#if object.description?exists>			
			,"description": "${object.description}"
</#if>
<#if object.headline?exists>
			,"headline": "${object.headline}"
</#if>
<#if object.teaser?exists>
			,"teaser": "${object.teaser}"
</#if>
<#if object.readMore?exists>
			,"readMore": "${object.readMore}"
</#if>
<#if object.effectiveFrom?exists>
			,"effectiveFrom": "${object.effectiveFrom}"
</#if>
<#if object.effectiveTo?exists>
			,"effectiveTo": "${object.effectiveTo}"
</#if>
<#if object.itemTypes?exists>
			,"itemTypes": [
	<#assign first = true>
	<#list object.itemTypes as obj>
		<#if first == false>,</#if>"${obj}"<#assign first = false>
	</#list>			
			]
</#if>
<#if object.relatedLinks?exists>
			,"relatedLinks": [
	<#assign first = true>
	<#list object.relatedLinks as obj>
		<#if first == false>,</#if>"${obj}"<#assign first = false>
	</#list>						
			]
</#if>



<#if object.teaserImage?exists && object.teaserImage?size &gt; 0>
			,"teaserImage": {
				"title": "${object.teaserImage[0].name}",
				"nodeRef": "${object.teaserImage[0].nodeRef}",
				"id": "${object.teaserImage[0].id}",
				"displayPath": "${object.teaserImage[0].displayPath}/${object.teaserImage[0].name}",
				"downloadUrl": "${object.teaserImage[0].downloadUrl}"
			}
</#if>
<#if object.relatedMedia?exists>
			,"relatedMedia": [
	<#assign first = true>
	<#list object.relatedMedia as obj>
		<#if first == false>,</#if>
		{
			"title": "${obj.name}",
			"nodeRef": "${obj.nodeRef}",
			"id": "${obj.id}",
			"displayPath": "${obj.displayPath}/${obj.name}",
			"downloadUrl": "${obj.downloadUrl}"
		}
	</#list>				
			]
</#if>
<#if object.categories?exists>
			,"categories": [
	<#assign first = true>
	<#list object.categories as obj>
		<#if first == false>,</#if>
		{
			"title": "${obj.name}",
			"nodeRef": "${obj.nodeRef}",
			"id": "${obj.id}",
			"displayPath": "${obj.displayPath}/${obj.name}",
			"downloadUrl": "${obj.downloadUrl}"
		}
	</#list>				
			]
</#if>
		}
		<#assign first=false>
	</#list>
	]
}

