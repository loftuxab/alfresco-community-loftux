<div class="page-title">
   <div class="title">
      <h1>
         <#if (profile.title != "")>${profile.title}<#else>${profile.shortName}</#if>
         <span class="light">${msg("header.site")}</span>
      </h1>
   </div>
   <div class="links">
      <a href="${url.context}/page/site/${page.url.templateArgs.site!""}/customise-site">${msg("link.customiseSite")}</a>
   </div>
</div>