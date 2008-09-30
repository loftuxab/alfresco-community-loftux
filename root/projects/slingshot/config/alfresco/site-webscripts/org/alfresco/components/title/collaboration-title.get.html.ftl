<#assign activePage = page.url.templateArgs.pageid!"">
<div class="page-title">
   <div class="title">
      <h1>
         <span class="light">${msg("header.sitePrefix")}</span>
         <#if (profile.title != "")>${profile.title}<#else>${profile.shortName}</#if>
         <span class="light">${msg("header.siteSuffix")}</span>
      </h1>
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
   <#if userIsSiteManager>
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