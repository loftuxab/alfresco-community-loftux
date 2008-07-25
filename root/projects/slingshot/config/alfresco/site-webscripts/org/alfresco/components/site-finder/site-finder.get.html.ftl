<script type="text/javascript">//<![CDATA[
   new Alfresco.SiteFinder("${args.htmlid}").setOptions(
   {
      showPrivateSites: false
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="site-finder">
	
	<div class="heading">${msg("site-finder.search")}</div>
	
	<div class="search-controls">
	   <div>
	   <div class="search-term"><input id ="${args.htmlid}-term" type="text" /></div>
	   <div class="search-button"><input id="${args.htmlid}-button" type="button" value="Search" /></div>
	   </div>
	</div>
	
	<#-- this div contains the site search results -->
	<div id="${args.htmlid}-sites" class="site-list"></div>
	
</div>