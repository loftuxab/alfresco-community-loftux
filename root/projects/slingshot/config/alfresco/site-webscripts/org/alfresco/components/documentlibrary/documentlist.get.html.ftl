<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/yui/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]!""}",
      initialPath: "${page.url.args["path"]!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="doclist">
   <div id="${args.htmlid}-headerBar" class="header-bar">
      <div class="new-folder inline"><button id="${args.htmlid}-newFolder-button" name="newFolder">${msg("button.new-folder")}</button></div>
      <span class="separator">|</span>
      <div class="file-upload inline"><button id="${args.htmlid}-fileUpload-button" name="fileUpload">${msg("button.upload")}</button></div>
      <span class="separator">|</span>
      <div class="inline">
         <button id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
         <select id="${args.htmlid}-selectedItems-menu" name="doclist-selectedItems-menu">
             <option value="copy">${msg("menu.selected-items.copy")}</option>
             <option value="move">${msg("menu.selected-items.move")}</option>
             <option value="delete">${msg("menu.selected-items.delete")}</option>
         </select>
      </div>
   </div>

   <div id="${args.htmlid}-navBar" class="nav-bar">
      <div class="inline"><button id="${args.htmlid}-folderUp-button" name="folderUp">${msg("button.up")}</button></div>
      <span class="separator">|</span>
      <span id="${args.htmlid}-breadcrumb"></span>
   </div>

   <div id="${args.htmlid}-pagerBar" class="yui-gb pager-bar">
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
      <div class="yui-u align-center">
         <span>&lt; 1 &gt;</span>
      </div>
      <div class="yui-u align-right">
         <span><a href="#" id="${args.htmlid}-showFolders-button"></a></span>
         <span class="separator">|</span>
         <span><a href="#" id="${args.htmlid}-detailedView-button"></a></span>
      </div>
   </div>

   <div id="${args.htmlid}-documents" class="documents"></div>
   
   <div id="${args.htmlid}-actionsBag" class="hiddenComponents">
      <div id="${args.htmlid}-actions-document">
         <span class="onDeleteAsset"><a href="#" class="action-link">${msg("actions.document.delete")}</a></span>
      </div>

      <div id="${args.htmlid}-actions-folder">
         <span class="onDeleteAsset"><a href="#" class="action-link">${msg("actions.folder.delete")}</a></span>
      </div>
   </div>

</div>