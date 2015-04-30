<#-- REPLACE "STANDARD INVITE PAGE LINK" WITH "CLOUD INVITE DIALOG LINK" -->
<@markup id="cloud-actionsContainer" action="after" target="actionsContainer">
   <script type="text/javascript">
      // Make the invite link display invite user component inside a dialog
      YAHOO.util.Event.on("${args.htmlid?js_string}-cloud-invite-button", "click", function(e)
      {
         Alfresco.util.loadWebscript({
            url: Alfresco.constants.URL_SERVICECONTEXT + "cloud/core/components/account/invite",
            properties:
            {
               site: <#if page.url.templateArgs.site??>"${page.url.templateArgs.site}"<#else>null</#if>
            }
         });
         YAHOO.util.Event.stopEvent(e);
      }, this, true);

   </script>
</@markup>
