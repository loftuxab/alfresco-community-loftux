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
   <div id="${args.htmlid}-headerBar" class="yui-gb">
      <div class="yui-u first">
         <div class="doclist-fileSelector">
            <input type="button" id="${args.htmlid}-fileSelect-button" name="doclist-fileSelect-button" value="Select..." />
            <select id="${args.htmlid}-fileSelect-menu" name="doclist-fileSelect-menu">
                <option value="all">All</option>
                <option value="none">None</option>
                <option value="invert">Invert Selection</option>                
                <option value="documents">Documents</option>                
                <option value="folders">Folders</option>                
            </select>
         </div>
      </div>
      <div class="yui-u">
         <div id="${args.htmlid}-pager" class="doclist-pager"><a href="#">&lt;</a> <a href="#">1</a> <a href="#">2</a> <a href="#">3</a> <a href="#">4</a> <a href="#">&gt;</a></div>
      </div>
      <div class="yui-u">
         <div id="${args.htmlid}-viewButtons" class="doclist-viewButtons">
            <span><a href="#" id="${args.htmlid}-fileUpload-button">Upload</a> | </span>
            <span><a href="#" id="${args.htmlid}-showFolders-button">Hide Folders</a> | </span>
            <span><a href="#">Detail List</a> | </span>
            <span><a href="#">Simple List</a></span>
         </div>
      </div>
   </div>

   <p>&nbsp;</p>

   <div id="${args.htmlid}-navBar">
      <span><a href="" id="${args.htmlid}-folderUp-button">Up</a></span>
   </div>

   <p>&nbsp;</p>

   <div id="${args.htmlid}-documents" class="doclist-documents"></div>

   <div id="${args.htmlid}-createSite"></div>
</div>