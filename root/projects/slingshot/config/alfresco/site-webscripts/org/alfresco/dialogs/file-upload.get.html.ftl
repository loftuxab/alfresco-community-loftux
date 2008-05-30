<div class="hd">
   <span id="${args.htmlid}-title-span">[the title shall be provided in show()]</span>
</div>
<div class="bd">
   <p>
      <input id="${args.htmlid}-browse-button" type="button" value="Browse" />
      <span id="${args.htmlid}-multiSelect-span">NOTE: You can select multiple files by using the CTRL or SHIFT key.</span>
   </p>
   <p>
      <span id="${args.htmlid}-status-span">HEJ</span>
   </p>
   <div id="${args.htmlid}-flashuploader-div" style="width:0px;height:0px">
      Unable to load Flash content. You can download the latest version of Flash Player from the
      <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.
   </div>

   <div id="${args.htmlid}-filelist-table" class="fileUpload-filelist-table"></div>

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
            <textarea cols="40" rows="4"></textarea>
         </div>
      </div>
   </div>

   <p>
      <input id="${args.htmlid}-startStop-button" type="button" value="Start upload" />
      <input id="${args.htmlid}-cancelOk-button" type="button" value="Cancel" />
   </p>

   <div class="hiddenComponents">
      <div id="${args.htmlid}-fileItemTemplate-div">
         <div class="fileupload-percentage-div">
            <span class="fileupload-percentage-span">&nbsp;</span>
         </div>
         <div class="fileupload-fileButton-div">
            <span class="fileupload-fileButton-span">
               <button class="fileupload-file-button" value="Remove">Remove</button>
            </span>
         </div>
         <div class="fileupload-progressBar-div">
            <span class="fileupload-progressSuccess-span">&nbsp;</span>
            <img src="${url.context}/images/filetypes32/_default.gif" class="fileupload-docImage-img"/>
            <span class="fileupload-progressInfo-span">{progressInfo}</span>
            <div class="fileupload-progressContentType-div">
               <select class="fileupload-contentType-menu <#if (contentTypes?size == 1)>hiddenComponents</#if>">
                  <#if (contentTypes?size > 0)>
                     <#list contentTypes as contentType>
                        <option value="${contentType.id}">${contentType.value}</option>
                     </#list>
                  </#if>
               </select>
            </div>
         </div>
      </div>
   </div>
</div>