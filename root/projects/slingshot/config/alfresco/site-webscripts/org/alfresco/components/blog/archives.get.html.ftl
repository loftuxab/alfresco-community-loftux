<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostListArchive("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      container: "${args.container!""}"
   });
//]]></script>
<div id="${args.htmlid}-body" class="archive blog-filter">
	<h2>${msg("header.title")}</h2>
	<ul class="filterLink" id="${args.htmlid}-archive"><li>&nbsp;</li></ul>
</div>
