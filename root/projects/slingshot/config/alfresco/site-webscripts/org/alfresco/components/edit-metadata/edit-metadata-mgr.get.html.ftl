<script type="text/javascript">//<![CDATA[
   new Alfresco.EditMetadataMgr("${args.htmlid}").setOptions(
   {
      nodeRef: "${nodeRef!}",
      backLinkNodeRef: "${backLinkNodeRef!""}",
      nodeType: "${nodeType}",
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="edit-metadata-mgr">
   <div class="heading">${msg("edit-metadata-mgr.heading")}</div>
</div>