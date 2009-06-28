<script type="text/javascript">//<![CDATA[
   new Alfresco.component.BaseFilter("Alfresco.TopicListFilter", "${args.htmlid}");
//]]></script>
<div class="filter topiclist-filter">
   <h2>${msg("header.browsetopics")}</h2>
   <ul class="filterLink">
   <#list filters as filter>
      <li><span class="${filter.id}"><a class="filter-link" href="#">${msg(filter.label)}</a></span></li>
   </#list>
   </ul>
</div>
