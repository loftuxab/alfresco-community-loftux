<#include "../admin-template.ftl" />

<@page title=msg("test.user.sync.page.title", urldecode(args.authenticatorName)) dialog=true >
   <div class="column-full">
      <p class="intro">${msg("test.user.sync.page.intro-text")?html}</p>
      <@section label=msg("test.user.sync.request.section") />
   </div>
   
   <@dialogbuttons>
      <@button label=msg("test.user.sync.test") onclick="AdminST.testSync()" />
   </@dialogbuttons>
   
   <div id="test-results" class="hidden">
      <@section label=msg("test.user.sync.result.section") />
      
      <div id="test-messages" class="column-full">
         <p id="test-auth-passed"></p>
         <p id="test-auth-active"></p>
         <div id="test-diagnostics"></div>
      </div>
      
      <div class="column-left">
         <@section label=msg("test.user.sync.users.section") />
         <p id="userList"></p>
      </div>
      
      <div class="column-right">
         <@section label=msg("test.user.sync.groups.section") />
         <p id="groupList"></p> 
      </div>
   </div>
   
   <script type="text/javascript">//<![CDATA[

/**
 * Admin Synchronization Test Component
 */
var AdminST = AdminST || {};

(function() {
   
   /* map of I18N message IDs to labels */
   AdminST.msgs = {
      "active": "${msg("test.user.sync.active")?j_string}",
      "notactive": "${msg("test.user.sync.notactive")?j_string}",
      "true": "${msg("test.user.sync.step.success")?j_string}",
      "false": "${msg("test.user.sync.step.failed")?j_string}",
      "passed": "${msg("test.user.sync.passed")?j_string}",
      "failed": "${msg("test.user.sync.failed")?j_string}",
      "results": "${msg("test.user.sync.results")?j_string}"
   };
   
   AdminST.testSync = function testSync()
   {
      Admin.request({
         method: "POST",
         url: "${url.service}",
         data: {
            authenticatorName: "${args.authenticatorName?js_string}",
            maxItems: 10
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
               el("test-auth-passed").innerHTML = json.testPassed ? AdminST.msgs["passed"] : AdminST.msgs["failed"];
               
               // display sync active 
               if (typeof json.syncActive !== "undefined")
               {
                  el("test-auth-active").className = json.syncActive ? "active" : "not-active";
                  el("test-auth-active").innerHTML = json.syncActive ? AdminST.msgs["active"] : AdminST.msgs["notactive"];
               }
               
               if (json.users)
               {
                  var users = '<table class="data">';
                  for (var i=0; i<json.users.length; i++)
                  {
                     users += "<tr><td style='padding-right:16px'>" + Admin.html(json.users[i].id) +"</td></tr>";
                  }
                  users += '</table>';
                  
                  el("userList").className = "results";
                  el("userList").innerHTML = users;
               }
               else
               {
                  el("userList").className = "hidden";
               }
               if (json.groups)
               {
                  var groups = '<table class="data">';
                  for (var i=0; i<json.groups.length; i++)
                  {
                     groups += "<tr><td style='padding-right:16px'>" + Admin.html(json.groups[i].id) +"</td></tr>";
                  }
                  groups += '</table>';
                  
                  el("groupList").className = "results";
                  el("groupList").innerHTML = groups;
               }
               else
               {
                  el("groupList").className = "hidden";
               }
               
               // display diagnostic messages if there are any
               var diagnostics = "";
               
               if (json.authenticationMessage)
               {
                  diagnostics = "<div>" + Admin.html(json.authenticationMessage) + "</div>";
               }
               if (json.diagnostic)
               {
                  diagnostics += '<p class="info label">' + AdminST.msgs["results"] + '</p><table class="results">';
                  for (var i=0; i<json.diagnostic.length; i++)
                  {
                     diagnostics += "<tr><td style='padding-right:16px'>" + Admin.html(json.diagnostic[i].message) +":</td>" +
                                    "<td class='" + (json.diagnostic[i].success ? "success" : "failure") + "'>" + AdminST.msgs[json.diagnostic[i].success] + "</td></tr>";
                  }
                  diagnostics += "</table>";
               }
               el("test-messages").className = diagnostics ? "" : "hidden";
               el("test-diagnostics").innerHTML = diagnostics;
            }
         }
      });
   }

})();
//]]></script>
</@page>