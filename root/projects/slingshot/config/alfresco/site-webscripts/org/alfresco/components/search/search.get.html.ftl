<script type="text/javascript">//<![CDATA[
   new Alfresco.Search("${args.htmlid}").setOptions(
   {
      siteId: "${siteId}",
      siteName: "${siteName?js_string}",
      initialSearchTerm: "${searchTerm?js_string}",
      initialSearchAll: "${searchAll?string}",
      minSearchTermLength: ${args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length')},
      maxSearchResults: ${args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results')}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="search">
   <div class="resultbar theme-bg-color-3">
      <span id="${args.htmlid}-search-info">
         ${msg("search.info.searching")}
      </span>
      
      <span id="${args.htmlid}-scope-toggle-container" class="hidden">
      (
         <a href="#" id="${args.htmlid}-scope-toggle-link" class="search-scope-toggle"></a>
      )
      </span>
   </div>
   
   <div id="${args.htmlid}-results" class="results"></div>
</div>