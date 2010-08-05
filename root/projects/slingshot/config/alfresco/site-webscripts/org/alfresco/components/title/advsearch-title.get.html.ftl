<div class="page-title search-title theme-bg-color-1">
   <h1 class="theme-color-3"><span>${msg("header.advancedsearch")}</span></h1>
   <#if page.url.templateArgs.site??>
   <div>
      <span class="navigation-item backLink">
         <a href="${url.context}/page/site/${page.url.templateArgs.site}/dashboard">${msg("header.backlink", siteTitle?html)}</a>
      </span>
   </div>
   </#if>
   <#if page.url.args.back??>
   <div>
      <span class="navigation-item backLink">
         <a href="${url.context}/page/search?${page.url.args.back?html}">${msg("header.results")}</a>
      </span>
   </div>
   </#if>
   <div class="clear"></div>
</div>