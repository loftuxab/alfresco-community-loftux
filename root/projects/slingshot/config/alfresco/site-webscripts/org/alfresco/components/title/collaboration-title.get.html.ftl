<div class="page-title">
   <div class="title">
      <h1>
         <#if (profile.title != "")>${profile.title}<#else>${profile.shortName}</#if>
         <span class="light">${msg("header.site")}</span>
      </h1>
   </div>
   <div class="links">
      <span class="navigation-item">
         <a href="${url.context}/page/site/${page.url.templateArgs.site!""}/invite">${msg("link.invite")}</a>
      </span>
      <span class="navigation-separator">|</span>
      <span class="navigation-item">
         <a href="${url.context}/page/site/${page.url.templateArgs.site!""}/customise-site">${msg("link.customiseSite")}</a>
      </span>
   </div>
</div>