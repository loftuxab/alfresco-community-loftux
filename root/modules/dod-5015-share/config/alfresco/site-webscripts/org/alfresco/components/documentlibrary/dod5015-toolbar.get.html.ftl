<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsDocListToolbar("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      hideNavBar: ${(preferences.hideNavBar!false)?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar">

   <div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
      <div class="left">
         <div class="new-series hideable DocListTree"><button id="${args.htmlid}-newSeries-button" name="record-series">${msg("button.new-series")}</button></div>
         <div class="separator hideable DocListTree">&nbsp;</div>
         <div class="new-category hideable DocListTree"><button id="${args.htmlid}-newCategory-button" name="record-category">${msg("button.new-category")}</button></div>
         <div class="separator hideable DocListTree">&nbsp;</div>
         <div class="new-folder hideable DocListTree"><button id="${args.htmlid}-newFolder-button" name="record-folder">${msg("button.new-folder")}</button></div>
         <div class="separator hideable DocListTree">&nbsp;</div>
         <div class="file-upload hideable DocListTree"><button id="${args.htmlid}-fileUpload-button" name="fileUpload">${msg("button.upload")}</button></div>
         <div class="separator hideable DocListTree">&nbsp;</div>
         <div class="selected-items hideable DocListTree DocListFilter TagFilter">
            <button class="no-access-check" id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
            <div id="${args.htmlid}-selectedItems-menu" class="yuimenu">
               <div class="bd">
                  <ul>
                     <li><a rel="delete" href="#"><span class="onActionDelete">${msg("menu.selected-items.delete")}</span></a></li>
                     <li><hr /></li>
                     <li><a rel="" href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
                  </ul>
               </div>
            </div>
         </div>
      </div>
      <div class="right">
         <div class="customize" style="display: none;"><button id="${args.htmlid}-customize-button" name="customize">${msg("button.customize")}</button></div>
         <div class="hide-navbar"><button id="${args.htmlid}-hideNavBar-button" name="hideNavBar">${msg("button.navbar.hide")}</button></div>
      </div>
   </div>

   <div id="${args.htmlid}-navBar" class="nav-bar flat-button theme-bg-4">
      <div class="nav-bar-left">
         <div class="folder-up hideable DocListTree"><button class="no-access-check" id="${args.htmlid}-folderUp-button" name="folderUp">${msg("button.up")}</button></div>
         <div class="separator hideable DocListTree">&nbsp;</div>
      </div>
      <div class="nav-bar-right">
         <div id="${args.htmlid}-breadcrumb" class="breadcrumb hideable DocListTree"></div>
      </div>
      <div id="${args.htmlid}-description" class="description hideable DocListFilter TagFilter"></div>
   </div>

</div>
