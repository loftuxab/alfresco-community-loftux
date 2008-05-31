<iframe id="yui-history-iframe" src="${url.context}/yui/assets/blank.html"></iframe> 
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]!""}",
      initialPath: "${page.url.args["path"]!""}"
   });
//]]></script>
<div id="${args.htmlid}-body" class="doclist">
   <div id="${args.htmlid}-headerBar" class="header-bar">
      <span class="new-folder"><button id="${args.htmlid}-newFolder-button" name="newFolder" value="New Folder">New Folder</button></span>
      <span class="separator">|</span>
      <span class="file-upload"><button id="${args.htmlid}-fileUpload-button" name="fileUpload" value="Upload">Upload</button></span>
      <span class="separator">|</span>
      <span>
         <input type="button" id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button" value="Selected items..." />
         <select id="${args.htmlid}-selectedItems-menu" name="doclist-selectedItems-menu">
             <option value="copy">Copy</option>
             <option value="move">Move</option>
             <option value="delete">Delete</option>
         </select>
      </span>
   </div>

   <div id="${args.htmlid}-navBar" class="nav-bar">
      <span><a href="" id="${args.htmlid}-folderUp-button">Up</a></span>
      <span class="separator">|</span>
      <span id="${args.htmlid}-breadcrumb"></span>
   </div>

   <div id="${args.htmlid}-pagerBar" class="yui-gb pager-bar">
      <div class="yui-u first">
         <span>
            <input type="button" id="${args.htmlid}-fileSelect-button" name="doclist-fileSelect-button" value="Select..." />
            <select id="${args.htmlid}-fileSelect-menu" name="doclist-fileSelect-menu">
                <option value="all">All</option>
                <option value="none">None</option>
                <option value="invert">Invert Selection</option>
                <option value="folders">Folders</option>
                <option value="documents">Documents</option>
            </select>
         </span>
      </div>
      <div class="yui-u align-center">
         <span>&lt; 1 &gt;</span>
      </div>
      <div class="yui-u align-right">
         <span><a href="#" id="${args.htmlid}-showFolders-button">Hide Folders</a></span>
         <span class="separator">|</span>
         <span><a href="#" id="${args.htmlid}-detailedView-button">Simple List</a></span>
      </div>
   </div>

   <div id="${args.htmlid}-documents" class="documents"></div>
   
   <div class="hiddenComponents">
      <div id="${args.htmlid}-actions-document">
         <span class="onDeleteDocument"><a href="" class="action-link">Delete document</a></span>
      </div>

      <div id="${args.htmlid}-actions-folder">
         <span class="onDeleteFolder"><a href="" class="action-link">Delete folder</a></span>
      </div>
   </div>

   <div id="${args.htmlid}-createFolder" class="create-folder"></div>
</div>