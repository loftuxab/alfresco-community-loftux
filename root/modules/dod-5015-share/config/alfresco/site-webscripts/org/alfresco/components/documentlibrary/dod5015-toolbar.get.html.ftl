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
         <div class="hideable toolbar-hidden DocListTree">
            <div class="new-series"><button id="${args.htmlid}-newSeries-button" name="dod:recordSeries">${msg("button.new-series")}</button></div>
            <div class="separator">&nbsp;</div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="new-category"><button id="${args.htmlid}-newCategory-button" name="dod:recordCategory">${msg("button.new-category")}</button></div>
            <div class="separator">&nbsp;</div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="new-folder"><button id="${args.htmlid}-newFolder-button" name="rma:recordFolder">${msg("button.new-folder")}</button></div>
            <div class="separator">&nbsp;</div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="file-upload"><button id="${args.htmlid}-fileUpload-button" name="fileUpload">${msg("button.upload")}</button></div>
            <div class="separator">&nbsp;</div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="import"><button id="${args.htmlid}-import-button" name="import">${msg("button.import")}</button></div>
            <div class="separator">&nbsp;</div>
         </div>
         <div class="selected-items">
            <button class="no-access-check" id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
            <div id="${args.htmlid}-selectedItems-menu" class="yuimenu">
               <div class="bd">
                  <ul>
                  <#list actionSet as action>
                     <li><a type="${action.asset!""}" rel="${action.permission!""}" href="${action.href}"><span class="${action.id}">${msg(action.label)}</span></a></li>
                  </#list>
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
      <div class="hideable toolbar-hidden DocListTree">
         <div class="folder-up"><button class="no-access-check" id="${args.htmlid}-folderUp-button" name="folderUp">${msg("button.up")}</button></div>
         <div class="separator">&nbsp;</div>
      </div>
      <div class="hideable toolbar-hidden DocListFilePlan_holds">
         <div class="folder-up"><button class="no-access-check" id="${args.htmlid}-holdsFolderUp-button" name="holdsFolderUp">${msg("button.up")}</button></div>
         <div class="separator">&nbsp;</div>
      </div>
      <div id="${args.htmlid}-breadcrumb" class="breadcrumb hideable toolbar-hidden DocListTree"></div>
      <div id="${args.htmlid}-description" class="description hideable toolbar-hidden DocListFilter TagFilter DocListSavedSearch DocListFilePlan"></div>
   </div>

</div>
