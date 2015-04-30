<#-- REPLACE "STANDARD INVITE PAGE LINK" WITH "CLOUD INVITE DIALOG LINK" -->
<@markup id="cloud-managerToolbar-inviteLink" action="replace" target="managerToolbar-inviteLink">
   <span class="first-child">
      <a id="${args.htmlid?html}-cloud-invite-link" href="#" class="theme-color-1">
         <img src="${url.context}/res/components/images/user-16.png" style="vertical-align: text-bottom" width="16" />
         ${msg("link.invite")}</a>
   </span>
   <script type="text/javascript">
      // Make the invite link display invite user component inside a dialog
      YAHOO.util.Event.on("${args.htmlid?js_string}-cloud-invite-link", "click", function(e)
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
