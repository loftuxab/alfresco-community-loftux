<#include "../admin-template.ftl" />

<@page title=msg("clustering.title") readonly=true>

   <div class="column-full">
      <p class="intro">${msg("clustering.intro-text")?html}</p>
      <p class="info">${msg("clustering.instruction-link")}</p>
      <@section label=msg("clustering.connected-host") />
   </div>
   
   <#if !cluster["ClusteringEnabled"].value>
   <div class="column-left">
      <@attrstatus attribute=cluster["ClusteringEnabled"] label=msg("clustering.connected-host.ClusteringEnabled") description=msg("clustering.connected-host.ClusteringEnabled.description") />
      <#if !license["ClusterEnabled"].value>
      <p class="info">${msg("clustering.connected-host.unlicensed")}</p>
      </#if>
   </div>
   <div class="column-right">
      <@attrfield attribute=cluster["ClusterName"] label=msg("clustering.connected-host.ClusterName") description=msg("clustering.connected-host.ClusterName.description") />
   </div>
   <#else>
   <div class="column-left">
      <@attrfield attribute=clusterAdmin["HostName"] label=msg("clustering.connected-host.HostName") description=msg("clustering.connected-host.HostName.description") />
      <@attrstatus attribute=cluster["ClusteringEnabled"] label=msg("clustering.connected-host.ClusteringEnabled") description=msg("clustering.connected-host.ClusteringEnabled.description") />
   </div>
   <div class="column-right">
      <@attrfield attribute=clusterAdmin["IPAddress"] label=msg("clustering.connected-host.IPAddress") description=msg("clustering.connected-host.IPAddress.description") />
      <@attrfield attribute=cluster["ClusterName"] label=msg("clustering.connected-host.ClusterName") description=msg("clustering.connected-host.ClusterName.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("clustering.cluster-members") />
      <div class="control">
         <span class="label">${msg("clustering.cluster-members.ClusterMembers")?html}:</span>
         <table id="rc-membertable" class="data">
            <tbody>
               <tr>
                  <th>${msg("clustering.column.host.name")?html}</th>
                  <th>${msg("clustering.column.host.ip")?html}</th>
                  <th>${msg("clustering.column.host.port")?html}</th>
                  <th>${msg("clustering.column.last.registered")?html}</th>
               </tr>
               <#list clusterMembers as cm>
               <tr>
                  <td>${cm["host.name"].value?html}</td>
                  <td>${cm["host.ip"].value?html}</td>
                  <td>${cm["host.port"].value?c}</td>
                  <td>${cm["last.registered"].value?datetime}</td>
               </tr>
               </#list>
            </tbody>
         </table>
      </div>
      <@attrfield attribute=clusterAdmin["NumClusterMembers"] label=msg("clustering.cluster-members.NumClusterMembers") description=msg("clustering.cluster-members.NumClusterMembers.description") />
      <@section label=msg("clustering.offline-cluster-members") />
      <div class="control">
         <span class="label">${msg("clustering.offline-cluster-members.OfflineMembers")?html}:</span>
         <table id="rc-offlinetable" class="data">
            <tbody>
               <tr>
                  <th>${msg("clustering.column.host.name")?html}</th>
                  <th>${msg("clustering.column.host.ip")?html}</th>
                  <th>${msg("clustering.column.host.port")?html}</th>
                  <th>${msg("clustering.column.last.registered")?html}</th>
                  <th></th>
               </tr>
               <#if offlineMembers?has_content>
               <#list offlineMembers as om>
               <tr>
                  <td>${om["host.name"].value?html}</td>
                  <td>${om["host.ip"].value?html}</td>
                  <td>${om["host.port"].value?c}</td>
                  <td>${om["last.registered"].value?datetime}</td>
                  <td><a href="#" onclick="AdminRC.removeOfflineMember(this, '${om["host.ip"].value?html}', '${om["host.port"].value?c}');return false;">${msg("clustering.offline-cluster-members.remove")?html}</a></td>
               </tr>
               </#list>
               <tr class="hidden">
                  <td>&#8212;</td>
                  <td>&#8212;</td>
                  <td>&#8212;</td>
                  <td>&#8212;</td>
                  <td></td>
               </tr>
               <#else>
               <tr>
                  <td>&#8212;</td>
                  <td>&#8212;</td>
                  <td>&#8212;</td>
                  <td>&#8212;</td>
                  <td></td>
               </tr>
               </#if>
            </tbody>
         </table>
      </div>
      <@section label=msg("clustering.connected-nonclustered-servers") />
      <div class="control">
         <span class="label">${msg("clustering.connected-nonclustered-servers.NonClusteredServers")?html}:</span>
         <table id="rc-nonmemtable" class="data">
            <tbody>
               <tr>
                  <th>${msg("clustering.column.host.name")?html}</th>
                  <th>${msg("clustering.column.host.ip")?html}</th>
               </tr>
               <#if nonClusteredServers?has_content>
               <#list nonClusteredServers as nm>
               <tr>
                  <td>${nm["host.name"].value?html}</td>
                  <td>${nm["host.ip"].value?html}</td>
               </tr>
               </#list>
               <#else>
               <tr>
                  <td>&#8212;</td>
                  <td>&#8212;</td>
               </tr>
               </#if>
            </tbody>
         </table>
         <span class="description">${msg("clustering.connected-nonclustered-servers.NonClusteredServers.description")?html}</span>
      </div>
      <@button id="validate-cluster-button" label=msg("clustering.button.validate-cluster") onclick="Admin.showDialog('${url.serviceContext}/enterprise/admin/admin-clustering-test');" />
   </div>

   <script type="text/javascript">//<![CDATA[
   
/**
 * Repository Clustering Component
 */
var AdminRC = AdminRC || {};

(function() {
   
   /* Remove Offline Cluster Member - action link event handler */
   AdminRC.removeOfflineMember = function removeOfflineMember(actionEl, hostIP, port)
   {
      Admin.request({
         method: "POST",
         url: "${url.service}",
         data: {
            hostIP: hostIP,
            port: port
         },
         fnSuccess: function(res)
         {
            if (res.responseJSON)
            {
               var json = res.responseJSON;
               if(json.success)
               {
                  var table = el("rc-offlinetable"),
                  rowIndex = actionEl.parentElement.parentElement.rowIndex;
               
                  // remove the row that contains this action element
                  table.deleteRow(rowIndex);
                  if(table.rows.length < 3)
                  {
                     table.rows[table.rows.length - 1].className = "";
                  }
               }
               else
               {
                  var error = "";
                  if(json.error)
                  {
                     switch(json.error)
                     {
                        case "badrequest":
                           error = "${msg("clustering.offline-cluster-members.remove.error.badrequest")?html}";
                           break;
                        case "seelog":
                        case "":
                           error = "${msg("clustering.offline-cluster-members.remove.error.seelog")?html}";
                           break;
                        default:
                           error = json.error;
                     }
                  }
                  else
                  {
                     error = "${msg("clustering.offline-cluster-members.remove.error.seelog")?html}";
                  }
                  alert("${msg("clustering.offline-cluster-members.remove.error")?html}\n" + error);
               }
            }
         },
         fnFailure: function(res)
         {
            var error = "";
            if (res.responseJSON)
            {
               var json = res.responseJSON;
               if(json.error)
               {
                  switch(json.error)
                  {
                     case "badrequest":
                        error = "${msg("clustering.offline-cluster-members.remove.error.badrequest")?html}";
                        break;
                     case "seelog":
                     case "":
                        error = "${msg("clustering.offline-cluster-members.remove.error.seelog")?html}";
                        break;
                     default:
                        error = json.error;
                  }
               }
               else
               {
                  error = "${msg("clustering.offline-cluster-members.remove.error.seelog")?html}";
               }
            }
            else
            {
               error = "${msg("clustering.offline-cluster-members.remove.error.seelog")?html}"; 
            }
            alert("${msg("clustering.offline-cluster-members.remove.error")?html}\n" + error);
         }
      });
   }
   
})();   
   
   //]]></script>
   </#if>
</@page>