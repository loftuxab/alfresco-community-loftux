<#include "../admin-template.ftl" />

<@page title=msg("authentication-passthru.title", urldecode(args.id)) dialog=true>

   <div class="column-full">
      <p class="intro">${msg("authentication-passthru.intro-text")?html}</p>
      <p class="info">${msg("authentication-passthru.instruction-link")}</p>
      <@section label=msg("authentication-passthru.passthru-authentication") />
   </div>
   
   <div class="column-left">
      <@attrcheckbox attribute=attributes["passthru.authentication.useLocalServer"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.useLocalServer") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.useLocalServer.description") />
      <@attrcheckbox attribute=attributes["passthru.authentication.guestAccess"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.guestAccess") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.guestAccess.description") />
      <@attrcheckbox attribute=attributes["passthru.authentication.authenticateFTP"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.authenticateFTP") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.authenticateFTP.description") />
      <@attrtext attribute=attributes["passthru.authentication.domain"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.domain") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.domain.description") />
      <@attrtext attribute=attributes["passthru.authentication.protocolOrder"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.protocolOrder") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.protocolOrder.description") />
   </div>
   <div class="column-right">
      <@attrcheckbox attribute=attributes["ntlm.authentication.mapUnknownUserToGuest"] label=msg("authentication-passthru.passthru-authentication.ntlm.authentication.mapUnknownUserToGuest") description=msg("authentication-passthru.passthru-authentication.ntlm.authentication.mapUnknownUserToGuest.description") />
      <@attrtext attribute=attributes["passthru.authentication.defaultAdministratorUserNames"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.defaultAdministratorUserNames") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.defaultAdministratorUserNames.description") />
      <@attrtext attribute=attributes["passthru.authentication.servers"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.servers") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.servers.description") escape=false />
      <@attrtext attribute=attributes["passthru.authentication.connectTimeout"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.connectTimeout") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.connectTimeout.description") />
      <@attrtext attribute=attributes["passthru.authentication.offlineCheckInterval"] label=msg("authentication-passthru.passthru-authentication.passthru.authentication.offlineCheckInterval") description=msg("authentication-passthru.passthru-authentication.passthru.authentication.offlineCheckInterval.description") />
   </div>

   <@dialogbuttons save=true />
   
</@page>