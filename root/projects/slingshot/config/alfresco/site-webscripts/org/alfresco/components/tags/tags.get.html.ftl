<script type="text/javascript">//<![CDATA[
   new Alfresco.TagComponent("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="tag tagTitle">
	<h2>	${msg("header.title")}</h2>
	<ul class="filterLink">
	<#if tags?size &gt; 0>
        <#list tags as tag>
            <li class="onTagSelection nav-label" id="${args.htmlid}-onTagSelection-${tag.name}">
                <a href="#" class="tag-link nav-link">${tag.name}</a> (${tag.count})
            </li>
        </#list>
	<#else>
	<li>${msg("label.no-tags")}</li>
	</#if>
	</ul>
</div>