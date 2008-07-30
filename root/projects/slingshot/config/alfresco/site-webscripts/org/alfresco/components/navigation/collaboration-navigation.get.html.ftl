<div class="site-navigation">
   <span class="navigation-item"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/dashboard">${msg("link.siteDashboard")}</a></span>
<#list pages as p>
   <span class="navigation-separator">|</span>
   <span class="navigation-item"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/<#if p.pageUrl??>${p.pageUrl}<#else>${p.pageId}</#if>">${p.title}</a></span>
</#list>
<span class="navigation-separator">|</span>
<span class="navigation-item-alt"><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/invite">${msg("link.members")}</a></span>
</div>