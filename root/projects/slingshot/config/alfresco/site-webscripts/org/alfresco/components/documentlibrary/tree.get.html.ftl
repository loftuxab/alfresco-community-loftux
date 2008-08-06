<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListTree("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"documentLibrary"}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="treeview doclib-filter">
   <h2>${msg("header.library")}</h2>
   <div id="${args.htmlid}-treeview" class="tree"></div>
</div>