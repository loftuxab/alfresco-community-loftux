<script type="text/javascript">//<![CDATA[
   new Alfresco.EditMetadataMgr("${args.htmlid}").setOptions(
   {
      nodeRef: "${page.url.args.nodeRef!}",
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="edit-metadata-mgr">
   <div class="heading">${msg("edit-metadata-mgr.heading")}</div>
</div>