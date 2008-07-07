<script type="text/javascript">//<![CDATA[
	new Alfresco.WikiFilter("${args.htmlid}").setSelected(
		"${page.url.args["filter"]!"all"}"
	);
//]]></script>
<div id="${args.htmlid}-body" class="filter wiki-filter">
   <h2>${msg("header.pages")}</h2>
   <ul class="filterLink">
      <li id="${args.htmlid}-recentlyModified"><span class="recentlyModified"><a href="?filter=recentlyModified" class="filter-link">${msg("link.recentlyModified")}</a></span></li>
      <li id="${args.htmlid}-all"><span class="all"><a href="?filter=all" class="filter-link">${msg("link.all")}</a></span></li>
      <li id="${args.htmlid}-recentlyAdded"><span class="recentlyAdded"><a href="?filter=recentlyAdded" class="filter-link">${msg("link.recentlyAdded")}</a></span></li>
      <li id="${args.htmlid}-myPages"><span class="editingMe"><a href="?filter=myPages" class="filter-link">${msg("link.myPages")}</a></span></li>
   </ul>
</div>