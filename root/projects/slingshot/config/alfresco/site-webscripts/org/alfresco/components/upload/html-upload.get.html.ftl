<div id="${args.htmlid}-dialog" class="html-upload hidden">
   <div class="hd">
      <span id="${args.htmlid}-title-span"></span>
   </div>
   <div class="bd">
      <form id="${args.htmlid}-htmlupload-form"
            method="post" enctype="multipart/form-data" accept-charset="utf-8"
            action="${url.context}/proxy/alfresco/api/upload.html">
         <fieldset>
         <input type="hidden" id="${args.htmlid}-siteId-hidden" name="siteId" value=""/>
         <input type="hidden" id="${args.htmlid}-containerId-hidden" name="containerId" value=""/>
         <input type="hidden" id="${args.htmlid}-username-hidden" name="username" value=""/>
         <input type="hidden" id="${args.htmlid}-updateNodeRef-hidden" name="updateNodeRef" value=""/>
         <input type="hidden" id="${args.htmlid}-uploadDirectory-hidden" name="uploadDirectory" value=""/>
         <input type="hidden" id="${args.htmlid}-overwrite-hidden" name="overwrite" value=""/>
         <input type="hidden" id="${args.htmlid}-thumbnails-hidden" name="thumbnails" value=""/>
         <input type="hidden" id="${args.htmlid}-successCallback-hidden" name="successCallback" value=""/>
         <input type="hidden" id="${args.htmlid}-successScope-hidden" name="successScope" value=""/>
         <input type="hidden" id="${args.htmlid}-failureCallback-hidden" name="failureCallback" value=""/>
         <input type="hidden" id="${args.htmlid}-failureScope-hidden" name="failureScope" value=""/>

         <p>
            <span id="${args.htmlid}-singleUploadTip-span">${msg("label.singleUploadTip")}</span>
            <span id="${args.htmlid}-singleUpdateTip-span">${msg("label.singleUpdateTip")}</span>
         </p>

         <div>
            <div class="yui-g">
               <h2>${msg("section.file")}</h2>
            </div>
            <div class="yui-gd <#if (contentTypes?size == 1)>hidden</#if>">
               <div class="yui-u first">
                  <label for="${args.htmlid}-contentType-select">${msg("label.contentType")}</label>
               </div>
               <div class="yui-u">
                  <select id="${args.htmlid}-contentType-select" name="contentType" tabindex="0">
                     <#if (contentTypes?size > 0)>
                        <#list contentTypes as contentType>
                           <option value="${contentType.id}">${msg(contentType.value)}</option>
                        </#list>
                     </#if>
                  </select>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${args.htmlid}-filedata-file">${msg("label.file")}</label>
               </div>
               <div class="yui-u">
                  <input type="file" id="${args.htmlid}-filedata-file" name="filedata" tabindex="0" />
               </div>
            </div>
         </div>

         <div id="${args.htmlid}-versionSection-div">
            <div class="yui-g">
               <h2>${msg("section.version")}</h2>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <span>${msg("label.version")}</span>
               </div> 
               <div class="yui-u">
                  <input id="${args.htmlid}-minorVersion-radioButton" type="radio" name="majorVersion" checked="checked" value="false" tabindex="0" />
                  <label for="${args.htmlid}-minorVersion-radioButton" id="${args.htmlid}-minorVersion">${msg("label.minorVersion")}</label>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">&nbsp;
               </div>
               <div class="yui-u">
                  <input id="${args.htmlid}-majorVersion-radioButton" type="radio" name="majorVersion" value="true" tabindex="0" />
                  <label for="${args.htmlid}-majorVersion-radioButton" id="${args.htmlid}-majorVersion">${msg("label.majorVersion")}</label>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${args.htmlid}-description-textarea">${msg("label.comments")}</label>
               </div>
               <div class="yui-u">
                  <textarea id="${args.htmlid}-description-textarea" name="description" cols="80" rows="4" tabindex="0"></textarea>
               </div>
            </div>
         </div>

         <div class="bdft">
            <input id="${args.htmlid}-upload-button" type="button" value="${msg("button.upload")}" tabindex="0" />
            <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.cancel")}" tabindex="0" />
         </div>

         </fieldset>
      </form>

   </div>
</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.HtmlUpload("${args.htmlid}").setMessages(
   ${messages}
);
Alfresco.util.relToTarget("${args.htmlid}-singleUploadTip-span");
//]]></script>
