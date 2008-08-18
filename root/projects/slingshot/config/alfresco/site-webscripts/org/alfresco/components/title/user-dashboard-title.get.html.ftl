<#assign activePage = page.url.templateArgs.pageid!"">
<div class="page-title">
   <div class="title">
      <h1>${user.properties["firstName"]} ${user.properties["lastName"]} <span class="light">${msg("header.dashboard")}</span></h1>
   </div>
   <div class="links">
      <#assign linkClass><#if "customise-dashboard" == activePage>class="active-page"</#if></#assign>
      <a href="${url.context}/page/customise-user-dashboard" ${linkClass}>${msg("link.customiseDashboard")}</a>
   </div>
</div>