<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      initialPath: "${page.url.args["path"]!""}"
   });
//]]></script>
<div class="doclist-headerBar">
   <div class="yui-gb">
      <div class="yui-u first">
         <div class="doclist-fileSelector">
            <input type="button" class="doclist-fileSelect-button" name="doclist-fileSelect-button" value="Select..." />
            <select class="doclist-fileSelect-menu" name="doclist-fileSelect-menu">
                <option value="one">One</option>
                <option value="two">Two</option>
                <option value="three">Three</option>                
            </select>
         </div>
      </div>
      <div class="yui-u">
         <div class="doclist-pager"><a href="#">&lt;</a> <a href="#">1</a> <a href="#">2</a> <a href="#">3</a> <a href="#">4</a> <a href="#">&gt;</a></div>
      </div>
      <div class="yui-u">
         <div class="doclist-viewButtons"><span class="doclist-fileUpload-buttonWrap hiddenComponents"><a href="#" class="doclist-fileUpload-button">Upload</a> | </span><a href="#" class="doclist-showFolders-button">Hide Folders</a> | <a href="#">Detail List</a> | <a href="#">Simple List</a></div>
      </div>
   </div>
   <p>&nbsp;</p>
   <div class="yui-g">
      <div class="yui-u first">
         <div class="doclist-documents"></div>
      </div>
   </div>
</div>
