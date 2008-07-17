<script type="text/javascript">//<![CDATA[
   new Alfresco.Header("${args.htmlid}").setOptions({
      siteId: "${page.url.templateArgs.site!""}"
   
   });
//]]></script>
<div class="header">
   <div class="logo">
      <img src="${url.context}${args.logo}" alt="Company Logo"/>
   </div>
   <div class="personal-menu">
      <span class="menu-item"><a href="${url.context}/page/user/${user.name}/dashboard">${msg("link.myDashboard")}</a></span>
      <span class="menu-item"><a href="${url.context}/page/user-profile">${msg("link.myProfile")}</a></span>
      <span class="menu-item"><a href="#">${msg("link.sites")}</a></span>
      <span class="menu-item"><a href="#">${msg("link.users")}</a></span>
   </div>
   <div class="util-menu">
      <span class="menu-item"><a href="#">${msg("link.help")}</a></span>
      <span class="menu-item">
         <#-- Check whether we got a site -->
         <select name="${args.htmlid}-searchtype" id="${args.htmlid}-searchtype" <#if ! page.url.templateArgs.site??>disabled="true"</#if>>
            <#if page.url.templateArgs.site??>
            <option value="site">Search this site</option>
            </#if>
            <option value="all">Search all sites</option>
         </select>
      </span>
      <span class="menu-item">
         <input type="text" name="${args.htmlid}-searchtext" id="${args.htmlid}-searchtext" value="" />
      </span>
      <span class="menu-item"><a href="${url.context}/logout">${msg("link.logout")}</a></span>
   </div>
</div>
<div class="clear"></div>