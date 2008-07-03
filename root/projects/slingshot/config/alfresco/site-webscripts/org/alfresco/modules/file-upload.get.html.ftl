<div id="${args.htmlid}-dialog" class="file-upload">
   <div class="hd">
      <span id="${args.htmlid}-title-span"></span>
   </div>
   <div class="bd">
      <p>
         <input id="${args.htmlid}-browse-button" type="button" value="${msg("button.browse")}" />
         <span id="${args.htmlid}-multiSelect-span">${msg("label.browseTip")}</span>
      </p>
      <p>
         <span id="${args.htmlid}-status-span"></span>
      </p>
      <div id="${args.htmlid}-flashuploader-div" style="width:0px;height:0px">
         ${msg("label.noFlash")}
      </div>

      <div id="${args.htmlid}-filelist-table" class="fileUpload-filelist-table"></div>

      <div id="${args.htmlid}-versionSection-div">
         <div class="yui-g section-title">
            <h2>${msg("section.version")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">
               ${msg("label.version")}
            </div>
            <div class="yui-u">
               Minor changes: <input id="${args.htmlid}-minorChanges" type="radio" name="newVersion" value="1.1" checked="checked"/><br>
               Major changes: <input id="${args.htmlid}-majorChanges" type="radio" name="newVersion" value="2.0"/>
            </div>
            <div class="yui-u first">
               Comments:
            </div>
            <div class="yui-u">
               <textarea rows="4"></textarea>
            </div>
         </div>
      </div>

      <p>
         <input id="${args.htmlid}-upload-button" type="button" value="${msg("button.upload")}" />
         <input id="${args.htmlid}-cancelOk-button" type="button" value="${msg("button.cancel")}" />
      </p>

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
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.FileUpload");
//]]></script>
