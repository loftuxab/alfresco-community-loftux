<#include "../admin-template.ftl" />

<@page title=msg("subscriptions.title")>

   <div class="column-full">
      <p class="intro">${msg("subscriptions.intro-text")?html}</p>
      <@section label=msg("subscriptions.subscription-service") />
      <p class="info">${msg("subscriptions.subscription-service.description")?html}</p>
      <@attrcheckbox attribute=attributes["subscriptions.enabled"] label=msg("subscriptions.subscription-service.subscriptions-enabled") description=msg("subscriptions.subscription-service.subscriptions-enabled.description") />
   </div>
   
</@page>