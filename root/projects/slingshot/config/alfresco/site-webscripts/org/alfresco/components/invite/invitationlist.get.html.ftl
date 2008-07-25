
<script type="text/javascript">//<![CDATA[
   new Alfresco.InvitationList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="invitationlist">

   <div id="${args.htmlid}-invitationBar" class="yui-g invitelist-bar">
      Search role, then click invite
      
      <input type="button" id="${args.htmlid}-selectallroles-button" value="Select all roles to...">
      <select id="${args.htmlid}-selectallroles-menu""> 
<!--         <option value="">Select all roles to...</option>  --> 
         <option value="consumer">Site Consumer</option> 
         <option value="collaborator">Site Coordinator</option> 
         <option value="manager">Site Manager</option>                 
      </select>
   </div>

   <div id="${args.htmlid}-inviteelist" class="inviteelist">
   </div>
   <input type="button" value="Invite" id="${args.htmlid}-invite-button" />
</div>