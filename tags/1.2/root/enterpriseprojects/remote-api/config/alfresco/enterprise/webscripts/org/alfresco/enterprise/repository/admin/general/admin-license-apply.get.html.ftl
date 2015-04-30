<#include "../admin-template.ftl" />

<@page title=msg("apply.license.title") dialog=true >

   <div class="column-full">
      <p class="intro">${msg("apply.license.intro-text")?html}</p>
      <p id="result">${msg("apply.license.waiting")?html}</p>
      
      <div class="buttons">
         <@button class="cancel" label=msg("admin-console.close") onclick="AdminAL.closeDialog();" />
      </div>
   </div>
   
   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {
   AdminAL.applyLicense();
});

/**
 * Admin Apply License Component
 */
var AdminAL = AdminAL || {};

(function() {

   AdminAL.closeDialog = function closeDialog()
   {
      parent.location.reload();
      top.window.Admin.removeDialog();
   }

   AdminAL.applyLicense = function applyLicense()
   {
      Admin.request({
         method: "POST",
         url: "${url.service}",
         fnSuccess: function(res)
         {
            if (res != null)
            {
               if (res.responseJSON)
               {
                  // display results
                  var json = res.responseJSON;
                  el("result").innerHTML = json.message;
               }
            }
            else
            {
               el("result").innerHTML = "${msg("apply.license.error")?html}";
            }
         }
      });
   }

})();

//]]></script>

</@page>