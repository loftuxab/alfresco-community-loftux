<script type="text/javascript">//<![CDATA[
   new Alfresco.SiteFinder("${args.htmlid}").setOptions(
   {
      showPrivateSites: true,
      currentUser: "${user.id}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="site-finder">
	
	<div class="title">${msg("site-finder.heading")}</div>
	
   <div class="finder-wrapper">
      <div class="search-bar">
         <div class="search-text"><input type="text" id="${args.htmlid}-term" class="search-term" /></div>
         <div class="search-button"><button id="${args.htmlid}-button">${msg("site-finder.search-button")}</button></div>
      </div>

      <div id="${args.htmlid}-sites" class="results"></div>
   </div>
	
</div>