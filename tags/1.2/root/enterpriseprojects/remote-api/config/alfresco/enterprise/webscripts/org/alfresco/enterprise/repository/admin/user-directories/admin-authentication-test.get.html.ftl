<#include "../admin-template.ftl" />

<@page title=msg("test.authentication.page.title", urldecode(args.authenticatorName)) dialog=true >

   <div class="column-full">
      <p class="intro">${msg("test.authentication.page.intro-text")?html}</p>
      <@section label=msg("test.authentication.request.section") />
   </div>
   <div class="column-left">
      <@text name="" id="test-username" label=msg("test.authentication.username") />
   </div>
   <div class="column-right">
      <@password name="" id="test-password" label=msg("test.authentication.password") visibilitytoggle=true />
   </div>
   <div class="column-full">
      <@dialogbuttons>
         <@button label=msg("test.authentication.test") onclick="AdminAT.testAuth()" />
      </@dialogbuttons>
      
      <@section label=msg("test.authentication.result.section") />
      <div id="test-results" class="hidden">
         <p id="test-auth-passed"></p>
      </div>
      <div id="test-messages" class="hidden">
         <@section label=msg("test.authentication.message.section") />
         <div id="test-diagnostics"></div>
      </div>
   </div>
   
   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {
   // bind form field Enter key presses to Test button event handler
   Admin.addEventListener(el("test-username"), 'keypress', function(e) {
      if (e.keyCode === 13) AdminAT.testAuth();
      return true;
   });
   Admin.addEventListener(el("test-password"), 'keypress', function(e) {
      if (e.keyCode === 13) AdminAT.testAuth();
      return true;
   });
});

/**
 * Admin Authentication Test Component
 */
var AdminAT = AdminAT || {};

(function() {
   
   /* map of I18N message IDs to labels */
   AdminAT.msgs = {
      "true": "${msg("test.authentication.step.success")?j_string}",
      "false": "${msg("test.authentication.step.failed")?j_string}",
      "passed": "${msg("test.authentication.passed")?j_string}",
      "failed": "${msg("test.authentication.failed")?j_string}",
      "results": "${msg("test.authentication.results")?j_string}"
   };
   
   AdminAT.testAuth = function testAuth()
   {
      // collect form data
      var username = Admin.trim(el("test-username").value),
          password = Admin.trim(el("test-password").value);
      
      if (username && password)
      {
         Admin.request({
            method: "POST",
            url: "${url.service}",
            data: {
               authenticatorName: "${args.authenticatorName?js_string}",
               userName: username,
               password: password
            },
            fnSuccess: function(res)
            {
               if (res.responseJSON)
               {
                  // unhide results area
                  el("test-results").className = "";
                  
                  // display results
                  var json = res.responseJSON;
                  el("test-auth-passed").className = json.testPassed ? "success" : "failure";
                  el("test-auth-passed").innerHTML = json.testPassed ? AdminAT.msgs["passed"] : AdminAT.msgs["failed"];
                  
                  // display diagnostic messages if there are any
                  var diagnostics = "";
                  if (json.authenticationMessage)
                  {
                     diagnostics = "<div>" + Admin.html(json.authenticationMessage) + "</div>";
                  }
                  if (json.diagnostic)
                  {
                     diagnostics += '<p class="info label">' + AdminAT.msgs["results"] + '</p><table class="results">';
                     for (var i=0; i<json.diagnostic.length; i++)
                     {
                        diagnostics += "<tr><td style='padding-right:16px'>" + Admin.html(json.diagnostic[i].message) +":</td>" +
                                       "<td class='" + (json.diagnostic[i].success ? "success" : "failure") + "'>" + AdminAT.msgs[json.diagnostic[i].success] + "</td></tr>";
                     }
                     diagnostics += "</table>";
                  }
                  el("test-messages").className = diagnostics ? "" : "hidden";
                  el("test-diagnostics").innerHTML = diagnostics;
               }
            }
         });
      }
   }

})();

//]]></script>

</@page>