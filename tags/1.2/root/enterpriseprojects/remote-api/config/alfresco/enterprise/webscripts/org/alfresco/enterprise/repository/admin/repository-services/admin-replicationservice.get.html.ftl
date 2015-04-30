<#include "../admin-template.ftl" />

<@page title=msg("replicationservice.title")>

   <div class="column-full">
      <p class="intro">${msg("replicationservice.intro-text")?html}</p>
      <p class="info">${msg("replicationservice.help-link")}</p>
      <@section label=msg("replicationservice.replication-service") />
      <p class="info">${msg("replicationservice.replication-service.description")?html}</p>
   </div>
   
   <div class="column-left">
      <@attrcheckbox attribute=attributes["replication.enabled"] label=msg("replicationservice.replication-service.replication.enabled") description=msg("replicationservice.replication-service.replication.enabled.description") />
   </div>
   <div class="column-right">
      <@attrcheckbox attribute=attributes["replication.transfer.readonly"] label=msg("replicationservice.replication-service.replication.transfer.readonly") description=msg("replicationservice.replication-service.replication.transfer.readonly.description") />
   </div>
   
</@page>