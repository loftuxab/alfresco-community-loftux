<#include "../admin-template.ftl" />

<@page title=msg("imap.title")>

   <div class="column-full">
      <p class="intro">${msg("imap.intro-text")?html}</p>
      <@section label=msg("imap.imapservice") />
   </div>
   <div class="column-left">
      <@attrcheckbox attribute=attributes["imap.server.enabled"] label=msg("imap.imapservice.imap.server.enabled") description=msg("imap.imapservice.imap.server.enabled.description") />
      <@attrtext attribute=attributes["imap.mail.to.default"] label=msg("imap.imapservice.imap.mail.to.default") description=msg("imap.imapservice.imap.mail.to.default.description") />
   </div>
   <div class="column-right">
      <@attrtext attribute=attributes["imap.server.host"] label=msg("imap.imapservice.imap.server.host") description=msg("imap.imapservice.imap.server.host.description") />
      <@attrtext attribute=attributes["imap.mail.from.default"] label=msg("imap.imapservice.imap.mail.from.default") description=msg("imap.imapservice.imap.mail.from.default.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("imap.protocol") />
   </div>
   <div class="column-left">
      <@attrcheckbox attribute=attributes["imap.server.imap.enabled"] label=msg("imap.protocol.imap.server.imap.enabled") description=msg("imap.protocol.imap.server.imap.enabled.description") />
   </div>
   <div class="column-right">
       <@attrtext attribute=attributes["imap.server.port"] label=msg("imap.protocol.imap.server.port") description=msg("imap.protocol.imap.server.port.description") />
  </div>

   <div class="column-full">
      <@section label=msg("imaps.protocol") />
   </div>
   <div class="column-left">
      <@attrcheckbox attribute=attributes["imap.server.imaps.enabled"] label=msg("imaps.protocol.imap.server.imaps.enabled") description=msg("imaps.protocol.imap.server.imaps.enabled.description") />
   </div>
   <div class="column-right">
       <@attrtext attribute=attributes["imap.server.imaps.port"] label=msg("imaps.protocol.imap.server.imaps.port") description=msg("imaps.protocol.imap.server.imaps.port.description") />
  </div>

</@page>