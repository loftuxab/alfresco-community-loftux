<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsDocListTree("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"documentLibrary"}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="treeview filter">
   <h2 id="${args.htmlid}-h2">${msg("header.library")}</h2>
   <div id="${args.htmlid}-treeview" class="tree"></div>
</div>