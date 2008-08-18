<#assign activePage = page.url.templateArgs.pageid!"">
<div class="page-title">
   <div class="title">
      <h1>
         <#if (profile.title != "")>${profile.title}<#else>${profile.shortName}</#if>
         <span class="light">${msg("header.site")}</span>
      </h1>
   </div>
   <div class="links">
      <#if userIsSiteManager>
      <span class="navigation-item">
         <#assign linkClass><#if "invite" == activePage>class="active-page"</#if></#assign>
         <a href="${url.context}/page/site/${page.url.templateArgs.site!}/invite" ${linkClass}>${msg("link.invite")}</a>
      </span>
      </#if>
      <#if userIsSiteManager>
      <span class="navigation-separator">|</span>
      <span class="navigation-item">
         <#assign linkClass><#if "customise-site" == activePage>class="active-page"</#if></#assign>
         <a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site" ${linkClass}>${msg("link.customiseSite")}</a>
      </span>
      </#if>
      <#assign siteDashboardUrl = page.url.context + "/page/site/" + page.url.templateArgs.site + "/dashboard">
      <#if userIsSiteManager && (page.url.uri == siteDashboardUrl || "customise-site-dashboard" == activePage) >
      <span class="navigation-separator">|</span>
      <span class="navigation-item">
         <#assign linkClass><#if "customise-site-dashboard" == activePage>class="active-page"</#if></#assign>
         <a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site-dashboard" ${linkClass}>${msg("link.customiseDashboard")}</a>
      </span>
      </#if>
   </div>
</div>