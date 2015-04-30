<#include "../admin-template.ftl" />

<@page title=msg("reset.authentication.page.title", urldecode(args.authenticatorName)) dialog=true >

   <div class="column-full">
      <p class="intro">${msg("reset.authentication.page.intro-text")?html}</p>
      <p class="info">${msg("reset.authentication.page.warning")?html}</p>
      <@dialogbuttons>
         <@button label=msg("reset.authentication.reset") onclick="AdminAR.resetAuth()" />
      </@dialogbuttons>
   </div>
   
   <script type="text/javascript">//<![CDATA[

/**
 * Admin Authentication Reset Component
 */
var AdminAR = AdminAR || {};

(function() {
   
   AdminAR.resetAuth = function resetAuth()
   {
      Admin.request({
         method: "POST",
         url: "${url.service}",
         data: {
            authenticatorName: "${args.authenticatorName?js_string}"
         },
         fnSuccess: function(res)
         {
            top.window.Admin.removeDialog("saved");
         }
      });
   }

})();

//]]></script>

</@page>