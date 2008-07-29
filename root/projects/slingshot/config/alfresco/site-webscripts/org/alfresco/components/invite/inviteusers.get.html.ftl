<script type="text/javascript">//<![CDATA[
   new Alfresco.InviteUsers("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="invitelistwrapper">
   <div class="title">${msg("inviteusers.title")}</div>
   
   <div class="invitelist">
      
      <div id="${args.htmlid}-invitelistBar" class="invitelist-bar">
         <div class="label">${msg("inviteusers.search.label")}</div>
         <div class="sinput"><input type="text" class="sinput" id="${args.htmlid}-search-text" value=""/></div>
         <div class="bsub">
            <span id="${args.htmlid}-search-button" class="yui-button"> 
               <span class="first-child"> 
                  <button type="button">${msg("inviteusers.search.button")}</button> 
               </span> 
            </span>
         </div>
      </div>

      <div id="${args.htmlid}-userslist" class="userslist">
      </div>
   </div>
</div>