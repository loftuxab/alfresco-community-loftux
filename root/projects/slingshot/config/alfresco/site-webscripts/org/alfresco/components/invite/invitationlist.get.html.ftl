<script type="text/javascript">//<![CDATA[
   new Alfresco.InvitationList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      roles:
      [
         <#list siteRoles as siteRole>"${siteRole}"<#if siteRole_has_next>,</#if></#list>
      ]
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-invitationlistwrapper" class="invitationlistwrapper">

<div class="title">${msg("invitationlist.title")}</div>

<div id="${args.htmlid}-invitationlist" class="invitationlist">

   <div id="${args.htmlid}-invitationBar" class="invitelist-bar">
      <button id="${args.htmlid}-selectallroles-button">${msg("invitationlist.selectallroles")}</button>
      <select id="${args.htmlid}-selectallroles-menu">
         <#list siteRoles as siteRole>
         <option value="${siteRole}">${msg('role.' + siteRole)}</option>
         </#list>
      </select>
   </div>

   <div id="${args.htmlid}-inviteelist" class="body inviteelist">
   </div>
   
   <div id="${args.htmlid}-role-column-template" style="display:none">
      <button class="role-selector-button" value="">${msg("role")}</button>
   </div>

</div>
<div class="sinvite">
   <input type="button" value="${msg("invitationlist.invite")}"  id="${args.htmlid}-invite-button" />
</div>
</div>