<script type="text/javascript">//<![CDATA[
   new Alfresco.PeopleFinder("${args.htmlid}").setOptions(
   {
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${args.site!""}</#if>",
      minSearchTermLength: "${args.minSearchTermLength!'3'}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="people-finder">
   <div class="title">${msg("title")}</div>
   
   <div class="finder-wrapper">
      <div class="search-bar">
         <div class="search-label"><label for="${args.htmlid}-search-text">${msg("label.search")}</label></div>
         <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" /></div>
         <div class="search-button"><button id="${args.htmlid}-search-button">${msg("button.search")}</button></div>
      </div>

      <div id="${args.htmlid}-results" class="results"></div>
   </div>
</div>