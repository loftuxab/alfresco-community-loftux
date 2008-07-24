
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
      <select>
        <option>Site Consumer</option>
        <option>Site Collaboratior</option>
        <option>Site Manager</option>
      </select>
   </div>

   <div id="${args.htmlid}-inviteelist" class="inviteelist">
   List of added users
   </div>
   <input type="button" value="Invite" />
</div>