<script type="text/javascript">//<![CDATA[
   new Alfresco.PeopleFinder("${args.htmlid}").setOptions(
   {
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${args.site!""}</#if>",
      minSearchTermLength: "${args.minSearchTermLength!'3'}",
      maxSearchResults: "${args.maxSearchResults!'100'}",
      setFocus: ${args.setFocus!'false'},
      addButtonSuffix: "${args.addButtonSuffix!''}",
      dataWebScript: "${(args.dataWebScript!'api/people')?replace("[", "{")?replace("]", "}")}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="people-finder list">
   
   <div class="title"><label for="${args.htmlid}-search-text">${msg("title")}</label></div>
   
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" maxlength="256" tabindex="0"/></div>
         <div class="search-button">
            <span id="${args.htmlid}-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
         </div>
      </div>
      
      <div id="${args.htmlid}-results" class="results"></div>
   </div>
</div>