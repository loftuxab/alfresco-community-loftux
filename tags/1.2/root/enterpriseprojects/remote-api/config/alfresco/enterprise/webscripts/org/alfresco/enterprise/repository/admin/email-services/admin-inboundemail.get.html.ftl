<#include "../admin-template.ftl" />

<@page title=msg("inboundemail.title")>

   <div class="column-full">
      <p class="intro">${msg("inboundemail.intro-text")?html}</p>
      <p class="info">${msg("inboundemail.instruction-link")}</p>
      <@section label=msg("inboundemail.inbound-email") />
   </div>
   
   <div class="column-left">
      <@attrcheckbox id="emailEnabled" attribute=attributes["email.inbound.enabled"] label=msg("inboundemail.inbound-email.enabled") description=msg("inboundemail.inbound-email.enabled.description") />
      <@attrhidden id="serverEnabled" attribute=attributes["email.server.enabled"] />
      <@attrhidden id="inboundEnabled" attribute=attributes["email.inbound.enabled"] />
      
      <@attrtext attribute=attributes["email.inbound.unknownUser"] label=msg("inboundemail.inbound-email.anonymous-username") description=msg("inboundemail.inbound-email.anonymous-username.description") />
      <@attrtext attribute=attributes["email.server.allowed.senders"] label=msg("inboundemail.inbound-email.allowed-senders") description=msg("inboundemail.inbound-email.allowed-senders.description") />
      <@attrcheckbox attribute=attributes["email.handler.folder.overwriteDuplicates"] label=msg("inboundemail.inbound-email.overwrite-duplicates") description=msg("inboundemail.inbound-email.overwrite-duplicates.description") />
      <@attrtext attribute=attributes["email.server.connections.max"] label=msg("inboundemail.inbound-email.maximum-server-connections") description=msg("inboundemail.inbound-email.maximum-server-connections.description") />
   </div>
   <div class="column-right">
      <@attrcheckbox id="email.server.auth.enabled" attribute=attributes["email.server.auth.enabled"] label=msg("inboundemail.inbound-email.smtp-authentication-enabled") description=msg("inboundemail.inbound-email.smtp-authentication-enabled.description") />
      <@attrtext attribute=attributes["email.server.port"] label=msg("inboundemail.inbound-email.email-server-port") description=msg("inboundemail.inbound-email.email-server-port.description") />
      <@attrtext attribute=attributes["email.server.domain"] label=msg("inboundemail.inbound-email.email-server-domain") description=msg("inboundemail.inbound-email.email-server-domain.description") />
      <@attrtext attribute=attributes["email.server.blocked.senders"] label=msg("inboundemail.inbound-email.blocked-senders") description=msg("inboundemail.inbound-email.blocked-senders.description") />
      <@attrtext attribute=attributes["email.inbound.emailContributorsAuthority"] label=msg("inboundemail.inbound-email.email-contributors-authority") description=msg("inboundemail.inbound-email.email-contributors-authority.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("inboundemail.transport-layer-security") />
      <p>${msg("inboundemail.transport-layer-security.description")?html}</p>
      <p>${msg("inboundemail.transport-layer-security.tls-support.description")}</p>
      <@options id="tls-support" name="tls-support" label=msg("inboundemail.transport-layer-security.tls-support")?html value=selectedTLS>
         <@option label=msg("inboundemail.transport-layer-security.tls-support.disabled")?html value="" />
         <@option label=msg("inboundemail.transport-layer-security.tls-support.hidden")?html value="email.server.hideTLS" />
         <@option label=msg("inboundemail.transport-layer-security.tls-support.enabled")?html value="email.server.enableTLS" />
         <@option label=msg("inboundemail.transport-layer-security.tls-support.required")?html value="email.server.requireTLS" />
      </@options>
      <@attrhidden id="hideTLS" attribute=attributes["email.server.hideTLS"] />
      <@attrhidden id="enableTLS" attribute=attributes["email.server.enableTLS"] />
      <@attrhidden id="requireTLS" attribute=attributes["email.server.requireTLS"] />
      <p class="light">${msg("inboundemail.transport-layer-security.tls-support.disabled.description")?html}</p>
      <p class="light">${msg("inboundemail.transport-layer-security.tls-support.hidden.description")?html}</p>
      <p class="light">${msg("inboundemail.transport-layer-security.tls-support.enabled.description")?html}</p>
      <p class="light">${msg("inboundemail.transport-layer-security.tls-support.required.description")?html}</p>
   </div>

   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {
   Admin.addEventListener(el("tls-support"), "change", function() {
         adminIE_tlsOptionChanged(this)
      }); 
   Admin.addEventListener(el("emailEnabled"), "change", function() {
         adminIE_enabledChanged(this)
      }); 
});

/**
* Set the correct bean attribute values for the different TLS options.
* Each option requires its own attribute to be true and the others to be false
* The values are set in hidden fields.
*
* @param element The dropdown element that has changed
*/
function adminIE_tlsOptionChanged(element)
{
   var hideTLSInput = el("hideTLS");
   var enableTLSInput = el("enableTLS");
   var requireTLSInput = el("requireTLS");
   switch (element.selectedIndex)
   {
      case 0: //Disabled - set all to false
         hideTLSInput.value = "false";
         enableTLSInput.value = "false";
         requireTLSInput.value = "false";
         break;
      case 1: //Hidden - set email.server.hideTLS to true, others to false
         hideTLSInput.value = "true";
         enableTLSInput.value = "false";
         requireTLSInput.value = "false";
         break;
      case 2: //Enabled - set email.server.enableTLS to true, others to false
         hideTLSInput.value = "false";
         enableTLSInput.value = "true";
         requireTLSInput.value = "false";
         break;
      case 3: //Required - set email.server.requireTLS to true, others to false
         hideTLSInput.value = "false";
         enableTLSInput.value = "false";
         requireTLSInput.value = "true";
         break;
   }
}

/**
* Set email.server.enabled and email.inbound.enabled when the emailEnabled is changed
* Both attributes need to be true for inbound email to be enabled.
* The values are set in hidden fields.
* 
* @param element The checkbox that has changed.
*/
function adminIE_enabledChanged(element)
{
   if(element.checked)
   {
      el("serverEnabled").value = "true";
      el("inboundEnabled").value = "true";
   } else
   {
      el("serverEnabled").value = "false";
      el("inboundEnabled").value = "false";
   }
}
//]]></script>

</@page>