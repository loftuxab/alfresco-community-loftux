<#include "../admin-template.ftl" />

<@page title=msg("fileservers.title")>

   <div class="column-full">
      <p class="intro">${msg("fileservers.intro-text")?html}</p>
      <@section label=msg("fileservers.filesystems") />
      <@attrtext attribute=attributes["filesystem.name"] label=msg("fileservers.filesystems.filesystem-name") description=msg("fileservers.filesystems.filesystem-name.description") />
      <@section label=msg("fileservers.cifs") />
      <@attrcheckbox attribute=attributes["cifs.enabled"] label=msg("fileservers.cifs.cifs-enabled") description=msg("fileservers.cifs.cifs-enabled.description") />
   </div>
   
   <div class="column-left">
      <@attrtext attribute=attributes["cifs.serverName"] label=msg("fileservers.cifs.server-name") description=msg("fileservers.cifs.server-name.description") maxlength=16 />
      <@attrtext attribute=attributes["cifs.domain"] label=msg("fileservers.cifs.domain") description=msg("fileservers.cifs.domain.description") />
   </div>
   <div class="column-right">
      <@attrcheckbox attribute=attributes["cifs.hostannounce"] label=msg("fileservers.cifs.host-announce") description=msg("fileservers.cifs.host-announce.description") />
      <@attrtext attribute=attributes["cifs.sessionTimeout"] label=msg("fileservers.cifs.session-timeout") description=msg("fileservers.cifs.session-timeout.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("fileservers.ftp") />
      <@attrcheckbox attribute=attributes["ftp.enabled"] label=msg("fileservers.ftp.ftp-enabled") description=msg("fileservers.ftp.ftp-enabled.description") />
   </div>

   <div class="column-left">
      <@attrtext attribute=attributes["ftp.port"] label=msg("fileservers.ftp.port") description=msg("fileservers.ftp.port.description") />
      <@attrtext attribute=attributes["ftp.dataPortTo"] label=msg("fileservers.ftp.dataport-to") description=msg("fileservers.ftp.dataport-to.description") />
   </div>
   <div class="column-right">
      <@attrtext attribute=attributes["ftp.dataPortFrom"] label=msg("fileservers.ftp.dataport-from") description=msg("fileservers.ftp.dataport-from.description") />
   </div>

</@page>