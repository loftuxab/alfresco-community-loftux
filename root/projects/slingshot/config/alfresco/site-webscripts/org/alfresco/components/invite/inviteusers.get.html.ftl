<#--
<script type="text/javascript">//<![CDATA[
   new Alfresco.InviteUserList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
-->

<div id="${args.htmlid}-body" class="invitelistwrapper">
   <div class="title">Search Alfreso users</div>
   
   <div class="invitelist">
	   
	   <div id="${args.htmlid}-invitelistBar" class="invitelist-bar">
		  <div class="label">Search</div>
		  <div class="sinput"><input type="text" class="sinput" name="${args.htmlid}-inviteuserlist-search" value=""/></div>
		  <div class="bsub"><input type="button" value="search" /></div>
	   </div>

	   <div id="${args.htmlid}-users" class="users">
			<div class="user">
				<div class="photo">here goes the photo</div>
				<div class="details">
					<span class="uname">Jim Smith</span>
					<span class="utitle">Title: Senior Developer</span>
					<span class="ucompany">Company: dotcom</span>
					<span class="uExpertise">Expertise: AJAX, Java</span>
				</div>
				<div class="badd"><input type="button" value="Add &gt;&gt;" /></div>
			</div>
			<div class="user">
				<div class="photo">here goes the photo</div>
				<div class="details">
					<span class="uname">Jim Smith</span>
					<span class="utitle">Title: Senior Developer</span>
					<span class="ucompany">Company: dotcom</span>
					<span class="uExpertise">Expertise: AJAX, Java</span>
				</div>
				<div class="badd"><input type="button" value="Add &gt;&gt;" /></div>
			</div>   		
	   </div>
   </div>
</div>