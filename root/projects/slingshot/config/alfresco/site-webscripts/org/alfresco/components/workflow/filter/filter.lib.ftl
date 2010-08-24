<#macro template header jsClass="Alfresco.component.BaseFilter" filterName="">
   <#local filterIds = "">
   <div class="filter doclib-filter">
      <h2>${header}</h2>
      <ul class="filterLink">
      <#list filters as filter>
         <#local filterIds>${filterIds}"${filter.id}"<#if filter_has_next>,</#if></#local>
         <li><span class="${filter.id}"><a class="filter-link" rel="${filter.data?html}" href="#">${msg(filter.label)}</a></span></li>
      </#list>
      </ul>
   </div>
   <#nested>
   <script type="text/javascript">//<![CDATA[
      new ${jsClass}("${filterName!jsClass}", "${args.htmlid?js_string}").setFilterIds([${filterIds}]);
   //]]></script>
</#macro>