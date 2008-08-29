<script type="text/javascript">//<![CDATA[
   new Alfresco.SiteMembers("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      currentUser: "${user.id}",
      currentUserRole: "${currentUserRole}",
      roles:
      [
         <#list siteRoles as siteRole>"${siteRole}"<#if siteRole_has_next>,</#if></#list>
      ]
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="site-members">
   
   <div class="heading">${msg("site-members.heading")}</div>
   
   <div class="search-controls">
      <input id="${args.htmlid}-term" type="text" class="search-term" />
      <input id="${args.htmlid}-button" type="button" value="${msg("site-members.search-button")}" />
   </div>
   
   <#-- this div contains the site members results -->
   <div id="${args.htmlid}-members" class="members-list"></div>
   
</div>