<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostListTags("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter menuTitle">
	<h2>${msg("postlist.tags.title")}</h2>
    <ul class="filterLink">
        <#list tags as tag>
            <li class="onTagSelection nav-label" id="${args.htmlid}-onTagSelection-${tag.name}">
                <a  href="" class="tag-link nav-link">${tag.name}</a> (${tag.count})
            </li>
        </#list>
	</ul>
</div>