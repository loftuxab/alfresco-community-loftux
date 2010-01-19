<script type="text/javascript">//<![CDATA[
   new Alfresco.AuthorityFinder("${args.htmlid}").setOptions(
   {
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${args.site!""}</#if>",
      minSearchTermLength: ${args.minSearchTermLength!'3'},
      maxSearchResults: ${args.maxSearchResults!'100'},
      setFocus: ${args.setFocus!'false'},
      addButtonSuffix: "${args.addButtonSuffix!''}",
      dataWebScript: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-query",
      viewMode: Alfresco.AuthorityFinder.VIEW_MODE_DEFAULT,
      authorityType: Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="authority-finder list">
   
   <div id="${args.htmlid}-title" class="title"><label for="${args.htmlid}-search-text">&nbsp;</label></div>
   
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" /></div>
         <div class="authority-search-button">
            <span id="${args.htmlid}-authority-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
         </div>
      </div>
      
      <div id="${args.htmlid}-results" class="results"></div>
   </div>
</div>