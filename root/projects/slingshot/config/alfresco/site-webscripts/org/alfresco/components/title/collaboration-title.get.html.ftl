<#assign activePage = page.url.templateArgs.pageid!"">
<div class="page-title">
   <div class="title">
      <h1>
         <#if (profile.title != "")>${profile.title}<#else>${profile.shortName}</#if>
         <span class="light">${msg("header.site")}</span>
      </h1>
   </div>
   <div class="links">
      <span class="navigation-item">
         <#assign linkClass><#if "invite" == activePage>class="active-page"</#if></#assign>
         <a href="${url.context}/page/site/${page.url.templateArgs.site!}/invite" ${linkClass}>${msg("link.invite")}</a>
      </span>
      <span class="navigation-separator">|</span>
      <span class="navigation-item">
         <#assign linkClass><#if "customise-site" == activePage>class="active-page"</#if></#assign>
         <a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site" ${linkClass}>${msg("link.customiseSite")}</a>
      </span>
      <span class="navigation-separator">|</span>
      <span class="navigation-item">
         <#assign linkClass><#if "customise-dashboard" == activePage>class="active-page"</#if></#assign>
         <a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site-dashboard" ${linkClass}>${msg("link.customiseDashboard")}</a>
      </span>
   </div>
</div>