<script type="text/javascript">//<![CDATA[
   new Alfresco.FileUpload("${args.htmlid}");   
//]]></script>

<!-- HTML Upload Panel -->
<div class="fileupload-htmldialog-panel">
  <div class="bd">
      <div class="panel-title">Upload with HTML</div>
  </div>
</div>

<!-- FLASH Upload Panel-->
<div class="fileupload-flashdialog-panel">
  <div class="hd">
    <span class="fileupload-title-text">[the title shall be provided in show()]</span>
  </div>
  <div class="bd">
     <div class="yui-gd">
         <div class="yui-u first">
            <input class="fileupload-browse-button" type="button" value="Browse" />
            <!-- this button was discussed but not included in the requirements yet -->
            <div class="hiddenComponents">
               <input class="fileupload-clear-button" type="button" value="Clear"/>
            </div>
         </div>
         <div class="yui-u">
            <span class="fileupload-multiSelect-text">NOTE: You can select multiple files by using the CTRL or SHIFT key.</span>
         </div>
      </div>
      <div id="fileupload-flashuploader-div" style="width:0px;height:0px">
         Unable to load Flash content. You can download the latest version of Flash Player from the
         <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.
      </div>
      <div class="fileupload-filelist-table"></div>
      <div class="fileupload-versionSection-div">
         <span>VERSION INFO</span>
         <div class="yui-gd">
            <div class="yui-u first">
               This new version has:
            </div>
            <div class="yui-u">
                <div class="fileupload-versiongroup-div" class="yui-buttongroup">
                    <input class="fileupload-minorChanges" type="radio" name="newVersion" value="1.1" checked>
                    <input class="fileupload-majorChanges" type="radio" name="newVersion" value="2.0">
                </div>
            </div>
            <div class="yui-u first">
               Comments:
            </div>
            <div class="yui-u">
                <textarea></textarea>               
            </div>
         </div>
      </div>
      <div>
         <input class="fileupload-upload-button" type="button" value="Start upload" />
         <input class="fileupload-cancel-button" type="button" value="Cancel" />
      </div>
      <div class="hiddenComponents">
         <div class="fileupload-fileItemTemplate-div">
            <div class="fileupload-leftcolumn-div">&nbsp;</div>
            <div class="fileupload-actions-div">
               <input class="fileupload-remove-button" type="button" value="Remove" />
            </div>
            <div class="progressBar">
               <span class="progressSuccess">&nbsp;</span>
               <span class="progressInfo">[filename and size]</span>
               <span class="progressPercentage">[percentage]</span>
            </div>

         </div>
      </div>
  </div>
</div>

