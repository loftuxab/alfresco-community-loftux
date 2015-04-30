<#include "../admin-template.ftl" />

<@page title=msg("clustering-test.title") dialog=true>

   <div class="column-full">
      <p class="intro">${msg("clustering-test.intro-text")?html}</p>
      <@section label=msg("clustering-test.results") />
      
      <div id="test-result">
      </div>      
         
      <@dialogbuttons />
   </div>
   
   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {
   AdminRCT.validateCluster();
});

/**
 * Admin Repositry Clustering Test Component
 */
var AdminRCT = AdminRCT || {};

(function() {

   AdminRCT.validateCluster = function validateCluster()
   {
      Admin.request({
         method: "POST",
         url: "${url.service}",
         fnSuccess: function(res)
         {
            var resultHTML = "";
            if(res != null)
            {
               if (res.responseJSON)
               {
                  // display results
                  var json = res.responseJSON;
                  if(json.status == "table")
                  {
                     resultHTML = "<table class='validation'>";
                     resultHTML += "<tr><td class='key'>${msg("clustering-test.nodes")?html}</td>";
                     var nodeNames = json.nodeNames;
                     for(var x = 0; x < nodeNames.length; x++)
                     {
                        resultHTML += "<td class='key'>" + nodeNames[x] + "</td>";
                     }
                     
                     var tableData = json.tableData;
                     for(var x = 0; x < tableData.length; x++)
                     {
                        resultHTML += "<tr" + ((x % 2 != 0) ? " class='even'" : "") + ">";
                        resultHTML += "<td class='key'>" + nodeNames[x] + "</td>";
                        
                        var row = tableData[x];
                        
                        for(var y = 0; y < row.length; y++)
                        {
                           switch(row[y])
                           {
                              case "none" :
                                 resultHTML += "<td>&#8212;</td>";
                                 break;
                              case "success" :
                                 resultHTML += "<td class='success'>${msg("clustering-test.success")?html}</td>";
                                 break;
                              case "failure" :
                                 resultHTML += "<td class='failure'>${msg("clustering-test.failure")?html}</td>";
                                 break;
                           }
                        }
                     }
                     
                     resultHTML += "</tr>";
                     resultHTML += "</table>";
                  }
                  else if(json.status == "message")
                  {
                     if(json.message == "error")
                     {
                        resultHTML = "<span class='failure'>${msg("admin-console.requesterror")?html}</span>";
                     }
                     else
                     {
                        resultHTML = "<span>" + json.message + "</span>";
                     }
                  }
                  else
                  {
                     resultHTML = "<span class='failure'>${msg("admin-console.requesterror")?html}</span>";
                  }
               }
            }
            else
            {
               resultHTML = "<span class='failure'>${msg("admin-console.requesterror")?html}</span>";
            }
            el("test-result").innerHTML = resultHTML;
            el("test-result").className = "";
         },
         fnFailure: function(res)
         {
            el("test-result").innerHTML = "<span class='failure'>${msg("admin-console.requesterror")?html}</span>";
            el("test-result").className = "";
         }
      });
   }

})();

//]]></script>
</@page>