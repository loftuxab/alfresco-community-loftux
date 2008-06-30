<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/yui/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]!""}",
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
   
   <div class="hiddenComponents">
      <div id="${args.htmlid}-actionSet-empty">
      </div>

      <div id="${args.htmlid}-actionSet-document">
         <span class="onActionEditOffline"><a href="#" class="action-link">${msg("actions.document.edit-offline")}</a></span>
         <span class="onActionCopyTo"><a href="#" class="action-link">${msg("actions.document.copy-to")}</a></span>
         <span class="onActionMoveTo"><a href="#" class="action-link">${msg("actions.document.move-to")}</a></span>
         <span class="onActionDeleteAsset"><a href="#" class="action-link">${msg("actions.document.delete")}</a></span>
      </div>

      <div id="${args.htmlid}-actionSet-locked">
         <span class="onActionUnlock"><a href="#" class="action-link">[Request Unlock]</a></span>
      </div>

      <div id="${args.htmlid}-actionSet-lockOwner">
         <span class="onActionUnlock"><a href="#" class="action-link">[Unlock]</a></span>
      </div>

      <div id="${args.htmlid}-actionSet-workingCopyOwner">
         <span class="onActionUploadNewVersion"><a href="#" class="action-link">${msg("actions.document.upload-new-version")}</a></span>
         <span class="onActionCancelEditing"><a href="#" class="action-link">${msg("actions.document.cancel-editing")}</a></span>
      </div>

      <div id="${args.htmlid}-actionSet-folder">
         <span class="onActionCopyTo"><a href="#" class="action-link">${msg("actions.folder.copy-to")}</a></span>
         <span class="onActionMoveTo"><a href="#" class="action-link">${msg("actions.folder.move-to")}</a></span>
         <span class="onActionDeleteAsset"><a href="#" class="action-link">${msg("actions.folder.delete")}</a></span>
      </div>
   </div>

</div>