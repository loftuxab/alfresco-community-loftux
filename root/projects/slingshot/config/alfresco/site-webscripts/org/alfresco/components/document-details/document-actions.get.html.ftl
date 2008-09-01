<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentActions("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"documentLibrary"}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="document-actions">

   <div class="heading">${msg("document-actions.heading")}</div>

   <div class="doclist">
      <#--
         IMPORTANT: Do not add linefeeds between tags on individual actions as this will break DOM parsing code.
         (See note in document-actions.js)
      -->
      <div id="${args.htmlid}-actionSet-document" class="action-set">
         <div class="onActionDownload"><a rel="" id="${args.htmlid}-download-action" href="#" class="simple-link" title="${msg("document-actions.download")}"><span>${msg("document-actions.download")}</span></a></div>
         <div class="onActionAssignWorkflow"><a rel="" href="#" class="action-link" title="${msg("document-actions.assign-workflow")}"><span>${msg("document-actions.assign-workflow")}</span></a></div>
         <div class="onActionCopyTo"><a rel="" href="#" class="action-link" title="${msg("document-actions.copy-to")}"><span>${msg("document-actions.copy-to")}</span></a></div>
         <div class="onActionMoveTo"><a rel="delete" href="#" class="action-link" title="${msg("document-actions.move-to")}"><span>${msg("document-actions.move-to")}</span></a></div>
         <div class="onActionDelete"><a rel="delete" href="#" class="action-link" title="${msg("document-actions.delete")}"><span>${msg("document-actions.delete")}</span></a></div>
      </div>
   </div>

</div>