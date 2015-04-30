<#include "../admin-template.ftl" />

<@page title=msg("syncsettings.title") dialog=true >

   <div class="column-full">
      <p class="intro">${msg("syncsettings.help-text")}</p>
      <p>${msg("syncsettings.help-link")}</p>
      
      <@section label=msg("syncsettings.settings") />
   </div>
   <div class="column-left">
      <@attrcheckbox attribute=attributes["synchronization.syncOnStartup"] label=msg("syncsettings.sync-on-startup") description=msg("syncsettings.sync-on-startup.description") />
      <@attrcheckbox attribute=attributes["synchronization.syncWhenMissingPeopleLogIn"] label=msg("syncsettings.sync-missing-people") description=msg("syncsettings.sync-missing-people.description") />
      <@attrcheckbox attribute=attributes["synchronization.allowDeletions"] label=msg("syncsettings.allow-deletions") description=msg("syncsettings.allow-deletions.description") />
      <@attrtext attribute=attributes["synchronization.loggingInterval"] label=msg("syncsettings.logging-interval") description=msg("syncsettings.logging-interval.description") maxlength=16 />
   </div>
   <div class="column-right">
      <@attrcheckbox attribute=attributes["synchronization.autoCreatePeopleOnLogin"] label=msg("syncsettings.auto-create-people") description=msg("syncsettings.auto-create-people.description") />
      <@attrcheckbox attribute=attributes["synchronization.synchronizeChangesOnly"] label=msg("syncsettings.sync-changes-only") description=msg("syncsettings.sync-changes-only.description") />
      <@attrtext attribute=attributes["synchronization.import.cron"] label=msg("syncsettings.import-cron-expression") description=msg("syncsettings.import-cron-expression.description") maxlength=256 />
      <@attrtext attribute=attributes["synchronization.workerThreads"] label=msg("syncsettings.sync-worker-threads") description=msg("syncsettings.sync-worker-threads.description") maxlength=16 />
   </div>
   
   <@dialogbuttons save=true />

</@page>