
<script type="text/javascript">//<![CDATA[
   new Alfresco.InvitationList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="invitationlistwrapper">

<div class="title">Invitation List</div>
   


<div id="${args.htmlid}-body" class="invitationlist">

   <div id="${args.htmlid}-invitationBar" class="invitelist-bar">Select role, then click <b>invite</b>
      <input type="button" id="${args.htmlid}-selectallroles-button" value="select all roles to...">
      <select id="${args.htmlid}-selectallroles-menu"> 
         <option value="consumer">Site Consumer</option> 
         <option value="collaborator">Site Coordinator</option> 
         <option value="manager">Site Manager</option>                 
      </select>
   </div>

   <div id="${args.htmlid}-inviteelist" class="body inviteelist">
   </div>
   
   <div id="${args.htmlid}-role-column-template" style="display:none">

         <button class="role-selector-button" value="" />
         
   </div>

<#--
         <a href="#" id="${args.htmlid}-removeInvitee" class="remove-item-button"><span class="removeIcon">&nbsp;</span></a>
-->        
</div>
<div class="sinvite">
   <input type="button" value="Send Invite"  id="${args.htmlid}-invite-button" />
</div>
</div>

