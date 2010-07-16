<div class="page-title search-title theme-bg-color-1">
   <h1 class="theme-color-3"><span>${msg("header.searchresults")}</span></h1>
   <div>
      <span class="navigation-item forwardLink">
         <a href="${url.context}/page<#if page.url.templateArgs.site??>/site/${page.url.templateArgs.site}</#if>/advsearch?back=true">${msg("header.advanced")}</a>
      </span>
   </div>
   <#if page.url.templateArgs.site??>
   <div>
      <span class="navigation-item backLink">
         <a href="${url.context}/page/site/${page.url.templateArgs.site}/dashboard">${msg("header.backlink", siteTitle?html)}</a>
      </span>
   </div>
   </#if>
   <div class="clear"></div>
</div>