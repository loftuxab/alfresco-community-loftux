<#assign activeSite = page.url.templateArgs.site!"">
<#assign activePage = page.url.templateArgs.pageid!"dashboard">
<#if activePage = "blog-postview" || activePage = "blog-postedit"><#assign activePage="blog-postlist"></#if>
<#if activePage = "links-linkedit" || activePage = "links-view"><#assign activePage="links"></#if>
<#if activePage = "discussions-topicview" || activePage = "discussions-createtopic"><#assign activePage="discussions-topiclist"></#if>
<#if activePage = "document-details" || activePage = "folder-details"><#assign activePage="documentlibrary"></#if>
<#if activePage = "wiki-create"><#assign activePage="wiki"></#if>
<div class="site-navigation">
<#if siteExists??>
   <#if url.context + "/page/site/" + activeSite + "/dashboard" == page.url.uri>
      <#assign linkClass>class="active-page theme-color-4"</#assign>
   <#else>
      <#assign linkClass>class="theme-color-4"</#assign>
   </#if>
   <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/dashboard" ${linkClass}>${msg("link.siteDashboard")}</a></span>
   <#list pages as p>
      <#assign linkPage><#if p.pageUrl??>${p.pageUrl}<#else>${p.pageId}</#if></#assign>
      <#if linkPage?index_of(activePage) != -1>
         <#assign linkClass>class="active-page theme-color-4"</#assign>      
      <#else>
         <#assign linkClass>class="theme-color-4"</#assign>
      </#if>
   <span class="navigation-separator">&nbsp;</span>
   <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/${linkPage}" ${linkClass}>
   <#if p.titleId??>${(msg(p.titleId))!p.title}<#else>${p.title}</#if>
   </a></span>
   </#list>
<span class="navigation-separator-alt">&nbsp;</span>
   <#if activePage = "site-members" || activePage = "pending-invites" || activePage = "invite" || activePage = "site-groups" || activePage = "add-groups">
      <#assign linkClass>class="active-page theme-color-4"</#assign>      
   <#else>
      <#assign linkClass>class="theme-color-4"</#assign>
   </#if>
<span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/site-members" ${linkClass}>${msg("link.members")}</a></span>
</#if>
</div>