<script type="text/javascript">//<![CDATA[
   new Alfresco.GroupFinder("${args.htmlid}").setOptions(
   {
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${args.site!""}</#if>",
      minSearchTermLength: "${args.minSearchTermLength!'3'}",
      maxSearchResults: "${args.maxSearchResults!'100'}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="group-finder list">
   
   <div class="title"><label for="${args.htmlid}-search-text">${msg("title")}</label></div>
   
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" /></div>
         <div class="search-button">
            <span id="${args.htmlid}-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
         </div>
      </div>
      
      <div id="${args.htmlid}-results" class="results"></div>
   </div>
</div>