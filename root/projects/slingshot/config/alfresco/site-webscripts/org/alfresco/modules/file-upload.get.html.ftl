<div id="${args.htmlid}-dialog" class="file-upload">
   <div class="hd">
      <span id="${args.htmlid}-title-span"></span>
   </div>
   <div class="bd">
      <p>
         <input id="${args.htmlid}-browse-button" type="button" value="${msg("button.browse")}" />
         <span id="${args.htmlid}-multiUploadTip-span">${msg("label.multiUploadTip")}</span>
         <span id="${args.htmlid}-singleUpdateTip-span">${msg("label.singleUpdateTip")}</span>
      </p>
      <p>
         <span id="${args.htmlid}-status-span"></span>
      </p>
      <#-- Increase width and height and remove display:none to see flash debug info -->
      <div id="${args.htmlid}-flashuploader-div">
         <p>${msg("label.noFlash")}</p>
      </div>

      <div id="${args.htmlid}-filelist-table" class="fileUpload-filelist-table"></div>

      <div id="${args.htmlid}-versionSection-div">
         <div class="yui-g">
            <h2>${msg("section.version")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">
               ${msg("label.version")}
            </div>
            <div class="yui-u">
               <input id="${args.htmlid}-minorVersion-radioButton" type="radio" name="majorVersion" checked="true"/> ${msg("label.minorVersion")}
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">&nbsp;
            </div>
            <div class="yui-u">
               <input id="${args.htmlid}-majorVersion-radioButton" type="radio" name="majorVersion"/> ${msg("label.majorVersion")}
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">
               ${msg("label.comments")}
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
            <select class="fileupload-contentType-menu <#if (contentTypes?size == 1)>hidden</#if>">
               <#if (contentTypes?size > 0)>
                  <#list contentTypes as contentType>
                     <option value="${contentType.id}">${contentType.value}</option>
                  </#list>
               </#if>
            </select>
         </div>
         <div id="${args.htmlid}-center-div" class="fileupload-center-div">
            <span class="fileupload-progressSuccess-span">&nbsp;</span>
            <img src="${url.context}/images/filetypes32/_default.gif" class="fileupload-docImage-img"/>
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
Alfresco.util.addMessages(${messages}, "Alfresco.module.FileUpload");
//]]></script>
