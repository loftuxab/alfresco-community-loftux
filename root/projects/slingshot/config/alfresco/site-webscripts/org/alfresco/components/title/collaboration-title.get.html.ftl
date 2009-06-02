<#assign activePage = page.url.templateArgs.pageid!"">
<#assign siteTitle><#if profile.title != "">${profile.title?html}<#else>${profile.shortName}</#if></#assign>
<script type="text/javascript">//<![CDATA[
   new Alfresco.CollaborationTitle("${args.htmlid}").setOptions(
   {
      site: "${page.url.templateArgs.site!""}",
      user: "${user.name!""}"
   }).setMessages(
      ${messages}
   );
   Alfresco.constants.DASHLET_RESIZE = ${userIsSiteManager?string};
//]]></script>
<div class="page-title theme-bg-color-1 theme-border-1">
   <div class="title">
      <h1 class="theme-color-3">${msg("header.site", "<span>${siteTitle}</span>")}</h1>
   </div>
   <div class="links title-button">
   <#if userIsSiteManager>
      <#assign linkClass><#if "invite" == activePage>class="active-page"</#if></#assign>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a href="${url.context}/page/site/${page.url.templateArgs.site!}/invite" ${linkClass}>${msg("link.invite")}</a>
         </span>
      </span>
   </#if>
   <#if userIsMember>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a id="${args.htmlid}-leave-link" href="#">${msg("link.leave")}</a>
         </span>
      </span>
   <#else>
      <span class="yui-button yui-link-button">
         <span class="first-child">
      <#if profile.visibility == "PUBLIC">
            <a id="${args.htmlid}-join-link" href="#">${msg("link.join")}</a>
      <#else>
            <a id="${args.htmlid}-requestJoin-link" href="#">${msg("link.request-join")}</a>
      </#if>
         </span>
      </span>
   </#if>
   <#if userIsSiteManager>
      <#assign linkClass><#if "edit-site" == activePage>class="active-page"</#if></#assign>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a href="#" onclick="Alfresco.module.getEditSiteInstance().show({shortName: '${profile.shortName}'}); return false;" ${linkClass}>${msg("link.editSite")}</a>
         </span>
      </span>
      <#assign linkClass><#if "customise-site" == activePage>class="active-page"</#if></#assign>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site" ${linkClass}>${msg("link.customiseSite")}</a>
         </span>
      </span>
   </#if>
   <#assign siteDashboardUrl = page.url.context + "/page/site/" + page.url.templateArgs.site + "/dashboard">
   <#if userIsSiteManager && (page.url.uri == siteDashboardUrl || "customise-site-dashboard" == activePage) >
      <#assign linkClass><#if "customise-site-dashboard" == activePage>class="active-page"</#if></#assign>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site-dashboard" ${linkClass}>${msg("link.customiseDashboard")}</a>
         </span>
      </span>
   </#if>
   </div>
</div>