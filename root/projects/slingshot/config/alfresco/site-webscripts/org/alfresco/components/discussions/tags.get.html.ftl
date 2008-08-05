<script type="text/javascript">//<![CDATA[
   new Alfresco.TopicListTags("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter menuTitle">
	<h2>${msg("topiclist.tags.title")}</h2>
    <ul class="filterLink">
        <#list tags as tag>
            <li class="filter-link" id="${args.htmlid}-selectTag-${tag.name}">
                <a  href="#" class="tag-link-li nav-link">${tag.name?html}</a> (${tag.count})
            </li>
        </#list>
	</ul>
</div>