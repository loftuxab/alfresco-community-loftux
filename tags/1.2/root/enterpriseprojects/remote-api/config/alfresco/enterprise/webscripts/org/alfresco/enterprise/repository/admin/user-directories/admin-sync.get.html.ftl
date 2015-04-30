<#include "../admin-template.ftl" />

<@page title=msg("sync.page.title") dialog=true >
   <div class="column-full">
      <p class="intro">${msg("sync.page.intro-text")?html}</p>
   </div>
   
   <@dialogbuttons>
      <@button label=msg("sync.button.title") onclick="AdminSync.sync()" />
   </@dialogbuttons>
   
   <div id="sync-results" class="hidden">
   
      <div id="sync-status" class="column-full">
         <p id="sync-message"></p>
      </div>
   
   </div>
 
<script type="text/javascript">//<![CDATA[

/**
 * Admin Synchronization Component
 */
var AdminSync = AdminSync || {};

(function() {
   
   /* map of I18N message IDs to labels */
   AdminSync.msgs = {
      "success": "${msg("sync.success")?j_string}",
      "failed": "${msg("sync.failed")?j_string}"
   };
   
   AdminSync.sync = function sync()
   {
      Admin.request({
         method: "POST",
         url: "${url.service}",
         data: {},
         fnSuccess: function(res)
         {
            if (res.responseJSON)
            {
               // unhide results area
               el("sync-results").className = "";
               
               var json = res.responseJSON;  
               el("sync-message").className = json.success ? "success" : "failure";
               el("sync-message").innerHTML = json.success ? AdminSync.msgs["success"] : AdminSync.msgs["failed"];
            }
         }
      });
   }

})();
//]]></script>

</@page>