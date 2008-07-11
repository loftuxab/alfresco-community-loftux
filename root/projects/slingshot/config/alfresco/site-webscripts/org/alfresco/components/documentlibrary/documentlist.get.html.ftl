<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/yui/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"documentLibrary"}",
      initialPath: "${page.url.args["path"]!""}",
      initialFilter:
      {
         filterId: "path",
         filterOwner: "Alfresco.DocListTree"
      }
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="doclist">
   <div id="${args.htmlid}-doclistBar" class="yui-g doclist-bar">
      <div class="yui-u first">
         <div class="inline">
            <button id="${args.htmlid}-fileSelect-button" name="doclist-fileSelect-button">${msg("menu.select")}</button>
            <select id="${args.htmlid}-fileSelect-menu" name="doclist-fileSelect-menu">
                <option value="all">${msg("menu.select.all")}</option>
                <option value="none">${msg("menu.select.none")}</option>
                <option value="invert">${msg("menu.select.invert")}</option>
                <option value="folders">${msg("menu.select.folders")}</option>
                <option value="documents">${msg("menu.select.documents")}</option>
            </select>
         </div>
      </div>
      <div class="yui-u align-right">
         <span><a href="#" id="${args.htmlid}-showFolders-button"></a></span>
         <span class="separator">|</span>
         <span><a href="#" id="${args.htmlid}-detailedView-button"></a></span>
      </div>
   </div>

   <div id="${args.htmlid}-documents" class="documents"></div>
   
   <div style="display:none">
      <div id="${args.htmlid}-actionSet-empty" class="action-set">
      </div>

      <div id="${args.htmlid}-actionSet-document" class="action-set">
         <div class="onActionDownload"><a href="{downloadUrl}" class="simple-link" title="${msg("actions.document.download")}"><span>${msg("actions.document.download")}</span></a></div>
         <div class="onActionEditOffline"><a href="#" class="action-link" title="${msg("actions.document.edit-offline")}"><span>${msg("actions.document.edit-offline")}</span></a></div>
         <div class="onActionMoveTo"><a href="#" class="action-link" title="${msg("actions.document.move-to")}"><span>${msg("actions.document.move-to")}</span></a></div>
         <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
         <div class="more-actions hidden">
            <div class="onActionCopyTo"><a href="#" class="action-link" title="${msg("actions.document.copy-to")}"><span>${msg("actions.document.copy-to")}</span></a></div>
            <div class="onActionDelete"><a href="#" class="action-link" title="${msg("actions.document.delete")}"><span>${msg("actions.document.delete")}</span></a></div>
         </div>
      </div>

      <div id="${args.htmlid}-actionSet-locked" class="action-set">
         <div class="onActionDownload"><a href="{downloadUrl}" class="simple-link" title="${msg("actions.document.download")}"><span>${msg("actions.document.download")}</span></a></div>
         <div class="onActionRequestUnlock"><a href="#" class="action-link" title="[Request Unlock]"><span>[Request Unlock]</span></a></div>
      </div>

      <div id="${args.htmlid}-actionSet-lockOwner" class="action-set">
         <div class="onActionDownload"><a href="{downloadUrl}" class="simple-link" title="${msg("actions.document.download-original")}"><span>${msg("actions.document.download-original")}</span></a></div>
         <div class="onActionUnlock"><a href="#" class="action-link" title="[Unlock]"><span>[Unlock]</span></a></div>
      </div>

      <div id="${args.htmlid}-actionSet-workingCopyOwner" class="action-set">
         <div class="onActionUploadNewVersion"><a href="#" class="action-link" title="${msg("actions.document.upload-new-version")}"><span>${msg("actions.document.upload-new-version")}</span></a></div>
         <div class="onActionDownload"><a href="{downloadUrl}" class="simple-link" title="${msg("actions.document.download-again")}"><span>${msg("actions.document.download-again")}</span></a></div>
         <div class="onActionCancelEditing"><a href="#" class="action-link" title="${msg("actions.document.cancel-editing")}"><span>${msg("actions.document.cancel-editing")}</span></a></div>
      </div>

      <div id="${args.htmlid}-actionSet-folder" class="action-set">
         <div class="onActionMoveTo"><a href="#" class="action-link" title="${msg("actions.folder.move-to")}"><span>${msg("actions.folder.move-to")}</span></a></div>
         <div class="onActionCopyTo"><a href="#" class="action-link" title="${msg("actions.folder.copy-to")}"><span>${msg("actions.folder.copy-to")}</span></a></div>
         <div class="onActionDelete"><a href="#" class="action-link" title="${msg("actions.folder.delete")}"><span>${msg("actions.folder.delete")}</span></a></div>
      </div>
   </div>

</div>