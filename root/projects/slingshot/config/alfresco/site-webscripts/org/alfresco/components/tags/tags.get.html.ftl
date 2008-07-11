<script type="text/javascript">//<![CDATA[
   new Alfresco.TagComponent("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="tag tagTitle">
	<h2>	${msg("header.title")}</h2>
	<#if tags?size &gt; 0>
    <ul class="filterLink">
        <#list tags as tag>
            <li class="onTagSelection nav-label" id="${args.htmlid}-onTagSelection-${tag.name}">
                <a href="" class="tag-link nav-link">${tag.name}</a> (${tag.count})
            </li>
        </#list>
	</ul>
	<#else>
	${msg("label.no-tags")}
	</#if>
</div>