<script type="text/javascript">//<![CDATA[
   new Alfresco.Header("${args.htmlid}");
//]]></script>
<div class="header">
   <div class="logo">
      <img src="${url.context}${args.logo}" alt="Company Logo"/>
   </div>
   <div class="personal-menu">
      <span class="menu-item"><a href="${url.context}/page/user/${user.name}/user-dashboard">${msg("link.myDashboard")}</a></span>
      <span class="menu-item"><a href="${url.context}/page/user-profile">${msg("link.myProfile")}</a></span>
      <span class="menu-item"><a href="#">${msg("link.sites")}</a></span>
      <span class="menu-item"><a href="#">${msg("link.users")}</a></span>
   </div>
   <div class="util-menu">
      <span class="menu-item"><a href="#">${msg("link.help")}</a></span>
      <span class="menu-item"><a href="#">${msg("link.search")}</a></span>
      <span class="menu-item"><input type="text" name="search" value="${msg("label.searchTip")}" /></span>
      <span class="menu-item"><a href="${url.context}/logout">${msg("link.logout")}</a></span>
   </div>
</div>
<div class="clear"></div>