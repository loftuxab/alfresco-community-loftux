<script type="text/javascript">//<![CDATA[
	new Alfresco.WikiFilter("${args.htmlid}").setSelected(
		"${page.url.args["filter"]!"main"}"
	);
//]]></script>
<div id="${args.htmlid}-body" class="filter wiki-filter">
   <h2>${msg("header.pages")}</h2>
   <ul class="filterLink">
      <li id="${args.htmlid}-main"><span class="recentlyModified"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/wiki-page?filter=main" class="filter-link">${msg("link.mainPage")}</a></span></li>   
      <li id="${args.htmlid}-recentlyModified"><span class="recentlyModified"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/wiki?filter=recentlyModified" class="filter-link">${msg("link.recentlyModified")}</a></span></li>
      <li id="${args.htmlid}-all"><span class="all"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/wiki?filter=all" class="filter-link">${msg("link.all")}</a></span></li>
      <li id="${args.htmlid}-recentlyAdded"><span class="recentlyAdded"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/wiki?filter=recentlyAdded" class="filter-link">${msg("link.recentlyAdded")}</a></span></li>
      <li id="${args.htmlid}-myPages"><span class="editingMe"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/wiki?filter=myPages" class="filter-link">${msg("link.myPages")}</a></span></li>
   </ul>
</div>