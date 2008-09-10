<#assign activeSite = page.url.templateArgs.site!"">
<#assign activePage = page.url.templateArgs.pageid!"dashboard">
<#if activePage = "document-details"><#assign activePage="documentlibrary"></#if>
<div class="site-navigation">
   <#assign linkClass><#if url.context + "/page/site/" + activeSite + "/dashboard" == page.url.uri>class="active-page"</#if></#assign>
   <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/dashboard" ${linkClass}>${msg("link.siteDashboard")}</a></span>
<#list pages as p>
   <#assign linkPage><#if p.pageUrl??>${p.pageUrl}<#else>${p.pageId}</#if></#assign>
   <#assign linkClass><#if linkPage?index_of(activePage) != -1>class="active-page"</#if></#assign>
   <span class="navigation-separator">&nbsp;</span>
   <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/${linkPage}" ${linkClass}>${p.title}</a></span>
</#list>
<span class="navigation-separator-alt">&nbsp;</span>
<#assign linkClass><#if ("site-members" == activePage) || ("sent-invites" == activePage) || ("invite" == activePage)>class="active-page"</#if></#assign>
<span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/site-members" ${linkClass}>${msg("link.members")}</a></span>
</div>