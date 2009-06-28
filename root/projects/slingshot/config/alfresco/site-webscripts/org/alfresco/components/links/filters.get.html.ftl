<script type="text/javascript">//<![CDATA[
   new Alfresco.component.BaseFilter("Alfresco.LinkFilter", "${args.htmlid}");
//]]></script>
<div class="filter links-filter">
   <h2 id="${args.htmlid}-h2">${msg("header.links")}</h2>
   <ul class="filterLink">
   <#list filters as filter>
      <li><span class="${filter.id}"><a class="filter-link" href="#">${msg(filter.label)}</a></span></li>
   </#list>
   </ul>
</div>
