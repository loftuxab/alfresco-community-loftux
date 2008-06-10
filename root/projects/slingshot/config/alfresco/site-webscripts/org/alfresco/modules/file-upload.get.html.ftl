<div id="${args.htmlid}-dialog" class="file-upload">
   <script type="text/javascript">//<![CDATA[

   /**
    * Note!
    *
    * IE doesn't run scripts when this template is rendered into the Dom!
    * So for now i18n doesn't work for the uploader in IE.
    *
    * A solution could be for the uploader (and all other modules that needs
    * i18n) to first only load the messages through a call to a service similar
    * to the one used for global messages:
    *
    * YAHOO.util.Get.script("http://localhost:8080/slingshot/service/messages/urlToComponent&messageScopeNameOrModuleIdOrName=id",
    * {
    *    onSuccess: aCallBackThatLaterLoadsTheTemplate
    * };
    *
    * and then loads the gui-template from the callback to ensure all messages
    * were loaded before the gui was displayed. Might make the popups feel too
    * slow. If so place the i18n-script call in the FileUpload constructor instead.
    *
    * The loaded script would look something like this (or look up the component
    * and use setMessages() as the script below):
    *
    * Alfresco.util.addMessages({"button.upload":"Upload File(s)","message.uploadStatus":"The remaining upload(s) has been cancel, at least {0} file(s) were uploaded.","label.noFiles":"No files to display. Click 'Browse' select files to upload.","header.update":"Update {0}","label.browseTip":"NOTE: You can select multiple files by using the CTRL or SHIFT key.","button.uploading":"Uploading...","header.upload":"Upload file(s)"}, "Alfresco.module.FileUpload");
    *
    * A 3rd solution would be to let the calling component set it.
    *
    * Have tried: XmlHttpRequest, GET.script, YAHOO.asyncRequest without a solution.
    */

   var uploaderComponent = Alfresco.util.ComponentManager.find({id: '${args.htmlid}'});
   if(uploaderComponent && uploaderComponent.length > 0)
   {
      // The component was instantiated and registered, lets localize it
      uploaderComponent[0].setMessages(${messages});
   }
   //]]></script>

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
         <span>${msg("section.version")}</span>
         <div class="yui-gd">
            <div class="yui-u first">
               ${msg("label.version")}
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
         <input id="${args.htmlid}-upload-button" type="button" value="${msg("button.upload")}" />
         <input id="${args.htmlid}-cancelOk-button" type="button" value="${msg("button.cancel")}" />
      </p>

      <div class="hiddenComponents">
         <div id="${args.htmlid}-left-div" class="fileupload-left-div">
            <span class="fileupload-percentage-span hiddenComponents">&nbsp;</span>
            <select class="fileupload-contentType-menu <#if (contentTypes?size == 1)>hiddenComponents</#if>">
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
