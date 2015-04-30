<#include "../admin-template.ftl" />

<@page title=msg("authentication-external.title", urldecode(args.id)) dialog=true>

   <div class="column-full">
      <p class="intro">${msg("authentication-external.intro-text")?html}</p>
      <p class="info">${msg("authentication-external.instruction-link")}</p>
      <@section label=msg("authentication-external.extenal-authentication") />
      <@attrcheckbox attribute=attributes["external.authentication.enabled"] label=msg("authentication-external.extenal-authentication.authentication-enabled") description=msg("authentication-external.extenal-authentication.authentication-enabled.description") />
   </div>
   
   <div class="column-left">
      <@attrtext attribute=attributes["external.authentication.proxyUserName"] label=msg("authentication-external.extenal-authentication.proxy-username") description=msg("authentication-external.extenal-authentication.proxy-username.description") />
      <@attrtext attribute=attributes["external.authentication.proxyHeader"] label=msg("authentication-external.extenal-authentication.proxy-header") description=msg("authentication-external.extenal-authentication.proxy-header.description") />
   </div>
   <div class="column-right">
      <@attrtext attribute=attributes["external.authentication.defaultAdministratorUserNames"] label=msg("authentication-external.extenal-authentication.admistrator-user-names") description=msg("authentication-external.extenal-authentication.admistrator-user-names.description") />
      <@attrtext attribute=attributes["external.authentication.userIdPattern"] label=msg("authentication-external.extenal-authentication.user-id-pattern") description=msg("authentication-external.extenal-authentication.user-id-pattern.description") />
   </div>
   
   <@dialogbuttons save=true />
   
</@page>