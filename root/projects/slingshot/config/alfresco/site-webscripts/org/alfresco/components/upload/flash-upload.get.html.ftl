<div id="${args.htmlid}-dialog" class="flash-upload hidden">
   <div class="hd">
      <span id="${args.htmlid}-title-span"></span>
   </div>
   <div class="bd">
      <div class="browse-wrapper">
         <div class="center">
            <div id="${args.htmlid}-flashuploader-div" class="browse">${msg("label.noFlash")}</div>
            <div class="label">${msg("label.browse")}</div>
         </div>
      </div>
      <div class="tip-wrapper">
         <span id="${args.htmlid}-multiUploadTip-span">${msg("label.multiUploadTip")}</span>
         <span id="${args.htmlid}-singleUpdateTip-span">${msg("label.singleUpdateTip")}</span>
      </div>

      <div id="${args.htmlid}-filelist-table" class="fileUpload-filelist-table"></div>

      <div class="status-wrapper">
         <span id="${args.htmlid}-status-span" class="status"></span>
      </div>

      <div id="${args.htmlid}-versionSection-div"> 
         <div class="yui-g">
            <h2>${msg("section.version")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">
               <label for="${args.htmlid}-minorVersion-radioButton">${msg("label.version")}</label>
            </div>
            <div class="yui-u">
               <input id="${args.htmlid}-minorVersion-radioButton" type="radio" name="majorVersion" checked="checked" /> ${msg("label.minorVersion")}
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">&nbsp;
            </div>
            <div class="yui-u">
               <input id="${args.htmlid}-majorVersion-radioButton" type="radio" name="majorVersion" /> ${msg("label.majorVersion")}
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">
               <label for="${args.htmlid}-description-textarea">${msg("label.comments")}</label>
            </div>
            <div class="yui-u">
               <textarea id="${args.htmlid}-description-textarea" name="description" rows="4"></textarea>
            </div>
         </div>
      </div>

      <!-- Templates for a file row -->
      <div style="display:none">
         <div id="${args.htmlid}-left-div" class="fileupload-left-div">
            <span class="fileupload-percentage-span hidden">&nbsp;</span>
            <select class="fileupload-contentType-select <#if (contentTypes?size == 1)>hidden</#if>">
               <#if (contentTypes?size > 0)>
                  <#list contentTypes as contentType>
                     <option value="${contentType.id}">${contentType.value}</option>
                  </#list>
               </#if>
            </select>
         </div>
         <div id="${args.htmlid}-center-div" class="fileupload-center-div">
            <span class="fileupload-progressSuccess-span">&nbsp;</span>
            <img src="${url.context}/components/images/generic-file-32.png" class="fileupload-docImage-img"/>
            <span class="fileupload-progressInfo-span"></span>
         </div>
         <div id="${args.htmlid}-right-div" class="fileupload-right-div">
            <span class="fileupload-fileButton-span">
               <button class="fileupload-file-button" value="Remove">${msg("button.remove")}</button>
            </span>
         </div>
      </div>
         <div class="bdft">
            <input id="${args.htmlid}-upload-button" type="button" value="${msg("button.upload")}" />
            <input id="${args.htmlid}-cancelOk-button" type="button" value="${msg("button.cancel")}" />
         </div>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.FlashUpload("${args.htmlid}").setMessages(
   ${messages}
);
//]]></script>
