<script type="text/javascript">//<![CDATA[
   new Alfresco.component.BaseFilter("Alfresco.DocListFilter", "${args.htmlid}");
//]]></script>
<div class="filter doclib-filter">
   <h2>${msg("header.documents")}</h2>
   <ul class="filterLink">
   <#list filters as filter>
      <li><span class="${filter.id}"><a rel="${filter.data?html}" href="#">${msg(filter.label)}</a></span></li>
   </#list>
   </ul>
</div>