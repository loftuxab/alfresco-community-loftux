<script type="text/javascript">//<![CDATA[
   new Alfresco.Search("${args.htmlid}").setOptions(
   {
      siteId: "${siteId}",
      containerId: "${containerId}",
      initialSearchTerm: "${searchTerm}",
      initialSearchAll: "${searchAll?string}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="search">
   <div class="resultbar">
      <span class="${args.htmlid}-search-result-info">
      </span>
      
      <#-- Only add switch if we are in a site context -->
      <#if siteId?has_content>
      (
      <a class="${args.htmlid}-search-all-switch">
         Search All Sites
      </a>
      )
      </#if>
   </div>
   <div>
      <input type="text" value="" id="${args.htmlid}-search-text" name="${args.htmlid}-search-text" />
      <button id="${args.htmlid}-search-button"></button>
   </div>

   <div id="${args.htmlid}-results" class="documents"></div>

   <div class="resultbar">
      <span class="${args.htmlid}-search-result-info">
      </span>
      
      <#-- Only add switch if we are in a site context -->
      <#if siteId?has_content>
      (
      <a class="${args.htmlid}-search-all-switch">
         Search All Sites
      </a>
      )
      </#if>
   </div>
</div>