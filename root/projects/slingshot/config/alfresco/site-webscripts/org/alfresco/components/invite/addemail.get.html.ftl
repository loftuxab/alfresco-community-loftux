
<script type="text/javascript">//<![CDATA[
   new Alfresco.AddEmailInvite("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="inviteusersbyemail">
	<div class="title">... or type email address</div>
	<div class="body">		
		<div class="user">
			<div class="labels">
				<span class="fname">First name:</span>
				<span class="lname">Last name:</span>
				<span class="email">Email:</span>		
			</div>   			
			<div class="inputs">
				<span class="fnamei"><input type="text" id="${args.htmlid}-firstname" /> </span>
				<span class="lnamei"><input type="text" id="${args.htmlid}-lastname" /> </span>
				<span class="emaili"><input type="text" id="${args.htmlid}-email" /> </span>
			</div>
			<div class="badd"><input type="button" value="Add &gt;&gt;" id="${args.htmlid}-add-email-button"/></div>			
		</div>
	</div> 
</div>
