<script type="text/javascript">//<![CDATA[
   new Alfresco.LinkTags("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"links"}"
   });
//]]></script>
<div id="${args.htmlid}-body" class="tags links-filter">
	<h2>${msg("header.title")}</h2>
	<ul class="filterLink" id="${args.htmlid}-tags"><li>&nbsp;</li></ul>
</div>
