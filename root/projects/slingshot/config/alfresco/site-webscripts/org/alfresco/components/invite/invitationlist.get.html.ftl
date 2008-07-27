
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
<#--
   <div id="${args.htmlid}-inviteelist" class="body inviteelist">
   		<div class="user">
   			<div class="photo">here goes the photo</div>
   			<div class="details">
   				<span class="uname">Jim Smith</span>
   				<span class="utitle">Senior Developer</span>
   				<span class="ucompany">dotcom</span>
   			</div>
   			<div class="roleselection">
			  <span class="roleselect">
			  <select>
				<option>Site Consumer</option>
				<option>Site Collaboratior</option>
				<option>Site Manager</option>
			  </select>   	
			  </span>
			  <span class="removeIcon">&nbsp;</span>
	   		</div>
   		</div>  		
   </div>
-->
   
</div>
<div class="sinvite">
   <input type="button" value="Send Invite"  id="${args.htmlid}-invite-button" />
</div>
</div>

