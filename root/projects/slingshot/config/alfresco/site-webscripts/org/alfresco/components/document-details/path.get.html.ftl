<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentPath("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"documentLibrary"}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="path-nav">
   <span class="heading">${msg("path.location")}:</span>
   <span class="path-link"><a href="${url.context}/page/site/${page.url.templateArgs.site}/documentlibrary">${msg("path.documents")}</a></span>
   <span id="${args.htmlid}-path"></span>
</div>