
<script type="text/javascript">//<![CDATA[
   new Alfresco.Search("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!""}",
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="search">
   <div id="${args.htmlid}-resultsBar" class="resultbar">
      Search for "<b>Awards</b>" in <b>Content Community</b> returned <b>32</b> results (<a>Search All Sites</a>)
   </div>
   <div>
      <input type="text" value="" id="${args.htmlid}-search-text" name="${args.htmlid}-search-text" />
      <button id="${args.htmlid}-search-button"></button>
   </div>

   <div id="${args.htmlid}-results" class="documents"></div>

</div>