<div class="site-navigation">
   <span class="navigation-item"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/dashboard">${msg("link.siteDashboard")}</a></span>
<#list pages as p>
   <span class="navigation-separator">|</span>
   <span class="navigation-item"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/${p.pageId}">${p.title}</a></span>
</#list>
</div>