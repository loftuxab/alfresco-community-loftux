<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListTags("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      container: "${args.container!""}"
   });
//]]></script>
<div id="${args.htmlid}-body" class="tags doclib-filter">
	<h2>${msg("header.title")}</h2>
	<ul class="filterLink" id="${args.htmlid}-tags"></ul>
</div>