<script type="text/javascript">//<![CDATA[
   new Alfresco.GroupFinder("${args.htmlid}").setOptions(
   {
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${args.site!""}</#if>",
      minSearchTermLength: "${args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length')}",
      maxSearchResults: "${args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results')}",
      setFocus: ${args.setFocus!'false'},
      addButtonSuffix: "${args.addButtonSuffix!''}",
      dataWebScript: "${(args.dataWebScript!'api/groups')?replace("[", "{")?replace("]", "}")}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="group-finder list">
   
   <div class="title"><label for="${args.htmlid}-search-text">${msg("title")}</label></div>
   
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" /></div>
         <div class="group-search-button">
            <span id="${args.htmlid}-group-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
         </div>
      </div>
      
      <div id="${args.htmlid}-results" class="results"></div>
   </div>
</div>