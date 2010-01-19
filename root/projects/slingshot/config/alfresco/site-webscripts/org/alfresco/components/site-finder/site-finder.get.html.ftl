<script type="text/javascript">//<![CDATA[
   new Alfresco.SiteFinder("${args.htmlid}").setOptions(
   {
      currentUser: "${user.id}",
      inviteData: [
   <#list inviteData as invite>
      {
         id: "${invite.inviteId}",
         siteId: "${invite.resourceName}",
         type: "${invite.invitationType}"
      }<#if invite_has_next>,</#if>
   </#list>
      ],
      setFocus: ${args.setFocus!'false'}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="site-finder">
	
	<div class="title"><label for="${args.htmlid}-term">${msg("site-finder.heading")}</label></div>
	
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${args.htmlid}-term" class="search-term" maxlength="256" /></div>
         <div class="search-button"><button id="${args.htmlid}-button">${msg("site-finder.search-button")}</button></div>
      </div>

      <div id="${args.htmlid}-sites" class="results"></div>
   </div>
	
</div>