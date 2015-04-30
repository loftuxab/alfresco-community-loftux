<#include "../admin-template.ftl" />

<@page title=msg("systemsettings.title")>

   <div class="column-full">
      <p class="intro">${msg("systemsettings.intro-text")?html}</p>
      <p class="info">${msg("systemsettings.instruction-link")}</p>
      <@section label=msg("systemsettings.alfresco-application") />
      <p class="intro">${msg("systemsettings.alfresco-application.description")}</p>
   </div>

   <div class="column-left">
      <@attrfield attribute=attributes["alfresco.context"] label=msg("systemsettings.alfresco-application.repository-context") description=msg("systemsettings.alfresco-application.repository-context.description") />
      <@attrfield attribute=attributes["alfresco.host"] label=msg("systemsettings.alfresco-application.repository-hostname") description=msg("systemsettings.alfresco-application.repository-hostname.description") />
      <@attrfield attribute=attributes["server.allowWrite"] label=msg("systemsettings.server-settings.server-allow-writes") description=msg("systemsettings.server-settings.server-allow-writes.description") />
   </div>
   <div class="column-right">
      <@attrfield attribute=attributes["alfresco.protocol"] label=msg("systemsettings.alfresco-application.protocol") description=msg("systemsettings.alfresco-application.protocol.description") />
      <@attrfield attribute=attributes["alfresco.port"] label=msg("systemsettings.alfresco-application.port") description=msg("systemsettings.alfresco-application.port.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("systemsettings.server-settings") />
   </div>
   <div class="column-left">
      <@text name="" id="allowedUsers" label=msg("systemsettings.server-settings.allowed-users") description=msg("systemsettings.server-settings.allowed-users.description") value=cvalue(attributes["server.allowedusers"].type, attributes["server.allowedusers"].value) />
      <@attrhidden id="hidAllowedUsers" attribute=attributes["server.allowedusers"] />
   </div>
   <div class="column-right">
      <@attrtext attribute=attributes["server.maxusers"] label=msg("systemsettings.server-settings.maxium-users") description=msg("systemsettings.server-settings.maxium-users.description") />
   </div>

   <div class="column-full">
      <@section label=msg("systemsettings.share-application") />
   </div>
   <div class="column-left">
      <@attrtext attribute=attributes["share.context"] label=msg("systemsettings.share-application.share-context") description=msg("systemsettings.share-application.share-context.description") />
      <@attrtext attribute=attributes["share.host"] label=msg("systemsettings.share-application.share-hostname") description=msg("systemsettings.share-application.share-hostname.description") />
      <@attrtext attribute=attributes["site.public.group"] label=msg("systemsettings.share-application.site-public-group") description=msg("systemsettings.share-application.site-public-group.description") />
   </div>
   <div class="column-right">
      <@attroptions attribute=attributes["share.protocol"] label=msg("systemsettings.share-application.protocol") description=msg("systemsettings.share-application.protocol.description")>
         <@option label="http" value="http" />
         <@option label="https" value="https" />
      </@attroptions>
      <@attrtext attribute=attributes["share.port"] label=msg("systemsettings.share-application.port") description=msg("systemsettings.share-application.port.description") />
   </div>

   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {

   // capture the submit event for the form
   var form = el("${FORM_ID}");
   Admin.addEventListener(form, 'submit', function(e) {
      e.preventDefault ? e.preventDefault() : event.returnValue = false;
      
      adminIE_allowedUsers(el("allowedUsers"),el("hidAllowedUsers"));
      
      form.submit();
      return false;
   });

});

/**
 * Add the current user (who is an admin user) to the list of allowed users so it is not locked out.
 * The user is only added if the list is not blank
 * 
 * @param element The list
 */
function adminIE_allowedUsers(element, hidElement)
{
   // Copy the visible element to the hidden element to hide the username appending from the user.
   hidElement.value = element.value;
   
   if (hidElement.value.length > 0 && hidElement.value.indexOf("${person.properties.userName}") == -1)
   {
      hidElement.value += ",${person.properties.userName}";
   }
}
//]]></script>

</@page>