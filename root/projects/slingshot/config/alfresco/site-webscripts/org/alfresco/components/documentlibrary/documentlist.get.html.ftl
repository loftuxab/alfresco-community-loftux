<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      initialPath: "${page.url.args["path"]!""}"
   });
//]]></script>
<div id="${args.htmlid}-headerBar">
   <div class="yui-gb">
      <div class="yui-u first">
         <div class="doclist-fileSelector">
            <input type="button" id="${args.htmlid}-fileSelect-button" name="doclist-fileSelect-button" value="Select..." />
            <select id="${args.htmlid}-fileSelect-menu" name="doclist-fileSelect-menu">
                <option value="one">One</option>
                <option value="two">Two</option>
                <option value="three">Three</option>                
            </select>
         </div>
      </div>
      <div class="yui-u">
         <div id="${args.htmlid}-pager" class="doclist-pager"><a href="#">&lt;</a> <a href="#">1</a> <a href="#">2</a> <a href="#">3</a> <a href="#">4</a> <a href="#">&gt;</a></div>
      </div>
      <div class="yui-u">
         <div id="${args.htmlid}-viewButtons" class="doclist-viewButtons"><span id="${args.htmlid}-fileUpload-buttonWrap" class="hiddenComponents"><a href="#" id="${args.htmlid}-fileUpload-button">Upload</a> | </span><a href="#" id="${args.htmlid}-showFolders-button">Hide Folders</a> | <a href="#">Detail List</a> | <a href="#">Simple List</a></div>
      </div>
   </div>
   <p>&nbsp;</p>
   <div id="${args.htmlid}-documents" class="doclist-documents"></div>
</div>

<div id="${args.htmlid}-createSite"></div>