<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListToolbar("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar">

   <div id="${args.htmlid}-headerBar" class="header-bar">
      <div class="new-folder hideable DocListTree"><button id="${args.htmlid}-newFolder-button" name="newFolder">${msg("button.new-folder")}</button></div>
      <div class="separator hideable DocListTree">|</div>
      <div class="file-upload hideable DocListTree"><button id="${args.htmlid}-fileUpload-button" name="fileUpload">${msg("button.upload")}</button></div>
      <div class="separator hideable DocListTree">|</div>
      <div class="selected-items hideable DocListTree DocListFilter DocListTags">
         <button class="no-access-check" id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
         <div id="${args.htmlid}-selectedItems-menu" class="yuimenu">
            <div class="bd">
               <ul>
                  <li><a rel="" href="#"><span class="onActionCopyTo">${msg("menu.selected-items.copy")}</span></a></li>
                  <li><a rel="" href="#"><span class="onActionMoveTo">${msg("menu.selected-items.move")}</span></a></li>
                  <li><a rel="delete" href="#"><span class="onActionDelete">${msg("menu.selected-items.delete")}</span></a></li>
                  <li><a rel="" href="#"><span class="onActionAssignWorkflow">${msg("menu.selected-items.assign-workflow")}</span></a></li>
                  <li><a rel="permissions" href="#"><span class="onActionManagePermissions">${msg("menu.selected-items.manage-permissions")}</span></a></li>
                  <li><hr /></li>
                  <li><a rel="" href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
               </ul>
            </div>
         </div>
      </div>
      <div id="${args.htmlid}-rssFeed" class="rss-feed"></div>
   </div>

   <div id="${args.htmlid}-navBar" class="nav-bar">
      <div class="folder-up hideable DocListTree"><button id="${args.htmlid}-folderUp-button" name="folderUp">${msg("button.up")}</button></div>
      <div class="separator hideable DocListTree">|</div>
      <div id="${args.htmlid}-breadcrumb" class="breadcrumb hideable DocListTree"></div>
      <div id="${args.htmlid}-description" class="description hideable DocListFilter DocListTags"></div>
   </div>

</div>
