<script type="text/javascript">//<![CDATA[
   new Alfresco.LinksView("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "links",
      linkId: "${page.url.args['linkId']}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="linksview-header">
	<div class="navbar">
		<span class="back-link">
			<a href="${url.context}/page/site/${page.url.templateArgs.site}/links">
				${msg("header.back")}
			</a>
		</span>
	</div>
</div>

<div id="${args.htmlid}-link">
   <div id="${args.htmlid}-link-view-div">
   </div>
</div>
