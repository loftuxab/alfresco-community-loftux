<#include "../admin-template.ftl" />

<@page title=msg("test.outboundemail.title") dialog=true >

   <div class="column-full">
      <p id="test-result">${msg("test.outboundemail.waiting")?html}</p>
      <@dialogbuttons />
      
      <div id="test-error" class="hidden">
         <@section label=msg("test.outboundemail.error") />
         <pre id="error-message" class="info label wrap"></pre>
      </div>
   </div>

   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {
   AdminOE.testEmail();
});

/**
 * Admin Outbound Email Component
 */
var AdminOE = AdminOE || {};

(function() {

   AdminOE.testEmail = function testEmail()
   {
      Admin.request({
         method: "POST",
         url: "${url.service}",
         fnSuccess: function(res)
         {
            if(res != null)
            {
               if (res.responseJSON)
               {
                  // display results
                  var json = res.responseJSON;
                  el("test-result").className = json.success ? "success" : "failure";
                  el("test-result").innerHTML = json.success ? "${msg("test.outboundemail.success")?html}" : "${msg("test.outboundemail.fail")?html}";
                  
                  // display error messages if there are any
                  if (!json.success && json.error)
                  {
                     el("error-message").innerHTML = Admin.html(json.error);
                     el("test-error").className = "";
                  }
               }
            }
            else
            {
               el("test-result").className = "failure";
               el("test-result").innerHTML = "${msg("test.outboundemail.fail")?html}";
            }
         }
      });

   }

})();

//]]></script>

</@page>