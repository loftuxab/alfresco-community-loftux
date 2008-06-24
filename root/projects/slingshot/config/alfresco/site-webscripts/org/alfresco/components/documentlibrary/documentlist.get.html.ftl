<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/yui/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]!""}",
      containerId: "${args.container!"documentLibrary"}",
      initialPath: "${page.url.args["path"]!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="doclist">
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