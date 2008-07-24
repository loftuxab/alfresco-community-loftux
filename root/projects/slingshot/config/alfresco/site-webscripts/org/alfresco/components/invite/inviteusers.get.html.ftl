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

<div id="${args.htmlid}-body" class="invitelist">

   <div id="${args.htmlid}-invitelistBar" class="yui-g invitelist-bar">
      Search
      <input type="text" name="${args.htmlid}-inviteuserlist-search" value=""/>
      <input type="button" value="search" />
   </div>

   <div id="${args.htmlid}-users" class="users">
   List of users
   </div>

</div>