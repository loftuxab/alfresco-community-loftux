<#include "../admin-template.ftl" />

<@page title=msg("authentication-kerberos.title", urldecode(args.id)) dialog=true>

   <div class="column-full">
      <p class="intro">${msg("authentication-kerberos.intro-text")?html}</p>
      <p class="info">${msg("authentication-kerberos.instruction-link")}</p>
      <@section label=msg("authentication-kerberos.kerberos-authentication") />
   </div>
   
   <div class="column-left">
      <@attrtext attribute=attributes["kerberos.authentication.user.configEntryName"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.user.configEntryName") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.user.configEntryName.description") />
      <@attrtext attribute=attributes["kerberos.authentication.cifs.configEntryName"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.cifs.configEntryName") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.cifs.configEntryName.description") />
      <@attrpassword attribute=attributes["kerberos.authentication.cifs.password"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.cifs.password") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.cifs.password.description") maxlength=127 visibilitytoggle=true populatevalue=true />
      <@attrcheckbox attribute=attributes["kerberos.authentication.stripUsernameSuffix"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.stripUsernameSuffix") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.stripUsernameSuffix.description") /> 
      <@attrcheckbox attribute=attributes["kerberos.authentication.authenticateFTP"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.authenticateFTP") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.authenticateFTP.description") />
   </div>
   <div class="column-right">
      <@attrtext attribute=attributes["kerberos.authentication.defaultAdministratorUserNames"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.defaultAdministratorUserNames") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.defaultAdministratorUserNames.description") />
      <@attrtext attribute=attributes["kerberos.authentication.realm"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.realm") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.realm.description") />
      <@attrtext attribute=attributes["kerberos.authentication.http.configEntryName"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.http.configEntryName") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.http.configEntryName.description") />
      <@attrpassword attribute=attributes["kerberos.authentication.http.password"] label=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.http.password") description=msg("authentication-kerberos.kerberos-authentication.kerberos.authentication.http.password.description") maxlength=127 visibilitytoggle=true populatevalue=true />
   </div>

   <@dialogbuttons save=true />
   
</@page>