<#if activeUserProfile>
   <#assign activePage = page.url.templateArgs.pageid?lower_case!"">
   <div id="${args.htmlid}-body" class="toolbar userprofile">
      <div class="link"><a href="profile" <#if activePage=="profile">class="activePage"</#if>>${msg("link.viewmyprofile")}</a></div>
      <#if !context.externalAuthentication>
      <div class="separator">&nbsp;</div>
      <div class="link"><a href="change-password" <#if activePage=="change-password">class="activePage"</#if>>${msg("link.changepassword")}</a></div>
      </#if>
      <!--<div class="separator">&nbsp;</div>
      <div class="link"><a href="#settings" <#if activePage=="settings">class="activePage"</#if>>${msg("link.settings")}</a></div>-->
   </div>
<#else>
   <#assign activePage = page.url.templateArgs.pageid?lower_case!"">
   <div id="${args.htmlid}-body" class="toolbar userprofile">
      <div class="link"><a href="profile" <#if activePage=="profile">class="activePage"</#if>>${msg("link.info")}</a></div>
      <div class="separator">&nbsp;</div>
      <div class="link"><a href="user-sites" <#if activePage=="user-sites">class="activePage"</#if>>${msg("link.sites")}</a></div>
      <div class="separator">&nbsp;</div>
      <div class="link"><a href="user-content" <#if activePage=="user-content">class="activePage"</#if>>${msg("link.content")}</a></div>
   </div>
</#if>