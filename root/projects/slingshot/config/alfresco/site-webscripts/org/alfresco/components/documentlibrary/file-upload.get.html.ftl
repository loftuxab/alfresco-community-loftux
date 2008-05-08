<script type="text/javascript">//<![CDATA[
   new Alfresco.FileUpload("${args.htmlid}");   
//]]></script>

<!-- HTML Upload Panel -->
<div id="${args.htmlid}-htmldialog-panel">
  <div class="bd">
      <div>Upload with HTML</div>
  </div>
</div>

<!-- FLASH Upload Panel-->
<div id="${args.htmlid}-flashdialog-panel">
  <div class="hd">
    <span id="${args.htmlid}-title-span">[the title shall be provided in show()]</span>
  </div>
  <div class="bd">
     <div class="yui-gd">
         <div class="yui-u first">
            <input id="${args.htmlid}-browse-button" type="button" value="Browse" />
            <!-- this button was discussed but not included in the requirements yet -->
            <div class="hiddenComponents">
               <input id="${args.htmlid}-clear-button" type="button" value="Clear"/>
            </div>
         </div>
         <div class="yui-u">
            <span id="${args.htmlid}-multiSelect-span">NOTE: You can select multiple files by using the CTRL or SHIFT key.</span>
         </div>
      </div>
      <div id="${args.htmlid}-flashuploader-div" style="width:0px;height:0px">
         Unable to load Flash content. You can download the latest version of Flash Player from the
         <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.
      </div>
      <div id="${args.htmlid}-filelist-table"></div>
      <div id="${args.htmlid}-versionSection-div">
         <span>VERSION INFO</span>
         <div class="yui-gd">
            <div class="yui-u first">
               This new version has:
            </div>
            <div class="yui-u">
                <div id="${args.htmlid}-version-buttongroup" class="yui-buttongroup">
                    <input id="${args.htmlid}-minorChanges" type="radio" name="newVersion" value="1.1" checked="checked"/>
                    <input id="${args.htmlid}-majorChanges" type="radio" name="newVersion" value="2.0"/>
                </div>
            </div>
            <div class="yui-u first">
               Comments:
            </div>
            <div class="yui-u">
                <textarea cols="80" rows="4"></textarea>
            </div>
         </div>
      </div>
      <div>
         <input id="${args.htmlid}-upload-button" type="button" value="Start upload" />
         <input id="${args.htmlid}-cancel-button" type="button" value="Cancel" />
      </div>
      <div class="hiddenComponents">
         <div id="${args.htmlid}-fileItemTemplate-div">
            <div class="fileupload-leftcolumn-div">&nbsp;</div>
            <div class="fileupload-actions-div">
               <input class="fileupload-remove-button" type="button" value="Remove" />
            </div>
            <div class="progressBar">
               <span class="progressSuccess">&nbsp;</span>
               <span class="progressInfo">{progressInfo}</span>
               <span class="progressPercentage">{percentage}</span>
            </div>
         </div>
      </div>
  </div>
</div>

