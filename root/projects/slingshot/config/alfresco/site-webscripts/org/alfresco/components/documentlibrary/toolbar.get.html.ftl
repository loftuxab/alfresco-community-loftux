<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListToolbar("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar doclib-toolbar">

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

</div>
