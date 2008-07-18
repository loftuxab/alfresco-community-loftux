		{
			"title": "${object.title}"
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
			"displayPath": "${obj.displayPath}/${obj.name}",
			"downloadUrl": "${obj.downloadUrl}"
		}
	</#list>				
			]
</#if>
		}

