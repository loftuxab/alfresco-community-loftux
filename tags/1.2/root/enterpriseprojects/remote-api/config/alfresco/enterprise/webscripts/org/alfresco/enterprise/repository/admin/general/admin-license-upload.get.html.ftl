<#include "../admin-template.ftl" />

<@page title=msg("upload.license.title") dialog=true >

   <div class="column-full">
      <p class="intro">${msg("upload.license.intro-text")?html}</p>
      
      <!-- File upload overlay -->
      <div id="ul-uploader" class="file-upload-wrapper">
         <input class="file-upload-hidden" name="licenseFile" type="file" size="30" id="ul-fileHidden" onchange="AdminUL.changeSelectedFile(this);" />
         <div class="file-upload-visible"><input class="file-upload-text" type="text" id="ul-fileField" readonly="true" placeholder="${msg("upload.placeholder")}" /></div>
      </div>
      
      <p id="ul-result" class="hidden">${msg("upload.license.waiting")?html}</p>
      
      <div class="buttons">
         <@button id="ul-upload-button" label=msg("upload.button.upload") onclick="AdminUL.uploadLicense()" disabled="true" />
         <@button class="cancel" label=msg("admin-console.close") onclick="AdminUL.closeDialog();" />
      </div>
  </div>
   
  <script type="text/javascript">//<![CDATA[
/**
 * Admin Upload License Component
 */
var AdminUL = AdminUL || {};

(function() {

   AdminUL.changeSelectedFile = function changeSelectedFile(element)
   {
      el('ul-fileField').value=element.value;
      el("ul-upload-button").disabled = false;
   }

   AdminUL.uploadLicense = function uploadLicense()
   {
      // disable the upload button and show the waiting message
      el("ul-upload-button").disabled = true;
      el("ul-uploader").className = "hidden";
      el("ul-result").className = "info";
      
      // perform the upload post request
      Admin.uploadFile('ul-fileHidden', "${url.service}",
         function(json)
         {
            if (json.success)
            {
               el("ul-result").innerHTML = "${msg("upload.license.success")?html}";
               el("ul-result").className = "success";
            }
            else
            {
               if(json.error == "fail")
               {
                  el("ul-result").innerHTML = "${msg("upload.license.fail")?html}";
               }
               else if(json.error == "reload-fail")
               {
                  el("ul-result").innerHTML = "${msg("upload.license.reload-fail")?html}";
               }
               else
               {
                  el("ul-result").innerHTML = "${msg("upload.license.error")?html}";
               }
               el("ul-result").className = "failure";
            }
         },
         function()
         {
            el("ul-result").innerHTML = "${msg("upload.license.error")?html}";
            el("ul-result").className = "failure";
         }
      );
   }
   
   AdminUL.closeDialog = function closeDialog()
   {
      if(el("ul-result").className == "success")
      {
         parent.location.reload();
      }
      top.window.Admin.removeDialog();
   }
   
})();

//]]></script>

</@page>