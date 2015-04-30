<#include "../admin-template.ftl" />

<@page title=msg("outboundemail.title")>

   <div class="column-full">
      <p class="intro">${msg("outboundemail.intro-text")?html}</p>
      <p class="info">${msg("outboundemail.instruction-link")}</p>
      <@section label=msg("outboundemail.outboundemail") />
   </div>
   
   <div class="column-left">
      <@attrtext attribute=attributes["mail.host"] label=msg("outboundemail.outboundemail.hostname") description=msg("outboundemail.outboundemail.hostname.description") />
      <@attrtext attribute=attributes["mail.encoding"] label=msg("outboundemail.outboundemail.encoding") description=msg("outboundemail.outboundemail.encoding.description") />
      <@attrcheckbox attribute=attributes["mail.from.enabled"] label=msg("outboundemail.outboundemail.from-enabled") description=msg("outboundemail.outboundemail.from-enabled.description") />
   </div>
   <div class="column-right">
      <@attrtext attribute=attributes["mail.port"] label=msg("outboundemail.outboundemail.email-server-port") description=msg("outboundemail.outboundemail.email-server-port.description") />
      <@attrtext attribute=attributes["mail.from.default"] label=msg("outboundemail.outboundemail.default-senders-address") description=msg("outboundemail.outboundemail.default-senders-address.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("outboundemail.protocol-and-authentication") />
      <p class="info">${msg("outboundemail.protocol-and-authentication.description")?html}</p>
      <@attroptions id="mailProtocol" attribute=attributes["mail.protocol"] label=msg("outboundemail.protocol-and-authentication.email-protocol") description=msg("outboundemail.protocol-and-authentication.email-protocol.description")>
         <@option label=msg("outboundemail.protocol-and-authentication.email-protocol.smtp") value="smtp" />
         <@option label=msg("outboundemail.protocol-and-authentication.email-protocol.smtps") value="smtps" />
      </@attroptions>
   </div>
   
   <div class="column-left">
      <@attrtext attribute=attributes["mail.username"] label=msg("outboundemail.protocol-and-authentication.user-name") description=msg("outboundemail.protocol-and-authentication.user-name.description") />
   </div>
   <div class="column-right">
      <@attrpassword attribute=attributes["mail.password"] label=msg("outboundemail.protocol-and-authentication.password") description=msg("outboundemail.protocol-and-authentication.password.description") visibilitytoggle=true populatevalue=true />
   </div>

   <div id="smtpSettings" <#if attributes["mail.protocol"].value != "smtp">class="hidden"</#if> >
      <div class="column-full">
         <@section label=msg("outboundemail.smtp-setup") />
         <p class="info">${msg("outboundemail.smtp-setup.description")?html}</p>
      </div>
      <div class="column-left">
         <@attrcheckbox attribute=attributes["mail.smtp.auth"] label=msg("outboundemail.smtp-setup.smtp-authentication-required") description=msg("outboundemail.smtp-setup.smtp-authentication-required.description") />
         <@attrcheckbox attribute=attributes["mail.smtp.starttls.enable"] label=msg("outboundemail.smtp-setup.start-smtp-tls-enable") description=msg("outboundemail.smtp-setup.start-smtp-tls-enable.description") />
      </div>
      <div class="column-right">
         <@attrtext attribute=attributes["mail.smtp.timeout"] label=msg("outboundemail.smtp-setup.smtp-timeout") description=msg("outboundemail.smtp-setup.smtp-timeout.description") />
         <@attrcheckbox attribute=attributes["mail.smtp.debug"] label=msg("outboundemail.smtp-setup.smtp-debug") description=msg("outboundemail.smtp-setup.smtp-debug.description") />
      </div>
   </div>

   <div id="smtpsSettings" <#if attributes["mail.protocol"].value != "smtps">class="hidden"</#if> >
      <div class="column-full">
         <@section label=msg("outboundemail.smtps-setup") />
         <p class="info">${msg("outboundemail.smtps-setup.description")?html}</p>
      </div>
      <div class="column-left">
         <@attrcheckbox attribute=attributes["mail.smtps.auth"] label=msg("outboundemail.smtps-setup.smtps-authentication-required") description=msg("outboundemail.smtps-setup.smtps-authentication-required.description") />
      </div>
      <div class="column-right">
         <@attrcheckbox attribute=attributes["mail.smtps.starttls.enable"] label=msg("outboundemail.smtps-setup.start-smtps-tls-enable") description=msg("outboundemail.smtps-setup.start-smtps-tls-enable.description") />
      </div>
   </div>

   <div class="column-full">
      <@section label=msg("outboundemail.test-message") />
      <p class="info">${msg("outboundemail.test-message.description")?html}</p>
   </div>
   
   <div class="column-left">
      <@attrcheckbox attribute=attributes["mail.testmessage.send"] label=msg("outboundemail.test-message.send-test-message-on-startup") description=msg("outboundemail.test-message.send-test-message-on-startup.description") />
      <@attrtext attribute=attributes["mail.testmessage.subject"] label=msg("outboundemail.test-message.subject") description=msg("outboundemail.test-message.subject.description") />
      <@button id="send-test-message-button" label=msg("outboundemail.test-message.send-test-message") onclick="Admin.showDialog('${url.serviceContext}/enterprise/admin/admin-outboundemail-test');" />
   </div>
   <div class="column-right">
      <@attrtext attribute=attributes["mail.testmessage.to"] label=msg("outboundemail.test-message.to") description=msg("outboundemail.test-message.to.description") />
      <@attrtext attribute=attributes["mail.testmessage.text"] label=msg("outboundemail.test-message.message") description=msg("outboundemail.test-message.message.description") />
   </div>

   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {
   
   Admin.addEventListener(el("mailProtocol"), "change", function() {
         adminIE_switchProtocolFields(this);
      }); 
      
   // Get the form element
   var form = el("${FORM_ID}");

   //Disable the Send Test Message button if the form has been changed.
   Admin.addEventListener(form, 'change', function(e) {
      el("send-test-message-button").disabled = true;
   });

});

/**
 * Switch the input elements in the SMTP and SMTPS DIVs.
 * 
 * @param element The option field.
 */
function adminIE_switchProtocolFields(element)
{
   if(element.options[element.selectedIndex].value == "smtp")
   {
      el("smtpsSettings").className = "hidden";
      el("smtpSettings").className = "";
   }
   else
   {
      el("smtpSettings").className = "hidden";
      el("smtpsSettings").className = "";
   }
}

//]]></script>

</@page>