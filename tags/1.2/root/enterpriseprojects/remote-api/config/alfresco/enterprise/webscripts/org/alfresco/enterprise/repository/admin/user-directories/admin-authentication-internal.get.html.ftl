<#include "../admin-template.ftl" />

<@page title=msg("authentication-internal.title", urldecode(args.id)) dialog=true>

   <div class="column-full">
      <p class="intro">${msg("authentication-internal.intro-text")?html}</p>
      <p class="info">${msg("authentication-internal.instruction-link")}</p>
      <@section label=msg("authentication-internal.internal-authentication") />
   </div>
   
   <div class="column-left">
      <@attrcheckbox attribute=attributes["alfresco.authentication.allowGuestLogin"] label=msg("authentication-internal.internal-authentication.allow-guest-login") description=msg("authentication-internal.internal-authentication.allow-guest-login.description") />
      <@attrcheckbox attribute=attributes["alfresco.authentication.authenticateFTP"] label=msg("authentication-internal.internal-authentication.alfresco.authentication.authenticateFTP") description=msg("authentication-internal.internal-authentication.alfresco.authentication.authenticateFTP.description") />
   </div>
   <div class="column-right">
      <@attrcheckbox attribute=attributes["ntlm.authentication.mapUnknownUserToGuest"] label=msg("authentication-internal.internal-authentication.map-unknown-user-to-guest") description=msg("authentication-internal.internal-authentication.map-unknown-user-to-guest.description") />
   </div>
   
   <@dialogbuttons save=true />
   
</@page>