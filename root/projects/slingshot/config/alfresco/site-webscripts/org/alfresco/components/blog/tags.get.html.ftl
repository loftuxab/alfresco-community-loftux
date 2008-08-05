<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostListTags("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter postlist-filter">
	<h2>${msg("postlist.tags.title")}</h2>
    <ul class="filterLink">
        <#list tags as tag>
            <li class="onTagSelection filter-link" id="${args.htmlid}-selectTag-${tag.name}">
                <a  href="#" class="tag-link nav-link">${tag.name?html}</a> (${tag.count})
            </li>
        </#list>
	</ul>
</div>