<script type="text/javascript">//<![CDATA[
   new Alfresco.TopicListTags("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter menuTitle">
	<h2>${msg("topiclist.tags.title")}</h2>
    <ul class="filterLink">
        <#list tags as tag>
            <li class="nav-label" id="${args.htmlid}-onTagSelection-${tag.name}">
                <a  href="#" class="tag-link-li nav-link">${tag.name}</a> (${tag.count})
            </li>
        </#list>
	</ul>
</div>