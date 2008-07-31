<#assign activeSite = page.url.templateArgs.site!"">
<#assign activePage = page.url.templateArgs.pageid!"">
<div class="site-navigation">
   <#assign linkClass><#if "dashboard" == activePage>class="active-page"</#if></#assign>
   <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/dashboard" ${linkClass}>${msg("link.siteDashboard")}</a></span>
<#list pages as p>
   <#assign linkPage><#if p.pageUrl??>${p.pageUrl}<#else>${p.pageId}</#if></#assign>
   <#assign linkClass><#if linkPage == activePage>class="active-page"</#if></#assign>
   <span class="navigation-separator">|</span>
   <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/${linkPage}" ${linkClass}>${p.title}</a></span>
</#list>
<span class="navigation-separator">|</span>
<span class="navigation-item-alt"><a href="${url.context}/page/site/${activeSite}/invite">${msg("link.members")}</a></span>
</div>