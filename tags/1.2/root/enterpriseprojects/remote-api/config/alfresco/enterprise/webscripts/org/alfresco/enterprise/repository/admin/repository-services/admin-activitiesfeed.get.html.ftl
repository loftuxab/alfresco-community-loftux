<#include "../admin-template.ftl" />

<@page title=msg("activitiesfeed.title")>

   <div class="column-full">
      <p class="intro-tall">${msg("activitiesfeed.intro-text")?html}</p>
      <@attrcheckbox attribute=attributes["activities.feed.notifier.enabled"] label=msg("activitiesfeed.feed-notifier-enabled") description=msg("activitiesfeed.feed-notifier-enabled.description") />
   </div>
   
   <div class="column-left">
      <@attrtext attribute=attributes["activities.feed.notifier.cronExpression"] label=msg("activitiesfeed.cronExpression") description=msg("activitiesfeed.cronExpression.description") />
      <@attrtext attribute=attributes["activities.feed.max.size"] label=msg("activitiesfeed.maximum-size") description=msg("activitiesfeed.maximum-size.description") />
   </div>
   <div class="column-right">
      <@attrtext attribute=attributes["activities.feed.max.ageMins"] label=msg("activitiesfeed.maximum-age") description=msg("activitiesfeed.maximum-age.description") />
   </div>
   
</@page>